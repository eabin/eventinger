package tk.eabin.events.ui.views

import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.server.VaadinSession
import com.vaadin.shared.ui.MarginInfo
import com.vaadin.ui.*
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.Event
import tk.eabin.events.db.dao.Participation
import tk.eabin.events.db.dao.User
import tk.eabin.events.db.schema.Events
import tk.eabin.events.db.schema.Participations
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 00:38
 */

class EventsView() : VerticalLayout(), View {
    private val currentUser: User
        get() {
            return VaadinSession.getCurrent().getAttribute(User::class.java)
        }


    override fun enter(p0: ViewChangeListener.ViewChangeEvent?) {
        println("Entering events view...")
        generateAll()
    }

    private fun generateAll() {
        transaction {
            removeAllComponents()
            defaultComponentAlignment = Alignment.MIDDLE_CENTER
            margin = MarginInfo(true)
            for (event in Event.find { Events.deleted.eq(false).and(Events.archived.eq(false)) }.sortedBy { it.startDate }) {
                val box = generateEventBox(event)
                addComponent(box)
            }
            setSizeUndefined()
            setWidth("100%")
            defaultComponentAlignment = Alignment.MIDDLE_CENTER
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
        generateAll()
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
        ret.setWidth("80%")
        return ret
    }

}