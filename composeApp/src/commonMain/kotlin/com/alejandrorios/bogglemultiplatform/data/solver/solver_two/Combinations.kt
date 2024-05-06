package com.alejandrorios.bogglemultiplatform.data.solver.solver_two

class Combinations(private val board: List<List<Char>>, private val prefixTree: PrefixTree) {
    private val neighborCoordinates = listOf(
        -1 to -1,
        -1 to 0,
        -1 to 1, // Up left, Up, Up right
        0 to -1,
        0 to 1, // Left, Right
        1 to -1,
        1 to 0,
        1 to 1 // Down left, Down, Down right
    )
    private val allCombinations = mutableSetOf<String>()
    private val columnLength = board[0].size
    private val rowLength = board.size

    /**
     * For any given matrix cell, return list of all valid neighbors
     */
    private fun getNeighbors(rowIndex: Int, columnIndex: Int): List<Pair<Int, Int>> {
        val allNeighbors = mutableListOf<Pair<Int, Int>>()

        for ((rowOffset, columnOffset) in neighborCoordinates) {
            val newRow = rowIndex + rowOffset
            val newColumn = columnIndex + columnOffset
            if (newRow in 0 until rowLength && newColumn in 0 until columnLength) {
                allNeighbors.add(newRow to newColumn)
            }
        }
        return allNeighbors
    }

    /**
     * Recursively search all available combinations from a single cell
     */
    private fun depthFirstSearch(row: Int, column: Int, visitedPath: MutableList<Pair<Int, Int>>, currentCombination: String) {
        val letter = board[row][column]
        visitedPath.add(row to column)

        val newCombination = currentCombination + letter
        if (prefixTree.contains(newCombination)) {
            allCombinations.add(newCombination)
        }

        val completions = prefixTree.complete(newCombination)
        if (completions.isEmpty()) {
            return
        }

        val currentNeighbors = getNeighbors(row, column)
        for (neighbor in currentNeighbors) {
            if (neighbor !in visitedPath) {
                depthFirstSearch(neighbor.first, neighbor.second, visitedPath.toMutableList(), newCombination)
            }
        }
    }

    /**
     * Perform a depth first search for each cell in matrix
     */
    fun allSearches(): Set<String> {
        for (rowIndex in 0 until rowLength) {
            for (columnIndex in 0 until columnLength) {
                depthFirstSearch(rowIndex, columnIndex, mutableListOf(), "")
            }
        }

        return allCombinations.filter { it.length > 2 }.toSet()
    }
}
