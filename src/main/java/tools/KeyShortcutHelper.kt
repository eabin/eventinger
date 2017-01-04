package tools

import com.vaadin.event.ShortcutListener
import com.vaadin.ui.TextField

/**
 * Created by IntelliJ IDEA.
 * User: eabin
 * Date: 04.01.17
 * Time: 20:39
 */
fun TextField.addKeyboardShortcutListener(name: String, code: Int, vararg modifierKeys: Int, action: () -> Unit) {
    val listener = object : ShortcutListener(name, null, code, modifierKeys) {
        override fun handleAction(p0: Any?, p1: Any?) {
            action()
        }
    }
    addFocusListener { addShortcutListener(listener) }
    addBlurListener { removeShortcutListener(listener) }
}
