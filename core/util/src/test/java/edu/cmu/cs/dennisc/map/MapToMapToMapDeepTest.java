package edu.cmu.cs.dennisc.map;

import org.junit.Test;

import static org.junit.Assert.*;

public class MapToMapToMapDeepTest {

  @Test
  public void newInstance_returnsNonNull() {
    assertNotNull(MapToMapToMap.newInstance());
  }

  @Test
  public void get_emptyMap_returnsNull() {
    assertNull(MapToMapToMap.newInstance().get("a", "b", "c"));
  }

  @Test
  public void put_thenGet_returnsValue() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", 42);

    assertEquals(Integer.valueOf(42), map.get("a", "b", "c"));
  }

  @Test
  public void multiplePuts_differentKeys() {
    MapToMapToMap<String, String, String, String> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", "v1");
    map.put("x", "y", "z", "v2");

    assertEquals("v1", map.get("a", "b", "c"));
    assertEquals("v2", map.get("x", "y", "z"));
  }

  @Test
  public void overwriteExistingKey() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", 1);
    map.put("a", "b", "c", 2);

    assertEquals(Integer.valueOf(2), map.get("a", "b", "c"));
  }

  @Test
  public void get_wrongFirstKey_returnsNull() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", 42);

    assertNull(map.get("x", "b", "c"));
  }

  @Test
  public void get_wrongSecondKey_returnsNull() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", 42);

    assertNull(map.get("a", "x", "c"));
  }

  @Test
  public void get_wrongThirdKey_returnsNull() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", 42);

    assertNull(map.get("a", "b", "x"));
  }

  @Test
  public void sharedFirstTwoKeys_differentThirdKey() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c1", 1);
    map.put("a", "b", "c2", 2);

    assertEquals(Integer.valueOf(1), map.get("a", "b", "c1"));
    assertEquals(Integer.valueOf(2), map.get("a", "b", "c2"));
  }

  @Test
  public void integerKeys() {
    MapToMapToMap<Integer, Integer, Integer, String> map = MapToMapToMap.newInstance();

    map.put(1, 2, 3, "value");

    assertEquals("value", map.get(1, 2, 3));
  }

  @Test
  public void nullValue() {
    MapToMapToMap<String, String, String, String> map = MapToMapToMap.newInstance();

    map.put("a", "b", "c", null);

    assertNull(map.get("a", "b", "c"));
  }

  @Test
  public void sharedFirstKey_differentSecondKeysRemainIndependent() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("group", "left", "value", 1);
    map.put("group", "right", "value", 2);

    assertEquals(Integer.valueOf(1), map.get("group", "left", "value"));
    assertEquals(Integer.valueOf(2), map.get("group", "right", "value"));
  }

  @Test
  public void overwriteOneBranch_doesNotAffectOtherBranches() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "b1", "c", 1);
    map.put("a", "b2", "c", 2);
    map.put("a", "b1", "c", 3);

    assertEquals(Integer.valueOf(3), map.get("a", "b1", "c"));
    assertEquals(Integer.valueOf(2), map.get("a", "b2", "c"));
  }

  @Test
  public void get_missingMiddleBranchAfterOtherInsert_returnsNull() {
    MapToMapToMap<String, String, String, Integer> map = MapToMapToMap.newInstance();

    map.put("a", "present", "c", 7);

    assertNull(map.get("a", "missing", "c"));
  }

  @Test
  public void nullValueInOneBranch_doesNotAffectOtherBranch() {
    MapToMapToMap<String, String, String, String> map = MapToMapToMap.newInstance();

    map.put("a", "b", "null", null);
    map.put("a", "b", "value", "kept");

    assertNull(map.get("a", "b", "null"));
    assertEquals("kept", map.get("a", "b", "value"));
  }
}
