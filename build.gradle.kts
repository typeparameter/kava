import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin)

    alias(libs.plugins.detekt)
    alias(libs.plugins.ktlint)
}

allprojects {
    group = "dev.reifiedbeans"
    version = "1.0.0"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "jacoco")
    apply(plugin = "org.jetbrains.kotlin.jvm")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    java {
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }

    ktlint {
        version = "1.8.0"
    }

    tasks.named<Test>("test") {
        useJUnitPlatform()
        finalizedBy("jacocoTestReport")
    }

    tasks.named<JacocoReport>("jacocoTestReport") {
        reports {
            xml.required = true
        }
    }
}
