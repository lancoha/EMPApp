package com.example.empapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AssetViewModel(private val repository: AssetRepository) : ViewModel() {
    val allAssets = repository.getAllAssets()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val recentAssets = repository.getRecents()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val favouriteAssets = allAssets.map { assets ->
        assets.filter { it.isFavourite }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val popularAssets = repository.getTop5Popular()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun addNewAsset(id: String, isFavourite: Boolean) {
        viewModelScope.launch {
            repository.insertAsset(Asset(id, isFavourite))
        }
    }

    fun setFavourite(assetId: String, isFav: Boolean) {
        viewModelScope.launch {
            repository.updateFavouriteStatus(assetId, isFav)
        }
    }

    fun addDailyDataForAsset(dataList: List<AssetDailyData>) {
        viewModelScope.launch {
            repository.insertAllDailyData(dataList)
        }
    }

    fun getDailyDataFlow(assetId: String) = repository.getDailyDataForAsset(assetId)

    fun addRecent(assetId: String) {
        viewModelScope.launch {
            repository.insertRecent(assetId)
        }
    }

    fun trackClick(assetId: String) {
        viewModelScope.launch {
            repository.incrementClickCount(assetId)
        }
    }
}
