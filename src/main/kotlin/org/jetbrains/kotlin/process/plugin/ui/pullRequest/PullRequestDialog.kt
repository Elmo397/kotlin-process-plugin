package org.jetbrains.kotlin.process.plugin.ui.pullRequest

import com.intellij.CommonBundle
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.ValidationInfo
import com.intellij.openapi.util.Couple
import com.intellij.openapi.util.text.StringUtil
import com.intellij.util.ThreeState
import org.jetbrains.kotlin.process.bot.git.branchName
import org.jetbrains.kotlin.process.bot.git.main
import org.jetbrains.kotlin.process.plugin.model.pullRequest.PullRequestBean
import org.jetbrains.plugins.github.GithubCreatePullRequestWorker
import org.jetbrains.plugins.github.util.GithubNotifications
import org.jetbrains.plugins.github.util.GithubProjectSettings
import org.jetbrains.plugins.github.util.GithubSettings
import java.awt.event.ItemEvent
import javax.swing.JComponent
import org.jetbrains.kotlin.process.plugin.model.pullRequest.Autocomplete


class PullRequestDialog(private var project: Project, private var worker: GithubCreatePullRequestWorker) :
    DialogWrapper(project, true) {

    private val ourDoNotAskOption =
        CreateRemoteDoNotAskOption()
    private var panel: PullRequestPanel =
        PullRequestPanel()
    private val myProjectSettings = GithubProjectSettings.getInstance(project)

    init {
        addDiffButtonActionListener()
        addForkButtonActionListener()
        addForkComboBoxItemListener()
        addBranchComboBoxItemListener()

        panel.setForks(worker.forks)

        val defaultRepo = myProjectSettings.createPullRequestDefaultRepo
        panel.setSelectedFork(defaultRepo)

        val defaultBranch = myProjectSettings.createPullRequestDefaultBranch
        if (defaultBranch != null) {
            panel.setSelectedBranch(defaultBranch)
        }

        title = "Create Pull Request - " + worker.currentBranch
        init()
    }

    fun addAutocompleteToDescription() {
        try {
            val descriptionTextArea = panel.getDescriptionTextArea()
            val reviewersLookup = PullRequestBean().getReviewers()
            val autoComplete =
                Autocomplete(descriptionTextArea, reviewersLookup)

            descriptionTextArea.document.addDocumentListener(autoComplete)
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    override fun doOKAction() {
        val branch = panel.getSelectedBranch()
        if (worker.checkAction(branch)) {
            assert(branch != null)
            worker.createPullRequest(branch!!, getRequestTitle(), getDescription())

            myProjectSettings.setCreatePullRequestDefaultBranch(branch.remoteName)
            myProjectSettings.setCreatePullRequestDefaultRepo(branch.forkInfo.path)

            org.jetbrains.kotlin.process.bot.git.project = project
            branchName = getRequestTitle()

            super.doOKAction()
            main() //TODO: you should not be here!
        }
    }

    override fun createCenterPanel(): JComponent? {
        return panel.getPanel()
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        return panel.getPreferredComponent()
    }

    override fun getHelpId(): String? {
        return "github.create.pull.request.dialog"
    }

    override fun getDimensionServiceKey(): String? {
        return "Github.CreatePullRequestDialog"
    }

    override fun doValidate(): ValidationInfo? {
        return if (StringUtil.isEmptyOrSpaces(getRequestTitle())) {
            ValidationInfo("Title can't be empty'", panel.getTitleTextField())
        } else null
    }

    private fun addDiffButtonActionListener() {
        panel.getShowDiffButton().addActionListener { worker.showDiffDialog(panel.getSelectedBranch()) }
    }

    private fun addForkButtonActionListener() {
        panel.getSelectForkButton().addActionListener {
            val forkInfo = worker.showTargetDialog()

            if (forkInfo != null) {
                panel.setForks(worker.forks)
                panel.setSelectedFork(forkInfo.path)
            }
        }
    }

    private fun addForkComboBoxItemListener(){
        panel.getForkComboBox().addItemListener { e ->
            if (e.stateChange == ItemEvent.DESELECTED) {
                panel.setBranches(emptyList())
            }

            if (e.stateChange == ItemEvent.SELECTED) {
                val fork = e.item as GithubCreatePullRequestWorker.ForkInfo

                panel.setBranches(fork.branches)
                panel.setSelectedBranch(fork.defaultBranch)

                if (fork.remoteName == null && !fork.isProposedToCreateRemote) {
                    fork.isProposedToCreateRemote = true
                    val createRemote = when (GithubSettings.getInstance().createPullRequestCreateRemote) {
                        ThreeState.YES -> true
                        ThreeState.NO -> false
                        ThreeState.UNSURE -> GithubNotifications.showYesNoDialog(
                            project,
                            "Can't Find Remote",
                            "Configure remote for '" + fork.path.user + "'?",
                            ourDoNotAskOption
                        )
                    }

                    if (createRemote) {
                        worker.configureRemote(fork)
                    }
                }

                if (fork.remoteName == null) {
                    panel.setDiffEnabled(false)
                } else {
                    panel.setDiffEnabled(true)
                    worker.launchFetchRemote(fork)
                }
            }
        }
    }

    private fun addBranchComboBoxItemListener() {
        panel.getBranchComboBox().addItemListener { e ->
            if (e.stateChange == ItemEvent.SELECTED) {
                val branch = e.item as GithubCreatePullRequestWorker.BranchInfo

                if (branch.forkInfo.remoteName != null) {
                    if (branch.diffInfoTask != null && branch.diffInfoTask!!.isDone && branch.diffInfoTask!!.safeGet() == null) {
                        panel.setDiffEnabled(false)
                    } else {
                        panel.setDiffEnabled(true)
                    }
                }

                if (panel.isTitleDescriptionEmptyOrNotModified()) {
                    val descriptionMsg = getDefaultDescriptionMessage()
                    val description = Couple.of(worker.currentBranch, descriptionMsg)

                    panel.setTitle(description.getFirst())
                    panel.setDescription(description.getSecond())
                }

                worker.launchLoadDiffInfo(branch)
            }
        }
    }

    private fun getDefaultDescriptionMessage(): String {
        return PullRequestBean().createDefaultDescriptionMessage()
    }

    private fun getRequestTitle(): String {
        return panel.getTitle()
    }

    private fun getDescription(): String {
        return panel.getDescription()
    }

    class CreateRemoteDoNotAskOption : DoNotAskOption {
        override fun isToBeShown(): Boolean {
            return true
        }

        override fun setToBeShown(value: Boolean, exitCode: Int) {
            when {
                value -> GithubSettings.getInstance().createPullRequestCreateRemote = ThreeState.UNSURE
                exitCode == OK_EXIT_CODE -> GithubSettings.getInstance().createPullRequestCreateRemote =
                    ThreeState.YES
                else -> GithubSettings.getInstance().createPullRequestCreateRemote = ThreeState.NO
            }
        }

        override fun canBeHidden(): Boolean {
            return true
        }

        override fun shouldSaveOptionsOnCancel(): Boolean {
            return false
        }

        override fun getDoNotShowMessage(): String {
            return CommonBundle.message("dialog.options.do.not.ask")
        }
    }
}
