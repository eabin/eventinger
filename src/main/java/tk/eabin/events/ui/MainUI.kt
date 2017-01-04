package tk.eabin.events.ui

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import com.vaadin.annotations.Push
import com.vaadin.annotations.Theme
import com.vaadin.annotations.Title
import com.vaadin.server.*
import com.vaadin.shared.Position
import com.vaadin.spring.annotation.SpringUI
import com.vaadin.ui.Notification
import com.vaadin.ui.UI
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.User
import tk.eabin.events.db.dao.UserCookie
import tk.eabin.events.db.schema.UserCookies
import tk.eabin.events.db.schema.Users
import tk.eabin.events.event.UserLoggedOutEvent
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
    private val bus = EventBus()

    companion object {
        val LOGIN_COOKIE = "eventinger-login"

        val eventBus: EventBus
            get() {
                return (getCurrent() as MainUI).bus
            }

        val currentUser: User?
            get() {
                return VaadinSession.getCurrent().getAttribute(User::class.java)
            }
    }

    init {
        bus.register(this)
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

        checkUserCookie()

        if (currentUser == null) {
            val loginForm = LoginView()
            loginForm.setLoginListener(object : LoginView.LoginListener {
                override fun onLogin(username: String, password: String, remember: Boolean) {
                    System.err.println(
                            "Logged in with user name " + username +
                                    " and password " + password.length)
                    transaction {
                        var success = false
                        val user = User.find { Users.login.eq(username) }
                        if (!user.empty()) {
                            val wannabe = user.first()
                            if (User.cryptPassword(wannabe.id.value, password) == wannabe.password) {
                                VaadinSession.getCurrent().setAttribute(User::class.java, wannabe)
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
                        } else if (remember) {
                            val sessionKey = UserCookie.generateSessionKey()
                            val cookie = UserCookie.new {
                                this.user = currentUser!!
                                this.cookie = sessionKey
                            }
                            VaadinSession.getCurrent().setAttribute(UserCookie::class.java, cookie)
//                            val loginCookie = Cookie(LOGIN_COOKIE, sessionKey)
//                            VaadinService.getCurrentResponse().addCookie(loginCookie)

                            // this seems like a hack, but apparently with websockets it is not easily possible
                            // to set cookies anymore?
                            Page.getCurrent().getJavaScript().execute(String.format("document.cookie = '%s=%s;';", LOGIN_COOKIE, sessionKey));
                        }
                    }
                }
            })

            /*{ username, password ->
    } )    */
            content = loginForm
            addStyleName("loginview")
        } else {
            content = MainView()
            navigator.navigateTo(navigator.state)
        }
    }

    /**
     * Check if user can be loaded from cookie
     */
    private fun checkUserCookie() {
        val cookies = VaadinService.getCurrentRequest().cookies
        cookies.forEach { println("COOKIE: ${it.name} = ${it.value}") }
        cookies.firstOrNull { it.name == LOGIN_COOKIE }?.let {
            transaction {
                UserCookie.find { UserCookies.cookie.eq(it.value) }.firstOrNull()?.let {
                    println("Usercookie confirmed.")
                    VaadinSession.getCurrent().setAttribute(UserCookie::class.java, it)
                    VaadinSession.getCurrent().setAttribute(User::class.java, it.user)
                }
            }
        }
    }


    @Subscribe
    fun onLogout(e: UserLoggedOutEvent) {
        println("Logging out user: ${currentUser?.login} with cookie: ${VaadinSession.getCurrent().getAttribute(UserCookie::class.java)}")
        VaadinSession.getCurrent().getAttribute(UserCookie::class.java)?.let {
            transaction {
                println("Usercookie deleted")
                it.delete()
            }
        }
        VaadinSession.getCurrent().setAttribute(User::class.java, null)
        VaadinSession.getCurrent().close()
        Page.getCurrent().reload()
    }
}