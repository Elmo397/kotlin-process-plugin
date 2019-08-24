package org.jetbrains.kotlin.process.plugin.rr.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import git4idea.DialogManager
import org.jetbrains.kotlin.process.plugin.finalCommit.ui.FinalCommitDialog
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.UIManager

class WarningPanel(canBeParent: Boolean, private val branch: String, private val project: Project) :
    DialogWrapper(canBeParent) {
    init {
        title = "Warning"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val label = JLabel("Some builds in teamcity was failed. Are you sure want change issue state to Fixed?")
        label.icon = UIManager.getIcon("OptionPane.warningIcon")

        val mergePanel = JPanel(BorderLayout())
        mergePanel.add(label)

        return mergePanel
    }

    override fun doOKAction() {
        val dialog = FinalCommitDialog(false, branch, project)
        DialogManager.show(dialog)

        super.doOKAction()
    }
}