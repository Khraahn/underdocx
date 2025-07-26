# Underdocx CLI Tool

The Underdocx CLI (Command Line Interface) tool allows you to process document templates from the command line without writing Java code. It's particularly useful for batch processing, automation scripts, and integration with other tools.

## Main Class
```java
org.underdocx.common.cli.UnderdocxEngineRunner
```

## Installation

### Using Maven Assembly Plugin
Build the CLI tool using the `buildCli` profile:

```bash
mvn clean package -P buildCli
```

This creates a JAR file with all dependencies in the `target` directory:
- `underdocx-0.12.1-jar-with-dependencies.jar`

### Running the CLI
```bash
java -jar underdocx-0.12.1-jar-with-dependencies.jar [options]
```

## Command Syntax

```bash
java -jar underdocx.jar <engine-type> <input-file> <output-file> [data-files...]
```

### Parameters

1. **engine-type** - The document engine to use:
   - `odt` - OpenDocument Text documents
   - `ods` - OpenDocument Spreadsheets  
   - `odg` - OpenDocument Graphics
   - `odp` - OpenDocument Presentations

2. **input-file** - Path to the template document

3. **output-file** - Path for the output document

4. **data-files** (optional) - One or more JSON data files to import

## Supported Document Types

### ODT (OpenDocument Text)
```bash
java -jar underdocx.jar odt template.odt output.odt data.json
```

### ODS (OpenDocument Spreadsheet)
```bash
java -jar underdocx.jar ods template.ods output.ods data.json
```

### ODG (OpenDocument Graphics)
```bash
java -jar underdocx.jar odg template.odg output.odg data.json
```

### ODP (OpenDocument Presentation)
```bash
java -jar underdocx.jar odp template.odp output.odp data.json
```

## Data File Format

Data files must be in JSON format and can contain:

### Variables
```json
{
  "variables": {
    "name": "John Doe",
    "age": 30,
    "department": "Engineering",
    "skills": ["Java", "Python", "JavaScript"]
  }
}
```

### Model Objects
```json
{
  "model": {
    "company": "Acme Corporation",
    "address": {
      "street": "123 Main St",
      "city": "Anytown",
      "zip": "12345"
    }
  }
}
```

### String Replacements
```json
{
  "stringReplacements": {
    "company": "Acme Corporation",
    "support": "support@acme.com",
    "website": "https://acme.com"
  }
}
```

### Aliases
```json
{
  "alias": [
    {
      "key": "currency",
      "replaceKey": "Number",
      "attributes": {
        "format": "$#,##0.00"
      }
    }
  ]
}
```

### Complete Data File Example
```json
{
  "variables": {
    "reportTitle": "Quarterly Sales Report",
    "quarter": "Q1 2024",
    "sales": [
      {"product": "Widget A", "amount": 15000, "region": "North"},
      {"product": "Widget B", "amount": 23000, "region": "South"},
      {"product": "Widget C", "amount": 18000, "region": "East"}
    ]
  },
  "model": {
    "company": "Acme Corporation",
    "contact": {
      "email": "reports@acme.com",
      "phone": "+1-555-0123"
    }
  },
  "stringReplacements": {
    "footer": "© 2024 Acme Corporation. All rights reserved."
  }
}
```

## Usage Examples

### Basic Template Processing
```bash
# Simple variable replacement
java -jar underdocx.jar odt letter-template.odt output-letter.odt customer-data.json
```

### Multiple Data Files
```bash
# Combine data from multiple sources
java -jar underdocx.jar odt report-template.odt monthly-report.odt \
  sales-data.json employee-data.json config.json
```

### Report Generation
```bash
# Generate sales report
java -jar underdocx.jar ods sales-template.ods march-sales.ods sales-march-2024.json
```

### Presentation Creation
```bash
# Create presentation from template
java -jar underdocx.jar odp presentation-template.odp company-presentation.odp \
  company-data.json slides-content.json
```

## Template Examples

### ODT Letter Template (`letter-template.odt`)
```
Dear ${customer.name},

Thank you for your order #${order.number} placed on ${Date value=${order.date} format="MMMM dd, yyyy"}.

Order Details:
${For item in order.items}
- ${item.name}: ${Number value=${item.price} format="$#,##0.00"} x ${item.quantity}
${EndFor}

Total: ${Number value=${order.total} format="$#,##0.00"}

Best regards,
${company}
```

### Data File (`customer-data.json`)
```json
{
  "variables": {
    "customer": {
      "name": "John Smith"
    },
    "order": {
      "number": "ORD-2024-001",
      "date": "2024-03-15",
      "items": [
        {"name": "Product A", "price": 29.99, "quantity": 2},
        {"name": "Product B", "price": 49.99, "quantity": 1}
      ],
      "total": 109.97
    }
  },
  "stringReplacements": {
    "company": "Acme Corporation"
  }
}
```

### Command
```bash
java -jar underdocx.jar odt letter-template.odt customer-letter.odt customer-data.json
```

## Advanced Features

### Environment Variables
Set environment variables for configuration:

```bash
# Set LibreOffice path for PDF generation
export LIBREOFFICE="/usr/bin/libreoffice"

# Set home directory for LibreOffice
export LIBREOFFICE_HOME="/home/user/.libreoffice"
```

### Batch Processing Script
Create a shell script for batch processing:

```bash
#!/bin/bash
# process-templates.sh

TEMPLATES_DIR="templates"
OUTPUT_DIR="output"
DATA_DIR="data"

# Process all ODT templates
for template in $TEMPLATES_DIR/*.odt; do
    filename=$(basename "$template" .odt)
    data_file="$DATA_DIR/$filename.json"
    output_file="$OUTPUT_DIR/$filename-$(date +%Y%m%d).odt"
    
    if [ -f "$data_file" ]; then
        echo "Processing $template with $data_file..."
        java -jar underdocx.jar odt "$template" "$output_file" "$data_file"
    else
        echo "No data file found for $template"
    fi
done
```

### Windows Batch Script
```batch
@echo off
rem process-templates.bat

set TEMPLATES_DIR=templates
set OUTPUT_DIR=output
set DATA_DIR=data

for %%f in (%TEMPLATES_DIR%\*.odt) do (
    set template=%%f
    set filename=%%~nf
    set data_file=%DATA_DIR%\%%~nf.json
    set output_file=%OUTPUT_DIR%\%%~nf-%date:~10,4%%date:~4,2%%date:~7,2%.odt
    
    if exist "!data_file!" (
        echo Processing !template! with !data_file!...
        java -jar underdocx.jar odt "!template!" "!output_file!" "!data_file!"
    ) else (
        echo No data file found for !template!
    )
)
```

## Integration Examples

### Makefile Integration
```makefile
# Makefile for document generation

UNDERDOCX_JAR = lib/underdocx.jar
TEMPLATES_DIR = templates
OUTPUT_DIR = build
DATA_DIR = data

# Generate all reports
reports: sales-report.odt employee-report.odt

sales-report.odt: $(TEMPLATES_DIR)/sales-template.odt $(DATA_DIR)/sales-data.json
	java -jar $(UNDERDOCX_JAR) odt $< $(OUTPUT_DIR)/$@ $(DATA_DIR)/sales-data.json

employee-report.odt: $(TEMPLATES_DIR)/employee-template.odt $(DATA_DIR)/employee-data.json
	java -jar $(UNDERDOCX_JAR) odt $< $(OUTPUT_DIR)/$@ $(DATA_DIR)/employee-data.json

clean:
	rm -f $(OUTPUT_DIR)/*.odt
```

### CI/CD Pipeline (GitHub Actions)
```yaml
name: Generate Documents

on:
  push:
    paths:
      - 'templates/**'
      - 'data/**'

jobs:
  generate:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v2
      
      - name: Set up Java
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          distribution: 'temurin'
          
      - name: Download Underdocx CLI
        run: |
          wget -O underdocx.jar https://github.com/winterrifier/underdocx/releases/download/v0.12.1/underdocx-0.12.1-jar-with-dependencies.jar
          
      - name: Generate documents
        run: |
          mkdir -p output
          for template in templates/*.odt; do
            filename=$(basename "$template" .odt)
            data_file="data/$filename.json"
            output_file="output/$filename.odt"
            
            if [ -f "$data_file" ]; then
              java -jar underdocx.jar odt "$template" "$output_file" "$data_file"
            fi
          done
          
      - name: Upload artifacts
        uses: actions/upload-artifact@v2
        with:
          name: generated-documents
          path: output/
```

## Error Handling

### Exit Codes
- `0` - Success
- Negative values - Error occurred

### Common Errors

1. **File Not Found**
```bash
Error: Template file not found: template.odt
```

2. **Invalid Engine Type**
```bash
Error: Unsupported engine type: txt
Supported types: odt, ods, odg, odp
```

3. **JSON Parse Error**
```bash
Error: Invalid JSON in data file: data.json
```

4. **Template Processing Error**
```bash
Error: Failed to process template - missing variable: ${customer.name}
```

### Debugging
Use verbose output for debugging:
```bash
java -Djava.util.logging.level=ALL -jar underdocx.jar odt template.odt output.odt data.json
```

## Performance Considerations

- Large templates may require significant memory
- Multiple data files are processed sequentially
- Consider splitting large datasets across multiple files
- Use appropriate JVM heap settings for large documents:

```bash
java -Xmx2g -jar underdocx.jar odt large-template.odt output.odt large-data.json
```

## Limitations

- Only supports ODF document formats (no TXT via CLI)
- No interactive mode
- Limited error reporting compared to programmatic API
- No support for custom command handlers

## See Also

- [OdtEngine](../engines/OdtEngine.md) - ODT template engine
- [OdsEngine](../engines/OdsEngine.md) - ODS template engine
- [Template Commands](../commands/) - Available template commands
- [JSON Data Format](../examples/JSONDataFormat.md) - Detailed JSON schema