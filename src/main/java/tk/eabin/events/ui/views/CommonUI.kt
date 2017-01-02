package tk.eabin.events.ui.views

import com.vaadin.ui.*
import com.vaadin.ui.themes.ValoTheme


/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 02.01.17
 * Time: 20:01
 */

fun showConfirmDialog(ui: UI, caption: String, text: String, textYes: String = "Yes",
                      onConfirm: () -> Unit) {
    val message = Label(text)
    message.setWidth("25em")

    val confirmDialog = Window(caption)
    confirmDialog.setModal(true)
    confirmDialog.setClosable(false)
    confirmDialog.setResizable(false)

    val root = VerticalLayout()
    root.isSpacing = true
    root.setMargin(true)
    confirmDialog.setContent(root)

    val footer = HorizontalLayout()
    footer.addStyleName(ValoTheme.WINDOW_BOTTOM_TOOLBAR)
    footer.setWidth("100%")
    footer.isSpacing = true

    root.addComponents(message, footer)

    val ok = Button(textYes, Button.ClickListener {
        confirmDialog.close()
        onConfirm()
    })
    ok.addStyleName(ValoTheme.BUTTON_PRIMARY)

    val cancel = Button("Cancel", Button.ClickListener {
        confirmDialog.close()
    })

    footer.addComponents(cancel, ok)
    footer.setExpandRatio(cancel, 1f)

    ui.addWindow(confirmDialog)
    confirmDialog.focus()
}