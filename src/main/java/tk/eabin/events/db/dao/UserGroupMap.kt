package tk.eabin.events.db.dao

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import tk.eabin.events.db.schema.UserGroupMaps

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 25.12.16
 * Time: 20:01
 */
class UserGroupMap(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserGroupMap>(UserGroupMaps)

    var user by User referencedOn UserGroupMaps.user
    var group by UserGroup referencedOn UserGroupMaps.group
}