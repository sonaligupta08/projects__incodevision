//custom classes

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.Scanner;

class InvalidAmountException extends Exception {
  public InvalidAmountException(String message) {
    super(message);
  }

}

class InsufficientAmount extends Exception {
  public InsufficientAmount(String message) {
    super(message);
  }
}

class InvalidAccountDetailsException extends Exception {
  public InvalidAccountDetailsException(String message) {
    super(message);
  }
}

class AccountNotFoundException extends Exception {
  public AccountNotFoundException(String message) {
    super(message);
  }
}

class BankAccount {

  public static int nextAccountNumber = 100001;

  private final int accountNumber;
  private final String accountHolderName;
  private final String accountType;
  private double balance;

  public BankAccount(String accountHolderName, String accountype, double openingBalance) {
    this.accountNumber = nextAccountNumber++;
    this.accountHolderName = accountHolderName;
    this.accountType = accountype;
    this.balance = openingBalance;
  }

  public int getAccountNumber() {
    return accountNumber;
  }

  public String getAccountHolderName() {
    return accountHolderName;
  }

  public String getAccountType() {
    return accountType;
  }

  public double getBalance() {
    return balance;
  }

  public void setBalance(Double balance) {
    this.balance = balance;
  }

  // Deposit money
  public void deposit(Double amount) throws InvalidAmountException {
    if (amount <= 0) {
      throw new InvalidAmountException("Deposit must be greater than 0");
    }
    if (Double.isNaN(amount) || Double.isInfinite(amount)) {
      throw new InvalidAmountException("invalid amount");
    }
    balance += amount;
  }

  // Withdraw Money
  public void withdraw(Double amount) throws InvalidAmountException, InsufficientAmount {
    if (amount > balance) {
      throw new InsufficientAmount("you dont have insufficient amount");
    }
    if (amount <= 0) {
      throw new InvalidAmountException("Amount should be greater than 0");
    }
    if (Double.isNaN(amount) || Double.isInfinite(amount)) {
      throw new InvalidAmountException("invalid amount");
    }
    balance -= amount;
  }

  @Override
  public String toString() {
    return String.format("Account No : %d%nHolder Name: %s%nType      : %s%nBalance       :%.2f",
        accountNumber, accountHolderName, accountType, balance);
  }
}

class Bank {
  public final Map<Integer, BankAccount> accounts = new HashMap<>();
  public static final double MINIMUM_OPENING_BALANCE = 500.0;

  public BankAccount createAccount(String holderName, String accountType, double openingBalance)
      throws InvalidAccountDetailsException, InvalidAmountException {
    if (holderName == null || holderName.trim().isEmpty()) {
      throw new InvalidAccountDetailsException("Account holder name cannot be empty.");
    }
    if (!accountType.equalsIgnoreCase("SAVINGS") && !accountType.equalsIgnoreCase("CURRENT")) {
      throw new InvalidAccountDetailsException("Account type must be saving or current");
    }
    if (openingBalance < MINIMUM_OPENING_BALANCE) {
      throw new InvalidAmountException("Account balance must me minimum 500 to open account");
    }
    if (!Pattern.matches("[a-zA-Z .]{2,50}", holderName.trim())) {
      throw new InvalidAccountDetailsException(
          "Account holder should contain only letters, spaces and charaters (2-50) only).");
    }
    BankAccount account = new BankAccount(holderName.trim(), accountType.toUpperCase(), openingBalance);
    accounts.put(account.getAccountNumber(), account);
    return account;
  }

  // if account doesn't exists
  public BankAccount findAccount(int accountNumber) throws AccountNotFoundException {
    BankAccount account = accounts.get(accountNumber);
    if (account == null) {
      throw new AccountNotFoundException("No account found :" + accountNumber);
    }
    return account;
  }

  // deposit money
  public void deposit(int accountNumber, double amount) throws AccountNotFoundException, InvalidAmountException {
    BankAccount account = accounts.get(accountNumber);
    account.deposit(amount);
  }

  // withdraw money
  public void withdraw(int accountNumber, double amount) throws InvalidAmountException, InsufficientAmount {
    BankAccount account = accounts.get(accountNumber);
    account.withdraw(amount);
  }

  public double checkBalance(int accountNumber) throws AccountNotFoundException {
    return findAccount(accountNumber).getBalance();
  }

  public boolean hasAccounts() {
    return !accounts.isEmpty();
  }

  public Map<Integer, BankAccount> getAllAccounts() {
    return accounts;
  }
}

class BankMenu {
  private final Bank bank;
  private final Scanner scanner;

  public BankMenu(Bank bank, Scanner scanner){
       this.bank = bank;
       this.scanner = scanner;
    }

  public void run() {
    boolean exit = false;
    System.out.println("=================================================");
    System.out.println("   WELCOME TO THE BANK ACCOUNT MANAGEMENT SYSTEM");
    System.out.println("=================================================");

    while (!exit) {
      printmenu();
      int choice = readMenuChoice();

      switch (choice) {
        case 1 -> handleCreateAccount();
        case 2 -> handleDeposit();
        case 3 -> handleWithdraw();
        case 4 -> handleBalanceInquiry();
        case 5 -> handleListAccounts();
        case 6 -> {
          System.out.println("Thank you for using the Bank Account Management System. Goodbye!");
          exit = true;
        }
        default -> System.out.println("Invalid choice. Please select a number between 1 and 6.");
      }
    }
  }

  public void printmenu() {
    System.out.println("\n-------------------- MENU ----------------------");
    System.out.println("1. Create Account");
    System.out.println("2. Deposit");
    System.out.println("3. Withdraw");
    System.out.println("4. Check Balance");
    System.out.println("5. List All Accounts");
    System.out.println("6. Exit");
    System.out.print("Enter your choice: ");
  }

  private int readMenuChoice() {
    while (true) {
      String line = scanner.nextLine().trim();
      try {
        return Integer.parseInt(line);
      } catch (NumberFormatException e) {
        System.out.println("please enter a valid number (1-6) : ");
      }
    }
  }

  private int readAccountnumber() {
    while (true) {
      System.out.println("Enter account number: ");
      String line = scanner.nextLine().trim();
      try {
        int value = Integer.parseInt(line);
        if (value <= 0) {
          System.out.println("Account number must be a positive integer.");
          continue;
        }
        return value;
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Account number must be numeric.");
      }
    }
  }

  private double readAmount(String promptLabel) {
    while (true) {
      System.out.print(promptLabel);
      String line = scanner.nextLine().trim();
      try {
        double value = Double.parseDouble(line);
        if (value <= 0) {
          System.out.println("Amount must be greater than zero.");
          continue;
        }
        return value;
      } catch (NumberFormatException e) {
        System.out.println("Invalid input. Please enter a numeric amount (e.g. 500.00).");
      }
    }
  }

  private void handleCreateAccount() {
    System.out.println("\n-- Create New Account --");
    System.out.print("Enter account holder name: ");
    String name = scanner.nextLine().trim();

    System.out.print("Enter account type (SAVINGS/CURRENT): ");
    String type = scanner.nextLine().trim();

    double openingBalance = readAmount("Enter opening balance (minimum 500.00): ");

    try {
      BankAccount account = bank.createAccount(name, type, openingBalance);
      System.out.println("\nAccount created successfully!");
      System.out.println(account);
    } catch (InvalidAccountDetailsException | InvalidAmountException e) {
      System.out.println("Error creating account: " + e.getMessage());
    }
  }

  private void handleDeposit() {
    System.out.println("\n-- Deposit Funds --");
    if (!bank.hasAccounts()) {
      System.out.println("No accounts exist yet. Please create an account first.");
      return;
    }
    int accNo = readAccountnumber();
    double amount = readAmount("Enter deposit amount: ");

    try {
      bank.deposit(accNo, amount);
      double newBalance = bank.checkBalance(accNo);
      System.out.printf("Deposit successful. New balance: %.2f%n", newBalance);
    } catch (AccountNotFoundException | InvalidAmountException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void handleWithdraw() {
    System.out.println("\n-- Withdraw Funds --");
    if (!bank.hasAccounts()) {
      System.out.println("No accounts exist yet. Please create an account first.");
      return;
    }
    int accNo = readAccountnumber();
    double amount = readAmount("Enter withdrawal amount: ");

    try {
      bank.withdraw(accNo, amount);
      double newBalance = bank.checkBalance(accNo);
      System.out.printf("Withdrawal successful. New balance: %.2f%n", newBalance);
    } catch (AccountNotFoundException | InvalidAmountException | InsufficientAmount e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void handleBalanceInquiry() {
    System.out.println("\n-- Check Balance --");
    if (!bank.hasAccounts()) {
      System.out.println("No accounts exist yet. Please create an account first.");
      return;
    }
    int accNo = readAccountnumber();
    try {
      BankAccount account = bank.findAccount(accNo);
      System.out.println("\n" + account);
    } catch (AccountNotFoundException e) {
      System.out.println("Error: " + e.getMessage());
    }
  }

  private void handleListAccounts() {
    System.out.println("\n-- All Accounts --");
    if (!bank.hasAccounts()) {
      System.out.println("No accounts to display.");
      return;
    }
    bank.getAllAccounts().values().forEach(acc -> {
      System.out.println("-------------------------------------------------");
      System.out.println(acc);
    });
    System.out.println("-------------------------------------------------");
  }
}
public class Banking_System {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Bank bank = new Bank();
        BankMenu menu = new BankMenu(bank, scanner);

        menu.run();
        scanner.close();
    }
}
