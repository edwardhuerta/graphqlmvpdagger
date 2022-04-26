package com.edwardhuerta.githubchallenge

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.apollographql.apollo3.api.CustomScalarAdapters
import com.apollographql.apollo3.api.json.MapJsonReader
import com.apollographql.apollo3.api.nullable
import com.apollographql.apollo3.api.obj
import com.edwardhuerta.githubchallenge.ui.model.MainScreenUiModel
import com.edwardhuerta.githubchallenge.ui.model.toMainScreenModel
import com.edwardhuerta.networkmodule.GetUserDataQuery
import com.edwardhuerta.networkmodule.adapter.GetUserDataQuery_ResponseAdapter
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import timber.log.Timber
import java.io.InputStream
import java.io.InputStreamReader

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class FromLocalJsonOfflineNetworkInstrumentedTest {

    @Test
    fun testSampleJsonOfflineConversion() = runTest {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        val uiModel: MainScreenUiModel? = getData(appContext)

        val mainUserUi = uiModel?.mainUserModel
        assertEquals("https://avatars.githubusercontent.com/u/66577?v=4", mainUserUi?.avatarUrl)
        assertEquals("Jake Wharton", mainUserUi?.firstLastName)
        assertEquals("JakeWharton", mainUserUi?.userName)
        assertEquals("j@ke.fyi", mainUserUi?.email)
        assertEquals(62569, mainUserUi?.followersCount)
        assertEquals(9, mainUserUi?.followingCount)

        assertEquals(3, uiModel?.pinnedItems?.size)
        val pinnedItem = uiModel?.pinnedItems?.firstOrNull()
        assertEquals("square", pinnedItem?.userName)
        assertEquals(39876, pinnedItem?.stargazersCount)
        assertEquals("https://avatars.githubusercontent.com/u/82592?v=4", pinnedItem?.avatarUrl)
        assertEquals("A type-safe HTTP client for Android and the JVM", pinnedItem?.cardDescriptionText)
        assertEquals("retrofit", pinnedItem?.cardTitleText)
        assertEquals("Shell", pinnedItem?.languagesInfo?.firstOrNull()?.programmingLanguageName)
        assertEquals("#89e051", pinnedItem?.languagesInfo?.firstOrNull()?.colorOfLanguage)

        assertEquals(10, uiModel?.starredRepositories?.size)
        val starredItem = uiModel?.starredRepositories?.firstOrNull()
        assertEquals("jquery-timeago", starredItem?.cardTitleText)
        assertEquals(":clock8: The original jQuery plugin that makes it easy to support automatically updating fuzzy timestamps (e.g. \"4 minutes ago\").", starredItem?.cardDescriptionText)
        assertEquals("https://avatars.githubusercontent.com/u/740?u=075b9bae1671f27ec88c3a469235fc20c6b16a2a&v=4", starredItem?.avatarUrl)
        assertEquals(3816, starredItem?.stargazersCount)
        assertEquals("rmm5t", starredItem?.userName)
        assertEquals("#701516", starredItem?.languagesInfo?.firstOrNull()?.colorOfLanguage)
        assertEquals("Ruby", starredItem?.languagesInfo?.firstOrNull()?.programmingLanguageName)

        assertEquals(10, uiModel?.topRepositories?.size)
        val topItems = uiModel?.topRepositories?.firstOrNull()
        assertEquals("cashapp", topItems?.userName)
        assertEquals(1187, topItems?.stargazersCount)
        assertEquals("https://avatars.githubusercontent.com/u/49219790?v=4", topItems?.avatarUrl)
        assertEquals("Run Kotlin/JS libraries in Kotlin/JVM and Kotlin/Native programs", topItems?.cardDescriptionText)
        assertEquals("zipline", topItems?.cardTitleText)
        assertEquals("Java", topItems?.languagesInfo?.firstOrNull()?.programmingLanguageName)
        assertEquals("#b07219", topItems?.languagesInfo?.firstOrNull()?.colorOfLanguage)
    }

    private fun getData(appContext: Context): MainScreenUiModel? {
        val fakeResponse: GetUserDataQuery.Data? = appContext.json("sample.json")
        return fakeResponse?.toMainScreenModel()
    }

    private fun Context.json(jsonName: String): GetUserDataQuery.Data? {
        Timber.i("--> reading json file: $jsonName")
        try {
            val inputstream: InputStream = assets.open(jsonName)
            val gson = GsonBuilder().create()
            val map: Map<String, Any?> = gson.fromJson(InputStreamReader(inputstream), object : TypeToken<Map<String, Any?>>() {}.type)
            val jsonReader = MapJsonReader(map)
            return GetUserDataQuery_ResponseAdapter.Data.obj().nullable<@JvmSuppressWildcards GetUserDataQuery.Data>().fromJson(jsonReader, CustomScalarAdapters.Empty)
        } catch (e: java.lang.Exception) {
            throw RuntimeException(e)
        }
    }
}