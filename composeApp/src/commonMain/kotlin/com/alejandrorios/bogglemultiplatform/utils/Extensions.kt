package com.alejandrorios.bogglemultiplatform.utils

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toOffset
import kotlin.math.abs

/**
 * Checks if two dice are adjacent on the Boggle board
 *
 * @param newIndex The index of the new die to check
 * @return true if the dice are adjacent, false otherwise
 */
fun List<Int>.areIndicesAdjacent(newIndex: Int) : Boolean{
    if(this.isEmpty()) return false

    if(this.last() == newIndex) return true

    // In a 4x4 grid, convert indices to row,col positions
    val currentRow = this.last() / 4
    val currentCol = this.last() % 4
    val newRow = newIndex / 4
    val newCol = newIndex % 4

    // Calculate the distance between the positions
    val rowDiff = abs(currentRow - newRow)
    val colDiff = abs(currentCol - newCol)

    // Dice are adjacent if they are at most 1 position away in any direction
    // (including diagonals)
    return rowDiff <= 1 && colDiff <= 1 && !(rowDiff == 0 && colDiff == 0)
}

/**
 * Finds the grid item key at the given position.
 *
 * @param hitPoint The position to check.
 * @return The grid item key, or null if no item is found.
 */
fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): LazyGridItemInfo? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains((hitPoint - itemInfo.offset.toOffset()).round())
    }

// Extension function to get the die key
fun LazyGridItemInfo.dieKey(): Int = this.key as Int
