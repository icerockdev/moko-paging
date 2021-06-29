/*
 * Copyright 2020 IceRock MAG Inc. Use of this source code is governed by the Apache 2.0 license.
 */
import org.gradle.api.internal.artifacts.DefaultModuleVersionSelector

buildscript {
    repositories {
        mavenCentral()
        google()
        gradlePluginPortal()
    }

    dependencies {
        classpath("dev.icerock.moko:units-generator:0.6.0")

        classpath(":paging-build-logic")
    }
}

allprojects {
    plugins.withId("org.gradle.maven-publish") {
        group = "dev.icerock.moko"
        version = libs.versions.mokoPagingVersion.get()
    }
    configurations.configureEach {
        resolutionStrategy {
            val coroutines: MinimalExternalModuleDependency = rootProject.libs.coroutines.get()
            val forcedCoroutines: ModuleVersionSelector = DefaultModuleVersionSelector.newSelector(
                coroutines.module,
                coroutines.versionConstraint.requiredVersion
            )
            force(forcedCoroutines)
        }
    }
}

tasks.register("clean", Delete::class).configure {
    delete(rootProject.buildDir)
}
