package org.jetbrains.kotlin.process.plugin.issue.model

import com.github.jk1.ytplugin.format
import com.github.jk1.ytplugin.issues.model.Issue
import com.github.jk1.ytplugin.rest.IssuesRestClient
import com.github.jk1.ytplugin.tasks.TaskManagerProxyComponent
import com.intellij.openapi.project.Project
import com.intellij.ui.Gray
import com.intellij.ui.JBColor
import com.intellij.ui.SimpleColoredComponent
import com.intellij.ui.SimpleTextAttributes
import com.intellij.ui.SimpleTextAttributes.*
import com.intellij.ui.border.CustomLineBorder
import com.intellij.util.ui.UIUtil
import java.awt.*
import javax.swing.*

class IssueListCellRenderer(
    private val viewportWidthProvider: () -> Int,
    private val project: Project
) : JPanel(BorderLayout()), ListCellRenderer<String> {

    private val topPanel = JPanel(BorderLayout())
    private val bottomPanel = JPanel(BorderLayout())
    private val idSummaryPanel = JPanel(BorderLayout())
    private val idSummary = SimpleColoredComponent()
    private val fields = SimpleColoredComponent()
    private val time = JLabel()
    private val glyphs = JLabel()

    init {
        idSummary.isOpaque = false
        idSummaryPanel.isOpaque = false
        idSummary.font = Font(UIUtil.getLabelFont().family, Font.PLAIN, UIUtil.getLabelFont().size + 1)

        fields.font = Font(UIUtil.getLabelFont().family, Font.PLAIN, UIUtil.getLabelFont().size)

        time.font = Font(UIUtil.getLabelFont().family, Font.PLAIN, UIUtil.getLabelFont().size - 2)

        border = CustomLineBorder(JBColor(Gray._220, Gray._85), 0, 0, 1, 0)

        topPanel.isOpaque = false
        topPanel.add(idSummaryPanel, BorderLayout.WEST)
        topPanel.add(time, BorderLayout.EAST)

        bottomPanel.isOpaque = false
        bottomPanel.add(fields, BorderLayout.WEST)
        bottomPanel.add(glyphs, BorderLayout.EAST)
        bottomPanel.border = BorderFactory.createEmptyBorder(3, 0, 0, 0)

        add(topPanel, BorderLayout.NORTH)
        add(bottomPanel, BorderLayout.SOUTH)
    }

    override fun getListCellRendererComponent(
        list: JList<out String>,
        branch: String, index: Int,
        isSelected: Boolean, cellHasFocus: Boolean
    ): Component {
        try {
            if(!branch.contains(noBranchMsg)) {
                val issue = getIssue(branch)!!
                val fgColor = getFgColor(isSelected, issue)

                background = UIUtil.getListBackground(isSelected)

                fillSummaryLine(issue, branch, fgColor)
                fillCustomFields(issue, fgColor, isSelected)
                createTime(isSelected, issue)
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return this
    }

    private fun getIssue(branch: String): Issue? {
        try {
            val issueId = branch.split("/")[2]
            val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()

            repositories
                .forEach { repository ->
                    try {
                        return IssuesRestClient(repository).getIssue(issueId)
                    } catch (e: RuntimeException) {
                    }
                }

            return null
        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

    private fun getFgColor(isSelected: Boolean, issue: Issue) = when {
        isSelected -> UIUtil.getListForeground(true)
        issue.resolved -> Color(150, 150, 150)
        UIUtil.isUnderDarcula() -> Color(200, 200, 200)
        else -> Color(8, 8, 52)
    }

    private fun fillSummaryLine(issue: Issue, branch: String, fgColor: Color) {
        idSummaryPanel.removeAll()
        idSummary.clear()
        idSummary.ipad = Insets(0, 4, 0, 0)

        var idStyle = STYLE_BOLD
        if (issue.resolved) {
            idStyle = idStyle.or(STYLE_STRIKEOUT)
        }

        idSummary.append(branch, SimpleTextAttributes(idStyle, fgColor))
        idSummary.append(" ")
        idSummaryPanel.add(idSummary, BorderLayout.EAST)
    }

    private fun fillCustomFields(issue: Issue, fgColor: Color, isSelected: Boolean) {
        val viewportWidth = viewportWidthProvider.invoke() - 100
        fields.clear()
        fields.isOpaque = !isSelected
        fields.background = this.background
        issue.customFields.forEach {
            if (viewportWidth > fields.computePreferredSize(false).width) {
                val attributes = when {
                    isSelected || UIUtil.isUnderDarcula() -> SimpleTextAttributes(STYLE_PLAIN, fgColor)
                    else -> SimpleTextAttributes(it.backgroundColor, it.foregroundColor, null, STYLE_PLAIN)
                }
                fields.append(it.formatValues(), attributes)
                fields.append("   ")
            }
        }
    }

    private fun createTime(isSelected: Boolean, issue: Issue) {
        time.foreground = when {
            isSelected -> UIUtil.getListForeground(true)
            else -> JBColor(Color(75, 107, 244), Color(87, 120, 173))
        }
        time.text = issue.updateDate.format() + " "
    }
}