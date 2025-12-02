package nl.bartoostveen.aoc.runner

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.userAgent
import nl.bartoostveen.aoc.days.*
import nl.bartoostveen.aoc.util.printException
import nl.bartoostveen.aoc.util.splitAtIndex
import java.io.File
import java.time.LocalDateTime
import java.time.Month
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.time.measureTimedValue

val env = System.getenv().toMutableMap()

val solutions: Map<Int, Map<Int, Puzzle.() -> Unit>> = mapOf(
    2025 to mapOf(
        1 to day202501,
        2 to day202502,
        3 to day202503,
    ),
)

data class Puzzle(
    val raw: String
) {
    val lines: List<String> by lazy {
        raw.lines().filter { it.isNotBlank() }.map { it.trim() }
    }

    private val values = mutableListOf<Any?>()

    private fun <T : Any> part() = object : ReadWriteProperty<Puzzle, T?> {
        private val index = values.size

        init {
            values.add(null)
        }

        @Suppress("UNCHECKED_CAST")
        override fun getValue(thisRef: Puzzle, property: KProperty<*>) = values[index] as? T

        override fun setValue(
            thisRef: Puzzle,
            property: KProperty<*>,
            value: T?
        ) {
            values[index] = value
        }
    }

    var partOne: Any? by part()
    var partTwo: Any? by part()

    fun print() {
        println("Solved AOC puzzle:")
        values.forEachIndexed { i, value ->
            println("Part ${i + 1}: ${value ?: "Unimplemented"}")
        }
    }
}

typealias Solution = Puzzle.() -> Unit

// marker function
fun puzzle(block: Solution) = block

inline fun <T> printMicros(
    name: String? = null,
    crossinline block: () -> T
) = measureTimedValue(block)
    .also { println("${name?.plus(" ") ?: ""}took ${it.duration.inWholeMicroseconds}μs") }
    .value

suspend fun main(args: Array<String>) {
    val envFile = File(args.firstOrNull() ?: ".env")
    if (envFile.exists()) runCatching {
        envFile.readLines().map { line ->
            val (key, value) = line.splitAtIndex(line.indexOf('='))
            env[key] = value
            println("Loading env var $key")
        }
    }.onFailure {
        println("Note: loading $envFile env file failed! Please check the formatting and try again.")
    }

    val now = LocalDateTime.now()
    val year = args.getOrNull(0)?.toIntOrNull()
        ?: if (now.month == Month.DECEMBER) now.year else now.year - 1
    val day = args.getOrNull(1)?.toIntOrNull()
        ?: if (now.month == Month.DECEMBER) now.dayOfMonth else 1

    val solution: Solution = solutions[year]?.get(day)
        ?: return println("Day $day of $year not yet implemented!")

    val fileName = "$year${day.toString().padStart(2, '0')}"
    val inputs = runCatching {
        val cacheFile = Environment.AOC_INPUT_CACHE
            .resolve("$fileName.txt")

        cacheFile.takeIf { it.exists() }?.readText()?.takeIf { it.isNotBlank() }
            ?: fetchInputs(year, day).also(cacheFile::writeText)
    }.printException().getOrNull()
        ?: return println("Cannot get inputs, stopping...")

    println("Running day $day of $year...")

    if (Environment.AOC_WARMUPS != 0) {
        println("Warming JVM up with ${Environment.AOC_WARMUPS} warmups")
        repeat(Environment.AOC_WARMUPS) {
            Puzzle(inputs).apply(solution)
        }
    }

    Puzzle(inputs).apply {
        printMicros("Solution") {
            solution()
        }
    }.print()

    Environment.AOC_INPUT_CACHE
        .resolve("$fileName.test.txt")
        .takeIf { it.exists() }
        ?.readText()
        ?.takeIf { it.isNotBlank() }
        ?.let {
            println()
            println("Found test inputs, running as well...")
            Puzzle(it).apply {
                printMicros("Test solution") {
                    solution()
                }
            }.print()
        }
}

val httpClient = HttpClient(CIO) {
    install(Logging) {
        level = LogLevel.INFO
        sanitizeHeader { it.equals("cookie", ignoreCase = true) }
    }

    defaultRequest {
        url("https://adventofcode.com")
        cookie("session", Environment.AOC_TOKEN)
        userAgent("AoC runner Bart Oostveen (github:25huizengek1)")
    }
}

suspend fun fetchInputs(year: Int, day: Int) = httpClient.get("/$year/day/$day/input").bodyAsText()
