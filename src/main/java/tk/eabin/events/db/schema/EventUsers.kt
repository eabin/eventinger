package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventUsers : IntIdTable("EVENT_USER") {
    val user = reference("USER_ID", Users)
    val event = reference("EVENT_ID", Events)
    val subscribed = integer("IS_SUBSCRIBED")
    val seen = bool("SEEN")
}