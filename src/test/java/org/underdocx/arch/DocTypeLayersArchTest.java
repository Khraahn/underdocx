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

package org.underdocx.arch;

import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition;

@AnalyzeClasses(packages = "org.underdocx")
public class DocTypeLayersArchTest {


    // classes that are in ODF layer should not access or depend on sublayers
    @ArchTest
    public static final ArchRule odfDoesNotAccessSubLayersOrEngine = ArchRuleDefinition.noClasses()
            .that().resideInAnyPackage("org.underdocx.doctypes.odf")
            .or().resideInAnyPackage("org.underdocx.doctypes.odf.commands..")
            .or().resideInAnyPackage("org.underdocx.doctypes.odf.modifiers..")
            .or().resideInAnyPackage("org.underdocx.doctypes.odf.tools..")
            .or().resideInAnyPackage("org.underdocx.doctypes.odf.placeholderprovider..")
            .or().resideInAnyPackage("org.underdocx.doctypes.odf.commons..")
            .should().accessClassesThat().resideInAnyPackage(
                    "org.underdocx.doctypes.odf.odt..",
                    "org.underdocx.doctypes.odf.odg..",
                    "org.underdocx.doctypes.odf.odp..",
                    "org.underdocx.doctypes.msoffice..",
                    "org.underdocx.doctypes.txt..");

    @ArchTest
    public static final ArchRule doctypeDoesNotAccessSubLayersOrEngine = ArchRuleDefinition.noClasses()
            .that().resideInAnyPackage("org.underdocx.doctypes.")
            .or().resideInAnyPackage("org.underdocx.doctypes.commands..")
            .or().resideInAnyPackage("org.underdocx.doctypes.modifiers..")
            .or().resideInAnyPackage("org.underdocx.doctypes.tools..")
            .or().resideInAnyPackage("org.underdocx.doctypes.commons..")
            .should().accessClassesThat().resideInAnyPackage(
                    "org.underdocx.doctypes.odf..",
                    "org.underdocx.doctypes.msoffice..",
                    "org.underdocx.doctypes.txt.."
            );

    @ArchTest
    public static final ArchRule txtDoesNotAccessOdf = ArchRuleDefinition.noClasses()
            .that().resideInAnyPackage("org.underdocx.doctypes.txt..")
            .should().accessClassesThat().resideInAnyPackage(
                    "org.underdocx.doctypes.odf..",
                    "org.underdocx.doctypes.msoffice.."
            );
}
