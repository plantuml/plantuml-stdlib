plugins {
    id("java")
    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("eclipse")
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    implementation("com.beust:jcommander:1.81")
    implementation("org.fusesource.jansi:jansi:2.4.0")
    implementation("com.google.code.gson:gson:2.8.8") 
    implementation("org.apache.commons:commons-text:1.9")
       
}

application {
    mainClass.set("com.pdiff.Main")
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

