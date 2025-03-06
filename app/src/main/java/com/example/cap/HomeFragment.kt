package com.example.cap

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.cap.databinding.ActivityMainBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var postAdapter: PostAdapter
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment HomeFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val posts = listOf(
            Post("dn", "Alice", "Hello, world!", "10:00 AM"),
            Post("dn", "Bob", "This is my first tweet.", "10:15 AM"),
            Post("dn", "Bob", "This is my first tweet.", "10:15 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Bob", "This is my first tweet.", "10:15 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM"),
            Post("dn", "Charlie", "Kotlin is awesome!", "10:30 AM")
        )
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        postAdapter = PostAdapter(posts)
        recyclerView.adapter = postAdapter
    }
    override fun onRefresh() {
        Handler(Looper.getMainLooper()).postDelayed({
            swipeRefreshLayout.isRefreshing = false
        },300)
    }
}