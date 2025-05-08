package com.alejandrorios.bogglemultiplatform.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.toOffset

/**
 * Code implementation taken and modified from:
 * https://medium.com/androiddevelopers/create-a-photo-grid-with-multiselect-behavior-using-jetpack-compose-9a8d588a9b63
 */
fun Modifier.boggleBoardDragHandler(
    lazyGridState: LazyGridState,
    selectedIds: MutableState<Set<Int>>,
    onDragEnded: () -> Unit,
    update: (values: List<Int>, isFromTap: Boolean) -> Unit
) = pointerInput(Unit) {
    var currentKey: Int? = null
    val selectedKeys = mutableListOf<Int>()
    var lastDragPosition = Offset.Zero

    detectDragGestures(
        onDragStart = { offset ->
            lastDragPosition = offset
            lazyGridState.gridItemKeyAtPosition(offset)?.let { itemInfo ->
                // Allow touch to start if it's anywhere within the circle area
                if (isInsideCircle(offset, itemInfo.offset.toOffset(), itemInfo.size.width)) {
                    val key = itemInfo.dieKey()
                    currentKey = key
                    selectedKeys.clear()
                    selectedKeys.add(key)
                    selectedIds.value = setOf(key)
                    update(selectedIds.value.toList(), false) // Immediately update on touch
                }
            }
        },
        onDragCancel = {
            currentKey = null
            selectedKeys.clear()
            selectedIds.value = emptySet()
            lastDragPosition = Offset.Zero
        },
        onDragEnd = {
            currentKey = null
            selectedKeys.clear()
            selectedIds.value = emptySet()
            lastDragPosition = Offset.Zero
            onDragEnded()
        },
        onDrag = { change, _ ->
            // Calculate drag direction
            val dragDirection = change.position - lastDragPosition
            lastDragPosition = change.position

            // Find the best matching die based on drag direction and position
            val closestItemInfo = findBestMatchingItemInfo(change, lazyGridState, dragDirection, lastDragPosition)

            if (closestItemInfo != null) {
                val key = closestItemInfo.dieKey()

                // Check if the touch is within the circle and it's a new die
                if (isInsideCircle(
                        change.position,
                        closestItemInfo.offset.toOffset(),
                        closestItemInfo.size.width
                    ) && key != currentKey
                ) {
                    // Only proceed if the new die is adjacent to the current die
                    val lastSelectedKey = selectedKeys.lastOrNull()
                    if (lastSelectedKey != null && !selectedKeys.areIndicesAdjacent(key)) {
                        // Not adjacent, don't add to selection
                        return@detectDragGestures
                    }

                    handleNewDieSelection(
                        key,
                        selectedKeys,
                        selectedIds,
                        update
                    )
                    currentKey = key
                }
            }
        }
    )
}

/**
 * Modifier for a Boggle die.
 *
 * @param interactionSource The interaction source.
 * @param onClick The click handler.
 * @param selectedKeys The list of selected keys.
 * @param index The index of the die.
 * @return The modifier.
 */
fun Modifier.boggleDieModifier(
    interactionSource: MutableInteractionSource,
    onClick: () -> Unit,
    selectedKeys: List<Int>,
    index: Int
) = this.clickable(
    interactionSource = interactionSource,
    indication = null,
    onClick = {
        // Check if this is the first selection or adjacent to the last selection
        if (selectedKeys.isEmpty() || selectedKeys.areIndicesAdjacent(index)) {
            onClick()
        }
    }
)
