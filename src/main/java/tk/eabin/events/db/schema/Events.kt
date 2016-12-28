package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object Events : IntIdTable("EVENT") {
    val startDate = integer("STARTDATE")
    val endDate = integer("ENDDATE")
    val creationDate = datetime("CDATE")
    val categoryId = reference("CATEGORY_ID", EventCategories)
    val locationId = reference("LOCATION_ID", EventLocations)
    val comment = varchar("COMMENT", 65535)
    val maxPeople = integer("MAXPEOPLE")
    val minPeople = integer("MINPEOPLE")
    val deleted = bool("DELETED")
    val archived = bool("ARCHIVED")

}