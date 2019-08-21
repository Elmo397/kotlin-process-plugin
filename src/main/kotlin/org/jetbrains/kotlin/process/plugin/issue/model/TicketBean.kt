package org.jetbrains.kotlin.process.plugin.issue.model

import com.github.jk1.ytplugin.YouTrackPluginApiComponent
import com.github.jk1.ytplugin.issues.model.Issue
import com.github.jk1.ytplugin.rest.IssuesRestClient
import com.github.jk1.ytplugin.tasks.TaskManagerProxyComponent
import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepository
import git4idea.repo.GitRepositoryManager
import org.jsoup.Jsoup
import java.util.*

fun openIssueInYouTrackPlugin(issueId: String, project: Project) {
    try {
        val youTrack = YouTrackPluginApiComponent(project)
        youTrack.openIssueInToolWidow(issueId)

    } catch (e: Throwable) {
        println(e.message)
    }
}

fun createBranch(issueId: String, devNick: String, shortDescription: String, project: Project) {
    try {
        val vcsRepoManager = VcsRepositoryManager.getInstance(project)
        val brancher = GitBrancher.getInstance(project)
        val repositories = GitRepositoryManager(project, vcsRepoManager).repositories
        val repoMap = linkedMapOf<GitRepository, String>()

        repositories.forEach { repo ->
            repoMap[repo] = "master"
        }

        val branchName = "rr/$devNick/$issueId/$shortDescription"
        brancher.createBranch(branchName, repoMap)
        brancher.checkout(branchName, false, repositories, null)

        saveBranchName(branchName)
    } catch (e: Throwable) {
        println(e.message)
    }
}

fun showDescription(
    issueIdField: ComboBox<String>?,
    project: Project
): String? {
    return try {
        val selectedIssueId = issueIdField?.selectedItem.toString()
        val issue = getIssue(selectedIssueId, project)!!

        Jsoup.parse(issue.description).text().replace(". ", ".\n") //todo: render html instead
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

fun showShortDescription(
    issueIdField: ComboBox<String>?,
    project: Project
): String? {
    return try {
        val selectedIssueId = issueIdField?.selectedItem.toString()
        val summary = getIssue(selectedIssueId, project)!!.summary

        createShortDescriptionForBranch(summary)
    } catch (e: Throwable) {
        e.printStackTrace()
        null
    }
}

private fun getIssue(issueId: String, project: Project): Issue? {
    val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()
    repositories
        .filter { repository -> urlIssueMap[issueId]!!.startsWith(repository.url) }
        .forEach { repository ->
            return IssuesRestClient(repository).getIssue(issueId)
        }

    return null
}

private fun createShortDescriptionForBranch(summary: String?): String? {
    var shortDescription: String? = ""

    val tokenizer = StringTokenizer(summary, " ,\"`'*^:;[]{}")
    var tokenCount = 0
    while (tokenizer.hasMoreTokens() && tokenCount < 10) {
        shortDescription += tokenizer.nextToken() + "."

        tokenCount++
    }

    val lastSymbol = shortDescription!!.length - 1
    shortDescription = shortDescription.substring(0, lastSymbol)

    return shortDescription
}

private fun saveBranchName(branchName: String) {
    PropertiesComponent.getInstance().setValue("branchName", branchName)
}
