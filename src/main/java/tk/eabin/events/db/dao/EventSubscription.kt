package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.EventSubscriptions

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class EventSubscription(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventSubscription>(EventSubscriptions)

    var user by User referencedOn EventSubscriptions.userId
    var category by EventCategory referencedOn EventSubscriptions.categoryId
    var byJabber by EventSubscriptions.byJabber
}