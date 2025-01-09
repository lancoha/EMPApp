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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.empapp.Chart.ChartsScreen
import com.example.empapp.Database.AssetRepository
import com.example.empapp.Database.AssetViewModel
import com.example.empapp.MainScreen.CryptoSection
import com.example.empapp.MainScreen.LearnSection
import com.example.empapp.MainScreen.PopularSection
import com.example.empapp.MainScreen.StocksSection
import com.example.empapp.MainScreen.TitleSection
import com.example.empapp.ui.theme.EMPAppTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
    }

    object GlobalVariables {
        var ChartSymbol = "BTC/USD"
    }

    @Composable
    private fun SetBarColor(color: Color){
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
                val context = LocalContext.current
                val repository = AssetRepository.getInstance(context)
                val assetViewModel = AssetViewModel(repository)
                HomeScreenContent(navController = navController, assetViewModel = assetViewModel)
            }

            composable("charts") {
                val context = LocalContext.current
                LaunchedEffect(Unit) {
                    val intent = Intent(context, ChartsScreen::class.java)
                    context.startActivity(intent)
                }
            }

            composable("explore") {
                val context = LocalContext.current
                val assetRepository = AssetRepository.getInstance(context)
                val assetViewModel = AssetViewModel(assetRepository)
                ExploreScreen(navController = navController, assetViewModel = assetViewModel)
            }

            composable("allCoinsScreen") {
                AllCoinsScreen(navController = navController)
            }

            composable("allStocksScreen") {
                AllStocksScreen(navController = navController)
            }

            composable("favourites") {
                val context = LocalContext.current
                val repository = AssetRepository.getInstance(context)
                val assetViewModel = AssetViewModel(repository)
                FavouritesScreen(navController = navController, assetViewModel = assetViewModel)
            }
        }
    }
}

@Composable
fun HomeScreenContent(navController: NavController, assetViewModel: AssetViewModel){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(0.dp)
    ) {
        TitleSection()
        CryptoSection(navController = navController, assetViewModel = assetViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        StocksSection(navController = navController, assetViewModel = assetViewModel)
        Spacer(modifier = Modifier.height(16.dp))
        PopularSection(navController = navController, assetViewModel = assetViewModel)
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
