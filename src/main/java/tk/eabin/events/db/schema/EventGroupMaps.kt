package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventGroupMaps : IntIdTable("EVENT_GROUP") {
    val event = reference("EVENT_ID", Events)
    val group = reference("USERGROUP_ID", UserGroups)
}