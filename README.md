# ExpenseTracker

ExpenseTracker is an Android application designed to simplify personal expense management by automatically detecting and recording financial transactions. Instead of requiring users to manually enter every expense, the application processes transaction messages and extracts relevant information such as the amount, merchant, transaction type, and date.

The extracted information is organized and stored locally, allowing users to view their spending history and understand their overall spending patterns through a simple dashboard.

## Overview

Keeping track of daily expenses manually can be inconvenient, especially when transactions are made frequently through different payment methods. ExpenseTracker addresses this problem by automating the initial stage of expense recording.

When a supported bank or payment transaction generates an SMS or notification, the application identifies the relevant message and processes its contents. A transaction parser extracts important details from the message, after which the expense can be categorized and stored in the application's local database.

The stored data is then used to provide users with an organized view of their financial activity, including transaction history, category-wise spending, and overall expenses.

## How It Works

The application follows a simple processing pipeline:

Transaction
     ↓
Bank / UPI SMS or Notification
     ↓
Transaction Detection
     ↓
Message Parsing
     ↓
Information Extraction
     ↓
Expense Categorization
     ↓
Local Database
     ↓
Expense Dashboard

For example, when a transaction message indicates that ₹450 was spent at a restaurant, the application can identify the transaction amount and merchant, determine that it is a debit transaction, assign it to a suitable category such as **Food**, and store the information for future reference.

## Technology Stack

The application is developed using **Android Studio** with **Kotlin** as the primary programming language. Android's SMS and notification capabilities are used for transaction detection, while **Room/SQLite** can be used for persistent local storage.

The user interface is built using Android's native UI components, with the application architecture designed to separate transaction detection, processing, storage, and presentation.

Android Studio
      │
      ├── Kotlin
      ├── Android SDK
      ├── SMS / Notification APIs
      ├── Room / SQLite
      └── Native Android UI

## System Architecture

The application can be viewed as four major layers. The input layer receives transaction information from SMS or notifications. The processing layer identifies financial messages and extracts transaction details. The data layer stores the processed transactions locally, while the presentation layer displays the information through the expense dashboard.

This structure makes it possible to improve individual components, such as replacing rule-based transaction extraction with an ML-based classifier, without completely redesigning the application.

## Key Functionality

The main functionality of ExpenseTracker revolves around **automated transaction recording**. The application attempts to identify relevant financial messages and convert unstructured transaction text into structured information.

A typical transaction stored by the application can contain information similar to:

Amount      : ₹450
Merchant    : ABC Restaurant
Type        : Expense
Category    : Food
Date        : Transaction Date

This information can then be used for expense history, category-wise analysis, budgeting, and future spending insights.

## Future Scope

The project can be extended with machine-learning-based expense categorization, monthly spending predictions, personalized saving insights, budget notifications, financial reports, CSV/PDF export, biometric authentication, and optional cloud synchronization.

Another potential enhancement is to improve transaction parsing so that the application can support a wider range of banks, UPI providers, and transaction message formats.

## Project Objective

The objective of ExpenseTracker is to demonstrate how **Android development, automated text processing, local data storage, and financial analytics** can be combined to reduce the effort involved in maintaining personal expense records.

By automating transaction capture while keeping the financial data organized locally, the project provides a foundation for building a more intelligent personal finance management system.

## Author

Chris Dsouza
Computer Science & Engineering – Data Science

## License

This project is developed for educational and development purposes.
