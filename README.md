# 📱 Timemanager - Android Application

## 📖 Overview
The **Timemanager Android Application** is the mobile client for my [Timemanager Server](https://github.com/buruadam/ktor-timemanager-server), developed as part of my Thesis Project. It is a modern Android app designed to help users manage their schedules and tasks, syncing data seamlessly with the server.

## 📷 Screenshots

<p align="center">
  <img height="400" alt="home_screen" src="https://github.com/user-attachments/assets/1495fb82-3722-4704-95cf-fd0bdb24b3c9" />
  <img height="400" alt="task_screen" src="https://github.com/user-attachments/assets/c17a4ca5-97a3-49d4-9c5f-d9396098b194" />
  <img height="400" alt="addtask_screen" src="https://github.com/user-attachments/assets/d950f237-770f-4428-9267-74c9341c4acc" />
  <img height="400" alt="pomodoro_screen" src="https://github.com/user-attachments/assets/dd54126c-4583-4300-91f9-8b8d31d2953d" />
</p>

## 🛠️ Technologies
*   **Language:** Kotlin
*   **UI Framework:** Jetpack Compose & Material 3
*   **Networking:** Ktor Client (CIO engine)
*   **Dependency Injection:** Koin
*   **Architecture:** MVVM (Model-View-ViewModel)

## ✨ Key Features
- **Task Management:** Full CRUD operations for daily tasks.
- **Secure Auth:** JWT-based authentication.
- **Backend Sync:** Real-time communication with the Ktor REST API.
- **Modern Navigation:** Smooth transitions using Compose Navigation.

## 🚀 Getting Started

### 📦 Prerequisites
*   JDK 11 or higher
*   [Git](https://git-scm.com)
*   [Android Studio](https://developer.android.com/studio) (Recommended IDE)

### ⚙️ Installation & Running

1.  **Start the API:**
    - Ensure that the [Timemanager Server](https://github.com/buruadam/ktor-timemanager-server) is up and running.

2.  **Clone the repository:**
    ```
    git clone https://github.com/buruadam/Timemanager.git
    ```
    
3.  Open the project in **Android Studio** and wait for the **Gradle Sync** to complete.

4.  **Network Configuration:**

    - The app is pre-configured to work with the Android Emulator (`http://10.0.2.2:8081`) by default. If you are using an emulator, no further configuration is needed.

    - **(Optional)** If you want to run the app on a physical device via your local network:
      - Open the `local.properties` file in the project root.
      - Add the following line with your workstation's local IP address:
        ```properties
        api.url=http://<your-local-ip-address>:8081
        ```

5.  **Launch:**
    - Select your device or emulator from the toolbar.
    - Click the **Run** button (green play icon).

**Android Studio** will automatically build, install, and launch the application for you.

## 📜 License
This project is licensed under the **MIT License**. See the [LICENSE](LICENSE) file for details.
