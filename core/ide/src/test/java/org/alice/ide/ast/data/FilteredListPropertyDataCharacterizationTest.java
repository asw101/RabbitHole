package org.alice.ide.ast.data;

import org.junit.Test;
import org.lgna.croquet.data.RefreshableListData;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Characterization tests for FilteredListPropertyData.
 * This is an abstract class that manages listener registration on
 * ListProperty instances and provides filtered list creation.
 */
public class FilteredListPropertyDataCharacterizationTest {

  // ══════════════════════════════════════════════════════════════
  // Structural characterization
  // ══════════════════════════════════════════════════════════════

  @Test
  public void isAbstract() {
    assertTrue(Modifier.isAbstract(FilteredListPropertyData.class.getModifiers()));
  }

  @Test
  public void isPublic() {
    assertTrue(Modifier.isPublic(FilteredListPropertyData.class.getModifiers()));
  }

  @Test
  public void extendsRefreshableListData() {
    assertTrue(RefreshableListData.class.isAssignableFrom(FilteredListPropertyData.class));
  }

  @Test
  public void inCorrectPackage() {
    assertEquals("org.alice.ide.ast.data",
        FilteredListPropertyData.class.getPackage().getName());
  }

  @Test
  public void hasListPropertyListenerField() throws Exception {
    Field f = FilteredListPropertyData.class.getDeclaredField("listPropertyListener");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void hasPropertyListenerField() throws Exception {
    Field f = FilteredListPropertyData.class.getDeclaredField("propertyListener");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
    assertTrue(Modifier.isFinal(f.getModifiers()));
  }

  @Test
  public void hasListPropertyField() throws Exception {
    Field f = FilteredListPropertyData.class.getDeclaredField("listProperty");
    assertTrue(Modifier.isPrivate(f.getModifiers()));
  }

  // ══════════════════════════════════════════════════════════════
  // Method contracts
  // ══════════════════════════════════════════════════════════════

  @Test
  public void hasIsAcceptableItemAbstractMethod() {
    boolean found = Arrays.stream(FilteredListPropertyData.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("isAcceptableItem")
            && Modifier.isAbstract(m.getModifiers())
            && Modifier.isProtected(m.getModifiers()));
    assertTrue("Must have abstract protected isAcceptableItem(E)", found);
  }

  @Test
  public void hasCreateValuesMethod() {
    boolean found = Arrays.stream(FilteredListPropertyData.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("createValues")
            && Modifier.isProtected(m.getModifiers()));
    assertTrue(found);
  }

  @Test
  public void hasGetListPropertyMethod() {
    boolean found = Arrays.stream(FilteredListPropertyData.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("getListProperty")
            && Modifier.isProtected(m.getModifiers()));
    assertTrue(found);
  }

  @Test
  public void hasSetListPropertyMethod() {
    boolean found = Arrays.stream(FilteredListPropertyData.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("setListProperty")
            && Modifier.isPublic(m.getModifiers()));
    assertTrue(found);
  }

  @Test
  public void setListPropertyMethod_takesOneParameter() {
    boolean found = Arrays.stream(FilteredListPropertyData.class.getDeclaredMethods())
        .anyMatch(m -> m.getName().equals("setListProperty")
            && m.getParameterCount() == 1);
    assertTrue(found);
  }

  @Test
  public void hasOneConstructor() {
    assertEquals(1, FilteredListPropertyData.class.getDeclaredConstructors().length);
  }

  @Test
  public void constructorIsPublic() {
    assertTrue(Modifier.isPublic(
        FilteredListPropertyData.class.getDeclaredConstructors()[0].getModifiers()));
  }

  @Test
  public void constructorTakesItemCodec() {
    Class<?>[] paramTypes = FilteredListPropertyData.class.getDeclaredConstructors()[0].getParameterTypes();
    assertEquals(1, paramTypes.length);
    assertEquals("ItemCodec", paramTypes[0].getSimpleName());
  }

  // ══════════════════════════════════════════════════════════════
  // Listener pattern verification
  // ══════════════════════════════════════════════════════════════

  @Test
  public void listPropertyListenerImplementsFourMethods() throws Exception {
    Field f = FilteredListPropertyData.class.getDeclaredField("listPropertyListener");
    f.setAccessible(true);
    // The listener field is an anonymous implementation of ListPropertyListener
    // with 4 methods: added, cleared, removed, set
    Class<?> listenerType = f.getType();
    Set<String> methodNames = Arrays.stream(listenerType.getMethods())
        .map(Method::getName)
        .collect(java.util.stream.Collectors.toSet());
    assertTrue("Must have added()", methodNames.contains("added"));
    assertTrue("Must have cleared()", methodNames.contains("cleared"));
    assertTrue("Must have removed()", methodNames.contains("removed"));
    assertTrue("Must have set()", methodNames.contains("set"));
  }
}
