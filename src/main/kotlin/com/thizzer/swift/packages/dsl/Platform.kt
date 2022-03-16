/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.swift.packages.dsl

import com.thizzer.swift.packages.extensions.quoted

class SupportedPlatform(
    val platformName: String,
    val versionString: String? = null,
    val versionEnum: SupportedPlatformVersion? = null,
    val custom: Boolean = false
) {
    interface SupportedPlatformVersion {
        val name: String
    }

    enum class MacOSVersion : SupportedPlatformVersion {
        v10_10, v10_11, v10_12, v10_13, v10_14, v10_15, v11, v12
    }

    enum class IOSVersion : SupportedPlatformVersion {
        v8, v9, v10, v11, v12, v13, v14, v15
    }

    enum class WatchOSVersion : SupportedPlatformVersion {
        v2, v3, v4, v5, v6, v7
    }

    enum class TVOSVersion : SupportedPlatformVersion {
        v9, v10, v11, v12, v13, v14
    }

    enum class DriverKitVersion : SupportedPlatformVersion {
        v19, v20, v21
    }

    enum class MacCatalystVersion : SupportedPlatformVersion {
        v13, v14, v15
    }

    override fun toString(): String {
        var out = ".${platformName}"
        if (custom) {
            out = ".custom"
        }
        if (!versionString.isNullOrEmpty()) {
            out += "(${versionString.quoted()})"
        } else if (versionEnum != null) {
            out += "(.${versionEnum.name})"
        }
        return out
    }
}

class PlatformList : MutableList<SupportedPlatform> by mutableListOf() {

    fun iOS(): SupportedPlatform {
        val platform = SupportedPlatform("iOS")
        add(platform)
        return platform
    }

    fun iOS(version: SupportedPlatform.IOSVersion): SupportedPlatform {
        val platform = SupportedPlatform("iOS", versionEnum = version)
        add(platform)
        return platform
    }

    fun iOS(versionString: String): SupportedPlatform {
        val platform = SupportedPlatform("iOS", versionString)
        add(platform)
        return platform
    }

    fun macOS(): SupportedPlatform {
        val platform = SupportedPlatform("macOS")
        add(platform)
        return platform
    }

    fun macOS(version: SupportedPlatform.MacOSVersion): SupportedPlatform {
        val platform = SupportedPlatform("macOS", versionEnum = version)
        add(platform)
        return platform
    }

    fun macOS(versionString: String): SupportedPlatform {
        val platform = SupportedPlatform("macOS", versionString)
        add(platform)
        return platform
    }

    fun watchOS(): SupportedPlatform {
        val platform = SupportedPlatform("watchOS")
        add(platform)
        return platform
    }

    fun watchOS(version: SupportedPlatform.WatchOSVersion): SupportedPlatform {
        val platform = SupportedPlatform("watchOS", versionEnum = version)
        add(platform)
        return platform
    }

    fun watchOS(versionString: String): SupportedPlatform {
        val platform = SupportedPlatform("watchOS", versionString)
        add(platform)
        return platform
    }

    fun tvOS(): SupportedPlatform {
        val platform = SupportedPlatform("tvOS")
        add(platform)
        return platform
    }

    fun tvOS(version: SupportedPlatform.TVOSVersion): SupportedPlatform {
        val platform = SupportedPlatform("tvOS", versionEnum = version)
        add(platform)
        return platform
    }

    fun tvOS(versionString: String): SupportedPlatform {
        val platform = SupportedPlatform("tvOS", versionString)
        add(platform)
        return platform
    }

    fun linux(): SupportedPlatform {
        val platform = SupportedPlatform("linux")
        add(platform)
        return platform
    }

    fun android(): SupportedPlatform {
        val platform = SupportedPlatform("android")
        add(platform)
        return platform
    }

    fun wasi(): SupportedPlatform {
        val platform = SupportedPlatform("wasi")
        add(platform)
        return platform
    }

    fun windows(): SupportedPlatform {
        val platform = SupportedPlatform("windows")
        add(platform)
        return platform
    }

    fun custom(platformName: String, versionString: String): SupportedPlatform {
        val platform = SupportedPlatform(platformName, versionString, custom = true)
        add(platform)
        return platform
    }

    fun driverKit(version: SupportedPlatform.DriverKitVersion): SupportedPlatform {
        val platform = SupportedPlatform("driverKit", versionEnum = version)
        add(platform)
        return platform
    }

    fun driverKit(versionString: String): SupportedPlatform {
        val platform = SupportedPlatform("driverKit", versionString)
        add(platform)
        return platform
    }

    fun macCatalyst(version: SupportedPlatform.MacCatalystVersion): SupportedPlatform {
        val platform = SupportedPlatform("macCatalyst", versionEnum = version)
        add(platform)
        return platform
    }

    fun macCatalyst(versionString: String): SupportedPlatform {
        val platform = SupportedPlatform("macCatalyst", versionString)
        add(platform)
        return platform
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