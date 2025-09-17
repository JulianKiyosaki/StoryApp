package com.julian.storyapp.service

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.julian.storyapp.R
import java.text.SimpleDateFormat
import java.util.*

class StoryAdapter(private val onItemClick: (Story) -> Unit) :
    PagingDataAdapter<Story, StoryAdapter.StoryViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_story, parent, false)
        return StoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: StoryViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
            holder.itemView.setOnClickListener { onItemClick(data) }
        }
    }

    inner class StoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_item_name)
        private val descriptionTextView: TextView = itemView.findViewById(R.id.desc_story)
        private val createdAtTextView: TextView = itemView.findViewById(R.id.tv_item_created_at)
        private val photoImageView: ImageView = itemView.findViewById(R.id.iv_item_photo)

        fun bind(story: Story) {
            nameTextView.text = story.name
            descriptionTextView.text = story.description

            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val date = try {
                dateFormat.parse(story.createdAt)
            } catch (e: Exception) {
                null
            }
            createdAtTextView.text = date?.let {
                SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(it)
            } ?: "Unknown date"

            Glide.with(itemView.context)
                .load(story.photoUrl)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_foreground)
                .into(photoImageView)
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Story>() {
            override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean =
                oldItem == newItem
        }
    }
}
