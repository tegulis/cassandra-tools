plugins {
    id("application")
    id("antlr")
}

group = "net.tegulis.cassandra"
version = "0.1"

application {
    mainClass = "net.tegulis.cassandra.Main"
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.antlr:antlr4:4.13.1")
    antlr("org.antlr:antlr4:4.13.1")
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.generateGrammarSource {
    // Workaround for Lexer/Parser ordering. See: https://github.com/antlr/antlr4/issues/2335#issuecomment-886043863
    val file = File(projectDir, "build/generated-src/antlr/main/cql")
    doFirst {
        file.mkdirs()
    }
    arguments = arguments + listOf("-lib", file.absolutePath, "-visitor", "-long-messages")
}

tasks.test {
    useJUnitPlatform()
}

tasks.register("flatJar", Jar::class) {
    group = "distribution"
    manifest {
        attributes["Main-Class"] = application.mainClass
    }
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
    dependsOn(configurations.runtimeClasspath)
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    with(tasks.jar.get())
    destinationDirectory = file("build/distributions/flat")
}
