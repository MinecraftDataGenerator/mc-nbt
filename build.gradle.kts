/*
 * Copyright (C) 2025 Paul Dietrich
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import dev.karmakrafts.conventions.apache2License
import dev.karmakrafts.conventions.setRepository
import java.net.URI
import java.time.Duration

plugins {
    alias(libs.plugins.karmaConventions)
    alias(libs.plugins.gradleNexus)
    alias(libs.plugins.dokka)
    `maven-publish`
    signing
}

group = "de.dietrichpaul.mcnbt"
version = libs.versions.mcnbt.get()

subprojects {
    apply<MavenPublishPlugin>()
    apply<SigningPlugin>()

    group = rootProject.group
    version = rootProject.version

    publishing {
        apache2License()
        setRepository("github.com", "MinecraftDataGenerator/mc-nbt")
    }
}

tasks {
    @Suppress("UNUSED")
    val printVersion by registering {
        group = "ci"
        doLast {
            println(project.version)
        }
    }
}

nexusPublishing {
    System.getenv("SONATYPE_USERNAME")?.let { userName ->
        repositories {
            sonatype {
                nexusUrl.set(URI.create("https://ossrh-staging-api.central.sonatype.com/service/local/"))
                snapshotRepositoryUrl.set(URI.create("https://central.sonatype.com/repository/maven-snapshots/"))
                username.set(userName)
                password.set(System.getenv("SONATYPE_PASSWORD"))
            }
        }
    }
    connectTimeout = Duration.ofSeconds(30)
    clientTimeout = Duration.ofMinutes(5)
}