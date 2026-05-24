# AGENTS.md — Project Agent Instructions

## Language Policy

**All documentation, comments, commit messages, code descriptions, and written content within this project must be in English — regardless of the language used by the user during any conversation.**

This rule applies to:
- All files under `docs/`
- README and markdown files
- Inline code comments
- Commit messages
- Pull request titles and descriptions
- Any agent-generated output within this repository

No exceptions are made based on the user's spoken or written language during a session.

---

## Critical Directive — Do Not Assume; Verify or Ask

**You, the agent, must never assume what the user wants when there is any doubt.**

- Verify the facts before acting. Check the actual state of the code, files, or configuration instead of guessing.
- If you cannot confirm something with 100% certainty, stop and ask the user a clarifying question rather than proceeding on an assumption.
- Do not infer specifics the user did not state. For example, if the user says *"I want to upgrade the Java version"*, do **not** automatically choose Java 21, Java 23, or any other version — ask the user which exact Java version they want to target before making any change.

This directive takes precedence over the urge to act quickly. When in doubt, ask.

---

## Project Overview

This project focuses on the modernization of a Java-based banking application — migrating from legacy Java 8 to modern Java 21 (Liberty).

---

## Agent Behavior Guidelines

1. **Language**: Always write in English.
2. **File naming**: Use kebab-case for all file and directory names (e.g., `general.md`, `api-guide.md`).
3. **Documentation**: Store all documentation in the `docs/` directory.
4. **Commits**: Write clear, imperative-mood commit messages in English (e.g., `Add Java version comparison guide`).
5. **Code style**: Follow Java best practices aligned with the target Java 21 standard.
6. **Do not assume — verify or ask**: Follow the **Critical Directive** above. When uncertain, verify the facts or ask the user before acting.
