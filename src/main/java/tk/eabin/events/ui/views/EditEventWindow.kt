package tk.eabin.events.ui.views

import com.vaadin.data.util.BeanItemContainer
import com.vaadin.event.ShortcutAction
import com.vaadin.server.FontAwesome
import com.vaadin.server.Sizeable
import com.vaadin.shared.ui.datefield.Resolution
import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import tk.eabin.events.db.dao.*
import tk.eabin.events.db.schema.EventGroupMaps
import tk.eabin.events.db.schema.EventLocations
import tk.eabin.events.ui.MainUI
import java.util.*


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 29.12.16
 * Time: 12:38
 */
class EditEventWindow(val event: Event?, caption: String, val saveCallback: (window: EditEventWindow) -> Unit) : Window(caption) {
    private val textInfo = TextField("Info", event?.comment ?: "")
    private val comboCategory = ComboBox("Category")
    private val comboLocation = ComboBox("Location")


    private val dateStart = DateField("Start Date", if (event != null) Date(event.startDate * 1000) else null)
    private val textMinPeople = TextField("Min. People", event?.minPeople?.toString() ?: "2").apply {
        addValidator {
            try {
                val i = Integer.parseInt(it.toString())
                if (i <= 0) {
                    throw com.vaadin.data.Validator.InvalidValueException("Must be a positive number")
                }
            } catch(e: Exception) {
                throw com.vaadin.data.Validator.InvalidValueException("Not an integer")
            }
        }
    }
    private val optionGroups = OptionGroup("Groups")
    private val btnSave = Button("Save")

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

        val locationSelector = buildCategoryLocationSelector()
        result.addComponent(comboCategory)
        result.addComponent(locationSelector)
        textInfo.setWidth("100%")
        result.addComponent(textInfo)
        configureStartDate()
        result.addComponent(dateStart)
        result.addComponent(textMinPeople)
        configureGroups()
        result.addComponent(optionGroups)

        setupChangeListeners()

        result.addComponent(buildFooter())

        return result
    }

    private fun configureGroups() {
        optionGroups.isMultiSelect = true
        val items = BeanItemContainer(UserGroup::class.java)
        transaction {
            for (g in MainUI.currentUser?.groups ?: emptyList<UserGroup>()) {
                items.addBean(g)
            }
            optionGroups.containerDataSource = items
            optionGroups.itemCaptionMode = AbstractSelect.ItemCaptionMode.PROPERTY
            optionGroups.itemCaptionPropertyId = "name"

            if (event != null) {
                for (g in event.groups) optionGroups.select(g)
            }
        }
    }

    private fun setupChangeListeners() {
        textMinPeople.addValueChangeListener { checkSaveable() }
        comboLocation.addValueChangeListener { checkSaveable() }
        dateStart.addValueChangeListener { checkSaveable() }
        optionGroups.addValueChangeListener { checkSaveable() }
        textInfo.addValueChangeListener { checkSaveable() }
    }

    private fun configureStartDate() {
        dateStart.resolution = Resolution.MINUTE
    }

    private fun buildCategoryLocationSelector(): Component {
        return transaction {
            val categories = EventCategory.all()
            val container = BeanItemContainer(EventCategory::class.java)
            for (category in categories) {
                container.addBean(category)
            }

            comboCategory.setWidth("100%")
            comboCategory.containerDataSource = container
            comboCategory.itemCaptionMode = AbstractSelect.ItemCaptionMode.PROPERTY
            comboCategory.itemCaptionPropertyId = "name"
            comboCategory.select(event?.category)

            comboCategory.addValueChangeListener {
                val category = it.property.value as EventCategory
                populateLocations(category)
            }

            if (event != null) populateLocations(event.category)

            comboLocation.itemCaptionMode = AbstractSelect.ItemCaptionMode.PROPERTY
            comboLocation.itemCaptionPropertyId = "name"
            comboLocation.select(event?.location)

            val btnCreateLocation = Button(FontAwesome.PLUS).apply {
                addClickListener {
                    val category = comboCategory.value as EventCategory? ?: return@addClickListener
                    EditLocationWindow.open(category, null) {
                        val location = if (it.location != null) {
                            println("Saving location")
                            transaction {
                                it.updateLocation(it.location)
                            }
                            it.location
                        } else {
                            transaction {
                                EventLocation.new {
                                    this.category = category
                                    it.updateLocation(this)
                                }
                            }
                        }
                        populateLocations(category, location)
                    }
                }
            }

            val locationSelector = HorizontalLayout(comboLocation, btnCreateLocation)
            locationSelector.setExpandRatio(comboLocation, 1f)
            locationSelector.setComponentAlignment(btnCreateLocation, Alignment.BOTTOM_CENTER)
            locationSelector
        }
    }

    private fun populateLocations(category: EventCategory, preselect: EventLocation? = null) {
        val dataSource = BeanItemContainer(EventLocation::class.java)
        var actualPreselect: EventLocation? = null
        transaction {
            val locations = EventLocation.find { EventLocations.categoryId.eq(category.id) }
            for (location in locations) {
                dataSource.addBean(location)
                println("Adding location: ${location.id} -> ${location.name}")
                // TODO: for some reason, a just created Entity is not equal to the re-read one from the db;
                // just compare ids as a workaround for now
                if (location.id == preselect?.id) actualPreselect = location
            }
        }
        comboLocation.containerDataSource = dataSource
        comboLocation.select(actualPreselect)
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
                saveCallback(this@EditEventWindow)
                close()
            }
        }
        btnSave.setClickShortcut(ShortcutAction.KeyCode.ENTER)

        footer.addComponents(cancel, btnSave)
        footer.setExpandRatio(cancel, 1f)
        footer.setComponentAlignment(cancel, Alignment.TOP_RIGHT)
        return footer
    }

    private val selectedGroups: Set<UserGroup>
        get() {
            return optionGroups.value as Set<UserGroup>
        }


    private fun checkSaveable() {
        btnSave.isEnabled = comboCategory.value != null && comboLocation.value != null
                && textMinPeople.isValid
                && !dateStart.isEmpty
                && !selectedGroups.isEmpty()
                && !textInfo.isEmpty
    }

    /**
     * this will modify [event], so this can only be called from within a transaction
     * also, it will update the assoc table for the selected groups
     */
    fun updateEvent(event: Event) {
        event.apply {
            MainUI.currentUser?.let {
                creator = it
            }
            comment = textInfo.value
            startDate = dateStart.value.time / 1000
            minPeople = textMinPeople.value.toInt()
            category = comboCategory.value as EventCategory
            location = comboLocation.value as EventLocation
        }
    }

    fun updateEventGroups(event: Event) {
        EventGroupMaps.deleteWhere { EventGroupMaps.event.eq(event.id) }
        selectedGroups.forEach {
            EventGroupMap.new {
                this.event = event
                this.group = it
            }
        }
    }
}

