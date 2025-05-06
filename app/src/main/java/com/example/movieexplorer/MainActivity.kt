package com.example.movieexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedReader
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import com.example.movieexplorer.ui.theme.MovieExplorerTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.material3.TextButton

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Set up the app theme and navigation
            MovieExplorerTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ) {
                    // Navigation graph: define all screens
                    composable("main") { MainScreen(navController) }
                    composable("add_movies") { AddMoviesScreen(navController) }
                    composable("search_movies") { SearchMoviesScreen(navController) }
                    composable("search_actors") { SearchActorsScreen(navController) }
                    composable("search_omdb_substring") { SearchOmdbSubstringScreen(navController) }
                }
            }
        }
    }
}

// Main screen: Welcome, popcorn image, and navigation buttons
@Composable
fun MainScreen(navController: NavHostController) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App title
            Text(
                text = "The Ultimate Place\n\nto Find Your\n\nNext Favorite Film",
                color = Color(0xFFD0BCFF),
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Popcorn image
            Image(
                painter = painterResource(id = R.drawable.popcorn),
                contentDescription = "Popcorn",
                modifier = Modifier.size(350.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
            // Navigation buttons
            MovieButton("Add Movies to DB", onClick = { navController.navigate("add_movies") })
            MovieButton("Search for Movie", onClick = { navController.navigate("search_movies") })
            MovieButton("Search for Actors", onClick = { navController.navigate("search_actors") })
            MovieButton("Search Movies Online", onClick = { navController.navigate("search_omdb_substring") })
        }
    }
}

// Reusable button for main actions
@Composable
fun MovieButton(text: String, onClick: () -> Unit, enabled: Boolean = true) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6C2BD7), // Purple
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

// AddMoviesScreen: Import movies from file to local DB
@Composable
fun AddMoviesScreen(navController: NavHostController) {
    val context = LocalContext.current
    var message by remember { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Button to add movies from file
            MovieButtonWithIcon(
                text = "Add Movies from File",
                icon = Icons.Default.Add,
                onClick = {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val assetManager = context.assets
                            val inputStream = assetManager.open("movies.txt")
                            val reader = BufferedReader(inputStream.reader())
                            val movies = mutableListOf<Movie>()
                            var buffer = StringBuilder()
                            var line: String?
                            while (reader.readLine().also { line = it } != null) {
                                if (line!!.trim().isEmpty()) {
                                    val movie = parseMovie(buffer.toString())
                                    if (movie != null) movies.add(movie)
                                    buffer = StringBuilder()
                                } else {
                                    buffer.append(line).append("\n")
                                }
                            }
                            // Last movie
                            val movie = parseMovie(buffer.toString())
                            if (movie != null) movies.add(movie)
                            val db = DatabaseProvider.getDatabase(context)
                            var addedCount = 0
                            for (m in movies) {
                                val existing = db.movieDao().getMovieByTitleAndYear(m.title, m.year)
                                if (existing == null) {
                                    db.movieDao().insertMovie(m)
                                    addedCount++
                                }
                            }
                            message = if (addedCount > 0) "Movies added to database!" else "No new movies added (all already exist)."
                        } catch (e: Exception) {
                            message = "Error: ${e.message}"
                        }
                    }
                }
            )
            Spacer(modifier = Modifier.height(24.dp))
            // Show result message
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(
                        message,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            // Back button
            MovieButtonWithIcon(
                text = "Back",
                icon = Icons.Default.ArrowBack,
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun SearchMoviesScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var title by rememberSaveable { mutableStateOf("") }
    var movie by remember { mutableStateOf<Movie?>(null) }
    var message by rememberSaveable { mutableStateOf("") }
    var details by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Search for a Movie",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 0.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextButton(onClick = { navController.navigate("search_actors") }) {
                    Text("Search for Actors", color = Color(0xFF6C2BD7), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { navController.navigate("search_omdb_substring") }) {
                    Text("Search Movie Online", color = Color(0xFF6C2BD7), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Enter movie title") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C2BD7),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6C2BD7),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovieButton("Retrieve Movie", onClick = {
                coroutineScope.launch(Dispatchers.IO) {
                    try {
                        val apiKey = "3984b463" // OMDb API key
                        if (apiKey.isBlank()) {
                            message = "API key is missing!"
                            return@launch
                        }
                        val url = "https://www.omdbapi.com/?t=" +
                                java.net.URLEncoder.encode(title, "UTF-8") +
                                "&apikey=$apiKey"
                        val connection = URL(url).openConnection() as HttpURLConnection
                        connection.requestMethod = "GET"
                        val response = connection.inputStream.bufferedReader().readText()
                        val json = JSONObject(response)
                        if (json.optString("Response") == "True") {
                            val m = Movie(
                                title = json.optString("Title"),
                                year = json.optString("Year"),
                                rated = json.optString("Rated"),
                                released = json.optString("Released"),
                                runtime = json.optString("Runtime"),
                                genre = json.optString("Genre"),
                                director = json.optString("Director"),
                                writer = json.optString("Writer"),
                                actors = json.optString("Actors"),
                                plot = json.optString("Plot")
                            )
                            movie = m
                            details = formatMovieDetails(m)
                            message = ""
                        } else {
                            movie = null
                            details = ""
                            message = json.optString("Error", "Movie not found.")
                        }
                    } catch (e: Exception) {
                        movie = null
                        details = ""
                        message = "Error: ${e.message}"
                    }
                }
            })
            // Add Save movie to Database button
            MovieButton(
                "Save movie to Database",
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        movie?.let {
                            val db = DatabaseProvider.getDatabase(context)
                            val existing = db.movieDao().getMovieByTitleAndYear(it.title, it.year)
                            if (existing == null) {
                                db.movieDao().insertMovie(it)
                                message = "Movie saved to database!"
                            } else {
                                message = "Movie already exists in database!"
                            }
                        }
                    }
                },
                enabled = movie != null
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (details.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(details, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(message, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            MovieButtonWithIcon(
                text = "Back",
                icon = Icons.Default.ArrowBack,
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun SearchActorsScreen(navController: NavHostController) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var actor by rememberSaveable { mutableStateOf("") }
    var results by rememberSaveable { mutableStateOf(listOf<Movie>()) }
    var message by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and navigation buttons for SearchActorsScreen
            Text(
                text = "Search for a actors",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 0.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextButton(onClick = { navController.navigate("search_movies") }) {
                    Text("Search for Movies", color = Color(0xFF6C2BD7), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { navController.navigate("search_omdb_substring") }) {
                    Text("Search Movie Online", color = Color(0xFF6C2BD7), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = actor,
                onValueChange = { actor = it },
                label = { Text("Enter actor name (or part of it)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C2BD7),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6C2BD7),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovieButtonWithIcon(
                text = "Search",
                icon = Icons.Default.Search,
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val db = DatabaseProvider.getDatabase(context)
                            val found = db.movieDao().searchMoviesByActor(actor)
                            results = found
                            message = if (found.isEmpty()) "No movies found for this actor." else ""
                        } catch (e: Exception) {
                            results = emptyList()
                            message = "Error: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (results.isNotEmpty()) {
                results.forEach { movie ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text(formatMovieDetails(movie), modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(message, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            MovieButtonWithIcon(
                text = "Back",
                icon = Icons.Default.ArrowBack,
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun SearchOmdbSubstringScreen(navController: NavHostController) {
    val coroutineScope = rememberCoroutineScope()
    var substring by rememberSaveable { mutableStateOf("") }
    var results by rememberSaveable { mutableStateOf(listOf<Movie>()) }
    var message by rememberSaveable { mutableStateOf("") }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title and navigation buttons for SearchOmdbSubstringScreen
            Text(
                text = "Search Movies Online",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(bottom = 0.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row {
                TextButton(onClick = { navController.navigate("search_movies") }) {
                    Text("Search for Movies", color = Color(0xFF6C2BD7), fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(onClick = { navController.navigate("search_actors") }) {
                    Text("Search for Actors", color = Color(0xFF6C2BD7), fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = substring,
                onValueChange = { substring = it },
                label = { Text("Enter part of movie title") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF6C2BD7),
                    unfocusedBorderColor = Color.Gray,
                    focusedLabelColor = Color(0xFF6C2BD7),
                    unfocusedLabelColor = Color.Gray,
                    cursorColor = Color.White,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
            MovieButtonWithIcon(
                text = "Search OMDb",
                icon = Icons.Default.Search,
                onClick = {
                    coroutineScope.launch(Dispatchers.IO) {
                        try {
                            val apiKey = "3984b463" // OMDb API key
                            val url = "https://www.omdbapi.com/?s=" +
                                java.net.URLEncoder.encode(substring, "UTF-8") +
                                "&apikey=$apiKey"
                            val connection = URL(url).openConnection() as HttpURLConnection
                            connection.requestMethod = "GET"
                            val response = connection.inputStream.bufferedReader().readText()
                            val json = JSONObject(response)
                            if (json.optString("Response") == "True") {
                                val searchArray = json.getJSONArray("Search")
                                val found = mutableListOf<Movie>()
                                for (i in 0 until searchArray.length()) {
                                    val item = searchArray.getJSONObject(i)
                                    found.add(
                                        Movie(
                                            title = item.optString("Title"),
                                            year = item.optString("Year"),
                                            rated = null,
                                            released = null,
                                            runtime = null,
                                            genre = null,
                                            director = null,
                                            writer = null,
                                            actors = null,
                                            plot = null
                                        )
                                    )
                                }
                                results = found
                                message = ""
                            } else {
                                results = emptyList()
                                message = json.optString("Error", "No movies found.")
                            }
                        } catch (e: Exception) {
                            results = emptyList()
                            message = "Error: ${e.message}"
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))
            if (results.isNotEmpty()) {
                results.forEach { movie ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Text("${movie.title} (${movie.year})", modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
            if (message.isNotEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Text(message, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(16.dp))
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
            MovieButtonWithIcon(
                text = "Back",
                icon = Icons.Default.ArrowBack,
                onClick = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun MovieButtonWithIcon(text: String, icon: ImageVector, onClick: () -> Unit, enabled: Boolean = true, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF6C2BD7),
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(20.dp)
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.padding(end = 8.dp))
        Text(text, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
    }
}

fun formatMovieDetails(movie: Movie): String {
    return """
Title: ${movie.title}
Year: ${movie.year}
Rated: ${movie.rated}
Released: ${movie.released}
Runtime: ${movie.runtime}
Genre: ${movie.genre}
Director: ${movie.director}
Writer: ${movie.writer}
Actors: ${movie.actors}
\nPlot: ${movie.plot}
""".trimIndent()
}

fun parseMovie(block: String): Movie? {
    val map = mutableMapOf<String, String>()
    val regex = Regex(""""?(\w+)"?\s*:\s*"?([^\" ,]+)"?,?""")
    for (line in block.lines()) {
        val match = regex.find(line)
        if (match != null) {
            val (key, value) = match.destructured
            map[key] = value.trim()
        }
    }
    if (map["Title"] == null) return null
    return Movie(
        title = map["Title"] ?: "",
        year = map["Year"],
        rated = map["Rated"],
        released = map["Released"],
        runtime = map["Runtime"],
        genre = map["Genre"],
        director = map["Director"],
        writer = map["Writer"],
        actors = map["Actors"],
        plot = map["Plot"]
    )
}
