package org.alice.ide.croquet.models.print;

import org.alice.ide.operations.InconsequentialActionOperation;
import org.junit.Test;

import java.awt.print.Printable;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class PrintOperationsStructureTest {

  @Test
  public void printPdfOperation_whenReflected_isAbstractPackagePrivate() {
    assertFalse(Modifier.isPublic(PrintPdfOperation.class.getModifiers()));
    assertTrue(Modifier.isAbstract(PrintPdfOperation.class.getModifiers()));
  }

  @Test
  public void printPdfOperation_whenReflected_extendsInconsequentialActionOperation() {
    assertEquals(InconsequentialActionOperation.class, PrintPdfOperation.class.getSuperclass());
  }

  @Test
  public void printPdfOperation_constructor_whenReflected_acceptsUuid() throws Exception {
    Constructor<PrintPdfOperation> constructor = PrintPdfOperation.class.getDeclaredConstructor(UUID.class);
    assertFalse(Modifier.isPublic(constructor.getModifiers()));
    assertEquals(1, constructor.getParameterCount());
  }

  @Test
  public void printPdfOperation_performInternal_whenReflected_isProtectedOverride() throws Exception {
    Method method = PrintPdfOperation.class.getDeclaredMethod("performInternal");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void printPdfOperation_notifyWrapAndRethrow_whenReflected_isPrivateHelper() throws Exception {
    Method method = PrintPdfOperation.class.getDeclaredMethod("notifyWrapAndRethrow", Exception.class, String.class);
    assertTrue(Modifier.isPrivate(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void printPdfOperation_notifyUserOfProblem_whenReflected_acceptsMessage() throws Exception {
    Method method = PrintPdfOperation.class.getDeclaredMethod("notifyUserOfProblem", String.class);
    assertFalse(Modifier.isPrivate(method.getModifiers()));
    assertEquals(void.class, method.getReturnType());
  }

  @Test
  public void printPdfOperation_getHtmlToPrint_whenReflected_isProtectedAbstract() throws Exception {
    Method method = PrintPdfOperation.class.getDeclaredMethod("getHtmlToPrint");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertTrue(Modifier.isAbstract(method.getModifiers()));
    assertEquals(ByteArrayOutputStream.class, method.getReturnType());
    assertEquals(IOException.class, method.getExceptionTypes()[0]);
  }

  @Test
  public void printPdfOperation_privateHelpers_whenReflected_exist() throws Exception {
    assertEquals(ByteArrayOutputStream.class,
        PrintPdfOperation.class.getDeclaredMethod("convertToPdf", ByteArrayOutputStream.class).getReturnType());
    assertEquals(void.class,
        PrintPdfOperation.class.getDeclaredMethod("printPdf", ByteArrayOutputStream.class).getReturnType());
  }

  @Test
  public void printSceneEditorOperation_whenReflected_isPublicConcreteBaseOperation() {
    assertTrue(Modifier.isPublic(PrintSceneEditorOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(PrintSceneEditorOperation.class.getModifiers()));
    assertEquals(InconsequentialActionOperation.class, PrintSceneEditorOperation.class.getSuperclass());
  }

  @Test
  public void printSceneEditorOperation_members_whenReflected_matchExpectedShape() throws Exception {
    Constructor<PrintSceneEditorOperation> constructor = PrintSceneEditorOperation.class.getDeclaredConstructor();
    assertTrue(Modifier.isPublic(constructor.getModifiers()));

    Method performInternal = PrintSceneEditorOperation.class.getDeclaredMethod("performInternal");
    assertTrue(Modifier.isProtected(performInternal.getModifiers()));
    assertTrue(Modifier.isFinal(performInternal.getModifiers()));

    Method getPrintable = PrintSceneEditorOperation.class.getDeclaredMethod("getPrintable");
    assertTrue(Modifier.isPrivate(getPrintable.getModifiers()));
    assertEquals(Printable.class, getPrintable.getReturnType());
  }

  @Test
  public void printAllOperation_whenReflected_isPublicConcretePrintPdfSubclass() {
    assertTrue(Modifier.isPublic(PrintAllOperation.class.getModifiers()));
    assertFalse(Modifier.isAbstract(PrintAllOperation.class.getModifiers()));
    assertEquals(PrintPdfOperation.class, PrintAllOperation.class.getSuperclass());
  }

  @Test
  public void printAllOperation_members_whenReflected_matchExpectedShape() throws Exception {
    Constructor<PrintAllOperation> constructor = PrintAllOperation.class.getDeclaredConstructor();
    assertTrue(Modifier.isPublic(constructor.getModifiers()));

    Method method = PrintAllOperation.class.getDeclaredMethod("getHtmlToPrint");
    assertTrue(Modifier.isProtected(method.getModifiers()));
    assertEquals(ByteArrayOutputStream.class, method.getReturnType());
    assertEquals(IOException.class, method.getExceptionTypes()[0]);
  }

  @Test
  public void printOperations_whenLoaded_areAccessible() {
    assertNotNull(PrintSceneEditorOperation.class);
    assertNotNull(PrintAllOperation.class);
    assertNotNull(PrintPdfOperation.class);
  }

  @Test
  public void printPdfOperation_declaredMethodCount_whenReflected_coversCoreWorkflow() {
    assertTrue(PrintPdfOperation.class.getDeclaredMethods().length >= 5);
  }

  @Test
  public void printAllOperation_getHtmlToPrint_whenReflected_isDeclaredBySubclass() throws Exception {
    Method method = PrintAllOperation.class.getDeclaredMethod("getHtmlToPrint");
    assertEquals(PrintAllOperation.class, method.getDeclaringClass());
  }
}
