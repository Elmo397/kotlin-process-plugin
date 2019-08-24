package org.jetbrains.kotlin.process.plugin.finalCommit.model

import com.intellij.ui.components.JBList
import java.awt.Dimension
import java.awt.Window
import java.awt.event.FocusEvent
import java.awt.event.FocusListener
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import java.util.function.Function
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import kotlin.math.min

class Autocomplete(
    private var descriptionTextArea: JTextArea,
    private var reviewersLookup: Function<String, List<String>>
) : DocumentListener, FocusListener, KeyListener {
    private val startPosition = 11 // Line "\n\nReviewer " have 11 symbols
    private val results: MutableList<String> = arrayListOf()
    private var offeredOptionsWindow: JWindow
    private var lookupList: JBList<*>
    private val model: ListModel

    init {
        descriptionTextArea.caretPosition = startPosition

        val parent = SwingUtilities.getWindowAncestor(descriptionTextArea)
        offeredOptionsWindow = JWindow(parent)
        offeredOptionsWindow.type = Window.Type.POPUP
        offeredOptionsWindow.focusableWindowState = false
        offeredOptionsWindow.isAlwaysOnTop = true

        model = ListModel()
        lookupList = JBList(model)

        offeredOptionsWindow.add(object : JScrollPane(lookupList) {
            override fun getPreferredSize(): Dimension {
                val preferredSize = super.getPreferredSize()
                preferredSize.width = descriptionTextArea.width
                return preferredSize
            }
        })

        descriptionTextArea.addFocusListener(this)
        descriptionTextArea.document.addDocumentListener(this)
        descriptionTextArea.addKeyListener(this)
    }

    override fun insertUpdate(event: DocumentEvent?) {
        documentChanged(event)
    }

    override fun changedUpdate(event: DocumentEvent?) {
        documentChanged(event)
    }

    override fun removeUpdate(event: DocumentEvent?) {
        documentChanged(event)
    }

    private fun documentChanged(event: DocumentEvent?) {
        try {
            val position = event!!.offset
            val content = descriptionTextArea.getText(0, position + 1)
            val wordPosition = findWordBeginning(position, content)
            val prefix = content!!.substring(wordPosition + 1).toLowerCase()

            SwingUtilities.invokeLater(CompletionTask(prefix))
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    private fun findWordBeginning(position: Int, content: String?): Int {
        var wordPosition = position

        while (wordPosition >= startPosition) {
            if (!Character.isLetter(content!!.toCharArray()[wordPosition])) {
                break
            }
            wordPosition--
        }

        return wordPosition
    }

    override fun focusLost(event: FocusEvent?) {
        SwingUtilities.invokeLater { this.hideAutocompletePopup() }
    }

    override fun focusGained(event: FocusEvent?) {
        SwingUtilities.invokeLater {
            if (results.size > 0) {
                showAutocompletePopup()
            }
        }
    }

    private fun showAutocompletePopup() {
        val location = descriptionTextArea.locationOnScreen
        val height = descriptionTextArea.height

        offeredOptionsWindow.setLocation(location.x, location.y + height)
        offeredOptionsWindow.isVisible = true
    }

    private fun hideAutocompletePopup() {
        offeredOptionsWindow.isVisible = false
    }

    override fun keyPressed(event: KeyEvent?) {
        when {
            event!!.keyCode == KeyEvent.VK_UP -> {
                val index = lookupList.selectedIndex

                if (index != -1 && index > 0) {
                    lookupList.selectedIndex = index - 1
                }
            }
            event.keyCode == KeyEvent.VK_DOWN -> {
                val index = lookupList.selectedIndex

                if (index != -1 && lookupList.model.size > index + 1) {
                    lookupList.selectedIndex = index + 1
                }
            }
            event.keyCode == KeyEvent.VK_ENTER -> { //TODO: how tab backspace after this event?!
                val text = lookupList.selectedValue as String

                descriptionTextArea.text = addReviewerNameToMsg(text)
                descriptionTextArea.caretPosition = 0
            }
            event.keyCode == KeyEvent.VK_ESCAPE -> hideAutocompletePopup()
        }
    }

    override fun keyReleased(event: KeyEvent?) {}

    override fun keyTyped(event: KeyEvent?) {}

    private inner class ListModel : AbstractListModel<Any>() {
        override fun getSize(): Int {
            return results.size
        }

        override fun getElementAt(index: Int): Any {
            return results[index]
        }

        fun updateView() {
            super.fireContentsChanged(this, 0, size)
        }
    }

    private inner class CompletionTask(private val content: String) : Runnable {
        override fun run() {
            results.clear()
            results.addAll(reviewersLookup.apply(content))

            model.updateView()
            lookupList.visibleRowCount = min(results.size, 10)

            if (results.size > 0) {
                lookupList.selectedIndex = 0
            }

            offeredOptionsWindow.pack()

            if (results.size > 0) {
                showAutocompletePopup()
            } else {
                hideAutocompletePopup()
            }
        }
    }
}