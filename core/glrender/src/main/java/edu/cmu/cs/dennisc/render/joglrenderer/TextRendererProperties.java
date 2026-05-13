package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.opengl.GLException;
import com.jogamp.opengl.util.packrect.Rect;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * Extracted property state, bounds, and cleanup delegate from NonCachingTextRenderer.
 * Owns color caching fields, smoothing, useVertexArrays, and associated methods.
 *
 * Suppressing checkstyle to ease comparison with TextRenderer.
 */
@SuppressWarnings("CheckStyle")
class TextRendererProperties {

  final NonCachingTextRenderer renderer;

  // For resetting the color after disposal of the old backing store
  boolean haveCachedColor;
  float cachedR;
  float cachedG;
  float cachedB;
  float cachedA;
  Color cachedColor;
  boolean needToResetColor;

  // Whether GL_LINEAR filtering is enabled for the backing store
  boolean smoothing = true;

  private boolean useVertexArrays = true;

  TextRendererProperties(final NonCachingTextRenderer renderer) {
    this.renderer = renderer;
  }

  public void dispose() throws GLException {
    if (null != renderer.mPipelinedQuadRenderer) {
      renderer.mPipelinedQuadRenderer.dispose();
    }
    renderer.packer.dispose();
    renderer.packer = null;
    renderer.cachedBackingStore = null;
    renderer.cachedGraphics = null;
    renderer.cachedFontRenderContext = null;

    if (renderer.dbgFrame != null) {
      renderer.dbgFrame.dispose();
    }
  }

  /** Returns the bounding rectangle of the given String, assuming it
   was rendered at the origin. See {@link #getBounds(CharSequence)
  getBounds(CharSequence)}. */
  public Rectangle2D getBounds(final String str) {
    return getBounds((CharSequence) str);
  }

  /** Returns the bounding rectangle of the given CharSequence,
   assuming it was rendered at the origin. The coordinate system of
   the returned rectangle is Java 2D's, with increasing Y
   coordinates in the downward direction. The relative coordinate
   (0, 0) in the returned rectangle corresponds to the baseline of
   the leftmost character of the rendered string, in similar
   fashion to the results returned by, for example, {@link
  java.awt.font.GlyphVector#getVisualBounds}. Most applications
   will use only the width and height of the returned Rectangle for
   the purposes of centering or justifying the String. It is not
   specified which Java 2D bounds ({@link
  java.awt.font.GlyphVector#getVisualBounds getVisualBounds},
   {@link java.awt.font.GlyphVector#getPixelBounds getPixelBounds},
   etc.) the returned bounds correspond to, although every effort
   is made to ensure an accurate bound. */
  public Rectangle2D getBounds(final CharSequence str) {
    // FIXME: this should be more optimized and use the glyph cache
    final Rect r = renderer.stringLocations.get(str);

    if (r != null) {
      final TextData data = (TextData) r.getUserData();

      // Reconstitute the Java 2D results based on the cached values
      return new Rectangle2D.Double(-data.origin().x, -data.origin().y,
          r.w(), r.h());
    }

    // Must return a Rectangle compatible with the layout algorithm --
    // must be idempotent
    return renderer.normalize(renderer.renderDelegate.getBounds(str, renderer.font,
        renderer.getFontRenderContext()));
  }

  /** Returns the pixel width of the given character. */
  public float getCharWidth(final char inChar) {
    return renderer.mGlyphProducer.getGlyphPixelWidth(inChar);
  }

  /** Changes the current color of this TextRenderer to the supplied
   one. The default color is opaque white.

   @param color the new color to use for rendering text
   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void setColor(final Color color) throws GLException {
    final boolean noNeedForFlush = (haveCachedColor && (cachedColor != null)
        && color.equals(cachedColor));

    if (!noNeedForFlush) {
      renderer.flushGlyphPipeline();
    }

    renderer.getBackingStore().setColor(color);
    haveCachedColor = true;
    cachedColor = color;
  }

  /** Changes the current color of this TextRenderer to the supplied
   one, where each component ranges from 0.0f - 1.0f. The alpha
   component, if used, does not need to be premultiplied into the
   color channels as described in the documentation for {@link
  com.jogamp.opengl.util.texture.Texture Texture}, although
   premultiplied colors are used internally. The default color is
   opaque white.

   @param r the red component of the new color
   @param g the green component of the new color
   @param b the blue component of the new color
   @param a the alpha component of the new color, 0.0f = completely
   transparent, 1.0f = completely opaque
   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void setColor(final float r, final float g, final float b, final float a)
      throws GLException {
    final boolean noNeedForFlush = (haveCachedColor && (cachedColor == null)
        && (r == cachedR) && (g == cachedG) && (b == cachedB)
        && (a == cachedA));

    if (!noNeedForFlush) {
      renderer.flushGlyphPipeline();
    }

    renderer.getBackingStore().setColor(r, g, b, a);
    haveCachedColor = true;
    cachedR = r;
    cachedG = g;
    cachedB = b;
    cachedA = a;
    cachedColor = null;
  }

  /**
   * Sets whether smoothing (i.e., GL_LINEAR filtering) is enabled
   * in the backing TextureRenderer of this NonCachingTextRenderer. A few
   * graphics cards do not behave well when this is enabled,
   * resulting in fuzzy text. Defaults to true.
   */
  public void setSmoothing(final boolean smoothing) {
    this.smoothing = smoothing;
    renderer.getBackingStore().setSmoothing(smoothing);
  }

  /**
   * Indicates whether smoothing is enabled in the backing
   * TextureRenderer of this NonCachingTextRenderer. A few graphics cards do
   * not behave well when this is enabled, resulting in fuzzy text.
   * Defaults to true.
   */
  public boolean getSmoothing() {
    return smoothing;
  }

  /**
   * Sets whether vertex arrays are being used internally for
   * rendering, or whether text is rendered using the OpenGL
   * immediate mode commands. This is provided as a concession for
   * certain graphics cards which have poor vertex array
   * performance. Defaults to true.
   */
  public void setUseVertexArrays(final boolean useVertexArrays) {
    this.useVertexArrays = useVertexArrays;
  }

  /**
   * Indicates whether vertex arrays are being used internally for
   * rendering, or whether text is rendered using the OpenGL
   * immediate mode commands. Defaults to true.
   */
  public final boolean getMyUseVertexArrays() {
    return useVertexArrays;
  }
}
