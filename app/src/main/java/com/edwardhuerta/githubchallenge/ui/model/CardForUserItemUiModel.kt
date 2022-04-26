package com.edwardhuerta.githubchallenge.ui.model

data class CardForUserItemUiModel(
    val avatarUrl: String,
    val userName: String,
    val cardTitleText: String,
    val cardDescriptionText: String,
    val stargazersCount: Int,
    val languagesInfo: List<LanguageAttributes>
) {
    data class LanguageAttributes(
        val colorOfLanguage: String,
        val programmingLanguageName: String
    )
}
