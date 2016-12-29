package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.EventGroupMaps
import tk.eabin.events.db.schema.EventUsers
import tk.eabin.events.db.schema.Events
import tk.eabin.events.db.schema.Participations

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class Event(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<Event>(Events)

    var startDate by Events.startDate
    var minPeople by Events.minPeople
    var maxPeople by Events.maxPeople
    var comment by Events.comment
    var category by EventCategory referencedOn Events.categoryId
    var location by EventLocation referencedOn Events.locationId
    val eventUsers by EventUser referrersOn EventUsers.event
    val participations by Participation referrersOn Participations.eventId
    val groups by UserGroup via EventGroupMaps
}