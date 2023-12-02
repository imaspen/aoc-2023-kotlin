import kotlin.system.exitProcess

fun printUsage() {
    println("Usage: aoc-2023-kotlin {day} {part}")
}

fun main(args: Array<String>) {
    val dayObject = when (val day = args.getOrNull(0)?.toUIntOrNull()) {
        1u -> days.Day01()
        in 2u..25u -> TODO("Day $day not implemented")
        else -> {
            println("Unrecognised day: $day")
            printUsage()
            exitProcess(1)
        }
    }

    val result = when (val part = args.getOrNull(1)?.toUIntOrNull()) {
        1u -> dayObject.part1()
        2u -> dayObject.part2()
        else -> {
            println("Unrecognised part: $part")
            printUsage()
            exitProcess(1)
        }
    }

    println(result)
}