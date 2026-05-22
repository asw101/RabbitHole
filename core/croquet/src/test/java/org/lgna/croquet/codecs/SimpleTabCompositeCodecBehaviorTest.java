package org.lgna.croquet.codecs;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.Test;
import org.lgna.croquet.Composite;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.SimpleTabComposite;
import org.lgna.croquet.views.CompositeView;

import javax.swing.JPanel;

import static org.junit.Assert.*;

public class SimpleTabCompositeCodecBehaviorTest {
  @Test
  public void appendRepresentation_initializesCompositeAndUsesUserRepr() {
    TestTab tab = new TestTab("tab-one");
    SimpleTabCompositeCodec<TestTab> codec = SimpleTabCompositeCodec.getInstance(TestTab.class);
    StringBuilder sb = new StringBuilder();

    codec.appendRepresentation(sb, tab);

    assertEquals(1, tab.initializeCount);
    assertEquals("tab-one", sb.toString());
  }

  @Test
  public void encodeValue_nonNullComposite_throwsTodoException() {
    SimpleTabCompositeCodec<TestTab> codec = SimpleTabCompositeCodec.getInstance(TestTab.class);

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> codec.encodeValue(new ByteArrayBinaryEncoder(), new TestTab("boom")));

    assertEquals("todo", exception.getMessage());
  }

  @Test
  public void decodeValue_trueMarker_throwsTodoException() {
    SimpleTabCompositeCodec<TestTab> codec = SimpleTabCompositeCodec.getInstance(TestTab.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    encoder.encode(true);

    RuntimeException exception = assertThrows(RuntimeException.class,
        () -> codec.decodeValue(encoder.createDecoder()));

    assertEquals("todo", exception.getMessage());
  }

  private static final class TestTab extends SimpleTabComposite<StubView> {
    private final String userRepr;
    private int initializeCount;

    private TestTab(String userRepr) {
      super(CroquetTestUtils.nextTestUUID(), IsCloseable.FALSE);
      this.userRepr = userRepr;
    }

    @Override
    protected void initialize() {
      this.initializeCount++;
      super.initialize();
    }

    @Override
    protected StubView createView() {
      return new StubView();
    }

    @Override
    public void appendUserRepr(StringBuilder userRepr) {
      userRepr.append(this.userRepr);
    }
  }

  private static final class StubView extends CompositeView<JPanel, Composite<?>> {
    private StubView() {
      super(null);
    }

    @Override
    protected JPanel createAwtComponent() {
      return new JPanel();
    }
  }
}
