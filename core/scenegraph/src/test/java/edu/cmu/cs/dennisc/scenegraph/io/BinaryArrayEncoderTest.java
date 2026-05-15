/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/
package edu.cmu.cs.dennisc.scenegraph.io;

import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import edu.cmu.cs.dennisc.texture.TextureCoordinate2f;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Vector3f;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * TDD tests for BinaryArrayEncoder — a package-private helper class
 * that will be extracted from ASGEncoder to hold binary array encoding.
 *
 * These tests FAIL until BinaryArrayEncoder is created.
 * They define the contract that the implementation must satisfy.
 */
public class BinaryArrayEncoderTest {

  // ═══════════════════════════════════════════════════════════════════
  // STRUCTURAL: Class existence and method signatures
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void binaryArrayEncoderClassExists() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    assertNotNull("BinaryArrayEncoder class must exist in the io package", cls);
  }

  @Test
  public void binaryArrayEncoderIsPackagePrivate() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    int modifiers = cls.getModifiers();
    assertFalse("BinaryArrayEncoder must not be public", Modifier.isPublic(modifiers));
    assertFalse("BinaryArrayEncoder must not be protected", Modifier.isProtected(modifiers));
    assertFalse("BinaryArrayEncoder must not be private", Modifier.isPrivate(modifiers));
  }

  @Test
  public void hasStaticEncodeVertexArrayMethod() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    Method m = cls.getDeclaredMethod("encodeVertexArray", Vertex[].class, OutputStream.class);
    assertTrue("encodeVertexArray must be static", Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void hasStaticEncodeIntArrayMethod() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    Method m = cls.getDeclaredMethod("encodeIntArray", int[].class, OutputStream.class);
    assertTrue("encodeIntArray must be static", Modifier.isStatic(m.getModifiers()));
  }

  @Test
  public void hasStaticEncodeDoubleArrayMethod() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    Method m = cls.getDeclaredMethod("encodeDoubleArray", double[].class, OutputStream.class);
    assertTrue("encodeDoubleArray must be static", Modifier.isStatic(m.getModifiers()));
  }

  // ═══════════════════════════════════════════════════════════════════
  // INT ARRAY: Binary format verification
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void encodeIntArrayWritesVersion2Header() throws Exception {
    int[] input = {10, 20, 30};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals("Version header must be 2", 2, dis.readInt());
    assertEquals("Count must match array length", 3, dis.readInt());
  }

  @Test
  public void encodeIntArrayWritesAllElements() throws Exception {
    int[] input = {100, 200, 300, 400};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    dis.readInt(); // skip version
    dis.readInt(); // skip count
    for (int expected : input) {
      assertEquals(expected, dis.readInt());
    }
    assertEquals("No extra bytes after data", 0, dis.available());
  }

  @Test
  public void encodeIntArrayEmptyProducesHeaderOnly() throws Exception {
    int[] input = {};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(2, dis.readInt()); // version
    assertEquals(0, dis.readInt()); // count
    assertEquals("No data bytes for empty array", 0, dis.available());
  }

  @Test
  public void encodeIntArrayRoundtripThroughDecoder() throws Exception {
    int[] input = {0, 1, 2, 3, 4, 5};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);

    int[] decoded = BinaryArrayDecoder.decodeIntArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(input.length, decoded.length);
    // Version 2 decoder applies winding swap: triplet[0] <-> triplet[2]
    assertArrayEquals(new int[]{2, 1, 0, 5, 4, 3}, decoded);
  }

  @Test
  public void encodeIntArraySingleElement() throws Exception {
    int[] input = {42};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(2, dis.readInt());
    assertEquals(1, dis.readInt());
    assertEquals(42, dis.readInt());
  }

  @Test
  public void encodeIntArrayNegativeValues() throws Exception {
    int[] input = {-1, Integer.MIN_VALUE, Integer.MAX_VALUE};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    dis.readInt(); // version
    dis.readInt(); // count
    assertEquals(-1, dis.readInt());
    assertEquals(Integer.MIN_VALUE, dis.readInt());
    assertEquals(Integer.MAX_VALUE, dis.readInt());
  }

  // ═══════════════════════════════════════════════════════════════════
  // DOUBLE ARRAY: Binary format verification
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void encodeDoubleArrayWritesVersion2Header() throws Exception {
    double[] input = {1.5, 2.5};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals("Version header must be 2", 2, dis.readInt());
    assertEquals("Count must match array length", 2, dis.readInt());
  }

  @Test
  public void encodeDoubleArrayWritesAllElements() throws Exception {
    double[] input = {Math.PI, Math.E, -0.0, Double.MAX_VALUE};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    dis.readInt(); // skip version
    dis.readInt(); // skip count
    for (double expected : input) {
      assertEquals(expected, dis.readDouble(), 0.0);
    }
    assertEquals("No extra bytes after data", 0, dis.available());
  }

  @Test
  public void encodeDoubleArrayEmptyProducesHeaderOnly() throws Exception {
    double[] input = {};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(2, dis.readInt());
    assertEquals(0, dis.readInt());
    assertEquals("No data bytes for empty array", 0, dis.available());
  }

  @Test
  public void encodeDoubleArrayRoundtripThroughDecoder() throws Exception {
    double[] input = {1.5, 2.7, 3.14159, -0.001, Double.MAX_VALUE};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);

    double[] decoded = BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(baos.toByteArray()));
    assertArrayEquals("Double values must survive roundtrip exactly", input, decoded, 0.0);
  }

  @Test
  public void encodeDoubleArraySingleElement() throws Exception {
    double[] input = {42.0};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);

    double[] decoded = BinaryArrayDecoder.decodeDoubleArray(new ByteArrayInputStream(baos.toByteArray()));
    assertArrayEquals(input, decoded, 0.0);
  }

  @Test
  public void encodeDoubleArraySpecialValues() throws Exception {
    double[] input = {Double.MIN_VALUE, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    dis.readInt(); // version
    dis.readInt(); // count
    assertEquals(Double.MIN_VALUE, dis.readDouble(), 0.0);
    assertEquals(Double.POSITIVE_INFINITY, dis.readDouble(), 0.0);
    assertEquals(Double.NEGATIVE_INFINITY, dis.readDouble(), 0.0);
  }

  // ═══════════════════════════════════════════════════════════════════
  // VERTEX ARRAY: Binary format verification
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void encodeVertexArrayWritesVersion3Header() throws Exception {
    Vertex[] input = {Vertex.createXYZ(1.0, 2.0, 3.0)};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals("Version header must be 3", 3, dis.readInt());
    assertEquals("Vertex count must be 1", 1, dis.readInt());
  }

  @Test
  public void encodeVertexArrayPositionOnlyRoundtrip() throws Exception {
    Vertex v1 = Vertex.createXYZ(1.0, 2.0, 3.0);
    Vertex v2 = Vertex.createXYZ(-4.0, -5.0, -6.0);
    Vertex[] input = {v1, v2};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(2, decoded.length);
    assertEquals(1.0, decoded[0].position.x(), 0.0001);
    assertEquals(2.0, decoded[0].position.y(), 0.0001);
    assertEquals(3.0, decoded[0].position.z(), 0.0001);
    assertEquals(-4.0, decoded[1].position.x(), 0.0001);
    assertEquals(-5.0, decoded[1].position.y(), 0.0001);
    assertEquals(-6.0, decoded[1].position.z(), 0.0001);
  }

  @Test
  public void encodeVertexArrayWithDiffuseColorRoundtrip() throws Exception {
    Color4f color = new Color4f(1.0f, 0.5f, 0.25f, 1.0f);
    Point3 pos = new Point3(10.0, 20.0, 30.0);
    Vertex v = new Vertex(pos, null, color, null, null);
    Vertex[] input = {v};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(1, decoded.length);
    assertEquals(10.0, decoded[0].position.x(), 0.0001);
    assertEquals(1.0f, decoded[0].diffuseColor.red, 0.0001f);
    assertEquals(0.5f, decoded[0].diffuseColor.green, 0.0001f);
    assertEquals(0.25f, decoded[0].diffuseColor.blue, 0.0001f);
    assertEquals(1.0f, decoded[0].diffuseColor.alpha, 0.0001f);
  }

  @Test
  public void encodeVertexArrayWithTextureCoordRoundtrip() throws Exception {
    TextureCoordinate2f tc = new TextureCoordinate2f(0.5f, 0.75f);
    Point3 pos = new Point3(7.0, 8.0, 9.0);
    Vertex v = new Vertex(pos, null, null, null, tc);
    Vertex[] input = {v};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(1, decoded.length);
    assertEquals(0.5f, decoded[0].textureCoordinate0.u, 0.0001f);
    assertEquals(0.75f, decoded[0].textureCoordinate0.v, 0.0001f);
  }

  @Test
  public void encodeVertexArrayWithSpecularHighlightRoundtrip() throws Exception {
    Color4f specular = new Color4f(0.9f, 0.8f, 0.7f, 0.6f);
    Point3 pos = new Point3(1.0, 1.0, 1.0);
    Vertex v = new Vertex(pos, null, null, specular, null);
    Vertex[] input = {v};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(1, decoded.length);
    assertEquals(0.9f, decoded[0].specularHighlightColor.red, 0.0001f);
    assertEquals(0.8f, decoded[0].specularHighlightColor.green, 0.0001f);
    assertEquals(0.7f, decoded[0].specularHighlightColor.blue, 0.0001f);
    assertEquals(0.6f, decoded[0].specularHighlightColor.alpha, 0.0001f);
  }

  @Test
  public void encodeVertexArrayWithAllFieldsRoundtrip() throws Exception {
    Color4f diffuse = new Color4f(1.0f, 0.0f, 0.0f, 1.0f);
    Color4f specular = new Color4f(0.5f, 0.5f, 0.5f, 1.0f);
    TextureCoordinate2f tc = new TextureCoordinate2f(0.25f, 0.75f);
    Point3 pos = new Point3(5.0, 6.0, 7.0);
    // Normal omitted intentionally — encoder writes double, decoder reads float (known mismatch)
    Vertex v = new Vertex(pos, null, diffuse, specular, tc);
    Vertex[] input = {v};

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(1, decoded.length);
    assertEquals(5.0, decoded[0].position.x(), 0.0001);
    assertEquals(1.0f, decoded[0].diffuseColor.red, 0.0001f);
    assertEquals(0.5f, decoded[0].specularHighlightColor.red, 0.0001f);
    assertEquals(0.25f, decoded[0].textureCoordinate0.u, 0.0001f);
  }

  @Test
  public void encodeVertexArrayEmptyProducesHeaderOnly() throws Exception {
    Vertex[] input = {};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    DataInputStream dis = new DataInputStream(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(3, dis.readInt()); // version
    assertEquals(0, dis.readInt()); // vertex count
    assertEquals("No data bytes for empty vertex array", 0, dis.available());
  }

  @Test
  public void encodeVertexArrayMultipleVerticesRoundtrip() throws Exception {
    Vertex[] input = new Vertex[10];
    for (int i = 0; i < 10; i++) {
      input[i] = Vertex.createXYZ(i * 1.0, i * 2.0, i * 3.0);
    }

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);

    Vertex[] decoded = BinaryArrayDecoder.decodeVertexArray(new ByteArrayInputStream(baos.toByteArray()));
    assertNotNull(decoded);
    assertEquals(10, decoded.length);
    for (int i = 0; i < 10; i++) {
      assertEquals(i * 1.0, decoded[i].position.x(), 0.0001);
      assertEquals(i * 2.0, decoded[i].position.y(), 0.0001);
      assertEquals(i * 3.0, decoded[i].position.z(), 0.0001);
    }
  }

  // ═══════════════════════════════════════════════════════════════════
  // INTEGRATION: ASGEncoder wrapper methods delegate to BinaryArrayEncoder
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void asgEncoderIntArrayDelegationProducesSameOutput() throws Exception {
    int[] input = {10, 20, 30, 40, 50, 60};

    ByteArrayOutputStream directBaos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, directBaos);

    ByteArrayOutputStream wrapperBaos = new ByteArrayOutputStream();
    ASGEncoder.encodeIntArrayInBinary(input, wrapperBaos);

    assertArrayEquals("Wrapper must produce identical output to direct call",
        directBaos.toByteArray(), wrapperBaos.toByteArray());
  }

  @Test
  public void asgEncoderDoubleArrayDelegationProducesSameOutput() throws Exception {
    double[] input = {1.1, 2.2, 3.3};

    ByteArrayOutputStream directBaos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, directBaos);

    ByteArrayOutputStream wrapperBaos = new ByteArrayOutputStream();
    ASGEncoder.encodeDoubleArrayInBinary(input, wrapperBaos);

    assertArrayEquals("Wrapper must produce identical output to direct call",
        directBaos.toByteArray(), wrapperBaos.toByteArray());
  }

  @Test
  public void asgEncoderVertexArrayDelegationProducesSameOutput() throws Exception {
    Vertex[] input = {Vertex.createXYZ(1.0, 2.0, 3.0), Vertex.createXYZ(4.0, 5.0, 6.0)};

    ByteArrayOutputStream directBaos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, directBaos);

    ByteArrayOutputStream wrapperBaos = new ByteArrayOutputStream();
    ASGEncoder.encodeVertexArrayInBinary(input, wrapperBaos);

    assertArrayEquals("Wrapper must produce identical output to direct call",
        directBaos.toByteArray(), wrapperBaos.toByteArray());
  }

  // ═══════════════════════════════════════════════════════════════════
  // BINARY FORMAT: Exact byte-level verification
  // ═══════════════════════════════════════════════════════════════════

  @Test
  public void encodeIntArrayExactByteCount() throws Exception {
    int[] input = {1, 2, 3};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeIntArray(input, baos);
    // version(4) + count(4) + 3*int(12) = 20 bytes
    assertEquals("int array of 3 should produce 20 bytes", 20, baos.toByteArray().length);
  }

  @Test
  public void encodeDoubleArrayExactByteCount() throws Exception {
    double[] input = {1.0, 2.0};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeDoubleArray(input, baos);
    // version(4) + count(4) + 2*double(16) = 24 bytes
    assertEquals("double array of 2 should produce 24 bytes", 24, baos.toByteArray().length);
  }

  @Test
  public void encodeVertexArrayPositionOnlyExactByteCount() throws Exception {
    Vertex[] input = {Vertex.createXYZ(1.0, 2.0, 3.0)};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    invokeEncodeVertexArray(input, baos);
    // version(4) + count(4) + format(4) + 3*double(24) = 36 bytes
    assertEquals("Single position-only vertex should produce 36 bytes", 36, baos.toByteArray().length);
  }

  // ═══════════════════════════════════════════════════════════════════
  // HELPERS: Reflection-based invocation (avoids compile-time dependency)
  // ═══════════════════════════════════════════════════════════════════

  private void invokeEncodeIntArray(int[] array, OutputStream os) throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    Method m = cls.getDeclaredMethod("encodeIntArray", int[].class, OutputStream.class);
    m.setAccessible(true);
    m.invoke(null, array, os);
  }

  private void invokeEncodeDoubleArray(double[] array, OutputStream os) throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    Method m = cls.getDeclaredMethod("encodeDoubleArray", double[].class, OutputStream.class);
    m.setAccessible(true);
    m.invoke(null, array, os);
  }

  private void invokeEncodeVertexArray(Vertex[] vertices, OutputStream os) throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayEncoder");
    Method m = cls.getDeclaredMethod("encodeVertexArray", Vertex[].class, OutputStream.class);
    m.setAccessible(true);
    m.invoke(null, vertices, os);
  }
}
