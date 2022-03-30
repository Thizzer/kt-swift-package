# Kotlin Swift Package (DSL)

Simple Kotlin library for generating Package.swift.

### Maven

```xml
<dependency>
	<groupId>com.thizzer.kt-swift-package</groupId>
	<artifactId>kt-swift-package</artifactId>
	<version>1.0.1</version>
</dependency>
```

### Gradle

#### Groovy

```gradle
implementation group: 'com.thizzer.kt-swift-package', name: 'kt-swift-package', version: '1.0.1'
```

#### Kotlin

```kotlin
implementation("com.thizzer.kt-swift-package:kt-swift-package:1.0.1")
```

## Usage

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