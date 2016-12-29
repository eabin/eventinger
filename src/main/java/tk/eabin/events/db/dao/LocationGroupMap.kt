package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.LocationGroupMaps

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class LocationGroupMap(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<LocationGroupMap>(LocationGroupMaps)

    var location by EventLocation referencedOn LocationGroupMaps.location
    var group by UserGroup referencedOn LocationGroupMaps.group
}