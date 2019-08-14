package org.jetbrains.kotlin.process.plugin.actions.rr

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.application.ApplicationManager
import org.jetbrains.kotlin.process.bot.rr.main

class RemoteRunStartAction : AnAction() {
    override fun actionPerformed(e: AnActionEvent) {
        ApplicationManager.getApplication().executeOnPooledThread {
            main()
        }
    }

    override fun update(e: AnActionEvent) {
        super.update(e)
        e.presentation.icon = AllIcons.Actions.Execute
    }
}