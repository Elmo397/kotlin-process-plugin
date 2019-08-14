package org.jetbrains.kotlin.process.plugin.ui.rr

import com.intellij.icons.AllIcons
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.ui.components.JBScrollPane
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.kotlin.process.bot.rr.buildMessages
import org.jetbrains.kotlin.process.plugin.ui.pullRequest.PullRequestCreator
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount
import java.awt.BorderLayout
import java.awt.Color
import javax.swing.*

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
//        label.foreground = Color.red
        label.icon = UIManager.getIcon("OptionPane.warningIcon")

/*        val messagesField = JTextArea(15, 70)
        buildMessages.forEach { message ->
            messagesField.text += "$message\n\n"
        }

        val scrollPane = JBScrollPane(
            messagesField,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )*/

        val mergePanel = JPanel(BorderLayout())
        mergePanel.add(label)
//        mergePanel.add(scrollPane, BorderLayout.AFTER_LAST_LINE)

        return mergePanel
    }

    override fun doOKAction() {
        super.doOKAction()
        PullRequestCreator().openPullRequestDialog(project, repository, remote, remoteUrl, account)
    }
}