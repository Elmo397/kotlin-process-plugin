package org.jetbrains.kotlin.process.plugin.actions.issue

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.process.bot.rr.main
import org.jetbrains.kotlin.process.plugin.ui.issue.TicketSelectionDialog

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

