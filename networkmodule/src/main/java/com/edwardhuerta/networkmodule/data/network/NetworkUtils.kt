package com.edwardhuerta.githubchallenge.data.network

import com.edwardhuerta.networkmodule.GetUserDataQuery

interface NetworkStateCallback {
    fun newState(state : NetworkState)
    fun onResultReady(result : GetUserDataQuery.Data?)
}

@Suppress("DataClassPrivateConstructor")
data class NetworkState private constructor(
    val status: Status,
    val msg: String? = null,
    val exception: Exception? = null,
) {
    companion object {
        val LOADED = NetworkState(Status.SUCCESS)
        val LOADING = NetworkState(Status.RUNNING)
        val LOADED_EMPTY = NetworkState(Status.LOAD_EMPTY)
        fun error(msg: String?, exception: Exception? = null) = NetworkState(Status.FAILED, msg, exception)
    }
}

enum class Status {
    RUNNING,
    SUCCESS,
    LOAD_EMPTY,
    FAILED
}