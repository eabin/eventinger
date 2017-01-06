package tk.eabin.events.ui.views

import com.vaadin.event.ShortcutAction
import com.vaadin.server.Sizeable
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.EventCategory
import tk.eabin.events.db.dao.EventSubscription
import tk.eabin.events.db.dao.User
import tk.eabin.events.db.schema.EventCategories
import tk.eabin.events.db.schema.EventSubscriptions


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 29.12.16
 * Time: 12:38
 */
class CategorySubscriptionWindow(val user: User, val saveCallback: (window: CategorySubscriptionWindow) -> Unit) : Window("Update Subscriptions") {
    private val subscriptions = mutableMapOf<Int, CheckBox>()

    private val btnSave = Button("Save")

    companion object {
        fun open(user: User) {
            val w = CategorySubscriptionWindow(user) {
                it.updateSubscriptions()
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

        val subscriptionComponent = buildCategorySubscriptions()
        result.addComponent(subscriptionComponent)

        setupChangeListeners()

        result.addComponent(buildFooter())

        return result
    }

    private fun buildCategorySubscriptions(): Component {
        val subscriptionChecks = VerticalLayout()
        subscriptionChecks.setWidth("100%")
        transaction {
            val s = (EventCategories leftJoin EventSubscriptions).select {
                EventSubscriptions.userId.eq(user.id).or(EventSubscriptions.userId.isNull())
            }
            for (c in s) {
                val checkbox = CheckBox(c[EventCategories.name], c.tryGet(EventSubscriptions.byJabber) ?: true)
                subscriptions.put(c[EventCategories.id].value, checkbox)
                subscriptionChecks.addComponent(checkbox)
            }
        }
        return subscriptionChecks
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
                saveCallback(this@CategorySubscriptionWindow)
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
    private fun updateSubscriptions() {
        transaction {
            val s = (EventCategories leftJoin EventSubscriptions).select {
                EventSubscriptions.userId.eq(user.id).or(EventSubscriptions.userId.isNull())
            }
            for (c in s) {
                val sId = c.tryGet(EventSubscriptions.id)
                val isSubscribed = subscriptions[c[EventCategories.id].value]?.value ?: true
                if (sId != null) {
                    EventSubscription[sId].byJabber = isSubscribed
                } else {
                    EventSubscription.new {
                        category = EventCategory[c[EventCategories.id]]
                        user = this@CategorySubscriptionWindow.user
                        byJabber = isSubscribed
                    }
                }
            }
        }
    }
}

