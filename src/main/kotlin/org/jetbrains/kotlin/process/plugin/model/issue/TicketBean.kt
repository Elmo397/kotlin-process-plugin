package org.jetbrains.kotlin.process.plugin.model.issue

import com.github.jk1.ytplugin.YouTrackPluginApiComponent
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

/**
 * @author Mamedova Elnara
 */
class TicketBean {
    fun getIssues(issueId: String, project: Project) {
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

    //TODO: why it doesn't work?! ¯\_(ツ)_/¯
    fun setIssueInProgress(issueId: String, urlIssueMap: MutableMap<String, String>, project: Project): Boolean {
        return try {
            val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()

            var result = false
            repositories
                .filter { repository -> urlIssueMap[issueId]!!.startsWith(repository.url) }
                .forEach { repository ->
                    val youTrack = YouTrackPluginApiComponent(project)

                    val issue = IssuesRestClient(repository).getIssue(issueId)
                    result = youTrack.executeCommand(issue!!, "State In Progress").isSuccessful
                }

            result
        } catch (e: Throwable) {
            println(e.stackTrace)
            false
        }
    }

    fun showDescription(
        issueIdField: ComboBox<String>?,
        urlIssueMap: MutableMap<String, String>,
        project: Project
    ): String? {
        return try {
            val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()
            val selectedIssueId = issueIdField?.selectedItem.toString()

            var description: String? = ""
            repositories
                .filter { repository -> urlIssueMap[selectedIssueId]!!.startsWith(repository.url) }
                .forEach { repository ->
                    val html = IssuesRestClient(repository).getIssue(selectedIssueId)?.issueDescription
                    val doc = Jsoup.parse(html)

                    description = doc.text()
                    description = description!!.replace(". ", ".\n")
                }

            description
        } catch (e: Throwable) {
            null
        }
    }

    fun showShortDescription(
        issueIdField: ComboBox<String>?,
        urlIssueMap: MutableMap<String, String>,
        project: Project
    ): String? {
        return try {
            val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()
            val selectedIssueId = issueIdField?.selectedItem.toString()

            var summary: String? = ""
            repositories
                .filter { repository -> urlIssueMap[selectedIssueId]!!.startsWith(repository.url) }
                .forEach { repository ->
                    summary = IssuesRestClient(repository).getIssue(selectedIssueId)?.summary
                }

            createShortDescriptionForBranch(summary)
        } catch (e: Throwable) {
            null
        }
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
}