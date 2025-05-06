# 🎬 MovieExplorer App

This is a Kotlin-based Android application developed as part of the Mobile Application.

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



![Screenshot_2025-05-06_at_3 00 47_PM-removebg-preview](https://github.com/user-attachments/assets/8686b47f-e794-4887-b10e-bfcadf65b2a2)


