package days

import kotlin.math.pow

class Day04 : Day {
    private class Ticket(private val winningNumbers: Set<Int>, private val gameNumbers: Set<Int>) {
        fun winningCount(): Int {
            return gameNumbers.intersect(winningNumbers).count()
        }

        fun getScore(): Double {
            val count = winningCount()
            if (count == 0) return 0.0

            return 2.0.pow(count - 1.0)
        }

        override fun toString(): String {
            return "Ticket(winningNumbers: $winningNumbers, gameNumbers: $gameNumbers)"
        }
    }

    private val numberMatcher = Regex("""\d+""")
    private val tickets = javaClass.getResource("/day04.txt")!!.readText().lines().map {line ->
        val sets = line.split(": ")[1].split(" | ").map { part ->
            val matches = numberMatcher.findAll(part)
            val set = matches.map { matchResult -> matchResult.value.toInt() }.toSet()
            // I've made the assumption that duplicates don't exist, but we should check that assumption holds
            assert(matches.count() == set.count()) { "Duplicate numbers exist, rethink this solution" }
            set
        }
        Ticket(sets[0], sets[1])
    }

    override fun part1(): String {
        val score = tickets.sumOf { ticket -> ticket.getScore() }
        return "%.0f".format(score)
    }

    override fun part2(): String {
        val ticketCounts = tickets.map { 1 }.toMutableList()
        tickets.forEachIndexed { index, ticket ->
            val count = ticket.winningCount()
            for (i in 1..count) {
                ticketCounts[index + i] += ticketCounts[index]
            }
        }
        return ticketCounts.sum().toString()
    }
}