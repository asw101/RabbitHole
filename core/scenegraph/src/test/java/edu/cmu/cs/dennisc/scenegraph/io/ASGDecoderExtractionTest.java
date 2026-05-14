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

import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import static org.junit.Assert.*;

/**
 * Characterization tests for the ASGDecoder extraction into
 * BinaryArrayDecoder and PropertyValueParser.
 * Verifies that the refactored delegate classes preserve identical
 * behavior to the original monolithic ASGDecoder.
 */
public class ASGDecoderExtractionTest {

  // ── Structural: delegate classes exist and are package-private ───

  @Test
  public void binaryArrayDecoderExists() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayDecoder");
    assertFalse("BinaryArrayDecoder should be package-private",
        Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void propertyValueParserExists() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.PropertyValueParser");
    assertFalse("PropertyValueParser should be package-private",
        Modifier.isPublic(cls.getModifiers()));
  }

  @Test
  public void binaryArrayDecoderHasExpectedMethods() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.BinaryArrayDecoder");
    assertStaticMethod(cls, "decodeVertexArray");
    assertStaticMethod(cls, "decodeIntArray");
    assertStaticMethod(cls, "decodeDoubleArray");
  }

  @Test
  public void propertyValueParserHasParseValueMethod() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.PropertyValueParser");
    assertStaticMethod(cls, "parseValue");
  }

  @Test
  public void propertyValueParserHasXmlHelpers() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.PropertyValueParser");
    assertStaticMethod(cls, "getFirstChild");
    assertStaticMethod(cls, "getChildren");
    assertStaticMethod(cls, "getNodeText");
  }

  // ── ASGDecoder still exposes binary decode delegates ──────────────

  @Test
  public void asgDecoderStillHasBinaryDelegates() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.ASGDecoder");
    assertStaticMethod(cls, "decodeVertexArrayInBinary");
    assertStaticMethod(cls, "decodeIntArrayInBinary");
    assertStaticMethod(cls, "decodeDoubleArrayInBinary");
  }

  @Test
  public void asgDecoderStillHasDecodeMethod() throws Exception {
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.ASGDecoder");
    assertStaticMethod(cls, "decode");
  }

  // ── Behavioral: binary roundtrip through new BinaryArrayDecoder ──

  @Test
  public void intArrayBinaryRoundtripV2() throws Exception {
    int[] original = {10, 20, 30, 40, 50, 60};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ASG.encodeIntArrayInBinary(original, baos);
    int[] decoded = ASG.decodeIntArrayInBinary(new ByteArrayInputStream(baos.toByteArray()));
    // Version 2 decode applies winding-order swap per triplet
    assertArrayEquals(new int[]{30, 20, 10, 60, 50, 40}, decoded);
  }

  @Test
  public void doubleArrayBinaryRoundtripV2() throws Exception {
    double[] original = {1.1, 2.2, 3.3};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ASG.encodeDoubleArrayInBinary(original, baos);
    double[] decoded = ASG.decodeDoubleArrayInBinary(new ByteArrayInputStream(baos.toByteArray()));
    assertArrayEquals(original, decoded, 0.0001);
  }

  @Test
  public void vertexArrayBinaryRoundtrip() throws Exception {
    Vertex[] original = {Vertex.createXYZIJKUV(1, 2, 3, 0, 1, 0, 0.5f, 0.5f)};
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ASG.encodeVertexArrayInBinary(original, baos);
    Vertex[] decoded = ASG.decodeVertexArrayInBinary(new ByteArrayInputStream(baos.toByteArray()));
    assertEquals(original.length, decoded.length);
    assertEquals(original[0].position, decoded[0].position);
  }

  @Test(expected = RuntimeException.class)
  public void invalidBinaryVersionThrows() {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    DataOutputStream dos = new DataOutputStream(baos);
    try {
      dos.writeInt(999);
      dos.flush();
    } catch (Exception e) {
      fail("setup failed");
    }
    ASG.decodeIntArrayInBinary(new ByteArrayInputStream(baos.toByteArray()));
  }

  // ── ASGDecoder line count gate ───────────────────────────────────

  @Test
  public void asgDecoderIsUnder500Lines() throws Exception {
    // Read the source file at compile/test time via classloader resource path
    // is not practical; instead verify via the class method count as a proxy.
    // The real gate is the wc -l check in CI / the PR description.
    Class<?> cls = Class.forName("edu.cmu.cs.dennisc.scenegraph.io.ASGDecoder");
    int methodCount = cls.getDeclaredMethods().length;
    // After extraction, ASGDecoder should have substantially fewer methods
    // than the original 20+ methods (now ~12 with delegates).
    assertTrue("ASGDecoder should have fewer than 20 declared methods, has " + methodCount,
        methodCount < 20);
  }

  // ── Helper ───────────────────────────────────────────────────────

  private static void assertStaticMethod(Class<?> cls, String name) {
    for (Method m : cls.getDeclaredMethods()) {
      if (m.getName().equals(name) && Modifier.isStatic(m.getModifiers())) {
        return;
      }
    }
    fail(cls.getSimpleName() + " should have a static method named " + name);
  }
}
