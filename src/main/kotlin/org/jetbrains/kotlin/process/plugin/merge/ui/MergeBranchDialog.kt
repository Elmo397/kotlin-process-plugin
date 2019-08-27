package org.jetbrains.kotlin.process.plugin.merge.ui

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.merge.model.getBranchOfFixedIssue
import org.jetbrains.kotlin.process.plugin.merge.model.merge
import java.awt.FlowLayout
import javax.swing.DefaultComboBoxModel
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel

class MergeBranchDialog(canBeParent: Boolean, private val project: Project) : DialogWrapper(canBeParent) {
    private val branchList = createBranchListBox()

    init {
        title = "Merge to master"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        val mergePanel = JPanel(FlowLayout())
        mergePanel.add(JLabel("Merge"), FlowLayout.LEFT)
        mergePanel.add(branchList)
        mergePanel.add(JLabel("branch."))

        return mergePanel
    }

    override fun doOKAction() {
        try {
            val selectedBranch = branchList.selectedItem!!.toString()
            merge(selectedBranch)
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        super.doOKAction()
    }

    private fun createBranchListBox(): ComboBox<String> {
        val resolvedBranches = DefaultComboBoxModel<String>()
        resolvedBranches.addAll(getBranchOfFixedIssue(project))

        return ComboBox(resolvedBranches, 320)
    }
}