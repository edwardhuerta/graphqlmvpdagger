package com.edwardhuerta.githubchallenge.ui.customview

import android.content.Context
import android.graphics.Paint
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.edwardhuerta.githubchallenge.R
import com.edwardhuerta.githubchallenge.databinding.MainScreenLayoutBinding
import com.edwardhuerta.githubchallenge.presentation.UserOverviewContract
import com.edwardhuerta.githubchallenge.ui.RepoItemAdapter
import com.edwardhuerta.githubchallenge.ui.model.MainScreenUiModel
import kotlinx.coroutines.CoroutineScope

class MainScreenRootLayout : FrameLayout, UserOverviewContract.View {

    private var binding: MainScreenLayoutBinding? = null
    private var presenter: UserOverviewContract.Presenter? = null
    private var coroutineScopeFromParent: CoroutineScope? = null

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        init()
    }

    private fun init() {
        val newBinding: MainScreenLayoutBinding = MainScreenLayoutBinding.inflate(LayoutInflater.from(context), this, true)
        this.binding = newBinding

        newBinding.pinnedRecyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = RepoItemAdapter(cardIsMaxWidth = true)
            isNestedScrollingEnabled = false
        }
        newBinding.topRepositoriesRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = RepoItemAdapter()
            isNestedScrollingEnabled = false
        }
        newBinding.starredRepositoriesRecyclerview.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = RepoItemAdapter()
            isNestedScrollingEnabled = false
        }

        this.binding?.errorContainerRetryButton?.setOnClickListener {
            presenter?.fetchData()
        }

        this.binding?.mainScreenSwipeToRefresh?.apply {
            setOnRefreshListener {
                presenter?.fetchData(true)
            }
        }

        this.binding?.pinnedTitleViewAll?.enableUnderline()
        this.binding?.starredRepositoriesTitleViewAll?.enableUnderline()
        this.binding?.topRepositoriesTitleViewAll?.enableUnderline()
    }

    private fun TextView.enableUnderline() {
        paintFlags = paintFlags or Paint.UNDERLINE_TEXT_FLAG
    }

    private fun bindUi(uiModel : MainScreenUiModel) {
        this.binding?.apply {
            mainUserAvatar.load(uiModel.mainUserModel?.avatarUrl) {
                crossfade(true)
                transformations(CircleCropTransformation())
            }
            mainUserFirstlastname.text = uiModel.mainUserModel?.firstLastName
            mainUserUsername.text = uiModel.mainUserModel?.userName
            mainUserEmail.text = uiModel.mainUserModel?.email
            mainUserFollowersCount.text = "${uiModel.mainUserModel?.followersCount ?: 0}"
            mainUserFollowingCount.text = "${uiModel.mainUserModel?.followingCount ?: 0}"

            (pinnedRecyclerview.adapter as RepoItemAdapter?)?.submitList(uiModel.pinnedItems)
            (topRepositoriesRecyclerview.adapter as RepoItemAdapter?)?.submitList(uiModel.topRepositories)
            (starredRepositoriesRecyclerview.adapter as RepoItemAdapter?)?.submitList(uiModel.starredRepositories)
        }
    }

    override fun toggleProgressbar(displayOnScreen: Boolean) {
        this.binding?.mainScreenSwipeToRefresh?.isRefreshing = displayOnScreen
    }

    override fun showError() {
        this.binding?.mainScreenErrorContainer?.visibility = View.VISIBLE
        this.binding?.mainScreenMainContentContainer?.visibility = View.INVISIBLE
        this.binding?.errorContainerMessage?.text = context.getString(R.string.error_generic)
    }

    override fun showErrorOnEmptyResponse() {
        this.binding?.mainScreenErrorContainer?.visibility = View.VISIBLE
        this.binding?.mainScreenMainContentContainer?.visibility = View.INVISIBLE
        this.binding?.errorContainerMessage?.text = context.getString(R.string.error_on_empty_response)
    }

    override fun showUserOverviewData(uiModel: MainScreenUiModel) {
        this.binding?.mainScreenErrorContainer?.let { errorContainer ->
            if (errorContainer.visibility == View.VISIBLE) {
                errorContainer.visibility = View.INVISIBLE
            }
        }

        this.binding?.mainScreenMainContentContainer?.let { mainContent ->
            if (mainContent.visibility == View.INVISIBLE) {
                mainContent.visibility = View.VISIBLE
            }
        }
        bindUi(uiModel)
    }

    override fun setPresenter(presenter: UserOverviewContract.Presenter, coroutineScope: CoroutineScope) {
        this.presenter = presenter
        this.coroutineScopeFromParent = coroutineScope
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        presenter?.setView(this, coroutineScopeFromParent)
        presenter?.fetchData()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        presenter?.unsetView()
    }
}