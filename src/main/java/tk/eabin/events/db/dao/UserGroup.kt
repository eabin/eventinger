package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.UserGroups

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class UserGroup(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserGroup>(UserGroups)

    var name by UserGroups.name
    var description by UserGroups.description
}