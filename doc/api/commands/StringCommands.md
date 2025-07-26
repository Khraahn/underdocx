# String Commands

String commands in Underdocx provide various ways to manipulate and format text content in templates. These commands handle variable replacement, string operations, and text formatting.

## Variable Access Commands

### ${variable}
Simple variable replacement with the variable name.

**Syntax:**
```
${variableName}
```

**Example:**
```java
engine.pushVariable("name", "John");
// Template: "Hello ${name}"
// Result: "Hello John"
```

### ${$variable}
Short syntax for variable access using the `$` prefix.

**Syntax:**
```
${$variableName}
```

**Example:**
```java
engine.pushVariable("title", "Manager");
// Template: "Position: ${$title}"
// Result: "Position: Manager"
```

### ${model.property}
Access properties of model objects using dot notation.

**Syntax:**
```
${model.propertyName}
${model.nested.property}
```

**Example:**
```java
Person person = new Person("John", "Doe");
engine.setModel(person);
// Template: "${firstName} ${lastName}"
// Result: "John Doe"
```

### ${$model.property}
Short syntax for model property access.

**Syntax:**
```
${$model.propertyName}
```

**Example:**
```java
engine.setModel(user);
// Template: "Email: ${$user.email}"
// Result: "Email: john@example.com"
```

## String Command

The `String` command provides explicit string operations and formatting.

### Basic String Output
**Syntax:**
```
${String value="text"}
${String value=${variable}}
```

**Parameters:**
- `value` - The string value to output (required)

**Examples:**
```java
// Static string
// Template: "${String value=\"Hello World\"}"
// Result: "Hello World"

// Variable string
engine.pushVariable("message", "Welcome");
// Template: "${String value=${message}}"
// Result: "Welcome"
```

### String with Fallback
**Syntax:**
```
${String value=${variable} fallback="default"}
```

**Parameters:**
- `value` - The primary string value
- `fallback` - Default value if primary is null/empty

**Example:**
```java
engine.pushVariable("optionalText", null);
// Template: "${String value=${optionalText} fallback=\"No text available\"}"
// Result: "No text available"
```

### String Formatting Options
**Syntax:**
```
${String value=${text} uppercase="true"}
${String value=${text} lowercase="true"}
${String value=${text} capitalize="true"}
```

**Parameters:**
- `uppercase` - Convert to uppercase
- `lowercase` - Convert to lowercase  
- `capitalize` - Capitalize first letter

**Examples:**
```java
engine.pushVariable("text", "hello world");

// Template: "${String value=${text} uppercase=\"true\"}"
// Result: "HELLO WORLD"

// Template: "${String value=${text} capitalize=\"true\"}"
// Result: "Hello world"
```

## String Replacement Commands

### registerStringReplacement()
Register global string replacements that are applied automatically.

**Java API:**
```java
engine.registerStringReplacement(String key, String replacement)
```

**Example:**
```java
engine.registerStringReplacement("company", "Acme Corporation");
engine.registerStringReplacement("support", "support@acme.com");

// Template: "Contact ${company} at ${support}"
// Result: "Contact Acme Corporation at support@acme.com"
```

## Advanced String Operations

### Concatenation
**Syntax:**
```
${Concat values=[${var1}, ${var2}, ${var3}] separator=" "}
```

**Parameters:**
- `values` - Array of values to concatenate
- `separator` - String to insert between values (optional)

**Example:**
```java
engine.pushVariable("first", "John");
engine.pushVariable("last", "Doe");

// Template: "${Concat values=[${first}, ${last}] separator=\" \"}"
// Result: "John Doe"
```

### Join Arrays/Collections
**Syntax:**
```
${Join items=${collection} separator=", "}
```

**Parameters:**
- `items` - Collection to join
- `separator` - String to insert between items

**Example:**
```java
List<String> fruits = Arrays.asList("apple", "banana", "orange");
engine.pushVariable("fruits", fruits);

// Template: "${Join items=${fruits} separator=\", \"}"
// Result: "apple, banana, orange"
```

### Replace Text
**Syntax:**
```
${Replace text=${source} find="old" replace="new"}
```

**Parameters:**
- `text` - Source text
- `find` - Text to find
- `replace` - Replacement text

**Example:**
```java
engine.pushVariable("message", "Hello World");

// Template: "${Replace text=${message} find=\"World\" replace=\"Universe\"}"
// Result: "Hello Universe"
```

## Conditional String Output

### Missing Data Handling
**Syntax:**
```
${String value=${variable} onNull="N/A"}
${String value=${variable} onEmpty="Empty"}
${String value=${variable} onError="Error"}
```

**Parameters:**
- `onNull` - Text to show when value is null
- `onEmpty` - Text to show when value is empty string
- `onError` - Text to show when there's an error accessing the value

**Example:**
```java
engine.pushVariable("optional", null);

// Template: "${String value=${optional} onNull=\"Not provided\"}"
// Result: "Not provided"
```

## String Escaping and Encoding

### HTML Escaping
**Syntax:**
```
${String value=${text} escapeHtml="true"}
```

**Example:**
```java
engine.pushVariable("html", "<script>alert('xss')</script>");

// Template: "${String value=${html} escapeHtml=\"true\"}"
// Result: "&lt;script&gt;alert('xss')&lt;/script&gt;"
```

### URL Encoding
**Syntax:**
```
${String value=${text} urlEncode="true"}
```

**Example:**
```java
engine.pushVariable("url", "hello world");

// Template: "${String value=${url} urlEncode=\"true\"}"
// Result: "hello%20world"
```

## Complex String Examples

### Template with Multiple String Operations
```java
TxtEngine engine = new TxtEngine();

String template = """
User Profile:
Name: ${String value="${user.firstName} ${user.lastName}" capitalize="true"}
Email: ${String value=${user.email} lowercase="true"}
Status: ${String value=${user.status} onNull="Unknown" uppercase="true"}
Bio: ${String value=${user.bio} onEmpty="No bio provided"}
""";

User user = new User("john", "DOE", "John.Doe@EXAMPLE.COM", "active", "");
engine.pushVariable("user", user);

TxtContainer doc = new TxtContainer(template);
engine.run(doc);
// Results in properly formatted user profile
```

### Dynamic String Building
```java
TxtEngine engine = new TxtEngine();

// Register common patterns
engine.registerStringReplacement("fullName", "${String value=\"${firstName} ${lastName}\" capitalize=\"true\"}");
engine.registerStringReplacement("email", "${String value=${emailAddress} lowercase=\"true\"}");

String template = """
Contact Information:
Full Name: ${fullName}
Email: ${email}
Phone: ${String value=${phone} onNull="Not provided"}
""";

engine.pushVariable("firstName", "jane");
engine.pushVariable("lastName", "smith");
engine.pushVariable("emailAddress", "JANE.SMITH@EXAMPLE.COM");
engine.pushVariable("phone", null);

TxtContainer doc = new TxtContainer(template);
engine.run(doc);
```

### List Processing with String Operations
```java
TxtEngine engine = new TxtEngine();

String template = """
Employee Directory:
${For employee in employees}
- ${String value="${employee.name}" capitalize="true"} (${String value=${employee.department} uppercase="true"})
  Email: ${String value=${employee.email} lowercase="true"}
  Joined: ${String value=${employee.joinDate} onNull="Unknown"}
${EndFor}

Departments: ${Join items=${departments} separator=" | "}
""";

List<Employee> employees = Arrays.asList(
    new Employee("john doe", "ENGINEERING", "John.Doe@company.com", "2020-01-15"),
    new Employee("jane smith", "marketing", "JANE@company.com", null)
);

List<String> departments = Arrays.asList("Engineering", "Marketing", "Sales");

engine.pushVariable("employees", employees);
engine.pushVariable("departments", departments);

TxtContainer doc = new TxtContainer(template);
engine.run(doc);
```

## Error Handling

String commands handle various error conditions gracefully:

```java
// Null values
engine.pushVariable("nullVar", null);
// Template: "${String value=${nullVar} onNull=\"Default\"}"
// Result: "Default"

// Missing variables
// Template: "${String value=${missingVar} onError=\"Variable not found\"}"
// Result: "Variable not found"

// Empty strings
engine.pushVariable("emptyVar", "");
// Template: "${String value=${emptyVar} onEmpty=\"No content\"}"
// Result: "No content"
```

## Best Practices

1. **Use appropriate fallbacks** for optional data
2. **Normalize case** for consistent output
3. **Escape content** when generating HTML or other markup
4. **Use string replacements** for frequently used patterns
5. **Combine with conditional commands** for complex logic

## ODT-Specific Considerations

When using string commands in ODT documents:

- Text formatting is preserved from the template
- Line breaks create new paragraphs
- Rich text markup can be included
- Images and other objects can be referenced

## Performance Notes

- String operations are generally fast
- Regular expressions in find/replace can impact performance
- Large string concatenations may use significant memory
- Consider using aliases for frequently repeated patterns

## See Also

- [Control Flow Commands](./ControlFlow.md) - Conditional and loop commands
- [Formatting Commands](./Formatting.md) - Date, time, and number formatting
- [Data Operations](./DataOperations.md) - Model and variable management