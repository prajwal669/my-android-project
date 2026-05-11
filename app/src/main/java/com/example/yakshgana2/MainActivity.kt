package com.example.yakshgana2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { AppEntry() }
    }
}

data class Artist(val name: String, val role: String, val description: String, val image: Int)
data class Event(val name: String = "", val troupeName: String = "", val date: String = "", val time: String = "", val location: String = "", val ticketPrice: String = "")
data class AudioTrack(val title: String, val description: String, val url: String)
data class ChatMessage(val text: String, val isUser: Boolean)

@Composable
fun AppEntry() {
    var showSplash by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(Firebase.auth.currentUser != null) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        showSplash = false
    }
    when {
        showSplash -> SplashScreen()
        !isLoggedIn -> LoginScreen(onLoginSuccess = { isLoggedIn = true })
        else -> HeritageApp(onLogout = { isLoggedIn = false })
    }
}

@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎭", fontSize = 80.sp)
            Spacer(modifier = Modifier.height(20.dp))
            Text("ಯಕ್ಷಗಾನ ಲೋಕ", color = Color(0xFFFF9800), fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("YAKSHAGANA LOKA", color = Color.White, fontSize = 16.sp, letterSpacing = 4.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Heritage • Culture • Karnataka", color = Color.Gray, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(40.dp))
            CircularProgressIndicator(color = Color(0xFFFF9800), modifier = Modifier.size(32.dp), strokeWidth = 3.dp)
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black), contentAlignment = Alignment.Center) {
        Column(modifier = Modifier.fillMaxWidth().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Text("🎭", fontSize = 60.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Yakshagana Loka", color = Color(0xFFFF9800), fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("ಯಕ್ಷಗಾನ ಲೋಕ", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Text(if (isSignUp) "Create Account" else "Welcome Back",
                color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value = email, onValueChange = { email = it },
                label = { Text("Email", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800), unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                singleLine = true)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password, onValueChange = { password = it },
                label = { Text("Password", color = Color.Gray) },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF9800), unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White),
                singleLine = true)
            Spacer(modifier = Modifier.height(8.dp))
            if (errorMsg.isNotEmpty()) {
                Text(errorMsg, color = if (errorMsg.contains("sent")) Color.Green else Color.Red, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
            }
            Button(
                onClick = {
                    if (email.isBlank() || password.isBlank()) { errorMsg = "Please enter email and password"; return@Button }
                    isLoading = true; errorMsg = ""
                    if (isSignUp) {
                        Firebase.auth.createUserWithEmailAndPassword(email, password)
                            .addOnSuccessListener { isLoading = false; onLoginSuccess() }
                            .addOnFailureListener { isLoading = false; errorMsg = it.message ?: "Sign up failed" }
                    } else {
                        Firebase.auth.signInWithEmailAndPassword(email, password)
                            .addOnSuccessListener { isLoading = false; onLoginSuccess() }
                            .addOnFailureListener { isLoading = false; errorMsg = it.message ?: "Login failed" }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoading) CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                else Text(if (isSignUp) "Sign Up" else "Login", color = Color.Black, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = { isSignUp = !isSignUp; errorMsg = "" }) {
                Text(if (isSignUp) "Already have an account? Login" else "New user? Create Account",
                    color = Color(0xFFFF9800), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = {
                if (email.isBlank()) { errorMsg = "Enter your email first"; return@TextButton }
                Firebase.auth.sendPasswordResetEmail(email)
                    .addOnSuccessListener { errorMsg = "Reset email sent! Check your inbox." }
                    .addOnFailureListener { errorMsg = it.message ?: "Failed" }
            }) {
                Text("Forgot Password?", color = Color.Gray, fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun HeritageApp(onLogout: () -> Unit) {
    var screen by remember { mutableStateOf("hub") }
    var selectedArtist by remember { mutableStateOf<Artist?>(null) }
    var showProfile by remember { mutableStateOf(false) }

    if (showProfile) {
        ProfileScreen(onBack = { showProfile = false }, onLogout = { onLogout() })
        return
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.Black) {
                NavigationBarItem(selected = screen == "hub", onClick = { screen = "hub" },
                    icon = { Icon(Icons.Default.Home, "", tint = Color.White) },
                    label = { Text("Hub", color = Color.White) })
                NavigationBarItem(selected = screen == "stars", onClick = { screen = "stars" },
                    icon = { Icon(Icons.Default.Star, "", tint = Color.White) },
                    label = { Text("Stars", color = Color.White) })
                NavigationBarItem(selected = screen == "audio", onClick = { screen = "audio" },
                    icon = { Icon(Icons.Default.MusicNote, "", tint = Color.White) },
                    label = { Text("Audio", color = Color.White) })
                NavigationBarItem(selected = screen == "ai", onClick = { screen = "ai" },
                    icon = { Icon(Icons.Default.Chat, "", tint = Color.White) },
                    label = { Text("AI", color = Color.White) })
                NavigationBarItem(selected = screen == "about", onClick = { screen = "about" },
                    icon = { Icon(Icons.Default.Info, "", tint = Color.White) },
                    label = { Text("About", color = Color.White) })
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().background(Color.Black).padding(padding)) {
            when {
                selectedArtist != null -> ArtistDetailScreen(selectedArtist!!) { selectedArtist = null }
                screen == "hub" -> HubScreen(onProfileClick = { showProfile = true })
                screen == "stars" -> StarsScreen { selectedArtist = it }
                screen == "audio" -> AudioScreen()
                screen == "ai" -> AIScreen()
                screen == "about" -> AboutScreen()
            }
        }
    }
}

@Composable
fun ProfileScreen(onBack: () -> Unit, onLogout: () -> Unit) {
    val user = Firebase.auth.currentUser
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "", tint = Color(0xFFFF9800)) }
                Text("My Profile", color = Color.White, fontSize = 18.sp)
            }
        }
        Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(20.dp))
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(Color(0xFFFF9800), RoundedCornerShape(40.dp))
                    .clickable { },
                contentAlignment = Alignment.Center
            ) {
                Text(user?.email?.first()?.uppercaseChar()?.toString() ?: "U",
                    color = Color.Black, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text("Tap to change photo (coming soon)", color = Color.Gray, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(user?.email ?: "Guest", color = Color.White, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Text("Yakshagana Fan", color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(32.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Account Info", color = Color(0xFFFF9800), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Email: ${user?.email}", color = Color.White, fontSize = 14.sp)
                    Text("User ID: ${user?.uid?.take(12)}...", color = Color.Gray, fontSize = 12.sp)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = { Firebase.auth.signOut(); onLogout() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Logout, "", tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Logout", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun HubScreen(onProfileClick: () -> Unit = {}) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var showNotification by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("events").get()
            .addOnSuccessListener { result ->
                events = result.documents.mapNotNull { it.toObject(Event::class.java) }
                loading = false
            }
            .addOnFailureListener { loading = false }
    }

    if (showNotification) {
        AlertDialog(
            onDismissRequest = { showNotification = false },
            title = { Text("🔔 Notifications", color = Color.White) },
            text = {
                Column {
                    Text("Tonight's shows are loaded!", color = Color(0xFFFF9800), fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("• Mangaluru Yakshagana Fest — 08:00 PM", color = Color.White, fontSize = 13.sp)
                    Text("• Udupi Krishna Mela — 06:00 PM", color = Color.White, fontSize = 13.sp)
                    Text("• Dharmasthala Mela 2025 — 07:00 PM", color = Color.White, fontSize = 13.sp)
                }
            },
            confirmButton = {
                TextButton(onClick = { showNotification = false }) {
                    Text("OK", color = Color(0xFFFF9800))
                }
            },
            containerColor = Color(0xFF1E1E1E)
        )
    }

    Column(modifier = Modifier.fillMaxSize().padding(20.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column {
                Text("HERITAGE", color = Color.White, fontSize = 28.sp)
                Text("HUB", color = Color(0xFFFF9800), fontSize = 28.sp)
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { showNotification = true }) {
                    Icon(Icons.Default.Notifications, "", tint = Color.White)
                }
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.AccountCircle, "", tint = Color(0xFFFF9800))
                }
            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Text("Tonight's Shows", color = Color(0xFFFF9800), fontSize = 18.sp)
        Spacer(modifier = Modifier.height(10.dp))
        if (loading) {
            CircularProgressIndicator(color = Color(0xFFFF9800))
        } else if (events.isEmpty()) {
            Text("No events found", color = Color.Gray)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(events) { event ->
                    Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                        shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(event.name, color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(event.troupeName, color = Color(0xFFFF9800), fontSize = 13.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.LocationOn, "", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(event.location, color = Color.Gray, fontSize = 12.sp)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Schedule, "", tint = Color.Gray, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${event.date} • ${event.time}", color = Color.Gray, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically) {
                                Text(event.ticketPrice,
                                    color = if (event.ticketPrice == "Free") Color.Green else Color.Yellow,
                                    fontSize = 13.sp)
                                val context = androidx.compose.ui.platform.LocalContext.current
                                TextButton(onClick = {
                                    val uri = android.net.Uri.parse("geo:0,0?q=${event.location}")
                                    context.startActivity(android.content.Intent(android.content.Intent.ACTION_VIEW, uri))
                                }) {
                                    Icon(Icons.Default.Map, "", tint = Color(0xFFFF9800), modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Map", color = Color(0xFFFF9800), fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StarsScreen(onClick: (Artist) -> Unit) {
    val artists = listOf(
        Artist("Keremane Shivarama Hegde", "Bhagavatha", "Keremane Shivarama Hegde was a visionary artist and the foundational pillar of the Badagutittu (Northern) style of Yakshagana. Despite a childhood of extreme poverty that forced him to work as a bus conductor, he rose to become the first Yakshagana performer to receive the Rashtrapati Award. In 1934, he founded the Idagunji Mahaganapathi Yakshagana Mandali, an institution that revolutionized the art form by making it a self-sustaining professional profession. During the pre-independence era, he bravely used his theatrical performances as a platform to spread messages for the Indian Freedom Movement. He is celebrated for his deep emotional character delineation and for establishing a family lineage that has now preserved the art for six generations.", R.drawable.artists1),
        Artist("Chittani Ramachandra Hegde", "Actor", "Chittani Ramachandra Hegde was a towering figure in the world of Yakshagana, revered for elevating the Badagutittu style to national prominence over a career spanning nearly 80 years. Born in 1933 in Honnavar, he overcame academic hurdles by dropping out of school at age seven to passionately pursue the arts. He is credited with creating the Chittani Gharana, a distinct performance style defined by vigorous dance, intricate footwork, and a commanding stage presence. His professional journey was historic, as he became the first Yakshagana artist to be honored with the Padma Shri (2012), India's fourth-highest civilian award. He remained active on stage until just a few days before his death in 2017.", R.drawable.artists2),
        Artist("Kalinga Navada", "Artist", "Kalinga Navada was a legendary Yakshagana Bhagavatha who redefined the musical dimensions of the Badagutittu tradition with his powerful and melodious voice. Born in 1957 in Gundmi, Udupi, he inherited his musical talent from his father and began his professional journey at the early age of 14. He is celebrated for pioneering a transformative style of singing that introduced complex classical ragas into folk theatre. His professional career reached its peak during his 12-year tenure with the Saligrama Mela. Though his life was cut short by a tragic road accident in 1990, his legacy as the Yuga Pravartaka (Epoch Maker) endures through his countless recordings.", R.drawable.artists3),
        Artist("Soorikumeru Govinda Bhat", "Artist", "Soorikumeru Govinda Bhat was a legendary veteran of the Tenku Thittu school of Yakshagana who professionally performed for nearly seven decades. Born into a humble family in Bantwal, he entered the art world at age 11 to support his family. He earned the title Dashavatari for his extraordinary ability to master diverse roles, ranging from heroic kings to villainous demons and graceful female characters. His professional journey was marked by a record-breaking tenure of over 50 consecutive years with the Dharmasthala Manjunatheswara Yakshagana Mandali. His immense contributions were recognized with the Sangeet Natak Akademi Award in 2016.", R.drawable.artists4)
    )
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Text("🌟 Yakshagana Stars", color = Color(0xFFFF9800), fontSize = 22.sp, modifier = Modifier.padding(16.dp))
        LazyColumn(modifier = Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(artists) { artist ->
                Card(modifier = Modifier.fillMaxWidth().clickable { onClick(artist) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Image(painter = painterResource(id = artist.image), contentDescription = null,
                            modifier = Modifier.size(80.dp).clip(RoundedCornerShape(12.dp)))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(artist.name, color = Color.White, fontSize = 16.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Box(modifier = Modifier.background(Color(0xFFFF9800).copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)) {
                                Text(artist.role, color = Color(0xFFFF9800), fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(artist.description, color = Color.Gray, fontSize = 12.sp, maxLines = 2)
                        }
                        Icon(Icons.Default.ChevronRight, "", tint = Color.Gray)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun ArtistDetailScreen(artist: Artist, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "", tint = Color(0xFFFF9800)) }
                Text("Artist Profile", color = Color.White, fontSize = 18.sp)
            }
        }
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(10.dp))
            Image(painter = painterResource(id = artist.image), contentDescription = null,
                modifier = Modifier.size(140.dp).clip(RoundedCornerShape(16.dp)))
            Spacer(modifier = Modifier.height(16.dp))
            Text(artist.name, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Box(modifier = Modifier.background(Color(0xFFFF9800), RoundedCornerShape(20.dp))
                .padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text(artist.role, color = Color.Black, fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(20.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("About", color = Color(0xFFFF9800), fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(artist.description, color = Color.White, fontSize = 14.sp, lineHeight = 22.sp)
                }
            }
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun AudioScreen() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val tracks = listOf(
        AudioTrack("Prasanaga: Ravana Vadhe", "Prasangakarta: Shri Hostota Manjunath Bhagawata", "https://youtu.be/61ZaoboQlJg?si=ApRfekvi4Lb-k7PN"),
        AudioTrack("Parna Kutira", "Devashilpi is beautiful", "https://youtu.be/kefxUGZUQKs?si=bQruRIakAu3Y4l6q"),
        AudioTrack("Karthaveeryarjuna Yakshagana Part I", "Chittani Ramachandra Hegde Troupe", "https://youtu.be/uYkhTu8wud8?si=nhIuzS-yaq-SxrrZ"),
        AudioTrack("Karthaveeryarjuna Yakshagana Part II", "Chittani Ramachandra Hegde Troupe", "https://youtu.be/7RBcuejaimg?si=hPjVp4eASTzDgmo6"),
        AudioTrack("Dharmasthala Yakshagana Mela", "Annual Dharmasthala performance Karthaveeryarjuna Kalaga", "https://youtu.be/GUceAatCBJs?si=tpuZOYsjkcn5XMt4"),
        AudioTrack("KOLLUR MAHATHME YAKSHAGANA", "Traditional performance", "https://www.youtube.com/watch?v=K4MJvj9CdRY"),
        AudioTrack("Mahabharata Yakshagana Full", "Full night performance", "https://youtu.be/iqUrhKuZTao?si=jfgXqh8xP58cwsLd"),
        AudioTrack("Yakshagana Karnataka Folk", "Karnataka folk tradition", "https://youtu.be/qUYWZXHL8JU?si=2sRE-tfhvXdT_xiO")
    )
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp)) {
            Column {
                Text("🎵 Talamaddale Radio", color = Color(0xFFFF9800), fontSize = 22.sp)
                Text("Tap ▶ to watch on YouTube", color = Color.Gray, fontSize = 13.sp)
            }
        }
        LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(tracks.size) { index ->
                val track = tracks[index]
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = {
                                context.startActivity(android.content.Intent(
                                    android.content.Intent.ACTION_VIEW,
                                    android.net.Uri.parse(track.url)))
                            },
                            modifier = Modifier.size(50.dp).background(Color(0xFFFF9800), RoundedCornerShape(25.dp))) {
                            Icon(Icons.Default.PlayArrow, "", tint = Color.White, modifier = Modifier.size(26.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(track.title, color = Color.White, fontSize = 15.sp)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(track.description, color = Color.Gray, fontSize = 12.sp)
                        }
                        Icon(Icons.Default.OpenInNew, "", tint = Color.Gray, modifier = Modifier.size(16.dp))
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

suspend fun askGemini(question: String): String {
    return try {
        val client = okhttp3.OkHttpClient.Builder()
            .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .readTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
            .build()
        val safeQuestion = question.replace("\"", "'").replace("\n", " ")
        val json = """{"contents":[{"parts":[{"text":"You are a helpful assistant. Answer concisely in 2-3 sentences. Question: $safeQuestion"}]}]}"""
        val body = json.toRequestBody("application/json".toMediaType())
        val request = okhttp3.Request.Builder()
            .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyBdpwb0Pxa3h8LU6r-DjIf-m8-Ejb-fPpw")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        val responseBody = response.body?.string() ?: ""
        if (!response.isSuccessful) return "API Error ${response.code}. Try again."
        val jsonObj = org.json.JSONObject(responseBody)
        jsonObj.getJSONArray("candidates")
            .getJSONObject(0)
            .getJSONObject("content")
            .getJSONArray("parts")
            .getJSONObject(0)
            .getString("text")
    } catch (e: Exception) {
        "Connection error. Check internet and try again."
    }
}

fun getYakshaganaAnswer(question: String): String? {
    val q = question.lowercase()
    return when {
        q.contains("what is yakshagana") || q.contains("yakshagana enu") ->
            "🎭 Yakshagana is a traditional theatre form of Karnataka, India. It combines dance, music, dialogue, costume, and stage techniques. It originated in coastal Karnataka around the 16th century."
        q.contains("costume") || q.contains("veshe") || q.contains("dress") ->
            "👑 Yakshagana costumes are very colorful and grand. Artists wear large headgear called 'Mundasu', colorful face paint, and heavy jewellery."
        q.contains("bhagavatha") || q.contains("singer") ->
            "🎵 The Bhagavatha is the main singer and narrator in Yakshagana. Famous ones include Keremane Shivarama Hegde and Balipa Narayana Bhagavatha."
        q.contains("chende") || q.contains("drum") ->
            "🥁 Chende is the main percussion instrument in Yakshagana. Other instruments include Maddale, Jagate, and Chakratala."
        q.contains("talamaddale") ->
            "🗣️ Talamaddale is Yakshagana performed sitting down without costumes, focusing on verbal debate and singing."
        q.contains("mela") || q.contains("troupe") ->
            "🎪 A Yakshagana troupe is called 'Mela'. Famous ones include Dharmasthala Mela and Idagunji Mahaganapathi Mela."
        q.contains("keremane") ->
            "🌟 Keremane Shivarama Hegde is one of the greatest Bhagavathas, known for powerful voice and mastery."
        q.contains("hello") || q.contains("hi") || q.contains("namaste") ->
            "🙏 Namaskara! I am your Yakshagana AI guide. Ask me anything!"
        else -> null
    }
}

@Composable
fun AIScreen() {
    var input by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var messages by remember { mutableStateOf(listOf(
        ChatMessage("🙏 Namaskara! I am your Yakshagana AI guide powered by Gemini. Ask me anything!", false)
    )) }
    val listState = androidx.compose.foundation.lazy.rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }
    val suggestions = listOf("What is Yakshagana?", "Tell me about Bhagavatha", "What is Talamaddale?", "Tell me a joke")
    fun handleQuestion(question: String) {
        if (question.isBlank() || isLoading) return
        val yakshaganaAnswer = getYakshaganaAnswer(question)
        if (yakshaganaAnswer != null) {
            messages = messages + ChatMessage(question, true) + ChatMessage(yakshaganaAnswer, false)
        } else {
            messages = messages + ChatMessage(question, true)
            isLoading = true
            scope.launch {
                val answer = kotlinx.coroutines.withContext(kotlinx.coroutines.Dispatchers.IO) { askGemini(question) }
                messages = messages + ChatMessage(answer, false)
                isLoading = false
            }
        }
    }
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp)) {
            Column {
                Text("🤖 Yakshagana AI", color = Color(0xFFFF9800), fontSize = 20.sp)
                Text("Powered by Gemini AI", color = Color.Gray, fontSize = 12.sp)
            }
        }
        androidx.compose.foundation.lazy.LazyRow(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(suggestions) { suggestion ->
                Box(modifier = Modifier.background(Color(0xFF2A1A00), RoundedCornerShape(20.dp))
                    .clickable { handleQuestion(suggestion) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)) {
                    Text(suggestion, color = Color(0xFFFF9800), fontSize = 12.sp)
                }
            }
        }
        LazyColumn(state = listState, modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(messages) { message ->
                Row(modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (message.isUser) Arrangement.End else Arrangement.Start) {
                    Box(modifier = Modifier.widthIn(max = 280.dp)
                        .background(if (message.isUser) Color(0xFFFF9800) else Color(0xFF1E1E1E),
                            RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp,
                                bottomStart = if (message.isUser) 16.dp else 4.dp,
                                bottomEnd = if (message.isUser) 4.dp else 16.dp))
                        .padding(12.dp)) {
                        Text(message.text, color = if (message.isUser) Color.Black else Color.White,
                            fontSize = 14.sp, lineHeight = 20.sp)
                    }
                }
            }
            if (isLoading) {
                item {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                        Box(modifier = Modifier.background(Color(0xFF1E1E1E), RoundedCornerShape(16.dp)).padding(12.dp)) {
                            Text("🤖 Thinking...", color = Color(0xFFFF9800), fontSize = 14.sp)
                        }
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(8.dp)) }
        }
        Row(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(8.dp),
            verticalAlignment = Alignment.CenterVertically) {
            TextField(value = input, onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask anything...", color = Color.Gray) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF2A2A2A), unfocusedContainerColor = Color(0xFF2A2A2A),
                    focusedTextColor = Color.White, unfocusedTextColor = Color.White,
                    cursorColor = Color(0xFFFF9800), focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent),
                shape = RoundedCornerShape(24.dp), singleLine = true)
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(onClick = { handleQuestion(input); input = "" },
                modifier = Modifier.size(48.dp).background(
                    if (isLoading) Color.Gray else Color(0xFFFF9800), RoundedCornerShape(24.dp))) {
                Icon(Icons.Default.Send, "", tint = Color.Black)
            }
        }
    }
}

@Composable
fun AboutScreen() {
    Column(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        Box(modifier = Modifier.fillMaxWidth().background(Color(0xFF1A1A1A)).padding(16.dp)) {
            Text("ℹ️ About", color = Color(0xFFFF9800), fontSize = 22.sp)
        }
        LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎭", fontSize = 60.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Yakshagana Loka", color = Color(0xFFFF9800), fontSize = 22.sp, fontWeight = FontWeight.Bold)
                        Text("ಯಕ್ಷಗಾನ ಲೋಕ", color = Color.Gray, fontSize = 14.sp)
                        Text("Version 1.0", color = Color.Gray, fontSize = 12.sp)
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("✨ Features", color = Color(0xFFFF9800), fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf("🏠 Hub — Live performance events", "🌟 Stars — Artist directory",
                            "🎵 Audio — Yakshagana on YouTube", "🤖 AI — Gemini powered assistant",
                            "🔐 Login — Firebase Authentication").forEach {
                            Text(it, color = Color.White, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1E1E)),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("👨‍💻 Project Info", color = Color(0xFFFF9800), fontSize = 16.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Project Title: 70", color = Color.White, fontSize = 14.sp)
                        Text("Android App Development using GenAI", color = Color.Gray, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        listOf("• Kotlin + Jetpack Compose", "• Firebase Auth + Firestore",
                            "• Google Gemini AI", "• YouTube Integration").forEach {
                            Text(it, color = Color.Gray, fontSize = 13.sp)
                        }
                    }
                }
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF2A1A00)),
                    shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🙏 ಯಕ್ಷಗಾನಕ್ಕೆ ಸ್ವಾಗತ", color = Color(0xFFFF9800), fontSize = 16.sp)
                        Text("Welcome to Yakshagana!", color = Color.White, fontSize = 14.sp)
                    }
                }
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}