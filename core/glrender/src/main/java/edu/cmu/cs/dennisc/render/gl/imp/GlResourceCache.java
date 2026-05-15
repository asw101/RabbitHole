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

package edu.cmu.cs.dennisc.render.gl.imp;

import com.jogamp.opengl.GL;
import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.Maps;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.render.gl.ForgettableBinding;
import edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrGeometry;
import edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrTexture;
import edu.cmu.cs.dennisc.scenegraph.Geometry;
import edu.cmu.cs.dennisc.texture.Texture;

import java.util.List;
import java.util.Map;

/**
 * GL resource lifecycle delegate extracted from {@link RenderContext}.
 * Owns display list and texture binding maps plus deferred-deletion queues.
 *
 * @author Dennis Cosgrove
 */
class GlResourceCache {

  private static final List<RenderContext.UnusedTexturesListener> unusedTexturesListeners = Lists.newCopyOnWriteArrayList();

  private final Map<GlrGeometry<? extends Geometry>, Integer> displayListMap = Maps.newHashMap();
  private final Map<GlrTexture<? extends Texture>, ForgettableBinding> textureBindingMap = Maps.newHashMap();
  private final List<Integer> toBeForgottenDisplayLists = Lists.newCopyOnWriteArrayList();
  private final List<ForgettableBinding> toBeForgottenTextures = Lists.newCopyOnWriteArrayList();

  GlResourceCache() {
  }

  static void addUnusedTexturesListener(RenderContext.UnusedTexturesListener listener) {
    unusedTexturesListeners.add(listener);
  }

  static void removeUnusedTexturesListener(RenderContext.UnusedTexturesListener listener) {
    unusedTexturesListeners.remove(listener);
  }

  static void clearUnusedTextures(GL gl) {
    for (RenderContext.UnusedTexturesListener listener : unusedTexturesListeners) {
      listener.unusedTexturesCleared(gl);
    }
  }

  Integer getDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter) {
    synchronized (this.displayListMap) {
      return this.displayListMap.get(geometryAdapter);
    }
  }

  Integer generateDisplayListID(GlrGeometry<? extends Geometry> geometryAdapter, RenderContext renderContext) {
    Integer id = renderContext.gl.glGenLists(1);
    synchronized (this.displayListMap) {
      this.displayListMap.put(geometryAdapter, id);
    }
    geometryAdapter.addRenderContext(renderContext);
    return id;
  }

  void forgetGeometryAdapter(GlrGeometry<? extends Geometry> geometryAdapter, boolean removeFromMap, RenderContext renderContext) {
    synchronized (this.displayListMap) {
      Integer value = this.displayListMap.get(geometryAdapter);
      if (value != null) {
        this.toBeForgottenDisplayLists.add(value);
        if (removeFromMap) {
          this.displayListMap.remove(geometryAdapter);
        }
        geometryAdapter.removeRenderContext(renderContext);
      }
    }
  }

  void forgetGeometryAdapter(GlrGeometry<? extends Geometry> geometryAdapter, RenderContext renderContext) {
    forgetGeometryAdapter(geometryAdapter, true, renderContext);
  }

  void actuallyForgetDisplayListsIfNecessary(RenderContext renderContext) {
    final int N = this.toBeForgottenDisplayLists.size();
    if (N > 0) {
      synchronized (this.toBeForgottenDisplayLists) {
        for (Integer toBeForgottenDisplayList : this.toBeForgottenDisplayLists) {
          renderContext.gl.glDeleteLists(toBeForgottenDisplayList, 1);
        }
        this.toBeForgottenDisplayLists.clear();
      }
    }
  }

  private void forgetTextureBindingID(GlrTexture<? extends Texture> textureAdapter, ForgettableBinding value, boolean removeFromMap, RenderContext renderContext) {
    if (value != null) {
      this.toBeForgottenTextures.add(value);
      if (removeFromMap) {
        this.textureBindingMap.remove(textureAdapter);
      }
      textureAdapter.removeRenderContext(renderContext);
      Logger.info("texture adapter forgotten:", textureAdapter, value);
    } else {
      Logger.warning("no id for texture adapter:", textureAdapter);
    }
  }

  void forgetTextureAdapter(GlrTexture<? extends Texture> textureAdapter, boolean removeFromMap, RenderContext renderContext) {
    synchronized (this.textureBindingMap) {
      forgetTextureBindingID(textureAdapter, this.textureBindingMap.get(textureAdapter), removeFromMap, renderContext);
    }
  }

  void forgetTextureAdapter(GlrTexture<? extends Texture> textureAdapter, RenderContext renderContext) {
    forgetTextureAdapter(textureAdapter, true, renderContext);
  }

  void actuallyForgetTexturesIfNecessary(RenderContext renderContext) {
    final int N = this.toBeForgottenTextures.size();
    if (N > 0) {
      synchronized (this.toBeForgottenTextures) {
        for (ForgettableBinding toBeForgottenTexture : this.toBeForgottenTextures) {
          toBeForgottenTexture.forget(renderContext);
        }
        this.toBeForgottenTextures.clear();
      }
    }
  }

  private void forgetAllGeometryAdapters(RenderContext renderContext) {
    synchronized (this.displayListMap) {
      for (GlrGeometry<? extends Geometry> geometryAdapter : this.displayListMap.keySet()) {
        forgetGeometryAdapter(geometryAdapter, false, renderContext);
      }
      this.displayListMap.clear();
    }
  }

  private void forgetAllTextureAdapters(RenderContext renderContext) {
    synchronized (this.textureBindingMap) {
      for (GlrTexture<? extends Texture> textureAdapter : this.textureBindingMap.keySet()) {
        forgetTextureBindingID(textureAdapter, this.textureBindingMap.get(textureAdapter), false, renderContext);
      }
      this.textureBindingMap.clear();
    }
  }

  void forgetAllCachedItems(RenderContext renderContext) {
    this.forgetAllGeometryAdapters(renderContext);
    this.forgetAllTextureAdapters(renderContext);
  }

  //  //todo: better name
  //  public void put( TextureAdapter< ? extends edu.cmu.cs.dennisc.texture.Texture > textureAdapter, com.sun.opengl.util.texture.Texture glTexture ) {
  //    this.textureBindingMap.put( textureAdapter, glTexture );
  //  }
}
