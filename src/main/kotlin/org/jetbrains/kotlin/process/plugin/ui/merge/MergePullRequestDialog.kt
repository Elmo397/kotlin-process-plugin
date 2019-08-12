package org.jetbrains.kotlin.process.plugin.ui.merge

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.ui.DialogWrapper
import git4idea.branch.GitBrancher
import git4idea.commands.Git
import git4idea.repo.GitRepositoryManager
import org.jetbrains.kotlin.process.bot.git.branchName
import org.jetbrains.kotlin.process.bot.git.main
import org.jetbrains.kotlin.process.bot.git.project
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MergePullRequestDialog(canBeParent: Boolean) : DialogWrapper(canBeParent) {
    init {
        title = "Merge pull request"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val mergePanel = JPanel()
        mergePanel.add(JLabel("Merge?!"))

        return mergePanel
    }

    override fun doOKAction() {
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

        super.doOKAction()
    }
}