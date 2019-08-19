package org.jetbrains.kotlin.process.plugin.ui.issue

import com.github.jk1.ytplugin.rest.IssuesRestClient
import com.github.jk1.ytplugin.tasks.TaskManagerProxyComponent
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.ui.Messages
import com.intellij.ui.components.JBScrollPane
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator
import org.jetbrains.kotlin.process.plugin.model.issue.*
import java.awt.BorderLayout
import java.awt.Color
import java.awt.ComponentOrientation
import java.awt.FlowLayout
import javax.swing.*
import javax.swing.event.DocumentListener

/**
 * @author Mamedova Elnara
 */
class TicketSelectionDialog(canBeParent: Boolean) : DialogWrapper(canBeParent) {
    private lateinit var issueDialog: JComponent
    private var issueIdField = createIssuesListBox()
    private var devNickField = createDevNickField()
    private var shortDescriptionField = fillShortDescriptionField()

    companion object {
        private lateinit var project: Project

        @JvmStatic
        fun setProject(project: Project) {
            Companion.project = project
        }
    }

    init {
        title = "Issues"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val branchPanel = createBranchPanel()
        val descriptionPanel = createDescriptionPanel()

        val issuePanel = JPanel(BorderLayout())
        issuePanel.add(branchPanel)
        issuePanel.add(JLabel(" "), BorderLayout.AFTER_LAST_LINE)
        issuePanel.add(descriptionPanel, BorderLayout.AFTER_LAST_LINE)

        issueDialog = issuePanel

        return issuePanel
    }

    override fun getPreferredFocusedComponent(): JComponent? {
        val propertiesComponent = PropertiesComponent.getInstance()
        return if (!propertiesComponent.isValueSet("devNick")) {
            devNickField
        } else {
            issueIdField
        }
    }

    override fun doOKAction() {
        val issueId = issueIdField?.selectedItem.toString()
        val devNick = devNickField.text
        val shortDescription = shortDescriptionField.text

        TicketBean().getIssues(issueId, project )

        setFocusNextField(devNickField, issueIdField!!)
        setFocusNextField(issueIdField!!, shortDescriptionField)

        val isValidDevNick = BranchValidator().isValidBranchNamePart(devNick)
        val isValidSummary = BranchValidator().isValidBranchNamePart(shortDescription)

        if (isValidDevNick && isValidSummary) {
            TicketBean().createBranch(issueId, devNick, shortDescription, project)

            val commandResult = changeIssueState(issueId, project, "State In Progress")
            showStateChangeResultBanner(commandResult, issueDialog)

            saveDeveloperName(devNick)
            saveSelectedIssueId(issueId)

            super.doOKAction()
        } else {
            when {
                !isValidDevNick -> devNickField.border = BorderFactory.createLineBorder(Color.red, 1)
                !isValidSummary -> shortDescriptionField.border = BorderFactory.createLineBorder(Color.red, 1)
            }
        }
    }

    fun openDialog(project: Project) {
        Companion.project = project
        show()
    }

    private fun createBranchPanel(): JPanel {
        val branchPanel = JPanel(FlowLayout())
        branchPanel.add(JLabel("Branch: "), FlowLayout.LEFT)
        branchPanel.add(JLabel("rr / "))
        branchPanel.add(devNickField)
        branchPanel.add(JLabel(" / "))
        branchPanel.add(issueIdField)
        branchPanel.add(JLabel(" / "))
        branchPanel.add(shortDescriptionField)
        branchPanel.componentOrientation = ComponentOrientation.LEFT_TO_RIGHT

        return branchPanel
    }

    private fun createDevNickField(): JTextField {
        var field = JTextField(10)
        field = addValidationListener(field)

        SwingUtilities.invokeLater { field.requestFocus() }
        field.toolTipText = "Developer nickname"

        val propertiesComponent = PropertiesComponent.getInstance()
        if (propertiesComponent.isValueSet("devNick")) {
            field.text = propertiesComponent.getValue("devNick")
        }

        return field
    }

    private fun createIssuesListBox(): ComboBox<String>? {
        val issuesBox = getIssuesId()
        issuesBox!!.isEditable = true
        AutoCompleteDecorator.decorate(issuesBox)

        return issuesBox
    }

    private fun getIssuesId(): ComboBox<String>? {
        try {
            val repositories = TaskManagerProxyComponent(project).getAllConfiguredYouTrackRepositories()
            val issuesList = DefaultComboBoxModel<String>()
            issuesList.addElement("")

            val query = "for: me"
            repositories.forEach { repository ->
                IssuesRestClient(repository)
                    .getIssues(query)
                    .forEach { issue ->
                        issuesList.addElement(issue.id)
                        urlIssueMap[issue.id] = issue.url
                    }
            }

            return ComboBox(issuesList)
        } catch (e: Throwable) {
            println(e)
            return null
        }
    }

    private fun fillShortDescriptionField(): JTextField {
        var field = JTextField(20)
        field = addValidationListener(field)

        field.text = TicketBean()
            .showShortDescription(
                issueIdField, urlIssueMap,
                project
            )
        field.toolTipText = "Issue short description"
        issueIdField!!.addActionListener {
            field.text = TicketBean()
                .showShortDescription(
                    issueIdField, urlIssueMap,
                    project
                )
        }

        return field
    }

    private fun addValidationListener(textField: JTextField): JTextField {
        textField.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(p0: javax.swing.event.DocumentEvent?) {
                checkField()
            }

            override fun removeUpdate(p0: javax.swing.event.DocumentEvent?) {
                checkField()
            }

            override fun changedUpdate(p0: javax.swing.event.DocumentEvent?) {
                checkField()
            }

            fun checkField() {
                val isValidBranchNamePart = BranchValidator().isValidBranchNamePart(textField.text)

                if (isValidBranchNamePart) {
                    textField.border = BorderFactory.createLineBorder(Color.blue, 0)
                } else {
                    textField.border = BorderFactory.createLineBorder(Color.red, 1)
                }
            }
        })

        return textField
    }

    private fun setFocusNextField(currentField: JComponent, nextField: JComponent) {
        if (currentField.hasFocus()) {
            SwingUtilities.invokeLater { nextField.requestFocus() }
        }
    }

    private fun createDescriptionPanel(): JPanel {
        val descriptionPanel = JPanel(BorderLayout())
        descriptionPanel.add(JLabel("Issue description:"))

        val descriptionField = createDescriptionField()
        val scrollPane = JBScrollPane(
            descriptionField,
            ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED
        )
        descriptionPanel.add(scrollPane, BorderLayout.AFTER_LAST_LINE)

        return descriptionPanel
    }

    private fun createDescriptionField(): JTextArea {
        val descriptionField = JTextArea(15, 70)
        descriptionField.isEditable = false

        try {
            descriptionField.text = TicketBean()
                .showDescription(issueIdField, urlIssueMap,
                    project
                )

            issueIdField!!.addActionListener {
                descriptionField.text = TicketBean()
                    .showDescription(issueIdField, urlIssueMap,
                        project
                    )
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        return descriptionField
    }

    private fun saveDeveloperName(devNick: String) {
        PropertiesComponent.getInstance().setValue("devNick", devNick)
    }

    private fun saveSelectedIssueId(issueId: String) {
        PropertiesComponent.getInstance().setValue("issueId", issueId)
    }
}