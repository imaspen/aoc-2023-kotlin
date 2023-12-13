package days

import kotlin.math.max

class Day13 : Day {
    private data class Pattern(val columns: List<String>, val rows: List<String>) {
        fun findReflection(strings: List<String>, hasSmudge: Boolean = false): Int? {
            for (i in 1..<strings.count()) {
                val end = strings.drop(i).take(i).reversed()
                val start = strings.take(i).drop(max(0, i - end.count()))
                if (!hasSmudge) {
                    if (start.zip(end).all { it.first == it.second }) {
                        return i
                    }
                } else {
                    // this is a very lazy way to do this, but it runs in reasonable time, and i heard once that premature optimization was the root of all evil
                    val mutableStart = start.toMutableList()
                    for (j in 0..<start.count()) {
                        for (k in 0..<start[j].count()) {
                            mutableStart[j] = start[j].replaceRange(k, k + 1, if (start[j][k] == '.') "#" else ".")
                            if (mutableStart.zip(end).all { it.first == it.second }) {
                                return i
                            }
                        }
                        mutableStart[j] = start[j]
                    }
                }
            }
            return null
        }

        fun findColumnReflection(hasSmudge: Boolean = false): Int? {
            return findReflection(columns, hasSmudge)
        }

        fun findRowReflection(hasSmudge: Boolean = false): Int? {
            return findReflection(rows, hasSmudge)
        }
    }

    private val patterns = javaClass.getResource("/day13.txt")!!.readText().split("\n\n").map { group ->
        val rows = group.lines()
        val columns = (0..<rows[0].count()).map { i -> rows.map { it[i] }.joinToString("") }
        Pattern(columns, rows)
    }

    override fun part1(): String {
        return patterns.sumOf { pattern ->
            pattern.findColumnReflection() ?: ((pattern.findRowReflection() ?: 0) * 100)
        }.toString()
    }

    override fun part2(): String {
        return patterns.sumOf { pattern ->
            pattern.findColumnReflection(true) ?: ((pattern.findRowReflection(true) ?: 0) * 100)
        }.toString()
    }
}