package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object LocationGroupMaps : IntIdTable("LOCATION_GROUP") {
    val location = reference("LOCATION_ID", EventLocations)
    val group = reference("USERGROUP_ID", UserGroups)
}