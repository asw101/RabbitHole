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

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Binary stream encoding for vertex, int, and double arrays.
 * Mirrors {@link BinaryArrayDecoder} for the write side.
 * Extracted from ASGEncoder to reduce file size.
 *
 * <p>All callers pass {@link java.io.ByteArrayOutputStream}, so no
 * intermediate {@link java.io.BufferedOutputStream} is needed —
 * DataOutputStream writes directly to the in-memory buffer.</p>
 *
 * @author Dennis Cosgrove
 */
class BinaryArrayEncoder {

  static void encodeVertexArray(Vertex[] vertices, OutputStream os) {
    DataOutputStream dos = new DataOutputStream(os);
    try {
      dos.writeInt(3);
      dos.writeInt(vertices.length);
      for (Vertex vertice : vertices) {
        int format = vertice.getFormat();
        dos.writeInt(format);
        if ((format & Vertex.FORMAT_POSITION) != 0) {
          dos.writeDouble(vertice.position.x());
          dos.writeDouble(vertice.position.y());
          dos.writeDouble(vertice.position.z());
        }
        if ((format & Vertex.FORMAT_NORMAL) != 0) {
          dos.writeDouble(vertice.normal.x());
          dos.writeDouble(vertice.normal.y());
          dos.writeDouble(vertice.normal.z());
        }
        if ((format & Vertex.FORMAT_DIFFUSE_COLOR) != 0) {
          dos.writeFloat(vertice.diffuseColor.red);
          dos.writeFloat(vertice.diffuseColor.green);
          dos.writeFloat(vertice.diffuseColor.blue);
          dos.writeFloat(vertice.diffuseColor.alpha);
        }
        if ((format & Vertex.FORMAT_SPECULAR_HIGHLIGHT_COLOR) != 0) {
          dos.writeFloat(vertice.specularHighlightColor.red);
          dos.writeFloat(vertice.specularHighlightColor.green);
          dos.writeFloat(vertice.specularHighlightColor.blue);
          dos.writeFloat(vertice.specularHighlightColor.alpha);
        }
        if ((format & Vertex.FORMAT_TEXTURE_COORDINATE_0) != 0) {
          dos.writeFloat(vertice.textureCoordinate0.u);
          dos.writeFloat(vertice.textureCoordinate0.v);
        }
      }
      dos.flush();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  static void encodeIntArray(int[] array, OutputStream os) {
    DataOutputStream dos = new DataOutputStream(os);
    try {
      dos.writeInt(2);
      dos.writeInt(array.length);
      for (int element : array) {
        dos.writeInt(element);
      }
      dos.flush();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  static void encodeDoubleArray(double[] array, OutputStream os) {
    DataOutputStream dos = new DataOutputStream(os);
    try {
      dos.writeInt(2);
      dos.writeInt(array.length);
      for (double element : array) {
        dos.writeDouble(element);
      }
      dos.flush();
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}
