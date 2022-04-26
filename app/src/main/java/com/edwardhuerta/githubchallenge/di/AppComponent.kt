package com.edwardhuerta.githubchallenge.di

import com.edwardhuerta.githubchallenge.MainActivity
import com.edwardhuerta.githubchallenge.presentation.UserOverviewContract
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        UserOverviewModule::class,
        ContextProviderModule::class
    ]
)
interface AppComponent : AndroidInjector<MainActivity> {

    fun presenter(): UserOverviewContract.Presenter

    @Component.Builder
    interface MyBuilder {

        /**
         * The activity is now part of the dependency graph.
         */
        @BindsInstance
        fun activity(activity: MainActivity): MyBuilder

        fun build(): AppComponent
    }
}