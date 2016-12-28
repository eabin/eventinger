package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.Users
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var login by Users.login
    var password by Users.password


    fun md5(str: String): String {
        try {
            val md5 = MessageDigest.getInstance("MD5")
            val digest = md5.digest(str.toByteArray())
            val d2 = ByteArray(digest.size + 1)
            //avoid negative numbers!
            d2[0] = 0
            System.arraycopy(digest, 0, d2, 1, digest.size)
            val hInt = BigInteger(d2)
            return hInt.toString(16)
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
            return str
        }
    }

    fun cryptPassword(str: String): String {
        return md5(id.toString() + str)
    }
}