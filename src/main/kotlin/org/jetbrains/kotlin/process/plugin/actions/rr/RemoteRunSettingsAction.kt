package org.jetbrains.kotlin.process.plugin.actions.rr

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import org.jetbrains.kotlin.process.plugin.ui.rr.Settings

class RemoteRunSettingsAction : AnAction(
    "Remote run settings",
    "Set wait time between checks",
    AllIcons.General.Settings
) {
    override fun actionPerformed(e: AnActionEvent) {
        Settings(false).show()
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.General.Settings
    }
}