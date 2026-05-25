package org.lgna.croquet;

import edu.cmu.cs.dennisc.codec.ByteArrayBinaryEncoder;
import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.codecs.EnumCodec;
import org.lgna.croquet.codecs.SimpleTabCompositeCodec;
import org.lgna.croquet.data.MutableListData;
import org.lgna.croquet.edits.StateEdit;
import org.lgna.croquet.history.UserActivity;

import javax.swing.JPanel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import java.awt.BorderLayout;
import java.awt.LayoutManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CroquetHeadlessBehaviorRecoveryTest {
  private enum SampleMode {
    ALPHA, BETA, GAMMA
  }

  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void enumCodec_roundTrips_and_uses_fallback_representation() {
    EnumCodec<SampleMode> codec = EnumCodec.getInstance(SampleMode.class);
    ByteArrayBinaryEncoder encoder = new ByteArrayBinaryEncoder();
    codec.encodeValue(encoder, SampleMode.BETA);

    assertEquals(SampleMode.BETA, codec.decodeValue(encoder.createDecoder()));

    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, SampleMode.GAMMA);
    assertEquals("GAMMA", sb.toString());
  }

  @Test
  public void mutableListData_reports_add_remove_and_reorder_changes() {
    MutableListData<String> data = new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"alpha", "gamma"});
    List<String> events = new ArrayList<>();
    data.addListener(new ListDataListener() {
      @Override
      public void intervalAdded(ListDataEvent e) {
        events.add("added:" + e.getIndex0() + '-' + e.getIndex1());
      }

      @Override
      public void intervalRemoved(ListDataEvent e) {
        events.add("removed:" + e.getIndex0() + '-' + e.getIndex1());
      }

      @Override
      public void contentsChanged(ListDataEvent e) {
        events.add("changed:" + e.getIndex0() + '-' + e.getIndex1());
      }
    });

    data.internalAddItem(1, "beta");
    data.internalRemoveItem("alpha");
    data.internalSetAllItems(Arrays.asList("gamma", "beta"));

    assertEquals(Arrays.asList("gamma", "beta"), snapshot(data));
    assertEquals(Arrays.asList("changed:0-2", "changed:0-1", "changed:0-1"), events);
  }

  @Test
  public void stringState_listeners_and_edit_cycle_capture_real_value_changes() {
    XvfbCroquetTestSupport.onEdt(() -> {
      TestStringState state = new TestStringState("before");
      CroquetTestUtils.removeDocumentListeners(state);

      AtomicReference<String> oldSchool = new AtomicReference<>();
      AtomicReference<String> newSchool = new AtomicReference<>();
      state.addValueListener(new State.ValueListener<String>() {
        @Override
        public void changing(State<String> model, String prevValue, String nextValue) {
        }

        @Override
        public void changed(State<String> model, String prevValue, String nextValue) {
          oldSchool.set(prevValue + "->" + nextValue);
        }
      });
      state.addNewSchoolValueListener(event -> newSchool.set(event.getPreviousValue() + "->" + event.getNextValue()));

      state.setValueTransactionlessly("after");

      assertEquals("after", state.getValue());
      assertEquals("before->after", oldSchool.get());
      assertEquals("before->after", newSchool.get());

      UserActivity activity = new UserActivity();
      activity.setCompletionModel(state);
      StateEdit<String> edit = new StateEdit<>(activity, "after", "redo");

      edit.doOrRedo(false);
      assertEquals("redo", state.getValue());

      edit.undo();
      assertEquals("after", state.getValue());
      return null;
    });
  }

  @Test
  public void composite_activation_and_tab_selection_delegate_to_children() {
    XvfbCroquetTestSupport.onEdt(() -> {
      RecordingComposite parent = new RecordingComposite("parent");
      RecordingComposite child = new RecordingComposite("child");
      RecordingTabComposite alpha = new RecordingTabComposite("alpha");
      RecordingTabComposite beta = new RecordingTabComposite("beta");
      HeadlessTabState tabState = new HeadlessTabState(alpha, beta);
      tabState.setValueTransactionlessly(alpha);

      parent.registerChildForTest(child);
      parent.registerTabStateForTest(tabState);

      parent.handlePreActivation();
      assertEquals(1, child.preActivationCount);
      assertEquals(2, alpha.preActivationCount);
      assertEquals(0, alpha.postDeactivationCount);

      tabState.setValueTransactionlessly(beta);
      assertEquals(1, alpha.postDeactivationCount);
      assertEquals(1, beta.preActivationCount);

      parent.handlePostDeactivation();
      assertEquals(1, child.postDeactivationCount);
      assertEquals(1, beta.postDeactivationCount);
      return null;
    });
  }

  private static List<String> snapshot(MutableListData<String> data) {
    List<String> values = new ArrayList<>();
    for (String value : data) {
      values.add(value);
    }
    return values;
  }

  private static final class TestStringState extends StringState {
    private TestStringState(String initialValue) {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), initialValue);
    }

    @Override
    protected Class<? extends Element> getClassUsedForLocalization() {
      return TestStringState.class;
    }

    @Override
    protected String getSubKeyForLocalization() {
      return "headlessRecovery";
    }
  }

  private static final class HeadlessTabState extends MutableDataTabState<RecordingTabComposite> {
    private HeadlessTabState(RecordingTabComposite... values) {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), -1,
          new MutableListData<>(SimpleTabCompositeCodec.getInstance(RecordingTabComposite.class), values));
    }
  }

  private static final class RecordingComposite extends SimpleComposite<XvfbCroquetTestSupport.TestPanel> {
    private final String name;
    private int preActivationCount;
    private int postDeactivationCount;

    private RecordingComposite(String name) {
      super(CroquetTestUtils.nextTestUUID());
      this.name = name;
    }

    private void registerChildForTest(RecordingComposite child) {
      this.registerSubComposite(child);
    }

    private void registerTabStateForTest(TabState<?, ?> tabState) {
      this.registerTabState(tabState);
    }

    @Override
    protected XvfbCroquetTestSupport.TestPanel createView() {
      return new XvfbCroquetTestSupport.TestPanel(this, 160, 60);
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append(this.name);
    }

    @Override
    public void handlePreActivation() {
      this.preActivationCount++;
      super.handlePreActivation();
    }

    @Override
    public void handlePostDeactivation() {
      this.postDeactivationCount++;
      super.handlePostDeactivation();
    }
  }

  private static final class RecordingTabComposite extends SimpleTabComposite<XvfbCroquetTestSupport.TestPanel> {
    private final String name;
    private int preActivationCount;
    private int postDeactivationCount;

    private RecordingTabComposite(String name) {
      super(CroquetTestUtils.nextTestUUID(), IsCloseable.FALSE);
      this.name = name;
    }

    @Override
    protected XvfbCroquetTestSupport.TestPanel createView() {
      return new XvfbCroquetTestSupport.TestPanel(this, 180, 70);
    }

    @Override
    public void appendUserRepr(StringBuilder sb) {
      sb.append(this.name);
    }

    @Override
    public void handlePreActivation() {
      this.preActivationCount++;
      super.handlePreActivation();
    }

    @Override
    public void handlePostDeactivation() {
      this.postDeactivationCount++;
      super.handlePostDeactivation();
    }
  }
}
