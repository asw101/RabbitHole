package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.common.util.InterruptSource;
import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.packrect.Rect;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Rectangle2D;

/**
 * Delegate that owns the rendering pipeline methods extracted from
 * NonCachingTextRenderer: beginRendering, endRendering, internal_draw3D,
 * flushGlyphPipeline, draw3D_ROBUST, and debug.
 *
 * Suppressing checkstyle to ease comparison with TextRenderer.
 */
@SuppressWarnings("CheckStyle")
class TextRendererPipeline {

  final NonCachingTextRenderer renderer;

  TextRendererPipeline(NonCachingTextRenderer renderer) {
    this.renderer = renderer;
  }

  void beginRendering(final boolean ortho, final int width, final int height,
                      final boolean disableDepthTestForOrtho) {
    final GL2 gl = GLContext.getCurrentGL().getGL2();

    if (NonCachingTextRenderer.DEBUG && !renderer.debugged) {
      debug(gl);
    }

    renderer.inBeginEndPair = true;
    renderer.isOrthoMode = ortho;
    renderer.beginRenderingWidth = width;
    renderer.beginRenderingHeight = height;
    renderer.beginRenderingDepthTestDisabled = disableDepthTestForOrtho;

    if (ortho) {
      renderer.getBackingStore().beginOrthoRendering(width, height,
          disableDepthTestForOrtho);
    } else {
      renderer.getBackingStore().begin3DRendering();
    }

    // Push client attrib bits used by the pipelined quad renderer
    gl.glPushClientAttrib((int) GL2.GL_ALL_CLIENT_ATTRIB_BITS);

    if (!renderer.haveMaxSize) {
      // Query OpenGL for the maximum texture size and set it in the
      // RectanglePacker to keep it from expanding too large
      final int[] sz = new int[1];
      gl.glGetIntegerv(GL.GL_MAX_TEXTURE_SIZE, sz, 0);
      renderer.packer.setMaxSize(sz[0], sz[0]);
      renderer.haveMaxSize = true;
    }

    if (renderer.needToResetColor && renderer.haveCachedColor) {
      if (renderer.cachedColor == null) {
        renderer.getBackingStore().setColor(renderer.cachedR, renderer.cachedG,
            renderer.cachedB, renderer.cachedA);
      } else {
        renderer.getBackingStore().setColor(renderer.cachedColor);
      }

      renderer.needToResetColor = false;
    }

    // Disable future attempts to use mipmapping if TextureRenderer
    // doesn't support it
    if (renderer.mipmap && !renderer.getBackingStore().isUsingAutoMipmapGeneration()) {
      if (NonCachingTextRenderer.DEBUG) {
        System.err.println("Disabled mipmapping in TextRenderer");
      }

      renderer.mipmap = false;
    }
  }

  /**
   * emzic: here the call to glBindBuffer crashes on certain graphicscard/driver combinations
   * this is why the ugly try-catch block has been added, which falls back to the old textrenderer
   *
   * @param ortho
   * @throws GLException
   */
  void endRendering(final boolean ortho) throws GLException {
    flushGlyphPipeline();

    renderer.inBeginEndPair = false;

    final GL2 gl = GLContext.getCurrentGL().getGL2();

    // Pop client attrib bits used by the pipelined quad renderer
    gl.glPopClientAttrib();

    // The OpenGL spec is unclear about whether this changes the
    // buffer bindings, so preemptively zero out the GL_ARRAY_BUFFER
    // binding
    if (renderer.getMyUseVertexArrays() && renderer.is15Available(gl)) {
      try {
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, 0);
      } catch (final Exception e) {
        renderer.isExtensionAvailable_GL_VERSION_1_5 = false;
      }
    }

    if (ortho) {
      renderer.getBackingStore().endOrthoRendering();
    } else {
      renderer.getBackingStore().end3DRendering();
    }

    if (++renderer.numRenderCycles >= NonCachingTextRenderer.CYCLES_PER_FLUSH) {
      renderer.numRenderCycles = 0;

      if (NonCachingTextRenderer.DEBUG) {
        System.err.println("Clearing unused entries in endRendering()");
      }

      renderer.clearUnusedEntries();
    }
  }

  void internal_draw3D(final CharSequence str, float x, final float y, final float z,
                       final float scaleFactor) {
    for (final TextRendererGlyph glyph : renderer.mGlyphProducer.getGlyphs(str)) {
      final float advance = glyph.draw3D(x, y, z, scaleFactor);
      x += advance * scaleFactor;
    }
  }

  void flushGlyphPipeline() {
    if (renderer.mPipelinedQuadRenderer != null) {
      renderer.mPipelinedQuadRenderer.draw();
    }
  }

  void draw3D_ROBUST(final CharSequence str, final float x, final float y, final float z,
                     final float scaleFactor) {
    String curStr;
    if (str instanceof String string) {
      curStr = string;
    } else {
      curStr = str.toString();
    }

    // Look up the string on the backing store
    Rect rect = renderer.stringLocations.get(curStr);

    if (rect == null) {
      // Rasterize this string and place it on the backing store
      Graphics2D g = renderer.getGraphics2D();
      final Rectangle2D origBBox = NonCachingTextRenderer.preNormalize(
          renderer.renderDelegate.getBounds(curStr, renderer.font,
              renderer.getFontRenderContext()));
      final Rectangle2D bbox = renderer.normalize(origBBox);
      final Point origin = new Point((int) -bbox.getMinX(),
          (int) -bbox.getMinY());
      rect = new Rect(0, 0, (int) bbox.getWidth(),
          (int) bbox.getHeight(),
          new TextData(curStr, origin, origBBox, -1));

      renderer.packer.add(rect);
      renderer.stringLocations.put(curStr, rect);

      // Re-fetch the Graphics2D in case the addition of the rectangle
      // caused the old backing store to be thrown away
      g = renderer.getGraphics2D();

      // OK, should now have an (x, y) for this rectangle; rasterize
      // the String
      final int strx = rect.x() + origin.x;
      final int stry = rect.y() + origin.y;

      // Clear out the area we're going to draw into
      g.setComposite(AlphaComposite.Clear);
      g.fillRect(rect.x(), rect.y(), rect.w(), rect.h());
      g.setComposite(AlphaComposite.Src);

      // Draw the string
      renderer.renderDelegate.draw(g, curStr, strx, stry);

      if (NonCachingTextRenderer.DRAW_BBOXES) {
        final TextData data = (TextData) rect.getUserData();
        // Draw a bounding box on the backing store
        g.drawRect(strx - data.origOriginX(),
            stry - data.origOriginY(),
            (int) data.origRect().getWidth(),
            (int) data.origRect().getHeight());
        g.drawRect(strx - data.origin().x,
            stry - data.origin().y,
            rect.w(),
            rect.h());
      }

      // Mark this region of the TextureRenderer as dirty
      renderer.getBackingStore().markDirty(rect.x(), rect.y(), rect.w(),
          rect.h());
    }

    // OK, now draw the portion of the backing store to the screen
    final TextureRenderer backingStore = renderer.getBackingStore();

    // NOTE that the rectangles managed by the packer have their
    // origin at the upper-left but the TextureRenderer's origin is
    // at its lower left!!!
    final TextData data = (TextData) rect.getUserData();
    data.markUsed();

    final Rectangle2D origRect = data.origRect();

    // Align the leftmost point of the baseline to the (x, y, z) coordinate requested
    backingStore.draw3DRect(x - (scaleFactor * data.origOriginX()),
        y - (scaleFactor * ((float) origRect.getHeight() - data.origOriginY())), z,
        rect.x() + (data.origin().x - data.origOriginX()),
        backingStore.getHeight() - rect.y() - (int) origRect.getHeight() -
            (data.origin().y - data.origOriginY()),
        (int) origRect.getWidth(), (int) origRect.getHeight(), scaleFactor);
  }

  //----------------------------------------------------------------------
  // Debugging functionality
  //
  void debug(final GL gl) {
    renderer.dbgFrame = new Frame("TextRenderer Debug Output");

    final GLCanvas dbgCanvas = new GLCanvas(new GLCapabilities(gl.getGLProfile()));
    dbgCanvas.setSharedContext(GLContext.getCurrent());
    dbgCanvas.addGLEventListener(new DebugListener(renderer, gl, renderer.dbgFrame));
    renderer.dbgFrame.add(dbgCanvas);

    final FPSAnimator anim = new FPSAnimator(dbgCanvas, 10);
    renderer.dbgFrame.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosing(final WindowEvent e) {
        // Run this on another thread than the AWT event queue to
        // make sure the call to Animator.stop() completes before
        // exiting
        new InterruptSource.Thread(null, new Runnable() {
          @Override
          public void run() {
            anim.stop();
          }
        }).start();
      }
    });
    renderer.dbgFrame.setSize(NonCachingTextRenderer.kSize, NonCachingTextRenderer.kSize);
    renderer.dbgFrame.setVisible(true);
    anim.start();
    renderer.debugged = true;
  }
}
