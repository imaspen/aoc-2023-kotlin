package days

class Day02 : Day {
    private val games = javaClass.getResource("/day02.txt")!!.readText().lines().map { line ->
        line.split(": ")[1].split("; ").map { set ->
            set.split(", ").associate { counts ->
                val parts = counts.split(" ")
                parts[1] to parts[0].toInt()
            }
        }
    }

    override fun part1(): String {
        return games.withIndex().fold(0) { acc, indexedSets ->
            val isValid = indexedSets.value.all { counts ->
                counts.getOrElse("red") { 0 } <= 12 && counts.getOrElse("green") { 0 } <= 13 && counts.getOrElse("blue") { 0 } <= 14
            }

            if (isValid) {
                acc + 1 + indexedSets.index
            } else {
                acc
            }
        }.toString()
    }

    override fun part2(): String {
        return games.fold(0) { acc, sets ->
            val maxCounts = mutableMapOf(
                "red" to 0,
                "green" to 0,
                "blue" to 0
            )

            sets.forEach { counts ->
                counts.forEach {count ->
                    if (maxCounts[count.key]!! < count.value) {
                        maxCounts[count.key] = count.value
                    }
                }
            }

            acc + (maxCounts["red"]!! * maxCounts["green"]!! * maxCounts["blue"]!!)
        }.toString()
    }
}