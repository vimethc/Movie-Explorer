package com.example.movieexplorer

class MovieRepository(private val movieDao: MovieDao) {
    // Insert a single movie into the database
    suspend fun insertMovie(movie: Movie) = movieDao.insertMovie(movie)
    // Insert multiple movies into the database
    suspend fun insertMovies(movies: List<Movie>) = movieDao.insertMovies(movies)
    // Retrieve all movies from the database
    suspend fun getAllMovies() = movieDao.getAllMovies()
    // Search for movies by title (substring match)
    suspend fun searchMoviesByTitle(title: String) = movieDao.searchMoviesByTitle(title)
    // Search for movies by actor (substring match)
    suspend fun searchMoviesByActor(actor: String) = movieDao.searchMoviesByActor(actor)
} 