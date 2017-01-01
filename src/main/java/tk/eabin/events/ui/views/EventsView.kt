package tk.eabin.events.ui.views

import com.google.common.eventbus.Subscribe
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.Event
import tk.eabin.events.db.dao.Participation
import tk.eabin.events.db.dao.ParticipationType
import tk.eabin.events.db.schema.EventGroupMaps
import tk.eabin.events.db.schema.Events
import tk.eabin.events.db.schema.Participations
import tk.eabin.events.event.AppEventBus
import tk.eabin.events.event.EventChangedEvent
import tk.eabin.events.event.EventCreatedEvent
import tk.eabin.events.event.ParticipationChangedEvent
import tk.eabin.events.ui.MainUI.Companion.currentUser
import java.util.*


private val categoryIcons = hashMapOf("soccer" to FontAwesome.SOCCER_BALL_O)


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 00:38
 */

class EventsView() : VerticalLayout(), View {
    override fun attach() {
        AppEventBus.registerWithEventBus(this)
    }

    override fun detach() {
        AppEventBus.unregisterFromEventBus(this)
    }

    override fun enter(p0: ViewChangeListener.ViewChangeEvent?) {
        println("Entering events view...")
        generateAll()
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
                        Event.new {
                            eventWindow.updateEvent(this)
                        }
                    }
                } else {
                    transaction {
                        eventWindow.updateEvent(eventWindow.event)
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

    private fun generateEvents(): Component {
        val eventsBox = CssLayout().apply {
            defaultComponentAlignment = Alignment.MIDDLE_CENTER
            addStyleName("dashboard-panels")
            setMargin(false)
            setWidth("100%")
            isSpacing = true


            val userGroupIds = currentUser.groups.map { it.id }

            // todo: this is an ugly version of filtering and sorting; better would be to do a select where exists to find
            // out about group relations - or find a mechanism that exposed already provides
            val events = (Events innerJoin EventGroupMaps).select {
                Events.deleted.eq(false).and(Events.archived.eq(false)).and(EventGroupMaps.group.inList(userGroupIds))
            }.orderBy(Events.startDate, isAsc = true)

            for (event in Event.wrapRows(events).toSortedSet(Comparator { a, b -> a.startDate.compareTo(b.startDate) })) {
                val box = generateEventBox(event)
                addComponent(box)
            }
            defaultComponentAlignment = Alignment.MIDDLE_CENTER
        }
        return eventsBox
    }

    private fun generateAll() {
        ui.access {
            transaction {
                addStyleName("dashboard-view")
                removeAllComponents()
                val header = generateHeader()
                addComponent(header)
                val events = generateEvents()
                addComponent(events)
                setExpandRatio(events, 1f)
            }
        }
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

    private fun createContentWrapper(content: Component): Component {
        val slot = CssLayout()
        slot.setWidth("100%")
        slot.addStyleName("dashboard-panel-slot")

        val card = CssLayout()
        card.setWidth("100%")
        card.addStyleName(ValoTheme.LAYOUT_CARD)

        val toolbar = HorizontalLayout()
        toolbar.addStyleName("dashboard-panel-toolbar")
        toolbar.setWidth("100%")

        val caption = Label(content.caption)
        caption.addStyleName(ValoTheme.LABEL_H4)
        caption.addStyleName(ValoTheme.LABEL_COLORED)
        caption.addStyleName(ValoTheme.LABEL_NO_MARGIN)
        content.caption = null

        val tools = MenuBar()
        tools.addStyleName(ValoTheme.MENUBAR_BORDERLESS)
        val max = tools.addItem("", FontAwesome.EXPAND, MenuBar.Command {

            fun menuSelected(selectedItem: MenuBar.MenuItem) {
                if (!slot.styleName.contains("max")) {
                    selectedItem.setIcon(FontAwesome.COMPRESS)
//                    toggleMaximized(slot, true)
                } else {
                    slot.removeStyleName("max")
                    selectedItem.setIcon(FontAwesome.EXPAND)
//                    toggleMaximized(slot, false)
                }
            }
        })
        max.styleName = "icon-only"
        val root = tools.addItem("", FontAwesome.COG, null)
        root.addItem("Configure", MenuBar.Command {
            fun menuSelected(selectedItem: MenuBar.MenuItem) {
                Notification.show("Not implemented in this demo")
            }
        })
        root.addSeparator()
        root.addItem("Close", {
            fun menuSelected(selectedItem: MenuBar.MenuItem) {
                Notification.show("Not implemented in this demo")
            }
        })

        toolbar.addComponents(caption, tools)
        toolbar.setExpandRatio(caption, 1f)
        toolbar.setComponentAlignment(caption, Alignment.MIDDLE_LEFT)

        card.addComponents(toolbar, content)
        slot.addComponent(card)
        return slot
    }

    private fun generateEventBox(event: Event): Component {
        val content = VerticalLayout()
        content.caption = event.category.name
        content.setMargin(true)
        content.isSpacing = true
//        content.setSizeFull()

        val comment = Label(event.comment)
        comment.caption = "Comment"
        comment.icon = FontAwesome.COMMENT
        content.addComponent(comment)

        val participations = HorizontalLayout()
        participations.setSizeFull()
        participations.addStyleName("event-participation")
        Responsive.makeResponsive(participations)

        for (p in ParticipationType.values()) {
            val participants = event.participations.filter { it.doesParticipate == p.value }.joinToString(", ") { it.user.login }
            val l = Label("[$participants]")
            l.icon = p.icon
            l.caption = p.name
            l.setSizeUndefined()

            participations.addComponent(l)
        }

        val buttons = HorizontalLayout().apply {

            for (p in ParticipationType.values()) {
                addComponent(Button(p.icon).apply {
                    addClickListener {
                        updateParticipation(event, p)
                    }
                })
            }
        }
        content.addComponent(participations)
        content.addComponent(buttons)
        return createContentWrapper(content)
    }


    @Subscribe
    fun onParticipationChanged(e: ParticipationChangedEvent) = generateAll()

    @Subscribe
    fun onEventChanged(e: EventChangedEvent) = generateAll()

    @Subscribe
    fun onEventCreated(e: EventCreatedEvent) = generateAll()
}