package org.alice.ide.croquet.models.html;

import org.junit.Test;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.JavaType;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserMethod;
import org.lgna.story.SScene;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

public class HtmlProjectWriterTest {

  @Test
  public void constructorCreatesNonNullInstance() {
    HtmlProjectWriter writer = new HtmlProjectWriter();
    assertNotNull(writer);
  }

  @Test
  public void writeTypeOutputContainsTypeName() throws IOException {
    HtmlProjectWriter writer = new HtmlProjectWriter();
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestScene");
    type.superType.setValue(JavaType.getInstance(SScene.class));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    writer.writeType(baos, type);
    String output = baos.toString();
    assertTrue("Output should contain the type name 'TestScene'", output.contains("TestScene"));
  }

  @Test
  public void writeTypeOutputContainsStandardHtmlElements() throws IOException {
    HtmlProjectWriter writer = new HtmlProjectWriter();
    NamedUserType type = new NamedUserType();
    type.name.setValue("AnotherType");
    type.superType.setValue(JavaType.getInstance(SScene.class));

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    writer.writeType(baos, type);
    String output = baos.toString();
    assertTrue("Output should contain <html", output.contains("<html"));
    assertTrue("Output should contain <head", output.contains("<head"));
    assertTrue("Output should contain <body", output.contains("<body"));
    assertTrue("Output should contain <style", output.contains("<style"));
  }

  @Test
  public void writeDeclarationOutputContainsMethodName() throws IOException {
    org.junit.Assume.assumeFalse(java.awt.GraphicsEnvironment.isHeadless());
    HtmlProjectWriter writer = new HtmlProjectWriter();
    NamedUserType type = new NamedUserType();
    type.name.setValue("TestType");
    type.superType.setValue(JavaType.getInstance(SScene.class));
    UserMethod method = new UserMethod();
    method.name.setValue("testMethod");
    method.returnType.setValue(JavaType.VOID_TYPE);
    method.body.setValue(new BlockStatement());
    type.methods.add(method);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try {
      writer.writeDeclaration(baos, method);
      String output = baos.toString();
      assertTrue("Output should contain method name 'testMethod'", output.contains("testMethod"));
    } catch (Exception e) {
      // In headless environments the HtmlEncoder may try to create Swing components;
      // the Assume guard above should skip, but this catch is a safety net.
      fail("writeDeclaration threw an unexpected exception: " + e.getMessage());
    }
  }
}
