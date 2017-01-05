package tk.eabin.events.event.notification

import com.google.common.eventbus.Subscribe
import org.apache.http.NameValuePair
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.HttpClients
import org.apache.http.message.BasicNameValuePair
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.springframework.http.HttpStatus
import tk.eabin.events.db.dao.Event
import tk.eabin.events.db.schema.UserGroupMaps
import tk.eabin.events.db.schema.Users
import tk.eabin.events.event.AppEventBus
import tk.eabin.events.event.EventCreatedEvent
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 05.01.17
 * Time: 21:00
 */
/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: Oct 9, 2010
 * Time: 10:11:06 PM
 */
class PushoverNotifier(val apiKey: String) : Notifier {
    val captionDateFormat = SimpleDateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.SHORT)

    init {
        AppEventBus.registerWithEventBus(this)
    }

    private fun sendMessage(user: String, message: String, payload: Map<String, String> = emptyMap()): Boolean {
        val client = HttpClients.createDefault()
        val post = HttpPost("https://api.pushover.net/1/messages.json")

        post.setHeader("Content-type", "application/x-www-form-urlencoded")

        val params = LinkedList<NameValuePair>()
        params.add(BasicNameValuePair("token", apiKey))
        params.add(BasicNameValuePair("user", user))
        params.add(BasicNameValuePair("message", message))

        for ((key, value) in payload) {
            params.add(BasicNameValuePair(key, value))
        }

        val entity = UrlEncodedFormEntity(params)

        post.setEntity(entity)

        val response = client.execute(post)
        if (response.getStatusLine().statusCode == HttpStatus.OK.value()) {
            println("Pushover notification sent.")
            return true
        } else {
            System.err.println("Error on posting to pushover login: " + response.getStatusLine().getReasonPhrase())
        }
        return false
    }

    @Subscribe
    fun onEventCreated(e: EventCreatedEvent) {
        println("Pushing event created to clients...")
        transaction {
            // find everybody in the event's group and notify them
            val event = Event[e.eventId] ?: return@transaction
            val groupIds = Event[e.eventId].groups.map { it.id }
            val users = (Users innerJoin UserGroupMaps).select {
                UserGroupMaps.group.inList(groupIds)
                        .and(Users.pushId.isNotNull())
            }.distinctBy { it[Users.id] }
            for (user in users) {
                val pushId = user[Users.pushId] ?: continue
                println("Pushing to user: " + user[Users.login])
                sendMessage(pushId, "New Event: ${event.category.name} @${event.location.name} - ${captionDateFormat.format(Date(event.startDate * 1000))}")
            }
        }
    }
}