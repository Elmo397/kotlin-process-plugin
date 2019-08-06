package org.jetbrains.kotlinProcessPlugin.model.pullRequest

import com.intellij.ide.util.PropertiesComponent
import java.util.function.Function
import java.util.stream.Collectors

/**
 * @author Mamedova Elnara
 */
class PullRequestBean {
    private var issue: String? = getIssue()

    fun createDefaultDescriptionMessage(): String {
        return "\n\nReviewer \n#$issue Fixed"
    }

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

    fun addReviewerNameToMsg(name: String): String {
        return "\nReviewer $name\n#$issue Fixed"
    }

    private fun getIssue(): String? {
        val propertiesComponent = PropertiesComponent.getInstance()
        return if (propertiesComponent.isValueSet("issueId")) {
            propertiesComponent.getValue("issueId")
        } else {
            "No issue"
        }
    }
}