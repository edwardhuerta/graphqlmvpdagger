package com.edwardhuerta.githubchallenge.ui.model

import com.edwardhuerta.networkmodule.GetUserDataQuery

fun GetUserDataQuery.Data.toMainScreenModel() : MainScreenUiModel {
    return MainScreenUiModel(
        mainUserModel = this.user?.convertToUiModel(),
        pinnedItems = this.user?.pinnedItems?.edges?.map { it?.convertToPinnedItemsUiModel() }.orEmpty(),
        topRepositories = this.user?.topRepositories?.edges?.map { it?.convertToTopRepositoriesUiModel() }.orEmpty(),
        starredRepositories = this.user?.starredRepositories?.edges?.map { it?.convertToStarredRepositoriesUiModel() }.orEmpty(),
    )
}

fun GetUserDataQuery.User.convertToUiModel() :MainUserUiModel {
    return MainUserUiModel(
        avatarUrl = (this.avatarUrl as String?).orEmpty(),
        firstLastName = name.orEmpty(),
        userName = login,
        email = email,
        followersCount = followers.totalCount,
        followingCount = following.totalCount,
    )
}

fun GetUserDataQuery.Edge.convertToPinnedItemsUiModel() : CardForUserItemUiModel {
    val repositoryInfo = node?.onRepository

    return CardForUserItemUiModel(
        avatarUrl = (repositoryInfo?.owner?.avatarUrl as String?).orEmpty(),
        userName = repositoryInfo?.owner?.login.orEmpty(),
        cardTitleText = repositoryInfo?.name.orEmpty(),
        cardDescriptionText = repositoryInfo?.description.orEmpty(),
        stargazersCount = repositoryInfo?.stargazerCount ?: 0,
        languagesInfo = repositoryInfo?.languages?.nodes?.mapNotNull { it?.convertToUiModel() }.orEmpty()
    )
}

fun GetUserDataQuery.Edge3.convertToTopRepositoriesUiModel() : CardForUserItemUiModel {
    val repositoryInfo: GetUserDataQuery.Node4? = node

    return CardForUserItemUiModel(
        avatarUrl = (repositoryInfo?.owner?.avatarUrl as String?).orEmpty(),
        userName = repositoryInfo?.owner?.login.orEmpty(),
        cardTitleText = repositoryInfo?.name.orEmpty(),
        cardDescriptionText = repositoryInfo?.description.orEmpty(),
        stargazersCount = repositoryInfo?.stargazerCount ?: 0,
        languagesInfo = repositoryInfo?.languages?.edges?.mapNotNull { it?.convertToUiModel() }.orEmpty()
    )
}

fun GetUserDataQuery.Edge1.convertToStarredRepositoriesUiModel() : CardForUserItemUiModel {
    val repositoryInfo = node

    return CardForUserItemUiModel(
        avatarUrl = (repositoryInfo.owner.avatarUrl as String?).orEmpty(),
        userName = repositoryInfo.owner.login,
        cardTitleText = repositoryInfo.name,
        cardDescriptionText = repositoryInfo.description.orEmpty(),
        stargazersCount = repositoryInfo.stargazerCount,
        languagesInfo = repositoryInfo.languages?.edges?.mapNotNull { it?.convertToUiModel() }.orEmpty()
    )
}

fun GetUserDataQuery.Edge2.convertToUiModel() : CardForUserItemUiModel.LanguageAttributes {
    return CardForUserItemUiModel.LanguageAttributes(
        colorOfLanguage = this.node.color.orEmpty(),
        programmingLanguageName = this.node.name
    )
}

fun GetUserDataQuery.Edge4.convertToUiModel() : CardForUserItemUiModel.LanguageAttributes {
    return CardForUserItemUiModel.LanguageAttributes(
        colorOfLanguage = this.node.color.orEmpty(),
        programmingLanguageName = this.node.name
    )
}

fun GetUserDataQuery.Node1.convertToUiModel() : CardForUserItemUiModel.LanguageAttributes {
    return CardForUserItemUiModel.LanguageAttributes(
        colorOfLanguage = this.color.orEmpty(),
        programmingLanguageName = this.name
    )
}