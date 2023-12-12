package days

class Day12 : Day {
    data class Row(val springs: String, val groups: List<Int>) {
        companion object {
            fun fromString(string: String): Row {
                val parts = string.split(' ')
                return Row(parts[0], parts[1].split(',').map { it.toInt() })
            }
        }
    }

    private val rows = javaClass.getResource("/day12.txt")!!.readText().lines().map { Row.fromString(it) }

    private val countPermutationsCache = mutableMapOf<Row, Long>()

    private fun countPermutations(springs: String, groups: List<Int>): Long {
        return countPermutations(Row(springs, groups))
    }

    private fun countPermutations(row: Row): Long {
        return countPermutationsCache.getOrPut(row) {
            val (springs, groups) = row

            if (springs.isEmpty()) {
                // we're out of space
                if (groups.isEmpty()) {
                    // and everything is accounted for, this branch was valid
                    return@getOrPut 1
                } else {
                    // but we expected more, this branch wasn't valid
                    return@getOrPut 0
                }
            }

            return@getOrPut when (springs.first()) {
                // we're at the start of a group
                '#' -> when {
                    // but there shouldn't be another group
                    groups.isEmpty() -> 0
                    // but there isn't enough room to finish this group
                    springs.count() < groups.first() -> 0
                    // but the group contains a gap
                    springs.take(groups[0]).contains('.') -> 0
                    // but the group is too long
                    springs.getOrNull(groups[0]) == '#' -> 0
                    // the space after the group is unknown, but must be empty, treat it that way and move on to the next group
                    springs.count() > groups[0] && springs[groups[0]] == '?' -> countPermutations(
                        springs.drop(groups[0] + 1).trimStart('.'), groups.drop(1)
                    )
                    // move on to the next group
                    else -> countPermutations(springs.drop(groups[0]).trimStart('.'), groups.drop(1))
                }

                // move to the start of the next possible group
                '.' -> countPermutations(Row(springs.trimStart('.'), groups))
                // expand both possibilities
                '?' -> countPermutations("#" + springs.drop(1), groups) + countPermutations(
                    "." + springs.drop(1), groups
                )

                else -> error("unexpected character in spring row")
            }
        }
    }

    override fun part1(): String {
        return rows.sumOf { countPermutations(it) }.toString()
    }

    override fun part2(): String {
        return rows.map { (springs, groups) ->
            Row(springs.plus('?').repeat(5).dropLast(1), groups + groups + groups + groups + groups)
        }.sumOf { countPermutations(it) }.toString()
    }
}