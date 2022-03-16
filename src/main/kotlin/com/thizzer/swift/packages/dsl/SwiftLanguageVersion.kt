package com.thizzer.swift.packages.dsl

import com.thizzer.swift.packages.extensions.quoted

class SwiftLanguageVersion(val version: String? = null, val custom: Boolean = false) {
    companion object {
        val v4 = SwiftLanguageVersion("v4")
        val v4_2 = SwiftLanguageVersion("v4_2")
        val v5 = SwiftLanguageVersion("v5")
    }

    override fun toString(): String {
        if (custom) {
            return ".version(${version.quoted()})"
        }
        return ".${version}"
    }
}

class SwiftLanguageVersionList : MutableList<SwiftLanguageVersion> by mutableListOf() {
    fun v4(): SwiftLanguageVersion {
        val languageVersion = SwiftLanguageVersion.v4
        add(languageVersion)
        return languageVersion
    }

    fun v4_2(): SwiftLanguageVersion {
        val languageVersion = SwiftLanguageVersion.v4_2
        add(languageVersion)
        return languageVersion
    }

    fun v5(): SwiftLanguageVersion {
        val languageVersion = SwiftLanguageVersion.v5
        add(languageVersion)
        return languageVersion
    }

    fun version(version: String): SwiftLanguageVersion {
        val languageVersion = SwiftLanguageVersion(version, true)
        add(languageVersion)
        return languageVersion
    }

    override fun toString(): String {
        return "[${joinToString { it.toString() }}]"
    }
}