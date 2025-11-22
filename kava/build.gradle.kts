import java.time.LocalDate

plugins {
    id("maven-publish")
    id("signing")

    alias(libs.plugins.dokka)
}

val projectName = "Kava"
val projectDescription = "Kotlin DSL for building Guice modules"
val projectInceptionYear = "2024"
val githubRepository = "reifiedbeans/kava"
val githubRepositoryUrl = "https://github.com/$githubRepository"

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(libs.guice)

    testImplementation(libs.mockk)
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.platform.launcher)
}

lateinit var mavenPublication: MavenPublication
publishing {
    publications {
        mavenPublication =
            create<MavenPublication>("central") {
                from(components["java"])

                pom {
                    name = projectName
                    description = projectDescription
                    url = githubRepositoryUrl
                    inceptionYear = projectInceptionYear

                    licenses {
                        license {
                            name = "MIT License"
                            url = "https://opensource.org/license/mit"
                        }
                    }

                    developers {
                        developer {
                            name = "Drew Davis"
                            email = "drew@reifiedbeans.dev"
                        }
                    }

                    scm {
                        connection = "scm:git:git://github.com/$githubRepository.git"
                        developerConnection = "scm:git:ssh://github.com:$githubRepository.git"
                        url = githubRepositoryUrl
                    }
                }
            }
    }

    /*
     * Publish to build directory for manual upload
     * since Gradle doesn't support the new Central Portal yet
     * https://github.com/gradle/gradle/issues/28120
     */
    repositories {
        maven {
            url = uri(layout.buildDirectory.dir("repo"))
        }
    }
}

dokka {
    moduleName.set(projectName)

    dokkaSourceSets.main {
        sourceLink {
            localDirectory.set(rootDir)
            remoteUrl("$githubRepositoryUrl/tree/v${project.version}")
            remoteLineSuffix.set("#L")
        }

        externalDocumentationLinks.register("guice") {
            val guiceJavadocUri = "https://google.github.io/guice/api-docs/${libs.versions.guice.get()}/javadoc"
            url(guiceJavadocUri)
            packageListUrl("$guiceJavadocUri/element-list")
        }
    }

    pluginsConfiguration.html {
        footerMessage = "© $copyrightRange Drew Davis"
    }
}

tasks.replace("javadocJar", Jar::class).apply {
    dependsOn(tasks.dokkaGenerateHtml)
    from(layout.buildDirectory.dir("dokka/html"))
    archiveClassifier.set("javadoc")
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(mavenPublication)
}

val copyrightRange: String
    get() {
        val currentYear = LocalDate.now().year.toString()
        return if (projectInceptionYear == currentYear) {
            projectInceptionYear
        } else {
            "$projectInceptionYear–$currentYear"
        }
    }
