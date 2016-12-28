package tk.eabin.events.event

import com.google.common.eventbus.EventBus
import tk.eabin.events.ui.EventingerViews

/**
 * For UI-local events
 * User: eabin
 * Date: 28.12.16
 * Time: 09:02
 */

data class PostViewChangeEvent(val view: EventingerViews)

class ProfileUpdatedEvent
class UserLoggedOutEvent

private val bus = EventBus()

object UIEventBus {
    fun registerWithEventBus(obj: Any) {
        bus.register(obj)
    }


    fun postEvent(event: Any) {
        bus.post(event)
    }

    fun unregisterFromEventBus(obj: Any) {
        bus.unregister(obj)
    }
}
