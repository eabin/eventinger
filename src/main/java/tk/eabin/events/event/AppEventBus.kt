package tk.eabin.events.event

import com.google.common.eventbus.EventBus

/**
 * For subscribing to global events
 * User: eabin
 * Date: 28.12.16
 * Time: 09:02
 */

private val bus = EventBus()

object AppEventBus {
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
