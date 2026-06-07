# Project-local IBM Bob Skills

Custom skills versioned with this repository. Each subdirectory is a standalone Bob extension (skill).

## Enable after cloning

For every skill in this directory, link it into your local Bob:

```bash
bob extensions link "$(pwd)/.bob-skills/<skill-name>"
```

Example:

```bash
bob extensions link "$(pwd)/.bob-skills/java-version-compliance"
```

Once linked, any edit to a skill's `SKILL.md` is immediately reflected — no reinstall needed.

## List installed skills

```bash
bob extensions list
```

## Available skills

| Skill | Purpose |
|---|---|
| `java-version-compliance` | Verify the project's declared target Java version is actually enforced by the build and matches the APIs used in source. |

## Authoring a new skill

A skill is a directory with:

- `bob-extension.json` — manifest (`name`, `version`, `description`, `contextFileName`)
- `SKILL.md` — the instructions Bob loads into context

Validate before linking:

```bash
bob extensions validate "$(pwd)/.bob-skills/<new-skill>"
```
