package com.edwardhuerta.githubchallenge

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.edwardhuerta.githubchallenge.data.network.NetworkState
import com.edwardhuerta.githubchallenge.data.network.NetworkStateCallback
import com.edwardhuerta.githubchallenge.ui.model.convertToUiModel
import com.edwardhuerta.networkmodule.GetUserDataQuery
import com.edwardhuerta.networkmodule.data.datasource.GithubGraphqlDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
@OptIn(ExperimentalCoroutinesApi::class)
class DataSourceNetworkInstrumentedTest {

    @Test
    fun testDataSource() = runTest {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.edwardhuerta.githubchallenge", appContext.packageName)

        val callback = object : NetworkStateCallback {
            var resultFromApi : GetUserDataQuery.Data? = null
            override fun newState(state: NetworkState) {
            }

            override fun onResultReady(result: GetUserDataQuery.Data?) {
                resultFromApi = result
            }
        }

        GithubGraphqlDataSource(appContext).fetchUserOverviewFromGithub(userLogin = "jakewharton", forceRefresh = true, networkStateCallback = callback)
        delay(3000)
        val data: GetUserDataQuery.Data? = callback.resultFromApi

        val mainUserUi = data?.user?.convertToUiModel()
        assertEquals("https://avatars.githubusercontent.com/u/66577?v=4", mainUserUi?.avatarUrl)
        assertEquals("Jake Wharton", mainUserUi?.firstLastName)
        assertEquals("JakeWharton", mainUserUi?.userName)
        assertEquals("j@ke.fyi", mainUserUi?.email)
//        assertEquals(62569, mainUserUi?.followersCount)
//        assertEquals(9, mainUserUi?.followingCount)
    }
}