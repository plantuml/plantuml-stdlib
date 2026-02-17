plugins {
    id("java")
    id("application")
    id("eclipse")
}

repositories {
    mavenCentral()
}

// Configuration for Google Closure Compiler
val closureConfig by configurations.creating

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.7.1")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.7.1")
    implementation("net.sourceforge.plantuml:plantuml:1.2024.5")
    implementation("com.aayushatharva.brotli4j:brotli4j:1.16.0")

    runtimeOnly("com.aayushatharva.brotli4j:native-linux-x86_64:1.16.0")
    runtimeOnly("com.aayushatharva.brotli4j:native-windows-x86_64:1.16.0")
    runtimeOnly("com.aayushatharva.brotli4j:native-windows-aarch64:1.16.0")
    // runtimeOnly("com.aayushatharva.brotli4j:native-osx-x86_64:1.16.0")

    closureConfig("com.google.javascript:closure-compiler:v20250820")
}


application {
    mainClass.set("com.plantuml.stdlibencoder.spm.MainSpm")
}

tasks.register<JavaExec>("runSpm") {
    group = "application"
    description = "Run MainSpm"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.plantuml.stdlibencoder.spm.MainSpm")
}

tasks.register<JavaExec>("runJs") {
    group = "application"
    description = "Run MainJs"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("com.plantuml.stdlibencoder.js.MainJs")
}

// Task to minify JavaScript files using Google Closure Compiler
abstract class MinifyJavaScriptTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:InputFiles
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputFiles
    abstract val closureClasspath: ConfigurableFileCollection

    @TaskAction
    fun minify() {
        val inDir = inputDir.get().asFile
        val outDir = outputDir.get().asFile
        outDir.mkdirs()

        println("Minifying JavaScript files with Google Closure Compiler...")
        println("Input:  ${inDir.absolutePath}")
        println("Output: ${outDir.absolutePath}")

        var totalOriginal = 0L
        var totalMinified = 0L

        inDir.listFiles { f -> f.extension == "js" }?.sorted()?.forEach { jsFile ->
            val outFile = File(outDir, "${jsFile.nameWithoutExtension}.min.js")
            execOperations.javaexec {
                classpath = closureClasspath
                mainClass.set("com.google.javascript.jscomp.CommandLineRunner")
                jvmArgs("-Xmx4g", "-XX:+UseParallelGC")
                args(
                    "--js", jsFile.absolutePath,
                    "--js_output_file", outFile.absolutePath,
                    "--compilation_level", "SIMPLE",
                    "--language_out", "ECMASCRIPT_2015",
                    "--warning_level", "QUIET",
                    "--rewrite_polyfills", "false"
                )
            }
            val orig = jsFile.length()
            val mini = outFile.length()
            totalOriginal += orig
            totalMinified += mini
            println("  ${jsFile.name} -> ${outFile.name}  (${orig / 1024} KB -> ${mini / 1024} KB)")
        }

        val ratio = if (totalOriginal > 0) (100 - (totalMinified * 100 / totalOriginal)) else 0
        println("")
        println("Google Closure Compiler minification complete!")
        println("  Total original:  ${totalOriginal / 1024} KB")
        println("  Total minified:  ${totalMinified / 1024} KB")
        println("  Reduction: $ratio%")
    }
}

tasks.register<MinifyJavaScriptTask>("minifyJavaScript") {
    group = "application"
    description = "Minifies JS files in output-js/ using Google Closure Compiler"
    dependsOn("runJs")
    mustRunAfter("runJs")
    inputDir.set(file("output-js"))
    outputDir.set(file("output-js-min"))
    closureClasspath.from(closureConfig)
}

tasks.named<JavaExec>("run") {
    dependsOn("minifyJavaScript")
    mustRunAfter("minifyJavaScript")
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

