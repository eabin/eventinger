package tk.eabin.events.ui

import com.vaadin.navigator.View
import tk.eabin.events.ui.views.EventsView

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 21:48
 */
enum class EventingerViews(val viewName: String, val viewClass: Class<out View>, val isStateful: Boolean) {
    EVENTS("events", EventsView::class.java, true);

    companion object {
        fun find(viewName: String): EventingerViews = values().first { it.viewName == viewName }
    }
}