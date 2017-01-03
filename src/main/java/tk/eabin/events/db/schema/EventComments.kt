package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventComments : IntIdTable("eventcomment") {
    val eventId = reference("event_id", Events)
    val userId = reference("user_id", Users)
    val comment = varchar("comment", 65535)
    val creationDate = long("cdate").clientDefault { Date().time / 1000 }
}