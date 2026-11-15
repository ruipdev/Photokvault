/*
 *   Copyright 2020–2026 Leon Latsch
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package dev.leonlatsch.photok.gallery.components

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Composable
fun rememberMultiSelectionState(
    items: List<String>,
    dateMap: Map<String, Long> = emptyMap()
) = remember(items, dateMap) {
    MultiSelectionState(items, dateMap)
}

@Composable
fun MultiSelectionBackHandler(state: MultiSelectionState) {
    BackHandler(enabled = state.isActive.value) {
        state.cancelSelection()
    }
}

class MultiSelectionState(
    private val allItems: List<String>,
    private val dateMap: Map<String, Long> = emptyMap(),
) {
    var isActive = mutableStateOf(false)
    var selectedItems = mutableStateOf(emptyList<String>())
    val showMore = mutableStateOf(false)

    fun selectAll() {
        isActive.value = true
        selectedItems.value = allItems
    }

    fun selectSameDay() {
        val refDate = getReferenceDate() ?: return
        isActive.value = true
        selectedItems.value = allItems.filter { getDate(it) == refDate }
    }

    fun selectSameMonth() {
        val refDate = getReferenceDate() ?: return
        isActive.value = true
        selectedItems.value = allItems.filter { 
            val d = getDate(it)
            d != null && d.year == refDate.year && d.month == refDate.month
        }
    }

    fun selectSameYear() {
        val refDate = getReferenceDate() ?: return
        isActive.value = true
        selectedItems.value = allItems.filter { 
            val d = getDate(it)
            d != null && d.year == refDate.year
        }
    }

    private fun getReferenceDate(): LocalDate? {
        val refUuid = selectedItems.value.firstOrNull() ?: allItems.firstOrNull() ?: return null
        return getDate(refUuid)
    }

    private fun getDate(uuid: String): LocalDate? {
        val timestamp = dateMap[uuid] ?: return null
        if (timestamp == 0L) return null
        return Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate()
    }

    fun cancelSelection() {
        isActive.value = false
        selectedItems.value = emptyList()
    }

    fun selectItem(uuid: String) {
        isActive.value = true
        selectedItems.value += uuid
    }

    fun deselectItem(uuid: String) {
        if (selectedItems.value.size == 1) {
            isActive.value = false
        }
        selectedItems.value -= uuid
    }

    fun showMore() {
        showMore.value = true
    }

    fun dismissMore() {
        showMore.value = false
    }
}
