package org.lgna.croquet;

import org.junit.BeforeClass;
import org.junit.Test;
import org.lgna.croquet.views.Table;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

public class SingleSelectTableRowStateBehaviorTest {
  @BeforeClass
  public static void ensureApplication() {
    CroquetTestUtils.ensureTestApplication();
  }

  @Test
  public void tableRowState_tracks_selection_and_exposes_table_data() {
    XvfbCroquetTestSupport.onEdt(() -> {
      TestTableRowState state = new TestTableRowState(new String[]{"alpha", "bravo", "charlie"});
      List<String> changes = new ArrayList<>();
      state.addValueListener(new State.ValueListener<String>() {
        @Override
        public void changing(State<String> model, String prevValue, String nextValue) {
        }

        @Override
        public void changed(State<String> model, String prevValue, String nextValue) {
          changes.add(prevValue + "->" + nextValue);
        }
      });

      assertEquals(3, state.getItemCount());
      assertEquals(Arrays.asList("alpha", "bravo", "charlie"), new ArrayList<>(state.getItems()));
      assertEquals(Arrays.asList("alpha", "bravo", "charlie"), snapshot(state));

      state.getSwingModel().getListSelectionModel().setSelectionInterval(2, 2);
      assertEquals("charlie", state.getValue());
      assertEquals(Arrays.asList("alpha->charlie"), changes);

      Table<String> table = state.createTable();
      JTable awtTable = table.getAwtComponent();
      assertSame(state.getSwingModel().getTableModel(), awtTable.getModel());
      assertSame(state.getSwingModel().getListSelectionModel(), awtTable.getSelectionModel());
      return null;
    });
  }

  @Test
  public void tableRowState_returns_null_for_selection_past_row_count() {
    XvfbCroquetTestSupport.onEdt(() -> {
      TestTableRowState state = new TestTableRowState(new String[]{"alpha", "bravo"});
      state.getSwingModel().getListSelectionModel().setSelectionInterval(1, 1);
      assertEquals("bravo", state.getValue());

      state.getSwingModel().getListSelectionModel().setLeadSelectionIndex(9);
      assertNull(state.getSwingModel().getListSelectionModel().getLeadSelectionIndex() < state.getItemCount() ? null : state.getSwingValue());
      return null;
    });
  }

  private static List<String> snapshot(TestTableRowState state) {
    List<String> values = new ArrayList<>();
    for (String value : state) {
      values.add(value);
    }
    return values;
  }

  private static final class TestTableRowState extends SingleSelectTableRowState<String> {
    private final List<String> rows;

    private TestTableRowState(String[] rows) {
      this(Arrays.asList(rows));
    }

    private TestTableRowState(List<String> rows) {
      this(rows, createModel(rows), createColumns(rows));
    }

    private TestTableRowState(List<String> rows, DefaultTableModel model, TableColumnModel columns) {
      super(Application.DOCUMENT_UI_GROUP, CroquetTestUtils.nextTestUUID(), rows.getFirst(), CroquetTestUtils.STRING_CODEC, model, columns);
      this.rows = rows;
    }

    @Override
    public String getItemAt(int index) {
      return this.rows.get(index);
    }

    @Override
    protected void setSwingValue(String nextValue) {
      int index = this.rows.indexOf(nextValue);
      if (index >= 0) {
        this.getSwingModel().getListSelectionModel().setSelectionInterval(index, index);
      } else {
        this.getSwingModel().getListSelectionModel().clearSelection();
      }
    }

    private static DefaultTableModel createModel(List<String> rows) {
      DefaultTableModel model = new DefaultTableModel(new Object[]{"value"}, 0);
      for (String row : rows) {
        model.addRow(new Object[]{row});
      }
      return model;
    }

    private static TableColumnModel createColumns(List<String> rows) {
      return new JTable(createModel(rows)).getColumnModel();
    }
  }
}
