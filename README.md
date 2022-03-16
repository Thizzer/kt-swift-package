# Kotlin Swift Package (DSL)

Simple Kotlin library for generating Package.swift file content using DSL.

## Example

```kotlin
val myLibraryPackage = swiftPackage {
    name = "MyLibrary"
    platforms {
        macOS(SupportedPlatform.MacOSVersion.v10_14)
        iOS(SupportedPlatform.IOSVersion.v13)
        tvOS(SupportedPlatform.TVOSVersion.v13)
    }
    dependencies {
        
    }
    targets {
        target {
            name = "MyLibrary"
            exclude("instructions.md")
            resources {
                process("text.txt")
                process("example.png")
                copy("settings.plist")
            }
        }
        binaryTarget {
            name = "SomeRemoteBinaryPackage"
            url = "https://url/to/some/remote/binary/package.zip"
            checksum = "The checksum of the XCFramework inside the ZIP archive."
        }
        binaryTarget {
            name = "SomeLocalBinaryPackage"
            path = "path/to/some.xcframework"
        }
        testTarget {
            name = "MyLibraryTests"
            dependencies("MyLibrary")
        }
    }
}

val fileContent = myLibraryPackage.toPackageFileString()
```