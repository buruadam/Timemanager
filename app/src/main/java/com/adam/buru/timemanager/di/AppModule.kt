package com.adam.buru.timemanager.di

import com.adam.buru.timemanager.data.remote.KtorClient
import com.adam.buru.timemanager.data.repository.AuthRepository
import com.adam.buru.timemanager.data.repository.AuthRepositoryImpl
import com.adam.buru.timemanager.data.repository.TaskRepository
import com.adam.buru.timemanager.data.repository.TaskRepositoryImpl
import com.adam.buru.timemanager.data.repository.UserRepository
import com.adam.buru.timemanager.data.repository.UserRepositoryImpl
import com.adam.buru.timemanager.ui.viewmodel.AuthViewModel
import com.adam.buru.timemanager.ui.viewmodel.HomeViewModel
import com.adam.buru.timemanager.ui.viewmodel.TaskViewModel
import com.adam.buru.timemanager.ui.viewmodel.UserViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single<KtorClient> { KtorClient }
    single<AuthRepository> { AuthRepositoryImpl(get()) }
    single<TaskRepository> { TaskRepositoryImpl(get()) }
    single<UserRepository> { UserRepositoryImpl(get()) }
    viewModel { AuthViewModel(get()) }
    viewModel { TaskViewModel(get()) }
    viewModel { HomeViewModel(get()) }
    viewModel { UserViewModel(get()) }
}