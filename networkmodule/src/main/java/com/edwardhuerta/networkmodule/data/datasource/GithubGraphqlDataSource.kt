@file:Suppress("BlockingMethodInNonBlockingContext")

package com.edwardhuerta.networkmodule.data.datasource

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.*
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.cache.http.HttpFetchPolicy
import com.apollographql.apollo3.cache.http.httpCache
import com.apollographql.apollo3.cache.http.httpExpireTimeout
import com.apollographql.apollo3.cache.http.httpFetchPolicy
import com.apollographql.apollo3.network.okHttpClient
import com.edwardhuerta.githubchallenge.data.network.NetworkState
import com.edwardhuerta.githubchallenge.data.network.NetworkStateCallback
import com.edwardhuerta.networkmodule.GetUserDataQuery
import com.edwardhuerta.networkmodule.data.network.interceptor.BearerTokenHeaderInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class GithubGraphqlDataSource @Inject constructor(
    private val appContext: Context,
) {
    private val baseUrl = "https://api.github.com/graphql"
    private val githubCacheDuration = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)

    private val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(BearerTokenHeaderInterceptor())
        .addInterceptor(HttpLoggingInterceptor { s -> Timber.tag("edward-okhttp").d(s) }.setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .httpCache(
            directory = File(appContext.cacheDir.toURI()),
            maxSize = 10 * 1024 * 1024
        )
        .httpFetchPolicy(HttpFetchPolicy.CacheFirst)
        .httpExpireTimeout(githubCacheDuration)
        .okHttpClient(okHttpClient = okHttpClient)
        .serverUrl(baseUrl)
        .build()

    // it is inside a coroutine, so it is OK to suppress
    @Suppress("BlockingMethodInNonBlockingContext")
    suspend fun fetchUserOverviewFromGithub(userLogin: String?, forceRefresh: Boolean?, networkStateCallback: NetworkStateCallback?) =
        if (checkInternetConnection(context = appContext)) {
            try {
                networkStateCallback?.newState(NetworkState.LOADING)
                val result: GetUserDataQuery.Data? = withContext(Dispatchers.IO) {

                    if (forceRefresh == true) {
                        apolloClient.httpCache.clearAll()
                    }

                    val response: ApolloResponse<GetUserDataQuery.Data> = apolloClient.query(GetUserDataQuery(login = userLogin.orEmpty())).execute()
                    response.data
                }
                networkStateCallback?.newState(NetworkState.LOADED)
                networkStateCallback?.onResultReady(result)
            } catch (e: Exception) {
                networkStateCallback?.newState(NetworkState.error("error while fetching useroverview", e))
            }
        } else {
            networkStateCallback?.newState(NetworkState.error("NO_INTERNET_ERROR"))
        }

    private fun checkInternetConnection(context: Context): Boolean {
        val connectivityManager: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCap = connectivityManager.activeNetwork ?: return false
        val activeNetwork: NetworkCapabilities = connectivityManager.getNetworkCapabilities(networkCap) ?: return false
        return when {
            activeNetwork.hasTransport(TRANSPORT_WIFI) -> true
            activeNetwork.hasTransport(TRANSPORT_CELLULAR) -> true
            activeNetwork.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}