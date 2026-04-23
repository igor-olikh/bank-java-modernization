# General: Java Version Overview

## Introduction

This document describes the key differences between Java versions relevant to this project — specifically the transition from **Java 8** (the legacy baseline) to **Java 21** (the modern Liberty target).

---

## Java 8 — The Legacy Standard

Java 8, released in **March 2014**, was one of the most widely adopted versions of Java in enterprise environments. It introduced significant language features that made Java more expressive and productive:

- **Lambda Expressions** — enabled functional-style programming
- **Stream API** — declarative data processing pipelines
- **Optional** — null-safe container type
- **Date/Time API (`java.time`)** — modern replacement for `java.util.Date`
- **Default methods in interfaces** — backward-compatible interface evolution

Despite its widespread adoption, Java 8 reached its **End of Public Updates** for commercial use in January 2019 (Oracle). It is now considered **outdated** in terms of:

- Security patches (no free long-term support for most vendors)
- Performance optimizations
- Modern language features
- Cloud-native and containerization capabilities

Many enterprise banking systems were built on Java 8 and have remained on it due to the cost and complexity of migration.

---

## Java 21 — The Modern Liberty Standard

**Java 21**, released in **September 2023**, is a **Long-Term Support (LTS)** release and represents the current gold standard for production Java applications. In the context of this project, it is referred to as the **"Liberty"** version — reflecting the Open Liberty application server ecosystem and the freedom from legacy constraints.

Java 21 introduces a wide range of improvements across language design, performance, and cloud-readiness:

### Language Features

| Feature | Description |
|---|---|
| **Virtual Threads (Project Loom)** | Lightweight threads that dramatically improve concurrency and throughput without complex async code |
| **Sealed Classes** | Restrict which classes can extend or implement a type — improves domain modeling |
| **Pattern Matching for `switch`** | Expressive and concise type-based branching |
| **Record Classes** | Immutable data carriers with auto-generated constructors, getters, `equals`, `hashCode`, and `toString` |
| **Text Blocks** | Multi-line string literals for SQL, JSON, HTML — improves readability |
| **Sequenced Collections** | Unified API for ordered collections with defined first/last element access |

### Performance & JVM Improvements

- **Generational ZGC** — Low-latency garbage collector with generational support, dramatically reducing GC pause times
- **G1GC enhancements** — Improved throughput for standard workloads
- **JIT compiler improvements** — Faster startup and warmer performance curves, critical for containerized microservices
- **Ahead-of-Time (AOT) compilation readiness** — Better integration with GraalVM native image

### Cloud-Native & Modernization Benefits

- Native support for **containerization** (Docker, Kubernetes)
- Smaller **memory footprint** compared to Java 8 under equivalent workloads
- Improved **startup time** — critical for serverless and cloud-run environments
- Full compatibility with modern frameworks: **Spring Boot 3.x**, **Quarkus**, **Open Liberty**
- Long-term vendor support through **2029+** (Oracle, Microsoft, Adoptium, Bank Semeru)

---

## Java 8 vs Java 21 — Side-by-Side Comparison

| Dimension | Java 8 (Legacy) | Java 21 (Liberty) |
|---|---|---|
| Release Year | 2014 | 2023 |
| LTS Status | End of free support | Active LTS (until 2029+) |
| Concurrency | Thread-per-request (heavyweight) | Virtual Threads (lightweight, scalable) |
| Language Style | Verbose, boilerplate-heavy | Concise, expressive (records, patterns) |
| Garbage Collection | CMS / G1GC (basic) | Generational ZGC, enhanced G1GC |
| Cloud Readiness | Limited | Full native support |
| Security | No free public updates | Actively patched |
| Framework Support | Spring Boot 2.x max | Spring Boot 3.x, Quarkus, Open Liberty |
| Container Efficiency | Poor | Optimized |

---

## Why This Migration Matters

Migrating from Java 8 to Java 21 in a banking context is not just a technical upgrade — it is a **strategic investment** in:

1. **Security**: Eliminating known vulnerabilities in end-of-life runtimes
2. **Scalability**: Virtual threads allow handling thousands of concurrent banking transactions with minimal resource overhead
3. **Maintainability**: Modern language features reduce boilerplate and improve code readability
4. **Compliance**: Modern runtimes better support audit, observability, and regulatory tooling
5. **Cost Efficiency**: Better JVM performance reduces cloud infrastructure costs

---

## Summary

| | Java 8 | Java 21 (Liberty) |
|---|---|---|
| Status | ⚠️ Legacy / End-of-Life | ✅ Active LTS |
| Recommended for new projects | ❌ No | ✅ Yes |
| Modernization target | — | ✅ This project's goal |

Java 21 is the foundation for building a resilient, secure, and scalable banking application aligned with modern software engineering standards.
