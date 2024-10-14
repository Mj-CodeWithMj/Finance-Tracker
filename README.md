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

**Main Acitivity ( Login page ) Image** 

![1000099084](https://github.com/user-attachments/assets/15cfe702-49d0-4ff6-9fcd-10fbc6d7bd65)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Registion page Image**

![1000099086](https://github.com/user-attachments/assets/5bc4fc16-dbec-41e1-a7fa-dd5e04d998b3)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**First time login User details page Image**

![1000099088](https://github.com/user-attachments/assets/9e86924d-6b2f-4a4c-8111-ca18d0a0008c)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
**Home Fragment (Home page) Image**

![1000099091](https://github.com/user-attachments/assets/36c92245-d260-4176-8901-f6c858b733b8)    ![1000099129](https://github.com/user-attachments/assets/ca742c02-cb38-4a0f-8b2c-149e34fe2a0f)

**Home Fragment Floatting button to add income and expense Dailog box Image**

![1000099093](https://github.com/user-attachments/assets/3a2da97b-830c-45ea-98ca-893e4a45fc85)    ![1000099094](https://github.com/user-attachments/assets/ca37b095-6cb3-430a-87c5-dbef680cff4a)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Income Fragment Image**
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


**Normal view Image**

![1000099097](https://github.com/user-attachments/assets/afea1b50-bf53-4c6e-a884-724cf3e2f8e3)   ![1000099135](https://github.com/user-attachments/assets/39509764-bb36-4467-952b-082a67c14a05)


**Date filtered Transaction Image**

![1000099098](https://github.com/user-attachments/assets/72628268-d726-475d-acae-0cd5352294de)   ![1000099136](https://github.com/user-attachments/assets/e839b380-a8fb-4c06-9f43-b32b7c08685c)


**Transaction Type filtered Image**

![1000099100](https://github.com/user-attachments/assets/52a6c53a-e2e3-453a-be30-255f13b59db9)   ![1000099137](https://github.com/user-attachments/assets/18e81c59-b0a2-4a75-a0bb-be9c2311ad29)


**Update Dailog Image**

![1000099104](https://github.com/user-attachments/assets/5b0f6802-8190-43db-b8fd-c958f41f77b4)   ![1000099138](https://github.com/user-attachments/assets/14857bf0-85d7-49b1-a952-98f779bbd410)


**Delete Dailog Image**

![1000099105](https://github.com/user-attachments/assets/e6f83781-ee7d-4606-b6a6-7a576943e7b0)   ![1000099139](https://github.com/user-attachments/assets/5f226f23-f1f4-406f-9bdd-26c9a65c1652)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Expense Fragment Image**
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


**Normal view Image** 

![1000099110](https://github.com/user-attachments/assets/1748ff10-1381-448b-af15-4328b803f19b)   ![1000099145](https://github.com/user-attachments/assets/6f2b2c70-4052-4187-ac82-9b49a60984cc)


**Date filtered Transaction Image**

![1000099111](https://github.com/user-attachments/assets/c4b5773a-5293-48ea-9c71-d0d936f2852a)   ![1000099147](https://github.com/user-attachments/assets/7fbe56c9-d0dd-4c63-bcef-4237ffa2a21b)


**Transaction Type filtered Image** 

![1000099112](https://github.com/user-attachments/assets/810eff0b-479d-4b4b-8ca1-56f63264badc)   ![1000099148](https://github.com/user-attachments/assets/aadd1cf5-6da0-485b-81c5-4ae892c5bcd3)


**Update Dailog Image**

![1000099113](https://github.com/user-attachments/assets/33b6c981-e331-4a2f-9457-7ef96a2f200c)   ![1000099149](https://github.com/user-attachments/assets/c96d4c03-5378-4078-9130-10233460285c)


**Delete Dailog Image**

![1000099114](https://github.com/user-attachments/assets/a5eb3afd-bf51-4d7d-8d0b-0cd90e029498)   ![1000099150](https://github.com/user-attachments/assets/d2716a65-b509-4188-b20c-5faa0c32893a)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

**Profile Fragment Image**
-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


**Normal View Image**

![1000099120](https://github.com/user-attachments/assets/1fc3e00e-1f90-46e3-8eb4-6532c74adc17)    ![1000099157](https://github.com/user-attachments/assets/3858bd00-43bc-4744-a140-062a05c73a77)

**Click on user Name UserDetailsEditProfile Image**

![1000099121](https://github.com/user-attachments/assets/707d0791-9bc4-412f-84b1-0a6df0445ee9)    ![1000099156](https://github.com/user-attachments/assets/fdea6c55-3933-4e8e-91b4-34b06cdcf798)     

**Edit Profile or uodate Profile Image**

![1000099122](https://github.com/user-attachments/assets/4a2954e7-2391-4def-b018-ae886b9fb0cf)   ![1000099158](https://github.com/user-attachments/assets/f7533045-1649-49d7-a8cd-a0dfd6b37135)


**Change Password**

![1000099123](https://github.com/user-attachments/assets/d0796ad1-b9df-4f22-a714-b8efc1fea70e)   ![1000099159](https://github.com/user-attachments/assets/a7d357f4-5722-4ecf-b323-480f47da5d4f)

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------


