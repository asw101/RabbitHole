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
package edu.cmu.cs.dennisc.nebulous;

import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.eula.LicenseRejectedException;
import edu.cmu.cs.dennisc.java.lang.SystemUtilities;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.Visual;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AxisAlignedBox;
import org.alice.math.immutable.Matrix4x4;
import org.alice.math.immutable.Point3;
import org.lgna.story.resources.JointId;
import org.lgna.story.resources.JointedModelResource;
import org.lgna.story.resourceutilities.NebulousStorytellingResources;

/**
 * @author Dennis Cosgrove
 */
public abstract class Model extends Geometry {

  static {
    if (SystemUtilities.getBooleanProperty("org.alice.ide.disableDefaultNebulousLoading", false)) {
      //Don't load nebulous resources if the default loading is disabled
      //Disabling should only happen under controlled circumstances like running the model batch process
    } else {
      NebulousStorytellingResources.INSTANCE.loadSimsBundles();
    }
    //    Manager.setDebugDraw( true );
  }

  public Model() throws LicenseRejectedException {
    Manager.initializeIfNecessary();
  }

  private native void render(GL gl, float globalBrightness, boolean renderAlpha, boolean renderOpaque);

  public void synchronizedRender(GL gl, float globalBrightness, boolean renderAlpha, boolean renderOpaque) {
    synchronized (renderLock) {
      try {
        this.render(gl, globalBrightness, renderAlpha, renderOpaque);
      } catch (RuntimeException re) {
        System.err.println(this);
        throw re;
      }
    }
  }

  private native void pick();

  public void synchronizedPick() {
    synchronized (renderLock) {
      pick();
    }
  }

  private native boolean isAlphaBlended();

  public boolean synchronizedIsAlphaBlended() {
    synchronized (renderLock) {
      return isAlphaBlended();
    }
  }

  private native boolean hasOpaque();

  public boolean synchronizedHasOpaque() {
    synchronized (renderLock) {
      return hasOpaque();
    }
  }

  private native void getAxisAlignedBoundingBoxForJoint(String name, String parent, double[] bboxData);

  private native void updateAxisAlignedBoundingBox(double[] bboxData);

  private native void getOriginalTransformationForPartNamed(double[] transformOut, String name, String parent);

  private native void getLocalTransformationForPartNamed(double[] transformOut, String name, String parent);

  private native void setLocalTransformationForPartNamed(String name, String parent, double[] transformIn);

  private native void getAbsoluteTransformationForPartNamed(double[] transformOut, String name);

  private native String[] getUnweightedMeshIds();

  private native String[] getWeightedMeshIds();

  private native String[] getTextureIds();

  private native void prepareModelForExporting();

  protected native int[] getIndicesForMesh(String meshId, String textureId);

  private native float[] getVerticesForMesh(String meshId);

  private native float[] getUnweightedVerticesForMesh(String meshId);

  private native float[] getUnweightedNormalsForMesh(String meshId);

  private native float[] getNormalsForMesh(String meshId);

  private native float[] getUvsForMesh(String meshId);

  protected native String[] getTextureIdsForMesh(String meshId);

  private native boolean isMeshWeightedToJoint(String meshId, String jointId);

  private native double[] getInvAbsTransForWeightedMeshAndJoint(String meshId, String jointId);

  private native float[] getVertexWeightsForWeightedMeshAndJoint(String meshId, String jointId, String parentId);

  private native int getMaterialTypeForTexture(String textureId);

  private native int getImageWidthForTexture(String textureId);

  private native int getImageHeightForTexture(String textureId);

  private native int getBytesPerPixelForTexture(String textureId);

  private native void getImageDataForTexture(String textureId, byte[] imageData);

  public void setVisual(Visual visual) {
    this.sgAssociatedVisual = visual;
  }

  public void setSGParent(Composite parent) {
    this.sgParent = parent;
  }

  public Composite getSGParent() {
    return this.sgParent;
  }

  public AffineMatrix4x4 getOriginalTransformationForJoint(JointId joint) {
    double[] buffer = new double[12];
    try {
      getOriginalTransformationForPartNamed(buffer, joint.toString(), joint.getParent() == null ? "" : joint.getParent().toString());
    } catch (RuntimeException re) {
      Logger.severe(joint);
      throw re;
    }
    return AffineMatrix4x4.createFromColumnMajorArray12(buffer);
  }

  public AffineMatrix4x4 getLocalTransformationForJoint(JointId joint) {
    double[] buffer = new double[12];
    try {
      getLocalTransformationForPartNamed(buffer, joint.toString(), joint.getParent() == null ? "" : joint.getParent().toString());
    } catch (RuntimeException re) {
      Logger.severe(joint);
      throw re;
    }
    return AffineMatrix4x4.createFromColumnMajorArray12(buffer);
  }

  public void setLocalTransformationForJoint(JointId joint, AffineMatrix4x4 localTrans) {
    synchronized (renderLock) {
      setLocalTransformationForPartNamed(joint.toString(), joint.getParent() == null ? "" : joint.getParent().toString(), localTrans.asColumnMajorArray12());
    }
  }

  public boolean hasJoint(JointId joint) {
    //There's no specific "hasJoint" native call, so this uses the getLocalTransformationForPartNamed and catches the error if the joint isn't found
    //TODO: implement a simpler "hasJoint" in the native code
    double[] buffer = new double[12];
    try {
      getLocalTransformationForPartNamed(buffer, joint.toString(), joint.getParent() == null ? "" : joint.getParent().toString());
    } catch (RuntimeException re) {
      return false;
    }
    return true;
  }

  public AffineMatrix4x4 getAbsoluteTransformationForJoint(JointId joint) {
    double[] buffer = new double[12];
    getAbsoluteTransformationForPartNamed(buffer, joint.toString());
    return AffineMatrix4x4.createFromColumnMajorArray12(buffer);
  }

  @Override
  public void transform(Matrix4x4 trans) {
    throw new RuntimeException("todo");
  }

  public AxisAlignedBox getAxisAlignedBoundingBoxForJoint(JointId joint) {
    double[] bboxData = new double[6];
    getAxisAlignedBoundingBoxForJoint(joint.toString(), joint.getParent() == null ? "" : joint.getParent().toString(), bboxData);
    AxisAlignedBox bbox = AxisAlignedBox.createAxisAlignedBox(bboxData[0], bboxData[1], bboxData[2], bboxData[3], bboxData[4], bboxData[5]);
    bbox.scale(this.sgAssociatedVisual.scale.getValue());
    return bbox;
  }

  @Override
  protected AxisAlignedBox updateBoundingBox() {
    //the bounding boxes come in the form (double[6])
    double[] bboxData = new double[6];
    updateAxisAlignedBoundingBox(bboxData);
    return new AxisAlignedBox(
        new Point3(bboxData[0], bboxData[1], bboxData[2]),
        new Point3(bboxData[3], bboxData[4], bboxData[5]));
  }

  public SkeletonVisual createSkeletonVisualForExporting(JointedModelResource resource) {
    return SkeletonExporter.export(this, resource);
  }

  // Package-private accessors for SkeletonExporter — keep native methods private
  void doPrepareForExporting() {
    prepareModelForExporting();
  }

  String[] fetchTextureIds() {
    return getTextureIds();
  }

  String[] fetchUnweightedMeshIds() {
    return getUnweightedMeshIds();
  }

  String[] fetchWeightedMeshIds() {
    return getWeightedMeshIds();
  }

  float[] fetchVerticesForMesh(String meshId) {
    return getVerticesForMesh(meshId);
  }

  float[] fetchUnweightedVerticesForMesh(String meshId) {
    return getUnweightedVerticesForMesh(meshId);
  }

  float[] fetchNormalsForMesh(String meshId) {
    return getNormalsForMesh(meshId);
  }

  float[] fetchUnweightedNormalsForMesh(String meshId) {
    return getUnweightedNormalsForMesh(meshId);
  }

  float[] fetchUvsForMesh(String meshId) {
    return getUvsForMesh(meshId);
  }

  boolean checkMeshWeightedToJoint(String meshId, String jointId) {
    return isMeshWeightedToJoint(meshId, jointId);
  }

  double[] fetchInvAbsTrans(String meshId, String jointId) {
    return getInvAbsTransForWeightedMeshAndJoint(meshId, jointId);
  }

  float[] fetchVertexWeights(String meshId, String jointId, String parentId) {
    return getVertexWeightsForWeightedMeshAndJoint(meshId, jointId, parentId);
  }

  int fetchMaterialType(String textureId) {
    return getMaterialTypeForTexture(textureId);
  }

  int fetchImageWidth(String textureId) {
    return getImageWidthForTexture(textureId);
  }

  int fetchImageHeight(String textureId) {
    return getImageHeightForTexture(textureId);
  }

  int fetchBytesPerPixel(String textureId) {
    return getBytesPerPixelForTexture(textureId);
  }

  void fetchImageData(String textureId, byte[] imageData) {
    getImageDataForTexture(textureId, imageData);
  }

  String fetchName() {
    return getName();
  }

  void buildRepr(StringBuilder sb) {
    appendRepr(sb);
  }

  @Override
  public AffineMatrix4x4 getPlane() {
    throw new RuntimeException("todo");
  }

  protected Composite sgParent;
  protected Visual sgAssociatedVisual;

  protected final Object renderLock = new Object();
}
