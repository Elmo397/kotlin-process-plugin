package org.jetbrains.kotlin.process.plugin.issue.model

import com.github.jk1.ytplugin.ComponentAware
import com.github.jk1.ytplugin.tasks.YouTrackServer
import com.intellij.dvcs.repo.VcsRepositoryManager
import com.intellij.openapi.project.Project
import com.intellij.ui.ListSpeedSearch
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBLoadingPanel
import git4idea.repo.GitRepositoryManager
import java.awt.BorderLayout
import java.awt.Dimension
import java.awt.event.ActionListener
import javax.swing.*

const val noBranchMsg = "No branches is created"

class IssueList(override val project: Project, allBranchesContent: JPanel) : JBLoadingPanel(BorderLayout(), project),
    ComponentAware {
    private val issueList: JBList<String> = JBList()
    private val branchListModel: BranchListModel = BranchListModel()
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

        renderer = IssueListCellRenderer({ issueListScrollPane.viewport.width }, project)
        issueList.cellRenderer = renderer
        add(issueListScrollPane, BorderLayout.CENTER)
        initIssueListModel()
        ListSpeedSearch(issueList)
    }

    private fun initIssueListModel() {
        issueList.emptyText.clear()
        issueList.model = branchListModel
    }

    override fun registerKeyboardAction(action: ActionListener, keyStroke: KeyStroke, condition: Int) {
        issueList.registerKeyboardAction(action, keyStroke, condition)
    }

    inner class BranchListModel : AbstractListModel<String>() {
        override fun getElementAt(index: Int): String {
            val vcsRepoManager = VcsRepositoryManager.getInstance(project)
            val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

            val branches: MutableList<String> = mutableListOf()
            repositories.forEach { repo ->
                repo.branches.localBranches
                    .filter { branch -> !branch.fullName.contains("master") }
                    .forEach { branch ->
                        branches.add(branch.name)
                    }
            }

            return when {
                branches.isNotEmpty() && index < branches.size -> branches[index]
                else -> noBranchMsg
            }
        }

        override fun getSize() = when {
            project.isDisposed -> 0
            else -> {
                val vcsRepoManager = VcsRepositoryManager.getInstance(project)
                val repositories = GitRepositoryManager(project, vcsRepoManager).repositories

                var size = 0
                repositories.forEach { repo ->
                    val localBranchesCount = repo.branches.localBranches.size - 1
                    size = if (size > localBranchesCount) size else localBranchesCount
                }

                size
            }
        }
    }
}