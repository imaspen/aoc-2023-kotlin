package days

class Day07 : Day {
    private class Hand(val cards: List<Int>, val bid: Int, val hasJokers: Boolean) : Comparable<Hand> {
        private val counts: Map<Int, Int> = cards.groupingBy { it }.eachCount()
        private val handPower: Int

        init {
            val jokers = counts.getOrDefault(0, 0)
            val values = buildMap<Int, Int> {
                putAll(counts.filterNot { hasJokers && it.key == 0 })
                if (hasJokers) {
                    val maxKey = maxByOrNull { it.value }?.key ?: 0
                    this[maxKey] = getOrDefault(maxKey, 0) + jokers
                }
            }.values

            handPower = when (values.max()) {
                5 -> 0

                4 -> -1

                3 -> {
                    if (values.contains(2)) {
                        -2
                    } else {
                        -3
                    }
                }

                2 -> {
                    if (values.count { it == 2 } == 2) {
                        -4
                    } else {
                        -5
                    }
                }

                else -> -6
            }
        }

        override fun compareTo(other: Hand): Int {
            val handPowerDiff = handPower - other.handPower
            if (handPowerDiff != 0) {
                return handPowerDiff
            }

            for (i in 0..4) {
                val cardPowerDiff = cards[i] - other.cards[i]
                if (cardPowerDiff != 0) {
                    return cardPowerDiff
                }
            }

            return 0
        }

        override fun toString(): String {
            return "Hand(cards: $cards, bid: $bid)"
        }
    }

    private fun getScore(hasJokers: Boolean = false): String {
        return javaClass.getResource("/day07.txt")!!.readText().replace('A', if (hasJokers) 'D' else 'E')
            .replace('K', if (hasJokers) 'C' else 'D').replace('Q', if (hasJokers) 'B' else 'C')
            .replace('J', if (hasJokers) '1' else 'B').replace('T', 'A').lines().map { line ->
                val parts = line.split(' ')
                Hand(parts[0].map { it.digitToInt(16) - if (hasJokers) 1 else 2 }, parts[1].toInt(10), hasJokers)
            }.sorted().withIndex().sumOf { (index, hand) -> index.inc() * hand.bid }.toString()
    }

    override fun part1(): String {
        return getScore()
    }

    override fun part2(): String {
        return getScore(true)
    }
}