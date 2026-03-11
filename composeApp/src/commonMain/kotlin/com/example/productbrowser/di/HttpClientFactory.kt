package com.example.productbrowser.di

import io.ktor.client.engine.HttpClientEngineFactory

expect fun createHttpClientEngine(): HttpClientEngineFactory<*>

