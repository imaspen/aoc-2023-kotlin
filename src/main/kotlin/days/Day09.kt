package days

class Day09 : Day {
    private tailrec fun getNext(sequence: List<Int>, last: Long = 0L): Long {
        return if (sequence.all {it == 0}) return last
        else getNext(sequence.windowed(2, 1).map { it[1] - it[0] }, last + sequence.last())
    }

    private fun getPrevious(sequence: List<Int>): Int {
        val lists = buildList {
            var current = sequence
            while (current.any { it != 0}) {
                add(current.first())
                current = current.windowed(2, 1).map { it[1] - it[0] }
            }
        }
        return lists.foldRight(0) { acc, i -> acc - i }
    }

    private val sequences = javaClass.getResource("/day09.txt")!!.readText().lines().map { line ->
        line.split(' ').map { it.toInt() }
    }

    override fun part1(): String {
        return sequences.sumOf { getNext(it) }.toString()
    }

    override fun part2(): String {
        return sequences.sumOf { getPrevious(it) }.toString()
    }
}