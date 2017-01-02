package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object UserGroupMaps : IntIdTable("user_group") {
    val user = reference("user_id", Users)
    val group = reference("usergroup_id", UserGroups)
}