package org.jetbrains.kotlin.process.plugin.model.merge

import com.intellij.dvcs.repo.VcsRepositoryManager
import git4idea.branch.GitBrancher
import git4idea.commands.Git
import git4idea.repo.GitRepositoryManager
import org.jetbrains.kotlin.process.bot.git.branchName
import org.jetbrains.kotlin.process.bot.git.project

fun merge() {
    val vcsRepoManager = VcsRepositoryManager.getInstance(project)
    val brancher = GitBrancher.getInstance(project)
    val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

    brancher.merge(branchName, GitBrancher.DeleteOnMergeOption.NOTHING, repositories)
//            brancher.rebase(repositories, branchName)

    //TODO: Are you really merging??
    val git = Git.getInstance()
    repositories.forEach { repo ->
        repo.remotes.forEach { remote ->
            remote.pushUrls.forEach { url ->
                val result = git.push(repo, remote.name, url, repo.currentBranch!!.fullName, true)
            }
        }
    }
}