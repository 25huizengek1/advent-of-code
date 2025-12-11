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
import nl.bartoostveen.aoc.util.Puzzle
import nl.bartoostveen.aoc.util.Solution
import nl.bartoostveen.aoc.util.printException
import nl.bartoostveen.aoc.util.printMicros
import nl.bartoostveen.aoc.util.splitAtIndex
import java.io.File
import java.time.LocalDateTime
import java.time.Month

val env = System.getenv().toMutableMap()

val solutions: Map<Int, Map<Int, Puzzle.() -> Unit>> = mapOf(
    2025 to mapOf(
        1 to day202501,
        2 to day202502,
        3 to day202503,
        4 to day202504,
        5 to day202505,
        6 to day202506,
        7 to day202507,
        8 to day202508,
        9 to day202509,
        10 to day202510,
        11 to day202511,
        12 to day202512
    )
)

suspend fun main(args: Array<String>) {
    val envFile = File(".env")
    if (envFile.exists()) {
        runCatching {
            for (line in envFile.readLines()) {
                val (rawKey, value) = line.splitAtIndex(line.indexOf('='))
                val key = rawKey.trim()
                if (key.firstOrNull()?.isLetter() != true) continue

                env[key] = value
                println("Loading env var $key")
            }
        }.onFailure {
            println("Note: loading $envFile env file failed! Please check the formatting and try again.")
        }
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

    if (Environment.AOC_RUN_MODE != Environment.RunMode.TEST) {
        Puzzle(inputs).apply {
            printMicros("Solution") {
                solution()
            }
        }.print()
    }

    if (Environment.AOC_RUN_MODE != Environment.RunMode.REGULAR) {
        Environment.AOC_INPUT_CACHE
            .resolve("$fileName.test.txt")
            .takeIf { it.exists() }
            ?.readText()
            ?.takeIf { it.isNotBlank() }
            ?.let {
                println()
                println("Found test inputs, running as well...")
                Puzzle(it).apply {
                    runCatching {
                        printMicros("Test solution") {
                            solution()
                        }
                    }.printException().onFailure {
                        println("Running test solution failed, see stack trace above, not throwing...")
                    }
                }.print()
            }
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

suspend fun fetchInputs(
    year: Int,
    day: Int
) = httpClient.get("/$year/day/$day/input").bodyAsText()
