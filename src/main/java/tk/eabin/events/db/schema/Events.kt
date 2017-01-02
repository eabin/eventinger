package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable
import java.util.*

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Events : IntIdTable("event") {
    val startDate = long("startdate")
    val endDate = long("enddate").nullable()
    val creationDate = long("cdate").clientDefault { Date().time / 1000 }
    val categoryId = reference("category_id", EventCategories)
    val locationId = reference("location_id", EventLocations)
    val creatorId = reference("creator_id", Users)
    val comment = varchar("comment", 65535).default("")
    val maxPeople = integer("maxpeople").default(0)
    val minPeople = integer("minpeople")
    val deleted = bool("deleted").default(false)
    val archived = bool("archived").default(false)
}