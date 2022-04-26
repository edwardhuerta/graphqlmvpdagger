package com.edwardhuerta.githubchallenge

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.edwardhuerta.githubchallenge.di.DaggerAppComponent
import com.edwardhuerta.githubchallenge.presentation.UserOverviewContract
import com.edwardhuerta.githubchallenge.ui.customview.MainScreenRootLayout
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var injectedPresenter: UserOverviewContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val appComponent = DaggerAppComponent.builder().activity(this).build()
        appComponent.inject(this)

        val mainScreenLayout: MainScreenRootLayout = findViewById(R.id.activity_main_user_information)
        mainScreenLayout.setPresenter(injectedPresenter, coroutineScope = lifecycleScope)
    }

    override fun onDestroy() {
        super.onDestroy()
        injectedPresenter.unsetView()
    }
}
