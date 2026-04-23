package com.bank.ui;

import com.bank.config.BankConfig;
import com.bank.service.*;

import java.util.Scanner;

/**
 * Main console user interface controller.
 * Renders the top-level menu and delegates to sub-menus.
 */
public class ConsoleUI {

    private static final String LINE  = "================================================================================";
    private static final String DIV   = "--------------------------------------------------------------------------------";

    private final Scanner scanner;
    private final CustomerMenu    customerMenu;
    private final AccountMenu     accountMenu;
    private final TransactionMenu transactionMenu;
    private final CardMenu        cardMenu;
    private final LoanMenu        loanMenu;
    private final BranchMenu      branchMenu;

    public ConsoleUI(CustomerService customerService, AccountService accountService,
                     TransactionService transactionService, CardService cardService,
                     LoanService loanService, BranchService branchService) {
        this.scanner        = new Scanner(System.in);
        this.customerMenu   = new CustomerMenu(scanner, customerService);
        this.accountMenu    = new AccountMenu(scanner, accountService, customerService);
        this.transactionMenu = new TransactionMenu(scanner, transactionService, accountService);
        this.cardMenu       = new CardMenu(scanner, cardService, accountService, customerService);
        this.loanMenu       = new LoanMenu(scanner, loanService, accountService, customerService);
        this.branchMenu     = new BranchMenu(scanner, branchService);
    }

    public void start() {
        printBanner();
        boolean running = true;
        while (running) {
            printMainMenu();
            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": customerMenu.show();    break;
                case "2": accountMenu.show();     break;
                case "3": transactionMenu.show(); break;
                case "4": cardMenu.show();        break;
                case "5": loanMenu.show();        break;
                case "6": branchMenu.show();      break;
                case "0":
                    System.out.println("\n  Thank you for banking with " + BankConfig.BANK_NAME + ". Goodbye.");
                    System.out.println(LINE);
                    running = false;
                    break;
                default:
                    System.out.println("  [!] Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private void printBanner() {
        System.out.println("\n" + LINE);
        System.out.printf("  %-76s%n", "");
        System.out.printf("  %-76s%n", center(BankConfig.BANK_NAME, 76));
        System.out.printf("  %-76s%n", center(BankConfig.BANK_SLOGAN, 76));
        System.out.printf("  %-76s%n", "");
        System.out.println(LINE);
        System.out.printf("  Support: %-20s  Phone: %-20s  v%s%n",
                BankConfig.SUPPORT_EMAIL, BankConfig.SUPPORT_PHONE, BankConfig.APP_VERSION);
        System.out.println(LINE);
    }

    private void printMainMenu() {
        System.out.println("\n" + LINE);
        System.out.println("  MAIN MENU");
        System.out.println(DIV);
        System.out.println("  [1]  Customer Management");
        System.out.println("  [2]  Account Management");
        System.out.println("  [3]  Transactions");
        System.out.println("  [4]  Cards");
        System.out.println("  [5]  Loans");
        System.out.println("  [6]  Branch Information");
        System.out.println("  [0]  Exit");
        System.out.println(DIV);
        System.out.print("  Select an option: ");
    }

    public static String center(String text, int width) {
        if (text == null) return "";
        int padding = (width - text.length()) / 2;
        if (padding <= 0) return text;
        return " ".repeat(padding) + text;
    }
}
