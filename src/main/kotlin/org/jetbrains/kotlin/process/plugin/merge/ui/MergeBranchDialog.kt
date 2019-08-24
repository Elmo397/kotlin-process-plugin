package org.jetbrains.kotlin.process.plugin.merge.ui

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.issue.model.changeIssueState
import org.jetbrains.kotlin.process.plugin.issue.model.showStateChangeResultBanner
import org.jetbrains.kotlin.process.plugin.merge.model.merge
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MergeBranchDialog(canBeParent: Boolean, private val project: Project) : DialogWrapper(canBeParent) {
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
        try {
            merge()

            val issueId = PropertiesComponent.getInstance().getValue("issueId")!!
            val commandResult =
                changeIssueState(issueId, project, "State Fixed")
            showStateChangeResultBanner(
                commandResult,
                this.contentPanel,
                "Fixed"
            )
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        super.doOKAction()
    }
}