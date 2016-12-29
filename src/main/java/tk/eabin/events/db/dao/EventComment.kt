package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.EventComments

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class EventComment(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventComment>(EventComments)

    var event by Event referencedOn EventComments.eventId
    var user by User referencedOn EventComments.userId
    var creationDate by EventComments.creationDate
    var comment by EventComments.comment
}