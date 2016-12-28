package tk.eabin.events.ui

import com.google.common.eventbus.EventBus
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.Responsive
import com.vaadin.server.VaadinRequest
import com.vaadin.spring.annotation.SpringUI
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
    }

    override fun detach() {
        super.detach()
        println("Cleaning up...")
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
                    val user = User.find { Users.login.eq(username) }
                    if (!user.empty()) {
                        val wannabe = user.first()
                        if (User.cryptPassword(wannabe.id.value, password) == wannabe.password) {
                            currentUser = wannabe
                            session.setAttribute(User::class.java, currentUser)
                            removeStyleName("loginview")
                            content = MainView()
                            navigator.navigateTo(navigator.state)
                        }
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