package org.lgna.croquet.imp.liststate;
import org.lgna.croquet.*;
import org.lgna.croquet.data.MutableListData;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;
public class SingleSelectListStateSwingModelExtendedTest {
  private static final Group G = Group.getInstance(UUID.fromString("a5000000-0000-0000-0000-000000000001"), "lsSwingExt");
  private MutableDataSingleSelectListState<String> state;
  @Before public void setUp() {
    MutableListData<String> d=new MutableListData<>(CroquetTestUtils.STRING_CODEC, new String[]{"a","b","c"});
    state = new MutableDataSingleSelectListState<String>(G, UUID.fromString("a5000000-0000-0000-0001-000000000001"), 0, d){};
    CroquetTestUtils.removeListSelectionListeners(state);
  }
  @Test public void getSwingModel_notNull() { assertNotNull(state.getSwingModel()); }
  @Test public void getListSelectionModel_notNull() { assertNotNull(state.getSwingModel().getListSelectionModel()); }
  @Test public void getComboBoxModel_notNull() { assertNotNull(state.getSwingModel().getComboBoxModel()); }
  @Test public void comboBoxModel_size() { assertEquals(3, state.getSwingModel().getComboBoxModel().getSize()); }
  @Test public void comboBoxModel_elementAt() { assertEquals("a", state.getSwingModel().getComboBoxModel().getElementAt(0)); }
  @Test public void comboBoxModel_selectedItem() { assertEquals("a", state.getSwingModel().getComboBoxModel().getSelectedItem()); }
  @Test public void comboBoxModel_afterChange() { state.setValueTransactionlessly("b"); assertEquals("b", state.getSwingModel().getComboBoxModel().getSelectedItem()); }
}
