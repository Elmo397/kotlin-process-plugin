package org.jetbrains.kotlinProcessPlugin.ui.pullRequest

import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.text.StringUtil
import com.intellij.ui.DocumentAdapter
import com.intellij.ui.SortedComboBoxModel
import org.jetbrains.annotations.NotNull
import org.jetbrains.plugins.github.GithubCreatePullRequestWorker
import org.jetbrains.plugins.github.api.GithubFullPath
import javax.swing.*
import javax.swing.event.DocumentEvent

class PullRequestPanel {
    private var myForkModel: SortedComboBoxModel<GithubCreatePullRequestWorker.ForkInfo>
    private var myBranchModel: SortedComboBoxModel<GithubCreatePullRequestWorker.BranchInfo>
    private lateinit var baseBranchComboBox: ComboBox<GithubCreatePullRequestWorker.BranchInfo>
    private lateinit var baseForkComboBox: ComboBox<GithubCreatePullRequestWorker.ForkInfo>
    private lateinit var titleTextField: JTextField
    private lateinit var descriptionTextArea: JTextArea
    private lateinit var pullRequestPanel: JPanel
    private lateinit var showDiffBtn: JButton
    private lateinit var selectOtherForkBtn: JButton
    private lateinit var myForkLabel: JLabel

    private var myTitleDescriptionUserModified = false

    init {
        descriptionTextArea.border = BorderFactory.createEtchedBorder()
        descriptionTextArea.focusTraversalKeysEnabled = false

        myBranchModel = SortedComboBoxModel { o1, o2 -> StringUtil.naturalCompare(o1.remoteName, o2.remoteName) }
        baseBranchComboBox.model = myBranchModel

        myForkModel = SortedComboBoxModel { o1, o2 -> StringUtil.naturalCompare(o1.path.user, o2.path.user) }
        baseForkComboBox.model = myForkModel

        val userModifiedDocumentListener = object : DocumentAdapter() {
            override fun textChanged(e: DocumentEvent) {
                myTitleDescriptionUserModified = true
            }
        }
        titleTextField.document.addDocumentListener(userModifiedDocumentListener)
        descriptionTextArea.document.addDocumentListener(userModifiedDocumentListener)
    }

    @NotNull
    fun getTitle(): String {
        return titleTextField.text
    }

    @NotNull
    fun getDescription(): String {
        return descriptionTextArea.text
    }

    @NotNull
    fun getSelectedFork(): GithubCreatePullRequestWorker.ForkInfo? {
        return myForkModel.selectedItem
    }

    @NotNull
    fun getSelectedBranch(): GithubCreatePullRequestWorker.BranchInfo? {
        return myBranchModel.selectedItem
    }

    fun setSelectedFork(path: GithubFullPath?) {
        if (path != null) {
            for (info in myForkModel.items) {
                if (path == info.path) {
                    myForkModel.selectedItem = info
                    return
                }
            }
        }

        if (myForkModel.size > 0) myForkModel.selectedItem = myForkModel.get(0)
    }

    fun setSelectedBranch(branch: String?) {
        if (branch != null) {
            for (info in myBranchModel.items) {
                if (branch == info.remoteName) {
                    myBranchModel.selectedItem = info
                    return
                }
            }
        }

        if (myBranchModel.size > 0) myBranchModel.selectedItem = myBranchModel.get(0)
    }

    fun setForks(forks: Collection<GithubCreatePullRequestWorker.ForkInfo>) {
        myForkModel.selectedItem = null
        myForkModel.setAll(forks)
    }

    fun setBranches(branches: Collection<GithubCreatePullRequestWorker.BranchInfo>) {
        myBranchModel.selectedItem = null
        myBranchModel.setAll(branches)
    }

    fun setTitle(title: String?) {
        titleTextField.text = title
        myTitleDescriptionUserModified = false
    }

    fun setDescription(title: String?) {
        descriptionTextArea.text = title
        myTitleDescriptionUserModified = false
    }

    fun setDiffEnabled(enabled: Boolean) {
        showDiffBtn.isEnabled = enabled
    }

    fun getPanel(): JPanel? {
        return pullRequestPanel
    }

    @NotNull
    fun getDescriptionTextArea(): JTextArea {
        return descriptionTextArea
    }

    @NotNull
    fun getTitleTextField(): JTextField {
        return titleTextField
    }

    @NotNull
    fun getSelectForkButton(): JButton {
        return selectOtherForkBtn
    }

    @NotNull
    fun getShowDiffButton(): JButton {
        return showDiffBtn
    }

    @NotNull
    fun getForkComboBox(): ComboBox<*> {
        return baseForkComboBox
    }

    @NotNull
    fun getBranchComboBox(): ComboBox<*> {
        return baseBranchComboBox
    }

    @NotNull
    fun getPreferredComponent(): JTextArea {
        return descriptionTextArea
    }

    fun isTitleDescriptionEmptyOrNotModified(): Boolean {
        return !myTitleDescriptionUserModified || StringUtil.isEmptyOrSpaces(titleTextField.text) && StringUtil.isEmptyOrSpaces(
            descriptionTextArea.text
        )
    }
}
