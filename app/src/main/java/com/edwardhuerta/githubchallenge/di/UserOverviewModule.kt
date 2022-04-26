package com.edwardhuerta.githubchallenge.di

import com.edwardhuerta.githubchallenge.presentation.OverviewPresenter
import com.edwardhuerta.githubchallenge.presentation.UserOverviewContract
import dagger.Binds
import dagger.Module

@Module
abstract class UserOverviewModule {
    @Binds
    abstract fun overviewPresenter(presenter: OverviewPresenter): UserOverviewContract.Presenter
}