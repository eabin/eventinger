package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.EventCategories

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class EventCategory(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventCategory>(EventCategories)

    var name by EventCategories.name
}