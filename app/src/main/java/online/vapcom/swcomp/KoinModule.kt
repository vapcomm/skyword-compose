package online.vapcom.swcomp

import online.vapcom.swcomp.media.AudioPlayer
import online.vapcom.swcomp.repo.DictRepository
import online.vapcom.swcomp.repo.DictRepositoryImpl
import online.vapcom.swcomp.ui.meaning.MeaningViewModel
import online.vapcom.swcomp.ui.search.SearchViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

/**
 * Модуль сервис-локатора Koin
 */
val appModule = module {
    single<DictRepository> { DictRepositoryImpl() }

    single { AudioPlayer(androidContext())}

    viewModel { SearchViewModel(get()) }
    viewModel { MeaningViewModel(get(), get(), get()) }
}