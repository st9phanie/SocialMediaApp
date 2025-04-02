import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cap.R
import com.example.cap.SearchResultProfile

class SearchResultsAdapter(
    private val userList: List<SearchResultProfile>,
    private val onUserClick: (SearchResultProfile) -> Unit // Click listener
) : RecyclerView.Adapter<SearchResultsAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.profile_results, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
        holder.itemView.setOnClickListener {
            onUserClick(user)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.username)
        //private val profileImageView: ImageView = itemView.findViewById(R.id.profilePicture)

        fun bind(user: SearchResultProfile) {
            usernameTextView.text = user.username
            // Load profile image if available
            // You can use libraries like Glide or Picasso to load images
          //  if (!user.profileImageUrl.isNullOrEmpty()) {
           //     Glide.with(itemView.context).load(user.profileImageUrl).into(profileImageView)
          //  }
        }
    }
}
