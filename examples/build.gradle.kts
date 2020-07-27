plugins {
    kotlin("jvm") version "1.3.72"
}

group = "com.verik"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation(files("../common/build/libs/verik-common-1.0-SNAPSHOT.jar"))
}

tasks.compileKotlin {
    kotlinOptions.jvmTarget = "1.8"
}
