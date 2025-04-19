package com.example.cap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.imageview.ShapeableImageView

class PostAdapter(private val posts: List<Post>,private val onProfileClick: (userId: String, username: String, displayName: String) -> Unit) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val displayName: TextView = itemView.findViewById(R.id.displayname)
        val username: TextView = itemView.findViewById(R.id.username)
        val content: TextView = itemView.findViewById(R.id.content)
        val timestamp: TextView = itemView.findViewById(R.id.time)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.postlayout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.displayName.text = post.displayName
        holder.username.text = "@${post.username}"
        holder.content.text = post.content
        holder.timestamp.text = getTimeAgo(post.timestamp)

        holder.displayName.setOnClickListener {
            onProfileClick(post.uid,post.username,post.displayName)
        }

        holder.username.setOnClickListener {
            onProfileClick(post.uid,post.username,post.displayName)
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
