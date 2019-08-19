package org.jetbrains.kotlin.process.plugin.model.issue

import com.github.jk1.ytplugin.YouTrackPluginApiComponent
import com.github.jk1.ytplugin.rest.IssuesRestClient
import com.github.jk1.ytplugin.tasks.TaskManagerProxyComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import javax.swing.JComponent

var urlIssueMap = hashMapOf<String, String>()

//TODO: why it doesn't work?! ¯\_(ツ)_/¯
fun changeIssueState(issueId: String, project: Project, command: String): Boolean {
    return try {
        val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()

        var result = false
        repositories
            .filter { repository -> urlIssueMap[issueId]!!.startsWith(repository.url) }
            .forEach { repository ->
                val youTrack = YouTrackPluginApiComponent(project)

                val issue = IssuesRestClient(repository).getIssue(issueId)
                result = youTrack.executeCommand(issue!!, command).isSuccessful
            }

        result
    } catch (e: Throwable) {
        e.printStackTrace()
        false
    }
}

fun showStateChangeResultBanner(result: Boolean, dialog: JComponent) {
    when {
        result -> {
            val msg = "Issue state successfully changed to \"In Process\""
            Messages.showMessageDialog(dialog, msg, "Success", Messages.getInformationIcon())
        }
        else -> {
            val msg = "Issue state has not changed"
            Messages.showMessageDialog(dialog, msg, "Error", Messages.getErrorIcon())
        }
    }
}