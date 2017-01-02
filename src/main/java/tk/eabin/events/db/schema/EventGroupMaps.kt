package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventGroupMaps : IntIdTable("event_group") {
    val event = reference("event_id", Events)
    val group = reference("usergroup_id", UserGroups)
}