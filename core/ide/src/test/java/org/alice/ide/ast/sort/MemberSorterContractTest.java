package org.alice.ide.ast.sort;

import org.junit.Test;
import org.lgna.project.ast.UserMethod;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class MemberSorterContractTest {

  private final MemberSorter sorter = AlphabeticalMemberSorter.SINGLETON;

  private UserMethod createMethod(String name) {
    UserMethod method = new UserMethod();
    if (name != null) {
      method.name.setValue(name);
    }
    return method;
  }

  // Only unique tests remain here — duplicates consolidated into MemberSorterTest

  @Test
  public void createSortedListWithNullElementsInList() {
    List<UserMethod> input = Arrays.asList(createMethod("beta"), null, createMethod("alpha"));

    List<UserMethod> result = sorter.createSortedList(input);

    assertEquals(3, result.size());
    assertNull(result.get(0));
    assertEquals("alpha", result.get(1).getName());
    assertEquals("beta", result.get(2).getName());
  }
}
