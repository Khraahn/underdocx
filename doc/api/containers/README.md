# Document Containers API Reference

Document containers in Underdocx are responsible for loading, manipulating, and saving documents of various formats. Each container type provides format-specific functionality while implementing the common `DocContainer` interface.

## Available Containers

### OpenDocument Format (ODF) Containers
- **[OdtContainer](./OdtContainer.md)** - OpenDocument Text documents (.odt)
- **[OdsContainer](./OdsContainer.md)** - OpenDocument Spreadsheets (.ods)  
- **[OdgContainer](./OdgContainer.md)** - OpenDocument Graphics (.odg)
- **[OdpContainer](./OdpContainer.md)** - OpenDocument Presentations (.odp)

### Text Containers
- **[TxtContainer](./TxtContainer.md)** - Plain text documents (.txt)

## Common Features

All document containers provide:

- **Loading** - Load documents from files, streams, URIs, or byte arrays
- **Saving** - Save documents to files or output streams
- **Content Management** - Add and manipulate document content
- **Temporary Files** - Create temporary files for preview or processing
- **File Type Detection** - Automatic file extension handling

## ODF-Specific Features

ODF containers (ODT, ODS, ODG, ODP) additionally provide:

- **PDF Generation** - Convert documents to PDF using LibreOffice
- **Rich Content** - Support for complex formatting, images, tables
- **Document Structure** - Access to document elements and metadata
- **Style Management** - Apply and modify document styles

## Container Hierarchy

```
DocContainer<D>
├── AbstractDocContainer<D>
│   └── TxtContainer
└── AbstractOdfContainer<D>
    ├── OdtContainer
    ├── OdsContainer
    ├── OdgContainer
    └── OdpContainer
```

## Quick Reference

### Creating Containers

```java
// Empty containers
OdtContainer odt = new OdtContainer();
TxtContainer txt = new TxtContainer();

// From string content
OdtContainer odtWithContent = new OdtContainer("Hello ${name}");
TxtContainer txtWithContent = new TxtContainer("Hello ${name}");

// From files
OdtContainer odtFromFile = new OdtContainer(new File("template.odt"));
TxtContainer txtFromFile = new TxtContainer(new FileInputStream("template.txt"));

// From resources
OdtContainer odtFromResource = new OdtContainer(new Resource("templates/doc.odt"));
```

### Basic Operations

```java
// Load and save
container.load(inputStream);
container.save(outputStream);
container.save(new File("output.odt"));

// Content manipulation
container.appendText("Additional content");

// File operations
File tempFile = container.createTmpFile();
String uri = container.createURI();
container.show(); // Open with default application
```

### ODF-Specific Operations

```java
// PDF generation (requires LibreOffice)
odtContainer.writePDF(new FileOutputStream("output.pdf"));
odtContainer.showPDF(); // Open PDF with default viewer

// Access document structure
OdfTextDocument document = odtContainer.getDocument();
OfficeTextElement contentRoot = odtContainer.getContentRoot();
```

## Error Handling

All container operations may throw `IOException` for file-related errors:

```java
try {
    OdtContainer doc = new OdtContainer(new File("template.odt"));
    doc.save(new File("output.odt"));
} catch (IOException e) {
    System.err.println("Error processing document: " + e.getMessage());
}
```

## Performance Considerations

- **Memory Usage** - Large documents require proportional memory
- **File I/O** - Consider using buffered streams for large files
- **Temporary Files** - Clean up temporary files when no longer needed
- **PDF Generation** - Requires LibreOffice installation and can be slow

## Thread Safety

Document containers are **not thread-safe**. Use separate container instances for concurrent processing.

## See Also

- [Template Engines](../engines/) - Process templates with containers
- [Core Interfaces](../interfaces/) - Base interfaces and contracts
- [Basic Examples](../examples/BasicExamples.md) - Usage examples
- [CLI Tool](../utilities/CLI.md) - Command-line document processing