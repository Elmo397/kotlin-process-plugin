package org.jetbrains.kotlin.process.plugin.actions.merge

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.process.plugin.ui.merge.MergePullRequestDialog

class MergeAction : AnAction(
    "Merge to master",
    "Merge to master",
    AllIcons.Vcs.Merge) {
    override fun actionPerformed(e: AnActionEvent) {
        MergePullRequestDialog(true, e.project!!).show()
    }
}