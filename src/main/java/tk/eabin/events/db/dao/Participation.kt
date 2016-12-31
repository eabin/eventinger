package tk.eabin.events.db.dao

import com.vaadin.server.FontAwesome
import com.vaadin.server.Resource
import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.Participations

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
enum class ParticipationType(val value: Int, val icon: Resource) {
    YES(1, FontAwesome.ARROW_CIRCLE_O_UP),
    NO(0, FontAwesome.ARROW_CIRCLE_DOWN),
    MAYBE(2, FontAwesome.ARROW_CIRCLE_O_RIGHT);

    companion object {
        fun byValue(value: Int) = ParticipationType.values().first { it.value == value }
    }
}

class Participation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Participation>(Participations) {

    }

    var event by Event referencedOn Participations.eventId
    var user by User referencedOn Participations.userId
    var doesParticipate by Participations.doesParticipate
    var externalName by Participations.externalName
}