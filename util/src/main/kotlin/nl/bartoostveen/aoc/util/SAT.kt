package nl.bartoostveen.aoc.util

import kotlin.math.abs

object SAT {
    typealias Literal = Int
    typealias Variable = Int

    typealias Assignment = Set<Literal>

    @JvmInline
    value class Clause(val lits: Set<Literal>) {
        fun assign(lit: Literal) = lits.filter { it != -lit }.toSet()
    }

    @JvmInline
    value class Cnf(val clauses: Set<Clause>) {
        fun assign(lit: Literal) = clauses
            .filter { lit !in it.lits }
            .map { it.assign(lit) }
            .toSet()

        fun assign(lits: Assignment) = lits.fold(clauses) { acc, lit ->
            Cnf(acc).assign(lit)
                .map { Clause(it) }
                .toSet()
        }

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
    }

    fun Cnf.findUnit() = clauses.find { it.lits.size == 1 }?.lits

    fun Cnf.propagateUnits(): Pair<Assignment, Cnf> {
        val unit = findUnit() ?: return (setOf<Literal>() to this)
        val new = assign(unit)
        val (newLits, cnf) = Cnf(new.toSet()).propagateUnits()
        return newLits + unit to cnf
    }

    fun Cnf.solve(): Assignment? {
        val vars = vars

        fun tryAssign(lit: Literal): Assignment? {
            val (propagatedLits, propagatedCnf) = propagateUnits()
            if (propagatedCnf.clauses == emptySet<Clause>()) return propagatedLits

            val assigned = propagatedCnf.assign(lit)
            if (assigned.isEmpty()) return setOf(lit)
            if (assigned.any { it.isEmpty() }) return null

            return Cnf(assigned.map { clause -> Clause(clause) }.toSet()).solve()
                ?.let { setOf(lit) + it }
        }

        return runCatching {
            tryAssign(vars.first()) ?: tryAssign(-vars.first())
        }.getOrNull()
    }
}
