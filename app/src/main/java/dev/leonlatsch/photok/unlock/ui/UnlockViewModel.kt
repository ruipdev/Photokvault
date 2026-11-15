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

package dev.leonlatsch.photok.unlock.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.leonlatsch.photok.encryption.domain.VaultService
import dev.leonlatsch.photok.encryption.domain.models.UnlockRequest
import dev.leonlatsch.photok.security.PasswordAttemptsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UnlockViewModel @Inject constructor(
    application: Application,
    private val vaultService: VaultService,
) : AndroidViewModel(application) {

    private val attemptsUseCase = PasswordAttemptsUseCase(application)

    private val _uiState = MutableStateFlow(UnlockUiState())
    val uiState: StateFlow<UnlockUiState> = _uiState

    init {
        updateLockStatus()
    }

    private fun updateLockStatus() {
        if (attemptsUseCase.isLocked()) {
            val lockUntil = System.currentTimeMillis() + (attemptsUseCase.getRemainingLockTimeSeconds() * 1000)
            _uiState.update { it.copy(lockedUntil = lockUntil) }
        } else {
            _uiState.update { it.copy(lockedUntil = null) }
        }
    }

    fun onPasswordChange(inputValue: String) {
        _uiState.update { it.copy(inputValue = inputValue, wrongPassword = false) }
    }

    fun unlock() {
        if (attemptsUseCase.isLocked()) return

        _uiState.update { it.copy(loading = true, wrongPassword = false) }
        
        viewModelScope.launch {
            try {
                val request = UnlockRequest.Password(_uiState.value.inputValue)
                val result = vaultService.unlock(request)
                
                if (result.isSuccess) {
                    attemptsUseCase.resetAttempts()
                    // Sinaliza que o desbloqueio foi bem-sucedido
                    _uiState.update { it.copy(isUnlocked = true, loading = false) }
                } else {
                    throw Exception("Senha incorreta")
                }
            } catch (e: Exception) {
                attemptsUseCase.incrementAttempts()
                updateLockStatus()
                
                _uiState.update { 
                    it.copy(
                        loading = false, 
                        wrongPassword = true,
                        inputValue = "",
                        lockedUntil = if (attemptsUseCase.isLocked()) it.lockedUntil else null
                    ) 
                }
            }
        }
    }
}
