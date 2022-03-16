import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.6.10"
    id("org.jetbrains.dokka") version "1.6.10"
    `maven-publish`
    signing
}

group = "com.thizzer.kt-swift-package"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "16"
}

//

val dokkaHtml by tasks.getting(org.jetbrains.dokka.gradle.DokkaTask::class)

val javadocJar: TaskProvider<Jar> by tasks.registering(Jar::class) {
    dependsOn(dokkaHtml)
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputDirectory)
}

//

var mavenCentralUsername: String? = System.getenv("MAVEN_CENTRAL_USERNAME")
var mavenCentralPassword: String? = System.getenv("MAVEN_CENTRAL_TOKEN")

publishing {
    repositories {
        maven {
            name = "mavenCentralDefault"

            url = if (version.toString().endsWith("SNAPSHOT")) uri("https://oss.sonatype.org/content/repositories/snapshots/") else uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = mavenCentralUsername
                password = mavenCentralPassword
            }
        }
        maven {
            name = "mavenCentralSnapshot"

            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
            credentials {
                username = mavenCentralUsername
                password = mavenCentralPassword
            }
        }
        maven {
            name = "mavenCentralRelease"

            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = mavenCentralUsername
                password = mavenCentralPassword
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            artifactId = "kt-swift-package"
            from(components["kotlin"])
            artifact(javadocJar)
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }

            pom {
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/Thizzer/kt-swift-package/blob/master/LICENSE")
                    }
                    organization {
                        url.set("https://www.thizzer.com")
                        name.set("Thizzer")
                    }
                    developers {
                        developer {
                            name.set("Thys ten Veldhuis")
                            email.set("t.tenveldhuis@gmail.com")
                            organization.set("Thizzer")
                            organizationUrl.set("https://www.thizzer.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/Thizzer/kt-swift-package.git")
                        developerConnection.set("scm:git:ssh://github.com:Thizzer/kt-swift-package.git")
                        url.set("https://github.com/Thizzer/kt-swift-package/tree/master")
                    }
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}