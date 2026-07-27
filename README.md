# 🛵 TripFuel v1.1 – Flagship GPS Fuel Tracking & Ride Profit Calculator

<p align="center">
  <img src="./tripfuel_logo.png" alt="TripFuel App Logo" width="180" />
</p>

**TripFuel** is a flagship Android application designed specifically for **Rapido, Porter, Uber Moto, Swiggy, Zomato, and delivery riders**. Built with **Android Jetpack Compose**, **Material 3 Expressive**, **Liquid Glass Aesthetics**, and 100% **Offline Local Phone Storage**, TripFuel gives riders complete financial clarity on every order delivery.

---

## 📲 Direct APK Download (v1.1)

📥 **[Click Here to Download TripFuel-v1.1.apk](https://github.com/dipu-dev-labs/TripFuel/releases/download/v1.1/TripFuel-v1.1.apk)**

---

## 📸 App Screenshots & Real Demo

| **Home Dashboard** | **Live GPS Tracking** |
|:---:|:---:|
| <img src="./demo%20screen%20shot/6285335792322287027.jpg" width="260" alt="TripFuel Home Screen" /> | <img src="./demo%20screen%20shot/6285335792322287028.jpg" width="260" alt="TripFuel Live Tracking" /> |
| **Ride Summary & Profits** | **Analytics & Settings** |
| <img src="./demo%20screen%20shot/6285335792322287029.jpg" width="260" alt="TripFuel Summary" /> | <img src="./demo%20screen%20shot/6285335792322287030.jpg" width="260" alt="TripFuel Analytics" /> |

---

## ✨ Key Features

- 🎨 **Liquid Glass Design Language:** Deep dark background (`#0B0F14`), Neon Electric Green (`#00E676`) & Teal Cyan (`#64FFDA`) visual tokens, frosted glass cards, gradient borders, and 60fps micro-animations.
- 📍 **High-Precision GPS Ride Tracking:** Tracks exact distance, ride duration, and live speed with continuous fuel cost calculations.
- 📲 **24/7 Background Service & Lock Screen Tracking:** Persistent status bar notification keeps tracking active even when switching apps (Swiggy/Zomato/Rapido) or with screen turned OFF.
- ⚡ **Real GPS vs. Demo Simulator Mode:** Seamlessly switch between Real GPS tracking and indoor Demo Simulation mode.
- 💰 **Instant Net Profit Calculator:** Quick-Add presets for Rapido, Porter, Uber Moto, Swiggy, Zomato, and Custom earnings auto-calculate Net Profit, Profit per KM, and Fuel Expense %.
- 📈 **Animated Canvas Analytics & Charts:** Weekly profit bar charts, monthly earnings line graphs, average fuel costs, and most profitable day insights.
- 🏍️ **Multi-Bike Garage Manager:** Manage multiple bikes (Mileage km/L, Fuel type, Nickname) and switch active vehicle instantly.
- ⛽ **Fuel Purchase Log:** Track petrol refills, litres, station names, and average fuel expenditure.
- 🛠️ **Bike Maintenance Reminders:** Service trackers for Oil Change, Air Filter, Brake Pads, Chain Lube, Tyres, Insurance, and PUC.
- 📋 **Reports & CSV Export:** Daily, Weekly, and Monthly income report summary generator with native share sheet export.
- 🔒 **100% Offline Local DB:** Uses phone storage (`SharedPreferences` + `Gson` JSON) for zero cloud latency and total data privacy.

---

## 🛠️ Technology Stack

* **Language:** Kotlin 2.2+
* **UI Framework:** Android Jetpack Compose + Material 3 Expressive
* **Architecture:** StateFlow, Coroutines, Service Architecture
* **Background Service:** Android Foreground Service with Sticky Notification
* **Local Storage:** Phone Local Storage (JSON / SharedPreferences)
* **Graphics:** Compose Canvas Custom Visual Engine
* **Build System:** Gradle 9.5 (Android Gradle Plugin 9.3)

---

## 🚀 How to Run Locally

1. Clone or open the repository in **Android Studio**:
   ```bash
   git clone https://github.com/dipu-dev-labs/TripFuel.git
   ```
2. Build the project using Gradle:
   ```bash
   ./gradlew assembleDebug
   ```
3. Run on an Android Device or Emulator (Android 7.0 / API 24+).

---

## 🔗 Official Repository Link

📌 **GitHub Repository:** [https://github.com/dipu-dev-labs/TripFuel](https://github.com/dipu-dev-labs/TripFuel)

---

## 📝 License

Distributed under the MIT License. See `LICENSE` for more information.
