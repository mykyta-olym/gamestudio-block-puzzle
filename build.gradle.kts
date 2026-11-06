plugins {
    id("java")
    id("application")
    id("org.springframework.boot") version "2.5.6"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    id("pmd")
    id("dev.anies.gradle.template") version "0.0.2"
}

group = "sk.tuke.gamestudio"
version = "1.0"

repositories {
    mavenCentral()
    maven(url = "https://repo.kpi.fei.tuke.sk/repository/maven-public")
}

java {
    sourceCompatibility = JavaVersion.VERSION_17 // Updated Java version
    targetCompatibility = JavaVersion.VERSION_17 // Updated Java version
}

tasks.withType<JavaCompile> {
    options.compilerArgs.add("-parameters")
    options.encoding = "UTF-8"
}
tasks.test {
    useJUnitPlatform()
}

pmd {
    toolVersion = "6.18.0"
    isConsoleOutput = true
    ruleSets = emptyList()
    ruleSetFiles = files("src/main/resources/pmd-ruleset.xml")
}

buildscript {
    repositories {
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
    }
    dependencies {
        classpath("dev.anies.gradle.template:gradle-template:0.0.2")
    }
}

apply(plugin = "dev.anies.gradle.template")

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.postgresql:postgresql:42.2.18")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.7.0")

    testImplementation("org.mockito:mockito-core:3.3.3")
    testImplementation("org.mockito:mockito-junit-jupiter:3.3.3")

    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation("org.mockito:mockito-inline:3.4.0")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.6")

    implementation ("io.springfox:springfox-boot-starter:3.0.0")


}

application {
    mainClass.set("sk.tuke.gamestudio.BlockPuzzleGame")
}
