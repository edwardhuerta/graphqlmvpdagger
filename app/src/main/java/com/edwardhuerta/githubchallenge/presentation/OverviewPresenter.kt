package com.edwardhuerta.githubchallenge.presentation

import com.edwardhuerta.networkmodule.GetUserDataQuery
import com.edwardhuerta.githubchallenge.data.network.NetworkState
import com.edwardhuerta.githubchallenge.data.network.NetworkStateCallback
import com.edwardhuerta.githubchallenge.data.network.Status
import com.edwardhuerta.githubchallenge.data.repository.UserOverviewRepository
import com.edwardhuerta.githubchallenge.ui.model.toMainScreenModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

class OverviewPresenter @Inject constructor(
    private val overviewRepository: UserOverviewRepository
) : UserOverviewContract.Presenter {

    private var uiView: UserOverviewContract.View? = null
    private val fixedUserLoginToUse = "jakewharton"

    private var lifecycleScope: CoroutineScope? = null

    private val handleNetworkStateCallback = object : NetworkStateCallback {
        override fun newState(state: NetworkState) {
            handleNetworkState(state)
        }

        override fun onResultReady(result: GetUserDataQuery.Data?) {
            result?.toMainScreenModel()?.let { uiModel ->
                uiView?.showUserOverviewData(uiModel)
            } ?: run {
                uiView?.showErrorOnEmptyResponse()
            }
        }
    }

    override fun fetchData(forceRefresh : Boolean) {
        lifecycleScope?.launch {
            if (forceRefresh) {
                uiView?.resetLists()
            }
            overviewRepository.fetchData(userLogin = fixedUserLoginToUse, forceRefresh = forceRefresh, networkStateCallback = handleNetworkStateCallback)
        }
    }

    private fun handleNetworkState(networkState: NetworkState) {
        when (networkState.status) {
            Status.RUNNING -> uiView?.toggleProgressbar(true)
            Status.FAILED -> {
                uiView?.toggleProgressbar(false)
                uiView?.showError()
            }
            Status.SUCCESS -> uiView?.toggleProgressbar(false)
            Status.LOAD_EMPTY -> uiView?.toggleProgressbar(false)
        }
    }

    override fun setView(uiView: UserOverviewContract.View, lifecycleCoroutineScope: CoroutineScope?) {
        this.uiView = uiView
        this.lifecycleScope = lifecycleCoroutineScope
    }

    override fun unsetView() {
        this.lifecycleScope = null
        this.uiView = null
    }
}