/*******************************************************************************
 * Copyright (c) 2006, 2018, Carnegie Mellon University. All rights reserved.
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
package org.lgna.story.resourceutilities;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;

/**
 * Static buffer helpers extracted from {@link JointedModelGltfExporter}.
 */
class GltfBufferUtils {

  private static final double EPSILON = 1e-6;

  private GltfBufferUtils() {
  }

  // Borrowed from JglTF/ObjNormals.java
  static void normalize(FloatBuffer normals) {
    int numZeroLengthNormals = 0;
    int numNaNNormals = 0;
    int n = normals.remaining() / 3;
    for (int i = 0; i < n; i++) {
      float x = normals.get(i * 3);
      float y = normals.get(i * 3 + 1);
      float z = normals.get(i * 3 + 2);
      float nx = 1.0f;
      float ny = 0.0f;
      float nz = 0.0f;
      if (Float.isNaN(x) || Float.isNaN(y) || Float.isNaN(z)) {
        numNaNNormals++;
      } else {
        double length = Math.sqrt(x * x + y * y + z * z);
        if (length < EPSILON) {
          numZeroLengthNormals++;
        } else {
          float invLength = (float) (1.0 / length);
          nx = x * invLength;
          ny = y * invLength;
          nz = z * invLength;
        }
      }
      normals.put(i * 3, nx);
      normals.put(i * 3 + 1, ny);
      normals.put(i * 3 + 2, nz);
    }
    if (numZeroLengthNormals > 0) {
      System.out.println("There have been " + numZeroLengthNormals + " normals with zero length. Using (1,0,0) as a default.");
    }
    if (numNaNNormals > 0) {
      System.out.println("There have been " + numNaNNormals + " normals with NaN components. Using (1,0,0) as a default.");
    }
  }

  static float[] convertToFloatArray(DoubleBuffer buf) {
    if (buf == null) {
      return new float[0];
    }
    float[] array = new float[buf.remaining()];
    int index = 0;
    while (buf.hasRemaining()) {
      array[index++] = (float) buf.get();
      array[index++] = (float) buf.get();
      array[index++] = (float) buf.get();
    }

    buf.rewind();
    return array;
  }
}
