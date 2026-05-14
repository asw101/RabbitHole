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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Binary stream decoding for vertex, int, and double arrays.
 * Extracted from ASGDecoder to reduce file size.
 *
 * @author Dennis Cosgrove
 */
class BinaryArrayDecoder {

  static Vertex[] decodeVertexArray(InputStream is) {
    Vertex[] vertices = null;
    BufferedInputStream bis = new BufferedInputStream(is);
    DataInputStream dis = new DataInputStream(bis);
    try {
      int version = dis.readInt();
      if (version == 1) {
        vertices = decodeVertexArrayV1(dis);
      } else if (version == 2) {
        vertices = decodeVertexArrayV2(dis);
      } else if (version == 3) {
        vertices = decodeVertexArrayV3(dis);
      } else {
        throw new RuntimeException("invalid file version: " + version);
      }
      return vertices;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  private static Vertex[] decodeVertexArrayV1(DataInputStream dis) throws IOException {
    int vertexCount = dis.readInt();
    Vertex[] vertices = new Vertex[vertexCount];
    for (int index = 0; index < vertices.length; index++) {
      double x = dis.readDouble();
      double y = dis.readDouble();
      double z = dis.readDouble();
      float i = (float) dis.readDouble();
      float j = (float) dis.readDouble();
      float k = (float) dis.readDouble();
      float u = (float) dis.readDouble();
      float v = (float) dis.readDouble();
      vertices[index] = Vertex.createXYZIJKUV(x, y, z, i, j, k, u, v);
    }
    return vertices;
  }

  private static Vertex[] decodeVertexArrayV2(DataInputStream dis) throws IOException {
    int vertexCount = dis.readInt();
    Vertex[] vertices = new Vertex[vertexCount];
    for (int index = 0; index < vertices.length; index++) {
      int format = dis.readInt();
      Point3 position = Point3.NaN;
      if ((format & Vertex.FORMAT_POSITION) != 0) {
        position = new Point3(dis.readDouble(), dis.readDouble(), dis.readDouble());
      }
      Vector3f normal = Vector3f.NaN;
      if ((format & Vertex.FORMAT_NORMAL) != 0) {
        normal = new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat());
      }
      final Color4f diffuseColor;
      if ((format & Vertex.FORMAT_DIFFUSE_COLOR) != 0) {
        float red = (float) dis.readDouble();
        float green = (float) dis.readDouble();
        float blue = (float) dis.readDouble();
        float alpha = (float) dis.readDouble();
        diffuseColor = new Color4f(red, green, blue, alpha);
      } else {
        diffuseColor = null;
      }
      final TextureCoordinate2f textureCoordinate0;
      if ((format & Vertex.FORMAT_TEXTURE_COORDINATE_0) != 0) {
        float u = (float) dis.readDouble();
        float v = (float) dis.readDouble();
        textureCoordinate0 = new TextureCoordinate2f(u, v);
      } else {
        textureCoordinate0 = null;
      }
      vertices[index] = new Vertex(position, normal, diffuseColor, null, textureCoordinate0);
    }
    return vertices;
  }

  private static Vertex[] decodeVertexArrayV3(DataInputStream dis) throws IOException {
    int vertexCount = dis.readInt();
    Vertex[] vertices = new Vertex[vertexCount];
    for (int index = 0; index < vertices.length; index++) {
      int format = dis.readInt();
      Point3 position = Point3.NaN;
      if ((format & Vertex.FORMAT_POSITION) != 0) {
        position = new Point3(dis.readDouble(), dis.readDouble(), dis.readDouble());
      }
      Vector3f normal = Vector3f.NaN;
      if ((format & Vertex.FORMAT_NORMAL) != 0) {
        normal = new Vector3f(dis.readFloat(), dis.readFloat(), dis.readFloat());
      }
      final Color4f diffuseColor;
      if ((format & Vertex.FORMAT_DIFFUSE_COLOR) != 0) {
        float red = dis.readFloat();
        float green = dis.readFloat();
        float blue = dis.readFloat();
        float alpha = dis.readFloat();
        diffuseColor = new Color4f(red, green, blue, alpha);
      } else {
        diffuseColor = null;
      }
      final Color4f specularHighlightColor;
      if ((format & Vertex.FORMAT_SPECULAR_HIGHLIGHT_COLOR) != 0) {
        float red = dis.readFloat();
        float green = dis.readFloat();
        float blue = dis.readFloat();
        float alpha = dis.readFloat();
        specularHighlightColor = new Color4f(red, green, blue, alpha);
      } else {
        specularHighlightColor = null;
      }
      final TextureCoordinate2f textureCoordinate0;
      if ((format & Vertex.FORMAT_TEXTURE_COORDINATE_0) != 0) {
        float u = dis.readFloat();
        float v = dis.readFloat();
        textureCoordinate0 = new TextureCoordinate2f(u, v);
      } else {
        textureCoordinate0 = null;
      }
      vertices[index] = new Vertex(position, normal, diffuseColor, specularHighlightColor, textureCoordinate0);
    }
    return vertices;
  }

  static int[] decodeIntArray(InputStream is) {
    int[] array = null;
    BufferedInputStream bis = new BufferedInputStream(is);
    DataInputStream dis = new DataInputStream(bis);
    try {
      int version = dis.readInt();
      if (version == 1) {
        int faceCount = dis.readInt();
        /* unused int verticesPerFace = */
        dis.readInt();
        array = new int[faceCount * 3];
        for (int i = 0; i < array.length; i++) {
          array[i] = dis.readInt();
        }
      } else if (version == 2) {
        int count = dis.readInt();
        array = new int[count];
        for (int i = 0; i < array.length; i++) {
          array[i] = dis.readInt();
        }
        for (int i = 0; i < array.length; i += 3) {
          int temp = array[i];
          array[i] = array[i + 2];
          array[i + 2] = temp;
        }
      } else {
        throw new RuntimeException("invalid file version: " + version);
      }
      return array;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }

  static double[] decodeDoubleArray(InputStream is) {
    double[] array = null;
    BufferedInputStream bis = new BufferedInputStream(is);
    DataInputStream dis = new DataInputStream(bis);
    try {
      int version = dis.readInt();
      if (version == 1) {
        // there was no version 1
      } else if (version == 2) {
        int count = dis.readInt();
        array = new double[count];
        for (int i = 0; i < array.length; i++) {
          array[i] = dis.readDouble();
        }
      } else {
        throw new RuntimeException("invalid file version: " + version);
      }
      return array;
    } catch (IOException ioe) {
      throw new RuntimeException(ioe);
    }
  }
}
