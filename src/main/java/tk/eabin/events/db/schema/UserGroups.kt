package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object UserGroups : IntIdTable("USERGROUP") {
    var name = varchar("NAME", 255)
    var description = varchar("DESCRIPTION", 65535)
}