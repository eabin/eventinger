package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:16
 */
object EventCategories : IntIdTable("eventcategory") {
    val name = varchar("name", 255)
}