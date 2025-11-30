package nl.bartoostveen.aoc.runner

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.cookie
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.userAgent
import nl.bartoostveen.aoc.days.day202501
import nl.bartoostveen.aoc.util.printException
import nl.bartoostveen.aoc.util.splitAtIndex
import java.io.File
import java.time.LocalDateTime
import java.time.Month

val env = System.getenv().toMutableMap()

val solutions = mapOf(
    2025 to mapOf(
        1 to ::day202501,
    ),
)

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

    val solution = solutions[year]?.get(day) ?: return println("Day $day of $year not yet implemented!")

    val inputs = runCatching {
        val cacheFile = Environment.AOC_INPUT_CACHE
            .resolve("$year${day.toString().padStart(2, '0')}.txt")

        cacheFile.takeIf { it.exists() }?.readText()?.takeIf { it.isNotBlank() }
            ?: fetchInputs(year, day).also(cacheFile::writeText)
    }.printException().getOrNull() ?: return println("Cannot get inputs, stopping...")

    println("Running day $day of $year...")
    solution(inputs)
}

val httpClient = HttpClient(CIO) {
    install(ContentNegotiation) {}
    install(Logging) {
        level = LogLevel.INFO
        sanitizeHeader { it.equals("cookie", ignoreCase = true) }
    }

    defaultRequest {
        url("https://adventofcode.com")
        cookie("session", Environment.AOC_TOKEN)
        userAgent("AoC solver Bart Oostveen (github:25huizengek1)")
    }
}

suspend fun fetchInputs(year: Int, day: Int) = httpClient.get("/$year/day/$day/input").bodyAsText()
