package tk.eabin.events.ui.views

import com.vaadin.ui.CssLayout
import com.vaadin.ui.HorizontalLayout
import tk.eabin.events.ui.EventingerMenu
import tk.eabin.events.ui.EventingerNavigator


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 28.12.16
 * Time: 21:39
 */

class MainView : HorizontalLayout() {
    init {
        setSizeFull()
        addStyleName("mainview")

        addComponent(EventingerMenu())

        val content = CssLayout()
        content.addStyleName("view-content")
        content.setSizeFull()
        addComponent(content)
        setExpandRatio(content, 1.0f)

        EventingerNavigator(content)

    }
}