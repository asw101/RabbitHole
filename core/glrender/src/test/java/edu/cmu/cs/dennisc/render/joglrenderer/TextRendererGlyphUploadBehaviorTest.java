package edu.cmu.cs.dennisc.render.joglrenderer;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;


import com.jogamp.opengl.util.packrect.Rect;
import org.junit.Test;
import sun.misc.Unsafe;

import java.awt.Font;
import java.awt.font.GlyphVector;
import java.lang.reflect.Field;

import static org.junit.Assert.*;

public class TextRendererGlyphUploadBehaviorTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }


  private static final Font FONT = new Font(Font.DIALOG, Font.PLAIN, 18);
  private static final Unsafe UNSAFE = lookupUnsafe();

  @Test
  public void draw3DUploadsGlyphRegistersItAndEmitsQuadVertices() throws Exception {
    NonCachingTextRenderer renderer = HeadlessTextRendererFactory.createRenderer(FONT);
    RecordingQuadRenderer quadRenderer = allocate(RecordingQuadRenderer.class);
    renderer.mPipelinedQuadRenderer = quadRenderer;

    GlyphVector glyphVector = FONT.createGlyphVector(renderer.getFontRenderContext(), new char[]{'A'});
    int glyphCode = glyphVector.getGlyphCode(0);
    TextRendererGlyph glyph = new TextRendererGlyph('A', glyphCode, glyphVector.getGlyphMetrics(0).getAdvance(), glyphVector, renderer.mGlyphProducer, renderer);

    float advance = glyph.draw3D(10.0f, 20.0f, 30.0f, 2.0f);

    assertEquals(glyphVector.getGlyphMetrics(0).getAdvance(), advance, 0.0001f);
    assertEquals(4, quadRenderer.texCoordCalls);
    assertEquals(4, quadRenderer.vertexCalls);
    assertSame(glyph, renderer.mGlyphProducer.glyphCache[glyphCode]);
    assertTrue(((HeadlessTextRendererFactory.HeadlessTextureRenderer) renderer.getBackingStore()).getMarkDirtyCount() > 0);

    Field rectField = TextRendererGlyph.class.getDeclaredField("glyphRectForTextureMapping");
    rectField.setAccessible(true);
    Rect rect = (Rect) rectField.get(glyph);
    assertNotNull(rect);
    assertTrue(((TextData) rect.getUserData()).used());

    Field vectorField = TextRendererGlyph.class.getDeclaredField("singleUnicodeGlyphVector");
    vectorField.setAccessible(true);
    assertNull(vectorField.get(glyph));
  }

  private static <T> T allocate(Class<T> type) {
    try {
      return type.cast(UNSAFE.allocateInstance(type));
    } catch (InstantiationException e) {
      throw new AssertionError(e);
    }
  }

  private static Unsafe lookupUnsafe() {
    try {
      Field field = Unsafe.class.getDeclaredField("theUnsafe");
      field.setAccessible(true);
      return (Unsafe) field.get(null);
    } catch (ReflectiveOperationException e) {
      throw new AssertionError(e);
    }
  }

  private static final class RecordingQuadRenderer extends TextRendererQuadRenderer {
    private int texCoordCalls;
    private int vertexCalls;

    private RecordingQuadRenderer() {
      super(null);
    }

    @Override
    public void glTexCoord2f(float v, float v1) {
      this.texCoordCalls++;
    }

    @Override
    public void glVertex3f(float inX, float inY, float inZ) {
      this.vertexCalls++;
    }
  }
}
