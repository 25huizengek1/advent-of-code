package nl.bartoostveen.aoc.util

import kotlin.math.abs
import kotlin.math.absoluteValue

object SAT {
    typealias Literal = Int
    typealias Variable = Int

    fun Int.display(): String {
        val letters = ('a'..'z').toList()
        var result = ""
        var num = absoluteValue
        while (num > 0) {
            num -= 1
            result += letters[num % letters.size]
            num /= letters.size
        }
        if (this < 0) result += '-'
        return result.reversed()
    }

    typealias Assignment = Set<Literal>

    val Set<Set<Literal>>.asCnf get() = map { it.asClause }.toSet().asCnf
    val Set<Clause>.asCnf get() = Cnf(this)
    val Set<Literal>.asClause get() = Clause(this)

    @JvmInline
    value class Clause(val lits: Set<Literal>) {
        fun assign(lit: Literal) = lits.filter { it != -lit }.toSet().asClause
        override fun toString() = "Clause(lits=${lits.map { it.display() }})"
    }

    @JvmInline
    value class Cnf(val clauses: Set<Clause>) {
        fun assign(lit: Literal) = clauses
            .filter { lit !in it.lits }
            .map { it.assign(lit) }
            .toSet()
            .asCnf

        fun assign(lits: Assignment) = lits.fold(clauses) { acc, lit ->
            Cnf(acc).assign(lit).clauses
        }.asCnf

        val vars get() = clauses.flatMap { it.lits.map { lit -> abs(lit) }.toSet() }.toSet()

        val allAssignments: Sequence<Set<Int>>
            get() {
                val start = vars
                val negStart = start.map { -it }
                return generateSequence(start) { nextAssignment(it).takeUnless { result -> result == negStart } }
            }

        fun nextAssignment(vars: Set<Variable>): Set<Literal> {
            if (vars.isEmpty()) return setOf()
            if (vars.size < 2) return setOf(-vars.first())

            val (a, b) = vars.take(2)
            val rest = vars.drop(2).toSet()

            return when {
                b < 0 && a < 0 -> setOf(-a, -b) + nextAssignment(rest)
                a < 0 -> setOf(-a, -b) + rest
                else -> setOf(-a, b) + rest
            }
        }

        fun findUnit() = clauses.find { it.lits.size == 1 }?.lits
        fun propagateUnits(): Pair<Assignment, Cnf> {
            val unit = findUnit() ?: return (setOf<Literal>() to this)
            val new = assign(unit)
            val (newLits, cnf) = new.propagateUnits()
            return newLits + unit to cnf
        }

        fun Cnf.solve(): Assignment? {
            val vars = vars

            fun tryAssign(lit: Literal): Assignment? {
                val (propagatedLits, propagatedCnf) = propagateUnits()
                if (propagatedCnf.clauses == emptySet<Clause>()) return propagatedLits

                val assigned = propagatedCnf.assign(lit)
                if (assigned.clauses.isEmpty()) return setOf(lit)
                if (assigned.clauses.any { it.lits.isEmpty() }) return null

                return assigned.solve()?.let { setOf(lit) + it }
            }

            return runCatching {
                tryAssign(vars.first()) ?: tryAssign(-vars.first())
            }.getOrNull()
        }

        override fun toString() = "Cnf(clauses=$clauses)"
    }
}
