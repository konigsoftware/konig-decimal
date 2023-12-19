plugins {
    id("org.jetbrains.kotlin.jvm") version "1.6.21"
    id("maven-publish")
    id("com.adarshr.test-logger") version "3.2.0"
    id("org.jetbrains.dokka") version "1.9.0"
    signing

    `java-library`
}

repositories {
    mavenCentral()
}

java {
    withSourcesJar()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.21")

    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.12.7")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.0")
}

tasks.test {
    useJUnitPlatform()
}

tasks.create<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    includeEmptyDirs = false
    from(tasks.named("dokkaHtml"))
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = rootProject.group as String
            artifactId = "konig-decimal"
            version = rootProject.version as String

            from(components["java"])

            artifact(tasks.named("javadocJar"))

            pom {
                url.set("https://github.com/konigsoftware/konig-decimal")

                name.set("Konig Decimal")
                description.set("Safe and developer friendly arithmetic in idiomatic Kotlin")

                scm {
                    connection.set("scm:git:https://github.com/konigsoftware/konig-decimal.git")
                    developerConnection.set("scm:git:git@github.com:konigsoftware/konig-decimal.git")
                    url.set("https://github.com/konigsoftware/konig-decimal")
                }

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/license/mit/")
                    }
                }

                developers {
                    developer {
                        id.set("konigsoftware.com")
                        name.set("Konig Decimal Contributors")
                        email.set("reidbuzby@gmail.com")
                        url.set("https://konigsoftware.com")
                        organization.set("Konig Software")
                        organizationUrl.set("https://konigsoftware.com")
                    }
                }
            }
        }
    }
}


signing {
    useInMemoryPgpKeys(System.getenv("GPG_PRIVATE_KEY"), System.getenv("GPG_PASSPHRASE"))
    sign(publishing.publications["mavenJava"])
}

tasks.withType<Sign> {
    onlyIf { System.getenv("GPG_PRIVATE_KEY") != null }
}
