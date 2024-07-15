package di

import com.russhwolf.settings.Settings
import data.local.PreferencesRepositoryImpl
import data.remote.api.CurrencyApiServiceImpl
import domain.CurrencyApiService
import domain.PreferencesRepository
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import presentation.screen.HomeViewModel

expect val platormModule: Module

val appmodule = module {
    singleOf(::Settings)
    singleOf(::PreferencesRepositoryImpl)
        .bind<PreferencesRepository>()

    singleOf(::PreferencesRepositoryImpl)
        .bind<PreferencesRepository>()

    singleOf(::CurrencyApiServiceImpl)
        .bind<CurrencyApiService>()
   factory {
       HomeViewModel(get(),get())
   }




}
fun initialKoin(config:KoinAppDeclaration? = null) {
    startKoin {
        config?.invoke(this)
        modules(appmodule,platormModule)
    }
}