# Преимущества перехода с Java 8 на Java 21

Этот документ простым языком объясняет, что можно делать в **Java 21** и что было трудно или невозможно в **Java 8**, на примерах нашей **банковской системы**.

Каждая секция построена по одной схеме:

- **Проблема в Java 8** — что было больно
- **Решение в Java 21** — как становится проще
- **Банковский пример** — код из нашего домена
- **Почему это важно банку** — бизнес-ценность

---

## 1. Virtual Threads — Обслуживаем больше клиентов одновременно

### Проблема в Java 8

Когда банк обрабатывает платежи, переводы или проверки на мошенничество, каждый запрос обычно выполняется в отдельном **потоке (thread)**. В Java 8 поток — тяжёлый объект: он занимает около **1 МБ памяти**, и операционная система может одновременно работать только с несколькими тысячами таких потоков.

Поэтому, если нужно обслужить **10 000 клиентов**, переводящих деньги одновременно, нельзя просто создать 10 000 потоков. Приходится использовать **пул потоков** (например, 200 потоков), а всё остальное ставить в очередь. Когда поток ждёт ответа от базы данных, он просто стоит и ничего не делает — но всё равно занимает свой 1 МБ памяти.

Именно поэтому банкам на Java 8 часто приходится добавлять **новые серверы** в Чёрную пятницу или в день зарплаты: каждый сервер ограничен в количестве одновременных операций.

### Решение в Java 21

Java 21 приносит **Virtual Threads**. Они выглядят и ведут себя точно так же, как обычные потоки, но они **почти бесплатные**: около 1 КБ памяти вместо 1 МБ. На одном сервере можно создать **сотни тысяч** таких потоков.

Когда виртуальный поток чего-то ждёт (ответа базы, внешнего API, сети), он уходит с дороги и даёт работать другим. Когда ответ приходит — продолжает с того же места.

### Банковский пример

**Java 8 — пул потоков, требует тщательной настройки:**

```java
ExecutorService executor = Executors.newFixedThreadPool(200);

for (TransferRequest req : todaysSalaryPayments) {
    executor.submit(() -> transactionService.transfer(
        req.fromAccount(), req.toAccount(), req.amount(), "Salary"
    ));
}
```

Если в 9:00 утра приходит 50 000 зарплатных платежей, 49 800 из них стоят в очереди.

**Java 21 — один виртуальный поток на каждый платёж:**

```java
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    for (TransferRequest req : todaysSalaryPayments) {
        executor.submit(() -> transactionService.transfer(
            req.fromAccount(), req.toAccount(), req.amount(), "Salary"
        ));
    }
}
```

Все 50 000 платежей обрабатываются одновременно. На том же сервере.

### Почему это важно банку

- **Меньше затрат на железо.** Та же машина обслуживает в **10–100 раз больше клиентов**.
- **Лучший пользовательский опыт.** Никаких «подождите» в часы пик.
- **Проще код.** Разработчики пишут логику сверху вниз, а не сложные цепочки колбэков.

---

## 2. Records — Меньше кода, меньше багов

### Проблема в Java 8

Банк работает с множеством мелких объектов: `Transaction`, `Address`, `CustomerSummary`, `Receipt`. В Java 8 каждый такой класс требует:

- Конструктор
- Геттер на каждое поле
- `equals()` и `hashCode()`
- `toString()` для логов
- Иногда `Builder`

Для класса с 8 полями получается легко **80 строк повторяющегося кода**. Каждая строка — это место, где разработчик может опечататься, забыть поле в `equals()` или скопировать неправильный геттер.

### Решение в Java 21

**Record** — это однострочное объявление, которое автоматически даёт всё перечисленное выше. Шаблонный код пишет компилятор; разработчик пишет только смысл.

### Банковский пример

**Java 8 — наш `Transaction` около 60 строк:**

```java
public class Transaction {
    private final String id;
    private final String fromAccountId;
    private final String toAccountId;
    private final BigDecimal amount;
    private final String currency;
    private final TransactionType type;

    public Transaction(String id, String from, String to,
                       BigDecimal amount, String currency, TransactionType type) {
        this.id = id;
        this.fromAccountId = from;
        // ...и так далее на каждое поле
    }

    public String getId() { return id; }
    public String getFromAccountId() { return fromAccountId; }
    // ...один геттер на каждое поле

    @Override
    public boolean equals(Object o) { /* 15 строк */ }

    @Override
    public int hashCode() { /* 5 строк */ }

    @Override
    public String toString() { /* 5 строк */ }
}
```

**Java 21 — то же самое одной строкой:**

```java
public record Transaction(
    String id,
    String fromAccountId,
    String toAccountId,
    BigDecimal amount,
    String currency,
    TransactionType type
) {}
```

Компилятор сам создаёт конструктор, геттеры, `equals`, `hashCode` и `toString`.

### Почему это важно банку

- **Меньше багов.** Меньше написанного руками кода — меньше ошибок в объектах, связанных с деньгами.
- **Быстрее разработка.** Новый «Receipt» или «StatementLine» создаётся за секунды.
- **Проще ревью кода.** Ревьюер видит, что уникального в классе, а не пробегается по 60 строкам шаблонного кода.

---

## 3. Pattern Matching с Sealed-типами — Компилятор ловит забытые случаи

### Проблема в Java 8

Банковский софт полон логики «выбери действие в зависимости от типа». Например, отображение транзакции:

- `DEPOSIT` → «Зачисление»
- `WITHDRAWAL` → «Списание»
- `TRANSFER` → «Отправлено на другой счёт»
- `FEE` → «Комиссия банка»

В Java 8 это пишется через `if`/`else if` или `switch`. Большая проблема: **если разработчик позже добавляет новый тип транзакции** (например, `CHARGEBACK`), ничто не напоминает ему обновить каждый `switch` в кодовой базе. Новый тип молча проваливается в `default`, и банк показывает клиенту неправильную информацию.

### Решение в Java 21

**Sealed-типы** позволяют сказать компилятору: «Вот единственно возможные виды Transaction». В сочетании с **pattern-matching `switch`** компилятор теперь **отказывается компилировать код**, если хотя бы один случай не обработан.

### Банковский пример

**Java 8 — легко забыть случай:**

```java
String describe(Transaction t) {
    if (t.getType() == TransactionType.DEPOSIT) {
        return "Money received: " + t.getAmount();
    } else if (t.getType() == TransactionType.WITHDRAWAL) {
        return "Money withdrawn: " + t.getAmount();
    } else if (t.getType() == TransactionType.TRANSFER) {
        return "Transfer to " + t.getToAccountId();
    }
    // А что с FEE? CHARGEBACK? Ничто не напомнит.
    return "Unknown";
}
```

**Java 21 — компилятор нас защищает:**

```java
sealed interface Transaction
    permits Deposit, Withdrawal, Transfer, Fee {}

String describe(Transaction t) {
    return switch (t) {
        case Deposit d    -> "Money received: " + d.amount();
        case Withdrawal w -> "Money withdrawn: " + w.amount();
        case Transfer tr  -> "Transfer to " + tr.toAccountId();
        case Fee f        -> "Bank fee: " + f.reason();
    };
}
```

Если завтра добавляем новый тип `Chargeback`, код **не скомпилируется**, пока его не обработают везде. Баг ловится до прода.

### Почему это важно банку

- **Безопаснее изменения.** Добавление нового типа транзакции, статуса счёта или продукта не сломает старые экраны.
- **Понятнее код.** Все возможные случаи видны в одном месте.
- **Меньше ручного тестирования.** Компилятор сам проверяет «ничего ли я не забыл?» — бесплатно.

---

## 4. Structured Concurrency — Чище параллельная работа

### Проблема в Java 8

Когда клиент открывает **мобильное приложение**, банку часто нужно загрузить параллельно сразу несколько вещей:

- Балансы счетов
- Последние транзакции
- Активные карты
- Открытые кредиты

В Java 8 это делается через `CompletableFuture` и кучу клейкого кода. Самое сложное — **обработка ошибок**: если «загрузка кредитов» упала, что происходит с тремя другими задачами? Они продолжают работать в фоне, тратя ресурсы и, возможно, возвращая данные, которые пользователь уже не увидит. Если пользователь закрывает экран — то же самое. Корректно прибрать всё это сложно, и **утечки случаются**.

### Решение в Java 21

**Structured Concurrency** относится к параллельным задачам как к маленькой «команде», живущей внутри чёткого scope. Правила:

- Все задачи в команде завершаются (или падают) вместе.
- Если одна падает — остальные **автоматически отменяются**.
- Если родитель сдаётся (например, пользователь ушёл с экрана) — отменяется вся команда.

### Банковский пример

**Java 8 — координация вручную, легко ошибиться:**

```java
CompletableFuture<List<Account>>     accounts =
    CompletableFuture.supplyAsync(() -> accountService.findByCustomer(id));
CompletableFuture<List<Transaction>> txs =
    CompletableFuture.supplyAsync(() -> transactionService.history(id));
CompletableFuture<List<Card>>        cards =
    CompletableFuture.supplyAsync(() -> cardService.findByCustomer(id));
CompletableFuture<List<Loan>>        loans =
    CompletableFuture.supplyAsync(() -> loanService.findByCustomer(id));

CompletableFuture.allOf(accounts, txs, cards, loans).join();
// Если loans упал, остальные три продолжают работать. Нужно не забыть отменить.
```

**Java 21 — чисто, автоматически, без утечек:**

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {

    var accounts = scope.fork(() -> accountService.findByCustomer(id));
    var txs      = scope.fork(() -> transactionService.history(id));
    var cards    = scope.fork(() -> cardService.findByCustomer(id));
    var loans    = scope.fork(() -> loanService.findByCustomer(id));

    scope.join();          // ждём всех
    scope.throwIfFailed(); // если кто-то упал, остальные уже отменены

    return new CustomerDashboard(
        accounts.get(), txs.get(), cards.get(), loans.get()
    );
}
```

Если падает `loans` — остальные три задачи **немедленно останавливаются**. Никаких утечек, никаких полу-результатов.

### Почему это важно банку

- **Меньше нагрузки на сервер.** Никаких «призрачных» задач, делающих работу, которая никому не нужна.
- **Лучше пользовательский опыт.** Дашборд появляется быстрее, потому что все четыре запроса идут параллельно.
- **Безопаснее код.** Отмена и cleanup делаются языком, а не разработчиком.

---

## 5. Text Blocks — Чище SMS, чеки и audit-логи

### Проблема в Java 8

Банк отправляет много форматированного текста: **SMS-уведомления**, **email-подтверждения**, **печатные чеки**, **записи аудит-лога**, **SQL-запросы**. В Java 8 многострочные строки приходится склеивать через `+` и `\n`. Получается нечитаемо, легко сломать, и форматирование в коде не похоже на то, что увидит клиент.

### Решение в Java 21

**Text Blocks** позволяют писать многострочные строки именно так, как они должны выглядеть, между маркерами `"""`. Что вы видите в коде — то и получает клиент.

### Банковский пример

**Java 8 — печатный чек:**

```java
String receipt =
    "==================================\n" +
    "         BANK RECEIPT\n" +
    "==================================\n" +
    "Account:    " + account.getNumber() + "\n" +
    "Operation:  " + type + "\n" +
    "Amount:     " + amount + " " + currency + "\n" +
    "Balance:    " + balance + "\n" +
    "Date:       " + date + "\n" +
    "Reference:  " + ref + "\n" +
    "==================================\n";
```

**Java 21 — тот же чек, читаемый:**

```java
String receipt = """
    ==================================
             BANK RECEIPT
    ==================================
    Account:    %s
    Operation:  %s
    Amount:     %.2f %s
    Balance:    %.2f
    Date:       %s
    Reference:  %s
    ==================================
    """.formatted(account.getNumber(), type, amount, currency,
                  balance, date, ref);
```

### Почему это важно банку

- **Меньше багов форматирования** в текстах для клиентов (пропущенный `\n` в SMS выглядит непрофессионально).
- **Проще обновлять** шаблоны, когда маркетинг или комплаенс просит поменять формулировку.
- **Чище SQL** для отчётов — длинные запросы становятся читаемыми, а не «супом из плюсов».

---

## 6. Modern HTTP Client — Простая интеграция с другими сервисами

### Проблема в Java 8

Современный банк редко живёт один. Ему нужно общаться с:

- **Центральным банком** для курсов валют
- Сервисами **fraud detection**
- **SMS / email** шлюзами
- **Кредитными бюро** (KYC, кредитный рейтинг)
- **Платёжными сетями** (SWIFT, SEPA, карточные сети)

В Java 8 встроенный HTTP-инструмент (`HttpURLConnection`) **неудобный**: десятки строк на один запрос, нет поддержки современных протоколов (HTTP/2), нет нормального `async`. Большинство команд добавляют внешнюю библиотеку (Apache HttpClient, OkHttp), что значит — больше зависимостей, которые нужно отслеживать и обновлять.

### Решение в Java 21

В Java 21 уже встроен **современный `HttpClient`**: короткий синтаксис, поддержка HTTP/2 «из коробки» и идеальная работа в паре с Virtual Threads (Секция 1) — даже тысячи параллельных внешних вызовов остаются дешёвыми.

### Банковский пример

**Java 8 — получение сегодняшнего курса USD/EUR:**

```java
URL url = new URL("https://api.centralbank.example/rates?base=USD&target=EUR");
HttpURLConnection conn = (HttpURLConnection) url.openConnection();
conn.setRequestMethod("GET");
conn.setConnectTimeout(5000);
conn.setReadTimeout(5000);

try (BufferedReader reader = new BufferedReader(
        new InputStreamReader(conn.getInputStream()))) {
    StringBuilder sb = new StringBuilder();
    String line;
    while ((line = reader.readLine()) != null) sb.append(line);
    String json = sb.toString();
    // ...а потом парсить JSON руками или внешней библиотекой
}
```

**Java 21 — тот же запрос:**

```java
HttpClient client = HttpClient.newHttpClient();

HttpRequest request = HttpRequest.newBuilder()
    .uri(URI.create("https://api.centralbank.example/rates?base=USD&target=EUR"))
    .timeout(Duration.ofSeconds(5))
    .build();

String json = client.send(request, BodyHandlers.ofString()).body();
```

Если соединить это с Records (Секция 2), парсинг ответа занимает ещё несколько строк.

### Почему это важно банку

- **Быстрее интеграции.** Подключить нового fraud- или KYC-партнёра — часы, а не дни.
- **Меньше внешних библиотек**, за безопасностью которых нужно следить.
- **Лучшая производительность** под нагрузкой благодаря HTTP/2 + Virtual Threads.

---

## Сводная таблица — Что получает клиент

| Возможность | Бизнес-ценность | Кратко в одну строку |
|---|---|---|
| **Virtual Threads** | Меньше затрат на железо, выше пропускная способность | Тот же сервер обслуживает в 10–100× больше клиентов |
| **Records** | Быстрее разработка, меньше багов | 60 строк превращаются в 1 |
| **Sealed + Pattern Matching** | Безопаснее изменения | Компилятор ловит забытые случаи |
| **Structured Concurrency** | Чище параллельная работа, без утечек | Параллельные задачи живут и умирают вместе |
| **Text Blocks** | Чище тексты для клиентов | Что пишете — то клиент и видит |
| **Modern HttpClient** | Быстрее, проще интеграции | Встроен, без лишних библиотек |

---

## Рекомендуемый порядок для демо

Для клиентского демо рекомендуем:

1. **Records** — легко понять, мгновенный визуальный эффект («60 строк → 1 строка»).
2. **Virtual Threads** — самый сильный бизнес-аргумент, можно показать через нагрузочный тест.
3. **Sealed + Pattern Matching** — про качество и безопасность; показать, как компилятор отказывается собирать код, когда забыли случай.
4. **Text Blocks + HttpClient** — короткое «и это ещё не всё» для финала.

Этот порядок идёт от **легко понять** → **впечатляющая производительность** → **лучшее качество** → **повседневное удобство**, оставляя у клиента общее позитивное впечатление.
