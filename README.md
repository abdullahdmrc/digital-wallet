<div align="center">

  

  <h1>💳 Digital Wallet</h1>

  <p>
    A full-stack, AI-powered digital wallet application built with <strong>Spring Boot</strong> and <strong>Angular</strong>, featuring a conversational AI chatbot, multi-currency support, PDF receipt generation, and a complete transaction approval workflow.
  </p>

  <p>
    <img src="https://img.shields.io/badge/Spring_Boot-4.0.2-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
    <img src="https://img.shields.io/badge/Angular-21-DD0031?style=for-the-badge&logo=angular&logoColor=white" alt="Angular" />
    <img src="https://img.shields.io/badge/PostgreSQL-15-316192?style=for-the-badge&logo=postgresql&logoColor=white" alt="PostgreSQL" />
    <img src="https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
    <img src="https://img.shields.io/badge/LangChain4j-0.35.0-FF6C37?style=for-the-badge&logo=openai&logoColor=white" alt="LangChain4j" />
    <img src="https://img.shields.io/badge/Java-21-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java 21" />
    <img src="https://img.shields.io/badge/Kubernetes-Skaffold-326CE5?style=for-the-badge&logo=kubernetes&logoColor=white" alt="Kubernetes" />
  </p>

</div>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Architecture](#-architecture)
- [Project Structure](#-project-structure)
- [Getting Started](#-getting-started)
  - [Prerequisites](#prerequisites)
  - [Running with Docker Compose](#running-with-docker-compose)
  - [Running Locally (Manual Setup)](#running-locally-manual-setup)
- [API Endpoints](#-api-endpoints)
- [AI Chatbot](#-ai-chatbot)
- [Roles & Permissions](#-roles--permissions)
- [Deployment (Kubernetes + Skaffold)](#-deployment-kubernetes--skaffold)
- [Environment Variables](#-environment-variables)
- [License](#-license)

---

## 🌟 Overview

**Digital Wallet** is a feature-rich, enterprise-grade fintech application that allows users to manage digital wallets, perform financial transactions, and get intelligent insights through an integrated AI assistant. The platform supports multi-currency wallets (TRY, USD, EUR) and provides a complete transaction lifecycle — from initiation to employee approval — with PDF receipt generation.

The AI assistant, powered by a locally-run **Llama 3.1** model via **Ollama** and the **LangChain4j** framework, can answer questions about wallet balances, transaction history, and spending summaries in natural language.

---

## ✨ Features

### 💼 Wallet Management
- Create multiple wallets per customer with custom names
- Multi-currency support: **TRY**, **USD**, **EUR**
- Auto-generated unique **IBAN** per wallet
- Toggle wallets active/inactive for shopping and withdrawals
- **Block / Unblock** wallet functionality (admin & employee)
- Real-time balance and usable balance tracking

### 💸 Transactions
- **Deposit** money into any wallet
- **Withdraw** from any wallet
- **IBAN-to-IBAN Transfer** between wallets
- Spending category tagging: `FOOD`, `SHOPPING`, `FUEL`, `CINEMA`, `TRAVEL`, `OTHER`
- Transaction status workflow: `PENDING` → `APPROVED` / `DENIED`
- **PDF Receipt** download for each transaction

### 🤖 AI Financial Assistant
- Integrated chatbot powered by **Llama 3.1** (via Ollama) + **LangChain4j**
- Retrieval-Augmented Generation (RAG) with in-memory vector store
- Per-session memory (last 10 messages)
- Tool-augmented answers:
  - Wallet balances
  - Recent transactions
  - Spending by category
  - Monthly account summary
- Secured: chatbot only accesses the authenticated user's data

### 🔐 Security
- **JWT-based authentication** (access tokens via `jjwt`)
- Role-based access control: `ROLE_CUSTOMER`, `ROLE_EMPLOYEE`
- Protected endpoints with Spring Security + `@PreAuthorize`
- Angular `AuthGuard` for client-side route protection
- HTTP Interceptor for automatic JWT header injection

### 📧 Notifications
- Email notification service via **Spring Mail**

### 🐳 DevOps & Deployment
- **Docker Compose** for local multi-container setup (PostgreSQL + Backend + Frontend)
- **Kubernetes** manifests for production deployment
- **Skaffold** configuration for streamlined K8s dev workflow
- **Google Cloud Deploy** pipeline (staging & production targets)
- Nginx-served frontend container

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| **Backend Framework** | Spring Boot 4.0.2 |
| **Language** | Java 21 |
| **Security** | Spring Security + JWT (jjwt 0.11.5) |
| **ORM** | Spring Data JPA + Hibernate |
| **Database** | PostgreSQL 15 |
| **AI / LLM** | LangChain4j 0.35.0 + Ollama (Llama 3.1) |
| **Embeddings** | AllMiniLmL6V2 (local ONNX model) |
| **PDF Generation** | OpenPDF 2.0.0 |
| **Email** | Spring Mail |
| **Utilities** | Lombok |
| **Frontend Framework** | Angular 21 |
| **UI Library** | Angular Material 21 |
| **HTTP Client** | Angular HttpClient |
| **Markdown Rendering** | marked 18.0.3 |
| **Containerization** | Docker + Docker Compose |
| **Orchestration** | Kubernetes + Skaffold |
| **Web Server** | Nginx (frontend container) |

---

## 🏗 Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        Browser / Client                          │
│                     Angular 21 Frontend                          │
│  ┌──────────┐ ┌──────────────┐ ┌──────────┐ ┌──────────────┐   │
│  │  Login / │ │  Customer    │ │  Wallet  │ │  AI Chatbot  │   │
│  │ Register │ │   Dashboard  │ │  Detail  │ │   Widget     │   │
│  └──────────┘ └──────────────┘ └──────────┘ └──────────────┘   │
│            ↑  JWT AuthGuard + HTTP Interceptor                   │
└────────────────────────┬────────────────────────────────────────┘
                         │ REST API (HTTP/JSON)
┌────────────────────────▼────────────────────────────────────────┐
│                   Spring Boot Backend                            │
│                                                                  │
│  ┌───────────────┐  ┌────────────────┐  ┌─────────────────┐    │
│  │  Auth         │  │  Wallet        │  │  Transaction    │    │
│  │  Controller   │  │  Controller    │  │  Controller     │    │
│  └───────┬───────┘  └───────┬────────┘  └────────┬────────┘    │
│          │                  │                     │              │
│  ┌───────▼──────────────────▼─────────────────────▼────────┐   │
│  │                   Service Layer                          │   │
│  │  AuthService │ WalletService │ TransactionService        │   │
│  │  ChatService │ EmailService  │ PdfService                │   │
│  └───────────────────────┬──────────────────────────────────┘   │
│                          │                                       │
│  ┌───────────────────────▼──────────────────────────────────┐   │
│  │              LangChain4j AI Layer                        │   │
│  │  ChatAssistant (Llama 3.1 via Ollama)                    │   │
│  │  RAG: InMemoryEmbeddingStore + AllMiniLmL6V2             │   │
│  │  Tools: WalletTools (balance, transactions, summaries)   │   │
│  └──────────────────────────────────────────────────────────┘   │
│                                                                  │
│  ┌──────────────────────────────────────────────────────────┐   │
│  │              Spring Data JPA / Repository Layer          │   │
│  └─────────────────────────┬────────────────────────────────┘   │
└────────────────────────────┼────────────────────────────────────┘
                             │
┌────────────────────────────▼────────────────────────────────────┐
│                     PostgreSQL 15                                │
│         Users │ Customers │ Wallets │ Transactions               │
└─────────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
digital-wallet-main/
├── docker-compose.yml              # Local multi-container setup
├── skaffold.yaml                   # Kubernetes dev workflow
│
├── deploy/                         # Kubernetes manifests
│   ├── db-manifest.yaml
│   ├── backend-manifest.yaml
│   ├── frontend-manifest.yaml
│   ├── delivery-pipeline.yaml      # Google Cloud Deploy pipeline
│   ├── target-staging.yaml
│   └── target-prod.yaml
│
├── digitalwallet-backend/          # Spring Boot application
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/main/java/com/example/digitalwallet/
│       ├── config/
│       │   └── ChatbotConfig.java  # LangChain4j / Ollama setup
│       ├── controller/
│       │   ├── AuthController.java
│       │   ├── ChatController.java
│       │   ├── WalletController.java
│       │   └── TransactionController.java
│       ├── dto/                    # Request/Response DTOs
│       ├── model/
│       │   ├── User.java
│       │   ├── Customer.java
│       │   ├── Wallet.java
│       │   └── Transaction.java
│       ├── repository/             # Spring Data JPA Repositories
│       ├── security/
│       │   ├── SecurityConfig.java
│       │   ├── JwtService.java
│       │   ├── JwtAuthFilter.java
│       │   └── UserDetailService.java
│       └── service/
│           ├── ChatAssistant.java  # LangChain4j AI interface
│           ├── WalletTools.java    # AI tool definitions
│           └── impl/
│               ├── AuthService.java
│               ├── WalletService.java
│               ├── TransactionService.java
│               ├── ChatService.java
│               ├── EmailService.java
│               └── PdfService.java
│
└── digitalwallet-frontend/         # Angular application
    └── digitalwalletapp/
        ├── Dockerfile
        ├── nginx.conf
        └── src/app/
            ├── components/
            │   ├── login/
            │   ├── register/
            │   ├── customer-home/
            │   ├── wallet-detail-component/
            │   ├── approvedeny/       # Employee transaction approval
            │   ├── admin/
            │   ├── chatbot-widget/
            │   └── transaction-creation/
            ├── guards/
            │   └── auth-guard-guard.ts
            ├── layouts/
            │   ├── auth-layout/
            │   └── main-layout/
            ├── services/
            │   ├── AuthService.ts
            │   ├── WalletService.ts
            │   └── chat-service.ts
            └── my-interceptor.ts      # JWT injection interceptor
```

---

## 🚀 Getting Started

### Prerequisites

Before running the project, make sure you have the following installed:

- [Docker](https://www.docker.com/get-started) & Docker Compose
- [Ollama](https://ollama.com/) — for the local LLM
- [Java 21](https://adoptium.net/) (for manual backend setup)
- [Node.js 20+](https://nodejs.org/) & Angular CLI (for manual frontend setup)

> **Important:** The AI chatbot requires **Ollama** to be running locally with the **Llama 3.1** model pulled.

```bash
# Pull the Llama 3.1 model (one-time setup)
ollama pull llama3.1

# Verify Ollama is running
ollama serve
```

---

### Running with Docker Compose

The easiest way to run the entire application stack:

```bash
# Clone the repository
git clone https://github.com/your-username/digital-wallet.git
cd digital-wallet

# Start all services (PostgreSQL + Backend + Frontend)
docker compose up --build
```

| Service | URL |
|---|---|
| Frontend (Angular) | http://localhost:4200 |
| Backend API | http://localhost:8081 |
| PostgreSQL | localhost:5433 |

> **Note:** Ollama must be running on your host machine at `http://localhost:11434` for the AI chatbot to work. The backend container connects to it via the host network.

---

### Running Locally (Manual Setup)

#### 1. Start PostgreSQL

```bash
# Using Docker for just the database
docker run --name wallet-db \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=1234 \
  -e POSTGRES_DB=digitalwalletdb \
  -p 5433:5432 \
  -d postgres:15
```

#### 2. Run the Backend

```bash
cd digitalwallet-backend

# Build and run with Maven Wrapper
./mvnw spring-boot:run
```

The backend will start at `http://localhost:8081`. Hibernate will auto-create the database schema on first run.

#### 3. Run the Frontend

```bash
cd digitalwallet-frontend/digitalwalletapp

# Install dependencies
npm install

# Start the development server
npm start
```

The frontend will be available at `http://localhost:4200`.

---

## 📡 API Endpoints

### Authentication

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/auth/register` | Register a new customer | ❌ |
| `POST` | `/api/auth/login` | Login and receive JWT token | ❌ |

### Wallets

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `GET` | `/api/wallets` | Get all wallets for current user | `CUSTOMER` |
| `POST` | `/api/wallets` | Create a new wallet | `CUSTOMER` |
| `GET` | `/api/wallets/{id}` | Get wallet by ID | `CUSTOMER` |
| `PUT` | `/api/wallets/{id}/block` | Block a wallet | `EMPLOYEE` |
| `PUT` | `/api/wallets/{id}/unblock` | Unblock a wallet | `EMPLOYEE` |

### Transactions

| Method | Endpoint | Description | Role |
|---|---|---|---|
| `POST` | `/api/deposits` | Deposit money into wallet | `CUSTOMER` |
| `POST` | `/api/withdraws` | Withdraw money from wallet | `CUSTOMER` |
| `POST` | `/api/transactions/transfer` | Transfer via IBAN | `CUSTOMER` |
| `GET` | `/api/transactions` | Get all transactions (admin view) | `EMPLOYEE` |
| `GET` | `/api/wallets/{id}/transactions` | Get transactions for a wallet | `CUSTOMER` |
| `POST` | `/api/transactions/approve/{id}` | Approve a pending transaction | `EMPLOYEE` |
| `POST` | `/api/transactions/deny/{id}` | Deny a pending transaction | `EMPLOYEE` |
| `GET` | `/api/transactions/{id}/receipt` | Download PDF receipt | `CUSTOMER` |

### AI Chatbot

| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| `POST` | `/api/chat` | Send a message to the AI assistant | ✅ |

---

## 🤖 AI Chatbot

The embedded AI assistant is built using the **LangChain4j** framework connected to a locally hosted **Llama 3.1** model via **Ollama**.

### Architecture

```
User Message
     ↓
ChatController → ChatService
     ↓
ChatAssistant (LangChain4j AiService)
     ↓                     ↓
  WalletTools (Tools)   RAG Content Retriever
  ├── getWalletBalances  (InMemoryEmbeddingStore)
  ├── getRecentTrans.    (AllMiniLmL6V2 Embeddings)
  ├── getSpendingByCategory   (kurallar.txt knowledge base)
  └── getMonthlySummarize
     ↓
  Ollama → Llama 3.1
     ↓
  AI Response (plain text)
```

### Capabilities

| Query Example | Tool Used |
|---|---|
| *"What is my wallet balance?"* | `getWalletBalances` |
| *"Show my last 5 transactions"* | `getRecentTransactions` |
| *"How much did I spend on food?"* | `getSpendingByCategory` |
| *"Give me an April summary"* | `getMonthlySummarize` |

- Each user session has an isolated **10-message memory window**
- The chatbot strictly respects data isolation — it **only exposes the authenticated user's data**

---

## 🔐 Roles & Permissions

| Feature | CUSTOMER | EMPLOYEE |
|---|---|---|
| Create/View Wallets | ✅ | — |
| Deposit / Withdraw | ✅ | — |
| Transfer via IBAN | ✅ | — |
| Download PDF Receipt | ✅ | — |
| Chat with AI Assistant | ✅ | — |
| Approve/Deny Transactions | — | ✅ |
| Block/Unblock Wallets | — | ✅ |
| View All Transactions | — | ✅ |

---

## ☸️ Deployment (Kubernetes + Skaffold)

The project includes Kubernetes manifests and a Skaffold configuration for container-based deployment.

```bash
# Build and deploy to local Kubernetes (e.g., Minikube)
skaffold run

# Development mode with hot-reload
skaffold dev
```

The `deploy/` directory contains:
- `db-manifest.yaml` — PostgreSQL StatefulSet/Deployment
- `backend-manifest.yaml` — Spring Boot Deployment + ClusterIP Service
- `frontend-manifest.yaml` — Angular/Nginx Deployment + Service
- `delivery-pipeline.yaml` — Google Cloud Deploy pipeline definition
- `target-staging.yaml` / `target-prod.yaml` — GKE deployment targets

---

## ⚙️ Environment Variables

The backend uses the following environment variables (configurable via Docker Compose or K8s manifests):

| Variable | Default | Description |
|---|---|---|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://db:5432/digitalwalletdb` | PostgreSQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `1234` | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate DDL mode |

> ⚠️ **Security Note:** Before deploying to production, replace default database credentials with strong passwords and manage them using Kubernetes Secrets or a secrets manager. Do not commit credentials to version control.

The AI chatbot expects **Ollama** to be accessible at `http://localhost:11434` with the `llama3.1` model loaded.

---

## 🗂 Data Model

```
User (id, username, password, role, email)
  └── Customer (id, firstName, lastName, tckn, user_id)
        └── Wallet (id, walletName, currency, iban, balance, usableBalance, isBlocked, customer_id)
              └── Transaction (id, amount, type, oppositeParty, status, date, spendingCategory, wallet_id)
```

**Transaction Types:** `DEPOSIT`, `WITHDRAW`  
**Opposite Party Types:** `IBAN`, `PAYMENT`  
**Transaction Status:** `PENDING` → `APPROVED` | `DENIED`  
**Spending Categories:** `FOOD`, `SHOPPING`, `FUEL`, `CINEMA`, `TRAVEL`, `OTHER`  
**Wallet Currencies:** `TRY`, `USD`, `EUR`

---

## 📦 Key Dependencies

### Backend (`pom.xml`)

| Dependency | Version | Purpose |
|---|---|---|
| `spring-boot-starter-webmvc` | 4.0.2 | REST API |
| `spring-boot-starter-security` | 4.0.2 | Authentication & Authorization |
| `spring-boot-starter-data-jpa` | 4.0.2 | ORM / Database |
| `spring-boot-starter-mail` | 4.0.2 | Email notifications |
| `jjwt-api/impl/jackson` | 0.11.5 | JWT token handling |
| `langchain4j` | 0.35.0 | Core AI framework |
| `langchain4j-ollama` | 0.35.0 | Ollama/Llama integration |
| `langchain4j-embeddings-all-minilm-l6-v2` | 0.35.0 | Local embedding model |
| `langchain4j-hugging-face` | 0.35.0 | HuggingFace model support |
| `openpdf` | 2.0.0 | PDF receipt generation |
| `lombok` | 1.18.42 | Boilerplate reduction |
| `postgresql` | (runtime) | JDBC driver |

### Frontend (`package.json`)

| Package | Version | Purpose |
|---|---|---|
| `@angular/core` | ^21.1.0 | Core framework |
| `@angular/material` | ~21.1.5 | UI component library |
| `@angular/router` | ^21.1.0 | Client-side routing |
| `jwt-decode` | ^4.0.0 | JWT decoding |
| `marked` | ^18.0.3 | Markdown rendering in chat |

---

## 📄 License

This project is licensed under the **MIT License**.

---

<div align="center">
  <p>Built with ❤️ using Spring Boot, Angular, and LangChain4j</p>
</div>
