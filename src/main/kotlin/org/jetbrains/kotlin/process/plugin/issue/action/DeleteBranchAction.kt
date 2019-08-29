package org.jetbrains.kotlin.process.plugin.issue.action

import com.github.jk1.ytplugin.whenActive
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.process.plugin.issue.ui.DeleteBranchDialog

class DeleteBranchAction(private val getSelectedBranch: (() -> String?)?) : AnAction(
    "Delete Branch",
    "Deletes the selected branch if the issue has already been fixed",
    AllIcons.Vcs.Remove
) {
    override fun actionPerformed(e: AnActionEvent) {
        e.whenActive {
            val branch = getSelectedBranch?.invoke()

            if (branch != null) {
                DeleteBranchDialog(false, e.project!!, branch).show()
            }
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabled = getSelectedBranch?.invoke() != null
        e.presentation.icon = AllIcons.Vcs.Remove
    }
}