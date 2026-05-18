package org.alice.ide.ast.sort;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.ManagementLevel;
import org.lgna.project.ast.UserMethod;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class AlphabeticalMemberSorterExtendedTest {

  private UserMethod method(String name) {
    UserMethod m = new UserMethod();
    m.name.setValue(name);
    m.returnType.setValue(JavaType.VOID_TYPE);
    m.managementLevel.setValue(ManagementLevel.NONE);
    return m;
  }

  @Test
  public void sorting_manyElements_allPresent() {
    List<UserMethod> methods = Arrays.asList(
        method("delta"), method("alpha"), method("charlie"),
        method("bravo"), method("echo"));
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(methods);
    assertEquals(5, sorted.size());
    assertEquals("alpha", sorted.get(0).getName());
    assertEquals("bravo", sorted.get(1).getName());
    assertEquals("charlie", sorted.get(2).getName());
    assertEquals("delta", sorted.get(3).getName());
    assertEquals("echo", sorted.get(4).getName());
  }

  @Test
  public void sorting_mixedCasePreservesOriginalCase() {
    UserMethod upper = method("Zebra");
    UserMethod lower = method("apple");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(upper, lower));
    assertEquals("apple", sorted.get(0).getName());
    assertEquals("Zebra", sorted.get(1).getName());
  }

  @Test
  public void sorting_duplicateNames_stableSorted() {
    UserMethod m1 = method("same");
    UserMethod m2 = method("same");
    UserMethod m3 = method("same");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(m1, m2, m3));
    assertEquals(3, sorted.size());
  }

  @Test
  public void sorting_specialCharacterNames() {
    UserMethod m1 = method("_private");
    UserMethod m2 = method("public");
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(m2, m1));
    assertEquals("_private", sorted.get(0).getName());
  }

  @Test
  public void createSortedList_returnsNewList() {
    UserMethod a = method("alpha");
    UserMethod b = method("bravo");
    List<UserMethod> original = Arrays.asList(b, a);
    List<UserMethod> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(original);
    assertNotSame(original, sorted);
  }

}
