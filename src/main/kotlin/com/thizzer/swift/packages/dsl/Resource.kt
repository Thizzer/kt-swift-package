/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.swift.packages.dsl

import com.thizzer.swift.packages.extensions.quoted
import java.util.*

sealed class Resource(val path: String) {
    class Copy(path: String) : Resource(path) {
        override fun toString(): String {
            return ".copy(${path.quoted()})"
        }
    }

    class Process(path: String, val localization: Locale?) : Resource(path) {
        override fun toString(): String {
            var out = ".process(${path.quoted()}"
            localization?.let {
                out += ", localization: ${localization.toLanguageTag().quoted()}"
            }
            out += ")"
            return out
        }
    }
}

class ResourceList : MutableList<Resource> by mutableListOf() {
    fun process(
        path: String? = null,
        localization: Locale? = null,
        configure: Resource.Process.() -> Unit = {}
    ): Resource {
        val resource = Resource.Process(path ?: "", localization)
        resource.configure()
        add(resource)
        return resource
    }

    fun copy(path: String? = null, configure: Resource.Copy.() -> Unit = {}): Resource {
        val resource = Resource.Copy(path ?: "")
        resource.configure()
        add(resource)
        return resource
    }

    override fun toString(): String {
        var out = "["
        if (isNotEmpty()) {
            out += "\n${joinToString(",\n") { it.toString().replaceIndent("\t") }}\n"
        }
        out += "]"
        return out
    }
}