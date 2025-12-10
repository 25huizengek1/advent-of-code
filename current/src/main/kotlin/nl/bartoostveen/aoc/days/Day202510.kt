package nl.bartoostveen.aoc.days

import nl.bartoostveen.aoc.util.puzzle
import nl.bartoostveen.aoc.util.splitByWhitespace
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import kotlin.math.abs

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

val day202510 = puzzle {
    val lights = lines.map { line ->
        val parts = line.splitByWhitespace()
        val target = parts.first().let { it.subSequence(1, it.length - 1) }.map { it == '#' }
        val buttons = parts.subList(1, parts.size - 1).map { button ->
            button.subSequence(1, button.length - 1).split(',').map(String::toInt)
        }
        val joltage =
            parts.last().let { it.subSequence(1, it.length - 1) }.split(',').map(String::toInt)
        Triple(target, buttons, joltage)
    }

    fun List<Boolean>.press(button: List<Int>) = mapIndexed { i, s -> if (i in button) !s else s }
    fun List<Int>.press(button: List<Int>) = mapIndexed { i, s -> if (i in button) s + 1 else s }

    fun partOne(
        current: List<Boolean>,
        target: List<Boolean>,
        buttons: List<List<Int>>
    ): Int {
        if (current == target) return 0

        val queue = ArrayDeque<Pair<List<Boolean>, Int>>()
        val seen = hashSetOf<List<Boolean>>()

        queue += current to 0
        seen += current

        while (queue.isNotEmpty()) {
            val (old, presses) = queue.removeFirst()

            for (button in buttons) {
                val new = old.press(button)
                val newPresses = presses + 1

                if (new == target) return newPresses
                if (new !in seen) {
                    seen += new
                    queue += new to newPresses
                }
            }
        }

        return -1
    }

    fun partTwo(
        current: List<Int>,
        target: List<Int>,
        buttons: List<List<Int>>
    ): Int {
        if (current == target) return 0

        val queue = ArrayDeque<Pair<List<Int>, Int>>()
        val seen = hashSetOf<List<Int>>()

        queue += current to 0
        seen += current

        while (queue.isNotEmpty()) {
            val (old, presses) = queue.removeFirst()

            outerFor@ for (button in buttons) {
                val new = old.press(button)
                for (i in new.indices) {
                    if (new[i] > target[i]) {
                        seen += new
                        break@outerFor
                    }
                }
                val newPresses = presses + 1

                if (new == target) return newPresses
                if (new !in seen) {
                    seen += new
                    queue += new to newPresses
                }
            }
        }

        return -1
    }

    partOne = lights.sumOf { (target, buttons, _) ->
        partOne(
            current = List(target.size) { false },
            target = target,
            buttons = buttons
        )
    }

    val tasks = lights.map { (target, buttons, targetJoltage) ->
        Callable {
            partTwo(
                current = List(target.size) { 0 },
                target = targetJoltage,
                buttons = buttons
            )
        }
    }

    partTwo = Executors.newCachedThreadPool().invokeAll(tasks).sumOf {
        try {
            it?.get() ?: 0
        } catch (_: Throwable) {
            0
        }
    }
}
