package com.formsense.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.formsense.app.data.model.ExerciseType
import com.formsense.app.ui.history.HistoryViewModel
import com.formsense.app.ui.home.HomeViewModel
import com.formsense.app.ui.profile.ProfileViewModel
import com.formsense.app.ui.progress.ProgressViewModel
import com.formsense.app.ui.results.ResultsEvent
import com.formsense.app.ui.results.ResultsViewModel
import com.formsense.app.ui.screens.ExerciseSelectionScreen
import com.formsense.app.ui.screens.ExerciseSetupScreen
import com.formsense.app.ui.screens.HomeScreen
import com.formsense.app.ui.screens.LoadingScreen
import com.formsense.app.ui.screens.LoginScreen
import com.formsense.app.ui.screens.ProfileScreen
import com.formsense.app.ui.screens.ProgressScreen
import com.formsense.app.ui.screens.ResultsScreen
import com.formsense.app.ui.screens.SessionScreen
import com.formsense.app.ui.screens.SessionsScreen
import com.formsense.app.ui.screens.SettingsScreen
import com.formsense.app.ui.screens.SignUpScreen
import com.formsense.app.ui.session.SessionEvent
import com.formsense.app.ui.session.SessionViewModel

object Routes {
    const val Loading = "loading"
    const val Login = "login"
    const val SignUp = "signup"
    const val Home = "home"
    const val ExerciseSelection = "exercise-selection"
    const val ExerciseSetup = "exercise-setup/{exercise}"
    const val Session = "session/{exercise}"
    const val Results = "results/{sessionId}"
    const val Sessions = "sessions"
    const val Progress = "progress"
    const val Profile = "profile"
    const val Settings = "settings"

    fun session(exercise: ExerciseType) = "session/${exercise.routeName}"
    fun exerciseSetup(exercise: ExerciseType) = "exercise-setup/${exercise.routeName}"
    fun results(sessionId: Long) = "results/$sessionId"
}

@Composable
fun FormSenseNavGraph(navController: NavHostController = rememberNavController()) {
    var isGuestUser by remember { mutableStateOf(false) }

    fun openTopLevel(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = (route != Routes.Home)
            }
            launchSingleTop = true
            restoreState = (route != Routes.Home)
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.Loading,
    ) {
        composable(Routes.Loading) {
            LoadingScreen(
                onTimeout = {
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Loading) { inclusive = true }
                    }
                },
            )
        }

        composable(Routes.Login) {
            LoginScreen(
                onLoginSuccess = {
                    isGuestUser = false
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onContinueAsGuest = {
                    isGuestUser = true
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Login) { inclusive = true }
                    }
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SignUp)
                },
            )
        }

        composable(Routes.SignUp) {
            SignUpScreen(
                onSignupSuccess = {
                    isGuestUser = false
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.SignUp) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigateUp()
                },
            )
        }

        composable(Routes.Home) {
            val viewModel = hiltViewModel<HomeViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            HomeScreen(
                state = state,
                onStartSession = { navController.navigate(Routes.ExerciseSelection) },
                onExerciseSelected = { navController.navigate(Routes.exerciseSetup(it)) },
                onBottomDestination = (::openTopLevel),
                onSettings = { navController.navigate(Routes.Settings) },
            )
        }

        composable(Routes.ExerciseSelection) {
            ExerciseSelectionScreen(
                onBack = navController::navigateUp,
                onExerciseSelected = { navController.navigate(Routes.exerciseSetup(it)) },
            )
        }

        composable(Routes.ExerciseSetup) { backStackEntry ->
            val exercise = ExerciseType.fromRoute(backStackEntry.arguments?.getString("exercise"))
            ExerciseSetupScreen(
                exercise = exercise,
                onBack = navController::navigateUp,
                onStart = { navController.navigate(Routes.session(exercise)) },
            )
        }

        composable(Routes.Session) { backStackEntry ->
            val viewModel = hiltViewModel<SessionViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        is SessionEvent.Saved -> navController.navigate(Routes.results(event.sessionId)) {
                            popUpTo(backStackEntry.destination.id) { inclusive = true }
                        }
                    }
                }
            }

            SessionScreen(
                state = state,
                onBack = navController::navigateUp,
                onEndSession = viewModel::finishSession,
                onRetryAnalyzer = viewModel::retryAnalyzer,
                onCameraFrame = viewModel::submitCameraFrame,
            )
        }

        composable(Routes.Results) {
            val viewModel = hiltViewModel<ResultsViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()

            LaunchedEffect(viewModel) {
                viewModel.events.collect { event ->
                    when (event) {
                        ResultsEvent.Deleted -> navController.navigate(Routes.Sessions) {
                            popUpTo(Routes.Results) { inclusive = true }
                            launchSingleTop = true
                        }
                    }
                }
            }

            ResultsScreen(
                state = state,
                onTryAgain = {
                    state.session?.exercise?.let { exercise ->
                        navController.navigate(Routes.session(exercise))
                    }
                },
                onReturnHome = {
                    navController.navigate(Routes.Home) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
                onSessionSelected = { navController.navigate(Routes.results(it)) },
                onDeleteSession = viewModel::deleteSession,
            )
        }

        composable(Routes.Sessions) {
            val viewModel = hiltViewModel<HistoryViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            SessionsScreen(
                state = state,
                onSessionSelected = { navController.navigate(Routes.results(it)) },
                onBottomDestination = (::openTopLevel),
            )
        }

        composable(Routes.Progress) {
            val viewModel = hiltViewModel<ProgressViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            ProgressScreen(
                state = state,
                onBottomDestination = (::openTopLevel),
            )
        }

        composable(Routes.Profile) {
            ProfileScreen(
                onOpenSettings = { navController.navigate(Routes.Settings) },
                onBottomDestination = (::openTopLevel),
            )
        }

        composable(Routes.Settings) {
            val viewModel = hiltViewModel<ProfileViewModel>()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            SettingsScreen(
                state = state,
                onBack = navController::navigateUp,
                onDeleteAllSessions = viewModel::deleteAllSessions,
                onDefaultTargetRepsChanged = viewModel::setDefaultTargetReps,
                onHapticFeedbackChanged = viewModel::setHapticFeedbackEnabled,
                onKeepScreenAwakeChanged = viewModel::setKeepScreenAwake,
                onSignOut = {
                    if (isGuestUser) {
                        viewModel.deleteAllSessions()
                        isGuestUser = false
                    }
                    navController.navigate(Routes.Login) {
                        popUpTo(Routes.Home) { inclusive = true }
                    }
                },
            )
        }
    }
}