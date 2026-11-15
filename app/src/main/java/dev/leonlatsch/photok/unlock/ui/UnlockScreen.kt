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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.leonlatsch.photok.R
import dev.leonlatsch.photok.ui.components.PasswordField
import dev.leonlatsch.photok.ui.theme.Dimens
import kotlinx.coroutines.delay

@Composable
fun UnlockScreen(
    onUnlockSuccess: () -> Unit,
    viewModel: UnlockViewModel = hiltViewModel(),
) {
    val uiState: UnlockUiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Se o desbloqueio foi bem-sucedido, chama a função de sucesso
    LaunchedEffect(uiState.isUnlocked) {
        if (uiState.isUnlocked) {
            onUnlockSuccess()
        }
    }

    // Se estiver bloqueado, mostra o ecrã de contagem decrescente
    if (uiState.lockedUntil != null) {
        LockedContent(uiState.lockedUntil!!)
        return
    }

    // Caso contrário, mostra o ecrã normal de desbloqueio
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = stringResource(R.string.unlock_title),
            style = MaterialTheme.typography.headlineMedium
        )
        
        Spacer(modifier = Modifier.height(Dimens.spacingMedium))

        PasswordField(
            value = uiState.inputValue,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = stringResource(R.string.password),
            error = if (uiState.wrongPassword) "Senha incorreta" else null,
            onDone = { viewModel.unlock() }
        )
        
        if (uiState.loading) {
            Spacer(modifier = Modifier.height(Dimens.spacingSmall))
            Text(text = "A verificar...")
        }
    }
}

@Composable
private fun LockedContent(lockedUntilTimestamp: Long) {
    var secondsLeft by remember { mutableLongStateOf(0L) }

    LaunchedEffect(lockedUntilTimestamp) {
        while (true) {
            val now = System.currentTimeMillis()
            val remaining = (lockedUntilTimestamp - now) / 1000
            if (remaining <= 0) break
            secondsLeft = remaining
            delay(1000L)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.spacingLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "🔒 App Bloqueada",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(Dimens.spacingMedium))
        Text(
            text = "Demasiadas tentativas falhadas.",
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(Dimens.spacingSmall))
        Text(
            text = "Tente novamente em ${secondsLeft / 60}:${String.format("%02d", secondsLeft % 60)}",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
