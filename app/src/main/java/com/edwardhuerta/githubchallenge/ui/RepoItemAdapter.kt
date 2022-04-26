package com.edwardhuerta.githubchallenge.ui

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.request.CachePolicy
import coil.transform.CircleCropTransformation
import com.edwardhuerta.githubchallenge.databinding.CardItemBinding
import com.edwardhuerta.githubchallenge.ui.model.CardForUserItemUiModel
import timber.log.Timber

class RepoItemAdapter(
    private val cardIsMaxWidth : Boolean = false
) : ListAdapter<CardForUserItemUiModel, ItemViewHolder>(AdapterDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            binding = CardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false).apply {
                if (cardIsMaxWidth) {
                    cardItemRootContainer.layoutParams = ConstraintLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 164.toPx()).apply {
                        bottomMargin = 15.toPx()
                    }
                }
            }
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        if (position != RecyclerView.NO_POSITION) {
            holder.bind(getItem(position))
        } else {
            Timber.v("bind can be ignored : NO_POSITION")
        }
    }
}

private class AdapterDiffCallback : DiffUtil.ItemCallback<CardForUserItemUiModel>() {
    override fun areItemsTheSame(oldItem: CardForUserItemUiModel, newItem: CardForUserItemUiModel): Boolean = oldItem.userName == newItem.userName
    override fun areContentsTheSame(oldItem: CardForUserItemUiModel, newItem: CardForUserItemUiModel): Boolean = oldItem == newItem
}

class ItemViewHolder(
    private val binding: CardItemBinding,
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: CardForUserItemUiModel) {
        binding.cardItemAvatar.load(item.avatarUrl) {
            diskCachePolicy(CachePolicy.ENABLED)
            crossfade(true)
            transformations(CircleCropTransformation())
        }
        binding.cardItemUsername.text = item.userName
        binding.cardItemTitle.text = item.cardTitleText
        binding.cardItemDescription.text = item.cardDescriptionText
        binding.cardItemStargazers.text = "${item.stargazersCount}"
        binding.cardItemLanguageName.text = item.languagesInfo.first().programmingLanguageName
        binding.cardItemLanguageName.changeTintOfDrawables(item.languagesInfo.first().colorOfLanguage)
    }

    private fun TextView.changeTintOfDrawables(colorStr: String) {
        for (drawable in compoundDrawables) {
            if (drawable != null) {
                drawable.colorFilter = PorterDuffColorFilter(Color.parseColor(colorStr), PorterDuff.Mode.SRC_IN)
            }
        }
    }
}