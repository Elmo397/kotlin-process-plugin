package org.jetbrains.kotlin.process.plugin.rr.model

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import org.jetbrains.kotlin.process.util.errorMessage
import java.io.File

const val SERVICE_NAME = "Remote Run"
var delay: Long = 120000
var isRunning = true

fun startRr() {
    while (isRunning) {
        try {
            checkRemoteRun()
        } catch (e: Throwable) {
            val errorMessage = e.errorMessage(SERVICE_NAME)
            Notifications.Bus.notify(
                Notification(
                    "Kotlin Process", "Error",
                    errorMessage, NotificationType.ERROR
                )
            )
        }

        Thread.sleep(delay)
    }
}