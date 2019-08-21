package org.jetbrains.kotlin.process.plugin.issue.model

/**
 * @author Mamedova Elnara
 */
class BranchValidator {
    /**
     * <p>Method that checks the naming of a branch for the presence of prohibited characters.</p>
     * <p>Git sets the following branch naming rules:
     * 1. They can include slash / for hierarchical (directory) grouping, but no slash-separated component
     * can begin with a dot . or end with the sequence .lock.
     * 2. They must contain at least one /. This enforces the presence of a category like heads/, tags/
     * etc. but the actual names are not restricted. If the --allow-onelevel option is used, this rule is waived.
     * 3. They cannot have two consecutive dots .. anywhere.
     * 4. They cannot have ASCII control characters (i.e. bytes whose values are lower than \040, or \177 DEL),
     * space, tilde ~, caret ^, or colon : anywhere.
     * 5. They cannot have question-mark ?, asterisk *, or open bracket [ anywhere. See the --refspec-pattern option
     * below for an exception to this rule.
     * 6. They cannot begin or end with a slash / or contain multiple consecutive slashes (see the --normalize option
     * below for an exception to this rule)
     * 7. They cannot end with a dot.
     * 8. They cannot contain a sequence @{.
     * 9. They cannot be the single character @.
     * 10. They cannot contain a \.
     * {@link https://git-scm.com/docs/git-check-ref-format}
     *
     * @param inputText - part of the name of the branch that the user entered
     * @return false if forbidden characters were found, else return true
     */
    fun isValidBranchNamePart(inputText: String): Boolean {
        return when {
            inputText.contains(Regex(pattern = """(~|:|\\|\^|\?|\*|\[|@\{|,|\s|\.\.+|/\.)""")) -> false
            inputText.endsWith(".lock") -> false
            inputText.endsWith("/") -> false
            inputText.endsWith(".") -> false
            inputText.startsWith("/") -> false
            inputText.startsWith(".") -> false
            inputText.isEmpty() -> false
            else -> true
        }
    }
}