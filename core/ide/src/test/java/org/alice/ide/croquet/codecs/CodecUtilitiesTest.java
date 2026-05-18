package org.alice.ide.croquet.codecs;

import org.junit.Test;
import org.lgna.project.ast.Node;
import org.lgna.project.ast.UserMethod;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.BlockStatement;
import org.lgna.project.ast.IntegerLiteral;
import org.lgna.project.ast.JavaType;

import java.util.Locale;

import static org.junit.Assert.*;

public class CodecUtilitiesTest {

  // --- StringCodec ---

  @Test
  public void stringCodecGetValueClass() {
    assertEquals(String.class, StringCodec.SINGLETON.getValueClass());
  }

  @Test
  public void stringCodecAppendRepresentation() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, "hello");
    assertEquals("hello", sb.toString());
  }

  @Test
  public void stringCodecAppendRepresentationNull() {
    StringBuilder sb = new StringBuilder();
    StringCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  // --- LocaleCodec ---

  @Test
  public void localeCodecGetValueClass() {
    assertEquals(Locale.class, LocaleCodec.SINGLETON.getValueClass());
  }

  @Test
  public void localeCodecAppendRepresentationNonNull() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.US);
    String result = sb.toString();
    assertFalse(result.isEmpty());
  }

  @Test
  public void localeCodecAppendRepresentationNull() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void localeCodecAppendRepresentationFrench() {
    StringBuilder sb = new StringBuilder();
    LocaleCodec.SINGLETON.appendRepresentation(sb, Locale.FRANCE);
    assertFalse(sb.toString().isEmpty());
  }

  // --- NodeCodec ---

  @Test
  public void nodeCodecGetInstance() {
    NodeCodec<UserMethod> codec = NodeCodec.getInstance(UserMethod.class);
    assertNotNull(codec);
    assertEquals(UserMethod.class, codec.getValueClass());
  }

  @Test
  public void nodeCodecGetInstanceSameClass() {
    NodeCodec<UserField> codec1 = NodeCodec.getInstance(UserField.class);
    NodeCodec<UserField> codec2 = NodeCodec.getInstance(UserField.class);
    assertNotNull(codec1);
    assertNotNull(codec2);
  }

  @Test
  public void nodeCodecAppendRepresentation() {
    NodeCodec<IntegerLiteral> codec = NodeCodec.getInstance(IntegerLiteral.class);
    StringBuilder sb = new StringBuilder();
    IntegerLiteral literal = new IntegerLiteral(42);
    codec.appendRepresentation(sb, literal);
    assertNotNull(sb.toString());
  }

  @Test
  public void nodeCodecAppendRepresentationNull() {
    NodeCodec<BlockStatement> codec = NodeCodec.getInstance(BlockStatement.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertNotNull(sb.toString());
  }

  @Test
  public void nodeCodecAddAndRemoveFromGlobalMap() {
    IntegerLiteral node = new IntegerLiteral(100);
    NodeCodec.addNodeToGlobalMap(node);
    NodeCodec.removeNodeFromGlobalMap(node);
  }

  // --- SingletonCodec ---

  @Test
  public void singletonCodecGetInstance() {
    SingletonCodec<StringCodec> codec = SingletonCodec.getInstance(StringCodec.class);
    assertNotNull(codec);
    assertEquals(StringCodec.class, codec.getValueClass());
  }

  @Test
  public void singletonCodecAppendRepresentation() {
    SingletonCodec<StringCodec> codec = SingletonCodec.getInstance(StringCodec.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, StringCodec.SINGLETON);
    assertFalse(sb.toString().isEmpty());
  }

  @Test
  public void singletonCodecAppendRepresentationNull() {
    SingletonCodec<StringCodec> codec = SingletonCodec.getInstance(StringCodec.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }
}
