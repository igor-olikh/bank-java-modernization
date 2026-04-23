package com.bank.data;

import com.bank.model.Account;
import com.bank.model.Address;
import com.bank.model.Customer;
import com.bank.model.Transaction;
import com.bank.model.enums.*;
import com.bank.service.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the system with 17 pre-defined customers, their accounts,
 * transaction history, cards, loans, and bank branches.
 */
public class DataSeeder {

    private final CustomerService    customerService;
    private final AccountService     accountService;
    private final TransactionService transactionService;
    private final CardService        cardService;
    private final LoanService        loanService;
    private final BranchService      branchService;

    public DataSeeder(CustomerService customerService, AccountService accountService,
                      TransactionService transactionService, CardService cardService,
                      LoanService loanService, BranchService branchService) {
        this.customerService    = customerService;
        this.accountService     = accountService;
        this.transactionService = transactionService;
        this.cardService        = cardService;
        this.loanService        = loanService;
        this.branchService      = branchService;
    }

    public void seed() {
        seedBranches();
        seedCustomers();
    }

    // -------------------------------------------------------------------------
    // Branches
    // -------------------------------------------------------------------------
    private void seedBranches() {
        branchService.addBranch("HQ001", "Headquarters",
                new Address("1 Finance Plaza", "New York", "NY", "10005", "United States"),
                "+1-212-555-0100", "Robert Mitchell", "Mon-Fri 09:00-17:00");

        branchService.addBranch("EU001", "European Hub",
                new Address("12 Bahnhofstrasse", "Zurich", "", "8001", "Switzerland"),
                "+41-44-555-0200", "Helena Braun", "Mon-Fri 08:30-16:30");

        branchService.addBranch("AS001", "Asia-Pacific Centre",
                new Address("88 Market Street", "Singapore", "", "048948", "Singapore"),
                "+65-6555-0300", "David Tan", "Mon-Fri 09:00-18:00");

        branchService.addBranch("ME001", "Middle East Office",
                new Address("King Fahd Road, Tower A", "Riyadh", "", "11564", "Saudi Arabia"),
                "+966-11-555-0400", "Omar Al-Farsi", "Sun-Thu 08:00-16:00");
    }

    // -------------------------------------------------------------------------
    // Customers
    // -------------------------------------------------------------------------
    private void seedCustomers() {
        // 1. James Anderson — USA
        Customer james = reg("James", "Anderson", "1985-03-14",
                "james.anderson@email.com", "+1-555-0101",
                new Address("742 Evergreen Terrace", "Springfield", "IL", "62701", "United States"),
                "American", CustomerType.INDIVIDUAL);
        Account jamesCheck = openAcc(james, AccountType.CHECKING, "USD", "15432.50");
        Account jamesSave  = openAcc(james, AccountType.SAVINGS,  "USD", "48200.00");
        seedTx(jamesCheck, "25000.00", "3000.00", "1850.00", "420.00", "180.00");
        seedTx(jamesSave,  "50000.00", "2000.00", "0.00",    "0.00",   "0.00");
        cardService.issueCard(jamesCheck.getAccountId(), james.getCustomerId(), CardType.DEBIT,  CardNetwork.VISA);
        cardService.issueCard(jamesSave.getAccountId(),  james.getCustomerId(), CardType.CREDIT, CardNetwork.MASTERCARD);

        // 2. María González — Spain
        Customer maria = reg("María", "González", "1990-07-22",
                "maria.gonzalez@email.es", "+34-91-555-0102",
                new Address("Calle Gran Vía 45, 3°", "Madrid", "", "28013", "Spain"),
                "Spanish", CustomerType.INDIVIDUAL);
        Account mariaCheck = openAcc(maria, AccountType.CHECKING, "EUR", "8750.00");
        seedTx(mariaCheck, "9000.00", "1200.00", "500.00", "350.00", "200.00");
        cardService.issueCard(mariaCheck.getAccountId(), maria.getCustomerId(), CardType.DEBIT, CardNetwork.VISA);

        // 3. Yuki Tanaka — Japan
        Customer yuki = reg("Yuki", "Tanaka", "1988-11-05",
                "yuki.tanaka@email.jp", "+81-3-5555-0103",
                new Address("2-1 Shinjuku", "Tokyo", "", "160-0022", "Japan"),
                "Japanese", CustomerType.INDIVIDUAL);
        Account yukiSave   = openAcc(yuki, AccountType.SAVINGS,     "JPY", "1250000.00");
        Account yukiInvest = openAcc(yuki, AccountType.INVESTMENT,  "JPY", "3800000.00");
        seedTx(yukiSave, "1500000.00", "300000.00", "50000.00", "0.00", "0.00");
        cardService.issueCard(yukiSave.getAccountId(), yuki.getCustomerId(), CardType.DEBIT, CardNetwork.VISA);

        // 4. Mohammed Al-Rashid — Saudi Arabia
        Customer mohammed = reg("Mohammed", "Al-Rashid", "1978-01-30",
                "m.alrashid@business.sa", "+966-50-555-0104",
                new Address("Olaya District, Building 7", "Riyadh", "", "11433", "Saudi Arabia"),
                "Saudi Arabian", CustomerType.CORPORATE);
        Account mohammedBiz = openAcc(mohammed, AccountType.BUSINESS, "SAR", "320000.00");
        seedTx(mohammedBiz, "400000.00", "80000.00", "15000.00", "5000.00", "2000.00");
        cardService.issueCard(mohammedBiz.getAccountId(), mohammed.getCustomerId(), CardType.CREDIT, CardNetwork.AMEX);

        // 5. Amara Osei — Ghana
        Customer amara = reg("Amara", "Osei", "1995-06-18",
                "amara.osei@email.gh", "+233-20-555-0105",
                new Address("15 Liberation Road", "Accra", "", "00233", "Ghana"),
                "Ghanaian", CustomerType.INDIVIDUAL);
        Account amaraCheck = openAcc(amara, AccountType.CHECKING, "GHS", "4800.00");
        seedTx(amaraCheck, "5000.00", "600.00", "200.00", "150.00", "100.00");
        cardService.issueCard(amaraCheck.getAccountId(), amara.getCustomerId(), CardType.DEBIT, CardNetwork.MASTERCARD);

        // 6. Lena Müller — Germany
        Customer lena = reg("Lena", "Müller", "1992-09-09",
                "lena.mueller@email.de", "+49-30-555-0106",
                new Address("Unter den Linden 10", "Berlin", "", "10117", "Germany"),
                "German", CustomerType.INDIVIDUAL);
        Account lenaSave = openAcc(lena, AccountType.SAVINGS, "EUR", "22500.00");
        seedTx(lenaSave, "25000.00", "3000.00", "1000.00", "500.00", "0.00");
        cardService.issueCard(lenaSave.getAccountId(), lena.getCustomerId(), CardType.DEBIT, CardNetwork.MASTERCARD);

        // 7. Priya Sharma — India  [has personal loan]
        Customer priya = reg("Priya", "Sharma", "1991-04-25",
                "priya.sharma@email.in", "+91-98-555-0107",
                new Address("Sector 14, Gurgaon", "Haryana", "", "122001", "India"),
                "Indian", CustomerType.INDIVIDUAL);
        Account priyaCheck = openAcc(priya, AccountType.CHECKING, "INR", "185000.00");
        seedTx(priyaCheck, "200000.00", "20000.00", "5000.00", "8000.00", "3000.00");
        cardService.issueCard(priyaCheck.getAccountId(), priya.getCustomerId(), CardType.DEBIT, CardNetwork.VISA);
        loanService.applyForLoan(priya.getCustomerId(), priyaCheck.getAccountId(),
                LoanType.PERSONAL, new BigDecimal("500000.00"), new BigDecimal("10.50"), 48);

        // 8. Lucas Oliveira — Brazil
        Customer lucas = reg("Lucas", "Oliveira", "1987-12-03",
                "lucas.oliveira@email.br", "+55-11-555-0108",
                new Address("Av. Paulista 1000", "São Paulo", "SP", "01310-100", "Brazil"),
                "Brazilian", CustomerType.INDIVIDUAL);
        Account lucasCheck = openAcc(lucas, AccountType.CHECKING, "BRL", "9200.00");
        Account lucasSave  = openAcc(lucas, AccountType.SAVINGS,  "BRL", "31500.00");
        seedTx(lucasCheck, "10000.00", "1500.00", "800.00", "600.00", "200.00");
        cardService.issueCard(lucasCheck.getAccountId(), lucas.getCustomerId(), CardType.DEBIT, CardNetwork.MASTERCARD);

        // 9. Irina Petrov — Russia
        Customer irina = reg("Irina", "Petrov", "1983-08-17",
                "irina.petrov@email.ru", "+7-495-555-0109",
                new Address("Tverskaya Street 15", "Moscow", "", "125009", "Russia"),
                "Russian", CustomerType.INDIVIDUAL);
        Account irinaSave = openAcc(irina, AccountType.SAVINGS, "RUB", "980000.00");
        seedTx(irinaSave, "1000000.00", "50000.00", "30000.00", "20000.00", "0.00");
        cardService.issueCard(irinaSave.getAccountId(), irina.getCustomerId(), CardType.DEBIT, CardNetwork.MASTERCARD);

        // 10. Chen Wei — China
        Customer chen = reg("Chen", "Wei", "1980-02-14",
                "chen.wei@business.cn", "+86-10-555-0110",
                new Address("No. 1 Financial Street", "Beijing", "", "100033", "China"),
                "Chinese", CustomerType.CORPORATE);
        Account chenInvest = openAcc(chen, AccountType.INVESTMENT, "CNY", "850000.00");
        Account chenBiz    = openAcc(chen, AccountType.BUSINESS,   "CNY", "1200000.00");
        seedTx(chenBiz, "1500000.00", "200000.00", "80000.00", "50000.00", "20000.00");
        cardService.issueCard(chenBiz.getAccountId(), chen.getCustomerId(), CardType.CREDIT, CardNetwork.AMEX);

        // 11. Fatima Benali — Morocco
        Customer fatima = reg("Fatima", "Benali", "1994-05-28",
                "fatima.benali@email.ma", "+212-6-555-0111",
                new Address("Boulevard Mohammed V 22", "Casablanca", "", "20000", "Morocco"),
                "Moroccan", CustomerType.INDIVIDUAL);
        Account fatimaCheck = openAcc(fatima, AccountType.CHECKING, "MAD", "12400.00");
        seedTx(fatimaCheck, "15000.00", "1800.00", "600.00", "400.00", "200.00");
        cardService.issueCard(fatimaCheck.getAccountId(), fatima.getCustomerId(), CardType.DEBIT, CardNetwork.VISA);

        // 12. Erik Lindström — Sweden  [has mortgage]
        Customer erik = reg("Erik", "Lindström", "1979-10-11",
                "erik.lindstrom@email.se", "+46-8-555-0112",
                new Address("Storgatan 12", "Stockholm", "", "11152", "Sweden"),
                "Swedish", CustomerType.INDIVIDUAL);
        Account erikCheck = openAcc(erik, AccountType.CHECKING, "SEK", "85000.00");
        seedTx(erikCheck, "90000.00", "12000.00", "3000.00", "2000.00", "500.00");
        cardService.issueCard(erikCheck.getAccountId(), erik.getCustomerId(), CardType.DEBIT, CardNetwork.VISA);
        loanService.applyForLoan(erik.getCustomerId(), erikCheck.getAccountId(),
                LoanType.MORTGAGE, new BigDecimal("2500000.00"), new BigDecimal("3.75"), 240);

        // 13. Aiko Nakamura — Japan
        Customer aiko = reg("Aiko", "Nakamura", "1997-03-07",
                "aiko.nakamura@email.jp", "+81-6-5555-0113",
                new Address("1-1 Namba", "Osaka", "", "542-0076", "Japan"),
                "Japanese", CustomerType.INDIVIDUAL);
        Account aikoSave = openAcc(aiko, AccountType.SAVINGS, "JPY", "680000.00");
        seedTx(aikoSave, "700000.00", "50000.00", "20000.00", "0.00", "0.00");
        cardService.issueCard(aikoSave.getAccountId(), aiko.getCustomerId(), CardType.DEBIT, CardNetwork.MASTERCARD);

        // 14. Samuel Okonkwo — Nigeria
        Customer samuel = reg("Samuel", "Okonkwo", "1982-11-23",
                "samuel.okonkwo@business.ng", "+234-80-555-0114",
                new Address("5 Victoria Island", "Lagos", "", "101241", "Nigeria"),
                "Nigerian", CustomerType.CORPORATE);
        Account samuelBiz   = openAcc(samuel, AccountType.BUSINESS,  "NGN", "5800000.00");
        Account samuelCheck = openAcc(samuel, AccountType.CHECKING,  "NGN", "1200000.00");
        seedTx(samuelBiz, "7000000.00", "800000.00", "250000.00", "100000.00", "50000.00");
        cardService.issueCard(samuelBiz.getAccountId(), samuel.getCustomerId(), CardType.CREDIT, CardNetwork.VISA);

        // 15. Sofia Papadopoulos — Greece  [has auto loan]
        Customer sofia = reg("Sofia", "Papadopoulos", "1993-06-30",
                "sofia.papadopoulos@email.gr", "+30-21-555-0115",
                new Address("Ermou 50", "Athens", "", "10563", "Greece"),
                "Greek", CustomerType.INDIVIDUAL);
        Account sofiaCheck = openAcc(sofia, AccountType.CHECKING, "EUR", "6300.00");
        seedTx(sofiaCheck, "7000.00", "1000.00", "400.00", "250.00", "150.00");
        cardService.issueCard(sofiaCheck.getAccountId(), sofia.getCustomerId(), CardType.DEBIT, CardNetwork.MASTERCARD);
        loanService.applyForLoan(sofia.getCustomerId(), sofiaCheck.getAccountId(),
                LoanType.AUTO, new BigDecimal("18000.00"), new BigDecimal("6.50"), 60);

        // 16. Noam Cohen — Israel
        Customer noam = reg("Noam", "Cohen", "1989-02-19",
                "noam.cohen@email.il", "+972-52-555-0116",
                new Address("Rothschild Boulevard 22", "Tel Aviv", "", "6688210", "Israel"),
                "Israeli", CustomerType.INDIVIDUAL);
        Account noamCheck = openAcc(noam, AccountType.CHECKING, "ILS", "42000.00");
        Account noamSave  = openAcc(noam, AccountType.SAVINGS,  "ILS", "115000.00");
        seedTx(noamCheck, "45000.00", "6000.00", "1500.00", "800.00", "400.00");
        cardService.issueCard(noamCheck.getAccountId(), noam.getCustomerId(), CardType.DEBIT,  CardNetwork.VISA);
        cardService.issueCard(noamSave.getAccountId(),  noam.getCustomerId(), CardType.CREDIT, CardNetwork.MASTERCARD);

        // 17. Mikhail Levin — Israel (born Russia) [has personal loan]
        Customer mikhail = reg("Mikhail", "Levin", "1976-09-04",
                "mikhail.levin@email.il", "+972-54-555-0117",
                new Address("HaYarkon Street 180", "Tel Aviv", "", "6343229", "Israel"),
                "Israeli (born Russia)", CustomerType.INDIVIDUAL);
        Account mikhailCheck = openAcc(mikhail, AccountType.CHECKING, "ILS", "28500.00");
        seedTx(mikhailCheck, "30000.00", "4000.00", "1200.00", "700.00", "300.00");
        cardService.issueCard(mikhailCheck.getAccountId(), mikhail.getCustomerId(), CardType.DEBIT, CardNetwork.VISA);
        loanService.applyForLoan(mikhail.getCustomerId(), mikhailCheck.getAccountId(),
                LoanType.PERSONAL, new BigDecimal("80000.00"), new BigDecimal("8.00"), 36);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private Customer reg(String first, String last, String dob, String email, String phone,
                          Address address, String nationality, CustomerType type) {
        return customerService.registerCustomer(
                first, last, LocalDate.parse(dob), email, phone, address, nationality, type);
    }

    private Account openAcc(Customer customer, AccountType type, String currency, String initialBalance) {
        Account account = accountService.openAccount(customer.getCustomerId(), type, currency);
        if (new BigDecimal(initialBalance).compareTo(BigDecimal.ZERO) > 0) {
            transactionService.deposit(account.getAccountId(),
                    new BigDecimal(initialBalance), "Opening deposit");
        }
        return account;
    }

    /**
     * Seeds 5 transactions: salary credit, grocery payment,
     * utility payment, ATM withdrawal, bank fee — if amount > 0.
     */
    private void seedTx(Account account, String salary, String grocery,
                         String utility, String atm, String fee) {
        String id  = account.getAccountId();
        String cur = account.getCurrency();
        if (pos(salary))  transactionService.deposit(id,   bd(salary),  "Monthly salary credit");
        if (pos(grocery)) transactionService.withdraw(id,  bd(grocery), "Grocery store payment");
        if (pos(utility)) transactionService.withdraw(id,  bd(utility), "Utility bill payment");
        if (pos(atm))     transactionService.withdraw(id,  bd(atm),     "ATM cash withdrawal");
        if (pos(fee))     transactionService.withdraw(id,  bd(fee),     "Monthly account fee");
    }

    private static boolean pos(String val) {
        return new BigDecimal(val).compareTo(BigDecimal.ZERO) > 0;
    }

    private static BigDecimal bd(String val) {
        return new BigDecimal(val);
    }
}
