package org.lgna.croquet;
import org.junit.Test;
import static org.junit.Assert.*;
public class CroquetHierarchyTest {
  @Test public void abstractElement_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(AbstractElement.class.getModifiers())); }
  @Test public void abstractCompletionModel_extendsAbstractElement() { assertTrue(AbstractElement.class.isAssignableFrom(AbstractCompletionModel.class)); }
  @Test public void operation_implementsCompletionModel() { assertTrue(CompletionModel.class.isAssignableFrom(Operation.class)); }
  @Test public void actionOperation_extendsOperation() { assertTrue(Operation.class.isAssignableFrom(ActionOperation.class)); }
  @Test public void iteratingOperation_extendsOperation() { assertTrue(Operation.class.isAssignableFrom(IteratingOperation.class)); }
  @Test public void singleThreadIteratingOp_extendsIteratingOp() { assertTrue(IteratingOperation.class.isAssignableFrom(SingleThreadIteratingOperation.class)); }
  @Test public void state_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(State.class.getModifiers())); }
  @Test public void booleanState_extendsState() { assertTrue(State.class.isAssignableFrom(BooleanState.class)); }
  @Test public void stringState_extendsState() { assertTrue(State.class.isAssignableFrom(StringState.class)); }
  @Test public void boundedNumberState_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(BoundedNumberState.class.getModifiers())); }
  @Test public void boundedIntegerState_extendsBoundedNumberState() { assertTrue(BoundedNumberState.class.isAssignableFrom(BoundedIntegerState.class)); }
  @Test public void boundedDoubleState_extendsBoundedNumberState() { assertTrue(BoundedNumberState.class.isAssignableFrom(BoundedDoubleState.class)); }
  @Test public void singleSelectListState_extendsState() { assertTrue(State.class.isAssignableFrom(SingleSelectListState.class)); }
  @Test public void multipleSelectionListState_isAbstract() { assertTrue(java.lang.reflect.Modifier.isAbstract(MultipleSelectionListState.class.getModifiers())); }
  @Test public void enumConstantState_extendsImmutableData() { assertTrue(ImmutableDataSingleSelectListState.class.isAssignableFrom(EnumConstantState.class)); }
  @Test public void mutableDataSingleSelectListState_extendsSingleSelect() { assertTrue(SingleSelectListState.class.isAssignableFrom(MutableDataSingleSelectListState.class)); }
  @Test public void composite_isInterface() { assertTrue(Composite.class.isInterface()); }
  @Test public void model_isInterface() { assertTrue(Model.class.isInterface()); }
  @Test public void completionModel_extendsModel() { assertTrue(Model.class.isAssignableFrom(CompletionModel.class)); }
  @Test public void edit_isInterface() { assertTrue(org.lgna.croquet.edits.Edit.class.isInterface()); }
}
