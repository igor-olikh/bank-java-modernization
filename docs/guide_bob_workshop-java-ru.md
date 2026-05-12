# Семинар по IBM Bob для мира Java — руководство участника

## Добро пожаловать

Этот семинар поможет вам познакомиться с **IBM Bob** — AI-инструментом, который помогает Java-разработчикам понимать незнакомый legacy-код, планировать модернизацию с Java 8 на Java 21 и реализовывать её контролируемым образом.

### Чему вы научитесь

- Анализировать незнакомое Java-приложение на Java 8 (legacy-стиль)
- Запрашивать у IBM Bob рекомендации по модернизации на Java 21 и обоснование (что менять, почему и какие фичи Java 21 использовать)
- Вносить изменения в код контролируемым образом
- Понимать многослойные приложения (Swing UI + Service + Repository + Domain)
- Генерировать документацию, диаграммы потоков и конфигурации сборки (Maven `pom.xml`, юнит-тесты)
- Бросать вызов ответам IBM Bob и проверять их

### Длительность семинара

Около **3 часов** (практическая часть, после открывающей сессии и демонстрации).

---

## Часть первая: подготовка

### Чек-лист перед началом семинара

- **IBM Bob IDE** установлен и запущен на вашем компьютере
- Установлены **JDK 8** и **JDK 21** (обе понадобятся: JDK 8 для запуска legacy-версии, JDK 21 — после модернизации); проверьте через `java -version`
- Установлен **Maven 3.6+** (`mvn -v`)
- Установлен **Git-клиент**
- Есть доступ к интернету
- Подготовьте пустую папку (`C:\JavaBob\` для Windows или `~/JavaBob/` для Mac)

### Знакомство со средой IBM Bob IDE

**Основные окна:**

- **Explorer** — файловый проводник (слева)
- **Editor** — редактор кода (по центру)
- **Chat** — чат с IBM Bob
- **Terminal** — командная строка

**Режимы:**

IBM Bob работает в двух режимах:

| Режим | Поведение | Когда использовать |
|---|---|---|
| **PLAN / ASK** | Советует, объясняет, рекомендует — не меняет файлы | Этапы 1–5 |
| **CODE** | Меняет файлы, реально выполняет команды | Этап 6 и далее |

Режим отображается в нижнем углу IDE. Важно проверять его перед каждым этапом.

---

## Часть вторая: практическая работа

## Этап 1 — Подготовительный: загрузка проекта

### Цель

Познакомиться с рабочей средой IBM Bob через практическую задачу — загрузку проекта **`bank-java-modernization`**.

### Полный процесс

**а. Вставьте в чат IBM Bob:**

> Download the project from https://github.com/igor-olikh/bank-java-modernization and load it into IBM Bob.

**б. IBM Bob представит план** и, скорее всего, порекомендует использовать `git client`.

**в. Направьте IBM Bob на прямую загрузку ZIP** в подготовленную вами папку:

> I prefer a direct download of a ZIP file from the site into the folder `C:\JavaBob\` (or `~/JavaBob/` if you are on Mac).

**г. Откройте проект в IDE:**

1. В меню: **File → Open Folder**
2. Выберите созданную папку `bank-java-modernization`
3. Подтвердите открытие

### Ожидаемые результаты

- Локальная копия проекта на вашей машине
- Знакомство с основными окнами IDE
- Первоначальное понимание, что IBM Bob обеспечивает автоматизацию задач вокруг кода, а не только самого кода

> **Обратите внимание:** для загрузки проекта IBM Bob ненадолго переключится в режим **CODE** (потому что нужно записать файлы на диск) — подтвердите переключение. Это не фокус данного этапа, мы вернёмся к нему подробно на этапе 6. Перед переходом ко второму этапу убедитесь, что вернулись в режим **PLAN/ASK**.

> **Совет:** если IBM Bob ничего не делает — он, скорее всего, ждёт от вас ответа. Проверьте чат.

> **Бонус (опционально):** после открытия проекта попросите IBM Bob:
>
> > Build the project with Maven and confirm that the build succeeds.
>
> IBM Bob перейдёт в режим **CODE**, выполнит `mvn clean package` и покажет результат. Это сразу подтверждает, что JDK и Maven у вас настроены правильно. После этого не забудьте вернуться в режим **PLAN/ASK**.

---

## Этап 2 — Анализ

### Цель

Понять, что делает приложение, какие модули существуют и как всё связано — не открывая ни одного Java-файла.

### Этап 2.1 — Общий анализ

**Вставьте в IBM Bob:**

> Analyze the project.

IBM Bob подготовит документ с анализом и объяснением приложения.

**Затем углубитесь следующим промптом:**

> Explain the application at a high level, including business goals, core functions, interfaces, and the system architecture.

### Что вы должны получить

- Резюме приложения для руководства
- Бизнес-цели (управление клиентами, счетами, переводами)
- Основные функции
- Архитектура (Java 8, Swing, Maven, in-memory repositories, MVC pattern)
- Карта основных сервисов и слоёв (`service/`, `repository/`, `model/`, `ui/`)

> **Практический совет:** рекомендуется попросить IBM Bob сохранить анализ в файл, чтобы вы могли вернуться к нему позже:
>
> > Save the analysis to a file `ANALYSIS.md`.

### Откройте созданный файл — нажмите на иконку **open preview**.

### Этап 2.2 — Анализ конкретной операции (денежный перевод между счетами)

**Вставьте в IBM Bob:**

> Explain the money transfer operation, including: which classes are involved, dependencies, data flow, exceptions, UI integration, and persistence.

### Что вы должны получить

IBM Bob предоставит подробный анализ операции **денежного перевода**, который будет включать (среди прочего):

- Идентификацию операции — перевод между двумя счетами одного или разных клиентов
- Главный метод — `TransactionService.transfer(fromAccountId, toAccountId, amount, description)`
- Связанный UI-экран — `TransactionsPanel`
- Список вызываемых сервисов (`AccountService.getAccount`, `AccountService.saveAccount`, `TransactionRepository.save`)
- Зависимости и интерфейсы — модели (`Account`, `Transaction`), репозитории, исключения (`InsufficientFundsException`, `AccountFrozenException`)
- Описание потока — от UI-клика через сервис в репозиторий и обратно

> **Ответ IBM Bob может быть богаче или немного отличаться при каждом запуске.** Это естественно для работы с AI-инструментом. Главное, чтобы ответ покрывал основные темы.

---

## Этап 3 — Запрос на изменение приложения

### Цель

Симулировать ситуацию, когда новый бизнес-запрос приходит к Java-разработчику, и увидеть, как IBM Bob переводит его в рекомендации.

### Бизнес-контекст

Новое регуляторное требование — отчёт о каждом денежном переводе на сумму свыше **9 000 шекелей**, в целях борьбы с отмыванием денег (**AML**).

### Промпт

**Вставьте в IBM Bob:**

> What needs to change in order to report into an external file or external table every money transfer between accounts in an amount above 9,000 NIS? All relevant details of the operation must be reported (such as date, time, amount, source, destination, etc.).

### Что вы должны получить

IBM Bob предоставит:

- Идентификацию правильного класса/метода для изменения: **`TransactionService.transfer(...)`** (файл `src/main/java/com/bank/service/TransactionService.java`)
- Описание необходимого изменения
- Список полей для отчёта (дата, время, сумма, счета, пользователь, терминал и т.д.)

> **Обратите внимание:** IBM Bob иногда даёт больше, чем вы просили — возможно даже сравнение между опциями, возможно даже код. Это нормально — он тщательный.

---

## Этап 4 — Углубление понимания (обоснование рекомендации)

### Цель

Увидеть, что IBM Bob умеет обосновывать свои рекомендации на основе кода. Это превращает его в собеседника для обсуждения, а не просто инструмент, возвращающий ответы.

### Контекст

Аналогично методу `TransactionService.transfer()`, методы `TransactionService.deposit()` и `TransactionService.withdraw()` тоже работают со счетами. Так почему изменение в одном методе, а не в других? IBM Bob должен объяснить, что это потому, что `deposit()` и `withdraw()` обрабатывают только один счёт (зачисление/списание), в то время как `transfer()` обрабатывает перевод между двумя счетами.

### Промпт

**Вставьте в IBM Bob:**

> Explain why the change is needed in method `TransactionService.transfer()` and not in `TransactionService.deposit()` or `TransactionService.withdraw()`.

### Что вы должны получить

| **`transfer()`** | **`deposit()` / `withdraw()`** |
|---|---|
| Обрабатывает перевод между двумя счетами | Обрабатывают один счёт (зачисление или списание) |
| Параметры: `fromAccountId` и `toAccountId` (источник + назначение) | Параметр: `accountId` (один счёт) |
| Соответствует бизнес-требованию AML | Соответствует депозиту/снятию (не требует AML-отчёта) |

IBM Bob покажет **сигнатуры методов** и их **тела** в качестве доказательства (`public Transaction transfer(...)`, `public Transaction deposit(...)`, `public Transaction withdraw(...)`).

---

## Этап 5 — Информация для поддержки принятия решений

### Цель

Улучшить суждение Java-разработчика путём сравнения вариантов реализации до начала кодирования.

### Промпт

**Вставьте в IBM Bob:**

> What is the preferred storage for AML reports — a relational database (JDBC/JPA), a NoSQL store (MongoDB), a structured file (CSV/JSON), or a message queue (Kafka)?

### Что вы должны получить

- Сравнение между использованием **реляционной БД (JDBC/JPA)**, **NoSQL (MongoDB)**, **структурированного файла (CSV/JSON)** и **очереди сообщений (Kafka)**
- Преимущества и недостатки каждого варианта
- Обоснованную рекомендацию IBM Bob — работать с **реляционной БД (например, PostgreSQL через JPA/Hibernate)**, так как требования AML включают аудит, ACID-транзакции и SQL-запросы для проверок

> **Совет:** если IBM Bob уже ответил на это на этапе 3, можно пропустить или задать только конкретный вопрос (например: «Каково влияние на производительность?»).

---

## Этап 6 — Модернизация Java 8 → Java 21 (режим `/java-modernization`)

### Цель

Продемонстрировать специализированный режим IBM Bob для модернизации Java — `/java-modernization`. В этом режиме IBM Bob автоматически анализирует, планирует и выполняет миграцию с Java 8 на Java 21. **Промпты писать не нужно** — вы только просматриваете предложения и подтверждаете действия.

### Контекст

В обычных режимах PLAN/ASK и CODE мы вручную задавали IBM Bob вопросы и инструкции. Но для модернизации Java у IBM Bob есть отдельный, заранее обученный режим — `/java-modernization`. Это правильный способ модернизировать enterprise-приложение, и именно его мы покажем.

### Этап 6.1 — Активация режима

В чате IBM Bob:

1. Наберите `/` — появится выпадающий список **Modes**
2. Выберите **`/java-modernization`** (подпись: *Modernize Java applications*)
3. В нижней-левой части чата появится индикатор активного режима — **Java Modernization**

После активации IBM Bob готов к модернизации. Длинные промпты больше не нужны — режим уже знает, что делать.

### Этап 6.2 — Запуск модернизации

Просто вставьте:

> Modernize this project from Java 8 to Java 21.

IBM Bob автоматически:

- Проанализирует всю кодовую базу
- Составит план модернизации с привязкой «файл → фича Java 21»
- Начнёт применять изменения, шаг за шагом

### Этап 6.3 — Подтверждения (ваша главная роль)

Это ключевой момент: **ваша задача — не писать, а проверять и подтверждать**.

IBM Bob будет периодически останавливаться и просить подтверждение. Типичные диалоги:

- *«Изменить `pom.xml` (source/target 1.8 → 21)?»* → **APPROVE**
- *«Конвертировать `Transaction.java` в record? (60 строк → 8 строк)»* → просмотрите diff → **APPROVE**
- *«Применить Virtual Threads к `TransactionService`?»* → **APPROVE**
- *«Заменить `if/else if` на pattern-matching `switch` в `TransactionMenu`?»* → **APPROVE**

> **Совет:** если хотите видеть детали перед каждым подтверждением, попросите IBM Bob:
>
> > Always show me the exact diff before applying any change.

### Этап 6.4 — Сравнение до/после

После каждого блока изменений сравните оригинал с модернизированной версией:

- В окне **Explorer** правой кнопкой на новой версии файла → **Select for Compare**
- Затем правой кнопкой на оригинале → **Compare with Selected**

Самый яркий момент — `Transaction.java`: ~60 строк boilerplate превращаются в одну строку Record.

### Что произведёт IBM Bob (типичный результат)

- **Обновлённый `pom.xml`** (`<source>21</source>`, `<target>21</target>`, добавлены актуальные плагины)
- **Модели как Records** — `Transaction`, `Address`, простые DTO
- **Sealed-иерархии** для типов транзакций / статусов счетов
- **Pattern-matching `switch`** там, где был длинный `if/else if`
- **Virtual Threads** в `TransactionService` для параллельной обработки
- **Text Blocks** в `DataSeeder` и других местах с длинными строками
- **Modern HttpClient** (если найдёт `HttpURLConnection`)
- **Документ `MODERNIZATION_NOTES.md`** с объяснением каждого изменения и обоснованием выбора фич Java 21

### Этап 6.5 — Сборка и проверка

После завершения модернизации попросите IBM Bob:

> Build the modernized project with Maven using JDK 21 and run it to verify everything works.

IBM Bob выполнит `mvn clean package` на JDK 21 и убедится, что:

- Проект собирается без ошибок
- Юнит-тесты проходят (если они есть)
- Приложение запускается

### Почему это важно

- **Специализированные режимы IBM Bob** — модернизация запускается одной командой, а не серией ручных промптов. У IBM Bob есть готовые режимы под конкретные enterprise-сценарии.
- **Видимый эффект «60 строк → 1 строка»** — Records делают код в разы короче и понятнее.
- **Безопасность изменений** — Sealed Types + Pattern Matching заставляют компилятор ловить забытые случаи. Новые типы транзакций не «провалятся» в `default`.
- **Реальный эффект для производительности** — Virtual Threads позволяют тому же серверу обслуживать в 10–100× больше клиентов.
- **Контролируемость** — несмотря на автоматический режим, каждое изменение проходит через подтверждение разработчика.

Перед переходом к этапу 7 убедитесь, что:

- Все изменения сохранены и одобрены
- Сборка успешна на JDK 21
- Приложение запускается
- IBM Bob возвращён в режим **PLAN/ASK** (или режим по умолчанию)

---

## Этап 7 — Реализация AML-фичи на модернизированном коде

### Цель

Реализовать ту самую AML-фичу, которую мы обсуждали на этапах 3–5, — теперь на современном Java 21. Показать, как новые фичи языка (Records, Virtual Threads) упрощают реализацию новой бизнес-функциональности.

### Контекст

На этапе 3 IBM Bob определил, что AML-проверку нужно добавлять в `TransactionService.transfer()`. На этапе 5 IBM Bob порекомендовал хранить отчёты в реляционной БД. После этапа 6 кодовая база уже на Java 21 — самое время реализовать AML.

Перед началом убедитесь, что IBM Bob вернулся в обычный режим **PLAN/ASK** (не `/java-modernization`).

### Этап 7.1 — План реализации

**Вставьте в IBM Bob:**

> Now that the project is on Java 21, implement the AML reporting feature we discussed earlier: report every transfer above 9,000 NIS to an AML store. Use Java 21 features where appropriate (a Record for the report DTO, Virtual Threads for asynchronous writing).

### Что вы должны получить (PLAN-режим)

- План создания нового сервиса `AmlReportService`
- Новый Record `AmlReport` (с полями date, time, amount, fromAccount, toAccount, user и т.д.)
- Изменения в `TransactionService.transfer()` — проверка порога 9 000 NIS и вызов AML-сервиса
- Подход к хранению (in-memory для демо, JDBC/JPA-готовый интерфейс для production)
- Подход к асинхронной записи через Virtual Threads, чтобы не блокировать основной перевод

### Этап 7.2 — Применение изменений

После того как план одобрен:

> Apply the plan. Show me each diff before approving.

IBM Bob запросит переход в CODE, покажет diff'ы, и по подтверждению применит изменения.

### Этап 7.3 — Опционально: сравнение со «старым» стилем

Если время позволяет, попросите IBM Bob показать, как та же фича выглядела бы в стиле Java 8:

> Show me how this AML feature would have looked if implemented in the original Java 8 style, in a separate file named `AmlReportServiceJava8Style.java`. Highlight the differences.

Это позволит наглядно оценить, **насколько Java 21 делает реализацию короче и безопаснее**.

### Что произведёт IBM Bob

- **`AmlReport`** — Record с типизированными полями (без boilerplate)
- **`AmlReportService`** — сервис с асинхронной записью через Virtual Threads
- **Изменённый `TransactionService.transfer()`** — добавлена проверка порога и вызов `AmlReportService`
- **Юнит-тест** для AML-логики
- **Файл `AML_IMPLEMENTATION.md`** с описанием изменений и обоснованием

### Почему это важно

- **Меньше boilerplate** — Record избавляет от ручного написания геттеров, `equals`, `hashCode`
- **Не блокирует основной поток** — асинхронная запись через Virtual Threads не задерживает сам перевод
- **Готовность к production** — структура сервиса и интерфейс хранилища позволяют легко перейти от in-memory к реальной БД
- **Прослеживаемость** — каждое изменение задокументировано в `AML_IMPLEMENTATION.md`

Перед переходом к этапу 8 убедитесь, что:

- Сборка успешна
- Юнит-тест AML-логики проходит
- IBM Bob возвращён в режим **PLAN/ASK**

---

## Этап 8 — Понимание недокументированных компонентов (BankConfig)

### Цель

Увидеть, что IBM Bob умеет давать **сквозное понимание кодовой базы** — находить все места использования класса или константы, даже когда в самом коде нет документации о его потребителях.

### Промпт

**Откройте новый чат (`+`).**

**Вставьте в IBM Bob:**

> What is the role of the class `BankConfig` and who uses it across the entire codebase?

### Что вы должны получить

IBM Bob объяснит:

- Роль: централизованная конфигурация бренда — имя банка, слоган, телефон поддержки, SWIFT-код, цвета UI
- Как переименовать банк: достаточно изменить **одну константу** `BANK_NAME`, и весь UI автоматически отразит изменение
- Список всех потребителей: `MainWindow.java`, панели в `ui/panels/`, `DataSeeder.java` и т.д. — с конкретными строками

### Почему это важно

В реальных банковских системах часто встречается ситуация: один класс с константами или утилитами используется десятками других классов. Поиск всех мест использования вручную — медленный процесс. IBM Bob делает это за секунды и даёт сквозную картину зависимостей.

### Дополнительное упражнение (рекомендуется): бросаем вызов IBM Bob

IBM Bob иногда говорит вещи, которые он не доказал. Стоит бросить ему вызов:

> Show me the specific code that proves your claims. Actually search in the files.

### Чего ожидать

- IBM Bob исправит себя, если ошибся
- Возможно, обнаружит дополнительные вещи в коде (например: неиспользуемые переменные)

> **Важный урок:** всегда бросайте вызов AI, когда что-то звучит преувеличенно. Инструмент тем мощнее, чем лучше вы умеете требовать доказательств.

---

## Этап 9 — Инфраструктура вокруг приложения (Maven, Docker, CI/CD)

### Цель

Показать ценность IBM Bob для Java-разработчика также в областях, прилегающих к Java, но не касающихся бизнес-логики — сборка (Maven), контейнеризация (Docker), CI/CD (GitHub Actions), миграции БД.

### Контекст

На этапе 2 IBM Bob объяснил, как приложение обрабатывает тему клиентов. После этапа 7 у нас появилась AML-фича, и AML-данные нужно хранить в реальной БД. Теперь зададим практические вопросы о поддержке инфраструктуры вокруг приложения.

> Вернитесь в режим **PLAN**.

### Этап 9.1 — Контейнеризация (Docker)

**Вставьте в IBM Bob:**

> Create a `Dockerfile` for this Java 21 application based on `eclipse-temurin:21-jdk-alpine`. The image should run `bank-system.jar` and expose any necessary ports.

**Что вы должны получить:**

- Готовый `Dockerfile` с многоступенчатой сборкой (build stage + runtime stage)
- Файл `.dockerignore`
- Объяснение каждой строки

### Этап 9.2 — CI/CD (GitHub Actions)

**Вставьте в IBM Bob:**

> Create a GitHub Actions workflow at `.github/workflows/build.yml` that builds the project with JDK 21 and runs the tests on every push.

**Что вы должны получить:**

- Файл `.github/workflows/build.yml` со стандартными шагами: `actions/checkout`, `actions/setup-java`, `mvn verify`
- Триггеры на `push` и `pull_request`

### Этап 9.3 — Миграция БД (Flyway) для AML-таблицы

**Вставьте в IBM Bob:**

> Generate a Flyway migration script `V1__create_aml_reports_table.sql` to create the `aml_reports` table needed by `AmlReportService` from Stage 7. Include appropriate indexes.

**Что вы должны получить:**

- SQL-скрипт `CREATE TABLE aml_reports` с типизированными колонками
- Индексы по дате и сумме (для частых запросов)
- Объяснение схемы и стратегии индексирования

### Почему это важно

IBM Bob владеет всем стеком вокруг Java-приложения: сборка (Maven), контейнеры (Docker), CI (GitHub Actions / Jenkins), БД-миграции, логирование. Разработчику не нужно переключаться между несколькими специалистами — IBM Bob закрывает всю обвязку.

---

## Этап 10 — Дополнительные вопросы

Часть вопросов была показана в открывающем демо сегодня, но также полезно пройти их самостоятельно.

Выбирайте вопросы по времени и интересу:

### Вопрос 10а — Проверки перед открытием счёта

> Which conditions are checked before opening an account, and where is this expressed in the code?

**Что это демонстрирует:** IBM Bob умеет извлекать бизнес-правила из кода.

### Вопрос 10б — Изменение номера счёта

> What is the implication of changing the account number from 8 digits to 10 digits, and what needs to be changed?

**Что это демонстрирует:** IBM Bob умеет отслеживать изменение структуры данных по всему коду.

### Вопрос 10в — Написание JUnit-теста

> Write a JUnit 5 test class for the 'open account' process in `AccountService.openAccount()`. Include both positive and negative test cases.

**Что это демонстрирует:** IBM Bob не только анализирует — он также пишет современный Java-код тестов.

### Вопрос 10г — Описание бизнес-потока

> Describe the business steps of 'money transfer' in the system, including business rules and controls. Provide a link to the code lines.

**Что это демонстрирует:** IBM Bob может перевести код в чёткое бизнес-описание для руководителей.

### Вопрос 10д — Диаграмма потока в Mermaid

> Create a flow diagram in Mermaid style for the money transfer process (showing UI → Service → Repository layers). Save it to a file `TRANSFER_FLOW.md`.

**Что это демонстрирует:** IBM Bob создаёт визуальную документацию.

> **Совет:** чтобы видеть отрисованные Mermaid-диаграммы в IBM Bob preview, установите расширение **Markdown Preview Mermaid Support**.

### Вопрос 10е — Продвинутый вызов: аутентификация T-PIN

> Below is a new business requirement: add an additional identity verification before performing a money transfer in `TransactionService.transfer()`. The verification before the transfer is by a random one-time 6-digit code, the **Transaction PIN (T-PIN)**, which will be shown on the screen and the user is required to enter it. Where does the change need to be made, suggest how to implement the change, and present a proposed code change using Java 21 idioms (e.g. a `record` for the T-PIN challenge).

**Что это демонстрирует:** полное сложное бизнес-требование — от анализа через предложение реализации до кода.

---

## Итоги

### Чему вы научились

- IBM Bob позволяет быстро анализировать незнакомое приложение
- IBM Bob работает между технологиями (Java, SQL, Maven, Docker, CI/CD)
- IBM Bob действует контролируемо — **PLAN**, подтверждение, только потом **CODE**
- IBM Bob может ошибаться — поэтому важно бросать ему вызов и проверять
- IBM Bob также значительно экономит время на задачах, поддерживающих код (документация, инфраструктурные конфиги, скрипты)
- У IBM Bob есть специализированные режимы — например, `/java-modernization` для миграции Java 8 → Java 21

### Пять ключевых советов

**1. Начинайте с PLAN, переходите в CODE только когда это нужно.**
Всегда начинайте в режиме **PLAN/ASK** для получения рекомендаций. Только когда вы уверены, переходите в **CODE** для исполнения.

**2. Сохраняйте анализы в файлы.**
Просите IBM Bob: «Save the analysis to file `XYZ.md`». Это позволяет вернуться к ним позже.

**3. Бросайте вызов недоказанным утверждениям.**
Если IBM Bob говорит что-то, выглядящее безосновательным: «Show me the code that proves this. If there is none — state that explicitly.»

**4. Давайте файлам явные имена.**
В промптах: «Save this to file `XYZ.md`», а не просто «Save this». Это предотвращает ошибки.

**5. Используйте специализированные режимы IBM Bob.**
Для модернизации Java-приложений активируйте режим `/java-modernization` — он сам знает best practices миграции на Java 21. Длинные промпты не нужны.

### Дополнительная помощь

- **Эксперт по IBM Bob** — для работы с инструментом
- **Эксперт по модернизации Java** — для вопросов про переход на Java 21
- **Эксперт по Java/Spring** — для технических вопросов по Java

**Удачи на семинаре!**
