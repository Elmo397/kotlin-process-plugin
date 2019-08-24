package org.jetbrains.kotlin.process.plugin.finalCommit.model

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.issue.model.changeIssueState
import org.jetbrains.kotlin.process.plugin.issue.model.getIssueOnBranch
import org.jetbrains.kotlin.process.plugin.issue.model.showStateChangeResultBanner
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