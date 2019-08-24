package org.jetbrains.kotlin.process.plugin.rr.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.process.plugin.rr.model.startRr

class RemoteRunStartAction : AnAction(
    "Check remote run",
    "Starting check remote run in TeamCity",
    AllIcons.Actions.Execute
) {
    override fun actionPerformed(e: AnActionEvent) {
        ApplicationManager.getApplication().executeOnPooledThread {
            startRr()
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.Actions.Execute
    }
}