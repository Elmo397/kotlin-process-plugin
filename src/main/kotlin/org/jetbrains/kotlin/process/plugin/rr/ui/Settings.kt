package org.jetbrains.kotlin.process.plugin.rr.ui

import com.intellij.openapi.ui.DialogWrapper
import org.jetbrains.kotlin.process.plugin.rr.model.delay
import java.awt.FlowLayout
import javax.swing.JComponent
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

class Settings(canBeParent: Boolean) : DialogWrapper(canBeParent) {
    private val timeField = JTextField(7)

    init {
        title = "RR settings"
        init()
    }

    override fun createCenterPanel(): JComponent? {
        timeField.text = (delay / 1000).toString()

        val settingsPanel = JPanel(FlowLayout())
        settingsPanel.add(JLabel("Wait"), FlowLayout.LEFT)
        settingsPanel.add(timeField)
        settingsPanel.add(JLabel("seconds between checks remote run."))

        return settingsPanel
    }

    override fun doOKAction() {
        delay = timeField.text.toLong() * 1000
        super.doOKAction()
    }
}