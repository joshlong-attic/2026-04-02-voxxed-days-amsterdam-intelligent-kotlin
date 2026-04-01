plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.spring") version "2.2.21"
    id("org.springframework.boot") version "4.0.5"
    id("io.spring.dependency-management") version "1.1.7"
    id("gg.jte.gradle") version "3.2.3"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(24)
    }
}

repositories {
    mavenCentral()
}

dependencies {


//    kotlinx-html = "0.12.0"
//
//[libraries]
//kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version = "1.9.0" }
//kotlinx-html-jvm = { module = "org.jetbrains.kotlinx:kotlinx-html-jvm", version.ref = "kotlinx-html" }
//kotlinx-html-common = { module = "org.jetbrains.kotlinx:kotlinx-html", version.ref = "kotlinx-html" }
//kotlinx-browser = { module = "org.jetbrains.kotlinx:kotlinx-browser", version = "0.5.0" }
//konform = { module = "io.konform:konform", version = "0.11.1" }


//    implementation("jetbrains.kotlinx:kotlinx-html-jvm:0.12.0")

    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.8.0")

//    implementation("jetbrains.kotlinx:kotlinx-browser:0.5.0")

    // everything below is custom
    implementation("gg.jte:jte-kotlin:3.2.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    // everything above is custom

    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-webmvc")
    implementation("gg.jte:jte-spring-boot-starter-4:3.2.3")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("tools.jackson.module:jackson-module-kotlin")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("org.postgresql:postgresql")
    testImplementation("org.springframework.boot:spring-boot-starter-jdbc-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
    }
}

jte {
    generate()
    binaryStaticContent = true
}

tasks.withType<Test> {
    useJUnitPlatform()
}
