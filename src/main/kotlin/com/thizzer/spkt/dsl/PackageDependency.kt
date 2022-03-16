/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.spkt.dsl

import com.thizzer.spkt.extensions.quoted

class PackageDependency(val path: String? = null, val url: String? = null, val version: String? = null) {

    companion object {
        val RANGE_SPECIFIERS = listOf<String>("!=", "==", "...", "..<", "<=", ">=", "<", ">")
    }

    override fun toString(): String {
        val params = mutableListOf<String>()
        path?.let {
            params.add("name: ${path.quoted()}")
        }
        url?.let {
            params.add("url: ${url.quoted()}")
        }
        version?.let { version ->
            val rangeSpecifierMatch = RANGE_SPECIFIERS.filter { version.contains(it, ignoreCase = true) }
            if(rangeSpecifierMatch.isNotEmpty()) {
                var versionRange = ""
                rangeSpecifierMatch.forEach {
                    if(version.startsWith(it)) {
                        versionRange += it + version.substring(it.length)
                    }
                    else {
                        val splitVersion = version.split(it, limit = 2)
                        versionRange += splitVersion[0].quoted() + it
                        if(splitVersion.size > 1) {
                            versionRange += splitVersion[1].quoted()
                        }
                    }
                }

                params.add(versionRange)
            }
            else {
                params.add("from: ${version.quoted()}")
            }
        }
        return ".package(\n" + params.joinToString(",\n").replaceIndent("\t") + "\n)"
    }
}

class PackageDependencyList : MutableList<PackageDependency> by mutableListOf() {

    fun `package`(url: String, version: String? = null): PackageDependency {
        val dependency = PackageDependency(url = url, version = version)
        add(dependency)
        return dependency
    }

    fun `package`(path: String): PackageDependency {
        val dependency = PackageDependency(path = path)
        add(dependency)
        return dependency
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