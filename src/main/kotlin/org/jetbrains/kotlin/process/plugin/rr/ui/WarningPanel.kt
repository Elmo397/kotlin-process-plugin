package org.jetbrains.kotlin.process.plugin.rr.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.kotlin.process.plugin.pullRequest.ui.PullRequestCreator
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.UIManager

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
        val label = JLabel("Some builds in teamcity was failed. Are you sure want creating pull request?")
        label.icon = UIManager.getIcon("OptionPane.warningIcon")

        val mergePanel = JPanel(BorderLayout())
        mergePanel.add(label)

        return mergePanel
    }

    override fun doOKAction() {
        super.doOKAction()
        PullRequestCreator()
            .openPullRequestDialog(project, repository, remote, remoteUrl, account)
    }
}