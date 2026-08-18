import java.util.Properties

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.compose)
    id("maven-publish")
    id("signing")
}

android {
    namespace = "com.chris.threed"
    compileSdk = 37

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    publishing {
        singleVariant("release") {
            withSourcesJar()
            withJavadocJar()
        }
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.ui.tooling.preview)
    debugImplementation(libs.androidx.ui.tooling)
}


publishing {
    repositories {
        maven {
            name = "LocalStaging"
            // This tells Gradle to build the final bundle inside your project's /build/ folder
            url = uri(layout.buildDirectory.dir("staging-repo"))
        }
    }
    publications {
        register<MavenPublication>("release") {
            groupId = "io.github.leochrish"
            artifactId = "3D"
            version = "1.0.0"

            afterEvaluate {
                from(components["release"])
            }

            pom {
                name.set("ThreeD Compose")
                description.set("A Jetpack Compose library for creating realistic 3D extrusion effects with soft shadows and colored glows.")
                url.set("https://github.com/leochrish/Three_Dimension")

                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("leochrish")
                        name.set("Leoni Christopher")
                        email.set("leoleeu14a1352@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:github.com/leochrish/Three_Dimension.git")
                    developerConnection.set("scm:git:ssh://github.com/leochrish/Three_Dimension.git")
                    url.set("https://github.com/leochrish/Three_Dimension/tree/main")
                }
            }
        }
    }
}

signing {
    val isCi = System.getenv("CI") == "true"

    // 1. ALWAYS bypass Java BouncyCastle and use the native OS terminal GPG
    useGpgCmd()

    if (isCi) {
        // We are on GitHub: The key was just imported into Ubuntu's native keyring
        project.extra["signing.gnupg.keyName"] = System.getenv("GPG_KEY_ID")
        project.extra["signing.gnupg.passphrase"] = System.getenv("GPG_PASSWORD")
    } else {
        // We are on your Mac: Read from local.properties
        val localProperties = Properties()
        val propsFile = rootProject.file("local.properties")

        if (propsFile.exists()) {
            propsFile.inputStream().use { localProperties.load(it) }
            val keyId = localProperties.getProperty("signing.keyId")
            val password = localProperties.getProperty("signing.password")

            if (keyId != null && password != null) {
                project.extra["signing.gnupg.executable"] =
                    "/opt/homebrew/bin/gpg" // Keep your Mac path
                project.extra["signing.gnupg.keyName"] = keyId
                project.extra["signing.gnupg.passphrase"] = password
            }
        }
    }

    sign(publishing.publications["release"])
}
