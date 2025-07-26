# TxtEngine

The `TxtEngine` class is the template engine for plain text documents in Underdocx. It extends `AbstractEngine<TxtContainer, TxtXml>` and implements the `EngineAPI<TxtContainer, TxtXml>` interface.

## Class Declaration
```java
public class TxtEngine extends AbstractEngine<TxtContainer, TxtXml>
```

## Package
```java
package org.underdocx.doctypes.txt;
```

## Constructors

### Default Constructor
```java
public TxtEngine()
```
Creates a text engine with default placeholder providers.

### Custom Placeholder Provider Constructor
```java
public TxtEngine(GenericTextualPlaceholdersProviderFactory<TxtContainer, ParametersPlaceholderData, TxtXml> parameters)
```
Creates a text engine with a custom placeholder provider factory.

**Parameters:**
- `parameters` - Custom placeholder provider factory

## Supported Commands

The `TxtEngine` automatically registers the following command handlers:

### Text and Variable Commands
- **Ignore** - `${Ignore}` - Ignores content without processing
- **Model** - `${model.property}` - Access model object properties
- **String** - `${String value="text"}` - String replacement and manipulation
- **ShortModelString** - `${$model.property}` - Short syntax for model access
- **ShortVarString** - `${$variable}` - Short syntax for variable access
- **Variable** - `${variable}` - Variable replacement

### Control Flow Commands
- **For** - `${For item in items}...${EndFor}` - Loop over collections
- **If** - `${If condition}...${EndIf}` - Conditional content

### Formatting Commands
- **Date** - `${Date format="yyyy-MM-dd"}` - Date formatting
- **Time** - `${Time format="HH:mm:ss"}` - Time formatting
- **Number** - `${Number value=123.45 format="#.##"}` - Number formatting
- **Counter** - `${Counter}` - Auto-incrementing counter

### Document Commands
- **Import** - `${Import $resource:"filename"}` - Import other text files

### Utility Commands
- **Join** - `${Join items separator=","}` - Join collections with separator
- **Concat** - `${Concat}` - Concatenate values
- **Calc** - `${Calc expression}` - Calculate mathematical expressions
- **Replace** - `${Replace}` - Find and replace operations

### Advanced Commands
- **Multi** - Execute multiple commands
- **Alias** - Create command aliases
- **DeleteNodesEod** - Clean up placeholder markers

## Methods

### String Replacement Management
```java
public void registerStringReplacement(String key, String replacement)
```
Registers a string replacement that will be applied during template processing.

**Parameters:**
- `key` - The placeholder key to replace
- `replacement` - The replacement string

### Alias Management
```java
public void registerAlias(String key, String placeholder, Pair<String, String>... attrReplacements)
public void registerAlias(String key, ParametersPlaceholderData placeholder, Pair<String, String>... attrReplacements)
public void registerAlias(AliasCommandHandler.AliasData aliasData)
```
Registers command aliases for simplified template syntax.

**Parameters:**
- `key` - The alias key
- `placeholder` - The target placeholder or command
- `attrReplacements` - Optional attribute replacements
- `aliasData` - Complete alias data object

### Custom Command Registration
```java
public void registerParametersCommandHandler(MCommandHandler<TxtContainer, ParametersPlaceholderData, TxtXml> commandHandler)
```
Registers a custom command handler.

**Parameters:**
- `commandHandler` - The custom command handler to register

### Modifier Access
```java
public ModifiersProvider<TxtContainer, TxtXml> getModifiers()
```
Returns the modifiers provider for advanced customization.

**Returns:**
- `ModifiersProvider<TxtContainer, TxtXml>` - The modifiers provider

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

#### Data Import
```java
public void importData(DataNode<?> importData)
public void importData(InputStream is)
public void importData(String json)
```

#### Template Processing
```java
public Optional<Problem> run(TxtContainer doc)
```

## Usage Examples

### Basic Template Processing
```java
// Create engine and document
TxtEngine engine = new TxtEngine();
TxtContainer doc = new TxtContainer("Hello ${name}!");

// Set variables
engine.pushVariable("name", "World");

// Process template
Optional<Problem> result = engine.run(doc);
if (result.isPresent()) {
    System.err.println("Error: " + result.get().getMessage());
} else {
    System.out.println(doc.getPlainText()); // "Hello World!"
}
```

### String Replacements
```java
TxtEngine engine = new TxtEngine();

// Register string replacements
engine.registerStringReplacement("company", "Acme Corporation");
engine.registerStringReplacement("email", "info@acme.com");

TxtContainer doc = new TxtContainer("Contact ${company} at ${email}");
engine.run(doc);

System.out.println(doc.getPlainText()); // "Contact Acme Corporation at info@acme.com"
```

### Complex Object Processing
```java
TxtEngine engine = new TxtEngine();

// Set model object
Customer customer = new Customer("John Doe", "john@example.com", true);
engine.setModel(customer);

String template = """
Customer Information:
Name: ${name}
Email: ${email}
Premium: ${isPremium}
""";

TxtContainer doc = new TxtContainer(template);
engine.run(doc);
System.out.println(doc.getPlainText());
```

### Loop Processing
```java
TxtEngine engine = new TxtEngine();

// Template with loop
String template = """
Shopping List:
${For item in items}
- ${item.name}: $${Number value=${item.price} format="#.##"}
${EndFor}
Total: $${Number value=${total} format="#.##"}
""";

TxtContainer doc = new TxtContainer(template);

// Set data
List<Item> items = Arrays.asList(
    new Item("Apple", 0.99),
    new Item("Banana", 0.59),
    new Item("Orange", 1.29)
);
double total = items.stream().mapToDouble(Item::getPrice).sum();

engine.pushVariable("items", items);
engine.pushVariable("total", total);

engine.run(doc);
System.out.println(doc.getPlainText());
```

### Conditional Content
```java
TxtEngine engine = new TxtEngine();

String template = """
Dear ${customer.name},

${If customer.isPremium}
Thank you for being a premium member!
You save 20% on all purchases.
${EndIf}

${If not customer.isPremium}
Upgrade to premium today and save 20%!
${EndIf}

Best regards,
Customer Service
""";

TxtContainer doc = new TxtContainer(template);

Customer customer = new Customer("Jane Smith", false);
engine.pushVariable("customer", customer);

engine.run(doc);
System.out.println(doc.getPlainText());
```

### File Import
```java
TxtEngine engine = new TxtEngine();

// Template that imports other files
String template = """
Email Template

${Import $resource:"header.txt"}

Dear ${name},

This is the main content of the email.

${Import $resource:"footer.txt"}
""";

TxtContainer doc = new TxtContainer(template);

// Set up file resources
engine.pushLeafVariable("header.txt", new File("templates/header.txt"));
engine.pushLeafVariable("footer.txt", new File("templates/footer.txt"));
engine.pushVariable("name", "John");

engine.run(doc);
System.out.println(doc.getPlainText());
```

### Date and Time Formatting
```java
TxtEngine engine = new TxtEngine();

String template = """
Report Generated: ${Date format="yyyy-MM-dd HH:mm:ss"}
Report Date: ${Date value=${reportDate} format="MMMM dd, yyyy"}
Processing Time: ${Time format="HH:mm:ss"}
""";

TxtContainer doc = new TxtContainer(template);

engine.pushVariable("reportDate", new Date());
engine.run(doc);
System.out.println(doc.getPlainText());
```

### JSON Data Import
```java
TxtEngine engine = new TxtEngine();

String jsonData = """
{
    "variables": {
        "title": "Monthly Report",
        "month": "March 2024",
        "data": {
            "sales": 15000,
            "expenses": 8000,
            "profit": 7000
        }
    },
    "stringReplacements": {
        "company": "Acme Corp",
        "currency": "USD"
    }
}
""";

// Import data from JSON
engine.importData(jsonData);

String template = """
${company} ${title} - ${month}

Financial Summary:
Sales: ${currency} ${Number value=${data.sales} format="#,##0"}
Expenses: ${currency} ${Number value=${data.expenses} format="#,##0"}
Profit: ${currency} ${Number value=${data.profit} format="#,##0"}
""";

TxtContainer doc = new TxtContainer(template);
engine.run(doc);
System.out.println(doc.getPlainText());
```

### Alias Usage
```java
TxtEngine engine = new TxtEngine();

// Register aliases for commonly used patterns
engine.registerAlias("currency", "${Number value=${value} format=\"$#,##0.00\"}");
engine.registerAlias("percent", "${Number value=${value} format=\"#.##%\"}");

String template = """
Sales Report:
Revenue: ${currency value=${revenue}}
Tax Rate: ${percent value=${taxRate}}
""";

TxtContainer doc = new TxtContainer(template);
engine.pushVariable("revenue", 15000.50);
engine.pushVariable("taxRate", 0.08);

engine.run(doc);
System.out.println(doc.getPlainText());
```

### Custom Command Handler
```java
public class UpperCaseCommandHandler extends AbstractTextualCommandHandler<TxtContainer, TxtXml> {
    public static final Regex KEYS = new Regex("Upper");
    
    @Override
    public CommandHandlerResult tryExecuteCommand(Selection<TxtContainer, ParametersPlaceholderData, TxtXml> selection) {
        String value = getStringValue(selection, "value");
        if (value != null) {
            modifiers.getStringModifier().modify(selection, value.toUpperCase());
            return CommandHandlerResult.CONSUMED;
        }
        return CommandHandlerResult.NOT_CONSUMED;
    }
}

// Register and use custom command
TxtEngine engine = new TxtEngine();
engine.registerParametersCommandHandler(new UpperCaseCommandHandler());

TxtContainer doc = new TxtContainer("Hello ${Upper value=\"world\"}!");
engine.run(doc);
System.out.println(doc.getPlainText()); // "Hello WORLD!"
```

### Email Template Generation
```java
TxtEngine engine = new TxtEngine();

// Email template
String template = """
From: ${sender.email}
To: ${recipient.email}
Subject: ${subject}

Dear ${recipient.name},

${If type equals "welcome"}
Welcome to our service! We're excited to have you on board.
${EndIf}

${If type equals "reminder"}
This is a friendly reminder about your upcoming appointment on ${appointment.date}.
${EndIf}

${For item in attachments}
Attachment: ${item}
${EndFor}

Best regards,
${sender.name}
${company}
""";

TxtContainer doc = new TxtContainer(template);

// Set email data
Sender sender = new Sender("John Smith", "john@company.com");
Recipient recipient = new Recipient("Jane Doe", "jane@example.com");

engine.pushVariable("sender", sender);
engine.pushVariable("recipient", recipient);
engine.pushVariable("subject", "Welcome to Our Service");
engine.pushVariable("type", "welcome");
engine.pushVariable("company", "Acme Corporation");

engine.run(doc);

// Save as email file
doc.save(new File("generated-email.txt"));
```

### Multi-line String Processing
```java
TxtEngine engine = new TxtEngine();

String template = """
Configuration File:
# Generated by Underdocx
server.port=${config.port}
server.host=${config.host}
database.url=jdbc:mysql://${config.db.host}:${config.db.port}/${config.db.name}
database.username=${config.db.username}
database.password=${config.db.password}

# Application settings
app.name=${config.app.name}
app.version=${config.app.version}
app.debug=${If config.debug}true${Else}false${EndIf}
""";

TxtContainer doc = new TxtContainer(template);

// Set configuration data
Map<String, Object> config = Map.of(
    "port", 8080,
    "host", "localhost",
    "db", Map.of(
        "host", "db.example.com",
        "port", 3306,
        "name", "myapp",
        "username", "user",
        "password", "secret"
    ),
    "app", Map.of(
        "name", "MyApplication",
        "version", "1.0.0"
    ),
    "debug", false
);

engine.pushVariable("config", config);
engine.run(doc);

// Save configuration file
doc.save(new File("application.properties"));
```

## Thread Safety

`TxtEngine` is not thread-safe. Create separate engine instances for concurrent processing.

## Performance Considerations

- Text engines are lightweight and fast
- Memory usage is minimal for small to medium templates
- Large text files may require streaming approaches
- Regular expressions in templates can impact performance

## See Also

- [TxtContainer](../containers/TxtContainer.md) - Text document container
- [AbstractEngine](../base/AbstractEngine.md) - Base class for engines
- [EngineAPI](../interfaces/EngineAPI.md) - Core engine interface
- [Template Commands](../commands/) - Available template commands