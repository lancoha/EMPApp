package com.example.empapp

import TwelveDataApi
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.empapp.ui.theme.EMPAppTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.github.mikephil.charting.data.Entry
import kotlinx.coroutines.flow.stateIn


class MainActivity : ComponentActivity() {
    private val viewModel: AssetViewModel by viewModels {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val repo = AssetRepository.getInstance(applicationContext)
                return AssetViewModel(repo) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.twelvedata.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api = retrofit.create(TwelveDataApi::class.java)
        val apiKey = "849555d1edc54fabb611c9c13c62c0ea"
        val dataFetcher = DataFetcher(api, apiKey)

        fun saveDataToDatabase(symbol: String, isFavourite: Boolean, entries: List<Pair<String, Entry>>) {
            lifecycleScope.launch {
                val repo = AssetRepository.getInstance(applicationContext)

                if (isFavourite) {
                    val dailyDataList = entries.map { (date, entry) ->
                        AssetDailyData(
                            assetId = symbol,
                            datetime = date,
                            close = entry.y.toDouble()
                        )
                    }

                    viewModel.addNewAsset(symbol, true)

                    if (dailyDataList.isNotEmpty()) {
                        viewModel.addDailyDataForAsset(dailyDataList)
                    }
                } else {
                    val currentAssets = repo.getAllAssets()
                    val assetList = currentAssets.stateIn(this).value
                    val assetExists = assetList.any { it.id == symbol }

                    if (!assetExists) {

                        viewModel.addNewAsset(symbol, false)
                    }

                    repo.updateFavouriteStatus(symbol, false)
                }
            }
        }
        //test PB
        dataFetcher.getStockData("AAPL") { entries ->
            saveDataToDatabase("AAPL", false, entries)
        }

        dataFetcher.getStockData("BTC/USD") { entries ->
            saveDataToDatabase("BTC/USD", false, entries)
        }

        dataFetcher.getStockData("TSLA") { entries ->
            saveDataToDatabase("TSLA", false, entries)
        }

        dataFetcher.getStockData("ETH/USD") { entries ->
            saveDataToDatabase("ETH/USD", true, entries)
        }

        lifecycleScope.launch {
            viewModel.allAssets.collect { assets ->
                Log.d("TEST_ASSETS", "Vsi asseti v bazi: $assets")
            }
        }

        lifecycleScope.launch {
            viewModel.getDailyDataFlow("AAPL").collect { aaplData ->
                Log.d("TEST_AAPL_DATA", "AAPL daily data: $aaplData")
            }
        }

        enableEdgeToEdge()
        setContent {
            EMPAppTheme {

                SetBarColor(color = MaterialTheme.colorScheme.background)

                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                    HomeScreen(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)

                    )
                }
            }
        }
    }

    @Composable
    private fun SetBarColor(color : Color){
        val systemUiController = rememberSystemUiController()
        SideEffect {
            systemUiController.setSystemBarsColor(
                color = color
            )
        }
    }
}

@Composable
fun HomeScreen(name: String, modifier: Modifier = Modifier) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomNavigationBar(navController)
        }
    ) { padding ->

        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(padding)
        ) {
            composable("home") {
                HomeScreenContent()
            }

            composable("charts") {
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val intent = Intent(context, ChartsScreen::class.java)
                    context.startActivity(intent)
                }
            }

            composable("explore") {
                ExploreScreen(navController = navController)
            }

            composable("widget") {
                WidgetScreen()
            }

            composable("allCoinsScreen") {
                AllCoinsScreen()
            }

            composable("allStocksScreen") {
                AllStocksScreen()
            }
        }
    }
}

@Composable
fun HomeScreenContent(){
    Column(
        modifier = Modifier.fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(0.dp)
    ) {
        TitleSection()
        CryptoSection()
        Spacer(modifier = Modifier.height(16.dp))
        StocksSection()
        Spacer(modifier = Modifier.height(16.dp))
        LearnSection()
    }
}


@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    EMPAppTheme {
        HomeScreen("Android")
    }
}