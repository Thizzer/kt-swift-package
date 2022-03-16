/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.swift.packages.dsl

import com.thizzer.swift.packages.extensions.quoted

sealed class Product(val name: String, val targets: List<String> = listOf()) {
    class Library(name: String, val type: Type? = null, targets: List<String> = listOf()) :
        Product(name, targets) {
        enum class Type {
            static, dynamic
        }

        override fun toString(): String {
            var out = ".library(name: ${name.quoted()}"
            type?.let {
                out += ", type: ${it.name.quoted()}"
            }
            out += ", targets: [${targets.joinToString(",") { it.quoted() }}])"
            return out
        }
    }

    class Executable(name: String, targets: List<String>) : Product(name, targets) {
        override fun toString(): String {
            return ".executable(name: ${name.quoted()}, targets: [${targets.joinToString(",") { it.quoted() }}])"
        }
    }

    class Plugin(name: String, targets: List<String>) : Product(name, targets) {
        override fun toString(): String {
            return ".plugin(name: ${name.quoted()}, targets: [${targets.joinToString(",") { it.quoted() }}])"
        }
    }
}

class ProductList : MutableList<Product> by mutableListOf() {
    fun executable(name: String, targets: List<String> = listOf()): Product.Executable {
        val product = Product.Executable(name, targets)
        add(product)
        return product
    }

    fun library(name: String, targets: List<String> = listOf()): Product.Library {
        return library(name, null, targets)
    }

    fun library(name: String, vararg targets: String): Product.Library {
        return library(name, null, targets.toList())
    }

    fun library(name: String, type: Product.Library.Type? = null, targets: List<String> = listOf()): Product.Library {
        val product = Product.Library(name, type, targets)
        add(product)
        return product
    }

    fun plugin(name: String, targets: List<String> = listOf()): Product.Plugin {
        val product = Product.Plugin(name, targets)
        add(product)
        return product
    }

    override fun toString(): String {
        var out = "["
        if (isNotEmpty()) {
            out += "\n\t" + joinToString(",\n\t") { it.toString() } + "\n"
        }
        out += "]"
        return out
    }
}