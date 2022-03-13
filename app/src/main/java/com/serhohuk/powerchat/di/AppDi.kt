package com.serhohuk.powerchat.di

import android.content.SharedPreferences
import com.serhohuk.powerchat.data.SharedPrefsStorage
import com.serhohuk.powerchat.repository.AppRepository
import com.serhohuk.powerchat.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val dataModule = module {
    single {
        SharedPrefsStorage(context = get())
    }

    single {
        AppRepository(prefs = get())
    }
}

val appModule = module {

    viewModel<MainViewModel> {
        MainViewModel(repository = get())
    }
}