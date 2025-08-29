# TUJ Office Hours App (Android)

An Android application built with Android Studio and Firebase to streamline the scheduling and management of office hours for students and faculty at Temple University, Japan Campus (TUJ).

[![Build Status](https://img.shields.io/badge/build-passing-brightgreen)](https://github.com/SushiBytesKB/TUJOfficeHoursApp)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Platform](https://img.shields.io/badge/Platform-Android-3DDC84.svg?style=flat&logo=android)](https://www.android.com/)

## Table of Contents

- [About The Project](#about-the-project)
- [Key Features](#key-features)
- [Tech Stack & Architecture](#tech-stack--architecture)
- [Getting Started](#getting-started)
  - [Prerequisites](#prerequisites)
  - [Firebase Setup](#firebase-setup)
  - [Installation & Build](#installation--build)
- [Project Structure](#project-structure)
- [Contributing](#contributing)
- [Contributors](#contributors)
- [License](#license)
- [Contact](#contact)

## About The Project

Coordinating office hours between university faculty and students can often be a cumbersome process. The **TUJ Office Hours App** was created as a native Android solution to solve this problem for the Temple University, Japan Campus community.

This application provides a centralized, mobile-first platform where:
* **Faculty** can define their availability and manage their appointment calendar directly from their phone.
* **Students** can easily view professors' open slots and book appointments on the go.

The goal is to provide a fast, responsive, and convenient mobile experience that saves time and reduces administrative overhead.

## Key Features

* 👤 **Role-Based Authentication:** Secure login/signup for both Students and Faculty using Firebase Authentication.
* 👨‍🏫 **Professor Dashboard:**
    * Set and update weekly office hour availability.
    * View all booked appointments in a clean, organized list.
    * Manage and cancel appointments.
* 🎓 **Student Dashboard:**
    * Browse a list of professors and their available office hours.
    * Book an appointment slot that fits their schedule.
    * View and cancel their upcoming appointments.
* ⚡ **Real-time Updates:** Data is synced in real-time using Cloud Firestore, so changes are reflected instantly.
* 🔔 **Push Notifications:** (Optional Feature) Using Firebase Cloud Messaging to send confirmations and reminders.

## Tech Stack & Architecture

This project is a native Android application developed using modern tools and practices.

| Component | Technology / Library |
| :--- | :--- |
| **IDE** | ![Android Studio](https://img.shields.io/badge/Android%20Studio-3DDC84.svg?style=for-the-badge&logo=android-studio&logoColor=white) |
| **Language** | ![Kotlin](https://img.shields.io/badge/Kotlin-7F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white) |
| **Backend** | ![Firebase](https://img.shields.io/badge/Firebase-FFCA28.svg?style=for-the-badge&logo=firebase&logoColor=black) |
| **UI** | Jetpack Compose / XML Layouts |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Firebase Services**| Authentication, Cloud Firestore, Cloud Storage |
| **Dependencies** | ViewModel, LiveData/Flow, Navigation Component, Coroutines |


## Getting Started

To get a local copy up and running, follow these simple steps.

### Prerequisites

Make sure you have the latest stable version of **Android Studio** installed.
* [Download Android Studio](https://developer.android.com/studio)
* Ensure you have the Android SDK and JDK properly configured within Android Studio.

### Firebase Setup

This project requires a Firebase backend. You will need to create your own Firebase project to run it.

1.  Go to the [Firebase Console](https://console.firebase.google.com/).
2.  Click **"Add project"** and follow the steps to create a new project.
3.  Inside your project, add a new **Android App**. Use the package name found in your `app/build.gradle.kts` (or `.gradle`) file (e.g., `com.sushibyteskb.tujofficehoursapp`).
4.  Download the generated **`google-services.json`** file. **This file is crucial.**
5.  In the Firebase Console, navigate to the **Authentication** section and enable the **Email/Password** sign-in method.
6.  Navigate to the **Cloud Firestore** section and create a database. Start in **test mode** for initial development (but remember to secure your rules for production!).

### Installation & Build

1.  **Clone the repository:**
    ```sh
    git clone [https://github.com/SushiBytesKB/TUJOfficeHoursApp.git](https://github.com/SushiBytesKB/TUJOfficeHoursApp.git)
    ```

2.  **Open the project in Android Studio:**
    * Open Android Studio.
    * Select "Open" and navigate to the cloned repository folder.

3.  **Add your Firebase config file:**
    * Locate the `google-services.json` file you downloaded earlier.
    * Switch to the **Project** view in the Android Studio project explorer (top-left dropdown).
    * Place the `google-services.json` file inside the `app/` directory.

4.  **Sync and Build:**
    * Android Studio should prompt you to sync the project with the Gradle files. If not, click the "Sync Project with Gradle Files" button (elephant icon).
    * Once the sync is complete, build the project (Build > Make Project).
    * Run the app on an Android emulator or a physical device.

## Project Structure

The project follows a standard Android application structure.

## Contributors
- Sushant Bharadwaj Kagolanu - [https://github.com/SushiBytesKB](https://github.com/SushiBytesKB)
- Bettina Marksteiner - [https://github.com/iggyw1g](https://github.com/Betty200121)
- Riju Pant - [https://github.com/20wangaz2](https://github.com/Rp0115)

## Contributing

Contributions are welcome! If you have suggestions for improving the app, please feel free to fork the repo and create a pull request.

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

## License

Distributed under the MIT License. See `LICENSE` file for more information.

## Contact

SushiBytesKB - [GitHub Profile](https://github.com/SushiBytesKB)

Project Link: [https://github.com/SushiBytesKB/TUJOfficeHoursApp](https://github.com/SushiBytesKB/TUJOfficeHoursApp)



