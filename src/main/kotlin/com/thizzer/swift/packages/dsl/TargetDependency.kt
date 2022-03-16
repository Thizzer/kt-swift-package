/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.swift.packages.dsl

import com.thizzer.swift.packages.extensions.quoted

class TargetDependencyCondition(val platforms: PlatformList = PlatformList()) {

    override fun toString(): String {
        return ".when(${platforms})"
    }
}

sealed class TargetDependency(var name: String? = null, var condition: TargetDependencyCondition?) {

    class Product(name: String?, var `package`: String? = null, condition: TargetDependencyCondition?) :
        TargetDependency(name, condition) {

        override fun toString(): String {
            val params = mutableListOf<String>()
            params.add("name: ${name.quoted()}")
            `package`?.let {
                params.add("package: ${`package`.quoted()}")
            }
            condition?.let {
                params.add("condition: ${condition}")
            }
            return ".product(\n${params.joinToString(",\n").replaceIndent("\t")}\n)"
        }
    }

    class Target(name: String?, condition: TargetDependencyCondition?) : TargetDependency(name, condition) {

        override fun toString(): String {
            val params = mutableListOf<String>()
            params.add("name: ${name.quoted()}")
            condition?.let {
                params.add("condition: ${condition}")
            }
            return ".target(\n${params.joinToString(",\n").replaceIndent("\t")}\n)"
        }
    }

    class ByName(name: String?, condition: TargetDependencyCondition?) : TargetDependency(name, condition) {

        override fun toString(): String {
            val params = mutableListOf<String>()
            params.add("name: ${name.quoted()}")
            condition?.let {
                params.add("condition: $condition")
            }
            return ".byName(\n${params.joinToString(",\n").replaceIndent("\t")}\n)"
        }
    }

    class StringLiteral(name: String?) : TargetDependency(name, null) {

        override fun toString(): String {
            return name.quoted()
        }
    }

    fun `when`(configure: PlatformList.() -> Unit) {
        if (condition == null) {
            condition = TargetDependencyCondition()
        }

        condition?.let {
            it.platforms.configure()
        }
    }
}

class TargetDependencyList : MutableList<TargetDependency> by mutableListOf() {

    fun stringLiteral(
        name: String? = null, configure: TargetDependency.StringLiteral.() -> Unit = {}
    ): TargetDependency {
        val dependency = TargetDependency.StringLiteral(name)
        dependency.configure()
        add(dependency)
        return dependency
    }

    fun product(
        name: String? = null,
        `package`: String? = null,
        condition: TargetDependencyCondition? = null,
        configure: TargetDependency.Product.() -> Unit = {}
    ): TargetDependency {
        val dependency = TargetDependency.Product(name, `package`, condition)
        dependency.configure()
        add(dependency)
        return dependency
    }

    fun target(
        name: String? = null,
        condition: TargetDependencyCondition? = null,
        configure: TargetDependency.Target.() -> Unit = {}
    ): TargetDependency {
        val dependency = TargetDependency.Target(name, condition)
        dependency.configure()
        add(dependency)
        return dependency
    }

    fun byName(
        name: String? = null,
        condition: TargetDependencyCondition? = null,
        configure: TargetDependency.ByName.() -> Unit = {}
    ): TargetDependency {
        val dependency = TargetDependency.ByName(name, condition)
        dependency.configure()
        add(dependency)
        return dependency
    }

    override fun toString(): String {
        var out = "["
        if (isNotEmpty()) {
            out += "\n" + joinToString(",\n\t") { it.toString().replaceIndent("\t") } + "\n"
        }
        out += "]"
        return out
    }
}