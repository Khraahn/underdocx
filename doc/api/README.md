# Underdocx API Documentation

Underdocx is an extensible open source Java framework for manipulating various types of documents, including ODT text documents (LibreOffice/OpenOffice Writer), ODG Graphics (LibreOffice/OpenOffice Draw), ODP Presentations (LibreOffice/OpenOffice Impress), ODS Spreadsheets (LibreOffice/OpenOffice Calc), and plain text files. It serves as a template engine that uses placeholders which can be replaced with custom texts, images, tables, and other documents.

## Quick Start

Add Underdocx as a dependency to your project:

```xml
<dependency>
    <groupId>io.github.winterrifier</groupId>
    <artifactId>underdocx</artifactId>
    <version>0.12.1</version>
</dependency>
```

## Core Components

The Underdocx API consists of several main components:

### Document Containers
- **[OdtContainer](./containers/OdtContainer.md)** - OpenDocument Text documents (.odt)
- **[OdsContainer](./containers/OdsContainer.md)** - OpenDocument Spreadsheets (.ods)
- **[OdgContainer](./containers/OdgContainer.md)** - OpenDocument Graphics (.odg)
- **[OdpContainer](./containers/OdpContainer.md)** - OpenDocument Presentations (.odp)
- **[TxtContainer](./containers/TxtContainer.md)** - Plain text documents (.txt)

### Template Engines
- **[OdtEngine](./engines/OdtEngine.md)** - Template engine for ODT documents
- **[OdsEngine](./engines/OdsEngine.md)** - Template engine for ODS documents
- **[OdgEngine](./engines/OdgEngine.md)** - Template engine for ODG documents
- **[OdpEngine](./engines/OdpEngine.md)** - Template engine for ODP documents
- **[TxtEngine](./engines/TxtEngine.md)** - Template engine for text documents

### Core Interfaces
- **[EngineAPI](./interfaces/EngineAPI.md)** - Main engine interface
- **[DocContainer](./interfaces/DocContainer.md)** - Document container interface

### Template Commands
- **[String Commands](./commands/StringCommands.md)** - Text replacement and manipulation
- **[Control Flow](./commands/ControlFlow.md)** - Conditional statements and loops
- **[Data Operations](./commands/DataOperations.md)** - Model binding and variable handling
- **[Document Operations](./commands/DocumentOperations.md)** - Import, export, and manipulation
- **[Formatting](./commands/Formatting.md)** - Date, time, number formatting

### Utilities
- **[CLI Tool](./utilities/CLI.md)** - Command-line interface
- **[PDF Generation](./utilities/PDF.md)** - PDF conversion capabilities

## Basic Usage Examples

### Simple Text Replacement
```java
import org.underdocx.doctypes.txt.TxtContainer;
import org.underdocx.doctypes.txt.TxtEngine;

public class SimpleExample {
    public static void main(String[] args) throws IOException {
        TxtContainer doc = new TxtContainer("Hello ${name}");
        TxtEngine engine = new TxtEngine();
        engine.pushVariable("name", "World");
        engine.run(doc);
        
        System.out.println(doc.getPlainText()); // Prints: Hello World
    }
}
```

### ODT Document Processing
```java
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;

public class OdtExample {
    public static void main(String[] args) throws IOException {
        OdtContainer doc = new OdtContainer("Hello ${name}");
        OdtEngine engine = new OdtEngine();
        engine.pushVariable("name", "World");
        engine.run(doc);
        
        File outputFile = File.createTempFile("output", ".odt");
        doc.save(outputFile);
        System.out.println("Document saved: " + outputFile);
    }
}
```

### Complex Template with Variables and Objects
```java
import org.underdocx.doctypes.odf.odt.OdtContainer;
import org.underdocx.doctypes.odf.odt.OdtEngine;
import java.util.List;
import java.util.Arrays;

public class ComplexExample {
    public static void main(String[] args) throws IOException {
        // Load template from file
        OdtContainer doc = new OdtContainer(new FileInputStream("template.odt"));
        OdtEngine engine = new OdtEngine();
        
        // Set variables
        engine.pushVariable("title", "Sales Report");
        engine.pushVariable("date", new Date());
        
        // Set complex objects
        List<Person> persons = Arrays.asList(
            new Person("John", "Doe", 30),
            new Person("Jane", "Smith", 25)
        );
        engine.pushVariable("persons", persons);
        
        // Process template
        engine.run(doc);
        
        // Save result
        doc.save(new File("output.odt"));
        
        // Optionally generate PDF (requires LibreOffice)
        if (/* LibreOffice available */) {
            doc.writePDF(new FileOutputStream("output.pdf"));
        }
    }
}
```

## Template Syntax Overview

Underdocx uses a placeholder-based template syntax:

- **Variables**: `${variableName}` - Simple variable replacement
- **Model Properties**: `${model.property}` - Access object properties
- **Conditions**: `${If condition}...${EndIf}` - Conditional content
- **Loops**: `${For item in items}...${EndFor}` - Iterate over collections
- **String Operations**: `${String value="Hello"}` - String manipulation
- **Date/Time**: `${Date format="yyyy-MM-dd"}` - Format dates and times
- **Numbers**: `${Number value=123.45 format="#.##"}` - Format numbers
- **Import**: `${Import $resource:"filename"}` - Include other documents

## Error Handling

All engines return an `Optional<Problem>` from the `run()` method:

```java
Optional<Problem> result = engine.run(doc);
if (result.isPresent()) {
    System.err.println("Error: " + result.get().getMessage());
} else {
    System.out.println("Processing completed successfully");
}
```

## Advanced Features

### Custom Command Handlers
You can extend Underdocx with custom command handlers:

```java
engine.registerParametersCommandHandler(new MyCustomCommandHandler());
```

### Placeholder Styles
Different placeholder styles are supported:
- Default: `${command}`
- Double brackets: `{{command}}`
- Custom styles can be implemented

### Data Import
Import data from JSON:

```java
engine.importData(jsonInputStream);
// or
engine.importData(jsonString);
```

## API Reference

For detailed API documentation, see the individual component documentation:

- [Containers API Reference](./containers/)
- [Engines API Reference](./engines/)
- [Commands API Reference](./commands/)
- [Interfaces API Reference](./interfaces/)
- [Utilities API Reference](./utilities/)

## Examples and Tutorials

- [Basic Examples](./examples/BasicExamples.md)
- [Advanced Usage](./examples/AdvancedUsage.md)
- [Template Syntax Guide](./examples/TemplateSyntax.md)
- [Integration Examples](./examples/Integration.md)