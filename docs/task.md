# Task: Java 8 Banking System — Implementation

## Overview

Build a **complete banking system** implemented entirely in **Java 8**, covering the core functionality of a real bank. The system serves as a foundation for future modernization to Java 21 (Liberty). This document defines the scope, architecture, entities, UI requirements, and sample data for the initial implementation.

> **Scope boundary**: This task covers only the Java 8 implementation. Modernization (migration to Java 21 / Liberty) is a separate, subsequent task and must NOT be performed here.

---

## Goals

1. Implement a complete, working banking system in Java 8.
2. Cover all major banking entities: customers, accounts, transactions, cards, loans, branches.
3. Provide a simple but authentic banking-style UI with **white-label placeholders** (bank name, logo, colors) — no real branding applied yet.
4. Seed the system with realistic sample data including customers from diverse international backgrounds.
5. Produce clean, well-commented Java 8 code that will later serve as the legacy baseline for modernization analysis.

---

## Technical Constraints

| Parameter | Value |
|---|---|
| Java Version | **Java 8** (strictly — no Java 9+ APIs) |
| Build Tool | Maven (`pom.xml`) |
| UI | Simple console-based or Swing-based interface |
| Persistence | In-memory (no database required for this phase) |
| Frameworks | No external frameworks — pure Java 8 |
| Architecture | Layered: Model → Repository → Service → UI |

---

## System Architecture

```
bank-system/
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── bank/
│                   ├── model/          # Domain entities
│                   ├── repository/     # In-memory data storage
│                   ├── service/        # Business logic
│                   ├── ui/             # User interface (console or Swing)
│                   ├── util/           # Helpers, formatters, generators
│                   ├── exception/      # Custom exception classes
│                   ├── init/           # Data seeding and bootstrap
│                   │   └── SampleDataInitializer.java
│                   └── BankApplication.java   # Entry point
├── pom.xml
└── README.md
```

---

## Banking Entities (Domain Model)

### 1. Customer
Represents an individual or corporate bank client.

| Field | Type | Description |
|---|---|---|
| `customerId` | `String` | Unique identifier (UUID) |
| `firstName` | `String` | First name |
| `lastName` | `String` | Last name |
| `dateOfBirth` | `LocalDate` | Date of birth |
| `email` | `String` | Email address |
| `phone` | `String` | Phone number |
| `address` | `Address` | Full address (street, city, country) |
| `nationality` | `String` | Country of citizenship |
| `customerType` | `CustomerType` | INDIVIDUAL / CORPORATE |
| `status` | `CustomerStatus` | ACTIVE / INACTIVE / BLOCKED |
| `createdAt` | `LocalDateTime` | Account opening date |

### 2. Account
Each customer can have one or more accounts.

| Field | Type | Description |
|---|---|---|
| `accountId` | `String` | Unique identifier (UUID) |
| `accountNumber` | `String` | Human-readable account number (IBAN-style) |
| `customerId` | `String` | Owner reference |
| `accountType` | `AccountType` | CHECKING / SAVINGS / BUSINESS / INVESTMENT |
| `currency` | `String` | ISO 4217 currency code (USD, EUR, GBP…) |
| `balance` | `BigDecimal` | Current balance |
| `availableBalance` | `BigDecimal` | Balance minus holds |
| `status` | `AccountStatus` | ACTIVE / FROZEN / CLOSED |
| `openedAt` | `LocalDateTime` | Account opening timestamp |
| `interestRate` | `BigDecimal` | Annual interest rate (for savings) |

### 3. Transaction
All financial movements on an account.

| Field | Type | Description |
|---|---|---|
| `transactionId` | `String` | Unique identifier (UUID) |
| `fromAccountId` | `String` | Source account (nullable for deposits) |
| `toAccountId` | `String` | Destination account (nullable for withdrawals) |
| `amount` | `BigDecimal` | Transaction amount |
| `currency` | `String` | Currency of the transaction |
| `type` | `TransactionType` | DEPOSIT / WITHDRAWAL / TRANSFER / PAYMENT / FEE |
| `status` | `TransactionStatus` | PENDING / COMPLETED / FAILED / REVERSED |
| `description` | `String` | Human-readable memo |
| `timestamp` | `LocalDateTime` | When the transaction occurred |
| `referenceCode` | `String` | External reference (e.g. merchant ID) |

### 4. Card
Debit and credit cards linked to accounts.

| Field | Type | Description |
|---|---|---|
| `cardId` | `String` | Unique identifier (UUID) |
| `accountId` | `String` | Linked account |
| `customerId` | `String` | Card holder |
| `cardNumber` | `String` | Masked: `**** **** **** 1234` |
| `cardType` | `CardType` | DEBIT / CREDIT / VIRTUAL |
| `network` | `CardNetwork` | VISA / MASTERCARD / AMEX |
| `expiryDate` | `YearMonth` | Card expiry |
| `cvv` | `String` | Stored as hash only |
| `status` | `CardStatus` | ACTIVE / BLOCKED / EXPIRED / CANCELLED |
| `creditLimit` | `BigDecimal` | Only for CREDIT cards |
| `dailyLimit` | `BigDecimal` | Daily spending cap |

### 5. Loan
Credit products issued to customers.

| Field | Type | Description |
|---|---|---|
| `loanId` | `String` | Unique identifier (UUID) |
| `customerId` | `String` | Borrower reference |
| `accountId` | `String` | Disbursement account |
| `loanType` | `LoanType` | PERSONAL / MORTGAGE / AUTO / BUSINESS |
| `principalAmount` | `BigDecimal` | Original loan amount |
| `outstandingBalance` | `BigDecimal` | Remaining balance |
| `interestRate` | `BigDecimal` | Annual interest rate (%) |
| `termMonths` | `int` | Loan duration in months |
| `monthlyPayment` | `BigDecimal` | Fixed monthly installment |
| `startDate` | `LocalDate` | Loan start date |
| `endDate` | `LocalDate` | Loan maturity date |
| `status` | `LoanStatus` | ACTIVE / PAID_OFF / DEFAULTED / CLOSED |

### 6. Branch
Physical bank branches.

| Field | Type | Description |
|---|---|---|
| `branchId` | `String` | Unique identifier |
| `branchCode` | `String` | Short branch code |
| `name` | `String` | Branch display name |
| `address` | `Address` | Branch address |
| `phone` | `String` | Branch contact number |
| `manager` | `String` | Branch manager name |
| `openingHours` | `String` | Operating hours |

### 7. Address (Embedded)

| Field | Type |
|---|---|
| `street` | `String` |
| `city` | `String` |
| `state` | `String` |
| `postalCode` | `String` |
| `country` | `String` |

### 8. AuditLog
Tracks all significant actions in the system for compliance and traceability.

| Field | Type | Description |
|---|---|---|
| `logId` | `String` | Unique identifier (UUID) |
| `action` | `String` | Action performed (e.g., TRANSFER, BLOCK_CARD, OPEN_ACCOUNT) |
| `performedBy` | `String` | User or system identifier |
| `entityType` | `String` | Affected entity type (ACCOUNT, CARD, LOAN, CUSTOMER) |
| `entityId` | `String` | Affected entity ID |
| `details` | `String` | Free-text description of the action |
| `timestamp` | `LocalDateTime` | When the action occurred |

---

## Business Logic (Service Layer)

### CustomerService
- `registerCustomer(...)` — create and validate a new customer
- `getCustomerById(String id)` — retrieve customer profile
- `updateCustomer(...)` — update personal information
- `blockCustomer(String id)` — flag customer as blocked
- `listAllCustomers()` — return all customers

### AccountService
- `openAccount(String customerId, AccountType type, String currency)` — open new account
- `closeAccount(String accountId)` — close account (zero balance required)
- `getBalance(String accountId)` — return current balance
- `freezeAccount(String accountId)` — freeze for compliance
- `getAccountsByCustomer(String customerId)` — list accounts per customer

### TransactionService
- `deposit(String accountId, BigDecimal amount, String description)` — credit funds
- `withdraw(String accountId, BigDecimal amount, String description)` — debit funds
- `transfer(String fromAccountId, String toAccountId, BigDecimal amount)` — internal transfer
- `getTransactionHistory(String accountId)` — paginated transaction history
- `reverseTransaction(String transactionId)` — reverse a completed transaction

> **Currency rule**: For this initial implementation, transfers are only allowed between accounts with the same currency. Cross-currency transfers are out of scope and will be addressed in the modernization phase.

### CardService
- `issueCard(String accountId, CardType type, CardNetwork network)` — issue new card
- `blockCard(String cardId)` — block a card immediately
- `activateCard(String cardId)` — activate a newly issued card
- `getCardsByAccount(String accountId)` — list cards linked to account

### LoanService
- `applyForLoan(String customerId, LoanType type, BigDecimal amount, int termMonths)` — submit loan application
- `approveLoan(String loanId)` — approve and disburse
- `makeRepayment(String loanId, BigDecimal amount)` — record a repayment
- `getLoanDetails(String loanId)` — retrieve loan summary

### AuditService
- `logAction(String action, String performedBy, String entityType, String entityId, String details)` — record an audit entry
- `getAuditLog()` — retrieve all audit records
- `getAuditLogByEntity(String entityType, String entityId)` — filter by entity

---

## Error Handling

All service methods must validate inputs and throw meaningful exceptions. The following custom exceptions must be defined in the `com.bank.exception` package:

| Exception | When Thrown |
|---|---|
| `InsufficientFundsException` | Withdrawal or transfer exceeds available balance |
| `AccountNotFoundException` | Referenced account does not exist |
| `CustomerNotFoundException` | Referenced customer does not exist |
| `InvalidTransactionException` | Transaction violates business rules (e.g., frozen account, currency mismatch) |
| `CardOperationException` | Invalid card operation (e.g., blocking an already blocked card) |
| `LoanApplicationException` | Loan validation failure (e.g., negative amount, zero term) |

**Rules**:
- UI layer must catch all exceptions gracefully and display user-friendly messages
- No unchecked exception should crash the application
- All exception classes must extend a common `BankException` base class

---

## Thread Safety

> All repository implementations must use `synchronized` methods or `ConcurrentHashMap` to prevent data corruption during concurrent access. This is intentionally implemented using Java 8 synchronization patterns, which will later serve as a modernization comparison point for Java 21 Virtual Threads.

---

## User Interface

### Technology

**Java Swing** — built into Java 8, no external UI dependencies required.
The interface must feel like a professional banking desktop application, not a generic Java tool.

### White-Label Placeholders

All branding elements are defined as constants in `BankConfig.java`, making it trivial to swap values for any bank:

```java
public class BankConfig {
    public static final String BANK_NAME          = "{{BANK_NAME}}";
    public static final String BANK_SLOGAN        = "{{BANK_SLOGAN}}";
    public static final String BANK_LOGO_PATH     = "{{LOGO_PATH}}";
    public static final String BANK_PRIMARY_COLOR = "{{PRIMARY_COLOR}}";
    public static final String SUPPORT_EMAIL      = "{{SUPPORT_EMAIL}}";
    public static final String SUPPORT_PHONE      = "{{SUPPORT_PHONE}}";
    public static final String SWIFT_CODE         = "{{SWIFT_CODE}}";
    public static final String BANK_COUNTRY       = "{{BANK_COUNTRY}}";
}
```

### Application Layout

```
+----------------------------------------------------------+
|  {{BANK_NAME}}   {{BANK_SLOGAN}}              v1.0.0    |  ← Header (dark navy)
+----------------+-----------------------------------------+
|                |                                         |
|  Dashboard     |                                         |
|  Customers     |        Content Panel                    |
|  Accounts      |        (switches per navigation)        |
|  Transactions  |                                         |
|  Cards         |                                         |
|  Loans         |                                         |
|  Branches      |                                         |
|                |                                         |
+----------------+-----------------------------------------+
|  Status bar                                              |
+----------------------------------------------------------+
```

### Panels

| Panel | Contents |
|---|---|
| **Dashboard** | Summary stats (customers, accounts, total balance), recent transactions table |
| **Customers** | Searchable JTable, Add / View / Block / Activate actions |
| **Accounts** | Customer selector + account table, Open / Freeze / Close actions |
| **Transactions** | Account selector + history table, Deposit / Withdraw / Transfer dialogs |
| **Cards** | Customer selector + card table, Issue / Block / Activate / Cancel actions |
| **Loans** | Customer selector + loan table, Apply / Repay dialogs |
| **Branches** | Branch information table |

### UI Color Scheme (White-Label Base)

| Element | Color |
|---|---|
| Header / Sidebar | `#0F172A` / `#1E293B` (dark navy) |
| Active nav item | `#3B82F6` (blue) |
| Content background | `#F8FAFC` (light) |
| Accent / Buttons | `#3B82F6` (blue) |
| Success indicators | `#22C55E` (green) |
| Danger / Error | `#EF4444` (red) |
| Table headers | `#334155` |

---

## Sample Customers

The system must be pre-loaded with at least **17 customers** representing diverse international backgrounds:

| # | First Name | Last Name | Nationality | Account Type |
|---|---|---|---|---|
| 1 | James | Anderson | United States | CHECKING + SAVINGS |
| 2 | María | González | Spain | CHECKING |
| 3 | Yuki | Tanaka | Japan | SAVINGS + INVESTMENT |
| 4 | Mohammed | Al-Rashid | Saudi Arabia | BUSINESS |
| 5 | Amara | Osei | Ghana | CHECKING |
| 6 | Lena | Müller | Germany | SAVINGS |
| 7 | Priya | Sharma | India | CHECKING + PERSONAL LOAN |
| 8 | Lucas | Oliveira | Brazil | CHECKING + SAVINGS |
| 9 | Irina | Petrov | Russia | SAVINGS |
| 10 | Chen | Wei | China | INVESTMENT + BUSINESS |
| 11 | Fatima | Benali | Morocco | CHECKING |
| 12 | Erik | Lindström | Sweden | CHECKING + MORTGAGE |
| 13 | Aiko | Nakamura | Japan | SAVINGS |
| 14 | Samuel | Okonkwo | Nigeria | BUSINESS + CHECKING |
| 15 | Sofia | Papadopoulos | Greece | CHECKING + AUTO LOAN |
| 16 | Noam | Cohen | Israel | CHECKING + SAVINGS |
| 17 | Mikhail | Levin | Israel (born Russia) | CHECKING + PERSONAL LOAN |

Each customer must have:
- At least one account with a realistic balance
- At least 5 historical transactions
- At least one card

---

## Enumerations

```java
enum CustomerType    { INDIVIDUAL, CORPORATE }
enum CustomerStatus  { ACTIVE, INACTIVE, BLOCKED }
enum AccountType     { CHECKING, SAVINGS, BUSINESS, INVESTMENT }
enum AccountStatus   { ACTIVE, FROZEN, CLOSED }
enum TransactionType { DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT, FEE }
enum TransactionStatus { PENDING, COMPLETED, FAILED, REVERSED }
enum CardType        { DEBIT, CREDIT, VIRTUAL }
enum CardNetwork     { VISA, MASTERCARD, AMEX }
enum CardStatus      { ACTIVE, BLOCKED, EXPIRED, CANCELLED }
enum LoanType        { PERSONAL, MORTGAGE, AUTO, BUSINESS }
enum LoanStatus      { ACTIVE, PAID_OFF, DEFAULTED, CLOSED }
```

---

## Out of Scope (for this task)

The following are explicitly **excluded** from this implementation:

- ❌ Migration to Java 9 / 11 / 17 / 21
- ❌ Spring Boot, Quarkus, or any framework
- ❌ Database integration (PostgreSQL, MySQL, etc.)
- ❌ REST API / HTTP layer
- ❌ Authentication / JWT / OAuth
- ❌ Real card network integration
- ❌ Regulatory/compliance reporting
- ❌ Real branding (logo, colors) — placeholders only

All of the above are subjects of future modernization tasks.

---

## Deliverables

| Artifact | Description |
|---|---|
| `src/` | Full Java 8 source code |
| `pom.xml` | Maven build file targeting Java 8 |
| `README.md` | How to build and run the system |
| `BankConfig.java` | White-label configuration class |
| Sample data | 17 pre-seeded customers with accounts, transactions, cards, and loans |

---

## Definition of Done

- [ ] All domain entities implemented with proper Java 8 idioms
- [ ] All service layer methods functional and covered with basic validation
- [ ] Console UI navigable with menus for all major banking operations
- [ ] `BankConfig.java` contains all white-label placeholders
- [ ] 17 sample customers pre-loaded at startup
- [ ] Each customer has accounts, transactions, at least one card
- [ ] Customers #7, #12, #15, #17 have pre-seeded loans as specified in the sample data table
- [ ] Custom exception hierarchy implemented under `com.bank.exception`
- [ ] Audit logging functional for key operations
- [ ] Code compiles cleanly with `mvn clean compile` on Java 8
- [ ] `README.md` describes how to build and run the system
