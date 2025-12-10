package nl.bartoostveen.aoc.util

@ConsistentCopyVisibility
data class SetUnionFindContext<T> @PublishedApi internal constructor(
    internal val underlying: HashMap<T, Entry<T>> = hashMapOf()
) : Map<T, SetUnionFindContext.Entry<T>> by underlying {
    data class Entry<T>(
        val value: T,
        var rank: Int = 0,
        var parent: Entry<T>? = null
    ) {
        val root: Entry<T> get() = parent?.root?.also { parent = it } ?: this
    }

    fun contains(first: T, second: T): Boolean {
        return (root(first) ?: return false) == (root(second) ?: return false)
    }

    operator fun contains(pair: Pair<T, T>) = contains(pair.first, pair.second)
    operator fun contains(pair: TupleSet<T>) = contains(pair.first, pair.second)

    fun add(value: T): Boolean {
        if (value in this) return false
        underlying[value] = Entry(value)
        return true
    }

    fun addPair(pair: Pair<T, T>) = addPair(pair.first, pair.second)
    fun addPair(pair: TupleSet<T>) = addPair(pair.first, pair.second)

    fun addPair(first: T, second: T): Boolean {
        if (contains(first, second)) return false

        val firstRoot = rootOfOrNew(first)
        val secondRoot = rootOfOrNew(second)
        if (firstRoot == secondRoot) return false

        when {
            firstRoot.rank < secondRoot.rank -> firstRoot.parent = secondRoot
            firstRoot.rank > secondRoot.rank -> secondRoot.parent = firstRoot
            else -> {
                firstRoot.parent = secondRoot
                secondRoot.rank++
            }
        }

        return true
    }

    fun root(v: T) = underlying[v]?.root
    fun rootOfOrNew(v: T) = underlying.getOrPut(v) { Entry(v) }.root
    val roots get() = keys.filter { getValue(it).parent == null }.toSet()
    val islands get() = keys.groupBy { getValue(it).root }
}

inline fun <reified T> unionFind(block: SetUnionFindContext<T>.() -> Unit) =
    SetUnionFindContext<T>().apply(block)
