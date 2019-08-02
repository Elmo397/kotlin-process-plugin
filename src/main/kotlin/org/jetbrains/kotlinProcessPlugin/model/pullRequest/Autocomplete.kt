package org.jetbrains.kotlinProcessPlugin.model.pullRequest

import javax.swing.JTextArea
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import java.util.Collections
import javax.swing.SwingUtilities
import java.awt.event.ActionEvent
import javax.swing.AbstractAction

class Autocomplete(private var descriptionTextArea: JTextArea, private var reviewers: MutableList<String>) : DocumentListener {
    enum class Mode {
        INSERT,
        COMPLETION
    }

    private var mode = Mode.INSERT
    private val startPosition = 11 // Line "\n\nReviewer " have 11 symbols

    init {
        this.reviewers.sort()
        descriptionTextArea.caretPosition = startPosition
    }

    override fun insertUpdate(event: DocumentEvent?) {
        if (event!!.length != 1) return

        try {
            val position = event.offset
            val content = descriptionTextArea.getText(0, position + 1)

            val wordPosition = findWordBeginning(position, content)
            if (position - wordPosition < 1) return

            findCompletion(content, wordPosition, position)
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

    private fun findCompletion(content: String?, wordPosition: Int, position: Int) {
        val prefix = content!!.substring(wordPosition + 1).toLowerCase()
        val itemPositionInList = Collections.binarySearch(reviewers, prefix)

        if (itemPositionInList < 0 && -itemPositionInList <= reviewers.size) {
            val match = reviewers[-itemPositionInList - 1]

            if (match.startsWith(prefix)) {
                // A completion is found
                val completion = match.substring(position - wordPosition)
                // We cannot modify Document from within notification,
                // so we submit a task that does the change later
                SwingUtilities.invokeLater(CompletionTask(completion, position + 1))
            }
        } else {
            mode = Mode.INSERT
        }
    }

    inner class CommitAction : AbstractAction() {
        private val serialVersionUID = 5794543109646743416L

        override fun actionPerformed(ev: ActionEvent) {
            if (mode === Mode.COMPLETION) {
                val position = descriptionTextArea.selectionEnd
                val sb = StringBuffer(descriptionTextArea.text)

                sb.insert(position, " ")
                descriptionTextArea.text = sb.toString()
                descriptionTextArea.caretPosition = position + 1
                mode = Mode.INSERT
            } else {
                descriptionTextArea.replaceSelection("\t")
            }
        }
    }

    private inner class CompletionTask internal constructor(private val completion: String, private val position: Int) :
        Runnable {

        override fun run() {
            val sb = StringBuffer(descriptionTextArea.text)

            sb.insert(position, completion)
            descriptionTextArea.text = sb.toString()
            descriptionTextArea.caretPosition = position + completion.length
            descriptionTextArea.moveCaretPosition(position)

            mode = Mode.COMPLETION
        }
    }

    override fun changedUpdate(event: DocumentEvent?) {}

    override fun removeUpdate(event: DocumentEvent?) {}
}