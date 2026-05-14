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

import com.jogamp.opengl.GL2;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.scenegraph.Component;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.Matrix3x3;
import org.alice.math.immutable.Matrix4x4;

import java.nio.DoubleBuffer;
import java.util.Map;

import static com.jogamp.opengl.GL.GL_LINES;

/**
 * Handles skeleton joint traversal for weight processing and debug rendering.
 * Extracted from {@code GlrSkeletonVisual} to reduce class size.
 */
final class SkeletonWeightProcessor {

  private SkeletonWeightProcessor() {
  }

  static void processWeightedMeshes(Joint skeleton, SkeletonVisual owner,
                                    Map<Integer, WeightedMeshControl[]> meshControllers) {
    synchronized (meshControllers) {
      for (WeightedMeshControl[] controls : meshControllers.values()) {
        for (WeightedMeshControl wmc : controls) {
          wmc.preProcess();
        }
      }
      Matrix3x3 inverseScale = owner.scale.getValue().invert();
      synchronized (skeleton) {
        processJoint(skeleton, AffineMatrix4x4.IDENTITY, inverseScale, meshControllers);
      }
      for (WeightedMeshControl[] controls : meshControllers.values()) {
        for (WeightedMeshControl wmc : controls) {
          wmc.postProcess();
        }
      }
    }
  }

  private static void processJoint(Joint joint, Matrix4x4 parentTransform, Matrix3x3 inverseScale,
                                   Map<Integer, WeightedMeshControl[]> meshControllers) {
    if (joint == null) {
      return;
    }
    Matrix4x4 absoluteLocalTransform = parentTransform.times(joint.localTransformation.getValue());
    Matrix4x4 unscaledJointTransform = absoluteLocalTransform.scaleTranslation(inverseScale);

    for (WeightedMeshControl[] controls : meshControllers.values()) {
      for (WeightedMeshControl wmc : controls) {
        wmc.process(joint, unscaledJointTransform);
      }
    }
    for (int i = 0; i < joint.getComponentCount(); i++) {
      Component comp = joint.getComponentAt(i);
      if (comp instanceof Joint childJoint) {
        processJoint(childJoint, absoluteLocalTransform, inverseScale, meshControllers);
      }
    }
  }

  static void renderSkeleton(RenderContext rc, Joint skeleton) {
    synchronized (skeleton) {
      renderJoint(rc, skeleton, AffineMatrix4x4.IDENTITY);
    }
  }

  private static void renderJoint(RenderContext rc, Composite currentNode, Matrix4x4 oTransformationPre) {
    if (currentNode == null) {
      return;
    }

    Matrix4x4 oTransformationPost = oTransformationPre;
    if (currentNode instanceof Transformable transformable) {
      oTransformationPost = oTransformationPre.times(transformable.localTransformation.getValue());

      if ((currentNode instanceof Joint)) {
        rc.gl.glPushMatrix();
        rc.gl.glMultMatrixd(DoubleBuffer.wrap(oTransformationPost.asColumnMajorArray16()));
        rc.gl.glBegin(GL_LINES);
        try {
          final float FULL = 1.0f;
          final float ZERO = 0.0f;
          final float LENGTH = .1f;

          rc.gl.glDepthFunc(GL2.GL_ALWAYS);
          rc.gl.glColor3f(FULL, ZERO, ZERO);
          rc.gl.glVertex3d(0, 0, 0);
          rc.gl.glVertex3d(LENGTH, 0, 0);
          rc.gl.glColor3f(ZERO, FULL, ZERO);
          rc.gl.glVertex3d(0, 0, 0);
          rc.gl.glVertex3d(0, LENGTH, 0);
          rc.gl.glColor3f(ZERO, ZERO, FULL);
          rc.gl.glVertex3d(0, 0, 0);
          rc.gl.glVertex3d(0, 0, LENGTH);
          rc.gl.glColor3f(FULL, FULL, FULL);
          rc.gl.glVertex3d(0, 0, 0);
          rc.gl.glVertex3d(0, 0, -2 * LENGTH);
        } finally {
          rc.gl.glEnd();
          rc.gl.glPopMatrix();
        }
      }
    }
    for (int i = 0; i < currentNode.getComponentCount(); i++) {
      Component comp = currentNode.getComponentAt(i);
      if (comp instanceof Composite jointChild) {
        renderJoint(rc, jointChild, oTransformationPost);
      }
    }
  }
}
