package tk.eabin.events.ui.views

import com.google.common.eventbus.Subscribe
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.Responsive
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.Event
import tk.eabin.events.db.dao.Participation
import tk.eabin.events.db.schema.EventGroupMaps
import tk.eabin.events.db.schema.Events
import tk.eabin.events.db.schema.Participations
import tk.eabin.events.event.AppEventBus
import tk.eabin.events.event.EventChangedEvent
import tk.eabin.events.event.EventCreatedEvent
import tk.eabin.events.event.ParticipationChangedEvent
import tk.eabin.events.ui.MainUI.Companion.currentUser
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


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

    private fun generateAll() {
        ui.access {
            transaction {
                removeAllComponents()
                val header = generateHeader()
                addComponent(header)
                defaultComponentAlignment = Alignment.MIDDLE_CENTER
                margin = MarginInfo(true)

                println("Updating events list for user: ${currentUser.login}")
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
                setSizeUndefined()
                setWidth("100%")
                defaultComponentAlignment = Alignment.MIDDLE_CENTER
            }
        }
    }

    private fun updateParticipation(modifiedEvent: Event, i: Int) {
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
                    doesParticipate = i
                }
            } else {
                participation.first().doesParticipate = i
            }
        }
        AppEventBus.postEvent(ParticipationChangedEvent(modifiedEvent.id.value))
    }

    private fun generateEventBox(event: Event): Component {
        val ret = Panel(event.category.name + "  -  " + LocalDateTime.ofEpochSecond(event.startDate.toLong(), 0, ZoneOffset.MIN))
        val content = VerticalLayout()
        content.addComponent(Label(event.comment))
        content.addComponent(Label(event.participations.joinToString(", ") { it.user.login + "[${it.doesParticipate}]" }))
        val buttons = HorizontalLayout().apply {
            addComponent(Button("Teilnehmen").apply {
                addClickListener {
                    updateParticipation(event, 1)
                }
            })
            addComponent(Button("Nicht Teilnehmen").apply {
                addClickListener {
                    updateParticipation(event, 0)
                }
            })
        }
        content.addComponent(buttons)

        ret.content = content
//        ret.setWidthUndefined()
        ret.setWidth("90%")

        return ret
    }


    @Subscribe
    fun onParticipationChanged(e: ParticipationChangedEvent) = generateAll()

    @Subscribe
    fun onEventChanged(e: EventChangedEvent) = generateAll()

    @Subscribe
    fun onEventCreated(e: EventCreatedEvent) = generateAll()
}