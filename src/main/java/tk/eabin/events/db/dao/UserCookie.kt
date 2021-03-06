package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.UserCookies
import java.security.SecureRandom
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class UserCookie(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserCookie>(UserCookies) {
        fun generateSessionKey(): String {
            val bytes = ByteArray(64)
            SecureRandom().nextBytes(bytes)
            val sessionKey = Base64.getEncoder().encodeToString(bytes)
            return sessionKey
        }
    }

    var user by User referencedOn UserCookies.userId
    var cookie by UserCookies.cookie
    var ip by UserCookies.ip


}