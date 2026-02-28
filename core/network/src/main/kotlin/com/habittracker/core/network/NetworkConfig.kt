package com.habittracker.core.network

/**
 * This module is reserved for future backend synchronization.
 *
 * It will provide:
 * - Retrofit API client for syncing habits and completion logs
 * - Interceptors for authentication and certificate pinning
 * - Network state detection and offline handling
 *
 * For now, the app operates in offline-first mode with Room as the source of truth.
 */
object NetworkConfig {
    const val BASE_URL = "https://api.habittracker.com/"
    const val CONNECT_TIMEOUT_SECONDS = 30
    const val READ_TIMEOUT_SECONDS = 30
    const val WRITE_TIMEOUT_SECONDS = 30

    // Certificate pins will be configured here when backend is added
    val SSL_PINS = listOf<String>()
}
