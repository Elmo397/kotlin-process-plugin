package org.jetbrains.kotlin.process.plugin.issue.model

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager
import org.jetbrains.kotlin.process.plugin.issue.ui.noBranchMsg
import javax.swing.AbstractListModel

class BranchListModel(private val project: Project) : AbstractListModel<String>() {
    override fun getElementAt(index: Int): String {
        val vcsRepoManager = VcsRepositoryManager.getInstance(project)
        val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

        val branches: MutableList<String> = mutableListOf()
        repositories.forEach { repo ->
            repo.branches.localBranches
                .filter { branch -> !branch.fullName.contains("master") }
                .forEach { branch ->
                    branches.add(branch.name)
                }
        }

        return when {
            branches.isNotEmpty() && index < branches.size -> branches[index]
            else -> noBranchMsg
        }
    }

    override fun getSize() = when {
        project.isDisposed -> 0
        else -> {
            val vcsRepoManager = VcsRepositoryManager.getInstance(project)
            val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

            var size = 0
            repositories.forEach { repo ->
                val localBranchesCount = repo.branches.localBranches.size - 1
                size = if (size > localBranchesCount) size else localBranchesCount
            }

            size
        }
    }
}