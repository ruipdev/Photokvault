/*
 *   Copyright 2020-2026 Leon Latsch
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

package dev.leonlatsch.photok.pro

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import dev.leonlatsch.photok.pro.domain.PasswordAttemptsResult
import dev.leonlatsch.photok.pro.domain.PasswordAttemptsUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PasswordAttemptsUseCaseImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : PasswordAttemptsUseCase {

    private val prefs: SharedPreferences = context.getSharedPreferences(
        "photok_security_lockout_prefs", 
        Context.MODE_PRIVATE
    )

    private companion object {
        const val KEY_ATTEMPTS = "failed_attempts"
        const val KEY_LOCKOUT_UNTIL = "lockout_until"
        const val MAX_ATTEMPTS = 6
        const val LOCKOUT_DURATION_MS = 30 * 60 * 1000L // 30 minutos em milissegundos
    }

    override fun currentLockout(): Long {
        val lockoutUntil = prefs.getLong(KEY_LOCKOUT_UNTIL, 0L)
        val now = System.currentTimeMillis()

        if (lockoutUntil > now) {
            // Ainda está bloqueado
            return lockoutUntil
        } else if (lockoutUntil > 0) {
            // O bloqueio expirou, limpar contadores
            resetAttempts()
        }
        return 0L
    }

    override fun onFailedAttempt(): PasswordAttemptsResult {
        // Verifica se já está bloqueado antes de contar mais uma
        val currentLockout = currentLockout()
        if (currentLockout > 0) {
            return PasswordAttemptsResult.Locked(currentLockout)
        }

        val attempts = prefs.getInt(KEY_ATTEMPTS, 0) + 1
        prefs.edit().putInt(KEY_ATTEMPTS, attempts).apply()

        if (attempts >= MAX_ATTEMPTS) {
            val lockoutUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS
            prefs.edit().putLong(KEY_LOCKOUT_UNTIL, lockoutUntil).apply()
            return PasswordAttemptsResult.Locked(lockoutUntil)
        }

        return PasswordAttemptsResult.None
    }

    override fun onSuccessfulUnlock() {
        resetAttempts()
    }

    private fun resetAttempts() {
        prefs.edit()
            .putInt(KEY_ATTEMPTS, 0)
            .putLong(KEY_LOCKOUT_UNTIL, 0L)
            .apply()
    }
}
