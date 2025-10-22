plugins {
    id("java")
    id("application")
    id("eclipse")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    implementation("net.sourceforge.plantuml:plantuml:1.2024.5")
    implementation("com.aayushatharva.brotli4j:brotli4j:1.16.0")

    runtimeOnly("com.aayushatharva.brotli4j:native-linux-x86_64:1.16.0")
    runtimeOnly("com.aayushatharva.brotli4j:native-windows-x86_64:1.16.0")
    // runtimeOnly("com.aayushatharva.brotli4j:native-osx-x86_64:1.16.0")
}


application {
    mainClass.set("com.plantuml.stdlibencoder.v2.Main")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to application.mainClass.get()
        )
    }
}

