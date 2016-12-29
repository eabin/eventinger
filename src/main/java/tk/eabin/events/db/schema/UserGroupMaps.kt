package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object UserGroupMaps : IntIdTable("USER_GROUP") {
    val user = reference("USER_ID", Users)
    val group = reference("USERGROUP_ID", UserGroups)
}