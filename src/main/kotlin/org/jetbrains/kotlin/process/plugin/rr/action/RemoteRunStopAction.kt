package org.jetbrains.kotlin.process.plugin.rr.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent

class RemoteRunStopAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        //here the stop class of the remote run check should be called
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.Debugger.KillProcess
    }
}