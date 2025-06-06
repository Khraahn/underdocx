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

package org.underdocx.doctypes.tools.placeholder;

import org.underdocx.common.enumerator.Enumerator;
import org.w3c.dom.Node;

public class GenericPlaceholderFromSectionsEnumerator implements Enumerator<Node> {

    private final Enumerator<Node> validSections;
    private final PlaceholdersFromSectionExtractor extractor;
    private Enumerator<Node> currentCollectedNodes = Enumerator.of();

    public GenericPlaceholderFromSectionsEnumerator(Enumerator<Node> validSections, PlaceholdersFromSectionExtractor extractor, Node firstValidNode) {
        this.validSections = validSections;
        this.extractor = extractor;
        if (validSections.hasNext()) {
            currentCollectedNodes = (extractor.extractPlaceholders(validSections.next(), firstValidNode));
        }
    }

    private GenericPlaceholderFromSectionsEnumerator(GenericPlaceholderFromSectionsEnumerator other) {
        this.validSections = other.validSections.cloneEnumerator();
        this.extractor = other.extractor;
        this.currentCollectedNodes = other.currentCollectedNodes.cloneEnumerator();
    }

    @Override
    public boolean hasNext() {
        while (currentCollectedNodes.isEmpty() && validSections.hasNext()) {
            Node nextSection = validSections.next();
            currentCollectedNodes = extractor.extractPlaceholders(nextSection, null);
        }
        return !currentCollectedNodes.isEmpty();
    }

    @Override
    public Node next() {
        while (currentCollectedNodes.isEmpty() && validSections.hasNext()) {
            Node nextSection = validSections.next();
            currentCollectedNodes = extractor.extractPlaceholders(nextSection, null);
        }
        if (!currentCollectedNodes.isEmpty()) {
            return currentCollectedNodes.next();
        } else {
            return null;
        }
    }

    @Override
    public GenericPlaceholderFromSectionsEnumerator cloneEnumerator() {
        return new GenericPlaceholderFromSectionsEnumerator(this);
    }

    public interface PlaceholdersFromSectionExtractor {
        Enumerator<Node> extractPlaceholders(Node section, Node firstValidNode);
    }
}
