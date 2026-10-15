import org.jreleaser.model.Active

plugins {
    id("buildlogic.common")
    `maven-publish`
    id("org.jreleaser")
}

dependencies {
    compileOnly(libs.minestom)
    compileOnly(libs.adventure.minimessage)
    compileOnly(libs.kotlinx.coroutines)

    testImplementation(libs.minestom)
    testImplementation(libs.adventure.minimessage)
    testImplementation(libs.bundles.test)
    testImplementation(libs.kotlinx.coroutines)
}

java { withSourcesJar() }

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            artifactId = "kotstom"

            pom {
                name = "KotStom"
                description = "Kotlin extensions for Minestom"
                url = "https://github.com/oglassdev/KotStom"

                licenses {
                    license {
                        name = "MIT License"
                        url = "http://www.opensource.org/licenses/mit-license.php"
                    }
                }

                developers {
                    developer {
                        id = "oglass"
                        name = "oglass"
                        email = "him@oglass.dev"
                    }
                }

                issueManagement {
                    system = "GitHub"
                    url = "https://github.com/oglassdev/KotStom/issues"
                }

                scm {
                    connection = "scm:git:git:github.com/oglassdev/KotStom.git"
                    developerConnection = "scm:git:https://github.com/oglassdev/KotStom.git"
                    url = "https://github.com/oglassdev/KotStom"
                }
            }
        }

        repositories {
            maven {
                url =
                    rootProject.layout.buildDirectory
                        .dir("staging-deploy")
                        .get()
                        .asFile
                        .toURI()
            }
        }
    }
}

tasks.assemble {
    doFirst {
        mkdir(
            rootProject.layout.buildDirectory
                .dir("jreleaser"),
        )
    }
}

jreleaser {
    signing {
        active = Active.ALWAYS
        pgp {
            armored = true
        }
    }
    release {
        github {
            prerelease {
                pattern = ".*-(alpha|beta|rc).*"
            }
            changelog {
                formatted = Active.ALWAYS
                preset = "conventional-commits"
            }
        }
    }
    deploy {
        maven {
            mavenCentral {
                create("sonatype") {
                    active = Active.ALWAYS
                    url = "https://central.sonatype.com/api/v1/publisher"
                    stagingRepository("build/staging-deploy")
                }
            }
        }
    }
}
