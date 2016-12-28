package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:42
 */

object EventLocations : IntIdTable("EVENTLOCATION") {
    val name = varchar("NAME", 255)
    val categoryId = reference("CATEGORY_ID", EventCategories)
}