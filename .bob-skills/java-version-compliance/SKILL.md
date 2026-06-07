---
name: java-version-compliance
description: >
  Verify that a Java project's declared target Java version is actually enforced by the
  build and matches the APIs and bytecode used in the source. Catches the classic
  source/target vs --release trap where code compiles and runs on a newer JDK but uses
  APIs that do not exist in the declared target version (e.g. String.repeat(), List.of(),
  HttpClient) and would fail at runtime on a real older JRE. Triggers on: "is this Java
  project really Java 8 / 11 / 17 / 21 compatible?", "check Java compatibility", "verify
  Java version", "audit Java target", "why does this compile but break on production?",
  pom.xml or build.gradle changes that touch maven.compiler.source/target/release or
  sourceCompatibility/targetCompatibility, or any time the project's declared Java
  version differs from the active JDK used to build/run it.
metadata:
  enabled: true
---

# Java Version Compliance Check

Authoritative procedure to confirm that a Java project's declared target version is **actually enforced** by the build and that all source code, dependencies, and compiled bytecode are valid for that version. Use whenever there is any doubt that "the project says Java N, but does it really compile and run cleanly on Java N?"

> **Scope:** Java projects only — Maven (`pom.xml`) or Gradle (`build.gradle` / `build.gradle.kts`). Skip if no Java build descriptor is present.

---

## 1. The trap this skill exists to catch

Setting `<maven.compiler.source>` / `<maven.compiler.target>` (or Gradle `sourceCompatibility` / `targetCompatibility`) **only** restricts language features and bytecode version. It does **not** restrict the JDK API. When the build runs on a newer JDK (say JDK 21) with `source/target = 8`:

- `String.repeat(int)`, `List.of(...)`, `Files.readString(...)`, `HttpClient`, `Optional.isEmpty()`, `Map.entry()`, and many other post-Java-8 APIs **compile silently**.
- `javac` emits the harmless-looking warning `bootstrap class path not set in conjunction with -source N` — which is the compiler literally saying "I am NOT verifying against the real Java N library."
- The resulting `.class` files have the correct major version, so they appear compliant.
- On a real Java 8 JRE the program crashes with `NoSuchMethodError` at runtime.

The **only** way to make `javac` enforce the declared API level is the `--release` flag (Maven `<maven.compiler.release>`, Gradle `options.release`). This skill verifies that, plus several other compliance dimensions.

---

## 2. Procedure — run all steps; report each result

### Step 1 — Detect build descriptor

- If `pom.xml` exists → Maven flow.
- If `build.gradle` or `build.gradle.kts` exists → Gradle flow.
- If both → run both; report each separately.
- If neither → tell the user no Java build descriptor was found and stop.

### Step 2 — Extract the declared target Java version

**Maven (`pom.xml`):**
```bash
grep -nE "<(maven.compiler.release|maven.compiler.source|maven.compiler.target|java.version)>" pom.xml
```
Look for:
- `<maven.compiler.release>` — the strict flag. ✅ enforces API.
- `<maven.compiler.source>` and `<maven.compiler.target>` — language/bytecode level only. ⚠ does NOT enforce API.
- `<java.version>` or a `<properties>` indirection — resolve which compiler property actually receives it.
- A `<plugin>` block for `maven-compiler-plugin` may override these via `<configuration><release>` / `<source>` / `<target>`.

**Gradle (`build.gradle[.kts]`):**
```bash
grep -nE "(sourceCompatibility|targetCompatibility|options\.release|languageVersion|JavaLanguageVersion|toolchain)" build.gradle build.gradle.kts 2>/dev/null
```
Look for:
- `options.release.set(N)` or `java { toolchain { languageVersion = JavaLanguageVersion.of(N) } }` with `release` configured → ✅ enforces API.
- Plain `sourceCompatibility = '1.8'` / `targetCompatibility = '1.8'` → ⚠ does NOT enforce API.

### Step 3 — Detect the active JDK

```bash
java -version 2>&1
javac -version 2>&1
echo "JAVA_HOME=$JAVA_HOME"
```
Maven: also `mvn -v` (it prints which Java it found). Gradle: `gradle -v` or `./gradlew -v`.

Identify the **major version** of the running JDK (e.g. 21).

### Step 4 — Diagnose the enforcement mode

| Declared in build | Active JDK | Verdict |
|---|---|---|
| `release=N` | any JDK ≥ N | ✅ Strict — API is enforced |
| `source/target=N` only | JDK == N | ✅ De facto strict — only Java N classes available |
| `source/target=N` only | JDK > N | ⚠ **TRAP** — newer JDK APIs compile silently |
| `source/target=N` only | JDK < N | ❌ Build will fail |
| Both `source/target=N` and `release=N` | any JDK ≥ N | ✅ Strict (release wins; redundant) |

If verdict is ⚠ TRAP — flag it loudly. This is the central case this skill catches.

### Step 5 — Empirically verify by recompiling under `--release`

Best evidence beats opinions. Force a clean build with strict API enforcement:

**Maven:**
```bash
mvn -B clean compile -Dmaven.compiler.release=<N>
```
or temporarily edit `pom.xml` to add `<maven.compiler.release>N</maven.compiler.release>` and run `mvn -B clean compile`.

**Gradle:**
```bash
./gradlew clean compileJava -Dorg.gradle.java.home=$JAVA_HOME --warning-mode all
```
or add `options.release.set(N)` to `tasks.withType(JavaCompile)` and rebuild.

If new errors appear that were not there with `source/target` alone, **those are real Java-N compliance violations** — list them.

Common culprits to grep for proactively (Java 8 targets):
```bash
grep -rnE "\.repeat\(|\.isEmpty\(\)|List\.of\(|Map\.of\(|Set\.of\(|Files\.readString|Files\.writeString|HttpClient\.newHttpClient|Optional\.or\(|Map\.entry\(" --include="*.java" .
```
(Adjust the regex for the declared target; e.g. for Java 11 targets, scan for Java 17+ APIs like `Stream.toList()`, sealed types, records, pattern matching.)

### Step 6 — Verify compiled bytecode major version

```bash
find target/classes build/classes -name "*.class" 2>/dev/null | head -3 | xargs -I{} javap -v "{}" | grep -E "major version" | head -3
```
Map:
- 52 → Java 8
- 55 → Java 11
- 61 → Java 17
- 65 → Java 21

Confirm the byte­code major version matches the declared target. A mismatch means the build is producing a different version than configured.

### Step 7 — Check that test sources are held to the same bar

Often forgotten. `pom.xml` may configure `<testSource>`/`<testTarget>` (Maven) or `compileTestJava` (Gradle) separately. Verify they match the main target — and apply `release` there too.

### Step 8 — Check dependencies (multi-release-jar caveat)

If any dependency is shipped as a multi-release JAR or requires a newer Java, the *project* still compiles, but a real older JRE may fail at class load. Quick check:
```bash
mvn dependency:tree 2>/dev/null | head -50
unzip -p <jar> META-INF/MANIFEST.MF | grep -i "Multi-Release"
```
Flag dependencies that declare `Multi-Release: true` or require a newer `Bundle-RequiredExecutionEnvironment` than the project target.

---

## 3. Report format — what to give the user

Always produce a structured, scannable report. Example:

```
Java Version Compliance Report — <project>

Declared target version: 8  (pom.xml: maven.compiler.source/target = 1.8)
Active JDK              : 21 (Eclipse Temurin / IBM Semeru / Zulu)
Enforcement mode        : ⚠ TRAP — source/target only, JDK > target

Trap impact:
  Newer APIs compiled silently because --release is not set.
  ConsoleUI.java:93   — uses String.repeat(int) (Java 11+ API)
  AccountService.java:142 — uses List.of(...) (Java 9+ API)

Empirical recompile with --release 8:
  ❌ ConsoleUI.java:93  cannot find symbol: method repeat(int)
  ❌ AccountService.java:142 cannot find symbol: method of(...)

Bytecode check:
  target/classes/.../ConsoleUI.class → major version 52 ✅ (matches target)

Test sources:
  Same source/target=1.8 — test classes share the same trap.

Dependencies (multi-release):
  none flagged

Recommended fixes:
  1. Replace pom.xml source/target with:
       <maven.compiler.release>8</maven.compiler.release>
     This makes javac enforce the Java 8 API.
  2. Refactor offending sites — drop in Java 8-compatible idioms:
       String.repeat(n)         → StringBuilder loop, or new String(new char[n]).replace('\0', ' ')
       List.of(a, b, c)         → Collections.unmodifiableList(Arrays.asList(a, b, c))
       Stream.toList()          → .collect(Collectors.toList())
       Files.readString(path)   → new String(Files.readAllBytes(path), StandardCharsets.UTF_8)
       Optional.isEmpty()       → !optional.isPresent()
  3. After both fixes, mvn clean compile must succeed under --release N without warnings.
```

---

## 4. When to STOP and ask the user

Per AGENTS.md — do not assume. Ask the user before acting if:

- The project declares **no** Java version anywhere. (Default is JDK-dependent — ask: "What Java version should this project target?")
- The declared target differs from the active JDK and you cannot tell the intent (modernize ↔ stay on legacy). Ask: "Do you want to (a) modernize the project to the active JDK, or (b) constrain the build to the original target version?"
- The user asks for a fix but multiple offending APIs exist with different reasonable rewrites — present options before refactoring widely.

---

## 5. Quick one-liner audit (for the impatient)

```bash
echo "=== declared ==="; grep -nE "<(maven.compiler.release|maven.compiler.source|maven.compiler.target)>|sourceCompatibility|targetCompatibility|options\.release" pom.xml build.gradle build.gradle.kts 2>/dev/null
echo "=== active ==="; java -version 2>&1
echo "=== strict recompile ==="; mvn -B -q clean compile -Dmaven.compiler.release=$(grep -oE "[0-9]+" pom.xml | head -1) 2>&1 | tail -20
echo "=== bytecode ==="; find target/classes build/classes -name "*.class" 2>/dev/null | head -1 | xargs -I{} javap -v "{}" 2>/dev/null | grep "major version"
```

If the strict recompile fails where the normal build succeeded — that is the report. The project is **not** actually compliant with its declared target.
