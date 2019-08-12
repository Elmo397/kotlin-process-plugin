package org.jetbrains.kotlin.process.plugin.ui.rr

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.kotlin.process.bot.rr.buildMessages
import org.jetbrains.kotlin.process.plugin.ui.pullRequest.PullRequestCreator
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextArea

class WarningPanel(
    canBeParent: Boolean,
    private val project: Project,
    private val repository: GitRepository,
    private val remote: GitRemote,
    private val remoteUrl: String,
    private val account: GithubAccount
) : DialogWrapper(canBeParent) {
    init {
        title = "Warning"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val mergePanel = JPanel()
        val label = JLabel("Some builds in teamcity was failed. Are you sure want creating pull request?")
        val messagesField = JTextArea(15, 70)

        buildMessages.forEach { message ->
            messagesField.text += "$message\n"
        }

        mergePanel.add(label)
        mergePanel.add(messagesField)

        return mergePanel
    }

    override fun doOKAction() {
        PullRequestCreator().openPullRequestDialog(project, repository, remote, remoteUrl, account)

        super.doOKAction()
    }

}