package org.jetbrains.kotlinProcessPlugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlinProcessPlugin.ui.TicketSelectionDialog

/**
 * @author Mamedova Elnara
 */
class TicketAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TicketSelectionDialog(true).openDialog(e.project!!)
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.Vcs.Vendors.Github
        TicketSelectionDialog.setProject(e.project!!)
    }
}

