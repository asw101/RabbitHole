package org.alice.stageide.modelresource;

import org.junit.Test;
import org.lgna.croquet.ItemCodec;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.UUID;

import static org.junit.Assert.*;

public class ResourceNodeCodecTest {

  private ResourceNode createNode(String name) {
    RootResourceKey key = new RootResourceKey("key_" + name, name);
    return new ResourceNode(UUID.randomUUID(), key);
  }

  private void assertEncodeThrowsAssertionError(ResourceNode value) {
    try {
      ResourceNodeCodec.SINGLETON.encodeValue(null, value);
      fail("Expected AssertionError");
    } catch (AssertionError expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void singleton_enumConstant_exists() {
    assertNotNull(ResourceNodeCodec.SINGLETON);
  }

  @Test
  public void values_onlySingletonConstant_exists() {
    assertArrayEquals(new ResourceNodeCodec[] {ResourceNodeCodec.SINGLETON}, ResourceNodeCodec.values());
  }

  @Test
  public void valueOf_singletonName_returnsSingletonInstance() {
    assertSame(ResourceNodeCodec.SINGLETON, ResourceNodeCodec.valueOf("SINGLETON"));
  }

  @Test
  public void class_implementsItemCodec() {
    assertTrue(ItemCodec.class.isAssignableFrom(ResourceNodeCodec.class));
  }

  @Test
  public void class_publicEnum_hasExpectedModifiers() {
    int modifiers = ResourceNodeCodec.class.getModifiers();
    assertTrue(Modifier.isPublic(modifiers));
    assertTrue(ResourceNodeCodec.class.isEnum());
  }

  @Test
  public void getValueClass_singleton_returnsResourceNodeClass() {
    assertEquals(ResourceNode.class, ResourceNodeCodec.SINGLETON.getValueClass());
  }

  @Test
  public void encodeValue_nullArguments_throwsAssertionError() {
    assertEncodeThrowsAssertionError(null);
  }

  @Test
  public void encodeValue_nonNullValue_throwsAssertionError() {
    assertEncodeThrowsAssertionError(createNode("CodecNode"));
  }

  @Test
  public void decodeValue_nullDecoder_throwsAssertionError() {
    try {
      ResourceNodeCodec.SINGLETON.decodeValue(null);
      fail("Expected AssertionError");
    } catch (AssertionError expected) {
      assertNotNull(expected);
    }
  }

  @Test
  public void appendRepresentation_nullValue_appendsNullLiteral() {
    StringBuilder sb = new StringBuilder();
    ResourceNodeCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("null", sb.toString());
  }

  @Test
  public void appendRepresentation_nonNullNode_appendsLocalizedCreationText() {
    StringBuilder sb = new StringBuilder();
    ResourceNode node = createNode("My Node");
    ResourceNodeCodec.SINGLETON.appendRepresentation(sb, node);
    assertEquals("My Node", sb.toString());
  }

  @Test
  public void appendRepresentation_existingText_preservesPrefix() {
    StringBuilder sb = new StringBuilder("prefix:");
    ResourceNodeCodec.SINGLETON.appendRepresentation(sb, createNode("CodecNode"));
    assertEquals("prefix:CodecNode", sb.toString());
  }

  @Test
  public void appendRepresentation_multipleCalls_accumulatesOutput() {
    StringBuilder sb = new StringBuilder();
    ResourceNodeCodec.SINGLETON.appendRepresentation(sb, createNode("First"));
    ResourceNodeCodec.SINGLETON.appendRepresentation(sb, null);
    assertEquals("Firstnull", sb.toString());
  }

  @Test
  public void getValueClass_methodSignature_matchesExpectedContract() throws Exception {
    Method method = ResourceNodeCodec.class.getMethod("getValueClass");
    assertEquals(Class.class, method.getReturnType());
    assertEquals(0, method.getParameterTypes().length);
  }

  @Test
  public void appendRepresentation_methodSignature_matchesExpectedContract() throws Exception {
    Method method = ResourceNodeCodec.class.getMethod("appendRepresentation", StringBuilder.class, ResourceNode.class);
    assertEquals(void.class, method.getReturnType());
    assertEquals(2, method.getParameterTypes().length);
  }

  @Test
  public void singleton_name_matchesEnumConstantName() {
    assertEquals("SINGLETON", ResourceNodeCodec.SINGLETON.name());
  }
}
