package org.lgna.story.resourceutilities;

import org.alice.math.immutable.AxisAlignedBox;
import org.alice.tweedle.file.Manifest;
import org.alice.tweedle.file.ModelManifest;
import org.junit.Test;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.time.Year;
import java.time.ZonedDateTime;

import static org.junit.Assert.*;

public class ModelResourceInfoTest {

  @Test
  public void parsesModelMetadataAndSubResourcesFromXml() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Tree" creator="CMU" creationYear="2020" placeOnGround="true">
          <BoundingBox>
            <Min x="-1.0" y="0.0" z="-2.0"/>
            <Max x="1.0" y="4.0" z="2.0"/>
          </BoundingBox>
          <Tags><Tag>plant</Tag></Tags>
          <GroupTags><GroupTag>nature</GroupTag></GroupTags>
          <ThemeTags><ThemeTag>forest</ThemeTag></ThemeTags>
          <Resource resourceName="DEFAULT" modelName="TreeModel" textureName="Bark" creator="Artist" creationYear="2021">
            <Tag>variant</Tag>
            <GroupTag>prop</GroupTag>
            <ThemeTag>woodland</ThemeTag>
          </Resource>
        </AliceModel>
        """));

    assertEquals("Tree", info.getModelName());
    assertEquals("CMU", info.getCreator());
    assertEquals(2020, info.getCreationYear());
    assertTrue(info.getPlaceOnGround());
    assertEquals(AxisAlignedBox.createAxisAlignedBox(-1.0, 0.0, -2.0, 1.0, 4.0, 2.0), info.getBoundingBox());
    assertArrayEquals(new String[] {"plant"}, info.getTags());
    assertArrayEquals(new String[] {"nature"}, info.getGroupTags());
    assertArrayEquals(new String[] {"forest"}, info.getThemeTags());

    ModelResourceInfo variant = info.getSubResource("DEFAULT");
    assertNotNull(variant);
    assertEquals(info, variant.getParent());
    assertEquals("TreeModel", variant.getModelName());
    assertEquals("Bark", variant.getTextureName());
    assertEquals("Artist", variant.getCreator());
    assertEquals(2021, variant.getCreationYear());
    assertArrayEquals(new String[] {"plant", "variant"}, variant.getTags());
    assertArrayEquals(new String[] {"nature", "prop"}, variant.getGroupTags());
    assertArrayEquals(new String[] {"forest", "woodland"}, variant.getThemeTags());
  }

  @Test
  public void malformedAndMissingOptionalXmlFieldsUseCurrentDefaults() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <Root>
          <AliceModel name="BrokenMetadata" creationYear="not-a-year" deprecated="not-a-boolean" placeOnGround="not-a-boolean"/>
        </Root>
        """));

    assertEquals("BrokenMetadata", info.getModelName());
    assertEquals("", info.getCreator());
    assertEquals(-1, info.getCreationYear());
    assertFalse(info.getPlaceOnGround());
    assertEquals(AxisAlignedBox.Empty, info.getBoundingBox());
    assertArrayEquals(new String[0], info.getTags());
    assertArrayEquals(new String[0], info.getGroupTags());
    assertArrayEquals(new String[0], info.getThemeTags());
    assertTrue(info.createModelManifest().provenance.created instanceof ZonedDateTime);
  }

  @Test
  public void createsManifestEntriesForModelVariants() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Tree" creator="CMU" creationYear="2020" placeOnGround="true">
          <Tags><Tag>plant</Tag></Tags>
          <GroupTags><GroupTag>nature</GroupTag></GroupTags>
          <ThemeTags><ThemeTag>forest</ThemeTag></ThemeTags>
          <Resource resourceName="DEFAULT" modelName="TreeModel" textureName="Bark"/>
          <Resource resourceName="SNOW" modelName="TreeModel" textureName="Snow"/>
        </AliceModel>
        """));

    ModelManifest manifest = info.createModelManifest();

    assertEquals("Tree", manifest.description.name);
    assertEquals("Tree.png", manifest.description.icon);
    assertEquals("CMU", manifest.provenance.creator);
    assertEquals(Year.of(2020), manifest.provenance.created);
    assertEquals("Tree", manifest.metadata.identifier.name);
    assertEquals(Manifest.ProjectType.Model, manifest.metadata.identifier.type);
    assertTrue(manifest.placeOnGround);
    assertEquals(2, manifest.resources.size());
    assertEquals(2, manifest.textureSets.size());
    assertEquals(2, manifest.models.size());
    assertNotNull(manifest.getStructure("TreeModel_Bark"));
    assertNotNull(manifest.getStructure("TreeModel_Snow"));
    assertNotNull(manifest.getTextureSet("TreeModel_Bark"));
    assertNotNull(manifest.getTextureSet("TreeModel_Snow"));
    assertEquals("DEFAULT", manifest.models.get(0).name);
    assertEquals("TreeModel_Bark", manifest.models.get(0).structure);
    assertEquals("TreeModel_Bark", manifest.models.get(0).textureSet);
    assertEquals("DEFAULT.png", manifest.models.get(0).icon);
  }

  @Test
  public void subResourceLookupPrefersExactModelAndTextureBeforeModelFallback() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Chair">
          <Resource resourceName="GENERIC" modelName="ChairModel"/>
          <Resource resourceName="RED" modelName="ChairModel" textureName="Red"/>
          <Resource resourceName="BLUE" modelName="ChairModel" textureName="Blue"/>
        </AliceModel>
        """));

    assertEquals("RED", info.getSubResource("ChairModel", "Red").getResourceName());
    assertEquals("BLUE", info.getSubResource("ChairModel", "Blue").getResourceName());
    assertEquals("GENERIC", info.getSubResource("ChairModel", "Green").getResourceName());
    assertNull(info.getSubResource("TableModel", "Red"));
  }

  @Test
  public void manifestDeduplicatesSharedStructureAndTextureSets() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Chair">
          <Resource resourceName="DEFAULT" modelName="ChairModel" textureName="Wood"/>
          <Resource resourceName="COPY" modelName="ChairModel" textureName="Wood"/>
        </AliceModel>
        """));

    ModelManifest manifest = info.createModelManifest();

    assertEquals(1, manifest.resources.size());
    assertEquals(1, manifest.textureSets.size());
    assertEquals(2, manifest.models.size());
    assertEquals("ChairModel_Wood", manifest.resources.get(0).name);
    assertEquals("ChairModel_Wood", manifest.textureSets.get(0).name);
    assertEquals("DEFAULT", manifest.models.get(0).name);
    assertEquals("COPY", manifest.models.get(1).name);
    assertEquals(manifest.models.get(0).structure, manifest.models.get(1).structure);
    assertEquals(manifest.models.get(0).textureSet, manifest.models.get(1).textureSet);
  }

  @Test
  public void subResourcePlaceOnGroundFalseOverridesTrueParent() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Prop" placeOnGround="true">
          <Resource resourceName="FLOATING" modelName="PropModel" placeOnGround="false"/>
        </AliceModel>
        """));

    ModelResourceInfo variant = info.getSubResource("FLOATING");

    assertTrue(info.getPlaceOnGround());
    assertFalse(variant.getPlaceOnGround());
  }

  @Test
  public void subResourceMissingPlaceOnGroundInheritsFromParent() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Prop" placeOnGround="true">
          <Resource resourceName="DEFAULT" modelName="PropModel"/>
        </AliceModel>
        """));

    ModelResourceInfo variant = info.getSubResource("DEFAULT");

    assertTrue(variant.getPlaceOnGround());
  }

  @Test
  public void manifestUsesModelNameWhenTextureNameIsMissing() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Chair">
          <Resource resourceName="GENERIC" modelName="ChairModel"/>
        </AliceModel>
        """));

    ModelManifest manifest = info.createModelManifest();

    assertNotNull(manifest.getStructure("ChairModel"));
    assertNotNull(manifest.getTextureSet("ChairModel"));
    assertNull(manifest.getStructure("ChairModel_null"));
    assertEquals("ChairModel", manifest.models.get(0).structure);
    assertEquals("ChairModel", manifest.models.get(0).textureSet);
  }

  @Test
  public void subResourceTagsIgnoreNestedUnrelatedTagElements() throws Exception {
    ModelResourceInfo info = new ModelResourceInfo(parseXml("""
        <AliceModel name="Tree">
          <Tags><Tag>parent</Tag></Tags>
          <GroupTags><GroupTag>parentGroup</GroupTag></GroupTags>
          <ThemeTags><ThemeTag>parentTheme</ThemeTag></ThemeTags>
          <Resource resourceName="DEFAULT" modelName="TreeModel">
            <Tags><Tag>variant</Tag></Tags>
            <GroupTags><GroupTag>prop</GroupTag></GroupTags>
            <ThemeTags><ThemeTag>forest</ThemeTag></ThemeTags>
            <Nested>
              <Tag>ignored</Tag>
              <GroupTag>ignoredGroup</GroupTag>
              <ThemeTag>ignoredTheme</ThemeTag>
            </Nested>
          </Resource>
        </AliceModel>
        """));

    ModelResourceInfo variant = info.getSubResource("DEFAULT");

    assertArrayEquals(new String[] {"parent", "variant"}, variant.getTags());
    assertArrayEquals(new String[] {"parentGroup", "prop"}, variant.getGroupTags());
    assertArrayEquals(new String[] {"parentTheme", "forest"}, variant.getThemeTags());
  }

  private static Document parseXml(String xml) throws Exception {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    return factory.newDocumentBuilder().parse(new InputSource(new StringReader(xml)));
  }
}
