package com.example.android.alephba.data.source

import com.example.android.alephba.data.model.Response
import kotlinx.coroutines.flow.Flow

interface PriceRepository {
    suspend fun updateBitcoinPrice(): Response<Unit>
    suspend fun getBitcoinPrice(): Flow<String?>
}