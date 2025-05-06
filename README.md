# 🎬 MovieExplorer App

This is a Kotlin-based Android application developed as part of the **5COSC023C Mobile Application Development** coursework at the University of Westminster.

The app is designed to help users search, view, and manage movie information using the **OMDb API** and a local **Room database**, all built with **Jetpack Compose** — no XML views or third-party libraries are used.

## ✅ Features

- 📥 **Add Movies to DB**  
  Hardcoded list of movies is saved into a local SQLite database using Room.

- 🔍 **Search for Movies (OMDb API)**  
  Allows users to fetch detailed movie info from the OMDb web service by title.

- 💾 **Save Movie to DB**  
  Saves fetched movie data from the OMDb API into the local Room database.

- 🧑‍🎤 **Search for Actors**  
  Case-insensitive, partial match search across movies stored in the local DB.

- 🌐 **Search Titles Online (OMDb API)**  
  Displays a list of all movies from the OMDb API containing a user-specified substring in the title.

- 🔁 **Orientation Support**  
  Maintains state across screen rotation using ViewModel and Compose state handling.

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **Room (SQLite)**
- **OMDb API** - https://www.omdbapi.com
- **Android Jetpack Libraries** (Navigation, ViewModel)

## 🔐 API Key

To use the OMDb API, you must generate a free API key from [https://www.omdbapi.com/apikey.aspx](https://www.omdbapi.com/apikey.aspx) and add it to the code where appropriate.



<img width="461" alt="Screenshot 2025-05-06 at 3 00 47 PM" src="https://github.com/user-attachments/assets/127096b2-0b11-4b36-9f4d-5ed1323d86a3" />

