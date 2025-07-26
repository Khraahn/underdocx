# Template Engines API Reference

Template engines in Underdocx process templates by finding placeholders and replacing them with dynamic content. Each engine is designed to work with specific document types and provides format-specific functionality.

## Available Engines

### OpenDocument Format (ODF) Engines
- **[OdtEngine](./OdtEngine.md)** - Template engine for ODT text documents
- **[OdsEngine](./OdsEngine.md)** - Template engine for ODS spreadsheets
- **[OdgEngine](./OdgEngine.md)** - Template engine for ODG graphics
- **[OdpEngine](./OdpEngine.md)** - Template engine for ODP presentations

### Text Engines  
- **[TxtEngine](./TxtEngine.md)** - Template engine for plain text documents

## Core Features

All template engines provide:

- **Variable Management** - Set and access template variables
- **Model Binding** - Bind complex objects as data models
- **Template Processing** - Execute template commands and replacements
- **Command Registration** - Register custom command handlers
- **Data Import** - Import data from JSON and other sources
- **Error Handling** - Comprehensive error reporting

## Engine Hierarchy

```
EngineAPI<C, D>
└── AbstractEngine<C, D>
    ├── TxtEngine
    └── AbstractOdfEngine<C, D>
        ├── OdtEngine
        ├── OdsEngine
        ├── OdgEngine
        └── OdpEngine
```

## Template Commands

All engines support a rich set of template commands:

### Text and Variables
- `${variable}` - Variable replacement
- `${model.property}` - Model property access
- `${String value="text"}` - String operations

### Control Flow
- `${If condition}...${EndIf}` - Conditional content
- `${For item in items}...${EndFor}` - Loops and iteration

### Formatting
- `${Date format="yyyy-MM-dd"}` - Date formatting
- `${Number value=123.45 format="#.##"}` - Number formatting
- `${Time format="HH:mm:ss"}` - Time formatting

### Utilities
- `${Join items separator=","}` - Join collections
- `${Concat}` - Concatenate values
- `${Replace}` - Find and replace operations

## Quick Reference

### Creating Engines

```java
// Default engines
OdtEngine odtEngine = new OdtEngine();
TxtEngine txtEngine = new TxtEngine();

// Custom placeholder styles
OdtEngine customEngine = new OdtEngine(new DoubleBracketsPlaceholdersProviderFactory());
```

### Variable Management

```java
// Simple variables
engine.pushVariable("name", "John Doe");
engine.pushVariable("age", 30);
engine.pushLeafVariable("status", "active");

// Complex objects
Person person = new Person("John", "Doe");
engine.setModel(person);
engine.pushVariable("customer", customer);

// JSON data
engine.importData(jsonString);
engine.importData(jsonInputStream);
```

### Template Processing

```java
// Process template
Optional<Problem> result = engine.run(container);

// Handle errors
if (result.isPresent()) {
    System.err.println("Error: " + result.get().getMessage());
} else {
    System.out.println("Processing completed successfully");
}
```

### Custom Commands

```java
// Register string replacements
engine.registerStringReplacement("company", "Acme Corp");

// Register aliases
engine.registerAlias("currency", "Number", 
    Pair.of("format", "$#,##0.00"));

// Register custom command handlers
engine.registerParametersCommandHandler(new CustomCommandHandler());
```

## Supported Commands by Engine

### All Engines
- String operations and variables
- Control flow (If, For)
- Date/Time/Number formatting
- Basic utilities (Join, Concat, Replace)

### ODF Engines (ODT, ODS, ODG, ODP)
- Image processing and replacement
- Document import/export
- Page style manipulation
- Table operations (ForRows, ForList)
- Rich text formatting

### ODT-Specific
- Text document operations
- Paragraph and page styling
- Underdocx framework commands

### ODS-Specific  
- Spreadsheet operations
- Cell and row manipulation

### ODG/ODP-Specific
- Graphics and presentation operations
- Page cloning and manipulation
- Drawing object operations

## Data Binding

### Simple Variables
```java
engine.pushVariable("title", "Sales Report");
engine.pushVariable("date", new Date());
engine.pushVariable("amount", 1234.56);
```

### Object Models
```java
// Direct object binding
Customer customer = new Customer("John", "Doe");
engine.setModel(customer);

// Nested object access via variables
engine.pushVariable("order", order);
// Template: ${order.customer.name}
```

### Collections
```java
List<Product> products = getProducts();
engine.pushVariable("products", products);

// Template: 
// ${For product in products}
// ${product.name}: ${product.price}
// ${EndFor}
```

### JSON Data Import
```java
String jsonData = """
{
    "variables": {
        "title": "Report",
        "items": [{"name": "Item 1"}, {"name": "Item 2"}]
    },
    "model": {
        "company": "Acme Corp"
    }
}
""";
engine.importData(jsonData);
```

## Error Handling

Template engines provide comprehensive error handling:

```java
Optional<Problem> result = engine.run(container);
if (result.isPresent()) {
    Problem problem = result.get();
    
    // Error information
    String code = problem.getCode();
    String message = problem.getMessage();
    String details = problem.getDetails();
    
    // Handle specific error types
    switch (code) {
        case "MISSING_VARIABLE":
            // Handle missing variable
            break;
        case "TEMPLATE_SYNTAX_ERROR":
            // Handle syntax error
            break;
        default:
            // Handle general error
            break;
    }
}
```

## Performance Optimization

### Engine Reuse
```java
// Reuse engines for better performance
OdtEngine engine = new OdtEngine();

for (Document doc : documents) {
    engine.clearVariables(); // Clear previous state
    engine.pushVariable("data", doc.getData());
    engine.run(doc.getContainer());
}
```

### Memory Management
```java
// Use streaming for large datasets
engine.pushVariable("largeDataset", createStreamingDataProvider());

// Clean up resources
engine.clearVariables();
container.close(); // If applicable
```

### Batch Processing
```java
// Process multiple templates efficiently
List<Template> templates = getTemplates();
OdtEngine engine = new OdtEngine();

templates.parallelStream().forEach(template -> {
    OdtEngine threadEngine = new OdtEngine(); // Thread-safe approach
    processTemplate(threadEngine, template);
});
```

## Thread Safety

**Important:** Template engines are **not thread-safe**. For concurrent processing:

```java
// Create separate engine instances per thread
public class TemplateProcessor {
    public void processTemplate(TemplateData data) {
        OdtEngine engine = new OdtEngine(); // Thread-local instance
        engine.pushVariable("data", data);
        // Process template...
    }
}
```

## Configuration

### Environment Settings
```java
// Disable image processing globally
UnderdocxEnv.getInstance().disableImagePlaceholderProvider = true;

// Set LibreOffice path for PDF generation
UnderdocxEnv.getInstance().libreOfficeExecutable = "/usr/bin/libreoffice";
```

### Custom Placeholder Styles
```java
// Use {{variable}} instead of ${variable}
GenericTextualPlaceholdersProviderFactory factory = 
    new DoubleBracketsPlaceholdersProviderFactory();
OdtEngine engine = new OdtEngine(factory);
```

## See Also

- [Document Containers](../containers/) - Work with different document types
- [Template Commands](../commands/) - Complete command reference
- [Basic Examples](../examples/BasicExamples.md) - Usage examples and tutorials
- [Core Interfaces](../interfaces/) - Engine interfaces and contracts