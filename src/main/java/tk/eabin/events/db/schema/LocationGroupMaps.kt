package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object LocationGroupMaps : IntIdTable("location_group") {
    val location = reference("location_id", EventLocations)
    val group = reference("usergroup_id", UserGroups)
}