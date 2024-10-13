💸 Finance Tracker Application
Track your daily income and expenses effortlessly!

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🎯 Purpose
The Finance Tracker App is designed to make managing your daily finances easier, safer, and more intuitive. 
This Android app allows users to track income and expenses through manual entries or automatically from SMS notifications—without sharing any sensitive information. 
With a clean UI and Firebase integration, it brings all your transactions to your fingertips.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
📱 User Journey
Permissions & Registration
Upon first launch, the app requests Notification and SMS permissions. After that, users register with their email and password. 
A verification link is sent to their email, and once verified, they can log in and begin using the app.

User Login & Profile Setup
On the first login, users are asked for a few basic profile details. Subsequent logins will take users directly to the app’s Home Page.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🌟 Key Features
Four Core Fragments
The app features Home, Income, Expense, and Profile fragments, each with distinct roles:

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🏠 Home Fragment
A floating action button allows users to add income or expense entries manually for cash transactions.
Track overall income, expenses, and balance at a glance, displayed with card views.
Add transactions with details like amount, transaction type (Cash, Bank, or Online), note, and date using the floating action buttons.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
💰 Income & Expense Fragments
View your total income and expenses at the top.
Filter transactions by date and type (All, Bank, Online, Cash).
Data fetched from Firebase Realtime Database is displayed in a clean RecyclerView. Filter results update the total amount in real time.
Users can update and delete transactions by clicking the corresponding icons in the recycler view.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
👤 Profile Fragment
Manage profile details with features like Edit Profile, Change Password, and Dark Mode (Night Mode).
User settings include options like enabling/disabling notifications and the ability to logout.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🤖 Automated Transaction Detection
The app automatically detects and extracts data from SMS notifications using custom regex logic for banks. The app can extract transaction details like:

Amount
Date (stored in dd-MM-yy format)
Transaction Type (Bank, Online, UPI)
No sensitive information (like Aadhaar, PAN, etc.) is collected or required.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🔔 User Notifications
Users receive three reminders daily to add expenses at convenient times:

1:30 PM
2:30 PM
9:30 PM
These reminders help users update their financial records during breaks or before the end of the day.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

🔐 Security & Privacy
The core of this app is user privacy. We do not collect any sensitive information like bank account numbers, Aadhaar, or PAN details. 
Your transactions are stored securely, and you remain in complete control of your data.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🛠️ Tech Stack
Firebase Realtime Database: For storing all income and expense data.
Firebase Firestore: For storing user profile details.
Firebase Authentication: Handles secure user login and registration with email and password.
SMS Data Extraction: Automatically fetches transactions from bank SMS notifications using custom regex logic.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🌐 Main Functionalities

🔑 Login & Registration
User registration with email and password.
Email verification process.
Forgot Password feature available on the login page.

📝 Manual & Automated Transaction Logging
Transactions can be recorded manually or fetched automatically from SMS notifications using regex patterns for various banks.
Users can choose to add transactions manually through the home screen or let the app detect SMS transactions.

🧑‍💻 User Profile Management
Update your profile, toggle between dark mode and light mode, change passwords, manage notifications, or delete your account—all within the app.

⏰ Notifications & Reminders
Friendly reminders sent at specific times throughout the day encourage users to keep their expense logs updated without intruding on their day-to-day activities.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🔍 Objectives
Our goal is to provide a simple, secure platform for users to track their income and expenses in one place without sharing sensitive information with third-party services. 
In today’s digital age, privacy is paramount, and the Finance Tracker app is designed with that in mind, allowing users to manage their finances worry-free!

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------




