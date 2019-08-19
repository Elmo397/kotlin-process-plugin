package org.jetbrains.kotlin.process.plugin.ui.merge

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.model.issue.changeIssueState
import org.jetbrains.kotlin.process.plugin.model.issue.showStateChangeResultBanner
import org.jetbrains.kotlin.process.plugin.model.merge.merge
import org.jetbrains.kotlin.process.plugin.ui.issue.TicketSelectionDialog
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MergePullRequestDialog(canBeParent: Boolean, private val project: Project) : DialogWrapper(canBeParent) {
    init {
        title = "Merge pull request"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val mergePanel = JPanel()
        mergePanel.add(JLabel("Merge?!"))

        return mergePanel
    }

    override fun doOKAction() {
        merge()

        val issueId = PropertiesComponent.getInstance().getValue("issueId")!!
        val commandResult = changeIssueState(issueId, project, "State Fixed")
        showStateChangeResultBanner(commandResult, this.contentPanel, "Fixed")

        super.doOKAction()
    }
}