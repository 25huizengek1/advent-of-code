package nl.bartoostveen.aoc.runner

import java.io.File
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

private val DEFAULT_AOC_CACHE = File("/home/bart/advent-of-code/.cache/aoc").also { it.mkdirs() }

object Environment {
    private val id: (String) -> String = { it }
    private val int: (String) -> Int = { it.toInt() }

    private val required = { error("Property required, but not given!") }

    private fun <T : Any> variable(
        default: () -> T,
        mapper: (String) -> T,
        envName: String? = null
    ) = PropertyDelegateProvider<Any?, ReadOnlyProperty<Any?, T>> { _, property ->
        val name = envName ?: property.name
        ReadOnlyProperty { _, _ ->
            runCatching {
                env[name]?.let { mapper(it) } ?: default()
            }.getOrElse { th ->
                throw RuntimeException("Property $name in Configuration had an error mapping its value", th)
            }
        }
    }

    private fun variable(envName: String? = null) = variable(
        default = required,
        mapper = id,
        envName = envName
    )

    val AOC_TOKEN by variable()
    val AOC_INPUT_CACHE by variable({ DEFAULT_AOC_CACHE }, { File(it).also(File::mkdirs) })
    val AOC_WARMUPS by variable({ 0 }, int)
}