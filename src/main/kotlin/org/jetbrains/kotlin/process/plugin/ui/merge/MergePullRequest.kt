package org.jetbrains.kotlin.process.plugin.ui.merge

import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.bot.git.main
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MergePullRequest(canBeParent: Boolean) : DialogWrapper(canBeParent) {
    override fun createCenterPanel(): JComponent? {
        val mergePanel = JPanel()
        mergePanel.add(JLabel("Merge?!"))

        return mergePanel
    }

    override fun doOKAction() {
        main()

        super.doOKAction()
    }
}