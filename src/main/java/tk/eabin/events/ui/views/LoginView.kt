package tk.eabin.events.ui.views

import com.vaadin.event.ShortcutAction.KeyCode
import com.vaadin.server.FontAwesome
import com.vaadin.server.Responsive
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme

class LoginView : VerticalLayout() {
    interface LoginListener {
        fun onLogin(username: String, password: String, remember: Boolean)
    }

    private var listener: LoginListener? = null

    private val checkRememberMe = CheckBox("Remember me", true)


    fun setLoginListener(listener: LoginListener) {
        this.listener = listener
    }

    init {
        setSizeFull()

        val loginForm = buildLoginForm()
        addComponent(loginForm)
        setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER)

    }

    private fun buildLoginForm(): Component {
        val loginPanel = VerticalLayout()
        loginPanel.setSizeUndefined()
        loginPanel.isSpacing = true
        Responsive.makeResponsive(loginPanel)
        loginPanel.addStyleName("login-panel")

        loginPanel.addComponent(buildLabels())
        loginPanel.addComponent(buildFields())
        loginPanel.addComponent(checkRememberMe)
        return loginPanel
    }

    private fun buildFields(): Component {
        val fields = HorizontalLayout()
        fields.isSpacing = true
        fields.addStyleName("fields")

        val username = TextField("Username")
        username.icon = FontAwesome.USER
        username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON)
        username.focus()

        val password = PasswordField("Password")
        password.icon = FontAwesome.LOCK
        password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON)

        val signin = Button("Sign In")
        signin.addStyleName(ValoTheme.BUTTON_PRIMARY)
        signin.setClickShortcut(KeyCode.ENTER)

        fields.addComponents(username, password, signin)
        fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT)

        signin.addClickListener { if (listener != null) listener!!.onLogin(username.value, password.value, checkRememberMe.value) }
        return fields
    }

    private fun buildLabels(): Component {
        val labels = CssLayout()
        labels.addStyleName("labels")

        val welcome = Label("Welcome")
        welcome.setSizeUndefined()
        welcome.addStyleName(ValoTheme.LABEL_H4)
        welcome.addStyleName(ValoTheme.LABEL_COLORED)
        labels.addComponent(welcome)

        val title = Label("Eventinger v4")
        title.setSizeUndefined()
        title.addStyleName(ValoTheme.LABEL_H3)
        title.addStyleName(ValoTheme.LABEL_LIGHT)
        labels.addComponent(title)
        return labels
    }

}
