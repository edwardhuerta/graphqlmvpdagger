@file:Suppress("BlockingMethodInNonBlockingContext")

package com.edwardhuerta.githubchallenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.apollographql.apollo3.ApolloClient
import com.apollographql.apollo3.api.ApolloResponse
import com.apollographql.apollo3.cache.http.HttpFetchPolicy
import com.apollographql.apollo3.cache.http.httpCache
import com.apollographql.apollo3.cache.http.httpExpireTimeout
import com.apollographql.apollo3.cache.http.httpFetchPolicy
import com.apollographql.apollo3.network.okHttpClient
import com.edwardhuerta.networkmodule.data.network.interceptor.BearerTokenHeaderInterceptor
import com.edwardhuerta.githubchallenge.ui.model.*
import com.edwardhuerta.networkmodule.GetUserDataQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.io.File
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class InstrumentationNetworkTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.edwardhuerta.githubchallenge", appContext.packageName)
    }

    @Test
    fun simpleNetworkCallTest() = runTest {

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val okHttpClient = getOkHttpClient()

        val cacheExpirationsInMillis: Long = TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS)

        Timber.v("seconds $cacheExpirationsInMillis")
        val apolloClient = ApolloClient.Builder()
            .httpCache(
                directory = File(appContext.cacheDir.toURI()),
                maxSize = 10 * 1024 * 1024
            )
            .httpFetchPolicy(HttpFetchPolicy.CacheFirst)
            .httpExpireTimeout(cacheExpirationsInMillis)
            .okHttpClient(okHttpClient = okHttpClient)
            .serverUrl("https://api.github.com/graphql")
            .build()

        apolloClient.httpCache.clearAll()

        for (i in 1..4) {
            if (i == 3) {
                apolloClient.httpCache.clearAll()
            }

            // make the request
            val response: ApolloResponse<GetUserDataQuery.Data> = apolloClient.query(GetUserDataQuery(login = "jakewharton")).execute()
            val headers = apolloClient.httpHeaders

            assertNotNull(headers)
            assertEquals("86400000", headers?.first { it.name == "X-APOLLO-EXPIRE-TIMEOUT" }?.value.orEmpty())

            // verify the response contents
            assertNull(response.errors)
            assertEquals("Jake Wharton", response.data?.user?.name)
            assertEquals("j@ke.fyi", response.data?.user?.email)
        }
    }

    @Test
    fun testMainUserUiMapping() = runTest {
        val okHttpClient = getOkHttpClient()
        val apolloClient = getApolloClientWithoutCache(okHttpClient)

        val response: ApolloResponse<GetUserDataQuery.Data> = apolloClient.query(GetUserDataQuery(login = "jakewharton")).execute()
        val body: GetUserDataQuery.Data? = response.data

        val mainUserUi: MainUserUiModel? = body?.user?.convertToUiModel()
        assertEquals("https://avatars.githubusercontent.com/u/66577?v=4", mainUserUi?.avatarUrl)
        assertEquals("Jake Wharton", mainUserUi?.firstLastName)
        assertEquals("JakeWharton", mainUserUi?.userName)
        assertEquals("j@ke.fyi", mainUserUi?.email)
//        assertEquals(62569, mainUserUi?.followersCount)
//        assertEquals(9, mainUserUi?.followingCount)
    }

    @Test
    fun testPinnedItemsUiMapping() = runTest {

        val okHttpClient = getOkHttpClient()
        val apolloClient = getApolloClientWithoutCache(okHttpClient)

        val response: ApolloResponse<GetUserDataQuery.Data> = apolloClient.query(GetUserDataQuery(login = "jakewharton")).execute()
        val body: GetUserDataQuery.Data? = response.data

        val pinnedItems: List<CardForUserItemUiModel?> = body?.user?.pinnedItems?.edges?.map { it?.convertToPinnedItemsUiModel() }.orEmpty()

        assertFalse(pinnedItems.isEmpty())

        pinnedItems.first()?.let { pinnedItem: CardForUserItemUiModel ->
            assertEquals("https://avatars.githubusercontent.com/u/82592?v=4", pinnedItem.avatarUrl)
            assertEquals("square", pinnedItem.userName)
            assertEquals("retrofit", pinnedItem.cardTitleText)
            assertEquals("A type-safe HTTP client for Android and the JVM", pinnedItem.cardDescriptionText)
//            assertEquals(39876, pinnedItem.stargazersCount)

            val firstLanguageAttribute = pinnedItem.languagesInfo.firstOrNull()
            assertEquals("#89e051", firstLanguageAttribute?.colorOfLanguage)
            assertEquals("Shell", firstLanguageAttribute?.programmingLanguageName)
        }
    }

    @Test
    fun testTopReposUiMapping() = runTest {

        val okHttpClient = getOkHttpClient()
        val apolloClient = getApolloClientWithoutCache(okHttpClient)

        val response: ApolloResponse<GetUserDataQuery.Data> = apolloClient.query(GetUserDataQuery(login = "jakewharton")).execute()
        val body: GetUserDataQuery.Data? = response.data

        val pinnedItems: List<CardForUserItemUiModel?> = body?.user?.topRepositories?.edges?.map { it?.convertToTopRepositoriesUiModel() }.orEmpty()

        assertFalse(pinnedItems.isEmpty())

        pinnedItems.first()?.let { pinnedItem: CardForUserItemUiModel ->
            assertEquals("https://avatars.githubusercontent.com/u/49219790?v=4", pinnedItem.avatarUrl)
            assertEquals("cashapp", pinnedItem.userName)
            assertEquals("zipline", pinnedItem.cardTitleText)
            assertEquals("Run Kotlin/JS libraries in Kotlin/JVM and Kotlin/Native programs", pinnedItem.cardDescriptionText)
//            assertEquals(1187, pinnedItem.stargazersCount)

            val firstLanguageAttribute = pinnedItem.languagesInfo.firstOrNull()
            assertEquals("#b07219", firstLanguageAttribute?.colorOfLanguage)
            assertEquals("Java", firstLanguageAttribute?.programmingLanguageName)
        }
    }

    @Test
    fun testStarredReposUiMapping() = runTest {
        val okHttpClient = getOkHttpClient()
        val apolloClient = getApolloClientWithoutCache(okHttpClient)

        val response: ApolloResponse<GetUserDataQuery.Data> = apolloClient.query(GetUserDataQuery(login = "jakewharton")).execute()
        val body: GetUserDataQuery.Data? = response.data

        val pinnedItems: List<CardForUserItemUiModel?> = body?.user?.starredRepositories?.edges?.map { it?.convertToStarredRepositoriesUiModel() }.orEmpty()

        assertFalse(pinnedItems.isEmpty())

        pinnedItems.first()?.let { pinnedItem: CardForUserItemUiModel ->
            assertEquals("https://avatars.githubusercontent.com/u/740?u=075b9bae1671f27ec88c3a469235fc20c6b16a2a&v=4", pinnedItem.avatarUrl)
            assertEquals("rmm5t", pinnedItem.userName)
            assertEquals("jquery-timeago", pinnedItem.cardTitleText)
            assertEquals(":clock8: The original jQuery plugin that makes it easy to support automatically updating fuzzy timestamps (e.g. \"4 minutes ago\").", pinnedItem.cardDescriptionText)
//            assertEquals(3816, pinnedItem.stargazersCount)

            val firstLanguageAttribute = pinnedItem.languagesInfo.firstOrNull()
            assertEquals("#701516", firstLanguageAttribute?.colorOfLanguage)
            assertEquals("Ruby", firstLanguageAttribute?.programmingLanguageName)
        }
    }

    private fun getApolloClientWithoutCache(okHttpClient: OkHttpClient) = ApolloClient.Builder()
        .okHttpClient(okHttpClient = okHttpClient)
        .serverUrl("https://api.github.com/graphql")
        .build()

    private fun getOkHttpClient() = OkHttpClient.Builder()
        .addInterceptor(BearerTokenHeaderInterceptor())
        .addInterceptor(HttpLoggingInterceptor { s -> Timber.tag("edward-okhttp").d(s) }.setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()
}