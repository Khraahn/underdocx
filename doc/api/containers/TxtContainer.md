# TxtContainer

The `TxtContainer` class is the container for plain text documents in Underdocx. It extends `AbstractDocContainer<TxtXml>` and implements the `DocContainer<TxtXml>` interface.

## Class Declaration
```java
public class TxtContainer extends AbstractDocContainer<TxtXml>
```

## Package
```java
package org.underdocx.doctypes.txt;
```

## Constructors

### Default Constructor
```java
public TxtContainer()
```
Creates an empty text document.

### String Content Constructor
```java
public TxtContainer(String content)
```
Creates a text document with the specified content.

**Parameters:**
- `content` - The initial text content for the document

### Resource Constructor
```java
public TxtContainer(Resource content) throws IOException
```
Creates a text document from a Resource object.

**Parameters:**
- `content` - Resource containing the document data

**Throws:**
- `IOException` - If an error occurs while reading the resource

### InputStream Constructor
```java
public TxtContainer(InputStream is) throws IOException
```
Creates a text document by loading from an InputStream.

**Parameters:**
- `is` - InputStream containing text document data

**Throws:**
- `IOException` - If an error occurs while reading the stream

### Byte Array Constructor
```java
public TxtContainer(byte[] data) throws IOException
```
Creates a text document from a byte array.

**Parameters:**
- `data` - Byte array containing text document data

**Throws:**
- `IOException` - If an error occurs while processing the data

## Methods

### getPlainText()
```java
public String getPlainText()
```
Returns the plain text content of the document.

**Returns:**
- `String` - The document's text content

### getFileExtension()
```java
public String getFileExtension()
```
Returns the file extension for text files.

**Returns:**
- `String` - "txt"

### load(InputStream)
```java
public void load(InputStream is) throws IOException
```
Loads a text document from an InputStream.

**Parameters:**
- `is` - InputStream containing text document data

**Throws:**
- `IOException` - If an error occurs while loading

### save(OutputStream)
```java
public void save(OutputStream os) throws IOException
```
Saves the text document to an OutputStream.

**Parameters:**
- `os` - OutputStream to write the document to

**Throws:**
- `IOException` - If an error occurs while saving

### appendText(String)
```java
public void appendText(String content)
```
Appends text content to the document.

**Parameters:**
- `content` - Text content to append

### toString()
```java
public String toString()
```
Returns the plain text content of the document.

**Returns:**
- `String` - The document's text content

## Inherited Methods from DocContainer Interface

### createTmpFile()
```java
public File createTmpFile() throws IOException
```
Creates a temporary file with the document content.

**Returns:**
- `File` - Temporary file containing the document

**Throws:**
- `IOException` - If an error occurs while creating the file

### createTmpFile(Long)
```java
public File createTmpFile(Long lifetime) throws IOException
```
Creates a temporary file with specified lifetime.

**Parameters:**
- `lifetime` - Lifetime in milliseconds

**Returns:**
- `File` - Temporary file containing the document

**Throws:**
- `IOException` - If an error occurs while creating the file

### show()
```java
public void show() throws IOException
```
Creates a temporary file and opens it with the default application.

**Throws:**
- `IOException` - If an error occurs while creating or opening the file

### createURI()
```java
public String createURI() throws IOException
```
Creates a temporary file and returns its URI as a string.

**Returns:**
- `String` - URI of the temporary file

**Throws:**
- `IOException` - If an error occurs while creating the file

## Usage Examples

### Basic Usage
```java
// Create empty document
TxtContainer doc = new TxtContainer();
doc.appendText("Hello World");

// Create document with content
TxtContainer doc2 = new TxtContainer("Initial content");

// Get content
String content = doc.getPlainText();
System.out.println(content);
```

### Template Processing
```java
// Create template
TxtContainer doc = new TxtContainer("Hello ${name}, welcome to ${place}!");

// Process with engine
TxtEngine engine = new TxtEngine();
engine.pushVariable("name", "John");
engine.pushVariable("place", "Underdocx");
engine.run(doc);

// Get result
System.out.println(doc.getPlainText()); // "Hello John, welcome to Underdocx!"
```

### File Operations
```java
// Load from file
TxtContainer doc = new TxtContainer(new FileInputStream("template.txt"));

// Process template
TxtEngine engine = new TxtEngine();
engine.pushVariable("data", "some value");
engine.run(doc);

// Save to file
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    doc.save(fos);
}
```

### Working with Resources
```java
// Load from classpath resource
Resource resource = new Resource("templates/email-template.txt");
TxtContainer doc = new TxtContainer(resource);

// Process and save
TxtEngine engine = new TxtEngine();
engine.pushVariable("recipient", "user@example.com");
engine.pushVariable("subject", "Welcome");
engine.run(doc);

doc.save(new File("generated-email.txt"));
```

### Conditional and Loop Processing
```java
TxtContainer doc = new TxtContainer(
    "Dear ${name},\n" +
    "${If hasItems}" +
    "Your items:\n" +
    "${For item in items}" +
    "- ${item}\n" +
    "${EndFor}" +
    "${EndIf}" +
    "Best regards"
);

TxtEngine engine = new TxtEngine();
engine.pushVariable("name", "John");
engine.pushVariable("hasItems", true);
engine.pushVariable("items", Arrays.asList("Item 1", "Item 2", "Item 3"));

engine.run(doc);
System.out.println(doc.getPlainText());
```

### String Replacements
```java
TxtContainer doc = new TxtContainer("Welcome to ${company}!");

TxtEngine engine = new TxtEngine();
engine.registerStringReplacement("company", "Acme Corp");

engine.run(doc);
System.out.println(doc.getPlainText()); // "Welcome to Acme Corp!"
```

### JSON Data Import
```java
TxtContainer doc = new TxtContainer("Hello ${user.name}, your email is ${user.email}");

String jsonData = """
{
    "variables": {
        "user": {
            "name": "John Doe",
            "email": "john@example.com"
        }
    }
}
""";

TxtEngine engine = new TxtEngine();
engine.importData(jsonData);
engine.run(doc);

System.out.println(doc.getPlainText()); // "Hello John Doe, your email is john@example.com"
```

## Thread Safety

`TxtContainer` is not thread-safe. If you need to process documents concurrently, create separate container instances for each thread.

## Performance Considerations

- Text containers are lightweight and efficient for simple template processing
- For large text files, consider processing in chunks or streaming
- Memory usage is proportional to the size of the text content

## See Also

- [TxtEngine](../engines/TxtEngine.md) - Template engine for text documents
- [DocContainer](../interfaces/DocContainer.md) - Base container interface
- [AbstractDocContainer](../base/AbstractDocContainer.md) - Base class for document containers