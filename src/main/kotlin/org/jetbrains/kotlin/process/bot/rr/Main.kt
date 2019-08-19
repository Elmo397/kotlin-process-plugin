@file:JvmName("Main")

package org.jetbrains.kotlin.process.bot.rr

import com.intellij.notification.Notification
import com.intellij.notification.NotificationType
import com.intellij.notification.Notifications
import org.jetbrains.kotlin.process.botutil.errorMessage
import java.io.File

const val SERVICE_NAME = "Remote Run"
val stateFile: File = File("tmp/rr.properties")
var delay: Long = 120000
var isRunning = true

fun main() {
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