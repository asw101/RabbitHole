package edu.cmu.cs.dennisc.xml;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class XMLUtilitiesTest {

  @Rule
  public TemporaryFolder temporaryFolder = new TemporaryFolder();

  private Document createDocumentWithRoot(String rootName) {
    Document document = XMLUtilities.createDocument();
    Element root = document.createElement(rootName);
    document.appendChild(root);
    return document;
  }

  private byte[] writeToBytes(Document document) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    XMLUtilities.write(document, outputStream);
    return outputStream.toByteArray();
  }

  private Document roundTrip(Document document) {
    return XMLUtilities.read(new ByteArrayInputStream(writeToBytes(document)));
  }

  private Element child(Document document, Element parent, String tagName) {
    Element element = document.createElement(tagName);
    parent.appendChild(element);
    return element;
  }

  private List<String> tagNames(List<Element> elements) {
    List<String> result = new ArrayList<String>();
    for (Element element : elements) {
      result.add(element.getTagName());
    }
    return result;
  }

  private int countNodeType(Element element, short nodeType) {
    int count = 0;
    NodeList nodeList = element.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      if (nodeList.item(i).getNodeType() == nodeType) {
        count++;
      }
    }
    return count;
  }

  private void assertMalformedXmlThrows(String xml) {
    try {
      XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      fail("Expected RuntimeException");
    } catch (RuntimeException runtimeException) {
      assertNotNull(runtimeException.getCause());
    }
  }

  @Test
  public void createDocumentReturnsNonNullDocument() {
    Document document = XMLUtilities.createDocument();
    assertNotNull(document);
    assertNull(document.getDocumentElement());
  }

  @Test
  public void createDocumentReturnsDistinctInstances() {
    Document first = XMLUtilities.createDocument();
    Document second = XMLUtilities.createDocument();
    assertNotSame(first, second);
  }

  @Test
  public void createDocumentAllowsRootAppend() {
    Document document = XMLUtilities.createDocument();
    Element root = document.createElement("root");
    document.appendChild(root);
    assertEquals("root", document.getDocumentElement().getTagName());
  }

  @Test
  public void streamRoundTripPreservesSimpleStructure() throws Exception {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    root.setAttribute("id", "1");
    child(document, root, "child").setTextContent("value");
    Document roundTripped = roundTrip(document);
    assertEquals("root", roundTripped.getDocumentElement().getTagName());
    assertEquals("1", roundTripped.getDocumentElement().getAttribute("id"));
    assertEquals("value", XMLUtilities.getSingleChildElementByTagName(roundTripped.getDocumentElement(), "child").getTextContent());
  }

  @Test
  public void fileRoundTripPreservesSimpleStructure() throws Exception {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "leaf").setTextContent("text");
    File file = temporaryFolder.newFile("simple.xml");
    XMLUtilities.write(document, file);
    Document roundTripped = XMLUtilities.read(file);
    assertEquals("text", XMLUtilities.getSingleChildElementByTagName(roundTripped.getDocumentElement(), "leaf").getTextContent());
  }

  @Test
  public void pathRoundTripPreservesSimpleStructure() throws Exception {
    Document document = createDocumentWithRoot("config");
    child(document, document.getDocumentElement(), "value").setTextContent("42");
    File file = new File(temporaryFolder.getRoot(), "path.xml");
    XMLUtilities.write(document, file.getAbsolutePath());
    Document roundTripped = XMLUtilities.read(file.getAbsolutePath());
    assertEquals("42", XMLUtilities.getSingleChildElementByTagName(roundTripped.getDocumentElement(), "value").getTextContent());
  }

  @Test
  public void getChildElementsByTagNameFindsMatchingChildren() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    child(document, root, "item");
    child(document, root, "item");
    child(document, root, "other");
    List<Element> items = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(2, items.size());
    assertEquals(Arrays.asList("item", "item"), tagNames(items));
  }

  @Test
  public void getChildElementsByTagNameReturnsEmptyListForNoMatch() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    child(document, root, "item");
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "missing");
    assertTrue(matches.isEmpty());
  }

  @Test
  public void getChildElementsByTagNameLooksOnlyAtDirectChildren() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    Element direct = child(document, root, "target");
    child(document, direct, "target");
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "target");
    assertEquals(1, matches.size());
    assertSame(direct, matches.get(0));
  }

  @Test
  public void getChildElementsByTagNamePreservesOrder() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    Element first = child(document, root, "item");
    Element second = child(document, root, "item");
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertSame(first, matches.get(0));
    assertSame(second, matches.get(1));
  }

  @Test
  public void getChildElementsByTagNameIsCaseSensitive() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    child(document, root, "Item");
    child(document, root, "item");
    assertEquals(1, XMLUtilities.getChildElementsByTagName(root, "Item").size());
    assertEquals(1, XMLUtilities.getChildElementsByTagName(root, "item").size());
  }

  @Test
  public void getChildElementsByTagNameIgnoresTextAndCommentNodes() {
    String xml = "<root>text<!--comment--><item/><item/></root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    List<Element> matches = XMLUtilities.getChildElementsByTagName(document.getDocumentElement(), "item");
    assertEquals(2, matches.size());
  }

  @Test
  public void getChildElementsByTagNameHandlesManyChildren() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 50; i++) {
      child(document, root, i % 2 == 0 ? "even" : "odd");
    }
    List<Element> evens = XMLUtilities.getChildElementsByTagName(root, "even");
    assertEquals(25, evens.size());
  }

  @Test
  public void getChildElementsByTagNameReturnsMatchingOrderForNamedChildren() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    child(document, root, "alpha");
    child(document, root, "beta");
    child(document, root, "gamma");
    assertEquals(Arrays.asList("alpha"), tagNames(XMLUtilities.getChildElementsByTagName(root, "alpha")));
    assertEquals(Arrays.asList("beta"), tagNames(XMLUtilities.getChildElementsByTagName(root, "beta")));
    assertEquals(Arrays.asList("gamma"), tagNames(XMLUtilities.getChildElementsByTagName(root, "gamma")));
  }

  @Test
  public void getSingleChildElementByTagNameFindsOneChild() {
    Document document = createDocumentWithRoot("root");
    Element target = child(document, document.getDocumentElement(), "single");
    target.setTextContent("value");
    Element found = XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "single");
    assertSame(target, found);
    assertEquals("value", found.getTextContent());
  }

  @Test
  public void getSingleChildElementByTagNameReturnsNullWhenMissing() {
    Document document = createDocumentWithRoot("root");
    Element found = XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "missing");
    assertNull(found);
  }

  @Test
  public void getSingleChildElementByTagNameReturnsFirstWhenMultipleExist() {
    Document document = createDocumentWithRoot("root");
    Element first = child(document, document.getDocumentElement(), "item");
    child(document, document.getDocumentElement(), "item");
    Element found = XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "item");
    assertSame(first, found);
  }

  @Test
  public void getSingleChildElementByTagNameIsCaseSensitive() {
    Document document = createDocumentWithRoot("root");
    Element upper = child(document, document.getDocumentElement(), "Item");
    Element lower = child(document, document.getDocumentElement(), "item");
    assertSame(upper, XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "Item"));
    assertSame(lower, XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "item"));
  }

  @Test
  public void getSingleChildElementByTagNameDoesNotSearchGrandchildren() {
    Document document = createDocumentWithRoot("root");
    Element parent = child(document, document.getDocumentElement(), "parent");
    child(document, parent, "target");
    Element found = XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "target");
    assertNull(found);
  }

  @Test
  public void complexDocumentRoundTripPreservesNestedContent() {
    Document document = createDocumentWithRoot("project");
    Element root = document.getDocumentElement();
    root.setAttribute("version", "1.0");
    for (int i = 0; i < 5; i++) {
      Element module = child(document, root, "module");
      module.setAttribute("name", "module-" + i);
      child(document, module, "description").setTextContent("Description " + i);
    }
    Document roundTripped = roundTrip(document);
    List<Element> modules = XMLUtilities.getChildElementsByTagName(roundTripped.getDocumentElement(), "module");
    assertEquals(5, modules.size());
    assertEquals("Description 3", XMLUtilities.getSingleChildElementByTagName(modules.get(3), "description").getTextContent());
  }

  @Test
  public void readWellFormedXmlStringParsesExpectedValues() {
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><child attr=\"v\">text</child></root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    Element child = XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "child");
    assertEquals("v", child.getAttribute("attr"));
    assertEquals("text", child.getTextContent());
  }

  @Test
  public void readGarbageXmlThrowsRuntimeException() {
    assertMalformedXmlThrows("not xml");
  }

  @Test
  public void readUnclosedTagThrowsRuntimeException() {
    assertMalformedXmlThrows("<root><child></root>");
  }

  @Test
  public void readEmptyXmlThrowsRuntimeException() {
    assertMalformedXmlThrows("");
  }

  @Test
  public void readDuplicateAttributeThrowsRuntimeException() {
    assertMalformedXmlThrows("<root a=\"1\" a=\"2\"/>");
  }

  @Test
  public void readInvalidCharacterThrowsRuntimeException() {
    assertMalformedXmlThrows("<root></root>");
  }

  @Test
  public void writeProducesValidXmlThatCanBeReadBack() {
    Document document = createDocumentWithRoot("valid");
    byte[] bytes = writeToBytes(document);
    assertTrue(bytes.length > 0);
    Document parsed = XMLUtilities.read(new ByteArrayInputStream(bytes));
    assertEquals("valid", parsed.getDocumentElement().getTagName());
  }

  @Test
  public void writeUsesUtf16EncodingDeclaration() {
    Document document = createDocumentWithRoot("utf16");
    String xml = new String(writeToBytes(document), StandardCharsets.UTF_16);
    assertTrue(xml.contains("encoding=\"UTF-16\""));
  }

  @Test
  public void emojiRoundTripPreservesSupplementaryCharacters() {
    Document document = createDocumentWithRoot("emoji");
    document.getDocumentElement().setTextContent("🙂🚀");
    Document parsed = roundTrip(document);
    assertEquals("🙂🚀", parsed.getDocumentElement().getTextContent());
  }

  @Test
  public void specialCharactersInAttributesAndTextRoundTrip() {
    Document document = createDocumentWithRoot("special");
    document.getDocumentElement().setAttribute("attr", "5 < 6 & 7 > 3 \"quoted\"");
    document.getDocumentElement().setTextContent("<hello>&world");
    Document parsed = roundTrip(document);
    assertEquals("5 < 6 & 7 > 3 \"quoted\"", parsed.getDocumentElement().getAttribute("attr"));
    assertEquals("<hello>&world", parsed.getDocumentElement().getTextContent());
  }

  @Test
  public void readRemovesWhitespaceOnlyNodesFromIndentedXml() {
    String xml = "<root>\n  <a/>\n  <b/>\n</root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertEquals(2, document.getDocumentElement().getChildNodes().getLength());
  }

  @Test
  public void readRemovesWhitespaceOnlyNodesFromTabbedXml() {
    String xml = "<root>\t<a/>\t<b/>\t</root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertEquals(2, document.getDocumentElement().getChildNodes().getLength());
  }

  @Test
  public void readPreservesNonWhitespaceTextNodes() {
    String xml = "<root>  prefix <child/> suffix  </root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertTrue(document.getDocumentElement().getTextContent().contains("prefix"));
    assertTrue(document.getDocumentElement().getTextContent().contains("suffix"));
  }

  @Test
  public void readPreservesTextNodeWhitespaceWithinElementText() {
    String xml = "<root><child>  spaced value  </child></root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertEquals("  spaced value  ", XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "child").getTextContent());
  }

  @Test
  public void readCdataSectionPreservesTextContent() {
    String xml = "<root><child><![CDATA[<escaped> & value]]></child></root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertEquals("<escaped> & value", XMLUtilities.getSingleChildElementByTagName(document.getDocumentElement(), "child").getTextContent());
  }

  @Test
  public void commentsArePreservedWhenReadingXml() {
    String xml = "<root><!--comment--><child/></root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertEquals(1, countNodeType(document.getDocumentElement(), Node.COMMENT_NODE));
  }

  @Test
  public void processingInstructionsArePreservedWhenReadingXml() {
    String xml = "<root><?target data?><child/></root>";
    Document document = XMLUtilities.read(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
    assertEquals(1, countNodeType(document.getDocumentElement(), Node.PROCESSING_INSTRUCTION_NODE));
  }

  @Test
  public void writeToNestedDirectoryCreatesParents() throws Exception {
    Document document = createDocumentWithRoot("nested");
    File file = new File(temporaryFolder.getRoot(), "a/b/c/nested.xml");
    XMLUtilities.write(document, file);
    assertTrue(file.exists());
    assertEquals("nested", XMLUtilities.read(file).getDocumentElement().getTagName());
  }

  @Test
  public void writeToNestedPathCreatesParents() throws Exception {
    Document document = createDocumentWithRoot("nestedPath");
    File file = new File(temporaryFolder.getRoot(), "x/y/z/path.xml");
    XMLUtilities.write(document, file.getAbsolutePath());
    assertTrue(file.exists());
    assertEquals("nestedPath", XMLUtilities.read(file.getAbsolutePath()).getDocumentElement().getTagName());
  }

  @Test
  public void deeplyNestedElementsRoundTripTenLevels() {
    Document document = createDocumentWithRoot("level0");
    Element current = document.getDocumentElement();
    for (int i = 1; i <= 10; i++) {
      current = child(document, current, "level" + i);
    }
    Document parsed = roundTrip(document);
    Element cursor = parsed.getDocumentElement();
    for (int i = 1; i <= 10; i++) {
      cursor = XMLUtilities.getSingleChildElementByTagName(cursor, "level" + i);
      assertNotNull(cursor);
    }
  }

  @Test
  public void manySiblingElementsRoundTripTwoHundredItems() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 200; i++) {
      Element item = child(document, root, "item");
      item.setAttribute("index", String.valueOf(i));
    }
    Document parsed = roundTrip(document);
    List<Element> items = XMLUtilities.getChildElementsByTagName(parsed.getDocumentElement(), "item");
    assertEquals(200, items.size());
    assertEquals("0", items.get(0).getAttribute("index"));
    assertEquals("199", items.get(199).getAttribute("index"));
  }

  @Test
  public void emptyRootElementRoundTripPreservesRoot() {
    Document document = createDocumentWithRoot("emptyRoot");
    Document parsed = roundTrip(document);
    assertEquals("emptyRoot", parsed.getDocumentElement().getTagName());
    assertFalse(parsed.getDocumentElement().hasChildNodes());
  }

  @Test
  public void longAttributeValueRoundTripPreservesContent() {
    Document document = createDocumentWithRoot("root");
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 300; i++) {
      builder.append("abc");
    }
    document.getDocumentElement().setAttribute("long", builder.toString());
    Document parsed = roundTrip(document);
    assertEquals(builder.toString(), parsed.getDocumentElement().getAttribute("long"));
  }

  @Test
  public void manyAttributesRoundTripPreservesAllValues() {
    Document document = createDocumentWithRoot("root");
    for (int i = 0; i < 30; i++) {
      document.getDocumentElement().setAttribute("attr" + i, "value" + i);
    }
    Document parsed = roundTrip(document);
    assertEquals(30, parsed.getDocumentElement().getAttributes().getLength());
    assertEquals("value29", parsed.getDocumentElement().getAttribute("attr29"));
  }

  @Test
  public void largeDocumentRoundTripWithOneHundredItems() {
    Document document = createDocumentWithRoot("items");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 100; i++) {
      Element item = child(document, root, "item");
      child(document, item, "name").setTextContent("item-" + i);
    }
    Document parsed = roundTrip(document);
    assertEquals(100, XMLUtilities.getChildElementsByTagName(parsed.getDocumentElement(), "item").size());
  }

  @Test
  public void largeDocumentRoundTripWithFiveHundredItems() {
    Document document = createDocumentWithRoot("items");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 500; i++) {
      Element item = child(document, root, "item");
      item.setAttribute("index", String.valueOf(i));
    }
    Document parsed = roundTrip(document);
    List<Element> items = XMLUtilities.getChildElementsByTagName(parsed.getDocumentElement(), "item");
    assertEquals(500, items.size());
    assertEquals("499", items.get(499).getAttribute("index"));
  }

  @Test
  public void unicodeTextRoundTripPreservesCharacters() {
    Document document = createDocumentWithRoot("unicode");
    document.getDocumentElement().setTextContent("こんにちは مرحبا Καλημέρα");
    Document parsed = roundTrip(document);
    assertEquals("こんにちは مرحبا Καλημέρα", parsed.getDocumentElement().getTextContent());
  }

  @Test
  public void multipleDocumentsCanBeWrittenAndReadSequentially() {
    for (int i = 0; i < 5; i++) {
      Document document = createDocumentWithRoot("root" + i);
      document.getDocumentElement().setAttribute("index", String.valueOf(i));
      Document parsed = roundTrip(document);
      assertEquals("root" + i, parsed.getDocumentElement().getTagName());
      assertEquals(String.valueOf(i), parsed.getDocumentElement().getAttribute("index"));
    }
  }

  @Test
  public void numericTextContentRoundTripPreservesDigits() {
    Document document = createDocumentWithRoot("number");
    document.getDocumentElement().setTextContent("1234567890");
    Document parsed = roundTrip(document);
    assertEquals("1234567890", parsed.getDocumentElement().getTextContent());
  }

  @Test
  public void commentNodeCanBeCreatedAndRoundTripped() {
    Document document = createDocumentWithRoot("root");
    Comment comment = document.createComment("inside-comment");
    document.getDocumentElement().appendChild(comment);
    Document parsed = roundTrip(document);
    assertEquals(1, countNodeType(parsed.getDocumentElement(), Node.COMMENT_NODE));
  }

  @Test
  public void processingInstructionCanBeCreatedAndRoundTripped() {
    Document document = createDocumentWithRoot("root");
    ProcessingInstruction instruction = document.createProcessingInstruction("target", "data");
    document.getDocumentElement().appendChild(instruction);
    Document parsed = roundTrip(document);
    assertEquals(1, countNodeType(parsed.getDocumentElement(), Node.PROCESSING_INSTRUCTION_NODE));
  }

  @Test
  public void roundTripPreservesSingleChildVariant0() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "alpha").setTextContent("A");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "alpha");
    assertNotNull(child);
    assertEquals("A", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant1() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "beta").setTextContent("B");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "beta");
    assertNotNull(child);
    assertEquals("B", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant2() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "gamma").setTextContent("C");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "gamma");
    assertNotNull(child);
    assertEquals("C", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant3() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "delta").setTextContent("D");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "delta");
    assertNotNull(child);
    assertEquals("D", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant4() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "epsilon").setTextContent("E");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "epsilon");
    assertNotNull(child);
    assertEquals("E", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant5() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "zeta").setTextContent("F");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "zeta");
    assertNotNull(child);
    assertEquals("F", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant6() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "eta").setTextContent("G");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "eta");
    assertNotNull(child);
    assertEquals("G", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant7() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "theta").setTextContent("H");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "theta");
    assertNotNull(child);
    assertEquals("H", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant8() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "iota").setTextContent("I");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "iota");
    assertNotNull(child);
    assertEquals("I", child.getTextContent());
  }

  @Test
  public void roundTripPreservesSingleChildVariant9() {
    Document document = createDocumentWithRoot("root");
    child(document, document.getDocumentElement(), "kappa").setTextContent("J");
    Document parsed = roundTrip(document);
    Element child = XMLUtilities.getSingleChildElementByTagName(parsed.getDocumentElement(), "kappa");
    assertNotNull(child);
    assertEquals("J", child.getTextContent());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant0() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 1; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(1, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant1() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 2; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(2, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant2() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 3; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(3, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant3() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 4; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(4, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant4() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 5; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(5, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant5() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 10; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(10, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant6() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 25; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(25, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant7() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 50; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(50, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant8() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 75; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(75, matches.size());
  }

  @Test
  public void getChildElementsByTagNameCountsMatchingChildrenVariant9() {
    Document document = createDocumentWithRoot("root");
    Element root = document.getDocumentElement();
    for (int i = 0; i < 100; i++) {
      child(document, root, "item");
    }
    List<Element> matches = XMLUtilities.getChildElementsByTagName(root, "item");
    assertEquals(100, matches.size());
  }

}
