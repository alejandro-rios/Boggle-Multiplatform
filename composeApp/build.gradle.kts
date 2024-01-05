import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    alias(libs.plugins.multiplatform)
    alias(libs.plugins.compose)
    alias(libs.plugins.cocoapods)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlinx.serialization)
}

@OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
kotlin {
    targetHierarchy.default()
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "11"
            }
        }
    }

    jvm("desktop")

    js {
        browser()
        binaries.executable()
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    cocoapods {
        version = "1.0.0"
        summary = "Compose application framework"
        homepage = "empty"
        ios.deploymentTarget = "11.0"
        podfile = project.file("../iosApp/Podfile")
        framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting

        all {
            languageSettings {
                optIn("org.jetbrains.compose.resources.ExperimentalResourceApi")
            }
        }
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.material3)
            implementation(compose.ui)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            api(libs.moko.mvvm.core)
            api(libs.moko.mvvm.compose)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.core)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.serialization.json)
            implementation(libs.kotlinx.serialization.json)

        }

        androidMain.dependencies {
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activityCompose)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.compose.uitooling)
            implementation(libs.ktor.client.okhttp)
        }

        desktopMain.dependencies {
            implementation(compose.desktop.common)
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.java)
        }

        jsMain.dependencies {
            implementation(compose.html.core)
            implementation(libs.ktor.client.js)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = "com.alejandrorios.bogglemultiplatform"
    compileSdk = 34

    defaultConfig {
        minSdk = 23
        targetSdk = 34

        applicationId = "com.alejandrorios.bogglemultiplatform.androidApp"
        versionCode = 1
        versionName = "1.0.0"
    }
    sourceSets["main"].apply {
        manifest.srcFile("src/androidMain/AndroidManifest.xml")
        resources.srcDirs("src/commonMain/resources")
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    packaging {
        resources.excludes.add("META-INF/**")
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)

            packageName = "com.alejandrorios.bogglemultiplatform.desktopApp"
            packageVersion = "1.0.0"
        }
    }
}

compose.experimental {
    web.application {}
}
