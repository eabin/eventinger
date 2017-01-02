package tk.eabin.events.ui.views

import com.google.common.eventbus.Subscribe
import com.vaadin.data.Property
import com.vaadin.data.util.ObjectProperty
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.Event
import tk.eabin.events.db.dao.EventComment
import tk.eabin.events.db.dao.Participation
import tk.eabin.events.db.dao.ParticipationType
import tk.eabin.events.db.schema.EventComments
import tk.eabin.events.db.schema.EventGroupMaps
import tk.eabin.events.db.schema.Events
import tk.eabin.events.db.schema.Participations
import tk.eabin.events.event.*
import tk.eabin.events.ui.MainUI.Companion.currentUser
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


private val categoryIcons = hashMapOf("soccer" to FontAwesome.SOCCER_BALL_O)


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 00:38
 */

class EventsView() : VerticalLayout(), View {
    data class EventChanged(val onChanged: (event: Event) -> Unit)

    data class ToolbarListener(
            val onEdit: (() -> Unit)? = null,
            val onDelete: (() -> Unit)? = null
    )

    val eventList by lazy {
        generateEvents()
    }

    init {
        println("Init EventsView")
    }

    override fun attach() {
        AppEventBus.registerWithEventBus(this)
        println("Attaching UI...")
        addStyleName("dashboard-view")
        val header = generateHeader()
        addComponent(header)
        addComponent(eventList)
        setExpandRatio(eventList, 1f)
    }

    override fun detach() {
        AppEventBus.unregisterFromEventBus(this)
    }

    override fun enter(p0: ViewChangeListener.ViewChangeEvent?) {
        println("Entering events view...")
        updateEvents()
    }

    private fun generateHeader(): Component {
        val header = HorizontalLayout()
        header.addStyleName("viewheader")
        header.isSpacing = true
        Responsive.makeResponsive(header)

        val titleLabel = Label("Events")
        titleLabel.setSizeUndefined()
        titleLabel.addStyleName(ValoTheme.LABEL_H1)
        titleLabel.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        header.addComponents(titleLabel, generateToolbar())

        return header
    }

    private fun saveEvent(eventWindow: EditEventWindow) {
        val event =
                if (eventWindow.event == null) {
                    transaction {
                        val newEvent = Event.new {
                            eventWindow.updateEvent(this)
                        }
                        eventWindow.updateEventGroups(newEvent)
                        newEvent
                    }
                } else {
                    transaction {
                        logger.addLogger(StdOutSqlLogger())
                        eventWindow.updateEvent(eventWindow.event)
                        eventWindow.updateEventGroups(eventWindow.event)
                        eventWindow.event.flush()
                    }
                    eventWindow.event
                }
        AppEventBus.postEvent(EventChangedEvent(event.id.value))
    }

    private fun generateToolbar(): Component {
        val toolbar = HorizontalLayout().apply {
            addStyleName("toolbar")
            isSpacing = true

            val btnCreate = Button("New Event").apply {
                addStyleName(ValoTheme.BUTTON_PRIMARY)
                addClickListener {
                    ui.addWindow(EditEventWindow(null, "New Event", { saveEvent(it) }))
                }
            }

            val group = CssLayout(btnCreate)
            group.addStyleName(ValoTheme.LAYOUT_COMPONENT_GROUP)
            addComponent(group)
        }

        return toolbar
    }

    private val eventListeners = hashMapOf<Int, MutableSet<EventChanged>>()
    private fun addListener(event: Event, listener: (event: Event) -> Unit) {
        val set = eventListeners.getOrPut(event.id.value, { mutableSetOf() })
        set += EventChanged(listener)
    }

    private fun updateEvents(vararg updatedEventIds: Int) {

        ui?.access {
            transaction {
                val userGroupIds = currentUser.groups.map { it.id }
                // todo: this is an ugly version of filtering and sorting and inserting; make this perform!
                // out about group relations - or find a mechanism that exposed already provides
                val rawEvents = (Events innerJoin EventGroupMaps).select {
                    Events.deleted.eq(false).and(Events.archived.eq(false)).and(EventGroupMaps.group.inList(userGroupIds))
                }.orderBy(Events.startDate, isAsc = true)
                val events = Event.wrapRows(rawEvents)
                val eventMap = events.associateBy { it.id.value }
                for (event in events.toSortedSet(Comparator { a, b -> a.startDate.compareTo(b.startDate) })) {
                    val eventId = "${event.id.value}"
                    val listeners = eventListeners[event.id.value]
                    if (listeners == null || listeners.isEmpty()) {
                        val box = buildEventBox(event)
                        box.id = eventId
                        val nextIndex = eventList.indexOfFirst {
                            val e = eventMap[it.id.toInt()] ?: return@indexOfFirst false
                            e.startDate > event.startDate
                        }
                        if (nextIndex < 0) {
                            eventList.addComponent(box)
                        } else {
                            eventList.addComponent(box, nextIndex)
                        }
                        // retry-update, because the eventbox is an empty shell
                        eventListeners[event.id.value]?.forEach { it.onChanged(event) }
                    } else {
                        listeners.forEach { it.onChanged(event) }
                    }
                }
            }
        }
    }

    private fun removeEvents(vararg removedEventIds: Int) {

        ui?.access {
            transaction {
                for (event in removedEventIds) {
                    val eventId = "${event}"
                    val component = eventList.first { it.id == eventId }
                    if (component != null) eventList.removeComponent(component)
                }
            }
        }
    }

    private fun generateEvents(): CssLayout {
        println("Generating events layout...")
        val eventsBox = CssLayout().apply {
            defaultComponentAlignment = Alignment.MIDDLE_CENTER
            addStyleName("dashboard-panels")
            setMargin(false)
            setWidth("100%")
            isSpacing = true


            defaultComponentAlignment = Alignment.MIDDLE_CENTER
        }
        return eventsBox
    }

    private fun updateParticipation(modifiedEvent: Event, i: ParticipationType) {
        transaction {
            println("Setting participation for event ${modifiedEvent.comment} to $i")
            val participation = Participation.find {
                Participations.userId.eq(currentUser.id).and(
                        Participations.eventId.eq(modifiedEvent.id)
                )
            }
            if (participation.empty()) {
                Participation.new {
                    event = modifiedEvent
                    user = currentUser
                    doesParticipate = i.value
                }
            } else {
                participation.first().doesParticipate = i.value
            }
        }
        AppEventBus.postEvent(ParticipationChangedEvent(modifiedEvent.id.value))
    }

    private fun createContentWrapper(content: Component, captionProperty: Property<String>,
                                     listener: ToolbarListener): Component {
        val slot = CssLayout()
        slot.setWidth("100%")
        slot.addStyleName("dashboard-panel-slot")

        val card = CssLayout()
        card.setWidth("100%")
        card.addStyleName(ValoTheme.LAYOUT_CARD)

        val toolbar = HorizontalLayout()
        toolbar.addStyleName("dashboard-panel-toolbar")
        toolbar.setWidth("100%")

        val caption = Label(captionProperty)
        caption.addStyleName(ValoTheme.LABEL_H4)
        caption.addStyleName(ValoTheme.LABEL_COLORED)
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN)

        val tools = MenuBar()
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS)

        val edit = tools.addItem("", FontAwesome.EDIT, MenuBar.Command {
            listener.onEdit?.invoke()
        })
        edit.styleName = "icon-only"

        val delete = tools.addItem("", FontAwesome.TRASH, MenuBar.Command {
            listener.onDelete?.invoke()
        })
        delete.styleName = "icon-only"

        val root = tools.addItem("", FontAwesome.COG, null)
        root.addItem("External Participants", {
            Notification.show("Not implemented in this demo")
        })

        root.addSeparator()
        root.addItem("Close", {
            Notification.show("Not implemented in this demo")
        })


        toolbar.addComponents(caption, tools)
        toolbar.setExpandRatio(caption, 1f)
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT)

        card.addComponents(toolbar, content)
        slot.addComponent(card)
        return slot
    }

    private fun buildEventBox(event: Event): Component {
        val content = VerticalLayout()
        content.setMargin(true)
        content.isSpacing = true

        val captionProperty = ObjectProperty<String>("")
        addListener(event) { captionProperty.value = event.category.name + " @" + event.location.name + " - " + SimpleDateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.SHORT).format(Date(event.startDate * 1000)) }

        val info = Label("")
        info.caption = "Info"
        info.icon = FontAwesome.INFO
        addListener(event) { info.value = it.comment }
        content.addComponent(info)

        val participations = buildParticipationBox(event)
        content.addComponent(participations)

        val chat = buildChatBox(event)
        content.addComponent(chat)

        val buttons = buildButtonBox(event)
        content.addComponent(buttons)

        return createContentWrapper(content, captionProperty,
                ToolbarListener(
                        onEdit = { ui.addWindow(EditEventWindow(event, "Edit Event", { saveEvent(it) })) },
                        onDelete = { showConfirmDialog(ui, "Delete Event", "You are about to delete the following event: (TODO)", "Delete", { deleteEvent(event.id.value) }) }
                )
        )
    }

    private fun buildChatBox(event: Event): Component {
        val chatBox = VerticalLayout().apply {
            setMargin(true)

            val mostRecentComment = Label()
            mostRecentComment.addStyleName(ValoTheme.LABEL_SMALL)
            addComponent(mostRecentComment)

            addListener(event) {
                transaction {
                    val commentsRaw = EventComments.select { EventComments.eventId.eq(event.id) }.orderBy(EventComments.creationDate, isAsc = false).limit(1)
                    if (!commentsRaw.empty()) {
                        val comment = EventComment.wrapRow(commentsRaw.first(), this)
                        mostRecentComment.caption = comment.user.login
                        mostRecentComment.value = comment.comment
                    }
                }
            }
        }

        val panel = Panel("Chat", chatBox)
        panel.icon = FontAwesome.COMMENTS
        panel.addStyleName(ValoTheme.PANEL_BORDERLESS)
        panel.addStyleName("event-chat-small")
        return panel
    }

    private fun buildButtonBox(event: Event): Component {
        val buttons = CssLayout().apply {
            addStyleName("event-buttons")

            for (p in ParticipationType.values()) {
                addComponent(Button(p.name, p.icon).apply {
                    addListener(event) {
                        if (it.participations.any {
                            val u = it.user ?: return@any false
                            u.id == currentUser.id && it.doesParticipate == p.value
                        }) {
                            addStyleName(ValoTheme.BUTTON_FRIENDLY)
                        } else {
                            removeStyleName(ValoTheme.BUTTON_FRIENDLY)
                        }
                    }
                    addClickListener {
                        updateParticipation(event, p)
                    }
                })
            }
        }
        Responsive.makeResponsive(buttons)
        return buttons
    }

    private fun buildParticipationBox(event: Event): HorizontalLayout {
        val participations = HorizontalLayout()
        participations.setSizeFull()
        participations.addStyleName("event-participation")
        Responsive.makeResponsive(participations)

        for (p in ParticipationType.values()) {
            val l = Label()
            l.icon = p.icon
            l.caption = p.name
            l.setSizeUndefined()

            addListener(event) {
                val participants = it.participations.filter { it.doesParticipate == p.value }.joinToString(", ") { it.user?.login ?: it.externalName ?: "???" }
                val text = "[$participants]"
                l.value = text
            }

            participations.addComponent(l)
        }
        return participations
    }

    private fun deleteEvent(id: Int) {
        println("Deleting event: $id")
        transaction {
            Event[id].let {
                it.deleted = true
                it.flush()
                AppEventBus.postEvent(EventDeletedEvent(id))
            }
        }
    }


    @Subscribe
    fun onParticipationChanged(e: ParticipationChangedEvent) = updateEvents(e.eventId)

    @Subscribe
    fun onEventChanged(e: EventChangedEvent) = updateEvents(e.eventId)

    @Subscribe
    fun onEventCreated(e: EventCreatedEvent) = updateEvents()

    @Subscribe
    fun onEventDeleted(e: EventDeletedEvent) = removeEvents(e.eventId)
}