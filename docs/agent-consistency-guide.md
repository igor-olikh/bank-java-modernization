# Agent Consistency Guide — Ensuring Deterministic Responses

## Overview

This guide explains how to configure AI assistants (like Bob) to provide consistent, deterministic answers to the same questions across multiple interactions.

---

## Key Principles for Consistent Responses

### 1. **Use the AGENTS.md File**

The `AGENTS.md` file in your project root serves as the **primary instruction set** for AI agents. This file:

- Defines project-specific rules and guidelines
- Sets language preferences and formatting standards
- Establishes behavioral patterns
- Provides context about the project

**Current Configuration:**
```markdown
# AGENTS.md — Project Agent Instructions

## Language Policy
All documentation must be in English

## Project Overview
Java banking application modernization (Java 8 → Java 21)

## Agent Behavior Guidelines
1. Language: Always write in English
2. File naming: Use kebab-case
3. Documentation: Store in docs/ directory
4. Commits: Clear, imperative-mood messages
5. Code style: Java 21 best practices
```

---

## Methods to Ensure Consistency

### Method 1: Explicit Instructions in AGENTS.md

Add specific response patterns to `AGENTS.md`:

```markdown
## Standard Responses

### When asked about Java version:
"This project uses Java 21 (upgraded from Java 8). See docs/java-21-upgrade.md for details."

### When asked about build tool:
"Maven 3.6+ is used. Build with: mvn clean package"

### When asked about running the application:
"Run with: java -jar target/bank-system.jar"
```

### Method 2: Create a FAQ Document

Create a dedicated FAQ file with standardized answers:

```markdown
# docs/faq.md

## Frequently Asked Questions

**Q: What Java version does this project use?**
A: Java 21 (upgraded from Java 8 in 2024)

**Q: How do I build the project?**
A: Run `mvn clean package`

**Q: How do I run the application?**
A: Run `java -jar target/bank-system.jar`
```

### Method 3: Use Configuration Files

Create a `.bob-config.json` or similar configuration:

```json
{
  "project": {
    "name": "Nova Bank",
    "java_version": "21",
    "build_tool": "Maven 3.6+",
    "architecture": "MVC"
  },
  "standard_responses": {
    "java_version": "This project uses Java 21 (upgraded from Java 8)",
    "build_command": "mvn clean package",
    "run_command": "java -jar target/bank-system.jar"
  }
}
```

### Method 4: Documentation-First Approach

Maintain comprehensive documentation that agents reference:

```
docs/
├── general.md           # Project overview
├── java-21-upgrade.md   # Migration details
├── benefits.md          # Java 21 benefits
├── faq.md              # Standard Q&A
└── agent-consistency-guide.md  # This file
```

---

## Best Practices

### 1. **Single Source of Truth**

- Keep all factual information in documentation
- Reference docs instead of generating new answers
- Update docs when facts change

### 2. **Structured Information**

```markdown
## Project Facts (Always Reference These)

| Question | Answer | Source |
|----------|--------|--------|
| Java Version | Java 21 | pom.xml, README.md |
| Build Tool | Maven 3.6+ | pom.xml |
| UI Framework | Java Swing | README.md |
| Architecture | MVC | README.md |
```

### 3. **Version Control**

- Commit AGENTS.md changes
- Track documentation updates
- Use semantic versioning for major changes

### 4. **Clear Hierarchies**

Priority order for information sources:
1. `AGENTS.md` (project rules)
2. `README.md` (project overview)
3. `docs/*.md` (detailed documentation)
4. Code comments
5. Inferred from code

---

## Implementation Example

### Enhanced AGENTS.md Structure

```markdown
# AGENTS.md — Project Agent Instructions

## Language Policy
[Existing content...]

## Project Overview
[Existing content...]

## Standard Responses

### Technical Stack
**Question:** "What Java version is used?"
**Answer:** "Java 21 (upgraded from Java 8). See docs/java-21-upgrade.md"

**Question:** "How to build?"
**Answer:** "mvn clean package"

**Question:** "How to run?"
**Answer:** "java -jar target/bank-system.jar"

### Architecture
**Question:** "What is the architecture?"
**Answer:** "MVC (Model-View-Controller) with Swing UI, in-memory repositories"

### Data
**Question:** "How many customers are pre-seeded?"
**Answer:** "18 international customers with accounts, cards, loans, and transactions"

## Response Guidelines

1. **Always reference documentation** when answering factual questions
2. **Use exact commands** from QUICKSTART.md for build/run instructions
3. **Cite specific files** when providing technical details
4. **Maintain consistency** with README.md project description
```

---

## Testing Consistency

### Verification Checklist

Test the same question multiple times:

```bash
# Test 1: Ask "What Java version?"
Expected: "Java 21 (upgraded from Java 8)"

# Test 2: Ask "How to build?"
Expected: "mvn clean package"

# Test 3: Ask "How many customers?"
Expected: "18 pre-seeded customers"
```

### Consistency Metrics

- ✅ **Exact match:** Same wording every time
- ✅ **Semantic match:** Same meaning, slight variation
- ❌ **Inconsistent:** Different facts or contradictions

---

## Advanced Techniques

### 1. **Template Responses**

Create response templates in AGENTS.md:

```markdown
## Response Templates

### Build Instructions Template
"To build: `mvn clean package`
To run: `java -jar target/bank-system.jar`
See QUICKSTART.md for details."

### Version Information Template
"Java: 21
Maven: 3.6+
Framework: Swing
See README.md for full stack."
```

### 2. **Context Anchoring**

Reference specific files for context:

```markdown
## Context Sources

- Technical specs → README.md
- Build/run → QUICKSTART.md
- Migration → docs/java-21-upgrade.md
- Benefits → docs/benefits.md
```

### 3. **Validation Rules**

Add validation rules to AGENTS.md:

```markdown
## Validation Rules

Before answering:
1. Check if answer exists in documentation
2. Reference the source file
3. Use exact terminology from docs
4. Avoid generating new facts
```

---

## Common Pitfalls

### ❌ Avoid

1. **Generating new information** not in docs
2. **Paraphrasing** when exact quotes are needed
3. **Assuming** facts without verification
4. **Contradicting** existing documentation

### ✅ Do

1. **Reference** documentation explicitly
2. **Quote** exact commands and versions
3. **Cite** source files
4. **Update** docs when facts change

---

## Maintenance

### Regular Updates

1. **Review AGENTS.md** monthly
2. **Update standard responses** when project changes
3. **Sync with README.md** and other docs
4. **Test consistency** after major changes

### Change Log

Track changes to standard responses:

```markdown
## AGENTS.md Change Log

### 2026-05-07
- Added standard responses section
- Defined Java version answer
- Established build command format

### 2026-04-15
- Initial AGENTS.md creation
- Set language policy
- Defined project overview
```

---

## Summary

To ensure Bob provides consistent answers:

1. ✅ **Maintain AGENTS.md** with standard responses
2. ✅ **Create comprehensive documentation** (README, QUICKSTART, FAQs)
3. ✅ **Reference docs** instead of generating answers
4. ✅ **Use exact terminology** from project files
5. ✅ **Test consistency** regularly
6. ✅ **Update docs** when facts change

**Key Principle:** Documentation is the single source of truth. Agents should reference it, not recreate it.

---

## Next Steps

1. Review and enhance your `AGENTS.md` file
2. Create a `docs/faq.md` with common questions
3. Add standard responses to AGENTS.md
4. Test consistency across multiple interactions
5. Maintain documentation as project evolves