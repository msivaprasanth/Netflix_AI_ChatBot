# Netflix AI Chatbot

A full-stack AI-powered chatbot built using Spring Boot, React, MongoDB, and Ollama (TinyLlama) that helps users discover movies and manage personalized recommendations — just like Netflix’s AI assistant!

---

##  Features
-  User authentication (JWT-secured)
-  Real-time AI chat powered by TinyLlama
-  Personalized movie recommendations
-  Password reset via Gmail SMTP
-  MongoDB for chat history and user preferences
-  Full-stack integration (React + Spring Boot)

---

##  Tech Stack

| Layer | Technology |
|-------|-------------|
| Frontend | React, Axios, Bootstrap |
| Backend | Spring Boot (REST API) |
| AI | Ollama - TinyLlama model |
| Database | MongoDB |
| Auth | JWT |
| Email | Gmail SMTP |

---

##  Setup Instructions

### 1️⃣ Clone Repository
git clone https://github.com/msivaprasanth/Netflix_AI_ChatBot.git

cd Netflix_AI_ChatBot

### 2️⃣ Backend Setup
mvn clean install

mvn spring-boot:run

### 3️⃣ FrontEnd Setup
cd frontend

npm install

npm start

### 4️⃣ Configure Environment 
spring.data.mongodb.uri=mongodb://localhost:27017/netflix_ai_chatbot

spring.ai.ollama.base-url=http://localhost:11434

spring.ai.ollama.chat.model=tinyllama

spring.mail.username=your-email@gmail.com

spring.mail.password=your-app-password

