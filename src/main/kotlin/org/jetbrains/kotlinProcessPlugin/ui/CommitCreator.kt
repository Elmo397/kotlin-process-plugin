package org.jetbrains.kotlinProcessPlugin.ui

import com.intellij.openapi.project.Project
import git4idea.GitUtil

/**
 * @author Mamedova Elnara
 */
class CommitCreator {
    fun openPullRequestDialog(project: Project) {
        val gitRepository = GitUtil.getRepositoryManager(project).getRepositoryForFile(project.projectFile!!)
        val gitRemote = gitRepository!!.remotes
        "stop here"
    }
}