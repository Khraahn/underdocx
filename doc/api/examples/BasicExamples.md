# Basic Examples

This document provides practical examples demonstrating common use cases for Underdocx. These examples range from simple variable replacement to complex document generation scenarios.

## Table of Contents

1. [Hello World](#hello-world)
2. [Simple Variable Replacement](#simple-variable-replacement)
3. [Object Property Access](#object-property-access)
4. [Collections and Loops](#collections-and-loops)
5. [Conditional Content](#conditional-content)
6. [Date and Number Formatting](#date-and-number-formatting)
7. [Document Import](#document-import)
8. [Invoice Generation](#invoice-generation)
9. [Report Generation](#report-generation)
10. [Email Templates](#email-templates)

## Hello World

The simplest Underdocx example demonstrates basic template processing.

### Text Document
```java
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.doctypes.txt.TxtEngine;

public class HelloWorld {
    public static void main(String[] args) throws IOException {
        // Create document and engine
        TxtContainer doc = new TxtContainer("Hello ${name}!");
        TxtEngine engine = new TxtEngine();
        
        // Set variable
        engine.pushVariable("name", "World");
        
        // Process template
        engine.run(doc);
        
        // Output result
        System.out.println(doc.getPlainText()); // "Hello World!"
    }
}
```

### ODT Document
```java
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class HelloWorldODT {
    public static void main(String[] args) throws IOException {
        // Create ODT document and engine
        OdtContainer doc = new OdtContainer("Hello ${name}!");
        OdtEngine engine = new OdtEngine();
        
        // Set variable
        engine.pushVariable("name", "World");
        
        // Process template
        engine.run(doc);
        
        // Save to file
        doc.save(new File("hello-world.odt"));
    }
}
```

## Simple Variable Replacement

Demonstrate various ways to replace variables in templates.

```java
public class VariableReplacementExample {
    public static void main(String[] args) throws IOException {
        TxtEngine engine = new TxtEngine();
        
        // Set simple variables
        engine.pushVariable("firstName", "John");
        engine.pushVariable("lastName", "Doe");
        engine.pushVariable("age", 30);
        engine.pushVariable("isEmployed", true);
        
        String template = """
        Name: ${firstName} ${lastName}
        Age: ${age}
        Employed: ${isEmployed}
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        System.out.println(doc.getPlainText());
        // Output:
        // Name: John Doe
        // Age: 30
        // Employed: true
    }
}
```

## Object Property Access

Access properties of complex objects using dot notation.

```java
// Define a Person class
public class Person {
    private String firstName;
    private String lastName;
    private int age;
    private Address address;
    
    // constructors, getters, setters...
    
    public static class Address {
        private String street;
        private String city;
        private String zipCode;
        
        // constructors, getters, setters...
    }
}

public class ObjectPropertyExample {
    public static void main(String[] args) throws IOException {
        TxtEngine engine = new TxtEngine();
        
        // Create complex object
        Person.Address address = new Person.Address("123 Main St", "Anytown", "12345");
        Person person = new Person("John", "Doe", 30, address);
        
        // Set as model
        engine.setModel(person);
        
        String template = """
        Name: ${firstName} ${lastName}
        Age: ${age}
        Address: ${address.street}, ${address.city} ${address.zipCode}
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        System.out.println(doc.getPlainText());
    }
}
```

## Collections and Loops

Process collections using the `For` command.

```java
public class CollectionLoopExample {
    public static void main(String[] args) throws IOException {
        TxtEngine engine = new TxtEngine();
        
        // Create a list of products
        List<Product> products = Arrays.asList(
            new Product("Laptop", 999.99, "Electronics"),
            new Product("Book", 19.99, "Education"),
            new Product("Coffee", 4.99, "Food")
        );
        
        engine.pushVariable("products", products);
        engine.pushVariable("storeName", "Tech Store");
        
        String template = """
        ${storeName} - Product Catalog
        
        ${For product in products}
        Name: ${product.name}
        Price: $${Number value=${product.price} format="#.##"}
        Category: ${product.category}
        
        ${EndFor}
        Total Products: ${products.size()}
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        System.out.println(doc.getPlainText());
    }
}
```

## Conditional Content

Use conditional statements to include content based on conditions.

```java
public class ConditionalContentExample {
    public static void main(String[] args) throws IOException {
        TxtEngine engine = new TxtEngine();
        
        Customer customer = new Customer("Alice Johnson", "alice@example.com", true, 1500.0);
        engine.pushVariable("customer", customer);
        
        String template = """
        Dear ${customer.name},
        
        ${If customer.isPremium}
        Thank you for being a premium customer!
        Your current balance is $${Number value=${customer.balance} format="#,##0.00"}.
        
        ${If customer.balance > 1000}
        You qualify for our platinum tier benefits!
        ${EndIf}
        ${EndIf}
        
        ${If not customer.isPremium}
        Consider upgrading to premium for exclusive benefits.
        ${EndIf}
        
        Best regards,
        Customer Service
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        System.out.println(doc.getPlainText());
    }
}
```

## Date and Number Formatting

Format dates and numbers using built-in formatters.

```java
public class FormattingExample {
    public static void main(String[] args) throws IOException {
        TxtEngine engine = new TxtEngine();
        
        // Set date and numeric variables
        engine.pushVariable("currentDate", new Date());
        engine.pushVariable("orderDate", LocalDate.of(2024, 3, 15));
        engine.pushVariable("amount", 1234.56);
        engine.pushVariable("percentage", 0.125);
        engine.pushVariable("quantity", 1500);
        
        String template = """
        Report Generated: ${Date format="yyyy-MM-dd HH:mm:ss"}
        Order Date: ${Date value=${orderDate} format="MMMM dd, yyyy"}
        
        Financial Information:
        Amount: ${Number value=${amount} format="$#,##0.00"}
        Tax Rate: ${Number value=${percentage} format="#.##%"}
        Quantity: ${Number value=${quantity} format="#,##0"}
        
        Current Time: ${Time format="HH:mm:ss"}
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        System.out.println(doc.getPlainText());
    }
}
```

## Document Import

Import content from other documents.

```java
public class DocumentImportExample {
    public static void main(String[] args) throws IOException {
        OdtEngine engine = new OdtEngine();
        
        // Set up file resources
        engine.pushLeafVariable("header", new File("templates/header.odt"));
        engine.pushLeafVariable("footer", new File("templates/footer.odt"));
        engine.pushLeafVariable("terms", new File("templates/terms.odt"));
        
        // Set document variables
        engine.pushVariable("customerName", "John Smith");
        engine.pushVariable("contractNumber", "CNT-2024-001");
        
        String template = """
        ${Import $resource:"header"}
        
        Contract Number: ${contractNumber}
        Customer: ${customerName}
        
        This contract outlines the terms and conditions...
        
        ${Import $resource:"terms"}
        
        ${Import $resource:"footer"}
        """;
        
        OdtContainer doc = new OdtContainer(template);
        engine.run(doc);
        
        doc.save(new File("contract.odt"));
    }
}
```

## Invoice Generation

Complete example for generating invoices.

```java
public class InvoiceGenerator {
    
    public static class Invoice {
        private String invoiceNumber;
        private Date invoiceDate;
        private Customer customer;
        private List<LineItem> items;
        private double taxRate;
        
        // constructors, getters, setters...
        
        public double getSubtotal() {
            return items.stream().mapToDouble(LineItem::getTotal).sum();
        }
        
        public double getTaxAmount() {
            return getSubtotal() * taxRate;
        }
        
        public double getTotal() {
            return getSubtotal() + getTaxAmount();
        }
    }
    
    public static class LineItem {
        private String description;
        private int quantity;
        private double unitPrice;
        
        public double getTotal() {
            return quantity * unitPrice;
        }
        
        // constructors, getters, setters...
    }
    
    public static void generateInvoice() throws IOException {
        OdtEngine engine = new OdtEngine();
        
        // Create invoice data
        Customer customer = new Customer(
            "Acme Corporation",
            "123 Business Ave",
            "Business City, BC 12345"
        );
        
        List<LineItem> items = Arrays.asList(
            new LineItem("Web Development", 40, 75.00),
            new LineItem("Database Setup", 8, 100.00),
            new LineItem("Training Session", 4, 150.00)
        );
        
        Invoice invoice = new Invoice(
            "INV-2024-001",
            new Date(),
            customer,
            items,
            0.08 // 8% tax
        );
        
        engine.pushVariable("invoice", invoice);
        engine.pushVariable("company", "Tech Solutions Inc.");
        
        // Load template from file
        OdtContainer doc = new OdtContainer(new File("templates/invoice-template.odt"));
        engine.run(doc);
        
        // Save generated invoice
        doc.save(new File("invoices/invoice-" + invoice.getInvoiceNumber() + ".odt"));
        
        // Optionally generate PDF
        doc.writePDF(new FileOutputStream("invoices/invoice-" + invoice.getInvoiceNumber() + ".pdf"));
    }
}
```

### Invoice Template (`invoice-template.odt`)
```
INVOICE

${company}
123 Tech Street
Tech City, TC 12345

Invoice #: ${invoice.invoiceNumber}
Date: ${Date value=${invoice.invoiceDate} format="MMMM dd, yyyy"}

Bill To:
${invoice.customer.name}
${invoice.customer.address}

DESCRIPTION                QTY    UNIT PRICE    TOTAL
${For item in invoice.items}
${item.description}        ${item.quantity}    $${Number value=${item.unitPrice} format="#.##"}    $${Number value=${item.total} format="#.##"}
${EndFor}

                                    Subtotal: $${Number value=${invoice.subtotal} format="#,##0.00"}
                                         Tax: $${Number value=${invoice.taxAmount} format="#,##0.00"}
                                       TOTAL: $${Number value=${invoice.total} format="#,##0.00"}

Payment Terms: Net 30 days
```

## Report Generation

Generate comprehensive reports with charts and data.

```java
public class SalesReportGenerator {
    
    public static void generateMonthlySalesReport() throws IOException {
        OdtEngine engine = new OdtEngine();
        
        // Sales data
        List<SalesRecord> salesData = Arrays.asList(
            new SalesRecord("Product A", "North", 15000, 120),
            new SalesRecord("Product B", "South", 23000, 180),
            new SalesRecord("Product C", "East", 18000, 150),
            new SalesRecord("Product A", "West", 12000, 95)
        );
        
        // Calculate totals
        double totalRevenue = salesData.stream().mapToDouble(SalesRecord::getRevenue).sum();
        int totalUnits = salesData.stream().mapToInt(SalesRecord::getUnits).sum();
        
        // Group by region
        Map<String, List<SalesRecord>> byRegion = salesData.stream()
            .collect(Collectors.groupingBy(SalesRecord::getRegion));
        
        // Set variables
        engine.pushVariable("reportTitle", "Monthly Sales Report");
        engine.pushVariable("reportMonth", "March 2024");
        engine.pushVariable("salesData", salesData);
        engine.pushVariable("totalRevenue", totalRevenue);
        engine.pushVariable("totalUnits", totalUnits);
        engine.pushVariable("regionData", byRegion);
        engine.pushVariable("generatedDate", new Date());
        
        OdtContainer doc = new OdtContainer(new File("templates/sales-report-template.odt"));
        engine.run(doc);
        
        doc.save(new File("reports/sales-report-march-2024.odt"));
    }
}
```

## Email Templates

Generate personalized email content.

```java
public class EmailTemplateGenerator {
    
    public static void generateWelcomeEmail() throws IOException {
        TxtEngine engine = new TxtEngine();
        
        User newUser = new User(
            "John",
            "Doe", 
            "john.doe@example.com",
            "Premium"
        );
        
        engine.pushVariable("user", newUser);
        engine.pushVariable("companyName", "Tech Solutions");
        engine.pushVariable("supportEmail", "support@techsolutions.com");
        
        String template = """
        Subject: Welcome to ${companyName}, ${user.firstName}!
        
        Dear ${user.firstName} ${user.lastName},
        
        Welcome to ${companyName}! We're excited to have you join our community.
        
        Your account details:
        Email: ${user.email}
        Account Type: ${user.accountType}
        
        ${If user.accountType equals "Premium"}
        As a premium member, you have access to:
        - Priority support
        - Advanced features
        - Exclusive content
        ${EndIf}
        
        ${If user.accountType equals "Basic"}
        Consider upgrading to Premium for additional benefits:
        - Priority support
        - Advanced features
        - Exclusive content
        ${EndIf}
        
        Getting Started:
        1. Log in to your account
        2. Complete your profile
        3. Explore our features
        
        If you have any questions, please contact us at ${supportEmail}.
        
        Best regards,
        The ${companyName} Team
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        // Save email content
        doc.save(new File("emails/welcome-" + newUser.getEmail().replace("@", "-") + ".txt"));
        
        System.out.println("Generated email:");
        System.out.println(doc.getPlainText());
    }
    
    public static void generateBulkEmails() throws IOException {
        List<User> users = loadUsersFromDatabase(); // Your user loading logic
        
        for (User user : users) {
            TxtEngine engine = new TxtEngine();
            engine.pushVariable("user", user);
            engine.pushVariable("companyName", "Tech Solutions");
            
            // Personalize based on user preferences
            if (user.getPreferences().isNewsletterSubscribed()) {
                generateNewsletterEmail(engine, user);
            }
            
            if (user.getAccountType().equals("Premium") && user.hasUpcomingRenewal()) {
                generateRenewalReminderEmail(engine, user);
            }
        }
    }
}
```

## JSON Data Integration

Load template data from JSON files.

```java
public class JsonDataExample {
    public static void main(String[] args) throws IOException {
        TxtEngine engine = new TxtEngine();
        
        // Load data from JSON file
        String jsonData = Files.readString(Paths.get("data/employee-data.json"));
        engine.importData(jsonData);
        
        // Or import from InputStream
        // engine.importData(new FileInputStream("data/employee-data.json"));
        
        String template = """
        Employee Report - ${reportTitle}
        Generated: ${Date format="yyyy-MM-dd"}
        
        ${For employee in employees}
        Name: ${employee.name}
        Department: ${employee.department}
        Salary: ${Number value=${employee.salary} format="$#,##0"}
        ${EndFor}
        
        Total Employees: ${employees.size()}
        Average Salary: ${Number value=${averageSalary} format="$#,##0"}
        """;
        
        TxtContainer doc = new TxtContainer(template);
        engine.run(doc);
        
        System.out.println(doc.getPlainText());
    }
}
```

### JSON Data File (`employee-data.json`)
```json
{
  "variables": {
    "reportTitle": "Q1 2024 Employee Report",
    "employees": [
      {
        "name": "John Smith",
        "department": "Engineering", 
        "salary": 75000
      },
      {
        "name": "Jane Doe",
        "department": "Marketing",
        "salary": 65000
      },
      {
        "name": "Bob Johnson", 
        "department": "Sales",
        "salary": 70000
      }
    ],
    "averageSalary": 70000
  },
  "stringReplacements": {
    "company": "Tech Solutions Inc."
  }
}
```

## Running the Examples

To run these examples:

1. **Set up your project** with Underdocx dependency
2. **Create the necessary classes** (Person, Product, Customer, etc.)
3. **Prepare template files** for document import examples
4. **Run the main methods** to see the results

Each example demonstrates different aspects of Underdocx:
- Variable substitution
- Object model binding
- Control flow (loops and conditions)
- Formatting (dates and numbers)
- Document composition
- Real-world use cases

## Next Steps

After trying these basic examples, explore:

- [Advanced Usage](./AdvancedUsage.md) - Complex scenarios and optimizations
- [Template Syntax Guide](./TemplateSyntax.md) - Complete command reference
- [Integration Examples](./Integration.md) - Framework and system integration

## See Also

- [API Documentation](../README.md) - Complete API reference
- [Command Reference](../commands/) - All available template commands
- [Container Documentation](../containers/) - Document container APIs
- [Engine Documentation](../engines/) - Template engine APIs