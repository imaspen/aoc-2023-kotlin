package days

import org.jgrapht.alg.StoerWagnerMinimumCut
import org.jgrapht.graph.DefaultEdge
import org.jgrapht.graph.SimpleGraph

class Day25 : Day {
    private val lines = javaClass.getResource("/day25.txt")!!.readText().lines()

    private fun createGraph(): SimpleGraph<String, DefaultEdge> {
        val graph = SimpleGraph<String, DefaultEdge>(DefaultEdge::class.java)
        for (line in lines) {
            val (from, toString) = line.split(": ")
            graph.addVertex(from)
            for (to in toString.split(' ')) {
                graph.addVertex(to)
                graph.addEdge(from, to)
            }
        }
        return graph
    }

    override fun part1(): String {
        val graph = createGraph()
        val minCut = StoerWagnerMinimumCut(graph).minCut()
        graph.removeAllVertices(minCut)
        return (minCut.size * graph.vertexSet().size).toString()
    }

    override fun part2(): String {
        return "Merry Christmas!"
    }
}