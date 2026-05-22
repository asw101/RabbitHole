package org.alice.ide.ast.sort;

import org.junit.Test;
import org.lgna.project.ast.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Comprehensive tests for {@link AlphabeticalMemberSorter} with various member types.
 */
public class AlphabeticalMemberSorterComprehensiveTest {

  private static UserMethod createMethod(String name) {
    UserMethod method = new UserMethod();
    method.name.setValue(name);
    method.returnType.setValue(JavaType.VOID_TYPE);
    return method;
  }

  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(Object.class));
    return field;
  }

  @Test
  public void createSortedList_twoMethods_alphabetical() {
    UserMethod a = createMethod("alpha");
    UserMethod b = createMethod("beta");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(b, a));
    assertEquals("alpha", sorted.get(0).name.getValue());
    assertEquals("beta", sorted.get(1).name.getValue());
  }

  @Test
  public void createSortedList_alreadySorted() {
    UserMethod a = createMethod("apple");
    UserMethod b = createMethod("banana");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(a, b));
    assertEquals("apple", sorted.get(0).name.getValue());
    assertEquals("banana", sorted.get(1).name.getValue());
  }

  @Test
  public void createSortedList_threeMethods_reversed() {
    UserMethod a = createMethod("cherry");
    UserMethod b = createMethod("apple");
    UserMethod c = createMethod("banana");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(a, c, b));
    assertEquals("apple", sorted.get(0).name.getValue());
    assertEquals("banana", sorted.get(1).name.getValue());
    assertEquals("cherry", sorted.get(2).name.getValue());
  }

  @Test
  public void createSortedList_fields() {
    UserField a = createField("zebra");
    UserField b = createField("apple");
    List<UserField> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(a, b));
    assertEquals("apple", sorted.get(0).name.getValue());
    assertEquals("zebra", sorted.get(1).name.getValue());
  }

  @Test
  public void createSortedList_singleItem() {
    UserMethod m = createMethod("only");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Collections.singletonList(m));
    assertEquals(1, sorted.size());
    assertEquals("only", sorted.get(0).name.getValue());
  }

  @Test
  public void createSortedList_fiveItems() {
    UserMethod e = createMethod("echo");
    UserMethod d = createMethod("delta");
    UserMethod c = createMethod("charlie");
    UserMethod b = createMethod("bravo");
    UserMethod a = createMethod("alpha");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(e, d, c, b, a));
    assertEquals("alpha", sorted.get(0).name.getValue());
    assertEquals("bravo", sorted.get(1).name.getValue());
    assertEquals("charlie", sorted.get(2).name.getValue());
    assertEquals("delta", sorted.get(3).name.getValue());
    assertEquals("echo", sorted.get(4).name.getValue());
  }

  @Test
  public void createSortedList_caseInsensitive() {
    UserMethod upper = createMethod("Zebra");
    UserMethod lower = createMethod("apple");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(upper, lower));
    // compareToIgnoreCase means Apple < Zebra
    assertEquals("apple", sorted.get(0).name.getValue());
    assertEquals("Zebra", sorted.get(1).name.getValue());
  }

  @Test
  public void createSortedList_sameNames() {
    UserMethod a = createMethod("same");
    UserMethod b = createMethod("same");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(a, b));
    assertEquals(2, sorted.size());
  }

  @Test
  public void singleton_isNotNull() {
    assertNotNull(AlphabeticalMemberSorter.SINGLETON);
  }

  @Test
  public void singleton_isMemberSorter() {
    assertTrue(AlphabeticalMemberSorter.SINGLETON instanceof MemberSorter);
  }

  @Test
  public void className_isAlphabeticalMemberSorter() {
    assertEquals("AlphabeticalMemberSorter", AlphabeticalMemberSorter.class.getSimpleName());
  }

  @Test
  public void createSortedList_returnsNewList() {
    List<UserMethod> original = Arrays.asList(createMethod("b"), createMethod("a"));
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(original);
    assertNotSame(original, sorted);
  }

  @Test
  public void createSortedList_doesNotModifyOriginal() {
    UserMethod b = createMethod("b");
    UserMethod a = createMethod("a");
    List<UserMethod> original = Arrays.asList(b, a);
    AlphabeticalMemberSorter.SINGLETON.createSortedList(original);
    assertSame(b, original.get(0));
    assertSame(a, original.get(1));
  }

  @Test
  public void createSortedList_emptyList() {
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Collections.emptyList());
    assertNotNull(sorted);
    assertTrue(sorted.isEmpty());
  }

  @Test
  public void createSortedList_multipleFields() {
    UserField f1 = createField("gamma");
    UserField f2 = createField("alpha");
    UserField f3 = createField("beta");
    List<UserField> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(f1, f3, f2));
    assertEquals("alpha", sorted.get(0).name.getValue());
    assertEquals("beta", sorted.get(1).name.getValue());
    assertEquals("gamma", sorted.get(2).name.getValue());
  }

  @Test
  public void createSortedList_withNumbersInNames() {
    UserMethod m1 = createMethod("method1");
    UserMethod m10 = createMethod("method10");
    UserMethod m2 = createMethod("method2");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(m10, m2, m1));
    assertEquals("method1", sorted.get(0).name.getValue());
    assertEquals("method10", sorted.get(1).name.getValue());
    assertEquals("method2", sorted.get(2).name.getValue());
  }

  @Test
  public void isEnum() {
    assertTrue(AlphabeticalMemberSorter.class.isEnum());
  }

  @Test
  public void values_hasOneSingleton() {
    assertEquals(1, AlphabeticalMemberSorter.values().length);
    assertEquals(AlphabeticalMemberSorter.SINGLETON, AlphabeticalMemberSorter.values()[0]);
  }
}
