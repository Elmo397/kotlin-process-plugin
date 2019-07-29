package org.jetbrains.kotlinProcessPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlinProcessPlugin.ui.CommitCreator

/**
 * @author Mamedova Elnara
 */
class CommitAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        CommitCreator().openPullRequestDialog(e.project!!)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.Vcs.Vendors.Github
    }
}