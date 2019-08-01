package org.jetbrains.kotlinProcessPlugin.ui.pullRequest

import com.intellij.openapi.project.Project
import git4idea.DialogManager
import git4idea.repo.GitRemote
import git4idea.repo.GitRepository
import org.jetbrains.plugins.github.GithubCreatePullRequestWorker
import org.jetbrains.plugins.github.api.GithubApiRequestExecutorManager
import org.jetbrains.plugins.github.authentication.accounts.GithubAccount

/**
 * @author Mamedova Elnara
 */
class PullRequestCreator {
    fun openPullRequestDialog(
        project: Project,
        gitRepository: GitRepository,
        remote: GitRemote,
        remoteUrl: String,
        account: GithubAccount
    ) {
        val executor = GithubApiRequestExecutorManager.getInstance().getExecutor(account, project) ?: return

        val worker = GithubCreatePullRequestWorker.create(
            project, gitRepository, remote, remoteUrl,
            executor, account.server
        ) ?: return

        val dialog = PullRequestDialog(project, worker)
        DialogManager.show(dialog)
    }
}