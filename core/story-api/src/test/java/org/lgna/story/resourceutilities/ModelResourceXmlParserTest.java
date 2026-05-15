package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AxisAlignedBox;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.*;

/**
 * TDD tests for ModelResourceXmlParser — the package-private utility class
 * holding 5 static XML-parsing helpers extracted from ModelResourceInfo.
 *
 * These tests define the contract BEFORE the implementation exists.
 * They must all fail initially and pass once ModelResourceXmlParser is created.
 */
public class ModelResourceXmlParserTest {

  // ─── getImmediateChildElementsByTagName ────────────────────────────

  @Test
  public void immediateChildElementsReturnsOnlyDirectChildren() throws Exception {
    Element root = rootElement("""
        <Root>
          <Tag>direct1</Tag>
          <Tag>direct2</Tag>
          <Nested>
            <Tag>nested-should-be-excluded</Tag>
          </Nested>
        </Root>
        """);

    List<Element> result = ModelResourceXmlParser.getImmediateChildElementsByTagName(root, "Tag");

    assertEquals(2, result.size());
    assertEquals("direct1", result.get(0).getTextContent());
    assertEquals("direct2", result.get(1).getTextContent());
  }

  @Test
  public void immediateChildElementsReturnsEmptyListWhenNoMatch() throws Exception {
    Element root = rootElement("""
        <Root>
          <Other>content</Other>
        </Root>
        """);

    List<Element> result = ModelResourceXmlParser.getImmediateChildElementsByTagName(root, "Tag");

    assertTrue(result.isEmpty());
  }

  @Test
  public void immediateChildElementsIgnoresTextNodes() throws Exception {
    Element root = rootElement("""
        <Root>
          some text
          <Tag>element</Tag>
          more text
        </Root>
        """);

    List<Element> result = ModelResourceXmlParser.getImmediateChildElementsByTagName(root, "Tag");

    assertEquals(1, result.size());
    assertEquals("element", result.get(0).getTextContent());
  }

  // ─── addImmediateChildTextContent ─────────────────────────────────

  @Test
  public void addImmediateChildTextContentCollectsDirectChildText() throws Exception {
    Element root = rootElement("""
        <Root>
          <Tag>alpha</Tag>
          <Tag>beta</Tag>
        </Root>
        """);

    LinkedList<String> collected = new LinkedList<>();
    ModelResourceXmlParser.addImmediateChildTextContent(root, "Tag", collected);

    assertEquals(2, collected.size());
    assertEquals("alpha", collected.get(0));
    assertEquals("beta", collected.get(1));
  }

  @Test
  public void addImmediateChildTextContentIgnoresNestedMatches() throws Exception {
    Element root = rootElement("""
        <Root>
          <Tag>direct</Tag>
          <Wrapper>
            <Tag>nested-ignored</Tag>
          </Wrapper>
        </Root>
        """);

    LinkedList<String> collected = new LinkedList<>();
    ModelResourceXmlParser.addImmediateChildTextContent(root, "Tag", collected);

    assertEquals(1, collected.size());
    assertEquals("direct", collected.get(0));
  }

  @Test
  public void addImmediateChildTextContentAppendsToExistingList() throws Exception {
    Element root = rootElement("""
        <Root>
          <Tag>new</Tag>
        </Root>
        """);

    LinkedList<String> existing = new LinkedList<>();
    existing.add("pre-existing");
    ModelResourceXmlParser.addImmediateChildTextContent(root, "Tag", existing);

    assertEquals(2, existing.size());
    assertEquals("pre-existing", existing.get(0));
    assertEquals("new", existing.get(1));
  }

  // ─── getResourceTags ──────────────────────────────────────────────

  @Test
  public void getResourceTagsExtractsFromContainer() throws Exception {
    Element root = rootElement("""
        <Resource>
          <Tags>
            <Tag>alpha</Tag>
            <Tag>beta</Tag>
          </Tags>
        </Resource>
        """);

    String[] tags = ModelResourceXmlParser.getResourceTags(root, "Tags", "Tag");

    assertArrayEquals(new String[] {"alpha", "beta"}, tags);
  }

  @Test
  public void getResourceTagsExtractsBareTagsBeforeContainerTags() throws Exception {
    Element root = rootElement("""
        <Resource>
          <Tag>bare</Tag>
          <Tags>
            <Tag>contained</Tag>
          </Tags>
        </Resource>
        """);

    String[] tags = ModelResourceXmlParser.getResourceTags(root, "Tags", "Tag");

    assertArrayEquals(new String[] {"bare", "contained"}, tags);
  }

  @Test
  public void getResourceTagsReturnsEmptyArrayWhenNoTags() throws Exception {
    Element root = rootElement("""
        <Resource>
          <Other>stuff</Other>
        </Resource>
        """);

    String[] tags = ModelResourceXmlParser.getResourceTags(root, "Tags", "Tag");

    assertArrayEquals(new String[0], tags);
  }

  @Test
  public void getResourceTagsWorksForGroupTags() throws Exception {
    Element root = rootElement("""
        <Resource>
          <GroupTags>
            <GroupTag>groupA</GroupTag>
            <GroupTag>groupB</GroupTag>
          </GroupTags>
        </Resource>
        """);

    String[] tags = ModelResourceXmlParser.getResourceTags(root, "GroupTags", "GroupTag");

    assertArrayEquals(new String[] {"groupA", "groupB"}, tags);
  }

  @Test
  public void getResourceTagsWorksForThemeTags() throws Exception {
    Element root = rootElement("""
        <Resource>
          <ThemeTags>
            <ThemeTag>forest</ThemeTag>
          </ThemeTags>
        </Resource>
        """);

    String[] tags = ModelResourceXmlParser.getResourceTags(root, "ThemeTags", "ThemeTag");

    assertArrayEquals(new String[] {"forest"}, tags);
  }

  @Test
  public void getResourceTagsIgnoresDeeplyNestedTags() throws Exception {
    Element root = rootElement("""
        <Resource>
          <Tags>
            <Tag>included</Tag>
          </Tags>
          <Nested>
            <Tags>
              <Tag>excluded-deep</Tag>
            </Tags>
          </Nested>
        </Resource>
        """);

    String[] tags = ModelResourceXmlParser.getResourceTags(root, "Tags", "Tag");

    assertArrayEquals(new String[] {"included"}, tags);
  }

  // ─── getBoundingBoxFromXML ────────────────────────────────────────

  @Test
  public void getBoundingBoxFromXMLParsesMinMaxCoordinates() throws Exception {
    Element bbox = rootElement("""
        <BoundingBox>
          <Min x="-1.5" y="0.0" z="-2.5"/>
          <Max x="1.5" y="3.0" z="2.5"/>
        </BoundingBox>
        """);

    AxisAlignedBox result = ModelResourceXmlParser.getBoundingBoxFromXML(bbox);

    assertEquals(AxisAlignedBox.createAxisAlignedBox(-1.5, 0.0, -2.5, 1.5, 3.0, 2.5), result);
  }

  @Test
  public void getBoundingBoxFromXMLReturnsNullForNullElement() {
    AxisAlignedBox result = ModelResourceXmlParser.getBoundingBoxFromXML(null);

    assertNull(result);
  }

  @Test
  public void getBoundingBoxFromXMLHandlesIntegerCoordinates() throws Exception {
    Element bbox = rootElement("""
        <BoundingBox>
          <Min x="0" y="0" z="0"/>
          <Max x="1" y="2" z="3"/>
        </BoundingBox>
        """);

    AxisAlignedBox result = ModelResourceXmlParser.getBoundingBoxFromXML(bbox);

    assertEquals(AxisAlignedBox.createAxisAlignedBox(0.0, 0.0, 0.0, 1.0, 2.0, 3.0), result);
  }

  @Test
  public void getBoundingBoxFromXMLHandlesNegativeCoordinates() throws Exception {
    Element bbox = rootElement("""
        <BoundingBox>
          <Min x="-10.0" y="-20.0" z="-30.0"/>
          <Max x="-1.0" y="-2.0" z="-3.0"/>
        </BoundingBox>
        """);

    AxisAlignedBox result = ModelResourceXmlParser.getBoundingBoxFromXML(bbox);

    assertEquals(AxisAlignedBox.createAxisAlignedBox(-10.0, -20.0, -30.0, -1.0, -2.0, -3.0), result);
  }

  // ─── getSubResourceFromXML ────────────────────────────────────────

  @Test
  public void getSubResourceFromXMLCreatesResourceWithAllAttributes() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="VARIANT" modelName="Model1" textureName="Tex1"
                  creator="Artist" creationYear="2022" placeOnGround="true"/>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
    assertEquals("VARIANT", result.getResourceName());
    assertEquals("Model1", result.getModelName());
    assertEquals("Tex1", result.getTextureName());
    assertEquals("Artist", result.getCreator());
    assertEquals(2022, result.getCreationYear());
    assertTrue(result.getPlaceOnGround());
    assertEquals(parent, result.getParent());
  }

  @Test
  public void getSubResourceFromXMLReturnsNullForNullElement() {
    ModelResourceInfo parent = createMinimalParent();

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(null, parent);

    assertNull(result);
  }

  @Test
  public void getSubResourceFromXMLHandlesMissingOptionalAttributes() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="MINIMAL"/>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
    assertEquals("MINIMAL", result.getResourceName());
    assertNull(result.getTextureName());
    assertEquals(-1, result.getCreationYear());
  }

  @Test
  public void getSubResourceFromXMLParsesMalformedCreationYearAsDefault() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="BAD" creationYear="not-a-number"/>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
    assertEquals(-1, result.getCreationYear());
  }

  @Test
  public void getSubResourceFromXMLParsesDeprecatedFlag() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="OLD" deprecated="true" modelName="OldModel"/>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
  }

  @Test
  public void getSubResourceFromXMLParsesSubResourceBoundingBox() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="BOXED" modelName="BoxModel">
          <BoundingBox>
            <Min x="-0.5" y="0.0" z="-0.5"/>
            <Max x="0.5" y="1.0" z="0.5"/>
          </BoundingBox>
        </Resource>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
    assertEquals(AxisAlignedBox.createAxisAlignedBox(-0.5, 0.0, -0.5, 0.5, 1.0, 0.5), result.getBoundingBox());
  }

  @Test
  public void getSubResourceFromXMLParsesSubResourceTags() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="TAGGED" modelName="TaggedModel">
          <Tag>bare-tag</Tag>
          <Tags><Tag>contained-tag</Tag></Tags>
          <GroupTag>bare-group</GroupTag>
          <GroupTags><GroupTag>contained-group</GroupTag></GroupTags>
          <ThemeTag>bare-theme</ThemeTag>
          <ThemeTags><ThemeTag>contained-theme</ThemeTag></ThemeTags>
        </Resource>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
    // Sub-resource own tags (parent tags prepended by getTags())
    // The raw tags on the sub-resource itself, before parent merging
    String[] allTags = result.getTags();
    // Parent has empty tags, so result is just own tags
    assertArrayEquals(new String[] {"bare-tag", "contained-tag"}, allTags);
    assertArrayEquals(new String[] {"bare-group", "contained-group"}, result.getGroupTags());
    assertArrayEquals(new String[] {"bare-theme", "contained-theme"}, result.getThemeTags());
  }

  @Test
  public void getSubResourceFromXMLPlaceOnGroundNullWhenMissing() throws Exception {
    ModelResourceInfo parent = createMinimalParent();
    Element resource = rootElement("""
        <Resource resourceName="NOGROUND" modelName="NoGroundModel"/>
        """);

    ModelResourceInfo result = ModelResourceXmlParser.getSubResourceFromXML(resource, parent);

    assertNotNull(result);
    // When placeOnGround is null and parent is false, should inherit false
    assertFalse(result.getPlaceOnGround());
  }

  // ─── Integration: ModelResourceInfo Document constructor still works ─

  @Test
  public void modelResourceInfoDocumentConstructorStillParsesCorrectly() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="TestModel" creator="Tester" creationYear="2025" placeOnGround="true">
          <BoundingBox>
            <Min x="-1.0" y="0.0" z="-1.0"/>
            <Max x="1.0" y="2.0" z="1.0"/>
          </BoundingBox>
          <Tags><Tag>alpha</Tag><Tag>beta</Tag></Tags>
          <GroupTags><GroupTag>group1</GroupTag></GroupTags>
          <ThemeTags><ThemeTag>theme1</ThemeTag></ThemeTags>
          <Resource resourceName="DEFAULT" modelName="TestMdl" textureName="Tex1"
                    creator="SubCreator" creationYear="2024"/>
          <Resource resourceName="ALT" modelName="TestMdl" textureName="Tex2"/>
        </AliceModel>
        """));

    assertEquals("TestModel", info.getModelName());
    assertEquals("Tester", info.getCreator());
    assertEquals(2025, info.getCreationYear());
    assertTrue(info.getPlaceOnGround());
    assertEquals(AxisAlignedBox.createAxisAlignedBox(-1.0, 0.0, -1.0, 1.0, 2.0, 1.0), info.getBoundingBox());
    assertArrayEquals(new String[] {"alpha", "beta"}, info.getTags());
    assertArrayEquals(new String[] {"group1"}, info.getGroupTags());
    assertArrayEquals(new String[] {"theme1"}, info.getThemeTags());

    ModelResourceInfo def = info.getSubResource("DEFAULT");
    assertNotNull(def);
    assertEquals("TestMdl", def.getModelName());
    assertEquals("Tex1", def.getTextureName());
    assertEquals("SubCreator", def.getCreator());
    assertEquals(2024, def.getCreationYear());

    ModelResourceInfo alt = info.getSubResource("ALT");
    assertNotNull(alt);
    assertEquals("Tex2", alt.getTextureName());
  }

  // ─── Helpers ──────────────────────────────────────────────────────

  private static ModelResourceInfo createMinimalParent() {
    return new ModelResourceInfo(null, "PARENT", "", -1, null,
        new String[0], new String[0], new String[0], "ParentModel", null, false, false);
  }

  private static Element rootElement(String xml) throws Exception {
    return parseXml(xml).getDocumentElement();
  }

  private static Document parseXml(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
  }
}
