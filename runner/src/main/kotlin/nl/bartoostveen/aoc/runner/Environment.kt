package nl.bartoostveen.aoc.runner

import java.io.File
import kotlin.properties.PropertyDelegateProvider
import kotlin.properties.ReadOnlyProperty

object Environment {
    private val id: (String) -> String = { it }
    private val int: (String) -> Int = { it.toInt() }
    private val boolean: (String) -> Boolean = {
        when (it) {
            "1", "true" -> true
            "0", "false" -> false
            else -> error("Invalid boolean $it")
        }
    }

    private inline fun <reified T : Enum<T>> enum(crossinline mapper: (T) -> String = { it.name }): (String) -> T =
        { enumValues<T>().first { enum -> mapper(enum) == it } }

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

    enum class RunMode {
        BOTH,
        REGULAR,
        TEST
    }

    val AOC_TOKEN by variable()
    val AOC_INPUT_CACHE by variable(required, { File(it).also(File::mkdirs) })
    val AOC_WARMUPS by variable({ 0 }, int)
    val AOC_RUN_MODE by variable({ RunMode.BOTH }, enum())
}
