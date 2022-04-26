This is an introduction to the code in the project to help you get familiar while reviewing the code. The most important notes are written here:

Please note that if your phone has adjusted text size in the accessability settings, then the design might not display as intended.

- I used coroutines. Only needed to use it in the GithubGraphqlDataSource.kt to access the network. Coroutine scope is the one used from the MainActivity.
- Used Android Studio version 2021.1.1 Patch 2
- the duration to cache the response to 1 day is set in : com.edwardhuerta.networkmodule.data.datasource.GithubGraphqlDataSource#githubCacheDuration
- If the user pulls to refresh, the cache in the client is cleared. (There is only one network call made)
- The graphql query can be seen in the file GetUserData.graphql in the network module.

- Apollo library is used to generate client to connect to GitHub graphql endpoint.
- Mockito is used to test the presenter logic (business logic) in the class com.edwardhuerta.githubchallenge.OverviewPresenterTest.
- The View-Presenter contract is in com.edwardhuerta.githubchallenge.presentation.UserOverviewContract
- There is a module that is of type 'com.android.library' containing the implementation of the network layer called 'networkmodule' in the base directory.
- dagger app component is in com.edwardhuerta.githubchallenge.di.AppComponent
- the github graphql model is mapped to a ui model com.edwardhuerta.githubchallenge.ui.model.MainScreenUiModel
- mappings between network and ui models are contained in UiModelMappings.kt

Testing :

- com.edwardhuerta.githubchallenge.InstrumentationNetworkTest : android test that was used as Test driven development to build and verify the connection the Github's GraphQl.

- com.edwardhuerta.githubchallenge.FromLocalJsonOfflineNetworkInstrumentedTest : while testing of the actual backend, the data might change. (think of the number of followers or the users following or the stargazers.) to use data that never changes, this FromLocalJsonOfflineNetworkInstrumentedTest class was created. a local JSON file of a sample response was put in sample.json, and we use that to always return the same data without needing to connect to the internet.

- com.edwardhuerta.githubchallenge.DataSourceNetworkInstrumentedTest : simple test to check that the data source class (GithubGraphqlDataSource.kt) is functioning as expected.

- com.edwardhuerta.githubchallenge.OverviewPresenterTest : unit test for the presenter contract. we control the callback and verify that the contract view is receiving the correct calls.
