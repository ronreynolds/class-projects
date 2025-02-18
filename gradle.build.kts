plugins {
    id("java")
}

group   = "com.ronreynolds"
version = "0.1.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenLocal()
    mavenCentral()
}

tasks.withType<Test> {
    useJUnitPlatform()
}