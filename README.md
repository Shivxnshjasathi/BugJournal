# 🐛 Bug Journal – Your Personal Software Bug Tracker

**Bug Journal** is a smart and simple app built for developers to log, track, and learn from the bugs they encounter while developing software. It’s more than a bug tracker—it's a learning companion that helps improve your debugging and documentation practices.

---

## 📱 What is Bug Journal?

Bug Journal is a mobile app (built using Flutter and Jetpack Compose for Android) that allows developers to:
- Log detailed bug reports
- Document how bugs were discovered and resolved
- Analyze patterns in past bugs
- Get smart AI-powered suggestions for tagging and fixing bugs

Whether you're a solo developer or working in a team, Bug Journal ensures no bug experience goes to waste.

---

## 🚀 Features

- ✍️ **Bug Logging**  
  Add details like title, app name, severity, environment, summary, description, steps to reproduce, resolution, tags, and timestamp.

- 🔍 **Search & Filter**  
  Quickly find bugs by tags, app name, severity, or other filters.

- 🔗 **Firebase Integration**  
  Sync bug reports with Firebase Firestore and Firebase Auth for secure login and real-time updates.

- 🧠 **AI-Powered Tag Suggestions**  
  Gemini API integration to suggest tags or categorize bugs automatically using AI.

- 🌐 **Offline Support**  
  Access and update bugs even when you're offline, with local storage fallback.

- 📊 **Insights (coming soon)**  
  Track frequent bug types, recurring issues, and resolution timelines.

---

## 🛠 Tech Stack

- **Frontend:** Flutter (iOS + Android), Jetpack Compose (Android-specific components)
- **Backend:** Firebase (Auth + Firestore)
- **AI Integration:** Gemini API for intelligent suggestions
- **Storage:** Firestore Cloud DB + Local persistence

---

## 📸 Try Apk


---

## 📦 Folder Structure

```

lib/
┣ models/          # Bug data models
┣ screens/         # UI screens (Home, BugDetail, AddBug, etc.)
┣ services/        # Firebase & AI integration
┣ widgets/         # Reusable UI components
┗ main.dart        # Entry point

````

---

## 🧑‍💻 How to Run

1. Clone the repo:
   ```bash
   git clone https://github.com/yourusername/bug-journal.git
   cd bug-journal


2. Install dependencies:

   ```bash
   flutter pub get
   ```

3. Configure Firebase:

   * Add your `google-services.json` (for Android) or `GoogleService-Info.plist` (for iOS)
   * Set up Firestore rules and enable Authentication

4. Run the app:

   ```bash
   flutter run
   ```

---

## 🔐 API Key Setup

Gemini API Key is fetched from Firebase Firestore under the path:

```
/gemini_api_key/{documentId}/gemini_api_key
```

Make sure you add this in your Firestore database before running the app.

---

## 🤝 Contributing

Want to help improve Bug Journal? PRs and suggestions are welcome! Please fork the repo and open a pull request with your changes.

---

## 📄 License

This project is licensed under the MIT License. See `LICENSE` for more information.

---

## 🙋‍♂️ Author

Developed by [Shivansh Jasathi](https://github.com/shivanshjasathi)
Let’s connect on [contact me](https://www.shivanshs.website) 

---
 
