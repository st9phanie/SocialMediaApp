import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.CollectionReference
open class ProfileViewModel : ViewModel() {
    private val usersRef: CollectionReference = FirebaseFirestore.getInstance().collection("users")
    private val currentUserId: String = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    val _usernameExists = mutableStateOf(false)
    val usernameExists: State<Boolean> get() = _usernameExists

    fun checkIfUsernameExists(username: String) {
        usersRef.whereEqualTo("username", username).get()
            .addOnSuccessListener { querySnapshot ->
                // Check if any user has the same username and is NOT the current user
                _usernameExists.value = querySnapshot.documents.any {
                    it.id != currentUserId
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error checking username", e)
                _usernameExists.value = false
            }
    }
}

