/*
MIT License

Copyright (c) 2024 Gerald Winter

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package org.underdocx.environment.err;

/**
 * Collection for predefined {@link Problem} definitions. Each enum entry implements {@link Problematic}
 * to simplify checks and fire {@link ProblemException}.
 */
public enum Problems implements Problematic {
    COMMAND_HANDLER_FAILED("Command handler failed execution"),
    UNEXPECTED_EXCEPION_CAUGHT("Exception caught during engine execution"),
    MISSING_VALUE("a mandatory value is missing or can't be resolved or computed"),
    PLACEHOLDER_PARSE_ERROR("Can't parse placeholder"),
    INVALID_PLACEHOLDER_STRUCTURE("invalid placeholder structure, e.g. If without EndIf etc"),
    INVALID_VALUE("value is invalid (null or incompatible type)"),
    UNEXPECTED_TYPE_DETECTED("an unexpected value type has been received and can't be handled"),
    UNEXPECTED_LOOP_CONDITION("unexpected loop condition"),
    LIBREOFFICE_ENV_NOT_SET("Ensure environment variable LIBREOFFICE is set to LibreOffice executable"),
    CODEC_ERROR("Can't encode or decode a string or object"),
    CODEC_PARSE_ERROR("Can't parse string, syntax error"),
    IO_EXCEPTION("An IO Exception has been caught, failed to load or save a resource"),
    ODF_FRAMEWORK_OPERARTION_EXCEPTION("An unexpected exception occurred accessing an ODF document "),
    INVALID_IF_CONDITION("If command contains an invalid structure or invalid values"),
    CANT_FIND_DOM_ELEMENT("a certain required DOM node can't be found"),
    PLACEHOLDER_TOOLKIT_MISSING("placeholder toolkit instance is missing"),
    EXPECTED_PLACEHOLDER_MISSING("A mandatory placeholder can not be found"),
    IMPORT_DATA_SCHEMA("The structure/schema of the import data is invalid");

    private String description = null;

    Problems(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public String getKey() {
        return this.name().toLowerCase().replace('_', '.');
    }

    public Problem toProblem() {
        return new Problem(getKey(), getDescription());
    }

}
