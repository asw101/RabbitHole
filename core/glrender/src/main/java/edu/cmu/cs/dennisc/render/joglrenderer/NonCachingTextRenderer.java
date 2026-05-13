package edu.cmu.cs.dennisc.render.joglrenderer;

import com.jogamp.common.util.PropertyAccess;
import com.jogamp.opengl.*;
import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.awt.TextureRenderer;
import com.jogamp.opengl.util.packrect.Rect;
import com.jogamp.opengl.util.packrect.RectVisitor;
import com.jogamp.opengl.util.packrect.RectanglePacker;
import jogamp.opengl.Debug;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A near clone of its superclass, TextRenderer, for the sole purpose of setting DISABLE_GLYPH_CACHE to false.
 * Turning off glyph caching changes the behavior in GlyphProducer.getGlyphs().
 *
 * Doing this corrects the handling of RTL fonts such as Hebrew and Arabic.
 *
 * Code that was not copied includes unused constructors and the RenderDelegate interface.
 * More could be done to reduce the code here, but I am keeping it simple, if verbose, so it can be updated with
 * future jogl versions.
 *
 * Suppressing checkstyle to ease comparison with TextRenderer.
 */
@SuppressWarnings("CheckStyle")
public class NonCachingTextRenderer extends TextRenderer {
  static final boolean DEBUG;

  static {
    Debug.initSingleton();
    DEBUG = PropertyAccess.isPropertyDefined("jogl.debug.NonCachingTextRenderer", true);
  }

  // These are occasionally useful for more in-depth debugging
  static final boolean DISABLE_GLYPH_CACHE = true;
  static final boolean DRAW_BBOXES = false;

  static final int kSize = 256;

  // Every certain number of render cycles, flush the strings which
  // haven't been used recently
  static final int CYCLES_PER_FLUSH = 100;

  // The amount of vertical dead space on the backing store before we
  // force a compaction
  private static final float MAX_VERTICAL_FRAGMENTATION = 0.7f;
  static final int kQuadsPerBuffer = 100;
  static final int kCoordsPerVertVerts = 3;
  static final int kCoordsPerVertTex = 2;
  static final int kVertsPerQuad = 4;
  static final int kTotalBufferSizeVerts = kQuadsPerBuffer * kVertsPerQuad;
  static final int kTotalBufferSizeCoordsVerts = kQuadsPerBuffer * kVertsPerQuad * kCoordsPerVertVerts;
  static final int kTotalBufferSizeCoordsTex = kQuadsPerBuffer * kVertsPerQuad * kCoordsPerVertTex;
  static final int kTotalBufferSizeBytesVerts = kTotalBufferSizeCoordsVerts * 4;
  static final int kTotalBufferSizeBytesTex = kTotalBufferSizeCoordsTex * 4;
  static final int kSizeInBytes_OneVertices_VertexData = kCoordsPerVertVerts * 4;
  static final int kSizeInBytes_OneVertices_TexData = kCoordsPerVertTex * 4;
  final Font font;
  private final boolean antialiased;
  private final boolean useFractionalMetrics;

  // Whether we're attempting to use automatic mipmap generation support
  boolean mipmap;
  RectanglePacker packer;
  boolean haveMaxSize;
  final TextRenderer.RenderDelegate renderDelegate;
  private TextureRenderer cachedBackingStore;
  private Graphics2D cachedGraphics;
  private FontRenderContext cachedFontRenderContext;
  final Map<String, Rect> stringLocations = new HashMap<String, Rect>();
  final TextRendererGlyphProducer mGlyphProducer;

  int numRenderCycles;

  // Need to keep track of whether we're in a beginRendering() /
  // endRendering() cycle so we can re-enter the exact same state if
  // we have to reallocate the backing store
  boolean inBeginEndPair;
  boolean isOrthoMode;
  int beginRenderingWidth;
  int beginRenderingHeight;
  boolean beginRenderingDepthTestDisabled;

  // For resetting the color after disposal of the old backing store
  boolean haveCachedColor;
  float cachedR;
  float cachedG;
  float cachedB;
  float cachedA;
  Color cachedColor;
  boolean needToResetColor;

  // For debugging only
  Frame dbgFrame;

  // Debugging purposes only
  boolean debugged;
  TextRendererQuadRenderer mPipelinedQuadRenderer;
  final TextRendererPipeline pipeline;

  //emzic: added boolean flag
  private boolean useVertexArrays = true;

  //emzic: added boolean flag
  boolean isExtensionAvailable_GL_VERSION_1_5;
  private boolean checkFor_isExtensionAvailable_GL_VERSION_1_5;

  // Whether GL_LINEAR filtering is enabled for the backing store
  boolean smoothing = true;

  /** Creates a new TextRenderer with the given font, using no
   antialiasing or fractional metrics, and the default
   RenderDelegate. Equivalent to <code>TextRenderer(font, false,
   false)</code>.

   @param font the font to render with
   */
  public NonCachingTextRenderer(final Font font) {
    this(font, false, false, null, false);
  }

  /** Creates a new TextRenderer with the given Font, specified font
   properties, and given RenderDelegate. The
   <code>antialiased</code> and <code>useFractionalMetrics</code>
   flags provide control over the same properties at the Java 2D
   level. The <code>renderDelegate</code> provides more control
   over the text rendered. If <CODE>mipmap</CODE> is true, attempts
   to use OpenGL's automatic mipmap generation for better smoothing
   when rendering the TextureRenderer's contents at a distance.

   @param font the font to render with
   @param antialiased whether to use antialiased fonts
   @param useFractionalMetrics whether to use fractional font
   metrics at the Java 2D level
   @param renderDelegate the render delegate to use to draw the
   text's bitmap, or null to use the default one
   @param mipmap whether to attempt use of automatic mipmap generation
   */
  public NonCachingTextRenderer(final Font font, final boolean antialiased,
                                final boolean useFractionalMetrics, TextRenderer.RenderDelegate renderDelegate,
                                final boolean mipmap) {
    super(font, antialiased, useFractionalMetrics, renderDelegate, mipmap);
    this.font = font;
    this.antialiased = antialiased;
    this.useFractionalMetrics = useFractionalMetrics;
    this.mipmap = mipmap;

    // FIXME: consider adjusting the size based on font size
    // (it will already automatically resize if necessary)
    packer = new RectanglePacker(new Manager(this), kSize, kSize);

    if (renderDelegate == null) {
      renderDelegate = new DefaultRenderDelegate();
    }

    this.renderDelegate = renderDelegate;

    mGlyphProducer = new TextRendererGlyphProducer(font.getNumGlyphs(), this);
    pipeline = new TextRendererPipeline(this);
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
    final Rect r = stringLocations.get(str);

    if (r != null) {
      final TextData data = (TextData) r.getUserData();

      // Reconstitute the Java 2D results based on the cached values
      return new Rectangle2D.Double(-data.origin().x, -data.origin().y,
          r.w(), r.h());
    }

    // Must return a Rectangle compatible with the layout algorithm --
    // must be idempotent
    return normalize(renderDelegate.getBounds(str, font,
        getFontRenderContext()));
  }

  /** Returns the Font this renderer is using. */
  public Font getFont() {
    return font;
  }

  /** Returns a FontRenderContext which can be used for external
   text-related size computations. This object should be considered
   transient and may become invalidated between beginRendering
   endRendering pairs. */
  public FontRenderContext getFontRenderContext() {
    if (cachedFontRenderContext == null) {
      cachedFontRenderContext = getGraphics2D().getFontRenderContext();
    }

    return cachedFontRenderContext;
  }

  /** Begins rendering with this {@link TextRenderer TextRenderer}
   into the current OpenGL drawable, pushing the projection and
   modelview matrices and some state bits and setting up a
   two-dimensional orthographic projection with (0, 0) as the
   lower-left coordinate and (width, height) as the upper-right
   coordinate. Binds and enables the internal OpenGL texture
   object, sets the texture environment mode to GL_MODULATE, and
   changes the current color to the last color set with this
   TextRenderer via {@link #setColor setColor}. This method
   disables the depth test and is equivalent to
   beginRendering(width, height, true).

   @param width the width of the current on-screen OpenGL drawable
   @param height the height of the current on-screen OpenGL drawable
   @throws com.jogamp.opengl.GLException If an OpenGL context is not current when this method is called
   */
  public void beginRendering(final int width, final int height) throws GLException {
    beginRendering(width, height, true);
  }

  /** Begins rendering with this {@link TextRenderer TextRenderer}
   into the current OpenGL drawable, pushing the projection and
   modelview matrices and some state bits and setting up a
   two-dimensional orthographic projection with (0, 0) as the
   lower-left coordinate and (width, height) as the upper-right
   coordinate. Binds and enables the internal OpenGL texture
   object, sets the texture environment mode to GL_MODULATE, and
   changes the current color to the last color set with this
   TextRenderer via {@link #setColor setColor}. Disables the depth
   test if the disableDepthTest argument is true.

   @param width the width of the current on-screen OpenGL drawable
   @param height the height of the current on-screen OpenGL drawable
   @param disableDepthTest whether to disable the depth test
   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void beginRendering(final int width, final int height, final boolean disableDepthTest)
      throws GLException {
    pipeline.beginRendering(true, width, height, disableDepthTest);
  }

  /** Begins rendering of 2D text in 3D with this {@link TextRenderer
  TextRenderer} into the current OpenGL drawable. Assumes the end
   user is responsible for setting up the modelview and projection
   matrices, and will render text using the {@link #draw3D draw3D}
   method. This method pushes some OpenGL state bits, binds and
   enables the internal OpenGL texture object, sets the texture
   environment mode to GL_MODULATE, and changes the current color
   to the last color set with this TextRenderer via {@link
  #setColor setColor}.

   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void begin3DRendering() throws GLException {
    pipeline.beginRendering(false, 0, 0, false);
  }

  /** Changes the current color of this TextRenderer to the supplied
   one. The default color is opaque white.

   @param color the new color to use for rendering text
   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void setColor(final Color color) throws GLException {
    final boolean noNeedForFlush = (haveCachedColor && (cachedColor != null) &&
        color.equals(cachedColor));

    if (!noNeedForFlush) {
      flushGlyphPipeline();
    }

    getBackingStore().setColor(color);
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
    final boolean noNeedForFlush = (haveCachedColor && (cachedColor == null) &&
        (r == cachedR) && (g == cachedG) && (b == cachedB) &&
        (a == cachedA));

    if (!noNeedForFlush) {
      flushGlyphPipeline();
    }

    getBackingStore().setColor(r, g, b, a);
    haveCachedColor = true;
    cachedR = r;
    cachedG = g;
    cachedB = b;
    cachedA = a;
    cachedColor = null;
  }

  /** Draws the supplied CharSequence at the desired location using
   the renderer's current color. The baseline of the leftmost
   character is at position (x, y) specified in OpenGL coordinates,
   where the origin is at the lower-left of the drawable and the Y
   coordinate increases in the upward direction.

   @param str the string to draw
   @param x the x coordinate at which to draw
   @param y the y coordinate at which to draw
   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void draw(final CharSequence str, final int x, final int y) throws GLException {
    draw3D(str, x, y, 0, 1);
  }

  /** Draws the supplied String at the desired location using the
   renderer's current color. See {@link #draw(CharSequence, int,
      int) draw(CharSequence, int, int)}. */
  public void draw(final String str, final int x, final int y) throws GLException {
    draw3D(str, x, y, 0, 1);
  }

  /** Draws the supplied CharSequence at the desired 3D location using
   the renderer's current color. The baseline of the leftmost
   character is placed at position (x, y, z) in the current
   coordinate system.

   @param str the string to draw
   @param x the x coordinate at which to draw
   @param y the y coordinate at which to draw
   @param z the z coordinate at which to draw
   @param scaleFactor a uniform scale factor applied to the width and height of the drawn rectangle
   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void draw3D(final CharSequence str, final float x, final float y, final float z,
                     final float scaleFactor) {
    pipeline.internal_draw3D(str, x, y, z, scaleFactor);
  }

  /** Draws the supplied String at the desired 3D location using the
   renderer's current color. See {@link #draw3D(CharSequence,
      float, float, float, float) draw3D(CharSequence, float, float,
  float, float)}. */
  public void draw3D(final String str, final float x, final float y, final float z, final float scaleFactor) {
    pipeline.internal_draw3D(str, x, y, z, scaleFactor);
  }

  /** Returns the pixel width of the given character. */
  public float getCharWidth(final char inChar) {
    return mGlyphProducer.getGlyphPixelWidth(inChar);
  }

  /** Causes the TextRenderer to flush any internal caches it may be
   maintaining and draw its rendering results to the screen. This
   should be called after each call to draw() if you are setting
   OpenGL state such as the modelview matrix between calls to
   draw(). */
  public void flush() {
    flushGlyphPipeline();
  }

  /** Ends a render cycle with this {@link TextRenderer TextRenderer}.
   Restores the projection and modelview matrices as well as
   several OpenGL state bits. Should be paired with beginRendering.

   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void endRendering() throws GLException {
    pipeline.endRendering(true);
  }

  /** Ends a 3D render cycle with this {@link TextRenderer TextRenderer}.
   Restores several OpenGL state bits. Should be paired with {@link
  #begin3DRendering begin3DRendering}.

   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void end3DRendering() throws GLException {
    pipeline.endRendering(false);
  }

  /** Disposes of all resources this TextRenderer is using. It is not
   valid to use the TextRenderer after this method is called.

   @throws GLException If an OpenGL context is not current when this method is called
   */
  public void dispose() throws GLException {
    if( null != mPipelinedQuadRenderer ) {
      mPipelinedQuadRenderer.dispose();
    }
    packer.dispose();
    packer = null;
    cachedBackingStore = null;
    cachedGraphics = null;
    cachedFontRenderContext = null;

    if (dbgFrame != null) {
      dbgFrame.dispose();
    }
  }

  //----------------------------------------------------------------------
  // Internals only below this point
  //

  static Rectangle2D preNormalize(final Rectangle2D src) {
    // Need to round to integer coordinates
    // Also give ourselves a little slop around the reported
    // bounds of glyphs because it looks like neither the visual
    // nor the pixel bounds works perfectly well
    final int minX = (int) Math.floor(src.getMinX()) - 1;
    final int minY = (int) Math.floor(src.getMinY()) - 1;
    final int maxX = (int) Math.ceil(src.getMaxX()) + 1;
    final int maxY = (int) Math.ceil(src.getMaxY()) + 1;
    return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
  }


  Rectangle2D normalize(final Rectangle2D src) {
    // Give ourselves a boundary around each entity on the backing
    // store in order to prevent bleeding of nearby Strings due to
    // the fact that we use linear filtering

    // NOTE that this boundary is quite heuristic and is related
    // to how far away in 3D we may view the text --
    // heuristically, 1.5% of the font's height
    final int boundary = (int) Math.max(1, 0.015 * font.getSize());

    return new Rectangle2D.Double((int) Math.floor(src.getMinX() - boundary),
        (int) Math.floor(src.getMinY() - boundary),
        (int) Math.ceil(src.getWidth() + 2 * boundary),
        (int) Math.ceil(src.getHeight()) + 2 * boundary);
  }

  TextureRenderer getBackingStore() {
    final TextureRenderer renderer = (TextureRenderer) packer.getBackingStore();

    if (renderer != cachedBackingStore) {
      // Backing store changed since last time; discard any cached Graphics2D
      if (cachedGraphics != null) {
        cachedGraphics.dispose();
        cachedGraphics = null;
        cachedFontRenderContext = null;
      }

      cachedBackingStore = renderer;
    }

    return cachedBackingStore;
  }

  Graphics2D getGraphics2D() {
    final TextureRenderer renderer = getBackingStore();

    if (cachedGraphics == null) {
      cachedGraphics = renderer.createGraphics();

      // Set up composite, font and rendering hints
      cachedGraphics.setComposite(AlphaComposite.Src);
      cachedGraphics.setColor(Color.WHITE);
      cachedGraphics.setFont(font);
      cachedGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
          (antialiased ? RenderingHints.VALUE_TEXT_ANTIALIAS_ON
              : RenderingHints.VALUE_TEXT_ANTIALIAS_OFF));
      cachedGraphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS,
          (useFractionalMetrics
              ? RenderingHints.VALUE_FRACTIONALMETRICS_ON
              : RenderingHints.VALUE_FRACTIONALMETRICS_OFF));
    }

    return cachedGraphics;
  }


  void flushGlyphPipeline() {
    pipeline.flushGlyphPipeline();
  }

  void draw3D_ROBUST(final CharSequence str, final float x, final float y, final float z,
                             final float scaleFactor) {
    pipeline.draw3D_ROBUST(str, x, y, z, scaleFactor);
  }

  void clearUnusedEntries() {
    final java.util.List<Rect> deadRects = new ArrayList<Rect>();

    // Iterate through the contents of the backing store, removing
    // text strings that haven't been used recently
    packer.visit(new RectVisitor() {
      @Override
      public void visit(final Rect rect) {
        final TextData data = (TextData) rect.getUserData();

        if (data.used()) {
          data.clearUsed();
        } else {
          deadRects.add(rect);
        }
      }
    });

    for (final Rect r : deadRects) {
      packer.remove(r);
      stringLocations.remove(((TextData) r.getUserData()).string());

      final int unicodeToClearFromCache = ((TextData) r.getUserData()).unicodeID;

      if (unicodeToClearFromCache > 0) {
        mGlyphProducer.clearCacheEntry(unicodeToClearFromCache);
      }
    }

    // If we removed dead rectangles this cycle, try to do a compaction
    final float frag = packer.verticalFragmentationRatio();

    if (!deadRects.isEmpty() && (frag > MAX_VERTICAL_FRAGMENTATION)) {
      if (DEBUG) {
        System.err.println(
            "Compacting TextRenderer backing store due to vertical fragmentation " +
                frag);
      }

      packer.compact();
    }

    if (DEBUG) {
      getBackingStore().markDirty(0, 0, getBackingStore().getWidth(),
          getBackingStore().getHeight());
    }
  }

  //----------------------------------------------------------------------
  // Glyph-by-glyph rendering support
  //

  // A temporary to prevent excessive garbage creation
  final char[] singleUnicode = new char[1];

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

  /**
   * Sets whether smoothing (i.e., GL_LINEAR filtering) is enabled
   * in the backing TextureRenderer of this NonCachingTextRenderer. A few
   * graphics cards do not behave well when this is enabled,
   * resulting in fuzzy text. Defaults to true.
   */
  public void setSmoothing(final boolean smoothing) {
    this.smoothing = smoothing;
    getBackingStore().setSmoothing(smoothing);
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

  final boolean is15Available(final GL gl) {
    if (!checkFor_isExtensionAvailable_GL_VERSION_1_5) {
      isExtensionAvailable_GL_VERSION_1_5 = gl.isExtensionAvailable(GLExtensions.VERSION_1_5);
      checkFor_isExtensionAvailable_GL_VERSION_1_5 = true;
    }
    return isExtensionAvailable_GL_VERSION_1_5;
  }
}
