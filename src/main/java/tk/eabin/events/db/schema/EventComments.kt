package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventComments : IntIdTable("EVENTCOMMENT") {
    val eventId = reference("EVENT_ID", Events)
    val userId = reference("USER_ID", Users)
    val comment = varchar("COMMENT", 65535)
    val creationDate = long("CDATE").clientDefault { Date().time }
}