package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.EventLocations
import tk.eabin.events.db.schema.LocationGroupMaps

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class EventLocation(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<EventLocation>(EventLocations)

    var name by EventLocations.name
    var category by EventCategory referencedOn EventLocations.categoryId
    var groups by UserGroup via LocationGroupMaps
}