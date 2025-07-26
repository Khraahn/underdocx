# OdtContainer

The `OdtContainer` class is the primary container for OpenDocument Text (.odt) documents in Underdocx. It extends `AbstractOdfContainer<OdfTextDocument>` and implements the `DocContainer<OdfTextDocument>` interface.

## Class Declaration
```java
public class OdtContainer extends AbstractOdfContainer<OdfTextDocument>
```

## Package
```java
package org.underdocx.doctypes.odf.odt;
```

## Constructors

### Default Constructor
```java
public OdtContainer()
```
Creates an empty ODT document.

### String Content Constructor
```java
public OdtContainer(String documentContent)
```
Creates an ODT document with the specified text content.

**Parameters:**
- `documentContent` - The initial text content for the document

### InputStream Constructor
```java
public OdtContainer(InputStream is) throws IOException
```
Creates an ODT document by loading from an InputStream.

**Parameters:**
- `is` - InputStream containing ODT document data

**Throws:**
- `IOException` - If an error occurs while reading the stream

### Resource Constructor
```java
public OdtContainer(Resource resource) throws IOException
```
Creates an ODT document from a Resource object.

**Parameters:**
- `resource` - Resource containing the document data

**Throws:**
- `IOException` - If an error occurs while reading the resource

### Byte Array Constructor
```java
public OdtContainer(byte[] data) throws IOException
```
Creates an ODT document from a byte array.

**Parameters:**
- `data` - Byte array containing ODT document data

**Throws:**
- `IOException` - If an error occurs while processing the data

### URI Constructor
```java
public OdtContainer(URI uri) throws IOException
```
Creates an ODT document by loading from a URI.

**Parameters:**
- `uri` - URI pointing to the ODT document

**Throws:**
- `IOException` - If an error occurs while loading from the URI

### File Constructor
```java
public OdtContainer(File file) throws IOException
```
Creates an ODT document by loading from a file.

**Parameters:**
- `file` - File containing the ODT document

**Throws:**
- `IOException` - If an error occurs while reading the file

### OdfTextDocument Constructor
```java
public OdtContainer(OdfTextDocument doc)
```
Creates an ODT container wrapping an existing OdfTextDocument.

**Parameters:**
- `doc` - Existing OdfTextDocument to wrap

## Methods

### getContentRoot()
```java
public OfficeTextElement getContentRoot()
```
Returns the root content element of the ODT document.

**Returns:**
- `OfficeTextElement` - The root content element

### getFileExtension()
```java
public String getFileExtension()
```
Returns the file extension for ODT files.

**Returns:**
- `String` - "odt"

### load(InputStream)
```java
public void load(InputStream is) throws IOException
```
Loads an ODT document from an InputStream.

**Parameters:**
- `is` - InputStream containing ODT document data

**Throws:**
- `IOException` - If an error occurs while loading

### save(OutputStream)
```java
public void save(OutputStream os) throws IOException
```
Saves the ODT document to an OutputStream.

**Parameters:**
- `os` - OutputStream to write the document to

**Throws:**
- `IOException` - If an error occurs while saving

### appendText(String)
```java
public void appendText(String content)
```
Appends text content to the document. Handles line breaks by creating new paragraphs.

**Parameters:**
- `content` - Text content to append

## Static Methods

### createDocument(String)
```java
public static OdtContainer createDocument(String content)
```
Factory method to create an ODT document with the specified content.

**Parameters:**
- `content` - Initial text content

**Returns:**
- `OdtContainer` - New container with the specified content

## Inherited Methods from AbstractOdfContainer

### writePDF(OutputStream)
```java
public void writePDF(OutputStream os) throws IOException
```
Converts the ODT document to PDF and writes it to the output stream. Requires LibreOffice to be installed and configured.

**Parameters:**
- `os` - OutputStream to write the PDF to

**Throws:**
- `IOException` - If an error occurs during PDF generation

### showPDF()
```java
public void showPDF() throws IOException
```
Converts the document to PDF and opens it with the default PDF viewer.

**Throws:**
- `IOException` - If an error occurs during PDF generation or opening

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
OdtContainer doc = new OdtContainer();
doc.appendText("Hello World");

// Create document with content
OdtContainer doc2 = new OdtContainer("Initial content");

// Save to file
doc.save(new File("output.odt"));
```

### Loading and Processing
```java
// Load from file
OdtContainer doc = new OdtContainer(new File("template.odt"));

// Process with engine
OdtEngine engine = new OdtEngine();
engine.pushVariable("name", "John");
engine.run(doc);

// Save result
doc.save(new File("result.odt"));
```

### PDF Generation
```java
OdtContainer doc = new OdtContainer("Hello ${name}");
OdtEngine engine = new OdtEngine();
engine.pushVariable("name", "World");
engine.run(doc);

// Generate PDF (requires LibreOffice)
try (FileOutputStream fos = new FileOutputStream("output.pdf")) {
    doc.writePDF(fos);
}
```

### Working with Resources
```java
// Load from classpath resource
Resource resource = new Resource("templates/document.odt");
OdtContainer doc = new OdtContainer(resource);

// Process and save
engine.run(doc);
doc.save(new File("processed.odt"));
```

## Thread Safety

`OdtContainer` is not thread-safe. If you need to process documents concurrently, create separate container instances for each thread.

## See Also

- [OdtEngine](../engines/OdtEngine.md) - Template engine for ODT documents
- [DocContainer](../interfaces/DocContainer.md) - Base container interface
- [AbstractOdfContainer](../base/AbstractOdfContainer.md) - Base class for ODF containers