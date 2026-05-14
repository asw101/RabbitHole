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
import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.property.InstanceProperty;
import edu.cmu.cs.dennisc.property.event.PropertyEvent;
import edu.cmu.cs.dennisc.property.event.PropertyListener;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.scenegraph.Composite;
import edu.cmu.cs.dennisc.scenegraph.Element;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.scenegraph.Joint;
import edu.cmu.cs.dennisc.scenegraph.Mesh;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisual;
import edu.cmu.cs.dennisc.scenegraph.SkeletonVisualBoundingBoxTracker;
import edu.cmu.cs.dennisc.scenegraph.TexturedAppearance;
import edu.cmu.cs.dennisc.scenegraph.WeightedMesh;
import edu.cmu.cs.dennisc.scenegraph.bound.BoundUtilities;
import org.alice.math.immutable.AxisAlignedBox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static com.jogamp.opengl.GL.*;
import static com.jogamp.opengl.GL2ES1.GL_ALPHA_TEST;

public class GlrSkeletonVisual extends GlrVisual<SkeletonVisual> implements PropertyListener, SkeletonVisualBoundingBoxTracker {

  private static final float ALPHA_TEST_THRESHOLD = .5f;

  @Override
  public void initialize(SkeletonVisual element) {
    if (this.owner != null) {
      this.owner.setTracker(null);
    }
    super.initialize(element);
    element.setTracker(this);
    initializeDataIfNecessary();
  }

  private void initializeDataIfNecessary() {
    if (this.isDataDirty) {
      initializeData();
    }
  }

  private void initializeData() {
    if (this.owner != null) {
      if (this.currentSkeleton != null) {
        this.setListeningOnSkeleton(this.currentSkeleton, false);
        System.out.println("SWITCHING SKELETON FROM " + this.currentSkeleton.hashCode() + " TO " + owner.skeleton.getValue().hashCode());
      }
      this.currentSkeleton = owner.skeleton.getValue();
      if (this.currentSkeleton != null) {
        this.setListeningOnSkeleton(this.currentSkeleton, true);
        this.skeletonIsDirty = true;
      }
      if (owner.renderBackfaces()) {
        this.glrBackFacingAppearance = this.glrFrontFacingAppearance;
      }
      updateAppearanceToGeometryAdapterMap();
      updateAppearanceToMeshControllersMap();
    }
    this.isDataDirty = false;
  }

  private void releaseMappedAdapter(GlrElement<? extends Element> adapter) {
    if ((adapter != this.glrBackFacingAppearance) && (adapter != this.glrFrontFacingAppearance)) {
      boolean matchesGeometry = false;
      if (this.glrGeometries != null) {
        for (GlrGeometry<? extends Geometry> geometryAdapter : this.glrGeometries) {
          if (adapter == geometryAdapter) {
            matchesGeometry = true;
            break;
          }
        }
      }
      if (!matchesGeometry) {
        if (adapter.owner != null) {
          adapter.handleReleased();
        }
      }
    }
  }

  @Override
  public void handleReleased() {
    super.handleReleased();
    for (Map.Entry<Integer, GlrTexturedAppearance> appearanceEntry : this.appearanceIdToAdapterMap.entrySet()) {
      releaseMappedAdapter(appearanceEntry.getValue());
    }
    for (Map.Entry<Integer, GlrMesh<Mesh>[]> meshEntry : this.appearanceIdToGeometryAdapaters.entrySet()) {
      for (int i = 0; i < meshEntry.getValue().length; i++) {
        releaseMappedAdapter(meshEntry.getValue()[i]);
      }
    }
    this.appearanceIdToAdapterMap.clear();
    this.appearanceIdToGeometryAdapaters.clear();
    this.appearanceIdToMeshControllersMap.clear();
  }

  private static void setMeshGlState(GL2 gl, boolean cullBackfaces, boolean useAlphaTest) {
    if (!cullBackfaces) {
      gl.glDisable(GL_CULL_FACE);
    } else {
      gl.glEnable(GL_CULL_FACE);
      gl.glCullFace(GL_BACK);
    }
    if (useAlphaTest) {
      gl.glEnable(GL_ALPHA_TEST);
      gl.glAlphaFunc(GL_GREATER, ALPHA_TEST_THRESHOLD);
    } else {
      gl.glDisable(GL_ALPHA_TEST);
    }
  }

  private static void resetMeshGlState(GL2 gl) {
    gl.glEnable(GL_CULL_FACE);
    gl.glDisable(GL_ALPHA_TEST);
  }

  @Override
  protected void pickGeometry(PickContext pc, boolean isSubElementActuallyRequired) {
    //TODO: Enable gl.glEnable( GL_TEXTURE_2D ) alpha test picking
    initializeDataIfNecessary();
    if (this.skeletonIsDirty) {
      this.processWeightedMesh();
    }
    int i = 0;
    for (Map.Entry<Integer, GlrTexturedAppearance> appearanceEntry : this.appearanceIdToAdapterMap.entrySet()) {
      WeightedMeshControl[] weightedMeshControls = appearanceIdToMeshControllersMap.get(appearanceEntry.getKey());
      if (weightedMeshControls != null) {
        for (WeightedMeshControl wmc : weightedMeshControls) {
          pc.gl.glPushName(i++);
          setMeshGlState(pc.gl, wmc.weightedMesh.cullBackfaces.getValue(), wmc.weightedMesh.useAlphaTest.getValue());
          wmc.pickGeometry(pc, isSubElementActuallyRequired);
          resetMeshGlState(pc.gl);
          pc.gl.glPopName();
        }
      }
      GlrMesh<Mesh>[] meshAdapters = this.appearanceIdToGeometryAdapaters.get(appearanceEntry.getKey());
      if (meshAdapters != null) {
        for (GlrMesh<Mesh> ma : meshAdapters) {
          pc.gl.glPushName(i++);
          setMeshGlState(pc.gl, ma.owner.cullBackfaces.getValue(), ma.owner.useAlphaTest.getValue());
          ma.pickGeometry(pc, isSubElementActuallyRequired);
          resetMeshGlState(pc.gl);
          pc.gl.glPopName();
        }
      }
    }
  }

  @Override
  public AxisAlignedBox getAxisAlignedMinimumBoundingBox() {
    initializeDataIfNecessary();
    if (this.skeletonIsDirty) {
      this.processWeightedMesh();
    }
    AxisAlignedBox aabb = AxisAlignedBox.NaN;
    for (Map.Entry<Integer, GlrTexturedAppearance> appearanceEntry : this.appearanceIdToAdapterMap.entrySet()) {
      WeightedMeshControl[] weightedMeshControls = appearanceIdToMeshControllersMap.get(appearanceEntry.getKey());
      if (weightedMeshControls != null) {
        for (WeightedMeshControl wmc : weightedMeshControls) {
          aabb = BoundUtilities.getBoundingBox(wmc.vertexBuffer).union(aabb);
        }
      }
      GlrMesh<Mesh>[] meshAdapters = this.appearanceIdToGeometryAdapaters.get(appearanceEntry.getKey());
      if (meshAdapters != null) {
        for (GlrMesh<Mesh> ma : meshAdapters) {
          aabb = ma.owner.getAxisAlignedMinimumBoundingBox().union(aabb);
        }
      }
    }
    return aabb;
  }

  @Override
  protected void renderGeometry(RenderContext rc, GlrVisual.RenderType renderType) {
    initializeDataIfNecessary();
    if (this.skeletonIsDirty) {
      this.processWeightedMesh();
    }

    boolean canRenderAlpha = (renderType == GlrVisual.RenderType.ALL) || (renderType == GlrVisual.RenderType.ALPHA_BLENDED);
    boolean canRenderOpaque = (renderType == GlrVisual.RenderType.ALL) || (renderType == GlrVisual.RenderType.OPAQUE);
    boolean DEBUG_SKELETON = false;
    if (!DEBUG_SKELETON) {
      for (Map.Entry<Integer, GlrTexturedAppearance> appearanceEntry : this.appearanceIdToAdapterMap.entrySet()) {
        if (renderType != RenderType.SILHOUETTE) {
          appearanceEntry.getValue().setTexturePipelineState(rc);
        }

        boolean textureIsAlphaBlend = appearanceEntry.getValue().isAlphaBlended();

        WeightedMeshControl[] weightedMeshControls = appearanceIdToMeshControllersMap.get(appearanceEntry.getKey());
        if (weightedMeshControls != null) {
          for (WeightedMeshControl wmc : weightedMeshControls) {
            boolean meshIsAlpha = textureIsAlphaBlend && !wmc.weightedMesh.useAlphaTest.getValue();
            if ((meshIsAlpha && canRenderAlpha) || (!meshIsAlpha && canRenderOpaque)) {
              setMeshGlState(rc.gl, wmc.weightedMesh.cullBackfaces.getValue(), wmc.weightedMesh.useAlphaTest.getValue());
              wmc.renderGeometry(rc);
              resetMeshGlState(rc.gl);
            }
          }
        }
        GlrMesh<Mesh>[] meshAdapters = this.appearanceIdToGeometryAdapaters.get(appearanceEntry.getKey());
        if (meshAdapters != null) {
          for (GlrMesh<Mesh> ma : meshAdapters) {
            boolean meshIsAlpha = textureIsAlphaBlend && !ma.owner.useAlphaTest.getValue();
            if ((meshIsAlpha && canRenderAlpha) || (!meshIsAlpha && canRenderOpaque)) {
              setMeshGlState(rc.gl, ma.owner.cullBackfaces.getValue(), ma.owner.useAlphaTest.getValue());
              ma.render(rc, renderType);
              resetMeshGlState(rc.gl);
            }
          }
        }
      }
    } else {
      SkeletonWeightProcessor.renderSkeleton(rc, this.currentSkeleton);
    }
  }

  @Override
  protected boolean isActuallyShowing() {
    initializeDataIfNecessary();
    return super.isActuallyShowing()
        || isShowing && appearanceIdToMeshControllersMap.size() > 0 && isAnyFaceActuallyShowing();
  }

  @Override
  protected boolean hasOpaque() {
    initializeDataIfNecessary();
    if (super.hasOpaque()) {
      return true;
    }
    if (isAllAlpha()) {
      return false;
    }
    if (appearanceIdToMeshControllersMap.size() > 0) {
      if (isAnyFaceNotAlphaBlended()) {
        return true;
      }
      for (Map.Entry<Integer, WeightedMeshControl[]> controlEntry : this.appearanceIdToMeshControllersMap.entrySet()) {
        GlrTexturedAppearance ta = appearanceIdToAdapterMap.get(controlEntry.getKey());
        if ((ta != null) && ta.isActuallyShowing() && !ta.isAlphaBlended()) {
          return true;
        }
      }
    }
    return false;
  }

  private boolean isAnyFaceNotAlphaBlended() {
    return glrFrontFacingAppearance != null && !glrFrontFacingAppearance.isAlphaBlended()
        || glrBackFacingAppearance != null && !glrBackFacingAppearance.isAlphaBlended();
  }

  @Override
  protected boolean isAlphaBlended() {
    initializeDataIfNecessary();
    if (super.isAlphaBlended()) {
      return true;
    }
    if (appearanceIdToMeshControllersMap.size() > 0) {
      if (isAnyFaceAlphaBlended()) {
        return true;
      }
      for (Map.Entry<Integer, WeightedMeshControl[]> controlEntry : this.appearanceIdToMeshControllersMap.entrySet()) {
        GlrTexturedAppearance ta = appearanceIdToAdapterMap.get(controlEntry.getKey());
        if ((ta != null) && ta.isActuallyShowing() && ta.isAlphaBlended()) {
          return true;
        }
      }
    }
    return false;
  }

  private void setListeningOnSkeleton(Composite c, boolean shouldListen) {
    if (c == null) {
      return;
    }
    if (c instanceof Joint joint) {
      if (shouldListen) {
        joint.localTransformation.addPropertyListener(this);
      } else {
        joint.localTransformation.removePropertyListener(this);
      }
    }
    for (int i = 0; i < c.getComponentCount(); i++) {
      if (c.getComponentAt(i) instanceof Composite) {
        setListeningOnSkeleton((Composite) c.getComponentAt(i), shouldListen);
      }
    }
  }

  @Override
  public void propertyChanged(PropertyEvent e) {
    handleSkeletonTransformationChange();
  }

  private void handleNewSkeleton() {
    this.isDataDirty = true;
  }

  private void handleSkeletonTransformationChange() {
    this.skeletonIsDirty = true;
  }

  public void processWeightedMesh() {
    if (this.currentSkeleton != null) {
      SkeletonWeightProcessor.processWeightedMeshes(this.currentSkeleton, this.owner, appearanceIdToMeshControllersMap);
    }
    this.skeletonIsDirty = false;
  }

  private void updateAppearanceIdToAdapterMap() {
    synchronized (appearanceIdToAdapterMap) {
      List<GlrElement<? extends Element>> oldAdapters = new ArrayList<>();
      for (Map.Entry<Integer, GlrTexturedAppearance> appearanceEntry : this.appearanceIdToAdapterMap.entrySet()) {
        if (!oldAdapters.contains(appearanceEntry.getValue())) {
          oldAdapters.add(appearanceEntry.getValue());
        }
      }
      appearanceIdToAdapterMap.clear();
      List<GlrElement<? extends Element>> newAdapters = new ArrayList<>();
      for (TexturedAppearance ta : this.owner.textures.getValue()) {
        GlrTexturedAppearance newAdapter = AdapterFactory.getAdapterFor(ta);
        appearanceIdToAdapterMap.put(ta.textureId.getValue(), newAdapter);
        if (!newAdapters.contains(newAdapter)) {
          newAdapters.add(newAdapter);
        }
      }
      for (GlrElement<? extends Element> oldAdapter : oldAdapters) {
        if (!newAdapters.contains(oldAdapter)) {
          releaseMappedAdapter(oldAdapter);
        }
      }
    }
  }

  private void updateAppearanceToGeometryAdapterMap() {
    synchronized (appearanceIdToGeometryAdapaters) {
      appearanceIdToGeometryAdapaters.clear();
      for (TexturedAppearance ta : this.owner.textures.getValue()) {
        List<GlrMesh<?>> meshAdapters = Lists.newLinkedList();
        for (GlrGeometry<?> adapter : this.glrGeometries) {
          if (adapter instanceof GlrMesh<?> ma) {
            if (ma.owner.textureId.getValue() == ta.textureId.getValue()) {
              meshAdapters.add(ma);
            }
          }
        }
        appearanceIdToGeometryAdapaters.put(ta.textureId.getValue(), meshAdapters.toArray(new GlrMesh[meshAdapters.size()]));
      }
    }
  }

  protected void updateAppearanceToMeshControllersMap() {
    synchronized (appearanceIdToMeshControllersMap) {
      appearanceIdToMeshControllersMap.clear();
      for (TexturedAppearance ta : this.owner.textures.getValue()) {
        List<WeightedMeshControl> controls = new LinkedList<>();
        for (WeightedMesh weightedMesh : this.owner.weightedMeshes.getValue()) {
          if (weightedMesh.textureId.getValue() == ta.textureId.getValue()) {
            WeightedMeshControl control = new WeightedMeshControl();
            control.initialize(weightedMesh);
            controls.add(control);
          }
        }
        appearanceIdToMeshControllersMap.put(ta.textureId.getValue(), controls.toArray(new WeightedMeshControl[controls.size()]));
      }
    }
  }

  @Override
  protected void updateGeometryAdapters() {
    super.updateGeometryAdapters();
    updateAppearanceToGeometryAdapterMap();
  }

  @Override
  protected void propertyChanged(InstanceProperty<?> property) {
    if (property == owner.skeleton) {
      handleNewSkeleton();
    } else if (property == owner.weightedMeshes) {
      updateAppearanceToMeshControllersMap();
    } else if (property == owner.textures) {
      updateAppearanceIdToAdapterMap();
    } else if (property != owner.baseBoundingBox) {
      super.propertyChanged(property);
    }
  }

  private boolean skeletonIsDirty = true;
  private Joint currentSkeleton = null;
  private final Map<Integer, GlrTexturedAppearance> appearanceIdToAdapterMap = Maps.newHashMap();
  protected final Map<Integer, WeightedMeshControl[]> appearanceIdToMeshControllersMap = Maps.newHashMap();
  private final Map<Integer, GlrMesh<Mesh>[]> appearanceIdToGeometryAdapaters = Maps.newHashMap();
  private boolean isDataDirty = true;
}
