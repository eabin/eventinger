package tk.eabin.events.db.dummy

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.Event
import tk.eabin.events.db.dao.EventCategory
import tk.eabin.events.db.dao.EventLocation
import tk.eabin.events.db.dao.User
import tk.eabin.events.db.schema.*
import java.time.LocalDateTime
import java.time.ZoneOffset

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 10:01
 */
fun createDummyData() {
    transaction {
        if (Users.exists()) {
            throw IllegalStateException("Expecting empty database")
        }
        SchemaUtils.create(Users, EventLocations, EventCategories, Events, Participations, EventUsers)

        val user = User.new {
            login = "test"
        }
        user.password = User.cryptPassword(user.id.value, "test")

        val category = EventCategory.new {
            name = "Soccer"
        }

        val location = EventLocation.new {
            name = "Playground"
            this.category = category
        }

        Event.new {
            this.category = category
            this.location = location
            comment = "Dummy Event"
            startDate = LocalDateTime.now().toEpochSecond(ZoneOffset.MIN).toInt()
            minPeople = 6
        }
    }
}