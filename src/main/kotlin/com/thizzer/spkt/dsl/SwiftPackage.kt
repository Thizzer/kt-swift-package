/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.spkt.dsl

import com.thizzer.spkt.extensions.quoted
import java.util.*

class SwiftPackage(var name: String, var defaultLocalization: Locale? = null) {

    private val platforms: PlatformList = PlatformList()
    private val products: ProductList = ProductList()
    private val targets: TargetList = TargetList()
    private val dependencies: PackageDependencyList = PackageDependencyList()
    private val swiftLanguageVersions: SwiftLanguageVersionList = SwiftLanguageVersionList()

    var cLanguageStandard: CLanguageStandard? = null
    var cxxLanguageStandard: CXXLanguageStandard? = null

    fun defaultLocalization(locale: String): TargetList {
        defaultLocalization = Locale.forLanguageTag(locale)
        return targets
    }

    fun defaultLocalization(locale: Locale): Locale {
        defaultLocalization = locale
        return defaultLocalization!!
    }

    fun platforms(configure: PlatformList.() -> Unit): PlatformList {
        platforms.configure()
        return platforms
    }

    fun products(configure: ProductList.() -> Unit): ProductList {
        products.configure()
        return products
    }

    fun targets(configure: TargetList.() -> Unit): TargetList {
        targets.configure()
        return targets
    }

    fun dependencies(configure: PackageDependencyList.() -> Unit): PackageDependencyList {
        dependencies.configure()
        return dependencies
    }

    fun swiftLanguageVersions(configure: SwiftLanguageVersionList.() -> Unit): SwiftLanguageVersionList {
        swiftLanguageVersions.configure()
        return swiftLanguageVersions
    }

    fun cLanguageStandard(cLanguageStandard: CLanguageStandard) {
        this.cLanguageStandard = cLanguageStandard
    }

    fun cxxLanguageStandard(cxxLanguageStandard: CXXLanguageStandard) {
        this.cxxLanguageStandard = cxxLanguageStandard
    }

    fun toPackageFileString(): String {
        var out = """
               // swift-tools-version:5.3
               import PackageDescription
        """.trimIndent()
        out += "\n\nlet package = ${toString()}"
        return out
    }

    override fun toString(): String {
        val params = mutableListOf<String>()
        params.add("name: ${name.quoted()}")

        defaultLocalization?.let {
            params.add("defaultLocalization: ${it.toLanguageTag().quoted()}")
        }

        if (platforms.isNotEmpty()) {
            params.add("platforms: $platforms")
        }

        if (products.isNotEmpty()) {
            params.add("products: $products")
        }

        if (dependencies.isNotEmpty()) {
            params.add("dependencies: $dependencies")
        }

        if (targets.isNotEmpty()) {
            params.add("targets: $targets")
        }

        if (swiftLanguageVersions.isNotEmpty()) {
            params.add("swiftLanguageVersions: $swiftLanguageVersions")
        }

        return "Package(\n${params.joinToString(",\n").replaceIndent("\t")}\n)"
    }
}

fun swiftPackage(name: String, configure: SwiftPackage.() -> Unit): SwiftPackage {
    val pckg = SwiftPackage(name)
    pckg.configure()
    return pckg
}

fun swiftPackage(configure: SwiftPackage.() -> Unit): SwiftPackage {
    return swiftPackage("", configure)
}