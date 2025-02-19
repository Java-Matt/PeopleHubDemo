# PeopleHubDemo 🚀

## Description 📝

**PeopleHubDemo** is a **Java Spring Boot application** designed to manage data for three distinct types of individuals: **Students**, **Employees**, and **Pensioners**. This project showcases my ability to build **robust, scalable, and extensible software solutions**, integrating advanced features such as **data validation**, **file handling**, and **concurrency control**.

At its core, the system revolves around an **abstract `Person` class**, which serves as the foundation for all person types. This class manages shared attributes like **first name**, **last name**, **email**, **personal number**, **height**, and **weight**, ensuring consistency across the application. Each subclass extends the `Person` class to include type-specific details:
- **Students**: University information and scholarships.
- **Employees**: Professional experiences.
- **Pensioners**: Pension amounts and years of work history.

The application is designed with **flexibility** and **user-friendliness** in mind, offering multiple ways to manage data while maintaining **data integrity** and **security**.

---

## Key Features ✨

### 🧑‍💻 **User-Friendly Data Management**
- **Manual Data Entry**: Add or update records through a secure interface with **role-based access control**.
- **Bulk Data Upload**: Import large datasets via file uploads with **real-time progress monitoring** and **detailed status tracking**.

### 🔒 **Data Integrity & Validation**
- **Rigorous Validation**: Ensures all inputs adhere to strict business rules, such as:
  - Enforcing unique personal numbers.
  - Validating non-overlapping employee work experiences.
  - Checking completeness of mandatory fields.
- **Custom Annotations**: Dynamically validate data for extensibility and scalability.

### 🔍 **Dynamic Search Functionality**
- **Flexible Querying**: Powered by a **dynamic specification builder**, enabling users to search for person records using shared or type-specific attributes.
- **Scalable & Adaptable**: Easily handles new person types without compromising performance.

### ⚙️ **Concurrency Control**
- **Optimistic Locking**: Prevents data modification conflicts in multi-user environments, ensuring **consistent and secure updates**.

### 🛠️ **Extensibility & Modularity**
- **Strategy Pattern**: Allows new person types to be added without modifying existing code.
- **Single-Table Inheritance**: Stores all person types in one database table, simplifying data management while maintaining high performance.

### 🧪 **Comprehensive Testing**
- **Unit & Integration Tests**: Cover core functionalities like CRUD operations, file uploads, validations, and concurrency handling.
- **Edge Case Scenarios**: Ensures the system behaves as expected under various conditions.

---

## Why PeopleHubDemo? 🌟

This project demonstrates my expertise in building **practical, user-focused applications** that integrate advanced concepts like:
- **Dynamic querying** for flexible data retrieval.
- **Custom validation** for robust data integrity.
- **Concurrency control** for secure multi-user environments.

Whether managing **academic records**, **employee data**, or **pension records**, PeopleHubDemo is a **scalable and reliable solution** for real-world use cases.

---

## Technologies Used 🛠️

- **Backend**: Java Spring Boot
- **Database**: Single-table inheritance for efficient data storage
- **Validation**: Custom annotations and dynamic validation
- **Concurrency**: Optimistic locking
- **Testing**: Comprehensive unit and integration tests

---

Thank you for checking out **PeopleHubDemo**! 🎉
