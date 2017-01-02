package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventUsers : IntIdTable("event_user") {
    val user = reference("user_id", Users)
    val event = reference("event_id", Events)
    val subscribed = integer("is_subscribed")
    val seen = bool("seen")
}