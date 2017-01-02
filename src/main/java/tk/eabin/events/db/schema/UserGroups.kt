package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object UserGroups : IntIdTable("usergroup") {
    var name = varchar("name", 255)
    var description = varchar("description", 65535)
}