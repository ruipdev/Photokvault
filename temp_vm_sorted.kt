
            )
            }
        )
        }
    }
 *
 */
}
/*
class GalleryViewModel @Inject constructor(
 *   Copyright 2020–2026 Leon Latsch
 *   distributed under the License is distributed on an "AS IS" BASIS,
        eventsChannel.trySend(navEvent)
                fileUris = choice.fileUris,
    fun handleUiEvent(event: GalleryUiEvent) {
    fun toggleBlur() {
            GalleryUiEvent.CancelAlbumSelection -> showAlbumSelectionDialog.value = false
        galleryUiStateFactory.create(photos, showAlbumSelection, sort)
@HiltViewModel
 *        http://www.apache.org/licenses/LICENSE-2.0
import android.content.res.Resources
import android.net.Uri
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.leonlatsch.photok.gallery.albums.domain.AlbumRepository
import dev.leonlatsch.photok.gallery.components.ImportChoice
import dev.leonlatsch.photok.gallery.components.PhotoTile
import dev.leonlatsch.photok.gallery.ui.importing.SharedUrisStore
import dev.leonlatsch.photok.gallery.ui.navigation.GalleryNavigationEvent
import dev.leonlatsch.photok.gallery.ui.navigation.PhotoAction
import dev.leonlatsch.photok.model.repositories.ImportSource
import dev.leonlatsch.photok.model.repositories.PhotoRepository
import dev.leonlatsch.photok.R
import dev.leonlatsch.photok.settings.data.Config
import dev.leonlatsch.photok.sort.domain.SortConfig
import dev.leonlatsch.photok.sort.domain.SortRepository
import javax.inject.Inject
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
                importSource = ImportSource.InApp,
        _isBlurred.value = !_isBlurred.value
            is GalleryUiEvent.OnAddToAlbum -> showAlbumSelectionDialog.value = true
            is GalleryUiEvent.OnDelete -> onDeleteSelectedItems(event.items)
            is GalleryUiEvent.OnExport -> onExportSelectedItems(event.items, event.target)
            is GalleryUiEvent.OnImportChoice -> onImportChoice(event.choice)
            is GalleryUiEvent.OpenPhoto -> navigateToPhoto(event.item)
            is GalleryUiEvent.SortChanged -> viewModelScope.launch {
            is ImportChoice.AddNewFiles -> GalleryNavigationEvent.StartImport(
            is ImportChoice.RestoreBackup -> GalleryNavigationEvent.StartRestoreBackup(choice.backupUri)
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   limitations under the License.
    @OptIn(ExperimentalCoroutinesApi::class)
package dev.leonlatsch.photok.gallery.ui
            PhotoAction.DeletePhotos(
            PhotoAction.ExportPhotos(
        photoActionsChannel.trySend(
        photoActionsChannel.trySend(PhotoAction.OpenPhoto(item.uuid))
        photoRepository.observeAll(sort)
    photoRepository: PhotoRepository,
        photosFlow,
                photosFlow.value.filter { selectedItems.contains(it.uuid) }
                photosFlow.value.filter { selectedItems.contains(it.uuid) },
    ) { photos, showAlbumSelection, sort ->
    private fun navigateToPhoto(item: PhotoTile) {
    private fun onDeleteSelectedItems(selectedItems: List<String>) {
    private fun onExportSelectedItems(selectedItems: List<String>, target: Uri?) {
    private fun onImportChoice(choice: ImportChoice) {
    private val config: Config,
    private val eventsChannel = Channel<GalleryNavigationEvent>(Channel.UNLIMITED)
    private val galleryUiStateFactory: GalleryUiStateFactory,
    private val _isBlurred = MutableStateFlow(config.galleryBlurEnabled)
    private val photoActionsChannel = Channel<PhotoAction>(Channel.UNLIMITED)
    private val photosFlow = sortFlow.flatMapLatest { sort ->
    private val showAlbumSelectionDialog = MutableStateFlow(false)
    private val sortFlow = sortRepository.observeSortFor(albumUuid = null, default = SortConfig.Gallery.default)
    private val sortRepository: SortRepository,
 *   See the License for the specific language governing permissions and
        showAlbumSelectionDialog,
        sortFlow,
                sortRepository.updateSortFor(albumUuid = null, sort = event.sort)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), GalleryUiState.Empty)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())
                target,
        target ?: return
 *   Unless required by applicable law or agreed to in writing, software
    val eventsFlow = eventsChannel.receiveAsFlow()
    val isBlurred = _isBlurred.asStateFlow()
        val navEvent = when (choice) {
    val photoActions = photoActionsChannel.receiveAsFlow()
    val uiState: StateFlow<GalleryUiState> = combine(
) : ViewModel() {
        when (event) {
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
