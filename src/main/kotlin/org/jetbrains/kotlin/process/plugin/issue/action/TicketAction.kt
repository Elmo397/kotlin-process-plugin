package org.jetbrains.kotlin.process.plugin.issue.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.process.plugin.issue.ui.TicketSelectionDialog

/**
 * @author Mamedova Elnara
 */
class TicketAction : AnAction(
    "Issue",
    "Choose issue and create branch",
    AllIcons.Vcs.Branch
) {
    override fun actionPerformed(e: AnActionEvent) {
        TicketSelectionDialog(true).openDialog(e.project!!)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.Vcs.Branch
        TicketSelectionDialog.setProject(e.project!!)
    }
}

