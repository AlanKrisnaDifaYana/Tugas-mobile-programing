package com.learn.tugasuas

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learn.tugasuas.data.GoogleAuthUIClient
import com.learn.tugasuas.presentation.add_game.AddGameScreen
import com.learn.tugasuas.presentation.home.HomeScreen
import com.learn.tugasuas.presentation.home.HomeViewModel
import com.learn.tugasuas.presentation.sign_in.SignInScreen
import com.learn.tugasuas.presentation.sign_in.SignInViewModel
import com.learn.tugasuas.ui.theme.TugasUasTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val googleAuthUIClient by lazy { GoogleAuthUIClient(context = this) }

    @SuppressLint("ViewModelConstructorInComposable")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            TugasUasTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    val currentUser = googleAuthUIClient.getSignedInUser()
                    val startDestination = if (currentUser != null) "home" else "sign_in"

                    // ViewModel dibuat di sini agar bisa di-share
                    val homeViewModel: HomeViewModel = viewModel()

                    NavHost(navController = navController, startDestination = startDestination) {

                        composable("sign_in") {
                            val viewModel: SignInViewModel = viewModel()
                            val state by viewModel.state.collectAsStateWithLifecycle()

                            LaunchedEffect(key1 = state.isSignInSuccessfull) {
                                if (state.isSignInSuccessfull) {
                                    Toast.makeText(applicationContext, "Sign in successful", Toast.LENGTH_LONG).show()
                                    navController.navigate("home") { popUpTo("sign_in") { inclusive = true } }
                                    viewModel.resetState()
                                }
                            }
                            SignInScreen(state = state, onSignInClick = {
                                lifecycleScope.launch {
                                    val result = googleAuthUIClient.signIn()
                                    viewModel.onSignInResult(result)
                                }
                            })
                        }

                        composable("home") {
                            val user = googleAuthUIClient.getSignedInUser()
                            if (user != null) {
                                HomeScreen(
                                    userData = user,
                                    viewModel = homeViewModel,
                                    onSignOut = {
                                        lifecycleScope.launch {
                                            googleAuthUIClient.signOut()
                                            navController.navigate("sign_in") { popUpTo("home") { inclusive = true } }
                                        }
                                    },
                                    onAddGameClick = {
                                        homeViewModel.onAddClick()
                                        navController.navigate("add_game")
                                    },
                                    onEditGameClick = { game ->
                                        homeViewModel.onEditClick(game)
                                        navController.navigate("add_game")
                                    }
                                )
                            }
                        }

                        composable("add_game") {
                            val user = googleAuthUIClient.getSignedInUser()
                            if (user != null) {
                                AddGameScreen(
                                    userId = user.userId,
                                    viewModel = homeViewModel,
                                    onNavigateBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}