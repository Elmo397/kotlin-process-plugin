package org.jetbrains.kotlin.process.plugin

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.actionSystem.ActionManager
import com.intellij.openapi.actionSystem.ActionPlaces
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.content.ContentManager
import org.jetbrains.kotlin.process.plugin.finalCommit.action.FinalCommitAction
import org.jetbrains.kotlin.process.plugin.issue.action.IssueAction
import org.jetbrains.kotlin.process.plugin.issue.ui.IssueList
import org.jetbrains.kotlin.process.plugin.merge.action.MergeAction
import org.jetbrains.kotlin.process.plugin.rr.action.RemoteRunSettingsAction
import org.jetbrains.kotlin.process.plugin.rr.action.RemoteRunStartAction
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildState
import org.jetbrains.teamcity.rest.BuildStatus
import java.awt.BorderLayout
import java.awt.Dimension
import javax.swing.*

val messagesField = JTextArea("")
lateinit var issueList: IssueList

fun writeFoundedBuild(build: Build) {
    messagesField.text += when (build.state) {
        BuildState.RUNNING -> "\nFound running build ${build.id.stringId} (branch ${build.branch.name})\n"
        BuildState.FINISHED -> "\nFound completed build ${build.id.stringId} (branch ${build.branch.name})\n"
        BuildState.DELETED -> "\nFound deleted build ${build.id.stringId} (branch ${build.branch.name})\n"
        BuildState.QUEUED -> "\nFound queued build ${build.id.stringId} (branch ${build.branch.name})\n"
        else -> "\nFound unknown build ${build.id.stringId} (branch ${build.branch.name})\n"
    }
}

fun writeMessage(build: Build, branchName: String) {
    if (build.state == BuildState.RUNNING) {
        messagesField.text += "Build for branch $branchName running.\n " +
                "Status: ${build.statusText}\n " +
                "Link: ${build.getHomeUrl()}\n\n"
    } else if (build.status == BuildStatus.FAILURE || build.status == BuildStatus.ERROR) {
        messagesField.text += "Build for branch $branchName just finished.\n " +
                "Status: ${build.statusText}\n " +
                "Link: ${build.getHomeUrl()}\n\n"
    } else {
        messagesField.text += "Build for branch $branchName.\n " +
                "Status: ${build.statusText}\n " +
                "Link: ${build.getHomeUrl()}\n\n"
    }
}

class KotlinProcessToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        toolWindow.contentManager.addContent("Branches", createAllBranchesContent(project), false)
        toolWindow.contentManager.addContent("Remote Run Log", createRrLogContent(), false)
    }

    //TODO: listen creation branches and update branch list
    private fun createAllBranchesContent(project: Project): JComponent {
        val allBranchesContent = JPanel()
        issueList = IssueList(project, allBranchesContent)

        allBranchesContent.add(createBranchActionToolbar(), BorderLayout.WEST)
        allBranchesContent.add(issueList, BorderLayout.CENTER)

        return allBranchesContent
    }

    private fun createBranchActionToolbar(): JComponent {
        val selectedBranch = { issueList.getSelectedBranch() }
        val group = DefaultActionGroup()

        group.add(IssueAction())
        group.add(RemoteRunStartAction())
        group.add(RemoteRunSettingsAction())
        group.add(FinalCommitAction(selectedBranch))
        group.add(MergeAction())

        return createVerticalToolbarComponent(group)
    }

    private fun createRrLogContent(): JComponent {
        val rrLogContent = JPanel()
        rrLogContent.add(createRrActionsToolbar(), BorderLayout.WEST)
        rrLogContent.add(createTextPanel(rrLogContent, messagesField), BorderLayout.CENTER)

        return rrLogContent
    }

    private fun ContentManager.addContent(title: String, component: JComponent, isCloseable: Boolean) {
        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(component, title, false)
        content.isCloseable = isCloseable
        addContent(content)
    }

    private fun createRrActionsToolbar(): JComponent {
        val group = DefaultActionGroup()

        group.add(IssueAction())
        group.add(RemoteRunStartAction())
        group.add(RemoteRunSettingsAction())
        group.add(MergeAction())

        return createVerticalToolbarComponent(group)
    }

    private fun createVerticalToolbarComponent(group: DefaultActionGroup) = createToolbarComponent(false, group)

    private fun createHorizontalToolbarComponent(group: DefaultActionGroup) = createToolbarComponent(true, group)

    private fun createToolbarComponent(horizontal: Boolean, group: DefaultActionGroup): JComponent =
        ActionManager.getInstance()
            .createActionToolbar(ActionPlaces.TOOLWINDOW_TITLE, group, horizontal)
            .component

    private fun createTextPanel(textPanel: JPanel, textArea: JTextArea): JComponent {
        textArea.isEditable = false

        return object : JScrollPane(
            textArea,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ) {
            override fun getPreferredSize(): Dimension {
                val preferredSize = super.getPreferredSize()
                preferredSize.width = textPanel.width - 40
                preferredSize.height = textPanel.height
                return preferredSize
            }
        }
    }
}