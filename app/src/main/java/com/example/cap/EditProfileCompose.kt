package com.example.cap

//import ProfileViewModel
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun EditProfileScreen(
    userID: String
) {
    val db = FirebaseFirestore.getInstance()

    var username by remember { mutableStateOf("") }
    var displayName by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }
    var profileImageUri by remember { mutableStateOf<Uri?>(null)}

    //val context = LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            profileImageUri = uri
        }
    }

    LaunchedEffect(userID) {
        db.collection("user_profile_info").document(userID).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    username = document.getString("username") ?: ""
                    displayName = document.getString("display_name") ?: ""
                    bio = document.getString("bio") ?: ""
                }
            }}
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0x80fdfdfd))

    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp)
                .background(color = Color(0xfffdfdfd))) {
            SmallTopAppBarExample(userID, username, displayName, bio)
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFD9D9D9),
                modifier = Modifier
                    .layout(){ measurable, constraints ->
                        val placeable = measurable.measure(constraints.copy(
                            maxWidth = constraints.maxWidth + 30.dp.roundToPx(), //add the end padding 16.dp
                        ))
                        layout(placeable.width, placeable.height) {
                            placeable.place(0.dp.roundToPx(), 0)
                        }
                    })
            // Profile Picture Picker
            Box(
                modifier = Modifier
                    .padding(top = 20.dp)
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFDFDFD))
                    .border(width = 0.1.dp, color = Color(0xFFD9D9D9), shape = CircleShape)
                    .align(Alignment.CenterHorizontally)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentAlignment = Alignment.Center
            ) {
                if (profileImageUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(profileImageUri),
                        contentDescription = "Profile Picture",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text("Pick Image", color = Color.Gray, fontSize = 14.sp)
                }
            }

            Text(
                text = "Edit profile picture",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(vertical = 15.dp),
                color = Color.Black,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = IG_sans
            )

            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFD9D9D9),
                modifier = Modifier
                    .layout(){ measurable, constraints ->
                        val placeable = measurable.measure(constraints.copy(
                            maxWidth = constraints.maxWidth + 30.dp.roundToPx(), //add the end padding 16.dp
                        ))
                        layout(placeable.width, placeable.height) {
                            placeable.place(0.dp.roundToPx(), 0)
                        }
                    })
            Spacer(modifier = Modifier.height(10.dp))
            // Username Field
            ProfileTextField(
                label = "Username",
                value = username,
                onValueChange = { username = it }
            )
            ProfileTextField(
                label = "Name",
                value = displayName,
                onValueChange = { displayName = it }
            )

            // Bio Field (with character count)
            Column {
                ProfileTextField(label = "Bio", value = bio, onValueChange = { if (it.length <= 150) bio = it })
                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${bio.length} / 150",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                    fontSize = 12.sp,
                    color = Color(0xFFD9D9D9)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(thickness = 0.5.dp, color = Color(0xFFD9D9D9),
                modifier = Modifier
                    .layout(){ measurable, constraints ->
                        val placeable = measurable.measure(constraints.copy(
                            maxWidth = constraints.maxWidth + 30.dp.roundToPx(), //add the end padding 16.dp
                        ))
                        layout(placeable.width, placeable.height) {
                            placeable.place(0.dp.roundToPx(), 0)
                        }
                    })
            Spacer(modifier = Modifier.height(30.dp))
            LogoutButton()
        }
    }
}
@Composable
fun LogoutButton() {
    val context = LocalContext.current
Box(contentAlignment = Alignment.Center){
    Text(
        text = "Log out",
        modifier = Modifier
            .clickable {
                FirebaseAuth.getInstance().signOut()
                val prefs = context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()
                val intent = Intent(context, Login::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                context.startActivity(intent)
            },
        fontFamily = IG_sans,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Red
    )}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmallTopAppBarExample(userID: String, username: String, displayName: String, bio: String) {
    val context = LocalContext.current

    CenterAlignedTopAppBar(
            modifier = Modifier
                .height(90.dp)
                .fillMaxHeight(),
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color(0xfffdfdfd),
                titleContentColor = Color.Black,
            ),
            title = { Box(
                modifier = Modifier.fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {Text(text="Edit Profile", fontFamily = IG_sans, fontSize = 18.sp, fontWeight = FontWeight.Bold) }},
            navigationIcon = {
                Box(
                    modifier = Modifier.fillMaxHeight(),
                    contentAlignment = Alignment.Center
                ) {

                IconButton(onClick = {
                    val db = FirebaseFirestore.getInstance()
                    db.collection("user_profile_info")
                        .whereEqualTo("username", username)
                        .get()
                        .addOnSuccessListener { documents ->
                            val usernameTaken = documents.any { it.id != userID }

                            if (usernameTaken) {
                                Toast.makeText(context, "Username already taken", Toast.LENGTH_SHORT).show()
                            } else {
                                updateProfile(userID, username, displayName, bio, context)
                                val intent = Intent(context, MainActivity::class.java)
                                context.startActivity(intent)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Error checking username", Toast.LENGTH_SHORT).show()
                        }

                }) {
                    Icon(painter = painterResource(id = R.drawable.baseline_arrow_back_ios_new_24),
                        contentDescription = "Back")
                }
            }}
        )
    }

@Composable
fun ProfileTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.weight(0.2f),
            color = Color.Black,
            fontSize = 16.sp,
            fontFamily = IG_sans
        )
        val gray90 = Color(0xFFFDFDFD)
        val gray75 = Color(0xFFD9D9D9)
        TextField(
            modifier = Modifier
                .weight(0.8f)
                .fillMaxWidth(),
            value = value,
            placeholder = {
                Text(
                    text = label,
                    color = Color(0xFFE6E6E6),
                    fontFamily = IG_sans,
                    fontSize = 16.sp
                )
            },
            onValueChange = { newText -> onValueChange(newText) },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            colors = TextFieldDefaults.colors(
                cursorColor = Color.Gray,
                unfocusedContainerColor = gray90,
                focusedContainerColor = gray90,
                focusedPlaceholderColor = gray75,
                unfocusedPlaceholderColor = gray75,
                unfocusedIndicatorColor = gray75,
                focusedIndicatorColor = gray75,
                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,
                unfocusedTrailingIconColor = gray90,
                focusedTrailingIconColor = gray75,
                disabledTextColor = Color.Black,
            ),
            trailingIcon = {
                if (value.isNotEmpty()) {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(imageVector = Icons.Outlined.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                    }
                }
            }
        )
    }}

    fun updateProfile(userId: String, username: String, displayName: String, bio: String, context: Context) {
        val db = FirebaseFirestore.getInstance()
        val userUpdates = mapOf(
            "username" to username,
            "display_name" to displayName,
            "bio" to bio
        )

        db.collection("user_profile_info").document(userId).update(userUpdates)
            .addOnSuccessListener {
                Toast.makeText(context, "Profile updated successfully!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        db.collection("users").document(userId).update(mapOf("username" to username))

    }


@Preview
@Composable
fun PreviewEditProfileScreen() {
    EditProfileScreen(userID = FirebaseAuth.getInstance().currentUser?.uid ?: "" )
}