package com.edwardhuerta.githubchallenge.data.repository

import com.edwardhuerta.githubchallenge.data.network.NetworkStateCallback
import com.edwardhuerta.networkmodule.data.datasource.GithubGraphqlDataSource
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserOverviewRepository @Inject constructor(
    private val dataSource : GithubGraphqlDataSource,
) {
    suspend fun fetchData(userLogin: String?, forceRefresh: Boolean?, networkStateCallback: NetworkStateCallback?) {
        dataSource.fetchUserOverviewFromGithub(userLogin = userLogin, forceRefresh = forceRefresh, networkStateCallback = networkStateCallback)
    }
}