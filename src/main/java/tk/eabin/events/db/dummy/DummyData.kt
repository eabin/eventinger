package tk.eabin.events.db.dummy

import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.*
import tk.eabin.events.db.schema.*
import java.util.*

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
        SchemaUtils.create(Users, EventLocations, EventCategories, Events, Participations, EventUsers, UserGroups, UserGroupMaps,
                LocationGroupMaps, EventComments, EventGroupMaps)

        val user1 = User.new {
            login = "test"
        }
        user1.password = User.cryptPassword(user1.id.value, "test")

        val user2 = User.new {
            login = "test2"
        }
        user2.password = User.cryptPassword(user2.id.value, "test")


        val group1 = UserGroup.new {
            name = "Group1"
            description = "Group interested in same stuff 1"
        }

        val group2 = UserGroup.new {
            name = "Group2"
            description = "Group interested in same stuff 2"
        }

        val group3 = UserGroup.new {
            name = "Group3"
            description = "Group interested in same stuff 3"
        }


        UserGroupMap.new {
            user = user1
            group = group1
        }
        UserGroupMap.new {
            user = user1
            group = group2
        }
        UserGroupMap.new {
            user = user1
            group = group3
        }

        UserGroupMap.new {
            user = user2
            group = group3
        }

        val categorySoccer = EventCategory.new {
            name = "Soccer"
        }

        val locationPlayground = EventLocation.new {
            name = "Playground"
            this.category = categorySoccer
        }.apply {
            for (g in arrayOf(group1, group2, group3)) {
                LocationGroupMap.new {
                    group = g
                    location = this@apply
                }
            }
        }

        val locationTv = EventLocation.new {
            name = "Passive@TV"
            this.category = categorySoccer
        }.apply {
            for (g in arrayOf(group1)) {
                LocationGroupMap.new {
                    group = g
                    location = this@apply
                }
            }
        }


        val categoryMovies = EventCategory.new {
            name = "Movies"
        }

        val locationMovieplex = EventLocation.new {
            name = "Movie Plex"
            this.category = categoryMovies
        }.apply {
            for (g in arrayOf(group1)) {
                LocationGroupMap.new {
                    group = g
                    location = this@apply
                }
            }
        }


        val locationHome = EventLocation.new {
            name = "Home Theater"
            this.category = categoryMovies
        }.apply {
            for (g in arrayOf(group2, group3)) {
                LocationGroupMap.new {
                    group = g
                    location = this@apply
                }
            }
        }

        val event1 = Event.new {
            this.category = categorySoccer
            this.location = locationPlayground
            comment = "Dummy Event"
            startDate = Date().time
            minPeople = 6
        }.apply {
            for (g in arrayOf(group1, group2, group3)) {
                EventGroupMap.new {
                    group = g
                    event = this@apply
                }
            }
        }

        val event2 = Event.new {
            this.category = categoryMovies
            this.location = locationMovieplex
            comment = "Not in group3"
            startDate = Date().time
            minPeople = 6
        }.apply {
            for (g in arrayOf(group1, group2)) {
                EventGroupMap.new {
                    group = g
                    event = this@apply
                }
            }
        }

    }
}