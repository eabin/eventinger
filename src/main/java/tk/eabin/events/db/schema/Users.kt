package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Users : IntIdTable("systemuser") {
    val login = varchar("login", 255)
    val password = varchar("password", 255).nullable()
    val pushId = varchar("jabber_id", 255).nullable()
    val lastSeen = long("last_seen").default(0)
}