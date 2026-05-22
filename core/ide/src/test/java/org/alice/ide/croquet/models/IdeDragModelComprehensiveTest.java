package org.alice.ide.croquet.models;

import org.junit.Test;
import org.lgna.croquet.AbstractModel;
import org.lgna.croquet.DragModel;
import org.lgna.croquet.DropSite;
import org.lgna.croquet.Triggerable;
import org.lgna.croquet.history.DragStep;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class IdeDragModelComprehensiveTest {

  @Test
  public void ideDragModel_isPublicAbstractClass() {
    assertTrue(Modifier.isPublic(IdeDragModel.class.getModifiers()));
    assertTrue(Modifier.isAbstract(IdeDragModel.class.getModifiers()));
  }

  @Test
  public void ideDragModel_extendsAbstractModel() {
    assertTrue(AbstractModel.class.isAssignableFrom(IdeDragModel.class));
  }

  @Test
  public void ideDragModel_implementsDragModel() {
    assertTrue(DragModel.class.isAssignableFrom(IdeDragModel.class));
  }

  @Test
  public void ideDragModel_hasSinglePublicConstructor() {
    Constructor<?>[] constructors = IdeDragModel.class.getConstructors();
    assertEquals(1, constructors.length);
    assertTrue(Modifier.isPublic(constructors[0].getModifiers()));
  }

  @Test
  public void ideDragModel_constructorAcceptsUuid() throws Exception {
    Constructor<IdeDragModel> constructor = IdeDragModel.class.getConstructor(UUID.class);
    assertArrayEquals(new Class<?>[] {UUID.class}, constructor.getParameterTypes());
  }

  @Test
  public void createListOfPotentialDropReceptors_isPublicFinal() throws Exception {
    Method method = IdeDragModel.class.getMethod("createListOfPotentialDropReceptors");
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isFinal(method.getModifiers()));
  }

  @Test
  public void localize_isProtectedConcreteMethod() throws Exception {
    Method method = IdeDragModel.class.getDeclaredMethod("localize");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertFalse(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void dragLifecycleMethods_arePublic() throws Exception {
    assertTrue(Modifier.isPublic(IdeDragModel.class.getMethod("handleDragStarted", DragStep.class).getModifiers()));
    assertTrue(Modifier.isPublic(IdeDragModel.class.getMethod("handleDragEnteredDropReceptor", DragStep.class).getModifiers()));
    assertTrue(Modifier.isPublic(IdeDragModel.class.getMethod("handleDragExitedDropReceptor", DragStep.class).getModifiers()));
    assertTrue(Modifier.isPublic(IdeDragModel.class.getMethod("handleDragStopped", DragStep.class).getModifiers()));
  }

  @Test
  public void testSubclass_canBeInstantiated() {
    assertNotNull(new TestIdeDragModel(UUID.randomUUID()));
  }

  @Test
  public void testSubclass_preservesMigrationId() {
    UUID id = UUID.randomUUID();
    TestIdeDragModel model = new TestIdeDragModel(id);
    assertEquals(id, model.getMigrationId());
  }

  @Test
  public void testSubclass_getDropOperationReturnsNullByStubContract() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    assertNull(model.getDropOperation(null, null));
  }

  @Test
  public void testSubclass_startsEnabled() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    assertTrue(model.isEnabled());
  }

  @Test
  public void testSubclass_canToggleEnabledState() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    model.setEnabled(false);
    assertFalse(model.isEnabled());
    model.setEnabled(true);
    assertTrue(model.isEnabled());
  }

  @Test
  public void relocalize_invokesConcreteNoOpLocalizeWithoutFailure() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    model.relocalize();
    assertEquals(1, model.localizeCalls);
  }

  @Test
  public void createListOfPotentialDropReceptorsReturnType_isList() throws Exception {
    Method method = IdeDragModel.class.getMethod("createListOfPotentialDropReceptors");
    assertEquals(List.class, method.getReturnType());
  }

  @Test
  public void ideDragModel_declaresNoFieldsOfItsOwn() {
    assertEquals(0, IdeDragModel.class.getDeclaredFields().length);
  }

  @Test
  public void ideDragModel_isTopLevelType() {
    assertNull(IdeDragModel.class.getEnclosingClass());
  }

  @Test
  public void ideDragModel_isNotFinalOrEnum() {
    assertFalse(Modifier.isFinal(IdeDragModel.class.getModifiers()));
    assertFalse(IdeDragModel.class.isEnum());
  }

  @Test
  public void ideDragModel_packageMatchesSource() {
    assertEquals("org.alice.ide.croquet.models", IdeDragModel.class.getPackage().getName());
  }

  @Test
  public void ideDragModel_simpleNameMatchesSource() {
    assertEquals("IdeDragModel", IdeDragModel.class.getSimpleName());
  }

  private static final class TestIdeDragModel extends IdeDragModel {
    private int localizeCalls;

    private TestIdeDragModel(UUID id) {
      super(id);
    }

    @Override
    protected void localize() {
      super.localize();
      localizeCalls++;
    }

    @Override
    public Triggerable getDropOperation(DragStep step, DropSite dropSite) {
      return null;
    }
  }

  @Test
  public void relocalize_canBeCalledMultipleTimes() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    model.relocalize();
    model.relocalize();
    assertEquals(2, model.localizeCalls);
  }

  @Test
  public void setEnabled_falseTwiceRemainsFalse() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    model.setEnabled(false);
    model.setEnabled(false);
    assertFalse(model.isEnabled());
  }

  @Test
  public void getDropOperationAcceptsNullArguments() {
    TestIdeDragModel model = new TestIdeDragModel(UUID.randomUUID());
    assertNull(model.getDropOperation(null, null));
  }

  @Test
  public void dragLifecycleMethodParameterTypeIsDragStep() throws Exception {
    assertEquals(DragStep.class, IdeDragModel.class.getMethod("handleDragStarted", DragStep.class).getParameterTypes()[0]);
  }

  @Test
  public void ideDragModel_declaredMethodCountRemainsStable() {
    assertEquals(7, IdeDragModel.class.getDeclaredMethods().length);
  }


  @Test
  public void ideDragModel_declaresNoTypeParameters() {
    assertEquals(0, IdeDragModel.class.getTypeParameters().length);
  }

}
