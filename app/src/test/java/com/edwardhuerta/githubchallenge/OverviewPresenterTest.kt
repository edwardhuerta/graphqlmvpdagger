package com.edwardhuerta.githubchallenge

import com.edwardhuerta.githubchallenge.data.network.NetworkState
import com.edwardhuerta.githubchallenge.data.network.NetworkStateCallback
import com.edwardhuerta.githubchallenge.data.repository.UserOverviewRepository
import com.edwardhuerta.githubchallenge.presentation.OverviewPresenter
import com.edwardhuerta.githubchallenge.presentation.UserOverviewContract
import com.edwardhuerta.githubchallenge.ui.model.MainScreenUiModel
import com.edwardhuerta.githubchallenge.ui.model.MainUserUiModel
import com.edwardhuerta.networkmodule.GetUserDataQuery
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.mockito.*

@OptIn(ExperimentalCoroutinesApi::class)
class OverviewPresenterTest {
    @Mock
    lateinit var repository: UserOverviewRepository

    @Mock
    lateinit var contractView: UserOverviewContract.View

    @Captor
    lateinit var networkStateCallbackCaptor: ArgumentCaptor<NetworkStateCallback>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun testLoadingOfData() = runTest {
        val userLogin = testPreSteps()

        Mockito.verify(repository).fetchData(Mockito.eq(userLogin), Mockito.eq(false), networkStateCallback = networkStateCallbackCaptor.capture())
        networkStateCallbackCaptor.value.newState(NetworkState.LOADING)

        val inOrderVerification = Mockito.inOrder(contractView)
        inOrderVerification.verify(contractView).toggleProgressbar(true)
    }

    @Test
    fun testLoadingFinishedOfData() = runTest {
        val userLogin = testPreSteps()

        Mockito.verify(repository).fetchData(Mockito.eq(userLogin), Mockito.eq(false), networkStateCallback = networkStateCallbackCaptor.capture())
        networkStateCallbackCaptor.value.newState(NetworkState.LOADED)
        networkStateCallbackCaptor.value.onResultReady(null)

        val inOrderVerification = Mockito.inOrder(contractView)
        inOrderVerification.verify(contractView).toggleProgressbar(false)
    }

    @Test
    fun testError() = runTest {
        val userLogin = testPreSteps()

        Mockito.verify(repository).fetchData(Mockito.eq(userLogin), Mockito.eq(false), networkStateCallback = networkStateCallbackCaptor.capture())
        networkStateCallbackCaptor.value.newState(NetworkState.error("error from test"))

        val inOrderVerification = Mockito.inOrder(contractView)
        inOrderVerification.verify(contractView).toggleProgressbar(false)
        inOrderVerification.verify(contractView).showError()
    }

    @Test
    fun testErrorOnEmpty() = runTest {
        val userLogin = testPreSteps()

        Mockito.verify(repository).fetchData(Mockito.eq(userLogin), Mockito.eq(false), networkStateCallback = networkStateCallbackCaptor.capture())
        networkStateCallbackCaptor.value.newState(NetworkState.LOADED_EMPTY)
        networkStateCallbackCaptor.value.onResultReady(null)

        val inOrderVerification = Mockito.inOrder(contractView)
        inOrderVerification.verify(contractView).toggleProgressbar(false)
        inOrderVerification.verify(contractView).showErrorOnEmptyResponse()
    }

    @Test
    fun testLoadingFinishedAndProcessResult() = runTest {
        val userLogin = testPreSteps()

        // succeed with the call and return a graphql model
        Mockito.verify(repository).fetchData(Mockito.eq(userLogin), Mockito.eq(false), networkStateCallback = networkStateCallbackCaptor.capture())
        networkStateCallbackCaptor.value.newState(NetworkState.LOADED)
        networkStateCallbackCaptor.value.onResultReady(getGraphQLData())

        // the presenter will conver the graphql model to a usable UI model
        val verificationUIModel = MainScreenUiModel(
            mainUserModel = MainUserUiModel(
                avatarUrl = "my avatarurl",
                firstLastName = "edward",
                userName = "edta",
                email = "asdf@asdf.com",
                followersCount = 123,
                followingCount = 456,
            ),
            pinnedItems = emptyList(),
            topRepositories = emptyList(),
            starredRepositories = emptyList(),
        )

        // do the verification
        val inOrderVerification = Mockito.inOrder(contractView)
        inOrderVerification.verify(contractView).toggleProgressbar(false)
        inOrderVerification.verify(contractView).showUserOverviewData(verificationUIModel)
    }


    private suspend fun TestScope.testPreSteps(): String {
        val testScope: TestScope = this
        val userLogin = "jakewharton"

        val presenterToTest = OverviewPresenter(overviewRepository = repository).apply { setView(contractView, lifecycleCoroutineScope = testScope) }
        presenterToTest.fetchData()
        delay(100)
        return userLogin
    }

    private fun getGraphQLData(): GetUserDataQuery.Data {
        return GetUserDataQuery.Data(
            user = GetUserDataQuery.User(
                pinnedItems = GetUserDataQuery.PinnedItems(edges = emptyList()),
                starredRepositories = GetUserDataQuery.StarredRepositories(edges = emptyList()),
                topRepositories = GetUserDataQuery.TopRepositories(edges = emptyList()),
                avatarUrl = "my avatarurl",
                name = "edward",
                login = "edta",
                email = "asdf@asdf.com",
                followers = GetUserDataQuery.Followers(123),
                following = GetUserDataQuery.Following(456),
            )
        )
    }
}