💸 Finance Tracker Application
Track your daily income and expenses effortlessly!

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🎯 Purpose

The Finance Tracker App is designed to make managing your daily finances easier, safer, and more intuitive. 
This Android app allows users to track income and expenses through manual entries or automatically from SMS notifications—without sharing any sensitive information. 
With a clean UI, Firebase integration, toast messages for user feedback, and progress bars to indicate loading or processing tasks, 
it brings all your transactions to your fingertips.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
📱 User Journey

Permissions & Registration
Upon first launch, the app requests Notification and SMS permissions. After that, users register with their email and password. 
A verification link is sent to their email, and once verified, they can log in and begin using the app. 
Toast messages are shown at various stages to inform users of successful registration, email verification, or any issues.

User Login & Profile Setup
On the first login, users are asked for a few basic profile details. 
Progress bars are displayed during data loading or authentication processes, and toast messages confirm successful logins or provide feedback if issues occur. 
Subsequent logins will take users directly to the app’s Home Page.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🌟 Key Features

Four Core Fragments
The app features Home, Income, Expense, and Profile fragments, each with distinct roles:

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🏠 Home Fragment

A floating action button allows users to add income or expense entries manually for cash transactions.
Track overall income, expenses, and balance at a glance, displayed with card views.
Add transactions with details like amount, transaction type (Cash, Bank, or Online), note, and date using the floating action buttons. Toast messages appear after adding, updating, or deleting a transaction for clear feedback.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
💰 Income & Expense Fragments

View your total income and expenses at the top.
Filter transactions by date and type (All, Bank, Online, Cash).
Data fetched from Firebase Realtime Database is displayed in a clean RecyclerView. Filter results update the total amount in real-time, and toast messages confirm actions like filtering, updating, or deleting transactions. 
Progress bars ensure a smooth experience during data loading.
Users can update and delete transactions by clicking the corresponding icons in the recycler view, with toast messages confirming success or failure.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
👤 Profile Fragment

Manage profile details with features like Edit Profile, Change Password, and Dark Mode (Night Mode).
User settings include options like enabling/disabling notifications and the ability to logout, with toast messages confirming profile updates and settings changes.
A progress bar is used during any loading of user data.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
🤖 Automated Transaction Detection

The app automatically detects and extracts data from SMS notifications using custom regex logic for banks. 
The app can extract transaction details like:
  Amount
  Date (stored in dd-MM-yy format)
  Transaction Type (Bank, Online, UPI)
  
Progress bars appear when SMS data is being processed, and toast messages inform users when SMS extraction has been successful or if an error occurs.
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

Main Acitivity ( Login page ) Image 

![1000099084](https://github.com/user-attachments/assets/f45e7339-7b23-469e-8b1a-c170b0d0d55a)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Registion page Image

![WhatsApp Image 2024-10-13 at 14 04 52_8fb43ad7](https://github.com/user-attachments/assets/17e4c184-db83-4e52-b495-77a2b9f83e81)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

First time login User details page Image

![WhatsApp Image 2024-10-13 at 14 07 23_07f57c68](https://github.com/user-attachments/assets/f5960153-05ed-487d-883a-68a4cb4cd360)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
Home Fragment (Home page) Image (Day Mode Image)

![WhatsApp Image 2024-10-13 at 14 10 35_3aade1ab](https://github.com/user-attachments/assets/92b688ad-4d8e-460c-904c-401d9c2ff953)

Home Fragment Floatting button to add income and expense Dailog box Image

![WhatsApp Image 2024-10-13 at 14 12 13_a4ff81b0](https://github.com/user-attachments/assets/d3f5dd02-5477-48a1-b66b-75d6020212a6)

![WhatsApp Image 2024-10-13 at 14 12 14_38fdcea8](https://github.com/user-attachments/assets/7c81928f-6393-4e85-bf7c-fc4fb0c435b7)


Home Fragment (Home page) Image (Night Mode Image)

![WhatsApp Image 2024-10-13 at 14 31 39_24b93e49](https://github.com/user-attachments/assets/54126c8a-6086-4c40-8aac-16bd0109255f)

Home Fragment Floatting button to add income and expense Dailog box Image

![WhatsApp Image 2024-10-13 at 14 31 40_6f78f173](https://github.com/user-attachments/assets/61baf454-af63-4bb6-bda8-aedb2bb232d1)

![WhatsApp Image 2024-10-13 at 14 31 40_d2306cb3](https://github.com/user-attachments/assets/e95eb2a1-b9cf-43a6-8037-6dcc6814cea6)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Income Fragment Image (Day Mode Image)

Normal view Image 

![WhatsApp Image 2024-10-13 at 14 16 35_0a652178](https://github.com/user-attachments/assets/74219cc7-9042-449d-8062-0c0740a20c6c)

Date filtered Transaction Image

![WhatsApp Image 2024-10-13 at 14 16 36_102d01c2](https://github.com/user-attachments/assets/cbbb682a-6904-40bf-9c74-1bf4410da9c7)

Transaction Type filtered Image 

![WhatsApp Image 2024-10-13 at 14 16 37_ae403a36](https://github.com/user-attachments/assets/e7661378-0ed1-4088-9d23-d9ece11bb9c6)

Update Dailog Image

![WhatsApp Image 2024-10-13 at 14 20 41_983bf1d4](https://github.com/user-attachments/assets/e50dd6a9-ab9e-47d8-bf91-90760f760b7f)

Delete Dailog Image

![WhatsApp Image 2024-10-13 at 14 20 41_ef7a2edd](https://github.com/user-attachments/assets/fcb3bc79-d7aa-4370-acd0-9d8f3378b53f)

Income Fragment Image (Night Mode Image)

Normal view Image

![WhatsApp Image 2024-10-13 at 14 33 52_f9783fe8](https://github.com/user-attachments/assets/4b2b0e8c-dab5-4d83-bef1-a29f9db0b1bb)

Date filtered Transaction Image

![WhatsApp Image 2024-10-13 at 14 33 52_14e5a6c3](https://github.com/user-attachments/assets/3e0a6767-84c8-40e3-b737-aafdde4012b8)

Transaction Type filtered Image 

![WhatsApp Image 2024-10-13 at 14 33 51_978049d1](https://github.com/user-attachments/assets/676c7b27-8319-47e4-b653-f0f95d897a51)

Update Dailog Image

![WhatsApp Image 2024-10-13 at 14 33 51_d8a75a3a](https://github.com/user-attachments/assets/039e3cd1-cd27-4142-8735-13917c15dc2e)

Delete Dailog Image

![WhatsApp Image 2024-10-13 at 14 33 50_51d05821](https://github.com/user-attachments/assets/747de9b3-fb99-48f7-a298-f72a13ac9b25)


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Expense Fragment Image (Day Mode Image)

Normal view Image 

![WhatsApp Image 2024-10-13 at 14 23 23_e4e4d55b](https://github.com/user-attachments/assets/8f414b4a-6d42-401e-921b-001929636d61)

Date filtered Transaction Image

![WhatsApp Image 2024-10-13 at 14 23 23_424d5d43](https://github.com/user-attachments/assets/5166ee11-7036-49c1-9596-28f152028553)

Transaction Type filtered Image 

![WhatsApp Image 2024-10-13 at 14 23 22_dcab9947](https://github.com/user-attachments/assets/f5fea4c1-ee1c-432a-b80e-cf76541c0c3b)

Update Dailog Image

![WhatsApp Image 2024-10-13 at 14 23 21_eaec1d41](https://github.com/user-attachments/assets/47598f7b-460c-43f3-9ffd-d2e5a9854d9a)

Delete Dailog Image

![WhatsApp Image 2024-10-13 at 14 23 21_1315f45f](https://github.com/user-attachments/assets/b06553f0-12c1-4ff8-9a15-c0ee4d2ad78d)

Expense Fragment Image (Night Mode Image)

Normal view Image 

![WhatsApp Image 2024-10-13 at 14 36 45_2fc7b184](https://github.com/user-attachments/assets/63ca2536-98ca-4deb-a50f-4452c2852a6b)

Date filtered Transaction Image

![WhatsApp Image 2024-10-13 at 14 36 46_d44a0520](https://github.com/user-attachments/assets/b9e00a78-6a23-4d4e-9234-e448dfe7eecc)

Transaction Type filtered Image 

![WhatsApp Image 2024-10-13 at 14 36 46_b5807230](https://github.com/user-attachments/assets/ff702ad2-7e5f-4bc2-b06f-b21b6a0c091d)

Update Dailog Image

![WhatsApp Image 2024-10-13 at 14 36 47_9a4d018b](https://github.com/user-attachments/assets/c774f8f6-a3d0-422b-b6d7-b7939a2f6b71)

Delete Dailog Image

![WhatsApp Image 2024-10-13 at 14 36 47_d1f8a1ab](https://github.com/user-attachments/assets/e2a6b21b-3092-4350-b46d-76dfdb4c34b8)


-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

Profile Fragment Image (Day Mode Image)

Normal View Image

![WhatsApp Image 2024-10-13 at 14 26 10_572456bc](https://github.com/user-attachments/assets/4717bdb6-906d-4958-b2b9-3e687e9f3cbd)

Click on user Name UserDetailsEditProfile Image

![WhatsApp Image 2024-10-13 at 14 26 11_22b1181a](https://github.com/user-attachments/assets/630028a1-4b71-4c93-9929-8ba280696301)

Edit Profile or uodate Profile Image

![WhatsApp Image 2024-10-13 at 14 26 11_c02bebe5](https://github.com/user-attachments/assets/86923c89-9d74-490c-89e7-5c56c45f3e19)

Change Password

![WhatsApp Image 2024-10-13 at 14 26 11_ba9fc9c1](https://github.com/user-attachments/assets/cf5320a7-ec0e-43b7-8d9f-eea1aa5da2e2)

Profile Fragment Image (Night Mode Image)

Normal View Image

![WhatsApp Image 2024-10-13 at 14 39 12_956c1dd6](https://github.com/user-attachments/assets/77a73aa0-393b-418f-863a-6365ac8356ce)

Click on user Name UserDetailsEditProfile Image

![WhatsApp Image 2024-10-13 at 14 39 12_1cc5fa0b](https://github.com/user-attachments/assets/ba4bebb4-3f35-4a30-a7f7-e2493fa65584)

Edit Profile or uodate Profile Image

![WhatsApp Image 2024-10-13 at 14 39 13_6e1fbe73](https://github.com/user-attachments/assets/0fb1a8f7-e41f-4c16-b906-99ff2929af66)

Change Password

![WhatsApp Image 2024-10-13 at 14 39 13_37fae66b](https://github.com/user-attachments/assets/13fcbdfd-6f87-4522-9b63-420d3e1d83ed)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


