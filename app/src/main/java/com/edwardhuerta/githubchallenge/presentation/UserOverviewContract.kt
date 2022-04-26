package com.edwardhuerta.githubchallenge.presentation

import com.edwardhuerta.githubchallenge.ui.model.MainScreenUiModel
import kotlinx.coroutines.CoroutineScope

interface UserOverviewContract {
    interface View {
        fun toggleProgressbar(displayOnScreen : Boolean)
        fun showError()
        fun showErrorOnEmptyResponse()

        /**
         * show the incoming data
         */
        fun showUserOverviewData(uiModel: MainScreenUiModel)
        fun setPresenter(presenter : Presenter, coroutineScope: CoroutineScope)
    }

    interface Presenter {
        /**
         * when the user pulls-to-refresh or after a network error
         */
        fun fetchData(forceRefresh : Boolean = false)

        /**
         * set the view to use
         */
        fun setView(uiView: View, lifecycleCoroutineScope: CoroutineScope?)

        /**
         * lose the reference to the view
         */
        fun unsetView()
    }
}