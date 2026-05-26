package org.alice.ide.ast.export.type;

import static org.alice.ide.testing.JacocoReflectionSupport.declaredMethods;

import edu.cmu.cs.dennisc.xml.XMLUtilities;
import org.junit.Test;
import org.lgna.project.VersionNotSupportedException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class TypeXmlUtitlitiesComprehensiveTest {

  private TypeSummary createSummary() {
    return new TypeSummary(
        3.1,
        "Hero",
        new ArrayList<>(Arrays.asList("ParentType", "java.lang.Object")),
        new ResourceInfo("org.example.HeroResource", "DEFAULT"),
        new ArrayList<>(Arrays.asList("walk", "jump")),
        new ArrayList<>(Arrays.asList(new FunctionInfo("java.lang.Integer", "getScore"))),
        new ArrayList<>(Arrays.asList(new FieldInfo("java.lang.String", "name"))));
  }

  private Document createDocumentWithVersion(double version) throws Exception {
    Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    Element root = document.createElement("typeSummary");
    root.setAttribute("version", Double.toString(version));
    Element type = document.createElement("type");
    type.setAttribute("name", "Hero");
    root.appendChild(type);
    document.appendChild(root);
    return document;
  }

  @Test
  public void classIsPublic() {
    assertTrue(Modifier.isPublic(TypeXmlUtitlities.class.getModifiers()));
  }

  @Test
  public void classIsConcrete() {
    assertFalse(Modifier.isAbstract(TypeXmlUtitlities.class.getModifiers()));
  }

  @Test
  public void simpleNameMatchesSource() {
    assertEquals("TypeXmlUtitlities", TypeXmlUtitlities.class.getSimpleName());
  }

  @Test
  public void packageNameMatchesSource() {
    assertEquals("org.alice.ide.ast.export.type", TypeXmlUtitlities.class.getPackage().getName());
  }

  @Test
  public void decodeMethodExists() throws Exception {
    assertNotNull(TypeXmlUtitlities.class.getDeclaredMethod("decode", Document.class));
  }

  @Test
  public void encodeMethodExists() throws Exception {
    assertNotNull(TypeXmlUtitlities.class.getDeclaredMethod("encode", TypeSummary.class));
  }

  @Test
  public void mainMethodExists() throws Exception {
    assertNotNull(TypeXmlUtitlities.class.getDeclaredMethod("main", String[].class));
  }

  @Test
  public void utilityMethodsArePublicStatic() throws Exception {
    Method decode = TypeXmlUtitlities.class.getDeclaredMethod("decode", Document.class);
    Method encode = TypeXmlUtitlities.class.getDeclaredMethod("encode", TypeSummary.class);
    assertTrue(Modifier.isPublic(decode.getModifiers()));
    assertTrue(Modifier.isStatic(decode.getModifiers()));
    assertTrue(Modifier.isPublic(encode.getModifiers()));
    assertTrue(Modifier.isStatic(encode.getModifiers()));
  }

  @Test
  public void encodeCreatesTypeSummaryRootElement() {
    assertEquals("typeSummary", TypeXmlUtitlities.encode(createSummary()).getDocumentElement().getTagName());
  }

  @Test
  public void encodeWritesVersionAttribute() {
    assertEquals("3.1", TypeXmlUtitlities.encode(createSummary()).getDocumentElement().getAttribute("version"));
  }

  @Test
  public void encodeWritesTypeElementNameAttribute() {
    Document document = TypeXmlUtitlities.encode(createSummary());
    assertEquals("Hero", ((Element) document.getElementsByTagName("type").item(0)).getAttribute("name"));
  }

  @Test
  public void encodeWritesResourceElementAndFieldAttribute() {
    Document document = TypeXmlUtitlities.encode(createSummary());
    Element resource = (Element) document.getElementsByTagName("resource").item(0);
    assertEquals("org.example.HeroResource", resource.getAttribute("className"));
    assertEquals("DEFAULT", resource.getAttribute("fieldName"));
  }

  @Test
  public void encodeOmitsResourceElementWhenResourceInfoIsNull() {
    TypeSummary summary = new TypeSummary(3.1, "Hero", new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    assertEquals(0, TypeXmlUtitlities.encode(summary).getElementsByTagName("resource").getLength());
  }

  @Test
  public void encodeWritesProcedureFunctionAndFieldCounts() {
    Document document = TypeXmlUtitlities.encode(createSummary());
    assertEquals(2, document.getElementsByTagName("procedure").getLength());
    assertEquals(1, document.getElementsByTagName("function").getLength());
    assertEquals(1, document.getElementsByTagName("field").getLength());
  }

  @Test
  public void encodeWritesFunctionAndFieldAttributes() {
    Document document = TypeXmlUtitlities.encode(createSummary());
    Element function = (Element) document.getElementsByTagName("function").item(0);
    Element field = (Element) document.getElementsByTagName("field").item(0);
    assertEquals("java.lang.Integer", function.getAttribute("returnClassName"));
    assertEquals("java.lang.String", field.getAttribute("valueClassName"));
  }

  @Test
  public void decodeRoundTripsVersionAndTypeName() throws Exception {
    TypeSummary decoded = TypeXmlUtitlities.decode(TypeXmlUtitlities.encode(createSummary()));
    assertEquals(3.1, decoded.getVersion(), 0.0);
    assertEquals("Hero", decoded.getTypeName());
  }

  @Test
  public void decodeRoundTripsHierarchyProceduresFunctionsAndFields() throws Exception {
    TypeSummary decoded = TypeXmlUtitlities.decode(TypeXmlUtitlities.encode(createSummary()));
    assertEquals(2, decoded.getHierarchyClassNames().size());
    assertEquals(2, decoded.getProcedureNames().size());
    assertEquals(1, decoded.getFunctionInfos().size());
    assertEquals(1, decoded.getFieldInfos().size());
  }

  @Test
  public void decodeRoundTripsResourceInfo() throws Exception {
    TypeSummary decoded = TypeXmlUtitlities.decode(TypeXmlUtitlities.encode(createSummary()));
    assertEquals("org.example.HeroResource", decoded.getResourceInfo().getClassName());
    assertEquals("DEFAULT", decoded.getResourceInfo().getFieldName());
  }

  @Test
  public void decodeAllowsExactMinimumVersion() throws Exception {
    TypeSummary decoded = TypeXmlUtitlities.decode(createDocumentWithVersion(TypeSummary.MINIMUM_ACCEPTABLE_VERSION));
    assertEquals(TypeSummary.MINIMUM_ACCEPTABLE_VERSION, decoded.getVersion(), 0.0);
  }

  @Test
  public void decodeRejectsUnsupportedVersion() throws Exception {
    try {
      TypeXmlUtitlities.decode(createDocumentWithVersion(3.0));
      fail("Expected VersionNotSupportedException");
    } catch (VersionNotSupportedException expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void decodeResourceWithoutFieldAttributeProducesNullFieldName() throws Exception {
    Document document = XMLUtilities.createDocument();
    Element root = document.createElement("typeSummary");
    root.setAttribute("version", "3.1");
    Element type = document.createElement("type");
    type.setAttribute("name", "Hero");
    Element resource = document.createElement("resource");
    resource.setAttribute("className", "org.example.HeroResource");
    root.appendChild(type);
    root.appendChild(resource);
    document.appendChild(root);
    TypeSummary decoded = TypeXmlUtitlities.decode(document);
    assertNull(decoded.getResourceInfo().getFieldName());
  }

  @Test
  public void unicodeNamesSurviveRoundTrip() throws Exception {
    TypeSummary summary = new TypeSummary(3.1, "类型🚀", new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    TypeSummary decoded = TypeXmlUtitlities.decode(TypeXmlUtitlities.encode(summary));
    assertEquals("类型🚀", decoded.getTypeName());
  }

  @Test
  public void emptyCollectionsSurviveRoundTrip() throws Exception {
    TypeSummary summary = new TypeSummary(3.1, "Empty", new ArrayList<String>(), null, new ArrayList<String>(), new ArrayList<FunctionInfo>(), new ArrayList<FieldInfo>());
    TypeSummary decoded = TypeXmlUtitlities.decode(TypeXmlUtitlities.encode(summary));
    assertTrue(decoded.getProcedureNames().isEmpty());
    assertTrue(decoded.getFunctionInfos().isEmpty());
    assertTrue(decoded.getFieldInfos().isEmpty());
  }

  @Test
  public void declaredMethodCountIsThree() {
    assertEquals(3, declaredMethods(TypeXmlUtitlities.class).length);
  }
}
