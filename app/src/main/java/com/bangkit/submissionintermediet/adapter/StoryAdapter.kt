package com.bangkit.submissionintermediet.adapter

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bangkit.submissionintermediet.pagging.StoryEntity
import com.bangkit.submissionintermediet.databinding.ListStoryBinding
import com.bangkit.submissionintermediet.view.detail.DetailActivity
import com.bumptech.glide.Glide

class StoryAdapter : PagingDataAdapter<StoryEntity, StoryAdapter.StoriesViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoriesViewHolder {
        val view = ListStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return StoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoriesViewHolder, position: Int) {
        val storyEntity = getItem(position)
        if (storyEntity != null) {
            holder.bind(storyEntity)
        }
    }

    inner class StoriesViewHolder(private val binding: ListStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(story: StoryEntity) {
            with(binding) {
                listName.text = story.name
                tvDescription.text = story.description
                Glide.with(itemView.context).load(story.photoUrl).into(listPhoto)

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailActivity::class.java).apply {
                        putExtra(DetailActivity.EXTRA_STORY_ID, story.id)
                        putExtra(DetailActivity.EXTRA_STORY_NAME, story.name)
                        putExtra(DetailActivity.EXTRA_STORY_DESCRIPTION, story.description)
                        putExtra(DetailActivity.EXTRA_STORY_IMAGE_URL, story.photoUrl)
                    }

                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        itemView.context as Activity,
                        Pair(listPhoto, "image"),
                        Pair(listName, "name"),
                        Pair(tvDescription, "description")
                    )
                    itemView.context.startActivity(intent, options.toBundle())
                }
            }
        }
    }

    companion object {
        val DIFF_CALLBACK: DiffUtil.ItemCallback<StoryEntity> =
            object : DiffUtil.ItemCallback<StoryEntity>() {
                override fun areItemsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: StoryEntity,
                    newItem: StoryEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}
