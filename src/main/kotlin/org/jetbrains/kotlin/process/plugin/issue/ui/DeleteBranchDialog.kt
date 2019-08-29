package org.jetbrains.kotlin.process.plugin.issue.ui

import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import git4idea.branch.GitBrancher
import git4idea.repo.GitRepositoryManager
import org.jetbrains.kotlin.process.plugin.issue.model.getIssueOnBranch
import java.awt.BorderLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.UIManager

class DeleteBranchDialog(canBeParent: Boolean, private val project: Project, private val branch: String) :
    DialogWrapper(canBeParent) {

    init {
        title = "Delete Branch"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        return when {
            getIssueOnBranch(branch, project)!!.resolved -> {
                val panel = JPanel()
                panel.add(JLabel("<html>Delete branch <b>$branch</b>?</html>"))

                panel
            }
            else -> {
                title = "Warning"

                val text = "<html>" +
                        "Issue from branch <b>$branch</b> doesn't resolved.<br/>" +
                        "Are you sure want delete this branch?" +
                        "</html>"
                val icon = UIManager.getIcon("OptionPane.warningIcon")
                val horizontalAlignment = 10

                val label = JLabel(text, icon, horizontalAlignment)
                val panel = JPanel(BorderLayout())
                panel.add(label, BorderLayout.NORTH)

                panel
            }
        }
    }

    override fun doOKAction() {
        val vcsRepoManager = VcsRepositoryManager.getInstance(project)
        val brancher = GitBrancher.getInstance(project)
        val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

        brancher.deleteBranch(branch, repositories)

        super.doOKAction()
    }
}