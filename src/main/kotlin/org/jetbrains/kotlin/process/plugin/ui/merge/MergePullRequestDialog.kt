package org.jetbrains.kotlin.process.plugin.ui.merge

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.model.merge.merge
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MergePullRequestDialog(canBeParent: Boolean) : DialogWrapper(canBeParent) {
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
            super.doOKAction()
        } catch (e: Throwable) {
            "stop here"
        }
    }
}