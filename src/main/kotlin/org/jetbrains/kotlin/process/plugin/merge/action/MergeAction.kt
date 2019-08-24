package org.jetbrains.kotlin.process.plugin.merge.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.process.plugin.merge.model.projectForMergeAction
import org.jetbrains.kotlin.process.plugin.merge.ui.MergeBranchDialog

class MergeAction : AnAction(
    "Merge to master",
    "Merge to master",
    AllIcons.Vcs.Merge) {
    override fun actionPerformed(e: AnActionEvent) {
        projectForMergeAction = e.project!!
        MergeBranchDialog(true, e.project!!).show()
    }
}