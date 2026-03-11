package com.example.productbrowser.di

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.android.Android

actual fun createHttpClientEngine(): HttpClientEngineFactory<*> = Android

