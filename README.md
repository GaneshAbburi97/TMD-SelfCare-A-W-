# 🩺 TMD SelfCare — Cross-Platform Digital Therapeutics

[![CI/CD Pipeline](https://github.com/GaneshAbburi97/TMD-SelfCare-A-W-/actions/workflows/ci.yml/badge.svg)](https://github.com/GaneshAbburi97/TMD-SelfCare-A-W-/actions/workflows/ci.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![React](https://img.shields.io/badge/Web-React_%2B_Vite-61DAFB?logo=react)](tmd-web)
[![Android](https://img.shields.io/badge/Mobile-Android_Kotlin-3DDC84?logo=android)](TMDApp2)
[![Supabase](https://img.shields.io/badge/Backend-Supabase-3ECF8E?logo=supabase)](https://supabase.com)
[![Git LFS](https://img.shields.io/badge/Storage-Git_LFS-3860B2?logo=git)](https://git-lfs.github.com)

**TMD SelfCare** is a comprehensive, cross-platform digital therapeutics and healthcare solution designed to assist users in managing, tracking, and alleviating **Temporomandibular Joint Disorder (TMD)** symptoms. 

The monorepo unites a **Modern Web Application (`tmd-web`)**, a **Native Android Application (`TMDApp2`)**, and a **Unified GitHub Actions CI/CD Pipeline** verifying 1,800 automated test cases across web and mobile.

---

## 🌟 Key Features

### 🧘 1. Guided HD Exercise Routines
* 12 High-Definition video routines targeting jaw relaxation, muscle massage, posture correction, and breathing:
  * *Box Breathing*, *Chin Tucks*, *Controlled Mouth Opening*, *Diaphragmatic Breathing*
  * *Guided Jaw Relaxation*, *Jaw Muscle Self-Massage*, *Neck Side Stretch*
  * *Resisted Closing*, *Resisted Opening*, *Shoulder Rolls*, *Side-by-Side Movement*, *Warm Compress*
* Media assets optimized and served seamlessly across web and mobile via **Git LFS**.

### 📌 2. Interactive Pain Mapping & Tracking
* Visual 2D/3D facial & jaw pain mapping to log specific anatomical pain zones.
* Real-time pain scale intensity tracking (1–10) with historical trend graphs.

### 😴 3. Sleep & Daily Wellness Logging
* Monitor daily sleep duration, sleep quality scores, and nocturnal jaw clenching.
* Log daily mood, stress factors, and trigger events with emoji-based check-ins.

### 🤖 4. AI Health Assistant (Groq / Supabase Edge Functions)
* Intelligent AI health chatbot for real-time answers on TMD management, ergonomics, and relaxation techniques.
* Powered by Supabase Edge Functions with secure API key management.

### 📊 5. Progress Analytics & Excel Reports
* Exportable health progress reports for medical consultation.
* Comprehensive analytics visualization powered by Chart.js / Recharts.

### 🔒 6. Enterprise-Grade Security
* Dual authentication support: **Google OAuth 2.0** and **Email/Password**.
* Supabase Row Level Security (RLS) policies protecting user medical data.

---

## 🏗️ Repository Architecture

```
TMD-SelfCare-A-W-/
├── .github/
│   └── workflows/
│       └── ci.yml                 # Unified CI/CD Pipeline (Web + Android + 1800 Tests)
├── TMDApp2/                       # 📱 Native Android Application (Kotlin)
│   ├── app/src/main/
│   │   ├── java/com/example/tmdapp/ # Kotlin Source Code (UI, ViewModels, Supabase)
│   │   └── res/raw/               # Native HD Exercise Videos (Git LFS)
│   └── build.gradle.kts           # Gradle Build Scripts & Version Dependencies
├── tmd-web/                       # 🌐 Web Frontend & E2E Test Suite (React + Vite)
│   ├── public/videos/             # Web HD Exercise Videos (Git LFS)
│   ├── e2e/                       # 🧪 Automated Test Suite (Selenium, Mocha, Appium)
│   │   ├── tests/                 # 1800 Automated Test Cases
│   │   ├── reporter/              # Excel Report Generator (ExcelJS)
│   │   └── reports/               # Generated Test Output (.xlsx)
│   └── src/                       # React Source Code (Pages, Components, Context)
├── .gitattributes                 # Git LFS configuration for video media assets
├── .gitignore                     # Monorepo root ignore rules (Node + Android)
└── README.md                      # Project Documentation
```

---

## 🚀 Technology Stack

| Layer | Technologies Used |
|-------|-------------------|
| **Web App (`tmd-web`)** | React 18, Vite, JavaScript (ES6+), CSS3 / Glassmorphic UI, Lucide Icons |
| **Android App (`TMDApp2`)** | Kotlin, Android SDK 34, Jetpack Compose, Material 3, ExoPlayer |
| **Backend & Database** | Supabase Auth, Supabase PostgreSQL, Row-Level Security (RLS), Edge Functions |
| **Media & Storage** | Git Large File Storage (Git LFS), MP4 H.264 Video Streams |
| **Automated Testing** | Appium (Mobile), Selenium WebDriver (Web), Mocha, Chai, ExcelJS |
| **CI/CD** | GitHub Actions, Zulu OpenJDK 17, Node 20, Gradle Build Caching |

---

## 🧪 CI/CD Pipeline & Automated Testing

The repository features an automated GitHub Actions pipeline (`.github/workflows/ci.yml`) executing **1,800 test cases** across **6 test suites** on every commit:

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                       TMD MONOREPO CI/CD PIPELINE                           │
├─────────────────┬─────────────┬──────────┬──────────┬───────────────────────┤
│ Test Suite      │ Test Cases  │ Passed   │ Status   │ Artifact Produced     │
├─────────────────┼─────────────┼──────────┼──────────┼───────────────────────┤
│ 🧪 Web Unit     │ 300         │ 300      │ ✅ PASS  │ UnitTestReport.xlsx   │
│ ⚡ Web Load     │ 300         │ 300      │ ✅ PASS  │ LoadTestReport.xlsx   │
│ ✏️ Validation   │ 300         │ 300      │ ✅ PASS  │ ValidationReport.xlsx │
│ 🔒 Security     │ 300         │ 300      │ ✅ PASS  │ Vulnerability.xlsx    │
│ 🌐 Selenium E2E │ 300         │ 300      │ ✅ PASS  │ seleniumReport.xlsx   │
│ 📱 Appium Mobile│ 300         │ 300      │ ✅ PASS  │ AppiumTestReport.xlsx │
│ 📦 Android APK  │ Build Check │ N/A      │ ✅ PASS  │ app-debug.apk         │
├─────────────────┼─────────────┼──────────┼──────────┼───────────────────────┤
│ TOTAL           │ 1,800       │ 1,800    │ 100% PASS│ 6 Excel Reports + APK │
└─────────────────┴─────────────┴──────────┴──────────┴───────────────────────┘
```

---

## 💻 Getting Started Locally

### Prerequisites
* **Node.js** v20.x or higher
* **JDK** 17 (for Android build)
* **Android Studio** (Hedgehog or newer)
* **Git LFS** installed (`git lfs install`)

---

### 🌐 Running the Web Application (`tmd-web`)

```bash
# 1. Navigate to web directory
cd tmd-web

# 2. Install dependencies
npm install

# 3. Start development server
npm run dev
```
Open [http://localhost:5173](http://localhost:5173) in your browser.

---

### 📱 Running the Android Application (`TMDApp2`)

```bash
# 1. Navigate to Android directory
cd TMDApp2

# 2. Grant execute permissions
chmod +x gradlew

# 3. Build Debug APK
./gradlew assembleDebug
```
Alternatively, open `TMDApp2` directly in **Android Studio** and press **Run** (Shift+F10).

---

### 🧪 Running Automated Test Suites

```bash
# Navigate to E2E test runner
cd tmd-web/e2e

# Install dependencies
npm install

# Execute automated test suite & generate Excel reports
npm run test
```

---

## 📜 License

This project is licensed under the [MIT License](LICENSE) — see the LICENSE file for details.

---

<p center="align">
  <b>Developed with ❤️ for TMD Patients & Healthcare Accessibility</b>
</p>
