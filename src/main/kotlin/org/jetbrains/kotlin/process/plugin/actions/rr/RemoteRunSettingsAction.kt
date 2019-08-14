package org.jetbrains.kotlin.process.plugin.actions.rr

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class RemoteRunSettingsAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.General.Settings
    }
}