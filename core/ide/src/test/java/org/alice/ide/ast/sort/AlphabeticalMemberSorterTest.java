package org.alice.ide.ast.sort;

import org.junit.Test;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class AlphabeticalMemberSorterTest {
  @Test
  public void isSingletonEnum() {
    assertEquals(1, AlphabeticalMemberSorter.values().length);
    assertSame(AlphabeticalMemberSorter.SINGLETON, AlphabeticalMemberSorter.valueOf("SINGLETON"));
  }
  @Test
  public void implementsMemberSorter() {
    assertTrue(MemberSorter.class.isAssignableFrom(AlphabeticalMemberSorter.class));
  }
  @Test
  public void sortsFieldsByName() {
    UserField alpha = createField("alpha");
    UserField beta = createField("beta");
    UserField gamma = createField("gamma");
    List<UserField> input = Arrays.asList(gamma, alpha, beta);
    List<UserField> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);
    assertEquals("alpha", sorted.get(0).getName());
    assertEquals("beta", sorted.get(1).getName());
    assertEquals("gamma", sorted.get(2).getName());
  }
  @Test
  public void sortsEmptyList() {
    List<UserField> result = AlphabeticalMemberSorter.SINGLETON.createSortedList(Collections.emptyList());
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
  @Test
  public void sortedListIsNewInstance() {
    UserField f = createField("test");
    List<UserField> input = Arrays.asList(f);
    List<UserField> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(input);
    assertNotSame(input, sorted);
  }
  @Test
  public void caseInsensitiveSort() {
    UserField upper = createField("Bravo");
    UserField lower = createField("alpha");
    List<UserField> sorted = AlphabeticalMemberSorter.SINGLETON.createSortedList(Arrays.asList(upper, lower));
    assertEquals("alpha", sorted.get(0).getName());
    assertEquals("Bravo", sorted.get(1).getName());
  }
  private static UserField createField(String name) {
    UserField field = new UserField();
    field.name.setValue(name);
    field.valueType.setValue(JavaType.getInstance(String.class));
    return field;
  }
}
