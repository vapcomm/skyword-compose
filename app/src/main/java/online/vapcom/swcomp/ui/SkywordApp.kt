/*
 * (c) VAP Communications Group, 2021
 */

package online.vapcom.swcomp.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import online.vapcom.swcomp.ui.meaning.MeaningScreen
import online.vapcom.swcomp.ui.meaning.MeaningViewModel
import online.vapcom.swcomp.ui.search.SearchScreen
import online.vapcom.swcomp.ui.search.SearchViewModel
import online.vapcom.swcomp.ui.theme.SkywordTheme
import org.koin.androidx.compose.getViewModel
import org.koin.core.parameter.parametersOf

/**
 * Верхний уровень Compose-приложения
 */
@Composable
fun SkywordApp() {
    SkywordTheme {
        val navController = rememberNavController()
        TopNavigation(navController)
    }
}

/**
 * Навигационный контейнер, использует весь экран целиком,
 * все остальные экраны должны сами рисовать appbar при необходимости.
 */
@Composable
fun TopNavigation(navController: NavHostController) {
    val systemUiController = rememberSystemUiController()
    systemUiController.setSystemBarsColor(
        color = if(isSystemInDarkTheme()) MaterialTheme.colors.surface else MaterialTheme.colors.primary,
        //darkIcons = !darkTheme
    )

    NavHost(
        navController = navController,
        startDestination = "search"
    ) {
        // экран поиска слова
        composable("search") {
            val viewModel = getViewModel<SearchViewModel>()
            SearchScreen(
                viewModel = viewModel,
                onMeaningClick = { meaningID ->
                    navController.navigate("meaning/$meaningID")
                }
            )
        }

        // экран отображения отдельного значения слова
        composable(
            route = "meaning/{meaningID}",
            arguments = listOf(navArgument("meaningID") { type = NavType.StringType })
        ) { entry ->
            val meaningID = entry.arguments?.getString("meaningID") ?: ""
            val viewModel = getViewModel<MeaningViewModel>(parameters = { parametersOf(meaningID) })

            MeaningScreen(
                viewModel = viewModel,
                onUpClick = { navController.popBackStack() }
            )
        }
    }
}
