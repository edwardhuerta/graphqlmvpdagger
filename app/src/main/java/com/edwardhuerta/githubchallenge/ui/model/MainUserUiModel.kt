package com.edwardhuerta.githubchallenge.ui.model

data class MainUserUiModel(
    val avatarUrl : String,
    val firstLastName : String,
    val userName : String,
    val email : String,
    val followersCount : Int,
    val followingCount : Int,
)
