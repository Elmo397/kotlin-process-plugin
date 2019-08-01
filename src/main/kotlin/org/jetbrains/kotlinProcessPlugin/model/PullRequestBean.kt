package org.jetbrains.kotlinProcessPlugin.model

/**
 * @author Mamedova Elnara
 */
class PullRequestBean {
    //TODO: add issue id selected when creating a branch
    //TODO: add autocomplete for reviewers
    fun createDefaultDescriptionMessage(): String {
        val message = "\n" +
                "\n" +
                "Reviewer [выпадающий список]\n" +
                "#<Issue> Fixed\t"

        return message
    }
}