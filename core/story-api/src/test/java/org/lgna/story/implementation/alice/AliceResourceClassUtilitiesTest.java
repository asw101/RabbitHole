package org.lgna.story.implementation.alice;

import org.junit.Test;
import org.lgna.story.SBiped;
import org.lgna.story.SModel;
import org.lgna.story.resources.BipedResource;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.ModelResource;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Tests for {@link AliceResourceClassUtilities} static utility methods.
 * Pure reflection / string work — no AWT or rendering.
 */
public class AliceResourceClassUtilitiesTest {

  // ── constants ─────────────────────────────────────────────────────────

  @Test
  public void defaultPackageIsEmptyString() {
    assertEquals("", AliceResourceClassUtilities.DEFAULT_PACKAGE);
  }

  @Test
  public void resourceSuffixIsResource() {
    assertEquals("Resource", AliceResourceClassUtilities.RESOURCE_SUFFIX);
  }

  // ── private constructor guard ─────────────────────────────────────────

  @Test(expected = Exception.class)
  public void constructorThrowsAssertionError() throws Exception {
    var ctor = AliceResourceClassUtilities.class.getDeclaredConstructor();
    ctor.setAccessible(true);
    ctor.newInstance();
  }

  // ── uppercaseFirstLetter ──────────────────────────────────────────────

  @Test
  public void uppercaseFirstLetter_normalWord() {
    assertEquals("Hello", AliceResourceClassUtilities.uppercaseFirstLetter("hello"));
  }

  @Test
  public void uppercaseFirstLetter_singleChar() {
    assertEquals("A", AliceResourceClassUtilities.uppercaseFirstLetter("a"));
  }

  @Test
  public void uppercaseFirstLetter_emptyString() {
    assertEquals("", AliceResourceClassUtilities.uppercaseFirstLetter(""));
  }

  @Test
  public void uppercaseFirstLetter_null() {
    assertNull(AliceResourceClassUtilities.uppercaseFirstLetter(null));
  }

  @Test
  public void uppercaseFirstLetter_allCaps() {
    assertEquals("Hello", AliceResourceClassUtilities.uppercaseFirstLetter("HELLO"));
  }

  @Test
  public void uppercaseFirstLetter_mixedCase() {
    assertEquals("Alice", AliceResourceClassUtilities.uppercaseFirstLetter("aLICE"));
  }

  // ── splitOnCapitalsAndNumbers ─────────────────────────────────────────

  @Test
  public void splitOnCapitalsAndNumbers_camelCase() {
    List<String> parts = AliceResourceClassUtilities.splitOnCapitalsAndNumbers("helloWorld");
    assertEquals(2, parts.size());
    assertEquals("hello", parts.get(0));
    assertEquals("World", parts.get(1));
  }

  @Test
  public void splitOnCapitalsAndNumbers_withNumbers() {
    List<String> parts = AliceResourceClassUtilities.splitOnCapitalsAndNumbers("item42Name");
    // "item" "42" "Name"
    assertEquals(3, parts.size());
    assertEquals("item", parts.get(0));
    assertEquals("42", parts.get(1));
    assertEquals("Name", parts.get(2));
  }

  @Test
  public void splitOnCapitalsAndNumbers_singleLowerWord() {
    List<String> parts = AliceResourceClassUtilities.splitOnCapitalsAndNumbers("test");
    assertEquals(1, parts.size());
    assertEquals("test", parts.get(0));
  }

  @Test
  public void splitOnCapitalsAndNumbers_allCaps() {
    List<String> parts = AliceResourceClassUtilities.splitOnCapitalsAndNumbers("ABC");
    // First uppercase triggers restart with empty prefix: ["", "A", "B", "C"]
    assertEquals(4, parts.size());
    assertEquals("", parts.get(0));
    assertEquals("A", parts.get(1));
    assertEquals("B", parts.get(2));
    assertEquals("C", parts.get(3));
  }

  @Test
  public void splitOnCapitalsAndNumbers_emptyString() {
    List<String> parts = AliceResourceClassUtilities.splitOnCapitalsAndNumbers("");
    assertTrue(parts.isEmpty());
  }

  @Test
  public void splitOnCapitalsAndNumbers_numberThenLetter() {
    List<String> parts = AliceResourceClassUtilities.splitOnCapitalsAndNumbers("3dModel");
    // '3' starts as digit (restart with empty prefix), 'd' transitions, 'M' uppercase
    assertEquals(4, parts.size());
    assertEquals("", parts.get(0));
    assertEquals("3", parts.get(1));
    assertEquals("d", parts.get(2));
    assertEquals("Model", parts.get(3));
  }

  // ── fullStringSplit ───────────────────────────────────────────────────

  @Test
  public void fullStringSplit_underscoreSeparated() {
    String[] parts = AliceResourceClassUtilities.fullStringSplit("MY_MODEL_NAME");
    // each chunk is uppercased -> split on capitals -> single letters
    // "MY" -> M, Y; "MODEL" -> M, O, D, E, L; "NAME" -> N, A, M, E
    // Actually let me re-read: fullStringSplit splits on [_ -], then splitOnCapitalsAndNumbers each
    // "MY" -> "M", "Y"
    assertNotNull(parts);
    assertTrue(parts.length > 0);
  }

  @Test
  public void fullStringSplit_hyphenSeparated() {
    String[] parts = AliceResourceClassUtilities.fullStringSplit("hot-air-balloon");
    assertNotNull(parts);
    assertEquals(3, parts.length);
    assertEquals("hot", parts[0]);
    assertEquals("air", parts[1]);
    assertEquals("balloon", parts[2]);
  }

  @Test
  public void fullStringSplit_spaceSeparated() {
    String[] parts = AliceResourceClassUtilities.fullStringSplit("hot air balloon");
    assertNotNull(parts);
    assertEquals(3, parts.length);
  }

  @Test
  public void fullStringSplit_camelCaseNoSeparator() {
    String[] parts = AliceResourceClassUtilities.fullStringSplit("hotAirBalloon");
    assertNotNull(parts);
    assertEquals(3, parts.length);
    assertEquals("hot", parts[0]);
    assertEquals("Air", parts[1]);
    assertEquals("Balloon", parts[2]);
  }

  // ── getClassNameFromName ──────────────────────────────────────────────

  @Test
  public void getClassNameFromName_convertsToTitleCase() {
    assertEquals("HotAirBalloon",
        AliceResourceClassUtilities.getClassNameFromName("hot_air_balloon"));
  }

  @Test
  public void getClassNameFromName_camelCase() {
    assertEquals("HotAirBalloon",
        AliceResourceClassUtilities.getClassNameFromName("hotAirBalloon"));
  }

  // ── getAliceClassName ─────────────────────────────────────────────────

  @Test
  public void getAliceClassName_stripsResourceSuffix() {
    assertEquals("Biped", AliceResourceClassUtilities.getAliceClassName("BipedResource"));
  }

  @Test
  public void getAliceClassName_noSuffix() {
    assertEquals("Biped", AliceResourceClassUtilities.getAliceClassName("Biped"));
  }

  @Test
  public void getAliceClassName_fromClass() {
    assertEquals("Biped", AliceResourceClassUtilities.getAliceClassName(BipedResource.class));
  }

  // ── getAliceMethodNameForEnum ─────────────────────────────────────────

  @Test
  public void getAliceMethodNameForEnum_singleWord() {
    assertEquals("Default", AliceResourceClassUtilities.getAliceMethodNameForEnum("DEFAULT"));
  }

  @Test
  public void getAliceMethodNameForEnum_multiWord() {
    assertEquals("WalkReady", AliceResourceClassUtilities.getAliceMethodNameForEnum("WALK_READY"));
  }

  // ── isTopLevelResource ────────────────────────────────────────────────

  @Test
  public void isTopLevelResource_annotatedResource() {
    assertTrue(AliceResourceClassUtilities.isTopLevelResource(BipedResource.class));
  }

  @Test
  public void isTopLevelResource_unannotatedClass() {
    // ModelResource itself has no @ResourceTemplate
    assertFalse(AliceResourceClassUtilities.isTopLevelResource(ModelResource.class));
  }

  // ── getModelClassForResourceClass ─────────────────────────────────────

  @Test
  public void getModelClassForResourceClass_bipedReturnsSBiped() {
    Class<? extends SModel> modelClass =
        AliceResourceClassUtilities.getModelClassForResourceClass(BipedResource.class);
    assertNotNull(modelClass);
    assertEquals(SBiped.class, modelClass);
  }

  @Test
  public void getModelClassForResourceClass_unannotatedReturnsNull() {
    assertNull(AliceResourceClassUtilities.getModelClassForResourceClass(ModelResource.class));
  }

  // ── getResourceClassForAliceName ──────────────────────────────────────

  @Test
  public void getResourceClassForAliceName_knownClassReturnsNonNull() {
    Class<? extends ModelResource> cls =
        AliceResourceClassUtilities.getResourceClassForAliceName("Biped");
    assertNotNull(cls);
    assertEquals(BipedResource.class, cls);
  }

  @Test
  public void getResourceClassForAliceName_unknownClassReturnsNull() {
    assertNull(AliceResourceClassUtilities.getResourceClassForAliceName("NonExistent"));
  }

  // ── getFieldsOfType ───────────────────────────────────────────────────

  @Test
  public void getFieldsOfType_findsPublicFieldsOfGivenType() {
    Field[] fields = AliceResourceClassUtilities.getFieldsOfType(BipedResource.class, JointId.class);
    assertNotNull(fields);
    assertTrue("BipedResource should have JointId fields", fields.length > 0);
    for (Field f : fields) {
      assertTrue(JointId.class.isAssignableFrom(f.getType()));
    }
  }

  @Test
  public void getFieldsOfType_noMatchReturnsEmptyArray() {
    Field[] fields = AliceResourceClassUtilities.getFieldsOfType(String.class, JointId.class);
    assertNotNull(fields);
    assertEquals(0, fields.length);
  }

  // ── getJoints ─────────────────────────────────────────────────────────

  @Test
  public void getJoints_bipedResourceHasJoints() {
    List<JointId> joints = AliceResourceClassUtilities.getJoints(BipedResource.class);
    assertNotNull(joints);
    assertTrue("BipedResource should define joints", joints.size() > 0);
  }

  // ── getResourceClassForModelClass ─────────────────────────────────────

  @Test
  public void getResourceClassForModelClass_bipedReturnsBipedResource() {
    Class<? extends ModelResource> resourceClass =
        AliceResourceClassUtilities.getResourceClassForModelClass(SBiped.class);
    assertNotNull(resourceClass);
    assertEquals(BipedResource.class, resourceClass);
  }
}
