package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object UserCookies : IntIdTable("usercookie") {
    val userId = reference("user_id", Users)
    val cookie = varchar("cookie", 128)
    val ip = varchar("ip", 32).nullable()

}