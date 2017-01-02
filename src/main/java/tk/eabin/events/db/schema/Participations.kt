package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Participations : IntIdTable("participation") {
    val eventId = reference("event_id", Events)
    val userId = reference("user_id", Users).nullable()
    val doesParticipate = integer("doesparticipate")
    val externalName = varchar("externalname", 255).nullable()
}