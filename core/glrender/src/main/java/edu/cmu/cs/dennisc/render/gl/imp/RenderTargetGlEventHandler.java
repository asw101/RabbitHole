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
import edu.cmu.cs.dennisc.java.util.logging.Logger;
import edu.cmu.cs.dennisc.render.event.RenderTargetInitializeEvent;
import edu.cmu.cs.dennisc.render.event.RenderTargetResizeEvent;
import edu.cmu.cs.dennisc.render.gl.GlDrawableUtils;
import edu.cmu.cs.dennisc.system.graphics.ConformanceTestResults;

class RenderTargetGlEventHandler implements GLEventListener {
  private final RenderTargetImp rtImp;

  RenderTargetGlEventHandler(RenderTargetImp rtImp) {
    this.rtImp = rtImp;
  }

  @Override
  public void init(GLAutoDrawable drawable) {
    assert drawable == rtImp.drawable;
    GL2 gl = drawable.getGL().getGL2();
    ConformanceTestResults.SINGLETON.updateRenderInformationIfNecessary(gl);
    final boolean USE_DEBUG_GL = false;
    if (USE_DEBUG_GL && !(gl instanceof DebugGL2)) {
      gl = new DebugGL2(gl);
      Logger.info("using debug gl: ", gl);
      drawable.setGL(gl);
    }
    rtImp.drawableWidth = GlDrawableUtils.getGlDrawableWidth(drawable);
    rtImp.drawableHeight = GlDrawableUtils.getGlDrawableHeight(drawable);
    rtImp.screenWidth = GlDrawableUtils.getGLJPanelWidth(drawable);
    rtImp.screenHeight = GlDrawableUtils.getGLJPanelHeight(drawable);
    rtImp.renderContext.setGL(gl);
    rtImp.fireInitialized(new RenderTargetInitializeEvent(rtImp.getRenderTarget(),
        GlDrawableUtils.getGlDrawableWidth(drawable), GlDrawableUtils.getGlDrawableHeight(drawable)));
  }

  @Override
  public void display(GLAutoDrawable drawable) {
    assert drawable == rtImp.drawable;
    GL2 gl = drawable.getGL().getGL2();
    if (rtImp.renderContext.gl == null) {
      init(drawable);
      Logger.outln("note: initialize necessary from display");
    }
    if (rtImp.drawableWidth <= 0 || rtImp.drawableHeight <= 0) {
      int nextW = GlDrawableUtils.getGlDrawableWidth(drawable);
      int nextH = GlDrawableUtils.getGlDrawableHeight(drawable);
      int nextSW = GlDrawableUtils.getGLJPanelWidth(drawable);
      int nextSH = GlDrawableUtils.getGLJPanelHeight(drawable);
      if (rtImp.drawableWidth != nextW || rtImp.drawableHeight != nextH) {
        Logger.severe(rtImp.drawableWidth, rtImp.drawableHeight, nextW, nextH);
        rtImp.drawableWidth = nextW;
        rtImp.drawableHeight = nextH;
        rtImp.screenWidth = nextSW;
        rtImp.screenHeight = nextSH;
      }
    }
    rtImp.renderContext.setGL(gl);
    rtImp.performRender();
  }

  @Override
  public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
    assert drawable == rtImp.drawable;
    rtImp.drawableWidth = width;
    rtImp.drawableHeight = height;
    rtImp.screenWidth = GlDrawableUtils.getGLJPanelWidth(drawable);
    rtImp.screenHeight = GlDrawableUtils.getGLJPanelHeight(drawable);
    rtImp.fireResized(new RenderTargetResizeEvent(rtImp.getRenderTarget(), width, height));
  }

  @Override
  public void dispose(GLAutoDrawable drawable) {
    Logger.todo(drawable);
  }
}
