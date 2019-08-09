@file:JvmName("Main")

package org.jetbrains.kotlin.process.bot.rr

import org.jetbrains.kotlin.process.botutil.errorMessage
import java.io.File

const val SERVICE_NAME = "Remote Run"

val stateFile: File = File("tmp/rr.properties")
const val delay: Long = 1000

fun main() {
    while (true) {
        try {
            checkRemoteRun()
        } catch (e: Throwable) {
            val errorMessage = e.errorMessage(SERVICE_NAME)
            //notifications.send(config.adminUserId, true, SERVICE_NAME, errorMessage).execute()
        }

        Thread.sleep(delay)
    }
}