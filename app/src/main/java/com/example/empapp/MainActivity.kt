package com.example.empapp

import AllCoinsScreen
import AllStocksScreen
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            EMPAppTheme {

                SetBarColor(color = MaterialTheme.colorScheme.background)

                val navigateTo = intent.getStringExtra("navigate_to")

                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = MaterialTheme.colorScheme.background) { innerPadding ->
                    HomeScreen(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        favouriteAsset("BTC/USD")  //na te 2 se zveze un favourite/unfavourite logic - ce je favouritan se shrani data v PB
        unfavouriteAsset("TSLA")
    }
    object GlobalVariables {
        val ChartSymbol = "BTC/USD" //kar je v ChartSymbol se izpise na chart <- link
    }
    fun favouriteAsset(assetId: String) {
        val repo = AssetRepository.getInstance(applicationContext)

        CoroutineScope(Dispatchers.IO).launch {
            repo.updateFavouriteStatus(assetId, true)
        }
    }
    private fun unfavouriteAsset(assetId: String) {
        val repo = AssetRepository.getInstance(applicationContext)

        CoroutineScope(Dispatchers.IO).launch {
            repo.updateFavouriteStatus(assetId, false)

            repo.deleteAllDailyDataForAsset(assetId)
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