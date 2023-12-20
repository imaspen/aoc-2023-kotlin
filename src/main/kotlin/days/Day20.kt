package days

class Day20 : Day {
    private data class Trigger(val module: String, val from: String, val isPulseHigh: Boolean)
    private sealed class Module(val label: String, val outputLabels: List<String>) {
        abstract fun trigger(from: String, isPulseHigh: Boolean): List<Trigger>
        abstract fun reset()

        companion object {
            fun fromStrings(strings: List<String>): Map<String, Module> {
                val modules = mutableMapOf<String, Module>()
                val conjunctions = mutableListOf<Pair<String, List<String>>>()

                for (string in strings) {
                    val (label, output) = string.split(" -> ")
                    val outputs = output.split(", ")
                    when (label[0]) {
                        '&' -> conjunctions.add(label.drop(1) to outputs)
                        '%' -> modules[label.drop(1)] = FlipFlop(label.drop(1), outputs)
                        else -> modules[label] = Broadcaster(label, outputs)
                    }
                }

                for ((label, outputs) in conjunctions) {
                    val moduleInputs = modules.filter { it.value.outputLabels.contains(label) }.keys
                    val conjunctionInputs = conjunctions.filter { it.second.contains(label) }.map { it.first }
                    val inputs = (moduleInputs + conjunctionInputs).toList()
                    modules[label] = Conjunction(label, outputs, inputs)
                }

                return modules
            }
        }
    }

    private class Broadcaster(label: String, outputLabels: List<String>) : Module(label, outputLabels) {
        override fun trigger(from: String, isPulseHigh: Boolean): List<Trigger> {
            return outputLabels.map { Trigger(it, label, isPulseHigh) }
        }

        override fun reset() {}
    }

    private class FlipFlop(label: String, outputLabels: List<String>) : Module(label, outputLabels) {
        private var isOn = false

        override fun trigger(from: String, isPulseHigh: Boolean): List<Trigger> {
            if (isPulseHigh) return emptyList()

            isOn = !isOn
            return outputLabels.map { Trigger(it, label, isOn) }
        }

        override fun reset() {
            isOn = false
        }
    }

    private class Conjunction(label: String, outputLabels: List<String>, val inputLabels: List<String>) :
        Module(label, outputLabels) {

        private val lastInputs = mutableMapOf<String, Boolean>()

        override fun trigger(from: String, isPulseHigh: Boolean): List<Trigger> {
            lastInputs[from] = isPulseHigh
            val output = inputLabels.any { label -> !lastInputs.getOrDefault(label, false) }
            return outputLabels.map { Trigger(it, label, output) }
        }

        override fun reset() {
            lastInputs.clear()
        }
    }

    private class Blank(label: String) : Module(label, emptyList()) {
        override fun trigger(from: String, isPulseHigh: Boolean): List<Trigger> {
            return emptyList()
        }

        override fun reset() {}
    }

    private val moduleLists =
        javaClass.getResource("/day20.txt")!!.readText().split("\n\n").map { Module.fromStrings(it.lines()) }

    override fun part1(): String {
        return moduleLists.map { modules ->
            var lowCount = 0
            var highCount = 0

            for (i in 0..<1000) {
                val toTrigger = ArrayDeque<Trigger>()
                toTrigger.add(Trigger("broadcaster", "button", false))
                lowCount++

                while (toTrigger.isNotEmpty()) {
                    val (label, from, isPulseHigh) = toTrigger.removeFirst()
                    val triggers = modules.getOrDefault(label, Blank(label)).trigger(from, isPulseHigh)
                    val highPulses = triggers.count { it.isPulseHigh }
                    highCount += highPulses
                    lowCount += triggers.size - highPulses
                    toTrigger.addAll(triggers)
                }
            }

            lowCount * highCount
        }.joinToString(",")
    }

    override fun part2(): String {
        val modules = moduleLists.last()
        var presses = 0L

        // rx is the output of conjunctions.
        // conjunctions are true where all their inputs are true.
        // inputs of all conjunctions can be simplified to the lcm of the period of all inputs.
        fun findConjunctionParents(label: String): List<Module>? {
            val parents = modules.filter { it.value.outputLabels.contains(label) }
            val parentsAreConjunctions = parents.values.all {
                when (it) {
                    is Conjunction -> true
                    else -> false
                }
            }
            if (!parentsAreConjunctions) return null

            val parentsParents = parents.map { findConjunctionParents(it.key) }
            return if (parentsParents.any { it == null }) parents.values.toList() else parentsParents.flatMap { it!! }
        }

        val parents = findConjunctionParents("rx")!!.associate { it.label to -1L }.toMutableMap()

        while (true) {
            val toTrigger = ArrayDeque<Trigger>()
            toTrigger.add(Trigger("broadcaster", "button", false))
            presses++

            while (toTrigger.isNotEmpty()) {
                val (label, from, isPulseHigh) = toTrigger.removeFirst()
                val triggers = modules.getOrDefault(label, Blank(label)).trigger(from, isPulseHigh)

                for (trigger in triggers) {
                    if (!trigger.isPulseHigh && parents[trigger.from] == -1L) {
                        parents[trigger.from] = presses
                    }
                }

                if (parents.values.all { it >= 0 }) {
                    fun gcd(aIn: Long, bIn: Long): Long {
                        var t: Long
                        var a = aIn
                        var b = bIn
                        while (b != 0L) {
                            t = b
                            b = a % b
                            a = t
                        }
                        return a
                    }

                    fun lcm(a: Long, b: Long): Long {
                        return a * b / gcd(a, b)
                    }

                    return parents.values.reduce {a, b -> lcm(a, b)}.toString()
                }

                toTrigger.addAll(triggers)
            }
        }
    }
}