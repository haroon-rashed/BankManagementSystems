# 🏦 Nova Bank Management System
### Complete OOP Java Project with Beautiful Swing GUI

---

## 📋 OOP Concepts Implemented

| Concept | Implementation |
|---------|----------------|
| **Encapsulation** | Private fields with getters/setters in all model classes |
| **Inheritance** | `SavingsAccount`, `CurrentAccount`, `FixedDepositAccount` extend `AbstractAccount` |
| **Polymorphism** | Each account type overrides `calculateInterest()`, `withdraw()`, `getAccountDetails()` |
| **Function Overriding** | `withdraw()` overridden in each account type with custom rules |
| **Function Overloading** | `createAccount()`, `deposit()`, `withdraw()`, `searchAccount()` overloaded in `BankService` |
| **Abstraction** | `AbstractAccount` and `AbstractTransaction` are abstract classes |
| **File Handling** | All data saved to binary `.dat` files via `ObjectInputStream/ObjectOutputStream` |

---

## 🚀 HOW TO RUN IN IntelliJ IDEA (Step-by-Step)

### Step 1 — Open the Project
1. Launch **IntelliJ IDEA**
2. Click **"Open"** (not "New Project")
3. Navigate to the `BankManagementSystem` folder
4. Click **OK / Open**
5. IntelliJ will recognize it as a Java project automatically

### Step 2 — Set the JDK
1. Go to **File → Project Structure** (Ctrl+Alt+Shift+S)
2. Under **Project SDK**, select **Java 11** or higher (Java 8+ works too)
3. Set **Language Level** to match your JDK
4. Click **Apply → OK**

### Step 3 — Mark Sources Root
1. In the Project panel on the left, right-click the **`src`** folder
2. Click **"Mark Directory as" → "Sources Root"**
   - The folder should turn blue

### Step 4 — Run the Application
**Option A (Recommended):**
1. In the Project panel, expand `src` → find `Main.java`
2. Right-click `Main.java` → **"Run 'Main.main()'"**

**Option B (Run Configuration):**
1. Click the green **▶ Run** button at the top
2. If prompted, select `Main` as the main class
3. Set **Working Directory** to the project root (`BankManagementSystem/` folder)

### Step 5 — Login
- **Default Admin:** username: `admin`, password: `Admin123`
- Or click **SIGN UP** to create a new account

---

## 📁 Project Structure

```
BankManagementSystem/
├── src/
│   ├── Main.java                          ← Entry Point
│   └── bank/
│       ├── abstract/
│       │   ├── AbstractAccount.java       ← Abstract base (ABSTRACTION)
│       │   └── AbstractTransaction.java   ← Abstract transaction
│       ├── models/
│       │   ├── SavingsAccount.java        ← INHERITANCE + OVERRIDING
│       │   ├── CurrentAccount.java        ← INHERITANCE + OVERRIDING
│       │   ├── FixedDepositAccount.java   ← INHERITANCE + OVERRIDING
│       │   ├── Transaction.java           ← Transaction record
│       │   └── User.java                  ← Login/Signup user
│       ├── utils/
│       │   ├── BankService.java           ← Business logic + OVERLOADING
│       │   ├── FileHandler.java           ← FILE HANDLING (persistence)
│       │   └── PasswordUtil.java          ← SHA-256 password hashing
│       └── gui/
│           ├── UITheme.java               ← Central styling/design system
│           ├── LoginFrame.java            ← Login & Signup screens
│           └── DashboardFrame.java        ← Main dashboard with all features
├── data/                                  ← Auto-created, stores .dat files
│   ├── accounts.dat                       ← Serialized account data
│   ├── users.dat                          ← Serialized user data
│   ├── transactions.dat                   ← Serialized transactions
│   └── system.log                         ← Text log file
├── BankManagementSystem.iml
└── .idea/
    ├── misc.xml
    ├── modules.xml
    └── workspace.xml
```

---

## 🎯 Features

### 💻 GUI Screens
- **Login Screen** — Beautiful dark theme with gradient, animated cards
- **Signup Screen** — Full registration with validation
- **Dashboard Overview** — Stats cards, recent transactions
- **Open Account** — Create Savings, Current, or Fixed Deposit accounts
- **Deposit / Withdraw** — With overloaded method support (with/without description)
- **Fund Transfer** — Between any two accounts
- **Search** — By account number OR owner name (overloaded search)
- **All Accounts** — Styled data table
- **Transaction History** — Filterable, exportable
- **Update Account** — Modify owner name
- **Delete Account** — With confirmation dialog
- **My Profile** — Update personal info + change password
- **User Management** — Admin only
- **Reports & Export** — Export to text files

### 🗄️ File Handling
All data is automatically saved to the `data/` directory:
- `accounts.dat` — All account objects (serialized)
- `users.dat` — All user objects (serialized)
- `transactions.dat` — All transaction records
- `system.log` — Human-readable audit log
- `accounts_report.txt` — Exportable accounts report
- `transactions_export.txt` — Exportable transactions

---

## ⚠️ Troubleshooting

**"Cannot find Main class"**
→ Right-click `src` folder → Mark Directory as → Sources Root

**"Package bank.base not found"**
→ Some IDEs need the package renamed; `abstract` is a reserved keyword.
→ If needed: rename `bank.base` to `bank.base` in all files.

**Data not saving**
→ Make sure Working Directory is set to project root in Run Configuration
→ File → Run Configurations → BankManagementSystem → Working Directory

---

## 👨‍💻 Technologies
- **Java 11+**
- **Swing / AWT** for GUI
- **Java Serialization** for file storage
- **SHA-256** for password security
- **Java Collections Framework** for data management
