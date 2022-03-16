/**
 * This software may be modified and distributed under the terms
 * of the MIT license.  See the LICENSE file for details.
 */
package com.thizzer.swift.packages.dsl

import com.thizzer.swift.packages.extensions.quoted

sealed class Target(var name: String? = null, var path: String? = null) {

    open class SimpleTarget(name: String? = null, path: String? = null) : com.thizzer.swift.packages.dsl.Target(name, path) {
        private val excludes = mutableListOf<String>()
        private val sources = mutableListOf<String>()
        private val dependencies = TargetDependencyList()

        fun exclude(vararg exclude: String) {
            excludes.addAll(exclude.toList())
        }

        fun sources(vararg source: String) {
            sources.addAll(source.toList())
        }

        fun dependencies(
            vararg stringLiteral: String, configure: TargetDependencyList.() -> Unit = {}
        ): TargetDependencyList {
            dependencies.addAll(stringLiteral.map { TargetDependency.StringLiteral(it) })
            dependencies.configure()
            return dependencies
        }

        fun dependencies(configure: TargetDependencyList.() -> Unit): TargetDependencyList {
            dependencies.configure()
            return dependencies
        }

        protected open fun toParams(): List<String> {
            val params = mutableListOf<String>()
            name?.let {
                params.add("name: ${it.quoted()}")
            }
            if (dependencies.isNotEmpty()) {
                params.add("dependencies: $dependencies")
            }
            path?.let {
                params.add("path: ${it.quoted()}")
            }
            if (excludes.isNotEmpty()) {
                params.add("exclude: [${excludes.joinToString { it.quoted() }}]")
            }
            if (sources.isNotEmpty()) {
                params.add("sources: [${sources.joinToString { it.quoted() }}]")
            }
            return params
        }

        override fun toString(): String {
            return ".target(\n${toParams().joinToString(",\n").replaceIndent("\t")}\n)"
        }
    }

    open class ComplexTarget(name: String? = null, path: String? = null) : SimpleTarget(name, path) {
        enum class BuildConfiguration {
            debug, release
        }

        class BuildSettingCondition(
            var platforms: PlatformList? = null,
            var configuration: BuildConfiguration? = null
        ) {
            fun platforms(configure: PlatformList.() -> Unit) {
                platforms = PlatformList()
                platforms?.let {
                    it.configure()
                }
            }

            override fun toString(): String {
                val params = mutableListOf<String>()
                platforms?.let {
                    params.add("platforms: $platforms")
                }
                configuration?.let {
                    params.add("configuration: .${it.name}")
                }
                return ".when(${params.joinToString()})"
            }
        }

        sealed class CompilerSetting(var condition: BuildSettingCondition? = null) {
            class HeaderSearchPath(val path: String, condition: BuildSettingCondition? = null) :
                CompilerSetting(condition) {
                override fun toString(): String {
                    var out = ".headerSearchPath(${path.quoted()}"
                    condition?.let {
                        out += ", $condition"
                    }
                    return out + ")"
                }
            }

            class Define(val name: String, val to: String? = null, condition: BuildSettingCondition? = null) :
                CompilerSetting(condition) {
                override fun toString(): String {
                    var out = ".define(${name.quoted()}"
                    to?.let {
                        out += ", ${to.quoted()}"
                    }
                    condition?.let {
                        out += ", $condition"
                    }
                    return out + ")"
                }
            }

            class UnsafeFlags(vararg val flags: String, condition: BuildSettingCondition? = null) :
                CompilerSetting(condition) {
                override fun toString(): String {
                    var out = ".unsafeFlags([${flags.joinToString { it.quoted() }}]"
                    condition?.let {
                        out += ", $condition"
                    }
                    return out + ")"
                }
            }

            fun `when`(configure: BuildSettingCondition.() -> Unit): BuildSettingCondition? {
                condition = BuildSettingCondition()
                condition?.let {
                    it.configure()
                }
                return condition
            }
        }

        class CompilerSettingsList : MutableList<CompilerSetting> by mutableListOf() {
            fun define(
                name: String,
                to: String? = null,
                condition: BuildSettingCondition? = null,
                configure: CompilerSetting.Define.() -> Unit = {}
            ): CompilerSetting {
                val setting = CompilerSetting.Define(name, to, condition)
                setting.configure()
                add(setting)
                return setting
            }

            fun headerSearchPath(
                path: String,
                condition: BuildSettingCondition? = null,
                configure: CompilerSetting.HeaderSearchPath.() -> Unit = {}
            ): CompilerSetting {
                val setting = CompilerSetting.HeaderSearchPath(path, condition)
                setting.configure()
                add(setting)
                return setting
            }

            fun unsafeFlags(
                vararg flags: String,
                condition: BuildSettingCondition? = null,
                configure: CompilerSetting.UnsafeFlags.() -> Unit = {}
            ): CompilerSetting {
                val setting = CompilerSetting.UnsafeFlags(*flags, condition = condition)
                setting.configure()
                add(setting)
                return setting
            }

            fun `when`(configure: BuildSettingCondition.() -> Unit): BuildSettingCondition {
                val condition = BuildSettingCondition()
                condition.configure()
                return condition
            }

            override fun toString(): String {
                return "[\n${joinToString(",\n").replaceIndent("\t")}\n]"
            }
        }

        sealed class LinkerSetting(var condition: BuildSettingCondition? = null) {
            class LinkedFramework(val framework: String, condition: BuildSettingCondition? = null) :
                LinkerSetting(condition) {
                override fun toString(): String {
                    var out = ".linkedFramework(${framework.quoted()}"
                    condition?.let {
                        out += ", $condition"
                    }
                    return out + ")"
                }
            }

            class LinkedLibrary(val library: String, condition: BuildSettingCondition? = null) :
                LinkerSetting(condition) {
                override fun toString(): String {
                    var out = ".linkedLibrary(${library.quoted()}"
                    condition?.let {
                        out += ", $condition"
                    }
                    return out + ")"
                }
            }

            class UnsafeFlags(vararg val flags: String, condition: BuildSettingCondition? = null) :
                LinkerSetting(condition) {
                override fun toString(): String {
                    var out = ".unsafeFlags([${flags.joinToString { it.quoted() }}]"
                    condition?.let {
                        out += ", $condition"
                    }
                    return out + ")"
                }
            }

            fun `when`(configure: BuildSettingCondition.() -> Unit): BuildSettingCondition? {
                condition = BuildSettingCondition()
                condition?.let {
                    it.configure()
                }
                return condition
            }
        }

        class LinkerSettingsList : MutableList<LinkerSetting> by mutableListOf() {
            fun linkedFramework(
                path: String,
                condition: BuildSettingCondition? = null,
                configure: LinkerSetting.LinkedFramework.() -> Unit = {}
            ): LinkerSetting {
                val setting = LinkerSetting.LinkedFramework(path, condition)
                setting.configure()
                add(setting)
                return setting
            }

            fun linkedLibrary(
                path: String,
                condition: BuildSettingCondition? = null,
                configure: LinkerSetting.LinkedLibrary.() -> Unit = {}
            ): LinkerSetting {
                val setting = LinkerSetting.LinkedLibrary(path, condition)
                setting.configure()
                add(setting)
                return setting
            }

            fun unsafeFlags(
                vararg flags: String,
                condition: BuildSettingCondition? = null,
                configure: LinkerSetting.UnsafeFlags.() -> Unit = {}
            ): LinkerSetting {
                val setting = LinkerSetting.UnsafeFlags(*flags, condition = condition)
                setting.configure()
                add(setting)
                return setting
            }

            fun `when`(configure: BuildSettingCondition.() -> Unit): BuildSettingCondition {
                val condition = BuildSettingCondition()
                condition.configure()
                return condition
            }

            override fun toString(): String {
                return "[\n${joinToString(",\n").replaceIndent("\t")}\n]"
            }
        }

        class PluginUsage(var name: String, var `package`: String? = null) {
            override fun toString(): String {
                val params = mutableListOf<String>()
                params.add("name: ${name.quoted()}")
                `package`?.let {
                    params.add("it: ${it.quoted()}")
                }
                return ".plugin(${params.joinToString()})"
            }
        }

        class PluginUsageList : MutableList<PluginUsage> by mutableListOf() {
            fun plugin(name: String, configure: PluginUsage.() -> Unit = {}): PluginUsage {
                val plugin = PluginUsage(name)
                plugin.configure()
                add(plugin)
                return plugin
            }

            fun plugin(name: String, `package`: String, configure: PluginUsage.() -> Unit = {}): PluginUsage {
                val plugin = PluginUsage(name, `package`)
                plugin.configure()
                add(plugin)
                return plugin
            }

            override fun toString(): String {
                return "[\n${joinToString(",\n").replaceIndent("\t")}\n]"
            }
        }

        private val resources = ResourceList()
        private val cSettings = CompilerSettingsList()
        private val cxxSettings = CompilerSettingsList()
        private val swiftSettings = CompilerSettingsList()
        private val linkerSettings = LinkerSettingsList()
        private val plugins = PluginUsageList()

        fun resources(configure: ResourceList.() -> Unit): ResourceList {
            resources.configure()
            return resources
        }

        fun cSettings(configure: CompilerSettingsList.() -> Unit): CompilerSettingsList {
            cSettings.configure()
            return cSettings
        }

        fun cxxSettings(configure: CompilerSettingsList.() -> Unit): CompilerSettingsList {
            cxxSettings.configure()
            return cxxSettings
        }

        fun swiftSettings(configure: CompilerSettingsList.() -> Unit): CompilerSettingsList {
            swiftSettings.configure()
            return swiftSettings
        }

        fun linkerSettings(configure: LinkerSettingsList.() -> Unit): LinkerSettingsList {
            linkerSettings.configure()
            return linkerSettings
        }

        fun plugins(configure: PluginUsageList.() -> Unit): PluginUsageList {
            plugins.configure()
            return plugins
        }

        override fun toParams(): List<String> {
            val params = super.toParams().toMutableList()
            if (resources.isNotEmpty()) {
                params.add("resources: $resources")
            }
            if (cSettings.isNotEmpty()) {
                params.add("cSettings: $cSettings")
            }
            if (cxxSettings.isNotEmpty()) {
                params.add("cxxSettings: $cxxSettings")
            }
            if (swiftSettings.isNotEmpty()) {
                params.add("swiftSettings: $swiftSettings")
            }
            if (linkerSettings.isNotEmpty()) {
                params.add("linkerSettings: $linkerSettings")
            }
            if (plugins.isNotEmpty()) {
                params.add("plugins: $plugins")
            }
            return params
        }
    }

    open class Target(name: String? = null, path: String? = null) : ComplexTarget(name, path) {
        var publicHeadersPath: String? = null

        override fun toParams(): List<String> {
            val params = super.toParams().toMutableList()
            publicHeadersPath?.let {
                params.add("publicHeadersPath: ${publicHeadersPath.quoted()}")
            }
            return params
        }
    }

    class ExecutableTarget(name: String? = null, path: String? = null) : Target(name, path) {

        override fun toString(): String {
            return super.toString().replace(".target", ".executableTarget")
        }
    }

    class TestTarget(name: String? = null, path: String? = null) : ComplexTarget(name, path) {

        override fun toString(): String {
            return super.toString().replace(".target", ".testTarget")
        }
    }

    class PluginTarget(name: String? = null, var capability: Capability? = null) : SimpleTarget(name) {
        sealed class Permission {
            class WriteToPackageDirectory(val reason: String) : Permission() {
                override fun toString(): String {
                    return ".writeToPackageDirectory(${reason.quoted()})"
                }
            }
        }

        class PermissionList : MutableList<Permission> by mutableListOf() {
            fun writeToPackageDirectory(reason: String): Permission {
                val permission = Permission.WriteToPackageDirectory(reason)
                add(permission)
                return permission
            }

            override fun toString(): String {
                return "[${joinToString()}]"
            }
        }

        sealed class CommandIntent {
            class DocumentationGeneration : CommandIntent() {
                override fun toString(): String {
                    return ".documentationGeneration()"
                }
            }

            class SourceCodeFormatting : CommandIntent() {
                override fun toString(): String {
                    return ".sourceCodeFormatting()"
                }
            }
        }

        sealed class Capability {
            class BuildTool : Capability() {
                override fun toString(): String {
                    return ".buildTool()"
                }
            }

            class Command(var intent: CommandIntent?, var permissions: PermissionList? = null) : Capability() {
                fun documentationGeneration(): CommandIntent {
                    return CommandIntent.DocumentationGeneration()
                }

                fun sourceCodeFormatting(): CommandIntent {
                    return CommandIntent.SourceCodeFormatting()
                }

                fun permissions(configure: PermissionList.() -> Unit) {
                    if (permissions == null) {
                        permissions = PermissionList()
                    }
                    permissions?.let {
                        it.configure()
                    }
                }

                override fun toString(): String {
                    val params = mutableListOf<String>()
                    params.add("intent: $intent")
                    params.add("permissions: ${permissions ?: "[]"}")
                    return ".command(${params.joinToString()})"
                }
            }
        }

        fun buildTool(): Capability {
            return Capability.BuildTool()
        }

        fun command(
            intent: CommandIntent? = null,
            permissions: PermissionList? = null,
            configure: Capability.Command.() -> Unit = {}
        ): Capability {
            val capability = Capability.Command(intent, permissions)
            capability.configure()
            return capability
        }

        override fun toParams(): List<String> {
            val params = super.toParams().toMutableList()
            capability?.let {
                params.add("capability: $capability")
            }
            return params
        }

        override fun toString(): String {
            return super.toString().replace(".target", ".plugin")
        }
    }

    class SystemLibraryTarget(
        name: String? = null,
        path: String? = null,
        var pkgConfig: String? = null,
        var providers: PackageProviderList? = null
    ) : com.thizzer.swift.packages.dsl.Target(name, path) {

        class PackageProvider(val provider: String, val packages: List<String> = mutableListOf()) {

            override fun toString(): String {
                return ".${provider}([${packages.joinToString { it.quoted() }}])"
            }
        }

        class PackageProviderList : MutableList<PackageProvider> by mutableListOf() {
            fun brew(vararg packages: String): PackageProvider {
                val provider = PackageProvider("brew", packages.toList())
                add(provider)
                return provider
            }

            fun yum(vararg packages: String): PackageProvider {
                val provider = PackageProvider("yum", packages.toList())
                add(provider)
                return provider
            }

            fun apt(vararg packages: String): PackageProvider {
                val provider = PackageProvider("apt", packages.toList())
                add(provider)
                return provider
            }

            override fun toString(): String {
                return "[${joinToString()}]"
            }
        }

        fun providers(configure: PackageProviderList.() -> Unit) {
            if (providers == null) {
                providers = PackageProviderList()
            }
            providers?.let {
                it.configure()
            }
        }

        override fun toString(): String {
            val params = mutableListOf<String>()
            params.add("name: ${name?.quoted() ?: "nil"}")
            path?.let {
                params.add("path: ${it.quoted()}")
            }
            pkgConfig?.let {
                params.add("pkgConfig: ${it.quoted()}")
            }
            providers?.let {
                params.add("providers: $it")
            }
            return ".systemLibrary(\n${params.joinToString(",\n").replaceIndent("\t")}\n)"
        }
    }

    class BinaryTarget(
        name: String? = null, path: String? = null, var url: String? = null, var checksum: String? = null
    ) : com.thizzer.swift.packages.dsl.Target(name, path) {
        private val dependencies = TargetDependencyList()

        override fun toString(): String {
            val params = mutableListOf<String>()
            params.add("name: ${name?.quoted() ?: "nil"}")
            path?.let {
                params.add("path: ${it.quoted()}")
            }
            url?.let {
                params.add("url: ${it.quoted()}")
            }
            checksum?.let {
                params.add("checksum: ${it.quoted()}")
            }
            if (dependencies.isNotEmpty()) {
                params.add("dependencies: $dependencies")
            }
            return ".binaryTarget(\n${params.joinToString(",\n").replaceIndent("\t")}\n)"
        }
    }
}

class TargetList : MutableList<Target> by mutableListOf() {

    fun target(
        name: String? = null, path: String? = null, configure: Target.Target.() -> Unit
    ): Target {
        val target = Target.Target(name, path)
        target.configure()
        add(target)
        return target
    }

    fun testTarget(name: String? = null, path: String? = null, configure: Target.TestTarget.() -> Unit = {}): Target {
        val target = Target.TestTarget(name, path)
        target.configure()
        add(target)
        return target
    }

    fun binaryTarget(
        name: String? = null, path: String? = null, configure: Target.BinaryTarget.() -> Unit = {}
    ): Target {
        val target = Target.BinaryTarget(name, path)
        target.configure()
        add(target)
        return target
    }

    fun binaryTarget(
        name: String? = null,
        url: String? = null,
        checksum: String? = null,
        configure: Target.BinaryTarget.() -> Unit = {}
    ): Target {
        val target = Target.BinaryTarget(name, null, url, checksum)
        target.configure()
        add(target)
        return target
    }

    fun systemLibrary(
        name: String? = null,
        path: String? = null,
        pkgConfig: String? = null,
        providers: Target.SystemLibraryTarget.PackageProviderList? = null,
        configure: Target.SystemLibraryTarget.() -> Unit = {}
    ): Target {
        val target = Target.SystemLibraryTarget(name, path, pkgConfig, providers)
        target.configure()
        add(target)
        return target
    }

    fun plugin(
        name: String? = null, configure: Target.PluginTarget.() -> Unit = {}
    ): Target {
        val target = Target.PluginTarget(name, null)
        target.configure()
        add(target)
        return target
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