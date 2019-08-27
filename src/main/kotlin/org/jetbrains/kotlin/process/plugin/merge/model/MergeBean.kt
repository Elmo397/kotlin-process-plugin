package org.jetbrains.kotlin.process.plugin.merge.model

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import git4idea.repo.GitRepositoryManager
import org.jetbrains.kotlin.process.plugin.issue.model.getIssueOnBranch

fun getBranchOfFixedIssue(project: Project): List<String> {
    val vcsRepoManager = VcsRepositoryManager.getInstance(project)
    val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

    val branches: MutableList<String> = mutableListOf()
    repositories.forEach { repo ->
        repo.branches.localBranches
            .filter { branch -> !branch.fullName.contains("master") }
            .filter { branch -> getIssueOnBranch(branch.name, project)!!.resolved }
            .forEach { branch ->
                branches.add(branch.name)
            }
    }

    return branches
}