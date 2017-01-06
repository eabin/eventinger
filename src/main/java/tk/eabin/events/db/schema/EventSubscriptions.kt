package tk.eabin.events.db.schema

import org.jetbrains.exposed.dao.IntIdTable

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:43
 */
object EventSubscriptions : IntIdTable("eventsubscription") {
    val userId = reference("user_id", Users)
    val categoryId = reference("category_id", EventCategories)
    val byJabber = bool("byjabber").default(true)
}