package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Users : IntIdTable("SYSTEMUSER") {
    val login = varchar("LOGIN", 255)
    val password = varchar("PASSWORD", 255)

}