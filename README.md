# Smart Pantry Manager

An Android application built entirely in Java that helps users reduce 
food waste by suggesting recipes based strictly on the leftover ingredients in their pantry.

## Database Option
I chose **SQLite**. It is a local, offline database that perfectly fits the 
requirement for persistent on-device storage without needing external API calls 
or internet connectivity. It also aligns with the MAD 700 curriculum requirements 
for local data persistence.

## Setup & Run Instructions
1. Clone this repository to your local machine.
2. Open the project in Android Studio.
3. Allow Gradle to sync.
4. Run the app on an Android Emulator (API 24 or higher) or a physical device.
5. Use the "+ Add New Ingredient" button to populate your pantry, then click "View Suggested Recipes"
   to see the strict-matching engine in action!
