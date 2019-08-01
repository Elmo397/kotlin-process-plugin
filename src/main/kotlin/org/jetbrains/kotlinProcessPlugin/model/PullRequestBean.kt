package org.jetbrains.kotlinProcessPlugin.model

import com.intellij.ide.util.PropertiesComponent

/**
 * @author Mamedova Elnara
 */
class PullRequestBean {
    //TODO: add autocomplete for reviewers
    fun createDefaultDescriptionMessage(): String {
        return "\n\n" + createReviewerSelectionLine() + createFixedIssueIdLine()
    }

    private fun createReviewerSelectionLine(): String {
        return "Reviewer \n"
    }

    private fun getReviewers() {

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