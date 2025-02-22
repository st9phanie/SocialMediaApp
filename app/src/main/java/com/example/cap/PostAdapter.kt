package com.example.cap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
class PostAdapter(private val tweets: List<Post>) : RecyclerView.Adapter<PostAdapter.TweetViewHolder>() {

    class TweetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val displayName: TextView = itemView.findViewById(R.id.displayname)
        val username: TextView = itemView.findViewById(R.id.username)
        val content: TextView = itemView.findViewById(R.id.content)
        val timestamp: TextView = itemView.findViewById(R.id.time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TweetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.postlayout, parent, false)
        return TweetViewHolder(view)
    }

    override fun onBindViewHolder(holder: TweetViewHolder, position: Int) {
        val tweet = tweets[position]
        holder.username.text = tweet.username
        holder.content.text = tweet.content
        holder.timestamp.text = tweet.timestamp
    }

    override fun getItemCount() = tweets.size
}
