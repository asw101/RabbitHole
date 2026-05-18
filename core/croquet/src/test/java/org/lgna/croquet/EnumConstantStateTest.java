package org.lgna.croquet;
import org.junit.Before;
import org.junit.Test;
import java.util.UUID;
import static org.junit.Assert.*;
public class EnumConstantStateTest {
  private enum Color { RED, GREEN, BLUE }
  private enum Size { SMALL, MEDIUM, LARGE, XLARGE }
  private static final Group G = Group.getInstance(UUID.fromString("e0000000-0000-0000-0000-000000000001"), "enumTest");
  private EnumConstantState<Color> state;
  @Before public void setUp() { state = new EnumConstantState<>(G, UUID.fromString("e0000000-0000-0000-0001-000000000001"), 0, Color.class); CroquetTestUtils.removeListSelectionListeners(state); }
  @Test public void initialIndex_zero() { assertEquals(Color.RED, state.getValue()); }
  @Test public void initialIndex_one() { EnumConstantState<Color> s=new EnumConstantState<>(G, UUID.fromString("e0000000-0000-0000-0001-000000000002"), 1, Color.class); CroquetTestUtils.removeListSelectionListeners(s); assertEquals(Color.GREEN, s.getValue()); }
  @Test public void getItemCount() { assertEquals(3, state.getItemCount()); }
  @Test public void getItemAt() { assertEquals(Color.RED, state.getItemAt(0)); assertEquals(Color.BLUE, state.getItemAt(2)); }
  @Test public void setValue() { state.setValueTransactionlessly(Color.BLUE); assertEquals(Color.BLUE, state.getValue()); }
  @Test public void fourConstantEnum() { EnumConstantState<Size> s=new EnumConstantState<>(G, UUID.fromString("e0000000-0000-0000-0001-000000000004"), 0, Size.class); CroquetTestUtils.removeListSelectionListeners(s); assertEquals(4, s.getItemCount()); }
  @Test public void extendsImmutableData() { assertTrue(state instanceof ImmutableDataSingleSelectListState); }
  @Test public void isEnabled() { assertTrue(state.isEnabled()); }
  @Test public void setEnabled_false() { state.setEnabled(false); assertFalse(state.isEnabled()); }
  @Test public void getItemCodec() { assertEquals(Color.class, state.getItemCodec().getValueClass()); }
  @Test public void cycleAll() { for(Color c:Color.values()){state.setValueTransactionlessly(c);assertEquals(c,state.getValue());} }
}
