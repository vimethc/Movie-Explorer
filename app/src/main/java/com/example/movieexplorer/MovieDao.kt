package com.example.movieexplorer

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

// DAO interface for accessing and modifying movies in the database
@Dao
interface MovieDao {
    // Insert a single movie, replacing on conflict (e.g., same id)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: Movie)

    // Insert multiple movies at once, replacing on conflict
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovies(movies: List<Movie>)

    // Get all movies from the database
    @Query("SELECT * FROM movies")
    suspend fun getAllMovies(): List<Movie>

    // Search for movies by title (case-insensitive, substring match)
    @Query("SELECT * FROM movies WHERE LOWER(title) LIKE '%' || LOWER(:title) || '%'")
    suspend fun searchMoviesByTitle(title: String): List<Movie>

    // Search for movies by actor (case-insensitive, substring match)
    @Query("SELECT * FROM movies WHERE LOWER(actors) LIKE '%' || LOWER(:actor) || '%'")
    suspend fun searchMoviesByActor(actor: String): List<Movie>

    // Get a movie by exact title and year (used to prevent duplicates)
    @Query("SELECT * FROM movies WHERE title = :title AND year = :year LIMIT 1")
    suspend fun getMovieByTitleAndYear(title: String, year: String?): Movie?
} 