package edu.cmu.cs.dennisc.pattern;

import org.junit.Assert;
import org.junit.Test;

public class DefaultNameableAndClassOwnerTrackableTest {
  public static class Holder {
    public static final DefaultNameableAndClassOwnerTrackable FIRST = new DefaultNameableAndClassOwnerTrackable();
    public static final DefaultNameableAndClassOwnerTrackable SECOND = new DefaultNameableAndClassOwnerTrackable();
  }

  @Test
  public void toStringFallsBackToDefaultNameableWhenNoOwnerExists() {
    DefaultNameableAndClassOwnerTrackable value = new DefaultNameableAndClassOwnerTrackable();
    value.setName("field");

    String text = value.toString();

    Assert.assertTrue(text.contains("name=\"field\""));
  }

  @Test
  public void toStringIncludesOwnerAndQuestionMarksWhenNameMissing() {
    DefaultNameableAndClassOwnerTrackable value = new DefaultNameableAndClassOwnerTrackable();
    value.setClassOwner(Holder.class);

    Assert.assertEquals(Holder.class.getName() + ".???", value.toString());
  }

  @Test
  public void toStringIncludesOwnerAndAssignedName() {
    DefaultNameableAndClassOwnerTrackable value = new DefaultNameableAndClassOwnerTrackable();
    value.setClassOwner(Holder.class);
    value.setName("field");

    Assert.assertEquals(Holder.class.getName() + ".field", value.toString());
  }

  @Test
  public void setNamesAndClassOwnersForPublicStaticFinalInstancesOwnedByAssignsMetadata() {
    Holder.FIRST.setName(null);
    Holder.FIRST.setClassOwner(null);
    Holder.SECOND.setName(null);
    Holder.SECOND.setClassOwner(null);

    DefaultNameableAndClassOwnerTrackable.setNamesAndClassOwnersForPublicStaticFinalInstancesOwnedBy(Holder.class);

    Assert.assertEquals("FIRST", Holder.FIRST.getName());
    Assert.assertEquals(Holder.class, Holder.FIRST.getClassOwner());
    Assert.assertEquals("SECOND", Holder.SECOND.getName());
    Assert.assertEquals(Holder.class, Holder.SECOND.getClassOwner());
  }
}
