package org.alice.ide.croquet.models;

import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

public class ResponsibleModelComprehensiveTest {

  @Test
  public void responsibleModel_isPublicInterface() {
    assertTrue(Modifier.isPublic(ResponsibleModel.class.getModifiers()));
    assertTrue(Modifier.isInterface(ResponsibleModel.class.getModifiers()));
  }

  @Test
  public void responsibleModel_declaresExactlyThreeMethods() {
    assertEquals(3, ResponsibleModel.class.getDeclaredMethods().length);
  }

  @Test
  public void responsibleModel_declaresNoFields() {
    assertEquals(0, ResponsibleModel.class.getDeclaredFields().length);
  }

  @Test
  public void doOrRedoInternal_signatureMatchesSource() throws Exception {
    Method method = ResponsibleModel.class.getMethod("doOrRedoInternal", boolean.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void undoInternal_signatureMatchesSource() throws Exception {
    Method method = ResponsibleModel.class.getMethod("undoInternal");
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void appendDescription_signatureMatchesSource() throws Exception {
    Method method = ResponsibleModel.class.getMethod("appendDescription", StringBuilder.class, boolean.class);
    assertEquals(void.class, method.getReturnType());
    assertTrue(Modifier.isPublic(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
  }

  @Test
  public void responsibleModel_isNotEnumOrAnnotation() {
    assertFalse(ResponsibleModel.class.isEnum());
    assertFalse(ResponsibleModel.class.isAnnotation());
  }

  @Test
  public void responsibleModel_hasNoSuperInterfaces() {
    assertEquals(0, ResponsibleModel.class.getInterfaces().length);
  }

  @Test
  public void trackingImplementation_isAssignableToInterface() {
    assertTrue(ResponsibleModel.class.isAssignableFrom(TrackingResponsibleModel.class));
  }

  @Test
  public void doOrRedoInternal_recordsDoInvocation() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    model.doOrRedoInternal(true);
    assertEquals(1, model.doCount);
    assertTrue(model.lastDoFlag);
  }

  @Test
  public void doOrRedoInternal_recordsRedoInvocation() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    model.doOrRedoInternal(false);
    assertEquals(1, model.redoCount);
    assertFalse(model.lastDoFlag);
  }

  @Test
  public void multipleDoOrRedoCallsAccumulateCounters() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    model.doOrRedoInternal(true);
    model.doOrRedoInternal(false);
    model.doOrRedoInternal(false);
    assertEquals(1, model.doCount);
    assertEquals(2, model.redoCount);
  }

  @Test
  public void undoInternal_updatesUndoCounter() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    model.undoInternal();
    assertEquals(1, model.undoCount);
  }

  @Test
  public void appendDescription_appendsDetailedMarkerWhenRequested() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    StringBuilder builder = new StringBuilder();
    model.appendDescription(builder, true);
    assertEquals("tracked:detailed", builder.toString());
  }

  @Test
  public void appendDescription_appendsTerseMarkerWhenRequested() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    StringBuilder builder = new StringBuilder();
    model.appendDescription(builder, false);
    assertEquals("tracked:terse", builder.toString());
  }

  @Test
  public void appendDescription_canAppendToExistingBuilderContent() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    StringBuilder builder = new StringBuilder("prefix|");
    model.appendDescription(builder, false);
    assertEquals("prefix|tracked:terse", builder.toString());
  }

  @Test
  public void implementation_canBeUsedThroughInterfaceReference() {
    ResponsibleModel model = new TrackingResponsibleModel();
    StringBuilder builder = new StringBuilder();
    model.doOrRedoInternal(true);
    model.undoInternal();
    model.appendDescription(builder, true);
    assertEquals("tracked:detailed", builder.toString());
  }

  @Test
  public void methodLookupByName_findsAllThreeMethods() {
    assertNotNull(find("doOrRedoInternal"));
    assertNotNull(find("undoInternal"));
    assertNotNull(find("appendDescription"));
  }

  @Test
  public void interfaceRemainsTopLevel() {
    assertNull(ResponsibleModel.class.getEnclosingClass());
  }

  @Test
  public void implementationStateStartsAtZero() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    assertEquals(0, model.doCount);
    assertEquals(0, model.redoCount);
    assertEquals(0, model.undoCount);
  }

  @Test
  public void repeatedAppendDescriptionCallsAccumulateText() {
    TrackingResponsibleModel model = new TrackingResponsibleModel();
    StringBuilder builder = new StringBuilder();
    model.appendDescription(builder, false);
    builder.append('|');
    model.appendDescription(builder, true);
    assertEquals("tracked:terse|tracked:detailed", builder.toString());
  }

  private static Method find(String name) {
    for (Method method : ResponsibleModel.class.getDeclaredMethods()) {
      if (method.getName().equals(name)) {
        return method;
      }
    }
    return null;
  }

  private static final class TrackingResponsibleModel implements ResponsibleModel {
    private int doCount;
    private int redoCount;
    private int undoCount;
    private boolean lastDoFlag;

    @Override
    public void doOrRedoInternal(boolean isDo) {
      lastDoFlag = isDo;
      if (isDo) {
        doCount++;
      } else {
        redoCount++;
      }
    }

    @Override
    public void undoInternal() {
      undoCount++;
    }

    @Override
    public void appendDescription(StringBuilder rv, boolean isDetailed) {
      rv.append(isDetailed ? "tracked:detailed" : "tracked:terse");
    }
  }
}
