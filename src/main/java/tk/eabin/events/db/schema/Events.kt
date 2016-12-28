package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Events : IntIdTable("EVENT") {
    val startDate = integer("STARTDATE")
    val endDate = integer("ENDDATE").nullable()
    val creationDate = integer("CDATE").clientDefault { LocalDateTime.now().toEpochSecond(ZoneOffset.MIN).toInt() }
    val categoryId = reference("CATEGORY_ID", EventCategories)
    val locationId = reference("LOCATION_ID", EventLocations)
    val comment = varchar("COMMENT", 65535).default("")
    val maxPeople = integer("MAXPEOPLE").nullable()
    val minPeople = integer("MINPEOPLE")
    val deleted = bool("DELETED").default(false)
    val archived = bool("ARCHIVED").default(false)

}