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

package dev.leonlatsch.photok.pro.domain

/**
 * Number of consecutive failed password attempts that triggers the lockout.
 */
const val MAX_PASSWORD_ATTEMPTS = 6

/**
 * Duration the app stays locked after [MAX_PASSWORD_ATTEMPTS] failed password attempts.
 */
val LOCKOUT_DURATION_MS: Long = 30L * 60L * 1000L // 30 minutes
