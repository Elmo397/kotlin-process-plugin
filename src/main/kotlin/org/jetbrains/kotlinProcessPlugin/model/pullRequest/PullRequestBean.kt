package org.jetbrains.kotlinProcessPlugin.model.pullRequest

import com.intellij.ide.util.PropertiesComponent

/**
 * @author Mamedova Elnara
 */
class PullRequestBean {
    fun createDefaultDescriptionMessage(): String {
        return "\n\n" + createReviewerSelectionLine() + createFixedIssueIdLine()
    }

    //TODO: try to get users info from YouTrack-rest-api
    fun getReviewers(): MutableList<String> {
        return mutableListOf("elmo")
    }

    private fun createReviewerSelectionLine(): String {
        return "Reviewer \n"
    }

    private fun createFixedIssueIdLine(): String {
        var issue: String? = "No issue"

        val propertiesComponent = PropertiesComponent.getInstance()
        if (propertiesComponent.isValueSet("issueId")) {
            issue = propertiesComponent.getValue("issueId")
        }

        return "#$issue Fixed"
    }
}