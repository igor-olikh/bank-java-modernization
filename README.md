# Banking System — Java 8 Implementation

## Overview

A complete banking system built on **Java 8**, covering the full domain:
customers, accounts, transactions, cards, loans, and branches.
Includes 17 pre-loaded international customers and a white-label console UI.

## Prerequisites

- Java 8 (JDK 1.8+)
- Apache Maven 3.6+

### Install on macOS (Homebrew)

```bash
brew install openjdk@8
brew install maven
```

After installing, add Java 8 to your PATH if needed:
```bash
export JAVA_HOME=$(/usr/libexec/java_home -v 1.8)
export PATH="$JAVA_HOME/bin:$PATH"
```

## Build

```bash
mvn clean package
```

This produces `target/bank-system.jar`.

## Run

```bash
java -jar target/bank-system.jar
```

## Project Structure

```
src/main/java/com/bank/
├── BankApplication.java       # Entry point
├── config/
│   └── BankConfig.java        # White-label placeholders
├── model/
│   ├── enums/                 # All domain enumerations
│   ├── Customer.java
│   ├── Account.java
│   ├── Transaction.java
│   ├── Card.java
│   ├── Loan.java
│   └── Branch.java
├── repository/                # In-memory ConcurrentHashMap stores
├── service/                   # Business logic layer
├── data/
│   └── DataSeeder.java        # 17 pre-loaded customers
└── ui/                        # Console menus
```

## White-Label Configuration

Edit `src/main/java/com/bank/config/BankConfig.java` to apply branding:

```java
public static final String BANK_NAME   = "My Bank";
public static final String BANK_SLOGAN = "Banking for Everyone";
```

## Pre-loaded Customers

17 customers from 15 countries including USA, Spain, Japan, Saudi Arabia,
Ghana, Germany, India, Brazil, Russia, China, Morocco, Sweden, Nigeria,
Greece, and Israel (including one Russian-born Israeli customer).
