package com.edwardhuerta.githubchallenge.ui.model

data class MainScreenUiModel(
    val mainUserModel : MainUserUiModel?,
    val pinnedItems : List<CardForUserItemUiModel?>,
    val topRepositories : List<CardForUserItemUiModel?>,
    val starredRepositories : List<CardForUserItemUiModel?>,
)
