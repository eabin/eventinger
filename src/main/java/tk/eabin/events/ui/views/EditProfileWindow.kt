package tk.eabin.events.ui.views

import com.vaadin.event.ShortcutAction
import com.vaadin.server.Sizeable
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.User


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 29.12.16
 * Time: 12:38
 */
class EditProfileWindow(val user: User?, caption: String, val saveCallback: (window: EditProfileWindow) -> Unit) : Window(caption) {
    private val textPushoverId = TextField("PushOver ID", user?.pushId ?: "").apply {
        setWidth("99%")
    }

    private val btnSave = Button("Save")

    companion object {
        fun open(user: User?) {
            val caption = if (user != null) "Edit Profile" else "Create User"
            val w = EditProfileWindow(user, caption) {
                if (it.user != null) {
                    println("Saving user")
                    transaction {
                        it.updateUser(it.user)
                        it.user.flush()
                    }
                }
            }
            UI.getCurrent().addWindow(w)
            w.focus()
        }
    }

    init {
        isModal = true
        isResizable = false
        setWidth(400f, Sizeable.Unit.PIXELS)
        addStyleName("edit-dashboard")

        content = buildContent()
    }

    private fun buildContent(): Component {
        val result = VerticalLayout()
        result.setMargin(true)
        result.isSpacing = true

        result.addComponent(textPushoverId)

        setupChangeListeners()

        result.addComponent(buildFooter())

        return result
    }


    private fun setupChangeListeners() {
    }


    private fun buildFooter(): Component {
        val footer = HorizontalLayout()
        footer.isSpacing = true
        footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
        footer.setWidth(100.0f, Sizeable.Unit.PERCENTAGE)

        val cancel = Button("Cancel").apply {
            addClickListener {
                close()
            }
        }
        cancel.setClickShortcut(ShortcutAction.KeyCode.ESCAPE)


        checkSaveable()
        btnSave.apply {
            addStyleName(ValoTheme.BUTTON_PRIMARY)
            addClickListener {
                saveCallback(this@EditProfileWindow)
                close()
            }
        }
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER)

        footer.addComponents(cancel, btnSave)
        footer.setExpandRatio(cancel, 1f)
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT)
        return footer
    }


    private fun checkSaveable() {
        btnSave.isEnabled = true
    }

    /**
     * this will modify [event], so this can only be called from within a transaction
     * also, it will update the assoc table for the selected groups
     */
    fun updateUser(user: User) {
        user.apply {
            if (textPushoverId.value.isNotEmpty()) pushId = textPushoverId.value
        }
    }
}

