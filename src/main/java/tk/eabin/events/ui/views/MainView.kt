package tk.eabin.events.ui.views

import com.vaadin.ui.HorizontalLayout
import com.vaadin.ui.VerticalLayout
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
        setWidth("100%")
        addStyleName("mainview")

        addComponent(EventingerMenu())

        val content = VerticalLayout()
        content.setWidth("100%")
        addComponent(content)
        setExpandRatio(content, 1.0f)

        EventingerNavigator(content)

    }
}