package org.jetbrains.kotlin.process.plugin.finalCommit.model

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import git4idea.commands.Git
import git4idea.commands.GitCommand
import git4idea.commands.GitLineHandler
import org.jetbrains.kotlin.process.plugin.issue.model.changeIssueState
import org.jetbrains.kotlin.process.plugin.issue.model.getIssueOnBranch
import org.jetbrains.kotlin.process.plugin.issue.model.showStateChangeResultBanner
import java.io.File
import java.util.function.Function
import java.util.stream.Collectors

private var issue: String? = "No issue is selected"

fun setIssueId(branch: String) {
    issue = branch.split("/")[2]
}

fun createDefaultDescriptionMessage() = "\n\nReviewer \n#$issue Fixed"

//TODO: Where I can get user info?
fun getReviewers(): Function<String, List<String>> {
    val reviewers = mutableListOf(
        "Alexander Podkhalyuzin",
        "Vladimir Dolzhenko",
        "Igor Yakovlev",
        "Dmitry Gridin",
        "Ilya Kirillov",
        "Nicolay Mitropolsky",
        "Nicolay Mitropolsky",
        "Natalia Selezneva",
        "Pavel Talanov",
        "Nikolay Krasko"
    )
    reviewers.sort()

    return Function { text ->
        reviewers.stream()
            .filter { reviewer ->
                text.isNotEmpty() &&
                        reviewer.toLowerCase().contains(text.toLowerCase()) &&
                        reviewer != text
            }
            .collect(Collectors.toList())
    }
}

fun addReviewerNameToMsg(name: String) = "\nReviewer $name\n#$issue Fixed"

fun changeStateToFixed(branch: String, project: Project, dialog: DialogWrapper) {
    val issue = getIssueOnBranch(branch, project)

    val commandResult = changeIssueState(issue!!.id, project, "State Fixed")
    showStateChangeResultBanner(
        commandResult,
        dialog.contentPanel,
        "Fixed"
    )
}

fun gitAdd(project: Project) {
    try {
        val git = Git.getInstance()
        val basePath = project.basePath!!
        val handler = GitLineHandler(project, File(basePath), GitCommand.ADD)

        handler.addParameters("--all")
        handler.endOptions()
        val result = git.runCommand(handler)
        result.throwOnError()
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}

fun gitCommit(project: Project, commitMessage: String) {
    try {
        val git = Git.getInstance()
        val basePath = project.basePath!!
        val handler = GitLineHandler(project, File(basePath), GitCommand.COMMIT)

        handler.addParameters("-m $commitMessage")
        handler.endOptions()
        val result = git.runCommand(handler)
        result.throwOnError()

        println(result.success())
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}