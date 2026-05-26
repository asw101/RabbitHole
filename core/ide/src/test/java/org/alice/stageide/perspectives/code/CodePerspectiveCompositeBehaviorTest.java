package org.alice.stageide.perspectives.code;

import org.alice.stageide.perspectives.CodePerspective;
import org.junit.Test;
import org.lgna.croquet.Composite;

import java.lang.reflect.Field;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class CodePerspectiveCompositeBehaviorTest {
  private static int ignoreDividerChangeCount(CodePerspectiveComposite composite) throws Exception {
    Field field = CodePerspectiveComposite.class.getDeclaredField("ignoreDividerChangeCount");
    field.setAccessible(true);
    return field.getInt(composite);
  }

  @Test
  public void codePerspectiveCachesMainComposite() {
    CodePerspective perspective = new CodePerspective(null, null);

    Composite<?> first = perspective.getMainComposite();
    Composite<?> second = perspective.getMainComposite();

    assertSame(first, second);
    assertTrue(first instanceof CodePerspectiveComposite);
  }

  @Test
  public void toolbarRemainsHiddenWhenPreferenceDisablesIt() {
    CodePerspective perspective = new CodePerspective(null, null);
    perspective.getMainComposite();

    assertNull(perspective.getToolBarComposite());
  }

  @Test
  public void codePerspectiveCompositeCachesLeadingCompositeWithoutProjectFrame() {
    CodePerspectiveComposite composite = new CodePerspectiveComposite(null);

    Composite<?> first = composite.getLeadingComposite();
    Composite<?> second = composite.getLeadingComposite();

    assertNotNull(first);
    assertSame(first, second);
  }

  @Test
  public void ignoreDividerChangeCountTracksBalancedIncrementAndDecrementCalls() throws Exception {
    CodePerspectiveComposite composite = new CodePerspectiveComposite(null);

    composite.incrementIgnoreDividerLocationChangeCount();
    composite.incrementIgnoreDividerLocationChangeCount();
    composite.decrementIgnoreDividerLocationChangeCount();
    assertEquals(1, ignoreDividerChangeCount(composite));

    composite.decrementIgnoreDividerLocationChangeCount();
    assertEquals(0, ignoreDividerChangeCount(composite));
  }
}
