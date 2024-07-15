package di

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import presentation.screen.HomeViewModel

actual val platormModule = module {
    factory {
        HomeViewModel(get(), get())
    }
    singleOf(::HomeViewModel)
}