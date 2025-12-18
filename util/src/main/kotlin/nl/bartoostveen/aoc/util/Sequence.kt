package nl.bartoostveen.aoc.util

fun <T> Sequence<T>.split(predicate: (T) -> Boolean): Sequence<List<T>> = sequence {
    var acc = mutableListOf<T>()

    forEach { p ->
        if (predicate(p)) {
            if (acc.isNotEmpty()) yield(acc)
            acc = mutableListOf()
        } else acc += p
    }

    if (acc.isNotEmpty()) yield(acc)
}

