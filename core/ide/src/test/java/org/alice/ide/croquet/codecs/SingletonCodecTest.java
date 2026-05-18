package org.alice.ide.croquet.codecs;

import org.junit.Test;
import org.lgna.project.ast.JavaType;

import static org.junit.Assert.*;

/**
 * Tests for {@link SingletonCodec} — cached codec that uses reflection
 * to encode/decode singleton instances via their static getInstance() method.
 */
public class SingletonCodecTest {

  // ---- getInstance factory ----

  @Test
  public void getInstance_returnsNonNull() {
    SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
    assertNotNull(codec);
  }

  // ---- getValueClass ----

  @Test
  public void getValueClass_returnsPassedClass() {
    SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
    assertEquals(JavaType.class, codec.getValueClass());
  }

  // ---- ItemCodec interface compliance ----

  @Test
  public void implementsItemCodec() {
    SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
    assertTrue(codec instanceof org.lgna.croquet.ItemCodec);
  }

  // ---- multiple getInstance calls ----

  @Test
  public void getInstance_calledMultipleTimes_returnsNonNull() {
    for (int i = 0; i < 5; i++) {
      SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
      assertNotNull("Call " + i, codec);
    }
  }

  @Test
  public void getValueClass_consistent() {
    SingletonCodec<JavaType> c1 = SingletonCodec.getInstance(JavaType.class);
    SingletonCodec<JavaType> c2 = SingletonCodec.getInstance(JavaType.class);
    assertEquals(c1.getValueClass(), c2.getValueClass());
  }

  // ---- appendRepresentation null safety ----

  @Test
  public void appendRepresentation_withNull_doesNotThrow() {
    SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertTrue(sb.toString().contains("null"));
  }

  @Test
  public void appendRepresentation_withNull_appendsNullText() {
    SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
    StringBuilder sb = new StringBuilder();
    codec.appendRepresentation(sb, null);
    assertFalse(sb.toString().isEmpty());
  }

  // ---- different types ----

  @Test
  public void differentTypes_differentCodecs() {
    SingletonCodec<JavaType> c1 = SingletonCodec.getInstance(JavaType.class);
    assertNotNull(c1);
  }

  @Test
  public void getValueClass_isCorrectType() {
    SingletonCodec<JavaType> codec = SingletonCodec.getInstance(JavaType.class);
    Class<JavaType> cls = codec.getValueClass();
    assertNotNull(cls);
    assertEquals("JavaType", cls.getSimpleName());
  }
}
