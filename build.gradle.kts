import java.util.concurrent.Executors
import java.util.concurrent.Future

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
    // Gradle 9 no longer puts the JUnit Platform launcher on the test runtime
    // classpath implicitly; without this line `gradlew test` cannot start.
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.7.1")
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

// Task to minify JavaScript files using Google Closure Compiler (parallel)
abstract class MinifyJavaScriptTask @Inject constructor(
    private val execOperations: ExecOperations
) : DefaultTask() {

    @get:InputFiles
    abstract val inputDir: DirectoryProperty

    @get:OutputDirectory
    abstract val outputDir: DirectoryProperty

    @get:InputFiles
    abstract val closureClasspath: ConfigurableFileCollection

    @get:Input
    abstract val maxParallel: Property<Int>

    @TaskAction
    fun minify() {
        val inDir = inputDir.get().asFile
        val outDir = outputDir.get().asFile
        outDir.mkdirs()

        val jsFiles = inDir.listFiles { f -> f.extension == "js" }?.sorted() ?: emptyList()
        val parallelism = maxParallel.get()
        println("Minifying ${jsFiles.size} JavaScript files with Google Closure Compiler (parallelism=$parallelism)...")
        println("Input:  ${inDir.absolutePath}")
        println("Output: ${outDir.absolutePath}")

        val pool = Executors.newFixedThreadPool(parallelism)
        val futures: List<Future<*>> = jsFiles.map { jsFile ->
            pool.submit {
                val outFile = File(outDir, "${jsFile.nameWithoutExtension}.min.js")
                execOperations.javaexec {
                    classpath = closureClasspath
                    mainClass.set("com.google.javascript.jscomp.CommandLineRunner")
                    jvmArgs("-Xmx2g", "-XX:+UseParallelGC")
                    args(
                        "--js", jsFile.absolutePath,
                        "--js_output_file", outFile.absolutePath,
                        "--compilation_level", "SIMPLE",
                        "--language_out", "ECMASCRIPT_2015",
                        "--warning_level", "QUIET",
                        "--rewrite_polyfills", "false"
                    )
                }
            }
        }

        // Wait for all to complete
        futures.forEach { future -> future.get() }
        pool.shutdown()

        var totalOriginal = 0L
        var totalMinified = 0L
        jsFiles.forEach { jsFile ->
            val outFile = File(outDir, "${jsFile.nameWithoutExtension}.min.js")
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

        // The JSON data bundles (JsonBuilder output) are already compact and
        // must stay byte-exact, so they are copied, not minified. Keeping them
        // in the same output directory means whatever publishes the .min.js
        // files to the site carries the .json files along.
        val jsonFiles = inDir.listFiles { f -> f.extension == "json" }?.sorted() ?: emptyList()
        jsonFiles.forEach { j -> j.copyTo(File(outDir, j.name), overwrite = true) }
        println("Copied ${jsonFiles.size} JSON data bundle(s) unchanged.")
    }
}

tasks.register<MinifyJavaScriptTask>("minifyJavaScript") {
    group = "application"
    description = "Minifies JS files in output-js/ using Google Closure Compiler (parallel)"
    dependsOn("runJs")
    mustRunAfter("runJs")
    inputDir.set(file("output-js"))
    outputDir.set(file("output-js-min"))
    closureClasspath.from(closureConfig)
    maxParallel.set(Runtime.getRuntime().availableProcessors().coerceAtMost(8))
}

tasks.named<JavaExec>("run") {
    dependsOn("minifyJavaScript")
    mustRunAfter("minifyJavaScript")
}

tasks.named<Delete>("clean") {
    delete("output")
    delete("output-js")
    delete("output-js-min")
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

