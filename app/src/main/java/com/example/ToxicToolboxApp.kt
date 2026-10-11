package com.example

import android.app.Application
import com.example.data.local.ToxicDatabase
import com.example.data.local.preferences.ToolboxPreferences
import com.example.data.repository.ToolboxRepository
import com.example.domain.usecase.DnsUseCase
import com.example.domain.usecase.EncodingUseCase
import com.example.domain.usecase.HashUseCase
import com.example.domain.usecase.HttpInspectorUseCase
import com.example.domain.usecase.JsonUseCase
import com.example.domain.usecase.JwtUseCase
import com.example.domain.usecase.NetworkDiagnosticsUseCase
import com.example.domain.usecase.RandomGeneratorUseCase
import com.example.domain.usecase.TextLabUseCase
import com.example.domain.usecase.UtilitiesUseCase

class ToxicToolboxApp : Application() {

    lateinit var database: ToxicDatabase
        private set

    lateinit var preferences: ToolboxPreferences
        private set

    lateinit var repository: ToolboxRepository
        private set

    val jsonUseCase by lazy { JsonUseCase() }
    val encodingUseCase by lazy { EncodingUseCase() }
    val hashUseCase by lazy { HashUseCase() }
    val textLabUseCase by lazy { TextLabUseCase() }
    val randomGeneratorUseCase by lazy { RandomGeneratorUseCase() }
    val jwtUseCase by lazy { JwtUseCase() }
    val httpInspectorUseCase by lazy { HttpInspectorUseCase() }
    val dnsUseCase by lazy { DnsUseCase() }
    val networkDiagnosticsUseCase by lazy { NetworkDiagnosticsUseCase(this) }
    val utilitiesUseCase by lazy { UtilitiesUseCase() }

    override fun onCreate() {
        super.onCreate()
        database = ToxicDatabase.getInstance(this)
        preferences = ToolboxPreferences(this)
        repository = ToolboxRepository(database, preferences)
    }
}
