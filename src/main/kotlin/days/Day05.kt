package days

class Day05 : Day {
    private class SourceDestinationMap(val sourceRange: LongRange, val destinationRange: LongRange) {
        fun getDestFromSource(source: Long): Long? {
            if (!sourceRange.contains(source)) {
                return null
            }

            return destinationRange.first + (source - sourceRange.first)
        }

        fun getDestRangeFromSourceRange(source: LongRange): Pair<LongRange, List<LongRange>>? {
            if (source.last < sourceRange.first || source.first > sourceRange.last) {
                return null
            } else if (source.first < sourceRange.first && source.last > sourceRange.last) {
                return Pair(
                    destinationRange,
                    listOf(LongRange(source.first, sourceRange.first - 1), LongRange(sourceRange.last + 1, source.last))
                )
            } else if (source.first < sourceRange.first) {
                return Pair(
                    LongRange(destinationRange.first, source.last + destinationRange.last - sourceRange.last),
                    listOf(LongRange(source.first, sourceRange.first - 1))
                )
            } else if (source.last > sourceRange.last) {
                return Pair(
                    LongRange(source.first + destinationRange.first - sourceRange.first, destinationRange.last),
                    listOf(LongRange(sourceRange.last + 1, source.last))
                )
            } else {
                val offset = destinationRange.first - sourceRange.first
                return Pair(LongRange(source.first + offset, source.last + offset), listOf())
            }
        }
    }

    private val sections = javaClass.getResource("/day05.txt")!!.readText().split("\n\n")
    private val seeds = sections[0].split(": ")[1].split(" ").map { it.toLong() }
    private val mapsList = sections.drop(1).map {section ->
        section.lines().drop(1).map{ line ->
            val definitions = line.split(" ").map { it.toLong() }
            SourceDestinationMap(
                LongRange(definitions[1], definitions[1] + definitions[2] - 1),
                LongRange(definitions[0], definitions[0] + definitions[2] - 1)
            )
        }
    }

    override fun part1(): String {
        return mapsList.fold(seeds) { sources, maps ->
            sources.map { source ->
                val dest = maps.firstNotNullOfOrNull { it.getDestFromSource(source) }
                dest ?: source
            }
        }.min().toString()
    }

    override fun part2(): String {
        val seedRanges = seeds.chunked(2).map { LongRange(it[0], it[0] + it[1] - 1) }

        return mapsList.fold(seedRanges) { sources, maps ->
            val remainingSources = sources.toMutableList()
            val destinations = mutableListOf<LongRange>()
            while (remainingSources.isNotEmpty()) {
                val source = remainingSources.removeLast()
                val output = maps.firstNotNullOfOrNull { it.getDestRangeFromSourceRange(source) }
                if (output == null) {
                    destinations.add(source)
                } else {
                    destinations.add(output.first)
                    remainingSources.addAll(output.second)
                }
            }
            destinations
        }.minOf { it.first }.toString()
    }
}