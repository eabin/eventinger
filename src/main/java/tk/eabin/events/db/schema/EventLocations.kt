package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:42
 */

object EventLocations : IntIdTable("eventlocation") {
    val name = varchar("name", 255)
    val categoryId = reference("category_id", EventCategories)
}