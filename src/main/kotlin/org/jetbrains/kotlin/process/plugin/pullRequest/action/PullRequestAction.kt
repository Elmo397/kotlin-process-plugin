package org.jetbrains.kotlin.process.plugin.pullRequest.action

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.kotlin.process.plugin.merge.model.gitMerge
import org.jetbrains.kotlin.process.plugin.rr.model.success
import org.jetbrains.kotlin.process.plugin.merge.model.projectForMergeAction
import org.jetbrains.kotlin.process.plugin.pullRequest.ui.PullRequestCreator
import org.jetbrains.kotlin.process.plugin.rr.ui.WarningPanel
import org.jetbrains.plugins.github.AbstractGithubUrlGroupingAction
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount

/**
 * @author Mamedova Elnara
 */
class PullRequestAction : AbstractGithubUrlGroupingAction(
    "Pull Request",
    "Create pull request from current branch",
    AllIcons.Vcs.Merge
) {
    override fun actionPerformed(
        e: AnActionEvent,
        project: Project,
        repository: GitRepository,
        remote: GitRemote,
        remoteUrl: String,
        account: GithubAccount
    ) {
        projectForMergeAction = project

        if (!success) {
            WarningPanel(
                false,
                project,
                repository,
                remote,
                remoteUrl,
                account
            ).show()
        } else {
            PullRequestCreator()
                .openPullRequestDialog(project, repository, remote, remoteUrl, account)
        }

        gitMerge()
    }
}