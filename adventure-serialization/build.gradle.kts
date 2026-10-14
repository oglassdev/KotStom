plugins {
    id("buildlogic.common")
    `maven-publish`
}

dependencies {
    compileOnly(libs.minestom)
    api(libs.kotlinx.serialization.core)

    testImplementation(libs.minestom)
    testImplementation(libs.bundles.test)
}

publishing {
    publications {
        create<MavenPublication>("library") {
            from(components["java"])
            artifactId = "kotstom-adventure-serialization"

            pom {
                name = "KotStom Adventure Serialization"
                description = "Adventure NBT kotlinx.serialization format"
                url = "https://github.com/bladehuntmc/KotStom"

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
                    url = "https://github.com/bladehuntmc/KotStom/issues"
                }

                scm {
                    connection = "scm:git:git:github.com/bladehuntmc/KotStom.git"
                    developerConnection = "scm:git:https://github.com/bladehuntmc/KotStom.git"
                    url = "https://github.com/bladehuntmc/KotStom"
                }
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
