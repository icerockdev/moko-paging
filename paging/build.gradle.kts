/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */

import java.util.Base64

plugins {
    plugin(Deps.Plugins.androidLibrary)
    plugin(Deps.Plugins.kotlinMultiplatform)
    plugin(Deps.Plugins.kotlinAndroidExtensions)
    plugin(Deps.Plugins.mobileMultiplatform)
    plugin(Deps.Plugins.mavenPublish)
    plugin(Deps.Plugins.signing)
}

group = "dev.icerock.moko"
version = Deps.mokoPagingVersion

dependencies {
    commonMainImplementation(Deps.Libs.MultiPlatform.coroutines)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmLiveData.common)
    commonMainApi(Deps.Libs.MultiPlatform.mokoMvvmState.common)

    androidMainImplementation(Deps.Libs.Android.appCompat)

    commonTestImplementation(Deps.Libs.Tests.kotlinTestJUnit)
    androidTestImplementation(Deps.Libs.Tests.androidCoreTesting)
    androidTestImplementation(Deps.Libs.MultiPlatform.ktorClient.android!!)
    commonTestImplementation(Deps.Libs.MultiPlatform.ktorClient.common)
    iosX64TestImplementation(Deps.Libs.MultiPlatform.ktorClient.iosX64!!)
    iosX64TestImplementation(Deps.Libs.MultiPlatform.coroutines) {
        isForce = true
    }
}
val javadocJar by tasks.registering(Jar::class) {
    archiveClassifier.set("javadoc")
}

publishing {
    repositories.maven("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/") {
        name = "OSSRH"

        credentials {
            username = System.getenv("OSSRH_USER")
            password = System.getenv("OSSRH_KEY")
        }
    }

    publications.withType<MavenPublication> {
        // Stub javadoc.jar artifact
        artifact(javadocJar.get())

        // Provide artifacts information requited by Maven Central
        pom {
            name.set("MOKO paging")
            description.set("Pagination logic in common code for mobile (android & ios) Kotlin Multiplatform development")
            url.set("https://github.com/icerockdev/moko-paging")
            licenses {
                license {
                    url.set("https://github.com/icerockdev/moko-paging/blob/master/LICENSE.md")
                }
            }

            developers {
                developer {
                    id.set("Alex009")
                    name.set("Aleksey Mikhailov")
                    email.set("aleksey.mikhailov@icerockdev.com")
                }
                developer {
                    id.set("Tetraquark")
                    name.set("Vladislav Areshkin")
                    email.set("vareshkin@icerockdev.com")
                }
            }

            scm {
                connection.set("scm:git:ssh://github.com/icerockdev/moko-paging.git")
                developerConnection.set("scm:git:ssh://github.com/icerockdev/moko-paging.git")
                url.set("https://github.com/icerockdev/moko-paging")
            }
        }
    }
}

signing {
    val signingKeyId: String? = System.getenv("SIGNING_KEY_ID")
    val signingPassword: String? = System.getenv("SIGNING_PASSWORD")
    val signingKey: String? = System.getenv("SIGNING_KEY")?.let { base64Key ->
        String(Base64.getDecoder().decode(base64Key))
    }
    if (signingKeyId != null) {
        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
        sign(publishing.publications)
    }
}
