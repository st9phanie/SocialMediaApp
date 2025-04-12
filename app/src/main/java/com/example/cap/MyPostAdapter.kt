package com.example.cap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView

class MyPostAdapter(private val posts: List<Post>, private val onDeleteClick: (Post) -> Unit) : RecyclerView.Adapter<MyPostAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val displayName: TextView = itemView.findViewById(R.id.displayname)
        val username: TextView = itemView.findViewById(R.id.username)
        val content: TextView = itemView.findViewById(R.id.content)
        val timestamp: TextView = itemView.findViewById(R.id.time)
        val more: ImageButton = itemView.findViewById(R.id.more)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mypostlayout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.displayName.text = post.displayName
        holder.username.text = "@${post.username}"
        holder.content.text = post.content
        holder.timestamp.text = getTimeAgo(post.timestamp)
        holder.more.setOnClickListener {
            // Show a popup menu
            val popup = PopupMenu(holder.itemView.context, holder.more)
            popup.inflate(R.menu.delete_post)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.delete_post -> {
                        onDeleteClick(post)
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    override fun getItemCount() = posts.size

    private fun getTimeAgo(time: Long): String {
        val diff = System.currentTimeMillis() - time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val weeks = days / 7

        return when {
            seconds < 60 -> "Just now"
            minutes < 60 -> "$minutes ${if (minutes == 1L) "minute" else "minutes"} ago"
            hours < 24 -> "$hours ${if (hours == 1L) "hour" else "hours"} ago"
            days < 7 -> "$days ${if (days == 1L) "day" else "days"} ago"
            weeks < 4 -> "$weeks ${if (weeks == 1L) "week" else "weeks"} ago"
            else -> "More than a month ago"
        }
    }

    private fun deletePost(){

    }

}