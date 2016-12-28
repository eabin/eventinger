package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Participations : IntIdTable("PARTICIPATION") {
    val eventId = reference("EVENT_ID", Events)
    val userId = reference("USER_ID", Users)
    val doesParticipate = integer("DOESPARTICIPATE")
    val externalName = varchar("EXTERNALNAME", 255).nullable()
}