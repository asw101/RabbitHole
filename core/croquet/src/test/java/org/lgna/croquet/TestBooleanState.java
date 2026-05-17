package org.lgna.croquet;

import java.util.UUID;

/**
 * Minimal concrete {@link BooleanState} subclass for headless testing.
 * Shared across BooleanStateTest, BooleanStateImpTest, and other tests
 * that need a constructable BooleanState without Application context.
 */
public class TestBooleanState extends BooleanState {

  public TestBooleanState(Group group, boolean initialValue) {
    super(group, UUID.randomUUID(), initialValue);
  }

  @Override
  protected Class<? extends Element> getClassUsedForLocalization() {
    return TestBooleanState.class;
  }

  @Override
  protected String getSubKeyForLocalization() {
    return "test";
  }
}
