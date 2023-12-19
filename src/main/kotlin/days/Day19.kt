package days

class Day19 : Day {
    private enum class Stat {
        EXTREME, MUSICAL, AERODYNAMIC, SHINY;

        companion object {
            fun fromChar(char: Char): Stat {
                return when (char) {
                    'x' -> EXTREME
                    'm' -> MUSICAL
                    'a' -> AERODYNAMIC
                    's' -> SHINY
                    else -> error("unrecognized stat: $char")
                }
            }
        }
    }

    private sealed class Outcome {
        class Output(val accepted: Boolean) : Outcome()
        class SubTest(val test: Test) : Outcome()
        class Instruction(val label: String) : Outcome()

        companion object {
            fun fromString(string: String): Outcome {
                if (!string.contains(":")) {
                    return when (string) {
                        "A" -> Output(true)
                        "R" -> Output(false)
                        else -> Instruction(string)
                    }
                }

                val (test, rest) = string.split(":", limit = 2)
                val stat = Stat.fromChar(test[0])
                val isGreaterThan = test[1] == '>'
                val comparisonValue = test.drop(2).toInt()
                val (match, miss) = rest.split(",", limit = 2)
                val matchOutcome = fromString(match)
                val missOutcome = fromString(miss)

                return SubTest(Test(stat, isGreaterThan, comparisonValue, matchOutcome, missOutcome))
            }
        }
    }

    private data class Test(
        val stat: Stat,
        val isGreaterThan: Boolean,
        val comparisonValue: Int,
        val matchOutcome: Outcome,
        val missOutcome: Outcome
    ) {
        fun doTest(part: Part, tests: Map<String, Test>): Boolean {
            var test = this
            while (true) {
                val value = part.getStat(test.stat)
                val outcome = when {
                    test.isGreaterThan && (value > test.comparisonValue) -> test.matchOutcome
                    !test.isGreaterThan && (value < test.comparisonValue) -> test.matchOutcome
                    else -> test.missOutcome
                }

                test = when (outcome) {
                    is Outcome.Output -> return outcome.accepted
                    is Outcome.SubTest -> outcome.test
                    is Outcome.Instruction -> tests[outcome.label]!!
                }
            }
        }

        fun getMatchingRanges(startingPartRange: PartRange, tests: Map<String, Test>): List<PartRange> {
            val toExplore = mutableListOf(startingPartRange to this)
            val acceptedRanges = mutableListOf<PartRange>()

            while (toExplore.isNotEmpty()) {
                val (partRange, test) = toExplore.removeLast()
                val (matches, misses) = partRange.split(test.stat, test.isGreaterThan, test.comparisonValue)

                if (matches != null) {
                    when (test.matchOutcome) {
                        is Outcome.SubTest -> {
                            toExplore.add(matches to test.matchOutcome.test)
                        }
                        is Outcome.Output -> {
                            if (test.matchOutcome.accepted) acceptedRanges.add(matches)
                        }
                        is Outcome.Instruction -> {
                            toExplore.add(matches to tests[test.matchOutcome.label]!!)
                        }
                    }
                }
                if (misses != null) {
                    when (test.missOutcome) {
                        is Outcome.SubTest -> {
                            toExplore.add(misses to test.missOutcome.test)
                        }
                        is Outcome.Output -> {
                            if (test.missOutcome.accepted) acceptedRanges.add(misses)
                        }
                        is Outcome.Instruction -> {
                            toExplore.add(misses to tests[test.missOutcome.label]!!)
                        }
                    }
                }
            }

            return acceptedRanges
        }
    }

    private data class Part(val extreme: Int, val musical: Int, val aerodynamic: Int, val shiny: Int) {
        fun getStat(stat: Stat): Int {
            return when (stat) {
                Stat.EXTREME -> extreme
                Stat.MUSICAL -> musical
                Stat.AERODYNAMIC -> aerodynamic
                Stat.SHINY -> shiny
            }
        }

        fun getSum(): Int {
            return extreme + musical + aerodynamic + shiny
        }

        companion object {
            fun fromString(string: String): Part {
                val matches = Regex("""\d+""").findAll(string).map { it.value.toInt() }.toList()
                return Part(matches[0], matches[1], matches[2], matches[3])
            }
        }
    }

    private data class PartRange(
        val extreme: IntRange = 1..4000,
        val musical: IntRange = 1..4000,
        val aerodynamic: IntRange = 1..4000,
        val shiny: IntRange = 1..4000
    ) {
        fun getStat(stat: Stat): IntRange {
            return when (stat) {
                Stat.EXTREME -> extreme
                Stat.MUSICAL -> musical
                Stat.AERODYNAMIC -> aerodynamic
                Stat.SHINY -> shiny
            }
        }

        fun split(stat: Stat, isGreaterThan: Boolean, comparisonValue: Int): Pair<PartRange?, PartRange?> {
            val range = getStat(stat)

            fun splitRange(range: IntRange): Pair<IntRange, IntRange> {
                return if (isGreaterThan) {
                    (comparisonValue + 1)..range.last to range.first..comparisonValue
                } else {
                    range.first..<comparisonValue to comparisonValue..range.last
                }
            }

            fun splitStat(): Pair<PartRange, PartRange> {
                val (matches, misses) = splitRange(range)
                return when (stat) {
                    Stat.EXTREME -> (
                        PartRange(matches, musical, aerodynamic, shiny) to
                        PartRange(misses, musical, aerodynamic, shiny)
                    )
                    Stat.MUSICAL -> (
                        PartRange(extreme, matches, aerodynamic, shiny) to
                        PartRange(extreme, misses, aerodynamic, shiny)
                    )
                    Stat.AERODYNAMIC -> (
                        PartRange(extreme, musical, matches, shiny) to
                        PartRange(extreme, musical, misses, shiny)
                    )
                    Stat.SHINY -> (
                        PartRange(extreme, musical, aerodynamic, matches) to
                        PartRange(extreme, musical, aerodynamic, misses)
                    )
                }
            }

            if (isGreaterThan) {
                // no match
                if (range.last <= comparisonValue) return null to this
                // only match
                if (range.first > comparisonValue) return this to null
            } else {
                // no match
                if (range.first >= comparisonValue) return null to this
                // only match
                if (range.last < comparisonValue) return this to null
            }

            return splitStat()
        }
    }

    private val tests: Map<String, Test>
    private val parts: List<Part>

    init {
        val (testStrings, partStrings) = javaClass.getResource("/day19.txt")!!.readText().split("\n\n")
            .map { it.lines() }

        tests = testStrings.associate { line ->
            val (identifier, definition) = line.dropLast(1).split("{")
            val test = when (val outcome = Outcome.fromString(definition)) {
                is Outcome.SubTest -> outcome.test
                else -> error("definition was not of a test")
            }
            identifier to test
        }

        parts = partStrings.map { Part.fromString(it) }
    }

    override fun part1(): String {
        val entry = tests["in"]!!
        return parts.filter { entry.doTest(it, tests) }.sumOf { it.getSum() }.toString()
    }

    override fun part2(): String {
        val entry = tests["in"]!!
        val ranges = entry.getMatchingRanges(PartRange(), tests)
        return ranges.sumOf { range ->
            range.extreme.count().toLong() * range.musical.count().toLong() * range.shiny.count().toLong() * range.aerodynamic.count().toLong()
        }.toString()
    }
}