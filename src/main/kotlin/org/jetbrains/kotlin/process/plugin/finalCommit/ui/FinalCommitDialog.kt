package org.jetbrains.kotlin.process.plugin.finalCommit.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.finalCommit.model.*
import org.jetbrains.kotlin.process.plugin.issue.model.changeIssueState
import org.jetbrains.kotlin.process.plugin.issue.model.getIssueOnBranch
import org.jetbrains.kotlin.process.plugin.issue.model.showStateChangeResultBanner
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class FinalCommitDialog(canBeParent: Boolean, private val branch: String, private val project: Project) :
    DialogWrapper(canBeParent) {
    private val commitMessageField = JTextArea(3, 35)

    init {
        title = "Commit"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val commitPanel = JPanel(BorderLayout())

        setIssueId(branch)
        commitMessageField.text = createDefaultDescriptionMessage()
        addAutocompleteToDescription()

        commitPanel.add(JLabel("Change issue state to Fixed and create final commit"), BorderLayout.PAGE_START)
        commitPanel.add(JLabel("Create final commit for branch:\n$branch"), BorderLayout.CENTER)
        commitPanel.add(commitMessageField, BorderLayout.PAGE_END)

        return commitPanel
    }

    override fun doOKAction() {
        changeStateToFixed(branch, project, this)

        super.doOKAction()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return commitMessageField
    }

    private fun addAutocompleteToDescription() {
        try {
            val reviewersLookup = getReviewers()
            val autoComplete = Autocomplete(commitMessageField, reviewersLookup)

            commitMessageField.document.addDocumentListener(autoComplete)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }
}