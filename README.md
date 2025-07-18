# Tecnologie Informatiche per il Web - TIW Project (Pure HTML Version) - Final evaluation: 28/30

This repository contains the server-side rendered version of the Exam Management System, developed for the "Tecnologie Informatiche per il Web" (TIW) university course.

The application allows professors to manage exam results and students to view and interact with their outcomes. This version is built using a classic multi-page architecture where the server generates and serves complete HTML pages.

---

## 🚀 Core Features

-   **Dual User Roles**: Separate interfaces and functionalities for Professors (`Docente`) and Students (`Studente`).
-   **Professor Dashboard**:
    -   Secure login to access a personalized homepage.
    -   View a list of owned courses and select a specific exam session (`appello`).
    -   Manage enrolled students via a dynamic table.
    -   **Sortable Lists**: Order students by matriculation number, name, email, etc., by clicking on table headers.
    -   **Grade Management**: Insert, update, and publish student grades. The status of each student progresses through a defined workflow (`Non inserito` -> `Inserito` -> `Pubblicato`).
    -   **Official Records (`Verbalizzazione`)**: Finalize and record the outcomes for an exam session, generating an official `Verbale`.
-   **Student Dashboard**:
    -   Secure login to view enrolled courses.
    -   Check the result (`esito`) for a specific exam.
    -   **Reject Grade**: Students have the option to reject a passing grade, which updates its status to `Rifiutato`.

---

## 🛠️ Technology Stack

This project leverages a pure server-side technology stack.

-   **Frontend**:
    -   `HTML5`: For the structure of the web pages.
    -   `CSS3`: For styling and layout.
    -   `Thymeleaf`: As a template engine to dynamically generate HTML content on the server.
-   **Backend**:
    -   `Java Servlets`: To handle all HTTP requests, manage user sessions, and control application logic.
    -   `JavaBeans`: To model the application's data (e.g., Student, Course, Grade).
    -   `DAO (Data Access Object)`: A design pattern used to abstract and encapsulate all access to the data source.
-   **Database**:
    -   `MySQL`: As the relational database management system.

---

## ⚙️ How It Works

The application follows a traditional **Multi-Page Application (MPA)** model.

1.  The user's browser sends an HTTP request to a **Java Servlet**.
2.  The Servlet processes the request, interacts with the database via DAOs, and prepares the necessary data.
3.  It then uses the `WebContext` to pass this data to a **Thymeleaf template**.
4.  Thymeleaf renders a complete HTML page with the dynamic data.
5.  The server sends this final HTML page back to the client's browser.

Every significant user action results in a new request to the server and a full page reload.
