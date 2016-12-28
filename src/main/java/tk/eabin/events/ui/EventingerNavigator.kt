package tk.eabin.events.ui

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 21:44
 */

import com.vaadin.navigator.Navigator
import com.vaadin.navigator.View
import com.vaadin.navigator.ViewChangeListener
import com.vaadin.navigator.ViewProvider
import com.vaadin.ui.ComponentContainer
import com.vaadin.ui.UI
import tk.eabin.events.event.PostViewChangeEvent

class EventingerNavigator(container: ComponentContainer) : Navigator(UI.getCurrent(), container) {
    private var errorViewProvider: ViewProvider? = null

    init {

        val host = ui.page.location.host
        initViewChangeListener()
        initViewProviders()

    }

    private fun initViewChangeListener() {
        addViewChangeListener(object : ViewChangeListener {

            override fun beforeViewChange(event: ViewChangeListener.ViewChangeEvent): Boolean {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true
            }

            override fun afterViewChange(event: ViewChangeListener.ViewChangeEvent) {
                println("Change view to: " + event.viewName)
                MainUI.eventBus.post(PostViewChangeEvent(EventingerViews.find(event.viewName)))
            }
        })
    }

    private fun initViewProviders() {
        // A dedicated view provider is added for each separate view type
        for (viewType in EventingerViews.values()) {
            val viewProvider = object : Navigator.ClassBasedViewProvider(
                    viewType.viewName, viewType.viewClass) {

                // This field caches an already initialized view instance if the
                // view should be cached (stateful views).
                private var cachedInstance: View? = null

                override fun getView(viewName: String?): View? {
                    var result: View? = null
                    if (viewType.viewName == viewName) {
                        if (viewType.isStateful) {
                            // Stateful views get lazily instantiated
                            if (cachedInstance == null) {
                                cachedInstance = super.getView(viewType
                                        .viewName)
                            }
                            result = cachedInstance
                        } else {
                            // Non-stateful views get instantiated every time
                            // they're navigated to
                            result = super.getView(viewType.viewName)
                        }
                    }
                    return result
                }
            }

            if (viewType === ERROR_VIEW) {
                errorViewProvider = viewProvider
            }

            addProvider(viewProvider)
        }

        setErrorProvider(object : ViewProvider {
            override fun getViewName(viewAndParameters: String): String {
                return ERROR_VIEW.viewName
            }

            override fun getView(viewName: String): View {
                return errorViewProvider!!.getView(ERROR_VIEW.viewName)
            }
        })
    }

    companion object {

        private val ERROR_VIEW = EventingerViews.EVENTS
    }
}
