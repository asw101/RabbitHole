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

import com.jogamp.opengl.*;
import edu.cmu.cs.dennisc.java.util.Lists;
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.print.PrintUtilities;
import edu.cmu.cs.dennisc.render.AsynchronousImageCapturer;
import edu.cmu.cs.dennisc.render.AsynchronousPicker;
import edu.cmu.cs.dennisc.render.RenderTarget;
import edu.cmu.cs.dennisc.render.event.*;
import edu.cmu.cs.dennisc.render.gl.GlDrawableUtils;
import edu.cmu.cs.dennisc.render.gl.imp.adapters.AdapterFactory;
import edu.cmu.cs.dennisc.render.gl.imp.adapters.GlrAbstractCamera;
import edu.cmu.cs.dennisc.scenegraph.AbstractCamera;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.nio.FloatBuffer;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import static com.jogamp.opengl.GL.GL_COLOR_BUFFER_BIT;

/**
 * @author Dennis Cosgrove
 */
public class RenderTargetImp {
  public RenderTargetImp(RenderTarget renderTarget) {
    this.renderTarget = renderTarget;
    this.reusableLookingGlassRenderEvent = new ReusableLookingGlassRenderEvent(this.getRenderTarget(), new Graphics2D(this.renderContext));
  }

  public RenderTarget getRenderTarget() {
    return this.renderTarget;
  }

  public edu.cmu.cs.dennisc.render.SynchronousPicker getSynchronousPicker() {
    return this.synchronousPicker;
  }

  public edu.cmu.cs.dennisc.render.SynchronousImageCapturer getSynchronousImageCapturer() {
    return this.synchronousImageCapturer;
  }

  public AsynchronousPicker getAsynchronousPicker() {
    return this.asynchronousPicker;
  }

  public AsynchronousImageCapturer getAsynchronousImageCapturer() {
    return this.asynchronousImageCapturer;
  }

  public void addRenderTargetListener(RenderTargetListener listener) {
    this.renderTargetListeners.add(listener);
  }

  public void removeRenderTargetListener(RenderTargetListener listener) {
    this.renderTargetListeners.remove(listener);
  }

  public void addSgCamera(AbstractCamera sgCamera, GLAutoDrawable glAutoDrawable) {
    assert sgCamera != null : this;
    this.sgCameras.add(sgCamera);
    if (!this.isListening()) {
      this.startListening(glAutoDrawable);
    }
  }

  public void removeSgCamera(AbstractCamera sgCamera, GLAutoDrawable glAutoDrawable) {
    assert sgCamera != null;
    this.sgCameras.remove(sgCamera);
    if (this.isListening()) {
      if (this.sgCameras.isEmpty()) {
        this.stopListening(glAutoDrawable);
      }
    }
  }

  public void clearSgCameras(GLAutoDrawable glAutoDrawable) {
    if (!this.sgCameras.isEmpty()) {
      this.sgCameras.clear();
    }
    if (this.isListening()) {
      this.stopListening(glAutoDrawable);
    }
  }

  public int getSgCameraCount() {
    return this.sgCameras.size();
  }

  public AbstractCamera getSgCameraAt(int index) {
    return this.sgCameras.get(index);
  }

  public List<AbstractCamera> getSgCameras() {
    return Collections.unmodifiableList(this.sgCameras);
  }

  public AbstractCamera getCameraAtAwtPoint(Point p) {
    ListIterator<AbstractCamera> iterator = this.sgCameras.listIterator(this.sgCameras.size());
    while (iterator.hasPrevious()) {
      AbstractCamera sgCamera = iterator.previous();
      Rectangle actualViewport = this.renderTarget.getActualViewportAsAwtRectangle(sgCamera);
      if (actualViewport.contains(p)) {
        return sgCamera;
      }

    }
    return null;
  }

  public void forgetAllCachedItems() {
      this.renderContext.forgetAllCachedItems();
  }

  public void clearUnusedTextures() {
    // Without a gl (GL2) there is nothing to clear
    if (this.renderContext.gl == null) {
        return;
    }
    this.renderContext.clearUnusedTextures();
  }

  /*package-private*/void addDisplayTask(DisplayTask displayTask) {
    displayTask.setRtImp(this);
    this.drawable.invoke(false, displayTask);
  }

  /*package-private*/ void fireInitialized(RenderTargetInitializeEvent e) {
    for (RenderTargetListener rtListener : this.renderTargetListeners) {
      rtListener.initialized(e);
    }
  }

  private void fireCleared(RenderTargetRenderEvent e) {
    for (RenderTargetListener rtListener : this.renderTargetListeners) {
      rtListener.cleared(e);
    }
  }

  private void fireRendered(RenderTargetRenderEvent e) {
    for (RenderTargetListener rtListener : this.renderTargetListeners) {
      rtListener.rendered(e);
    }
  }

  /*package-private*/ void fireResized(RenderTargetResizeEvent e) {
    for (RenderTargetListener rtListener : this.renderTargetListeners) {
      rtListener.resized(e);
    }
  }

  private static class ReusableLookingGlassRenderEvent extends RenderTargetRenderEvent {
    public ReusableLookingGlassRenderEvent(RenderTarget renderTarget, Graphics2D g) {
      super(renderTarget, g);
    }

    @Override
    public boolean isReservedForReuse() {
      return true;
    }

    private void prologue() {
      ((Graphics2D) getGraphics2D()).initialize(getTypedSource().getSurfaceSize());
    }

    private void epilogue() {
      getGraphics2D().dispose();
    }
  }

  private boolean isListening;

  public boolean isListening() {
    return this.isListening;
  }

  private void startListening(GLAutoDrawable drawable) {
    if (drawable != null) {
      this.isListening = true;
      this.drawable = drawable;
      this.drawable.addGLEventListener(this.glEventHandler);
    }
  }

  private void stopListening(GLAutoDrawable drawable) {
    if (drawable != null && drawable != this.drawable) {
      Logger.severe("request GLEventAdapter.stopListening(drawable) came for different drawable", drawable, this.drawable);
    } else {
      if (this.isListening) {
        this.isListening = false;
        if (drawable != null) {
          drawable.removeGLEventListener(this.glEventHandler);
        }
      } else {
        Logger.warning("request GLEventAdapter.stopListening(drawable) ignored; already not listening.");
      }
      this.drawable = null;
    }
  }

  /*package-private*/ void performRender() {
    RenderTarget rt = this.getRenderTarget();
    if (rt.isRenderingEnabled()) {
      this.renderContext.actuallyForgetTexturesIfNecessary();
      this.renderContext.actuallyForgetDisplayListsIfNecessary();
      if (this.isDisplayIgnoredDueToPreviousException) {
        //pass
      } else if ((this.drawableWidth == 0) || (this.drawableHeight == 0)) {
        Logger.severe(this.drawableWidth, this.drawableHeight, rt.getSurfaceSize());
      } else {
        try {
          this.reusableLookingGlassRenderEvent.prologue();
          try {
            this.fireCleared(this.reusableLookingGlassRenderEvent);
          } finally {
            this.reusableLookingGlassRenderEvent.epilogue();
          }
          if (rt.getSgCameraCount() > 0) {
            this.renderContext.initialize();
            for (AbstractCamera sgCamera : this.sgCameras) {
              GlrAbstractCamera<? extends AbstractCamera> cameraAdapterI = AdapterFactory.getAdapterFor(sgCamera);
              cameraAdapterI.performClearAndRenderOffscreen(this.renderContext, this.drawableWidth, this.drawableHeight);
              this.reusableLookingGlassRenderEvent.prologue();
              try {
                // Pass the screen size to post render because operations like speech bubbles use the screen size as a reference rather than the drawable size
                cameraAdapterI.postRender(this.renderContext, this.screenWidth, this.screenHeight, rt, this.reusableLookingGlassRenderEvent.getGraphics2D());
              } finally {
                this.reusableLookingGlassRenderEvent.epilogue();
              }
            }
            this.renderContext.renderLetterboxingIfNecessary(this.drawableWidth, this.drawableHeight);
          } else {
            this.renderContext.gl.glClearColor(0, 0, 0, 1);
            this.renderContext.gl.glClear(GL_COLOR_BUFFER_BIT);
          }
          this.reusableLookingGlassRenderEvent.prologue();
          try {
            this.fireRendered(this.reusableLookingGlassRenderEvent);
          } finally {
            this.reusableLookingGlassRenderEvent.epilogue();
          }
          this.renderContext.gl.glFlush();
          if ((this.rvColorBuffer != null) || (this.rvDepthBuffer != null)) {
            this.renderContext.captureBuffers(this.rvColorBuffer, this.rvDepthBuffer, this.atIsUpsideDown);
          }

        } catch (RuntimeException re) {
          Logger.severe("rendering will be disabled due to exception");
          this.isDisplayIgnoredDueToPreviousException = true;
          re.printStackTrace();
          throw re;
        } catch (Error er) {
          Logger.severe("rendering will be disabled due to exception");
          this.isDisplayIgnoredDueToPreviousException = true;
          er.printStackTrace();
          throw er;
        }
      }
    }
  }

  private BufferedImage createBufferedImageForUseAsColorBuffer(int type) {
    if (this.drawable != null) {
      if ((this.drawableWidth != GlDrawableUtils.getGlDrawableWidth(this.drawable)) || (this.drawableHeight != GlDrawableUtils.getGlDrawableHeight(this.drawable))) {
        PrintUtilities.println("warning: createBufferedImageForUseAsColorBuffer size mismatch");
        this.drawableWidth = GlDrawableUtils.getGlDrawableWidth(this.drawable);
        this.drawableHeight = GlDrawableUtils.getGlDrawableHeight(this.drawable);
        this.screenWidth = GlDrawableUtils.getGLJPanelWidth(drawable);
        this.screenHeight = GlDrawableUtils.getGLJPanelHeight(drawable);
      }
    } else {
      PrintUtilities.println("warning: drawable null");
    }

    if ((this.drawableWidth > 0) && (this.drawableHeight > 0)) {
      return new BufferedImage(this.drawableWidth, this.drawableHeight, type);
    } else {
      return null;
    }
  }

  public BufferedImage createBufferedImageForUseAsColorBuffer() {
    int type = BufferedImage.TYPE_4BYTE_ABGR;
    return createBufferedImageForUseAsColorBuffer(type);
  }

  public BufferedImage getColorBuffer(BufferedImage rv, boolean[] atIsUpsideDown) {
    return this.getColorBufferWithTransparencyBasedOnDepthBuffer(rv, null, atIsUpsideDown);
  }

  public BufferedImage createBufferedImageForUseAsColorBufferWithTransparencyBasedOnDepthBuffer() {
    return createBufferedImageForUseAsColorBuffer(BufferedImage.TYPE_4BYTE_ABGR);
  }

  public FloatBuffer createFloatBufferForUseAsDepthBuffer() {
    return FloatBuffer.allocate(this.drawableWidth * this.drawableHeight);
  }

  public BufferedImage getColorBufferWithTransparencyBasedOnDepthBuffer(BufferedImage rv, FloatBuffer depthBuffer, boolean[] atIsUpsideDown) {
    if (drawable != null) {
      GLContext glCurrentContext = GLContext.getCurrent();
      if ((glCurrentContext != null) && (glCurrentContext == this.drawable.getContext())) {
        this.renderContext.captureBuffers(rv, depthBuffer, atIsUpsideDown);
      } else {
        if (this.rvColorBuffer != null) {
          Logger.severe(this.rvColorBuffer);
        }
        this.rvColorBuffer = rv;
        this.rvDepthBuffer = depthBuffer;
        this.atIsUpsideDown = atIsUpsideDown;
        this.drawable.setAutoSwapBufferMode(false);
        try {
          this.drawable.display();
        } finally {
          this.rvColorBuffer = null;
          this.rvDepthBuffer = null;
          this.atIsUpsideDown = null;
          this.drawable.setAutoSwapBufferMode(true);
        }
      }
    }
    return rv;
  }

  final RenderContext renderContext = new RenderContext();

  GLAutoDrawable drawable;

  //The drawable size and the screen size are not necessarily the same
  //This is known to be the case on retina displays where the drawable size is 2x the screen size
  //See https://jogamp.org/bugzilla/show_bug.cgi?id=741 for details
  int drawableWidth;
  int drawableHeight;
  int screenWidth;
  int screenHeight;

  private BufferedImage rvColorBuffer = null;
  private FloatBuffer rvDepthBuffer = null;
  private boolean[] atIsUpsideDown = null;

  private boolean isDisplayIgnoredDueToPreviousException = false;
  private final ReusableLookingGlassRenderEvent reusableLookingGlassRenderEvent;

  private final RenderTarget renderTarget;

  private final SynchronousPicker synchronousPicker = new SynchronousPicker(this);
  private final SynchronousImageCapturer synchronousImageCapturer = new SynchronousImageCapturer(this);

  private final GlrAsynchronousPicker asynchronousPicker = new GlrAsynchronousPicker(this);
  private final GlrAsynchronousImageCapturer asynchronousImageCapturer = new GlrAsynchronousImageCapturer(this);

  private final List<RenderTargetListener> renderTargetListeners = Lists.newCopyOnWriteArrayList();

  private final List<AbstractCamera> sgCameras = Lists.newCopyOnWriteArrayList();

  final RenderTargetGlEventHandler glEventHandler = new RenderTargetGlEventHandler(this);
}
