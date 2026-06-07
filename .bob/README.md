# Project-local IBM Bob Configuration

Files in this directory configure IBM Bob's behavior for this specific project. They are versioned in the repository so the same setup applies for every contributor.

## `custom_modes.yaml`

Project-specific chat modes that appear in Bob's `/` menu when this project is open. They complement the global modes defined in `~/.bob/settings/custom_modes.yaml`.

Bob loads `.bob/custom_modes.yaml` automatically when it opens the workspace — no manual action required after `git clone`.

### Modes defined here

| Slug | Purpose |
|---|---|
| `/java-version-check` | Audit whether the project's declared target Java version is actually enforced by the build, and whether the source uses only APIs valid for that version. Pairs with the `java-version-compliance` skill in `.bob-skills/`. |

## Related

- Project-local **skills** (background instructions Bob loads into context automatically) live in `../.bob-skills/`.
- Global Bob settings stay at `~/.bob/settings/`.
