package dev.leonlatsch.photok.security

import android.content.Context
import androidx.core.content.edit
import java.time.Instant

class PasswordAttemptsUseCase(private val context: Context) {
    companion object {
        private const val PREFS_NAME = "security_attempts"
        private const val KEY_ATTEMPTS = "failed_attempts"
        private const val KEY_LOCK_UNTIL = "lock_until_timestamp"
        const val MAX_ATTEMPTS = 6
        private const val LOCK_DURATION_MS = 30 * 60 * 1000L // 30 minutos
    }

    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun incrementAttempts() {
        val current = prefs.getInt(KEY_ATTEMPTS, 0) + 1
        prefs.edit { putInt(KEY_ATTEMPTS, current) }
        
        if (current >= MAX_ATTEMPTS) {
            val lockUntil = Instant.now().toEpochMilli() + LOCK_DURATION_MS
            prefs.edit { putLong(KEY_LOCK_UNTIL, lockUntil) }
        }
    }

    fun resetAttempts() {
        prefs.edit { 
            putInt(KEY_ATTEMPTS, 0) 
            remove(KEY_LOCK_UNTIL)
        }
    }

    fun isLocked(): Boolean {
        val lockUntil = prefs.getLong(KEY_LOCK_UNTIL, 0)
        return lockUntil > Instant.now().toEpochMilli()
    }

    fun getRemainingLockTimeSeconds(): Long {
        val lockUntil = prefs.getLong(KEY_LOCK_UNTIL, 0)
        val now = Instant.now().toEpochMilli()
        return if (lockUntil > now) (lockUntil - now) / 1000 else 0
    }
}
