package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class SystemPropertyTest {

  private SystemProperty createProperty(String key, String value) {
    return new SystemProperty(key, value);
  }

  private List<String> keys(List<SystemProperty> properties) {
    List<String> result = new ArrayList<String>();
    for (SystemProperty property : properties) {
      result.add(property.getKey());
    }
    return result;
  }

  private List<SystemProperty> reversedProperties(String prefix, int count) {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    for (int i = count - 1; i >= 0; i--) {
      String key = String.format(prefix + "-%03d", i);
      properties.add(createProperty(key, "value-" + i));
    }
    return properties;
  }

  @Test
  public void constructorStoresBasicKeyAndValue() {
    SystemProperty property = createProperty("os.name", "Linux");
    assertEquals("os.name", property.getKey());
    assertEquals("Linux", property.getValue());
  }

  @Test
  public void constructorStoresEmptyKeyAndEmptyValue() {
    SystemProperty property = createProperty("", "");
    assertEquals("", property.getKey());
    assertEquals("", property.getValue());
    assertEquals("SystemProperty[:]", property.toString());
  }

  @Test
  public void constructorStoresNullKeyAndNullValue() {
    SystemProperty property = createProperty(null, null);
    assertNull(property.getKey());
    assertNull(property.getValue());
    assertEquals("SystemProperty[null:null]", property.toString());
  }

  @Test
  public void constructorStoresNullValueWithRealKey() {
    SystemProperty property = createProperty("user.home", null);
    assertEquals("user.home", property.getKey());
    assertNull(property.getValue());
    assertEquals("SystemProperty[user.home:null]", property.toString());
  }

  @Test
  public void constructorStoresNullKeyWithRealValue() {
    SystemProperty property = createProperty(null, "value");
    assertNull(property.getKey());
    assertEquals("value", property.getValue());
    assertEquals("SystemProperty[null:value]", property.toString());
  }

  @Test
  public void compareToIsReflexive() {
    SystemProperty property = createProperty("alpha", "1");
    assertEquals(0, property.compareTo(property));
  }

  @Test
  public void compareToIsSymmetricForDifferentKeys() {
    SystemProperty alpha = createProperty("alpha", "1");
    SystemProperty beta = createProperty("beta", "2");
    assertTrue(alpha.compareTo(beta) < 0);
    assertTrue(beta.compareTo(alpha) > 0);
  }

  @Test
  public void compareToIsTransitive() {
    SystemProperty alpha = createProperty("alpha", "1");
    SystemProperty beta = createProperty("beta", "2");
    SystemProperty gamma = createProperty("gamma", "3");
    assertTrue(alpha.compareTo(beta) < 0);
    assertTrue(beta.compareTo(gamma) < 0);
    assertTrue(alpha.compareTo(gamma) < 0);
  }

  @Test
  public void compareToIsAntiSymmetric() {
    SystemProperty alpha = createProperty("alpha", "1");
    SystemProperty beta = createProperty("beta", "2");
    assertEquals(-Integer.signum(beta.compareTo(alpha)), Integer.signum(alpha.compareTo(beta)));
  }

  @Test
  public void compareToEmptyKeysAreEqual() {
    SystemProperty left = createProperty("", "left");
    SystemProperty right = createProperty("", "right");
    assertEquals(0, left.compareTo(right));
  }

  @Test
  public void compareToCaseMatters() {
    SystemProperty upper = createProperty("Alpha", "1");
    SystemProperty lower = createProperty("alpha", "2");
    assertTrue(upper.compareTo(lower) < 0);
    assertTrue(lower.compareTo(upper) > 0);
  }

  @Test
  public void compareToUsesLexicalOrderForNumericKeys() {
    SystemProperty ten = createProperty("10", "ten");
    SystemProperty two = createProperty("2", "two");
    assertTrue(ten.compareTo(two) < 0);
    assertTrue(two.compareTo(ten) > 0);
  }

  @Test
  public void compareToSupportsSpecialCharacterKeys() {
    SystemProperty bang = createProperty("!important", "x");
    SystemProperty letter = createProperty("alpha", "y");
    assertTrue(bang.compareTo(letter) < 0);
    assertTrue(letter.compareTo(bang) > 0);
  }

  @Test
  public void compareToIgnoresValueWhenKeysMatch() {
    SystemProperty left = createProperty("same", "one");
    SystemProperty right = createProperty("same", "two");
    assertEquals(0, left.compareTo(right));
  }

  @Test
  public void compareToNullKeyThrowsNullPointerException() {
    SystemProperty property = createProperty(null, "value");
    try {
      property.compareTo(createProperty("other", "value"));
      fail("Expected NullPointerException");
    } catch (NullPointerException npe) {
      assertNotNull(npe);
    }
  }

  @Test
  public void toStringMatchesExactFormat() {
    SystemProperty property = createProperty("java.version", "21");
    assertEquals("SystemProperty[java.version:21]", property.toString());
  }

  @Test
  public void toStringSupportsNewlines() {
    SystemProperty property = createProperty("multiline", "line1\nline2");
    assertEquals("SystemProperty[multiline:line1\nline2]", property.toString());
  }

  @Test
  public void toStringSupportsTabs() {
    SystemProperty property = createProperty("tabbed", "a\tb");
    assertEquals("SystemProperty[tabbed:a\tb]", property.toString());
  }

  @Test
  public void toStringSupportsUnicode() {
    SystemProperty property = createProperty("emoji", "🙂🚀");
    assertEquals("SystemProperty[emoji:🙂🚀]", property.toString());
  }

  @Test
  public void toStringSupportsBrackets() {
    SystemProperty property = createProperty("array", "[1,2,3]");
    assertEquals("SystemProperty[array:[1,2,3]]", property.toString());
  }

  @Test
  public void toStringSupportsEmptyValue() {
    SystemProperty property = createProperty("empty", "");
    assertEquals("SystemProperty[empty:]", property.toString());
  }

  @Test
  public void toStringSupportsWindowsStylePaths() {
    SystemProperty property = createProperty("user.dir", "C:\\alice\\bin");
    assertEquals("SystemProperty[user.dir:C:\\alice\\bin]", property.toString());
  }

  @Test
  public void toStringSupportsJsonLikeValues() {
    SystemProperty property = createProperty("config", "{\"enabled\":true}");
    assertEquals("SystemProperty[config:{\"enabled\":true}]", property.toString());
  }

  @Test
  public void toStringSupportsXmlLikeValues() {
    SystemProperty property = createProperty("xml", "<node attr=\"value\"/>");
    assertEquals("SystemProperty[xml:<node attr=\"value\"/>]", property.toString());
  }

  @Test
  public void gettersReturnConstructorArgumentsVerbatim() {
    SystemProperty property = createProperty("a=b", "c:d");
    assertEquals("a=b", property.getKey());
    assertEquals("c:d", property.getValue());
  }

  @Test
  public void constructorAllowsVeryLongValues() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 200; i++) {
      builder.append("x");
    }
    SystemProperty property = createProperty("long", builder.toString());
    assertEquals(200, property.getValue().length());
    assertTrue(property.toString().contains(builder.toString()));
  }

  @Test
  public void constructorAllowsVeryLongKeys() {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < 50; i++) {
      builder.append("key");
    }
    SystemProperty property = createProperty(builder.toString(), "value");
    assertEquals(builder.toString(), property.getKey());
    assertTrue(property.toString().startsWith("SystemProperty["));
  }

  @Test
  public void sortingListOf3PropertiesUsesCompareToByKey() {
    List<SystemProperty> properties = reversedProperties("key", 3);
    Collections.sort(properties);
    assertEquals(3, properties.size());
    assertEquals(String.format("key-%03d", 0), properties.get(0).getKey());
    assertEquals(String.format("key-%03d", 2), properties.get(properties.size() - 1).getKey());
    assertEquals(keys(properties).get(0), properties.get(0).getKey());
  }

  @Test
  public void sortingListOf6PropertiesUsesCompareToByKey() {
    List<SystemProperty> properties = reversedProperties("key", 6);
    Collections.sort(properties);
    assertEquals(6, properties.size());
    assertEquals(String.format("key-%03d", 0), properties.get(0).getKey());
    assertEquals(String.format("key-%03d", 5), properties.get(properties.size() - 1).getKey());
    assertEquals(keys(properties).get(0), properties.get(0).getKey());
  }

  @Test
  public void sortingListOf20PropertiesUsesCompareToByKey() {
    List<SystemProperty> properties = reversedProperties("key", 20);
    Collections.sort(properties);
    assertEquals(20, properties.size());
    assertEquals(String.format("key-%03d", 0), properties.get(0).getKey());
    assertEquals(String.format("key-%03d", 19), properties.get(properties.size() - 1).getKey());
    assertEquals(keys(properties).get(0), properties.get(0).getKey());
  }

  @Test
  public void sortingListOf100PropertiesUsesCompareToByKey() {
    List<SystemProperty> properties = reversedProperties("key", 100);
    Collections.sort(properties);
    assertEquals(100, properties.size());
    assertEquals(String.format("key-%03d", 0), properties.get(0).getKey());
    assertEquals(String.format("key-%03d", 99), properties.get(properties.size() - 1).getKey());
    assertEquals(keys(properties).get(0), properties.get(0).getKey());
  }

  @Test
  public void sortingListPreservesDuplicateKeysTogether() {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    properties.add(createProperty("beta", "2"));
    properties.add(createProperty("alpha", "1"));
    properties.add(createProperty("alpha", "3"));
    Collections.sort(properties);
    assertEquals("alpha", properties.get(0).getKey());
    assertEquals("alpha", properties.get(1).getKey());
    assertEquals("beta", properties.get(2).getKey());
  }

  @Test
  public void sortingListOfMixedCaseKeysUsesNaturalStringOrder() {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    properties.add(createProperty("beta", "2"));
    properties.add(createProperty("Alpha", "1"));
    properties.add(createProperty("alpha", "3"));
    Collections.sort(properties);
    assertEquals("Alpha", properties.get(0).getKey());
    assertEquals("alpha", properties.get(1).getKey());
    assertEquals("beta", properties.get(2).getKey());
  }

  @Test
  public void sortingListOfSpecialCharacterKeysPlacesPunctuationFirst() {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    properties.add(createProperty("zeta", "3"));
    properties.add(createProperty("_underscore", "2"));
    properties.add(createProperty("!bang", "1"));
    Collections.sort(properties);
    assertEquals("!bang", properties.get(0).getKey());
    assertEquals("_underscore", properties.get(1).getKey());
    assertEquals("zeta", properties.get(2).getKey());
  }

  @Test
  public void compareToWorksForLongKeys() {
    String firstKey = "alpha.alpha.alpha";
    String secondKey = "alpha.alpha.beta";
    SystemProperty first = createProperty(firstKey, "1");
    SystemProperty second = createProperty(secondKey, "2");
    assertTrue(first.compareTo(second) < 0);
    assertTrue(second.compareTo(first) > 0);
  }

  @Test
  public void compareToWorksForKeysContainingWhitespace() {
    SystemProperty first = createProperty("alpha key", "1");
    SystemProperty second = createProperty("beta key", "2");
    assertTrue(first.compareTo(second) < 0);
    assertTrue(second.compareTo(first) > 0);
  }

  @Test
  public void compareToWorksForKeysContainingDashes() {
    SystemProperty first = createProperty("alpha-key", "1");
    SystemProperty second = createProperty("alpha_key", "2");
    assertTrue(first.compareTo(second) < 0);
    assertTrue(second.compareTo(first) > 0);
  }

  @Test
  public void compareToWorksForKeysContainingPeriods() {
    SystemProperty first = createProperty("java.home", "1");
    SystemProperty second = createProperty("java.io.tmpdir", "2");
    // "java.home" < "java.io.tmpdir" lexicographically ('h' < 'i')
    assertTrue(first.compareTo(second) < 0);
    assertTrue(second.compareTo(first) > 0);
  }

  @Test
  public void sortingAlreadySortedListLeavesOrderIntact() {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    properties.add(createProperty("alpha", "1"));
    properties.add(createProperty("beta", "2"));
    properties.add(createProperty("gamma", "3"));
    Collections.sort(properties);
    assertEquals("alpha", properties.get(0).getKey());
    assertEquals("beta", properties.get(1).getKey());
    assertEquals("gamma", properties.get(2).getKey());
  }

  @Test
  public void sortingReverseOrderedListProducesAscendingKeys() {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    properties.add(createProperty("gamma", "3"));
    properties.add(createProperty("beta", "2"));
    properties.add(createProperty("alpha", "1"));
    Collections.sort(properties);
    assertEquals("alpha", properties.get(0).getKey());
    assertEquals("beta", properties.get(1).getKey());
    assertEquals("gamma", properties.get(2).getKey());
  }

  @Test
  public void sortingListOfKeysWithTabsAndSpacesStillUsesStringOrder() {
    List<SystemProperty> properties = new ArrayList<SystemProperty>();
    properties.add(createProperty("beta key", "2"));
    properties.add(createProperty("alpha	key", "1"));
    Collections.sort(properties);
    assertEquals("alpha	key", properties.get(0).getKey());
    assertEquals("beta key", properties.get(1).getKey());
  }

  @Test
  public void toStringSupportsTabsAndNewlinesTogether() {
    SystemProperty property = createProperty("mixed", "a\tb\nline2");
    assertEquals("SystemProperty[mixed:a\tb\nline2]", property.toString());
  }

  @Test
  public void toStringSupportsBracketCharactersInKey() {
    SystemProperty property = createProperty("[section]", "value");
    assertEquals("SystemProperty[[section]:value]", property.toString());
  }

  @Test
  public void toStringSupportsColonInValue() {
    SystemProperty property = createProperty("path", "a:b:c");
    assertEquals("SystemProperty[path:a:b:c]", property.toString());
  }

  @Test
  public void compareToWithEmptyStringAndLetterKeyOrdersEmptyFirst() {
    SystemProperty empty = createProperty("", "value");
    SystemProperty alpha = createProperty("alpha", "value");
    assertTrue(empty.compareTo(alpha) < 0);
    assertTrue(alpha.compareTo(empty) > 0);
  }

}
