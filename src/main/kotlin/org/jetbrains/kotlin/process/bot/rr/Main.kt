@file:JvmName("Main")

package org.jetbrains.kotlin.process.bot.rr

import java.io.File

val stateFile: File = File("tmp/rr.properties")
var delay: Long = 120000
var isRunning = true

fun main() {
    while (isRunning) {
        try {
            checkRemoteRun()
        } catch (e: Throwable) {
            e.printStackTrace()
        }

        Thread.sleep(delay)
    }
}