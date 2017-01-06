package tk.eabin.events.ui.views

import com.vaadin.event.ShortcutAction
import com.vaadin.server.Sizeable
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import tk.eabin.events.db.dao.EventCategory
import tk.eabin.events.db.dao.EventLocation


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 29.12.16
 * Time: 12:38
 */
class EditLocationWindow(val location: EventLocation?, val category: EventCategory, caption: String, val saveCallback: (window: EditLocationWindow) -> Unit) : Window(caption) {
    private val textName = TextField("Name", location?.name ?: "").apply {
        setWidth("99%")
    }

    private val btnSave = Button("Save")

    companion object {
        fun open(category: EventCategory, location: EventLocation?, saveCallback: (EditLocationWindow) -> Unit) {
            val caption = if (location != null) "Edit Location" else "Create Location"
            val w = EditLocationWindow(location, category, caption, saveCallback)
            UI.getCurrent().addWindow(w)
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

        result.addComponent(textName)
        textName.focus()

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
                saveCallback(this@EditLocationWindow)
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
    fun updateLocation(location: EventLocation) {
        location.apply {
            if (textName.value.isNotEmpty()) name = textName.value
        }
    }
}

