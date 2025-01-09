# HistARy Explorer 🌍📜

## About  
**HistARy Explorer** is an interactive app designed to make exploring and learning about historical places a fun, immersive, and tech-driven experience. With features like navigation, geofencing, and augmented reality (AR), this app provides a unique way to connect with history.

---

## 📸 Demo 



---

## ✨ Features  

### Key Features:  
- **🗺️ Ola Maps Integration:**  
  - Interactive maps for exploring locations.  
  - Turn-by-turn navigation to historical places.  

- **📍 Manual Geofence:**  
  - Detects historical places within a 1km radius without third-party geofencing tools.  
  - Sends notifications when you're near a historical site.  

- **🔍 3D and AR Views:**  
  - View 3D models of historical places in detail.  
  - Experience these places in augmented reality using Google AR services.  

---

### App Flow:  
- **🚀 Splash Screen:** Initial loading screen.  
- **🔐 Login/Signup:**  
  - Users can sign up or log in using Firebase Authentication.  
  - Signup includes entering a name and email.  
- **🏠 Home Screen:**  
  - Displays "Liked Places" and "Visited Places" in separate recycler views.  
  - Shows empty views if no places are added.  
  - Bottom navigation for easy access to key features.  
- **👤 Profile Section:**  
  - Displays user details (name and email).  
  - Logout button for secure sign-out.  
- **🗺️ Maps Section:**  
  - User location detection with a floating action button.  
  - Markers represent historical places fetched from Firebase Realtime Database.  
  - Unique marker icons for each historical place.  

---

### Marker Details:  
- **📌 Marker Interaction:**  
  - Clicking on a marker opens a draggable bottom sheet:  
    - Place name, images (viewpager), history, and interesting facts.  
    - **Action buttons:**  
      - **❤️ Like:** Adds the place to "Liked Places" with a konfetti effect.  
      - **✅ Visited:** Adds to "Visited Places" with a larger konfetti effect.  
      - **🎲 Trivia:** Displays an MCQ-style trivia game with correct and incorrect answers highlighted.  
      - **🕶️ 3D View:** Opens Google AR services for 3D and AR experiences.  

---

### Additional Features:  
- Firebase UID to remember logged-in users.  
- Beautiful and functional **neo-brutalism UI**.  

---

## 🛠️ Technologies Used  

- **Android Studio:** For app development.  
- **Java and XML:** App logic and UI design.  
- **Firebase:**  
  - Authentication: Secure user login and signup.  
  - Realtime Database: Storing and fetching historical data, markers, and user interactions.  
- **Ola Maps API:** Navigation and geolocation services.  
- **Google AR Services:** For 3D and AR views.  

---

## 🚀 Installation and Setup  

- Clone this repository:  
   ```bash
   git clone https://github.com/vaibhavsr313/HistARy-Explorer
   
- Open the project in Android Studio.
- Add your Firebase configuration file (google-services.json).
- Set up API keys for Ola Maps and Google AR services in local.properties.
- Build and run the app on your device or emulator.

---

## 🤝 Contributing

- Contributions are welcome! Feel free to open an issue or submit a pull request.
