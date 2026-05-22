package org.alice.ide.croquet.models.html;

import org.junit.Before;
import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.code.ProcessableNode;
import org.lgna.project.ast.UserMethod;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HtmlEncoderBranchCoverageTest {

  private Document document;
  private HtmlEncoder encoder;

  @Before
  public void setUp() throws Exception {
    document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    encoder = new HtmlEncoder(document);
  }

  @Test
  public void htmlEncoder_isPublicConcreteAstProcessor() {
    assertTrue(Modifier.isPublic(HtmlEncoder.class.getModifiers()));
    assertFalse(Modifier.isAbstract(HtmlEncoder.class.getModifiers()));
    assertEquals("AstProcessor", HtmlEncoder.class.getInterfaces()[0].getSimpleName());
  }

  @Test
  public void htmlEncoder_hasSinglePackagePrivateConstructorTakingDocument() {
    Constructor<?>[] constructors = HtmlEncoder.class.getDeclaredConstructors();
    assertEquals(1, constructors.length);
    assertEquals(1, constructors[0].getParameterTypes().length);
    assertEquals(Document.class, constructors[0].getParameterTypes()[0]);
    assertFalse(Modifier.isPublic(constructors[0].getModifiers()));
  }

  @Test
  public void getNewCodeOrganizer_handlesSceneType() {
    assertNotNull(encoder.getNewCodeOrganizerForTypeName("Scene"));
  }

  @Test
  public void getNewCodeOrganizer_handlesProgramType() {
    assertNotNull(encoder.getNewCodeOrganizerForTypeName("Program"));
  }

  @Test
  public void getNewCodeOrganizer_handlesUnknownType() {
    assertNotNull(encoder.getNewCodeOrganizerForTypeName("UnknownType"));
  }

  @Test
  public void getNewCodeOrganizer_returnsFreshInstances() {
    assertNotSame(encoder.getNewCodeOrganizerForTypeName("Scene"), encoder.getNewCodeOrganizerForTypeName("Scene"));
  }

  @Test
  public void isSectionToInclude_filtersConstructorSection() throws Exception {
    assertFalse(invokeIsSectionToInclude("ConstructorSection"));
  }

  @Test
  public void isSectionToInclude_filtersGettersAndSettersSection() throws Exception {
    assertFalse(invokeIsSectionToInclude("GettersAndSettersSection"));
  }

  @Test
  public void isSectionToInclude_filtersStaticMethodsSection() throws Exception {
    assertFalse(invokeIsSectionToInclude("StaticMethodsSection"));
  }

  @Test
  public void isSectionToInclude_keepsRegularSections() throws Exception {
    assertTrue(invokeIsSectionToInclude("MethodsSection"));
  }

  @Test
  public void isClassEmpty_returnsTrueForEmptySections() throws Exception {
    assertTrue(invokeIsClassEmpty(new HashMap<>()));
  }

  @Test
  public void isClassEmpty_returnsTrueForSkippedSectionContent() throws Exception {
    Map<String, List<ProcessableNode>> sections = new HashMap<>();
    sections.put("ConstructorSection", List.of(newConfiguredMethod("ignored", ManagementLevel.NONE)));
    assertTrue(invokeIsClassEmpty(sections));
  }

  @Test
  public void isClassEmpty_returnsTrueForGeneratedMethodsOnly() throws Exception {
    Map<String, List<ProcessableNode>> sections = new HashMap<>();
    sections.put("MethodsSection", List.of(newConfiguredMethod("generatedMethod", ManagementLevel.GENERATED)));
    assertTrue(invokeIsClassEmpty(sections));
  }

  @Test
  public void isClassEmpty_returnsFalseForNonGeneratedMethod() throws Exception {
    Map<String, List<ProcessableNode>> sections = new HashMap<>();
    sections.put("MethodsSection", List.of(newConfiguredMethod("userMethod", ManagementLevel.NONE)));
    assertFalse(invokeIsClassEmpty(sections));
  }

  @Test
  public void addElement_appendsConfiguredNodeToCurrentParent() throws Exception {
    Element root = pushRoot();
    Element element = (Element) invokePrivate("addElement",
        new Class<?>[] {String.class, String.class, String.class},
        "span", "test-class", "text-value");
    assertSame(root, element.getParentNode());
    assertEquals("span", element.getTagName());
    assertEquals("test-class", element.getAttribute("class"));
    assertEquals("text-value", element.getTextContent());
  }

  @Test
  public void addSpan_createsSpanNode() throws Exception {
    pushRoot();
    Element span = (Element) invokePrivate("addSpan", new Class<?>[] {String.class, String.class}, "span-class", "value");
    assertEquals("span", span.getTagName());
  }

  @Test
  public void addDiv_createsDivNodeWithDefaultText() throws Exception {
    pushRoot();
    Element div = (Element) invokePrivate("addDiv", new Class<?>[] {String.class}, "div-class");
    assertEquals("div", div.getTagName());
    assertEquals(" ", div.getTextContent());
  }

  @Test
  public void addSpan_acceptsNullContent() throws Exception {
    pushRoot();
    Element span = (Element) invokePrivate("addSpan", new Class<?>[] {String.class, String.class}, "nullable", null);
    assertTrue(span.getTextContent() == null || span.getTextContent().isEmpty());
  }

  @Test
  public void parentNode_returnsDocumentWhenStackEmpty() throws Exception {
    Node parent = (Node) invokePrivate("parentNode", new Class<?>[0]);
    assertSame(document, parent);
  }

  @Test
  public void parentElement_returnsTopOfStack() throws Exception {
    Element root = pushRoot();
    Element parent = (Element) invokePrivate("parentElement", new Class<?>[0]);
    assertSame(root, parent);
  }

  @Test
  public void pushDiv_pushesAndPopsActiveStack() throws Exception {
    Element root = pushRoot();
    int before = activeElements().size();
    invokePrivate("pushDiv", new Class<?>[] {String.class, Runnable.class}, "wrapper", (Runnable) () -> {
      assertEquals(before + 1, activeElements().size());
    });
    assertEquals(before, activeElements().size());
    assertEquals(1, root.getChildNodes().getLength());
  }

  @Test
  public void pushSpan_pushesAndPopsActiveStack() throws Exception {
    Element root = pushRoot();
    int before = activeElements().size();
    invokePrivate("pushSpan", new Class<?>[] {String.class, Runnable.class}, "wrapper", (Runnable) () -> {
      assertEquals(before + 1, activeElements().size());
    });
    assertEquals(before, activeElements().size());
    assertEquals(1, root.getChildNodes().getLength());
  }

  @Test
  public void encode_emptyClassLeavesRootWithoutChildren() {
    Element root = document.createElement("div");
    document.appendChild(root);
    NamedUserType userType = newConfiguredType("EmptyScene");
    encoder.encode(userType, root);
    assertEquals(0, root.getChildNodes().getLength());
  }

  @Test
  public void encode_generatedOnlyClassLeavesRootWithoutChildren() {
    Element root = document.createElement("div");
    document.appendChild(root);
    NamedUserType userType = newConfiguredType("GeneratedScene");
    userType.methods.add(newConfiguredMethod("generatedSetup", ManagementLevel.GENERATED));
    encoder.encode(userType, root);
    assertEquals(0, root.getChildNodes().getLength());
  }

  @Test
  public void serializedDocumentContainsEscapedTextFromAddElement() throws Exception {
    pushRoot();
    invokePrivate("addElement", new Class<?>[] {String.class, String.class, String.class},
        "span", "escaped", "A & B < C");
    String xml = serialize(document);
    assertTrue(xml.contains("A &amp; B &lt; C"));
  }

  @SuppressWarnings("unchecked")
  private Deque<Element> activeElements() {
    try {
      Field field = HtmlEncoder.class.getDeclaredField("activeElements");
      field.setAccessible(true);
      return (Deque<Element>) field.get(encoder);
    } catch (Exception e) {
      throw new AssertionError(e);
    }
  }

  private Element pushRoot() {
    Element root = document.createElement("div");
    document.appendChild(root);
    activeElements().push(root);
    return root;
  }

  private boolean invokeIsSectionToInclude(String key) throws Exception {
    return (Boolean) invokePrivate("isSectionToInclude", new Class<?>[] {String.class}, key);
  }

  private boolean invokeIsClassEmpty(Map<String, List<ProcessableNode>> sections) throws Exception {
    return (Boolean) invokePrivate("isClassEmpty", new Class<?>[] {Map.class}, sections);
  }

  private Object invokePrivate(String name, Class<?>[] parameterTypes, Object... args) throws Exception {
    Method method = HtmlEncoder.class.getDeclaredMethod(name, parameterTypes);
    method.setAccessible(true);
    return method.invoke(encoder, args);
  }

  private static NamedUserType newConfiguredType(String name) {
    NamedUserType userType = new NamedUserType();
    userType.name.setValue(name);
    userType.superType.setValue(JavaType.getInstance(org.lgna.story.SScene.class));
    return userType;
  }

  private static UserMethod newConfiguredMethod(String name, ManagementLevel level) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.body.setValue(new BlockStatement());
    method.managementLevel.setValue(level);
    return method;
  }

  private static String serialize(Document document) throws Exception {
    StringWriter writer = new StringWriter();
    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(document), new StreamResult(writer));
    return writer.toString();
  }
}
