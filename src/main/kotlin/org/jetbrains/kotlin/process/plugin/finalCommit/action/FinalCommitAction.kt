package org.jetbrains.kotlin.process.plugin.finalCommit.action

import com.github.jk1.ytplugin.whenActive
import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import git4idea.DialogManager
import org.jetbrains.kotlin.process.plugin.finalCommit.ui.FinalCommitDialog
import org.jetbrains.kotlin.process.plugin.rr.model.success
import org.jetbrains.kotlin.process.plugin.rr.ui.WarningPanel

class FinalCommitAction(private val getSelectedBranch: (() -> String?)?) : AnAction(
    "Final commit",
    "Create final commit message and change issue state to Fixed",
    AllIcons.Actions.Commit
) {
    override fun actionPerformed(e: AnActionEvent) {
        e.whenActive {
            val branch = getSelectedBranch?.invoke()

            if (branch != null) {
                when {
                    success -> {
                        val dialog = FinalCommitDialog(false, branch, it)
                        DialogManager.show(dialog)
                    }
                    else -> WarningPanel(false, branch, it)
                }
            }
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.isEnabled = getSelectedBranch?.invoke() != null
        e.presentation.icon = AllIcons.Actions.Commit
    }
}