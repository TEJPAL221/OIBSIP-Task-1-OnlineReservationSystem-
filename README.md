# OIBSIP-Task-1-OnlineReservationSystem-
# 🛤️ Online Reservation System

### 🌐 Project by:Tejpal singh Rathore
### 🏢 Internship: Oasis Infobyte – Java Development Internship  

---

## 📋 Project Description

The **Online Reservation System** is a Java-based console application that simulates the core functionalities of a train ticket reservation and cancellation system. Built as part of the Oasis Infobyte Java Development Internship, this project integrates JDBC to connect and interact with a MySQL database for persistent data management.

---

## 🚀 Features

- 🔒 **User Authentication** (Login/Register system)
- 🧾 **Ticket Reservation** with dynamic details
- ❌ **Ticket Cancellation** functionality
- 📊 **View Booking Status**
- 💾 **Database Integration** using **MySQL** and **JDBC**
- 💻 **Console-based interface** with clear prompts and instructions

---

## 🛠️ Technologies Used

- **Java SE (JDK 17 or above)**
- **MySQL Database**
- **JDBC API**
- **MySQL Connector/J**
- **Command Line Interface (CLI)**

---

## 🗂️ File Structure

OnlineReservationSystem/
│
├── OnlineReservationSystem.java # Main application file
├── README.md # Project documentation
├── mysql-connector-j-9.3.0.jar # JDBC driver
└── /sql # SQL scripts (optional, if added)

yaml
Copy
Edit

---

## 🏁 Getting Started

### ✅ Prerequisites

- Java JDK installed (version 17 or higher recommended)
- MySQL installed and configured
- MySQL Connector/J downloaded

### 🔧 Setup Instructions

1. **Clone or Download** this project folder.
2. **Open terminal (CMD or PowerShell)** and navigate to the project directory:



Compile the Java file:


javac -cp "C:\Users\tejpal singh rathore\Downloads\mysql-connector-j-9.3.0\mysql-connector-j-9.3.0.jar" OnlineReservationSystem.java
Run the application:


java -cp ".;C:\Users\tejpal singh rathore\Downloads\mysql-connector-j-9.3.0\mysql-connector-j-9.3.0.jar" OnlineReservationSystem
🗃️ Database Configuration
Make sure MySQL is running and the following database and table exist:

sql
Copy
Edit
CREATE DATABASE reservation_system;

USE reservation_system;

CREATE TABLE reservations (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100),
    train_no VARCHAR(10),
    class VARCHAR(10),
    date_of_journey DATE,
    from_station VARCHAR(100),
    to_station VARCHAR(100),
    status VARCHAR(20)
);
Update database credentials in your Java file if needed:

java
Copy
Edit
String url = "jdbc:mysql://localhost:3306/reservation_system";
String user = "root";
String password = "your_password_here";
