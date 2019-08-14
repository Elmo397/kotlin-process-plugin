package org.jetbrains.kotlin.process.plugin.ui.rr

import com.intellij.find.impl.FindPopupPanel.createToolbar
import com.intellij.openapi.actionSystem.DefaultActionGroup
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import com.intellij.util.ui.JBUI
import com.intellij.util.ui.components.BorderLayoutPanel
import org.jetbrains.kotlin.process.plugin.actions.rr.RemoteRunSettingsAction
import org.jetbrains.kotlin.process.plugin.actions.rr.RemoteRunStartAction
import org.jetbrains.kotlin.process.plugin.actions.rr.RemoteRunStopAction
import org.jetbrains.teamcity.rest.Build
import org.jetbrains.teamcity.rest.BuildState
import org.jetbrains.teamcity.rest.BuildStatus
import java.awt.BorderLayout
import java.awt.ComponentOrientation
import java.awt.Dimension
import java.awt.FlowLayout
import javax.swing.*

val messagesField = JTextArea("")

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
    } else if(build.status == BuildStatus.FAILURE || build.status == BuildStatus.ERROR) {
        messagesField.text += "Build for branch $branchName just finished.\n " +
                "Status: ${build.statusText}\n " +
                "Link: ${build.getHomeUrl()}\n\n"
    } else {
        messagesField.text += "Build for branch $branchName.\n " +
                "Status: ${build.statusText}\n " +
                "Link: ${build.getHomeUrl()}\n\n"
    }
}

private class RemoteRunToolWindow : ToolWindowFactory {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val rrToolWindow = JPanel(FlowLayout())
        rrToolWindow.add(createActionsToolbar(), FlowLayout.LEFT)
        rrToolWindow.add(createRemoteRunMessagePanel(rrToolWindow))
        rrToolWindow.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT

        val contentFactory = ContentFactory.SERVICE.getInstance()
        val content = contentFactory.createContent(rrToolWindow, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createActionsToolbar(): BorderLayoutPanel {
        return JBUI.Panels.simplePanel().addToLeft(createLeftActionsToolbar())
    }

    private fun createLeftActionsToolbar(): JComponent {
        var group = DefaultActionGroup()
        val actionsPanel = JPanel(BorderLayout())

        group.add(RemoteRunStartAction())
        actionsPanel.add(createToolbar(group), BorderLayout.PAGE_START)

        group = DefaultActionGroup()
        group.add(RemoteRunStopAction())
        actionsPanel.add(createToolbar(group))

        group = DefaultActionGroup()
        group.add(RemoteRunSettingsAction())
        actionsPanel.add(createToolbar(group), BorderLayout.AFTER_LAST_LINE)

        return actionsPanel
    }

    private fun createRemoteRunMessagePanel(rrToolWindow: JPanel): JComponent {
        return object : JScrollPane(
            messagesField,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        ) {
            override fun getPreferredSize(): Dimension {
                val preferredSize = super.getPreferredSize()
                preferredSize.width = rrToolWindow.width - 40
                preferredSize.height = rrToolWindow.height
                return preferredSize
            }
        }
    }
}