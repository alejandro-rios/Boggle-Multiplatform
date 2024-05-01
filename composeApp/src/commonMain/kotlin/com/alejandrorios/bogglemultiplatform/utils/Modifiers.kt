package com.alejandrorios.bogglemultiplatform.utils

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.round
import androidx.compose.ui.unit.toIntRect

/**
 * Code implementation taken and modified from:
 * https://medium.com/androiddevelopers/create-a-photo-grid-with-multiselect-behavior-using-jetpack-compose-9a8d588a9b63
 */

fun Modifier.photoGridDragHandler(
    lazyGridState: LazyGridState,
    selectedIds: MutableState<Set<Int>>,
    onDragEnded: () -> Unit,
    update: (values: List<Int>, isFromTap: Boolean) -> Unit
) = pointerInput(Unit) {
    var currentKey: Int? = null
    val selectedKeys = mutableListOf<Int>()

    detectDragGestures(
        onDragStart = { offset ->
            lazyGridState.gridItemKeyAtPosition(offset)?.let { key ->
                currentKey = key
                selectedKeys.clear()
                selectedKeys.add(key)
                selectedIds.value = setOf(key)
            }
        },
        onDragCancel = {
            currentKey = null
            selectedKeys.clear()
            selectedIds.value = emptySet()
        },
        onDragEnd = {
            currentKey = null
            selectedKeys.clear()
            selectedIds.value = emptySet()
            onDragEnded()
        },
        onDrag = { change, _ ->
            lazyGridState.gridItemKeyAtPosition(change.position)?.let { key ->
                if (key != currentKey) {
                    if (selectedKeys.contains(key)) {
                        val keysToRemove = selectedKeys.subList(selectedKeys.indexOf(key) + 1, selectedKeys.size)
                        selectedKeys.removeAll(keysToRemove)
                        selectedIds.value = emptySet()
                        selectedIds.value = selectedKeys.toSet()
                    } else {
                        selectedKeys.add(key)
                        selectedIds.value += key
                    }
                    update(selectedIds.value.toList(), false)
                    currentKey = key
                }
            }
        }
    )
}

fun LazyGridState.gridItemKeyAtPosition(hitPoint: Offset): Int? =
    layoutInfo.visibleItemsInfo.find { itemInfo ->
        itemInfo.size.toIntRect().contains(hitPoint.round() - itemInfo.offset)
    }?.key as? Int
