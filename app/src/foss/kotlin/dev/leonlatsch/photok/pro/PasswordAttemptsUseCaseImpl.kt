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

import dev.leonlatsch.photok.pro.domain.LOCKOUT_DURATION_MS
import dev.leonlatsch.photok.pro.domain.MAX_PASSWORD_ATTEMPTS
import dev.leonlatsch.photok.pro.domain.PasswordAttemptsResult
import dev.leonlatsch.photok.pro.domain.PasswordAttemptsUseCase
import dev.leonlatsch.photok.settings.data.PasswordAttemptStore
import javax.inject.Inject

/**
 * FOSS implementation of the password attempt lockout:
 * after [MAX_PASSWORD_ATTEMPTS] failed attempts the vault stays locked for [LOCKOUT_DURATION_MS].
 */
class PasswordAttemptsUseCaseImpl @Inject constructor(
    private val store: PasswordAttemptStore,
) : PasswordAttemptsUseCase {

    override fun onFailedAttempt(): PasswordAttemptsResult {
        val attempts = store.failedAttempts + 1

        if (attempts >= MAX_PASSWORD_ATTEMPTS) {
            val lockedUntil = System.currentTimeMillis() + LOCKOUT_DURATION_MS
            store.lockedUntilMillis = lockedUntil
            store.failedAttempts = 0
            return PasswordAttemptsResult.Locked(lockedUntil)
        }

        store.failedAttempts = attempts
        return PasswordAttemptsResult.None
    }

    override fun onSuccessfulUnlock() {
        store.failedAttempts = 0
        store.lockedUntilMillis = null
    }

    override fun currentLockout(): Long {
        val lockedUntil = store.lockedUntilMillis ?: return 0L

        if (lockedUntil <= System.currentTimeMillis()) {
            store.lockedUntilMillis = null
            store.failedAttempts = 0
            return 0L
        }

        return lockedUntil
    }
}
