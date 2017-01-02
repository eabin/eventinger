package tk.eabin.events.ui

import com.google.common.eventbus.EventBus
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.Page
import com.vaadin.server.Responsive
import com.vaadin.server.VaadinRequest
import com.vaadin.server.VaadinSession
import com.vaadin.shared.Position
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.User
import tk.eabin.events.db.schema.Users
import tk.eabin.events.ui.views.LoginView
import tk.eabin.events.ui.views.MainView


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 23.12.16
 * Time: 23:00
 */
@SpringUI
@Theme("dashboard")
@Title("EventManager")
@Push
open class MainUI : UI() {
    private var currentUser: User? = null
    private val bus = EventBus()

    companion object {
        val eventBus: EventBus
            get() {
                return (getCurrent() as MainUI).bus
            }

        val currentUser: User
            get() {
                return VaadinSession.getCurrent().getAttribute(User::class.java)
            }
    }

    override fun detach() {
        super.detach()
        println("Cleaning up...")
    }

    private fun notify(caption: String, info: String) {
        val notification = Notification(caption)
        notification.description = info
        notification.isHtmlContentAllowed = true
        notification.styleName = "tray dark small closable"
        notification.position = Position.BOTTOM_CENTER
        notification.delayMsec = 60000
        notification.show(Page.getCurrent())

    }

    override fun init(request: VaadinRequest) {
        Responsive.makeResponsive(this)
        addStyleName(ValoTheme.UI_WITH_MENU);

        currentUser = session.getAttribute(User::class.java)
        if (currentUser == null) {
            val loginForm = LoginView()
            loginForm.setLoginListener { username, password ->
                System.err.println(
                        "Logged in with user name " + username +
                                " and password " + password.length)
                transaction {
                    var success = false
                    val user = User.find { Users.login.eq(username) }
                    if (!user.empty()) {
                        val wannabe = user.first()
                        if (User.cryptPassword(wannabe.id.value, password) == wannabe.password) {
                            currentUser = wannabe
                            session.setAttribute(User::class.java, currentUser)
                            removeStyleName("loginview")
                            content = MainView()
                            navigator.navigateTo(navigator.state)
                            success = true
                        } else {
                        }
                    }
                    println("Login check: $success")
                    if (!success) {
                        notify("Invalid username/password", "The username/password you entered is not correct")
                    }
                }
            }
            content = loginForm
            addStyleName("loginview")
        } else {
            content = MainView()
            navigator.navigateTo(navigator.state)
        }
    }


}