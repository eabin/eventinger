package tk.eabin.events.event

import com.google.common.eventbus.AsyncEventBus
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

/**
 * For subscribing to global events
 * User: eabin
 * Date: 28.12.16
 * Time: 09:02
 */
data class ParticipationChangedEvent(val eventId: Int)

data class EventUserChangedEvent(val eventId: Int)

data class EventCreatedEvent(val eventId: Int)
data class EventDeletedEvent(val eventId: Int)
data class EventChangedEvent(val eventId: Int)
data class CommentCreatedEvent(val eventId: Int)

private val bus = AsyncEventBus("App Event Bus", ThreadPoolExecutor(5, 10, 1, TimeUnit.MINUTES, LinkedBlockingDeque()))

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
