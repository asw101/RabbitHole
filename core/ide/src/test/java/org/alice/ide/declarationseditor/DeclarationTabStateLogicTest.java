package org.alice.ide.declarationseditor;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.*;

public class DeclarationTabStateLogicTest {
  private static final class FakeEntry {
    private final String type;
    private final String name;
    private final boolean typeComposite;

    private FakeEntry(String type, String name, boolean typeComposite) {
      this.type = type;
      this.name = name;
      this.typeComposite = typeComposite;
    }

    @Override
    public boolean equals(Object other) {
      if (this == other) {
        return true;
      }
      if (!(other instanceof FakeEntry entry)) {
        return false;
      }
      return this.typeComposite == entry.typeComposite && Objects.equals(this.type, entry.type) && Objects.equals(this.name, entry.name);
    }

    @Override
    public int hashCode() {
      return Objects.hash(this.type, this.name, this.typeComposite);
    }

    @Override
    public String toString() {
      return this.name;
    }
  }

  private static FakeEntry typeEntry(String type) {
    return new FakeEntry(type, type + "Type", true);
  }

  private static FakeEntry memberEntry(String type, String name) {
    return new FakeEntry(type, name, false);
  }

  private static FakeEntry orphanEntry(String name) {
    return new FakeEntry(null, name, false);
  }

  @Test
  public void buildOrderedItemsGroupsMembersByTypeAndInsertsTypeTabs() {
    FakeEntry alphaMethod = memberEntry("Alpha", "alphaMethod");
    FakeEntry betaMethod = memberEntry("Beta", "betaMethod");
    FakeEntry alphaField = memberEntry("Alpha", "alphaField");

    List<FakeEntry> orderedItems = DeclarationTabStateLogic.buildOrderedItems(Arrays.asList(alphaMethod, betaMethod), alphaField, entry -> entry.type, entry -> entry.typeComposite, DeclarationTabStateLogicTest::typeEntry);

    assertEquals(Arrays.asList(typeEntry("Alpha"), alphaMethod, alphaField, null, typeEntry("Beta"), betaMethod), orderedItems);
  }

  @Test
  public void buildOrderedItemsAvoidsDuplicatingExistingTypeTabAndKeepsOrphansLast() {
    FakeEntry alphaType = typeEntry("Alpha");
    FakeEntry alphaMethod = memberEntry("Alpha", "alphaMethod");
    FakeEntry orphan = orphanEntry("orphan");

    List<FakeEntry> orderedItems = DeclarationTabStateLogic.buildOrderedItems(Arrays.asList(alphaType, alphaMethod), orphan, entry -> entry.type, entry -> entry.typeComposite, DeclarationTabStateLogicTest::typeEntry);

    assertEquals(Arrays.asList(alphaType, alphaMethod, null, orphan), orderedItems);
  }

  @Test
  public void buildOrderedItemsPreservesLeadingSeparatorForPureOrphans() {
    FakeEntry orphan = orphanEntry("orphan");

    List<FakeEntry> orderedItems = DeclarationTabStateLogic.buildOrderedItems(Collections.emptyList(), orphan, entry -> entry.type, entry -> entry.typeComposite, DeclarationTabStateLogicTest::typeEntry);

    assertEquals(Arrays.asList((FakeEntry) null, orphan), orderedItems);
  }
}
