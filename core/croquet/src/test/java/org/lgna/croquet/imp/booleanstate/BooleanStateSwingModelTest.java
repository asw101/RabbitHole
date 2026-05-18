package org.lgna.croquet.imp.booleanstate;
import org.lgna.croquet.CroquetTestUtils;
import org.lgna.croquet.Group;
import org.lgna.croquet.TestBooleanState;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;
public class BooleanStateSwingModelTest {
  private static final Group G = Group.getInstance(UUID.fromString("b5000000-0000-0000-0000-000000000001"), "bsSwing");
  private TestBooleanState state;
  @Before public void setUp() { state = new TestBooleanState(G, false); CroquetTestUtils.removeItemListeners(state); }
  @Test public void getSwingModel_notNull() { assertNotNull(state.getImp().getSwingModel()); }
  @Test public void getButtonModel_notNull() { assertNotNull(state.getImp().getSwingModel().getButtonModel()); }
  @Test public void buttonModel_initialFalse() { assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected()); }
  @Test public void buttonModel_syncsOnChange() { state.setValueTransactionlessly(true); assertTrue(state.getImp().getSwingModel().getButtonModel().isSelected()); }
  @Test public void buttonModel_syncsBack() { state.setValueTransactionlessly(true); state.setValueTransactionlessly(false); assertFalse(state.getImp().getSwingModel().getButtonModel().isSelected()); }
  @Test public void buttonModel_enabled() { assertTrue(state.getImp().getSwingModel().getButtonModel().isEnabled()); }
  @Test public void imp_notNull() { assertNotNull(state.getImp()); }
}
