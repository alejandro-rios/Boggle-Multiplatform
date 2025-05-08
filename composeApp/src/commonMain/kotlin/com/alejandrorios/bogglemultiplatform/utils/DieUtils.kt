package com.alejandrorios.bogglemultiplatform.utils

import androidx.compose.foundation.lazy.grid.LazyGridItemInfo
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerInputChange
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect
import androidx.compose.ui.unit.toOffset
import kotlin.math.*

// Constants
private const val CIRCLE_RADIUS_FACTOR = 0.45f
private const val DIAGONAL_ALIGNMENT_BOOST = 1.5f
private const val DIAGONAL_MISALIGNMENT_PENALTY = 0.5f
private const val DIAGONAL_DETECTION_THRESHOLD = 0.5f

/**
 * Finds the best matching item info based on the drag direction and position.
 */
internal fun findBestMatchingItemInfo(
    change: PointerInputChange,
    lazyGridState: LazyGridState,
    dragDirection: Offset,
    lastDragPosition: Offset
): LazyGridItemInfo? {
    val nearbyItems = lazyGridState.layoutInfo.visibleItemsInfo.filter { itemInfo ->
        val expandedRect = itemInfo.size.toIntRect().inflate(itemInfo.size.width / 2)
        expandedRect.contains((change.position - itemInfo.offset.toOffset()).round())
    }

    return when (nearbyItems.size) {
        0 -> null
        1 -> nearbyItems[0]
        else -> {
            val isDiagonalMove = abs(dragDirection.x) > DIAGONAL_DETECTION_THRESHOLD &&
                    abs(dragDirection.y) > DIAGONAL_DETECTION_THRESHOLD

            nearbyItems.minByOrNull { itemInfo ->
                val itemCenter = itemInfo.offset.toOffset() +
                        Offset(itemInfo.size.width / 2f, itemInfo.size.height / 2f)
                val vectorToItem = itemCenter - lastDragPosition

                if (dragDirection.getDistance() > 0) {
                    val dotProduct = dragDirection.x * vectorToItem.x + dragDirection.y * vectorToItem.y
                    val dragMagnitude = dragDirection.getDistance()
                    val vectorMagnitude = vectorToItem.getDistance()

                    val cosTheta = if (dragMagnitude > 0 && vectorMagnitude > 0)
                        dotProduct / (dragMagnitude * vectorMagnitude) else 0f

                    if (isDiagonalMove) {
                        val isInDragDirection = dragDirection.x * vectorToItem.x > 0 &&
                                dragDirection.y * vectorToItem.y > 0
                        -cosTheta * (if (isInDragDirection) DIAGONAL_ALIGNMENT_BOOST else DIAGONAL_MISALIGNMENT_PENALTY)
                    } else -cosTheta
                } else vectorToItem.getDistance()
            }
        }
    }
}

/**
 * Checks if a point is inside the circular area of a die.
 */
internal fun isInsideCircle(point: Offset, itemOffset: Offset, itemSize: Int): Boolean {
    val center = itemOffset + Offset(itemSize / 2f, itemSize / 2f)
    val distance = sqrt((point.x - center.x).pow(2) + (point.y - center.y).pow(2))
    return distance <= itemSize * CIRCLE_RADIUS_FACTOR
}

/**
 * Handles the selection of a new die.
 */
internal fun handleNewDieSelection(
    key: Int,
    selectedKeys: MutableList<Int>,
    selectedIds: MutableState<Set<Int>>,
    update: (List<Int>, Boolean) -> Unit
) {
    if (key in selectedKeys) {
        selectedKeys.removeAll(selectedKeys.subList(selectedKeys.indexOf(key) + 1, selectedKeys.size))
        selectedIds.value = selectedKeys.toSet()
    } else {
        selectedKeys.add(key)
        selectedIds.value += key
    }
    update(selectedIds.value.toList(), false)
}
