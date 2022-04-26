package com.edwardhuerta.githubchallenge.di

import android.content.Context
import com.edwardhuerta.githubchallenge.MainActivity
import dagger.Module
import dagger.Provides

@Module
class ContextProviderModule {

    @Provides
    fun provideContext(activity : MainActivity) : Context {
        return activity
    }

}