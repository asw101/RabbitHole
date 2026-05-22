package org.lgna.croquet;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.Timeout;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.Timeout;

public class AbstractSplitCompositeXvfbTest {

  @Rule
  public Timeout globalTimeout = Timeout.seconds(30);
  @Test
  public void containsAndLifecycleCascadeToBothSides() {
    TestSplitComposite composite = new TestSplitComposite();

    assertTrue(composite.contains(composite.leading.flag));
    assertTrue(composite.contains(composite.trailing.flag));

    composite.handlePreActivation();
    composite.handlePostDeactivation();

    assertEquals(1, composite.leading.preActivationCount);
    assertEquals(1, composite.trailing.preActivationCount);
    assertEquals(1, composite.leading.postDeactivationCount);
    assertEquals(1, composite.trailing.postDeactivationCount);
  }

  @Test
  public void releaseViewReleasesBothChildViews() {
    TestSplitComposite composite = new TestSplitComposite();
    composite.getLeadingComposite().getView();
    composite.getTrailingComposite().getView();
    composite.getView();

    composite.releaseView();

    assertNull(composite.leading.peekView());
    assertNull(composite.trailing.peekView());
  }

  private static final class TestSplitComposite extends SplitComposite {
    private final XvfbCroquetTestSupport.TestSimpleComposite leading;
    private final XvfbCroquetTestSupport.TestSimpleComposite trailing;

    private TestSplitComposite() {
      this(new XvfbCroquetTestSupport.TestSimpleComposite("leading"), new XvfbCroquetTestSupport.TestSimpleComposite("trailing"));
    }

    private TestSplitComposite(XvfbCroquetTestSupport.TestSimpleComposite leading, XvfbCroquetTestSupport.TestSimpleComposite trailing) {
      super(java.util.UUID.randomUUID(), leading, trailing);
      this.leading = leading;
      this.trailing = trailing;
    }

    @Override
    protected org.lgna.croquet.views.SplitPane createView() {
      return this.createHorizontalSplitPane();
    }
  }
}
