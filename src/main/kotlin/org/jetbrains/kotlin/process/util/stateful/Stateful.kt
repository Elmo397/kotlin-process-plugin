package org.jetbrains.kotlin.process.util.stateful

import com.fatboyindustrial.gsonjavatime.Converters
import com.google.gson.GsonBuilder
import java.io.File

private val gson = GsonBuilder().also { Converters.registerAll(it) }.create()

interface Stateful<T : Stateful<T>> {
    companion object
}

fun <T : Stateful<T>> (@Suppress("unused") Stateful<T>).write(file: File) {
    file.parentFile.mkdirs()
    file.bufferedWriter().use { writer ->
        writer.write(gson.toJson(this))
    }
}

inline fun <reified T : Stateful<T>> Stateful.Companion.read(file: File): T? {
    return if (file.exists()) {
        read(file, T::class.java)
    } else {
        null
    }
}

@PublishedApi
internal fun <T> read(file: File, cls: Class<T>): T {
    return file.bufferedReader().use { reader ->
        gson.fromJson(reader, cls)
    }
}