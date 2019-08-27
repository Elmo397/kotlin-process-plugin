package org.jetbrains.kotlin.process.plugin.issue.ui

import com.github.jk1.ytplugin.ComponentAware
import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import com.intellij.ui.ListSpeedSearch
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBLoadingPanel
import git4idea.repo.GitRepositoryManager
import org.jetbrains.kotlin.process.plugin.issue.model.BranchListModel
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.*

const val noBranchMsg = "No branches is created"

class IssueList(override val project: Project, allBranchesContent: JPanel) : JBLoadingPanel(BorderLayout(), project),
    ComponentAware {
    private val issueList: JBList<String> = JBList()
    private val branchListModel: BranchListModel = BranchListModel(project)
    private val renderer: IssueListCellRenderer

    init {
        val issueListScrollPane = object : JScrollPane(
            issueList,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ) {
            override fun getPreferredSize(): Dimension {
                val preferredSize = super.getPreferredSize()
                preferredSize.width = allBranchesContent.width - 40
                preferredSize.height = allBranchesContent.height
                return preferredSize
            }
        }

        renderer = IssueListCellRenderer(
            { issueListScrollPane.viewport.width },
            project
        )
        issueList.cellRenderer = renderer
        add(issueListScrollPane, BorderLayout.CENTER)
        initIssueListModel()
        ListSpeedSearch(issueList)
    }

    fun getSelectedBranch() = when {
        issueList.selectedIndex == -1 -> null
        issueList.selectedIndex >= branchListModel.size -> null
        else -> branchListModel.getElementAt(issueList.selectedIndex)
    }

    fun update() = branchListModel.update()

    private fun initIssueListModel() {
        issueList.emptyText.clear()
        issueList.model = branchListModel
    }

    override fun registerKeyboardAction(action: ActionListener, keyStroke: KeyStroke, condition: Int) {
        issueList.registerKeyboardAction(action, keyStroke, condition)
    }
}