# OdtEngine

The `OdtEngine` class is the primary template engine for OpenDocument Text (.odt) documents in Underdocx. It extends `AbstractOdfEngine<OdtContainer, OdfTextDocument>` and implements the `EngineAPI<OdtContainer, OdfTextDocument>` interface.

## Class Declaration
```java
public class OdtEngine extends AbstractOdfEngine<OdtContainer, OdfTextDocument>
```

## Package
```java
package org.underdocx.doctypes.odf.odt;
```

## Constructors

### Default Constructor
```java
public OdtEngine()
```
Creates an ODT engine with default placeholder providers.

### Custom Placeholder Provider Constructor
```java
public OdtEngine(GenericTextualPlaceholdersProviderFactory<OdtContainer, ParametersPlaceholderData, OdfTextDocument> parameters)
```
Creates an ODT engine with a custom placeholder provider factory.

**Parameters:**
- `parameters` - Custom placeholder provider factory

## Supported Commands

The `OdtEngine` automatically registers the following command handlers:

### Text and Variable Commands
- **Ignore** - `${Ignore}` - Ignores content without processing
- **Model** - `${model.property}` - Access model object properties
- **String** - `${String value="text"}` - String replacement and manipulation
- **ShortModelString** - `${$model.property}` - Short syntax for model access
- **ShortVarString** - `${$variable}` - Short syntax for variable access
- **Variable** - `${variable}` - Variable replacement

### Control Flow Commands
- **For** - `${For item in items}...${EndFor}` - Loop over collections
- **ForRows** - `${ForRows row in data}...${EndForRows}` - Loop over table rows
- **ForList** - `${ForList item in list}...${EndForList}` - Loop over list items
- **If** - `${If condition}...${EndIf}` - Conditional content

### Formatting Commands
- **Date** - `${Date format="yyyy-MM-dd"}` - Date formatting
- **Time** - `${Time format="HH:mm:ss"}` - Time formatting
- **Number** - `${Number value=123.45 format="#.##"}` - Number formatting
- **Counter** - `${Counter}` - Auto-incrementing counter

### Document Commands
- **Import** - `${Import $resource:"filename"}` - Import other ODT documents
- **Export** - `${Export}` - Export content sections
- **PageStyle** - `${PageStyle}` - Apply page styles
- **UnderdocxCommand** - `${Underdocx}` - Framework-specific commands

### Utility Commands
- **Join** - `${Join items separator=","}` - Join collections with separator
- **Concat** - `${Concat}` - Concatenate values
- **Calc** - `${Calc expression}` - Calculate mathematical expressions
- **Replace** - `${Replace}` - Find and replace operations
- **Remove** - `${Remove}` - Remove content sections
- **Clone** - `${Clone}` - Clone document sections
- **CreateImage** - `${CreateImage}` - Create images programmatically

### Image Commands
- **Image** - Image placeholder replacement (enabled by default unless disabled)

### Advanced Commands
- **Multi** - Execute multiple commands
- **Alias** - Create command aliases
- **DeleteNodesEod** - Clean up placeholder markers

## Methods

### Inherited from AbstractEngine

#### Variable Management
```java
public void pushVariable(String name, Object value)
public void pushVariable(String name, DataNode<?> tree)
public void pushLeafVariable(String name, Object value)
public void pushJsonVariable(String name, String json) throws JsonProcessingException
public Optional<DataNode<?>> getVariable(String name)
```

#### Model Management
```java
public void setModel(Object object)
public void setModel(DataNode<?> tree)
public void setModel(Object object, ReflectionDataNode.Resolver resolver)
```

#### String Replacements
```java
public void registerStringReplacement(String key, String replacement)
```

#### Alias Management
```java
public void registerAlias(AliasCommandHandler.AliasData aliasData)
```

#### Data Import
```java
public void importData(DataNode<?> importData)
public void importData(InputStream is)
public void importData(String json)
```

#### Template Processing
```java
public Optional<Problem> run(OdtContainer doc)
```

#### Custom Command Handlers
```java
public void registerParametersCommandHandler(MCommandHandler<OdtContainer, ParametersPlaceholderData, OdfTextDocument> commandHandler)
```

## Usage Examples

### Basic Template Processing
```java
// Create engine and document
OdtEngine engine = new OdtEngine();
OdtContainer doc = new OdtContainer("Hello ${name}!");

// Set variables
engine.pushVariable("name", "World");

// Process template
Optional<Problem> result = engine.run(doc);
if (result.isPresent()) {
    System.err.println("Error: " + result.get().getMessage());
} else {
    doc.save(new File("output.odt"));
}
```

### Complex Object Processing
```java
OdtEngine engine = new OdtEngine();
OdtContainer doc = new OdtContainer(new File("template.odt"));

// Set model object
Person person = new Person("John", "Doe", 30);
engine.setModel(person);

// Set additional variables
engine.pushVariable("title", "Employee Report");
engine.pushVariable("date", new Date());

// Process and save
engine.run(doc);
doc.save(new File("employee-report.odt"));
```

### Loop Processing
```java
OdtEngine engine = new OdtEngine();

// Template with loop
String template = """
Employee List:
${For employee in employees}
Name: ${employee.name}
Position: ${employee.position}
Salary: ${Number value=${employee.salary} format="$#,##0.00"}

${EndFor}
""";

OdtContainer doc = new OdtContainer(template);

// Set data
List<Employee> employees = Arrays.asList(
    new Employee("John Doe", "Developer", 75000),
    new Employee("Jane Smith", "Manager", 85000)
);
engine.pushVariable("employees", employees);

engine.run(doc);
doc.save(new File("employee-list.odt"));
```

### Conditional Content
```java
OdtEngine engine = new OdtEngine();

String template = """
Dear ${customer.name},

${If customer.isPremium}
Thank you for being a premium customer!
You have access to exclusive features.
${EndIf}

${If not customer.isPremium}
Consider upgrading to premium for additional benefits.
${EndIf}

Best regards,
Customer Service
""";

OdtContainer doc = new OdtContainer(template);

Customer customer = new Customer("John Doe", true);
engine.pushVariable("customer", customer);

engine.run(doc);
doc.save(new File("customer-letter.odt"));
```

### Document Import
```java
OdtEngine engine = new OdtEngine();

// Template that imports other documents
String template = """
Main Document

${Import $resource:"header.odt"}

Content section here.

${Import $resource:"footer.odt"}
""";

OdtContainer doc = new OdtContainer(template);

// Set up resources
engine.pushLeafVariable("header.odt", new File("templates/header.odt"));
engine.pushLeafVariable("footer.odt", new File("templates/footer.odt"));

engine.run(doc);
doc.save(new File("combined-document.odt"));
```

### String Replacements and Aliases
```java
OdtEngine engine = new OdtEngine();

// Register string replacements
engine.registerStringReplacement("company", "Acme Corporation");
engine.registerStringReplacement("support", "support@acme.com");

// Template using replacements
OdtContainer doc = new OdtContainer("Welcome to ${company}! Contact us at ${support}");

engine.run(doc);
doc.save(new File("welcome.odt"));
```

### JSON Data Import
```java
OdtEngine engine = new OdtEngine();

String jsonData = """
{
    "variables": {
        "title": "Sales Report",
        "quarter": "Q1 2024",
        "sales": [
            {"product": "Widget A", "amount": 1500},
            {"product": "Widget B", "amount": 2300}
        ]
    },
    "model": {
        "company": "Acme Corp",
        "address": "123 Main St"
    }
}
""";

// Import data from JSON
engine.importData(jsonData);

// Template using imported data
String template = """
${title} - ${quarter}
Company: ${model.company}

Sales Data:
${For sale in sales}
Product: ${sale.product} - Amount: ${Number value=${sale.amount} format="$#,##0"}
${EndFor}
""";

OdtContainer doc = new OdtContainer(template);
engine.run(doc);
doc.save(new File("sales-report.odt"));
```

### Image Processing
```java
OdtEngine engine = new OdtEngine();

// Template with image placeholder
OdtContainer doc = new OdtContainer(new File("template-with-image.odt"));

// Set image variables
engine.pushLeafVariable("logo", new File("images/company-logo.png"));
engine.pushLeafVariable("signature", new File("images/signature.png"));

engine.run(doc);
doc.save(new File("document-with-images.odt"));
```

### Custom Command Handler
```java
public class CustomCommandHandler extends AbstractTextualCommandHandler<OdtContainer, OdfTextDocument> {
    public static final Regex KEYS = new Regex("CustomCommand");
    
    @Override
    public CommandHandlerResult tryExecuteCommand(Selection<OdtContainer, ParametersPlaceholderData, OdfTextDocument> selection) {
        // Custom command implementation
        return CommandHandlerResult.CONSUMED;
    }
}

// Register custom command
OdtEngine engine = new OdtEngine();
engine.registerParametersCommandHandler(new CustomCommandHandler());

// Use in template
OdtContainer doc = new OdtContainer("${CustomCommand param=\"value\"}");
engine.run(doc);
```

### Error Handling
```java
OdtEngine engine = new OdtEngine();
OdtContainer doc = new OdtContainer("Hello ${missingVariable}");

Optional<Problem> result = engine.run(doc);
if (result.isPresent()) {
    Problem problem = result.get();
    System.err.println("Error Code: " + problem.getCode());
    System.err.println("Message: " + problem.getMessage());
    System.err.println("Details: " + problem.getDetails());
} else {
    System.out.println("Processing completed successfully");
    doc.save(new File("output.odt"));
}
```

## Configuration

### Disable Image Processing
```java
// Disable image placeholder provider globally
UnderdocxEnv.getInstance().disableImagePlaceholderProvider = true;

OdtEngine engine = new OdtEngine();
// Image commands will not be registered
```

### Custom Placeholder Styles
```java
// Use double brackets instead of ${...}
GenericTextualPlaceholdersProviderFactory<OdtContainer, ParametersPlaceholderData, OdfTextDocument> factory = 
    new OdfDoubleBracketsPlaceholdersProviderFactory<>();

OdtEngine engine = new OdtEngine(factory);

// Now use {{variable}} instead of ${variable}
OdtContainer doc = new OdtContainer("Hello {{name}}!");
```

## Thread Safety

`OdtEngine` is not thread-safe. Create separate engine instances for concurrent processing.

## Performance Considerations

- Reuse engine instances when possible (but not across threads)
- Large documents may require significant memory
- PDF generation requires LibreOffice installation
- Image processing can be memory-intensive

## See Also

- [OdtContainer](../containers/OdtContainer.md) - ODT document container
- [AbstractOdfEngine](../base/AbstractOdfEngine.md) - Base class for ODF engines
- [EngineAPI](../interfaces/EngineAPI.md) - Core engine interface
- [Template Commands](../commands/) - Available template commands