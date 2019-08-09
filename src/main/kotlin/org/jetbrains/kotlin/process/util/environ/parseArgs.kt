package org.jetbrains.kotlin.process.env

import java.io.File
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method
import java.lang.reflect.Proxy
import java.net.URLClassLoader

inline fun <reified T : Any> parseEnviron(): T {
    return parseEnviron(T::class.java)
}

@PublishedApi
internal fun <T> parseEnviron(cls: Class<T>): T {
    val cl = URLClassLoader(emptyArray())
    return parseEnviron(cl, parseEnviron(), cls, prefix = "")
}

internal fun <T> parseEnviron(cl: ClassLoader, env: Map<String, String>, cls: Class<T>, prefix: String): T {
    if (!cls.isInterface) {
        error("Interface expected, got ${cls.name}")
    }

    val map = HashMap<String, Any>()

    for (method in cls.methods) {
        val propertyName = method.getPropertyName() ?: continue
        val fullName = prefix.appendComponent(propertyName)

        val rawArg by lazy {
            env.getOrElse(fullName) {
                error("Value is not specified for $fullName")
            }
        }

        fun typeError(typeName: String): Nothing = error("Invalid value for $fullName, $typeName expected")

        val value = when (val type = method.returnType) {
            String::class.java -> rawArg
            Int::class.java -> rawArg.toIntOrNull() ?: typeError("Int")
            Long::class.java -> rawArg.toLongOrNull() ?: error("Long")
            File::class.java -> File(rawArg)
            else -> parseEnviron(cl, env, type, prefix.appendComponent(propertyName))
        }

        map[propertyName] = value
    }

    @Suppress("UNCHECKED_CAST")
    return Proxy.newProxyInstance(cl, arrayOf(cls), object : InvocationHandler {
        override fun invoke(proxy: Any, method: Method, env: Array<out Any>?): Any? {
            val propertyName = method.getPropertyName() ?: return null
            return if (env == null || env.isEmpty()) map[propertyName] else null
        }
    }) as T
}

private fun String.appendComponent(component: String): String {
    return if (this.isEmpty()) {
        component
    } else {
        "${this}_$component"
    }
}

private fun Method.getPropertyName(): String? {
    if (name.startsWith("get")) {
        return name.drop(3).decapitalize()
    }

    return null
}

private fun parseEnviron(): Map<String, String> {
    val map = hashMapOf<String, String>()

    for ((key, value) in System.getenv()) {
        if (!key.startsWith("P_")) {
            continue
        }

        map[key.drop(2)] = value
    }

    return map
}