package org.jetbrains.kotlin.process.util

import java.io.PrintWriter
import java.io.StringWriter

fun Throwable.errorMessage(serviceName: String): String {
    return buildString {
        append("An error occurred in $serviceName: ")
        appendln(this@errorMessage.message)
        appendln()

        val buffer = StringWriter()
        printStackTrace(PrintWriter(buffer))
        append(buffer)
    }
}