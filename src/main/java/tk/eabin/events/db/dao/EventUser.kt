package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.EventUsers

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class EventUser(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventUser>(EventUsers)

    val event by Event referencedOn EventUsers.event
    val user by User referencedOn EventUsers.user
    var subscribed by EventUsers.subscribed
    var seen by EventUsers.seen
}