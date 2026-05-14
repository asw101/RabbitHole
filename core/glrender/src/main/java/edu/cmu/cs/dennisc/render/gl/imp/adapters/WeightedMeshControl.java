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

package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import edu.cmu.cs.dennisc.java.util.BufferUtilities;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.scenegraph.InverseAbsoluteTransformationWeightsPair;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix4x4;

import java.nio.DoubleBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Controls per-vertex skinning for a single {@link WeightedMesh}.
 * Extracted from {@code GlrSkeletonVisual} to reduce class size.
 */
public class WeightedMeshControl {
  protected WeightedMesh weightedMesh;

  protected DoubleBuffer vertexBuffer;
  protected FloatBuffer normalBuffer;
  protected FloatBuffer textCoordBuffer;
  protected IntBuffer indexBuffer;

  private AffineMatrix4x4[] weightedJointMatrices;
  private float[] weights;
  private boolean needsInitialization = true;

  public void initialize(WeightedMesh weightedMesh) {
    this.weightedMesh = weightedMesh;
    internalInitialize();
  }

  private void internalInitialize() {
    if (this.weightedMesh != null) {
      this.textCoordBuffer = this.weightedMesh.textCoordBuffer.getValue();
      this.indexBuffer = this.weightedMesh.indexBuffer.getValue();

      this.normalBuffer = BufferUtilities.copyFloatBuffer(this.weightedMesh.normalBuffer.getValue());
      this.vertexBuffer = BufferUtilities.copyDoubleBuffer(this.weightedMesh.vertexBuffer.getValue());
      int nVertexCount = this.vertexBuffer.limit() / 3;

      this.weightedJointMatrices = new AffineMatrix4x4[nVertexCount];
      this.weights = new float[nVertexCount];
      for (int i = 0; i < nVertexCount; i++) {
        this.weightedJointMatrices[i] = AffineMatrix4x4.NaN;
        this.weights[i] = 0f;
      }
      needsInitialization = false;
    }
  }

  void preProcess() {
    for (int i = 0; i < this.weightedJointMatrices.length; i++) {
      this.weightedJointMatrices[i] = AffineMatrix4x4.NaN;
      this.weights[i] = 0f;
    }
  }

  void process(Joint joint, Matrix4x4 jointTransform) {
    InverseAbsoluteTransformationWeightsPair iatwp = this.weightedMesh.weightInfo.getValue().getMap().get(joint.jointID.getValue());
    if (iatwp == null) {
      return;
    }
    // jointTransform * IBMi - This is the reverse of the Collada skin weighting spec which is IBMi * JMi
    Matrix4x4 oDelta = jointTransform.times(iatwp.getInverseAbsoluteTransformation());
    InverseAbsoluteTransformationWeightsPair.WeightIterator weightIterator = iatwp.getIterator();
    while (weightIterator.hasNext()) {
      int vertexIndex = weightIterator.getIndex();
      float weight = weightIterator.next();
      // Accumulating the transforms by weight produces interim that breaks the Orientation's normalization
      // until the total weight is 1, or any other value is corrected for in postProcess.
      AffineMatrix4x4 transform = (AffineMatrix4x4) oDelta.times(weight);
      this.weightedJointMatrices[vertexIndex] = weightedJointMatrices[vertexIndex].plusPreservingAffine(transform);
      this.weights[vertexIndex] += weight;
    }
  }

  void postProcess() {
    for (int i = 0; i < weightedJointMatrices.length; i++) {
      float weight = weights[i];
      if ((!(0.999f < weight)) || (!(weight < 1.001f))) {
        if (weight != 0) {
          // Adjust for accumulated weight. Once done the Orientation should be normalized again.
          weightedJointMatrices[i] = weightedJointMatrices[i].times(1.0 / weight);
        }
      }
    }
    transformBuffers(weightedMesh.vertexBuffer.getValue().asReadOnlyBuffer(),
                     weightedMesh.normalBuffer.getValue().asReadOnlyBuffer());
  }

  private void transformBuffers(DoubleBuffer verticesSrc, FloatBuffer normalsSrc) {
    double[] vertexSrc = new double[3];
    float[] normalSrc = new float[3];
    double[] vertexDst = new double[3];
    float[] normalDst = new float[3];
    vertexBuffer.rewind();
    normalBuffer.rewind();
    verticesSrc.rewind();
    normalsSrc.rewind();

    for (Matrix4x4 voAffineMatrix : weightedJointMatrices) {
      vertexSrc[0] = verticesSrc.get();
      vertexSrc[1] = verticesSrc.get();
      vertexSrc[2] = verticesSrc.get();
      voAffineMatrix.transformPoint3(vertexDst, 0, vertexSrc, 0);
      vertexBuffer.put(vertexDst);

      normalSrc[0] = normalsSrc.get();
      normalSrc[1] = normalsSrc.get();
      normalSrc[2] = normalsSrc.get();
      voAffineMatrix.transformVector3(normalDst, 0, normalSrc, 0);
      normalBuffer.put(normalDst);
    }
  }

  public void renderGeometry(RenderContext rc) {
    if (this.needsInitialization) {
      this.internalInitialize();
    }
    GlrMesh.renderMesh(rc, vertexBuffer, normalBuffer, textCoordBuffer, indexBuffer);
  }

  public void pickGeometry(PickContext pc, boolean isSubElementRequired) {
    if (this.needsInitialization) {
      this.internalInitialize();
    }
    GlrMesh.pickMesh(pc, vertexBuffer, indexBuffer);
  }
}
