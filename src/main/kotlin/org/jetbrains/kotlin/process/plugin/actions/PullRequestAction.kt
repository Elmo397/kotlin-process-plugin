package org.jetbrains.kotlin.process.plugin.actions

import com.intellij.icons.AllIcons
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.project.Project
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.kotlin.process.plugin.ui.pullRequest.PullRequestCreator
import org.jetbrains.plugins.github.AbstractGithubUrlGroupingAction
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount

/**
 * @author Mamedova Elnara
 */ //TODO: make it visible in KotlinProcessGroup
class PullRequestAction : AbstractGithubUrlGroupingAction(
    "Pull Request",
    "Create pull request from current branch",
    AllIcons.Vcs.Vendors.Github
) {
    override fun actionPerformed(
        e: AnActionEvent,
        project: Project,
        repository: GitRepository,
        remote: GitRemote,
        remoteUrl: String,
        account: GithubAccount
    ) {
        PullRequestCreator().openPullRequestDialog(project, repository, remote, remoteUrl, account)
    }
}