package org.lgna.croquet.icon;

import org.junit.Before;
import org.junit.Test;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

public class ImageIconFactoryTest {

  private BufferedImage image;
  private Icon icon;
  private ImageIconFactory factoryFromImage;

  @Before
  public void setUp() {
    image = new BufferedImage(24, 18, BufferedImage.TYPE_INT_ARGB);
    icon = new ImageIcon(image);
    factoryFromImage = new ImageIconFactory(image);
  }

  // ── Construction ───────────────────────────────────────────────────

  @Test
  public void constructor_withIcon() {
    ImageIconFactory factory = new ImageIconFactory(icon);

    assertNotNull(factory);
    assertSame(icon, factory.getSourceImageIcon());
  }

  @Test
  public void constructor_withImage() {
    assertNotNull(factoryFromImage);
    assertNotNull(factoryFromImage.getSourceImageIcon());
  }

  @Test
  public void constructorCount_andSignatures_viaReflection() {
    Constructor<?>[] constructors = ImageIconFactory.class.getDeclaredConstructors();
    Set<Class<?>> parameterTypes = new HashSet<Class<?>>();
    for (Constructor<?> constructor : constructors) {
      assertEquals(1, constructor.getParameterTypes().length);
      parameterTypes.add(constructor.getParameterTypes()[0]);
    }

    assertEquals(3, constructors.length);
    assertEquals(new HashSet<Class<?>>(Arrays.asList(Icon.class, URL.class, Image.class)),
        parameterTypes);
  }

  // ── Hierarchy ──────────────────────────────────────────────────────

  @Test
  public void class_hierarchy() {
    assertTrue(AbstractSingleSourceImageIconFactory.class.isAssignableFrom(ImageIconFactory.class));
    assertFalse(Modifier.isAbstract(ImageIconFactory.class.getModifiers()));
  }

  // ── Methods ────────────────────────────────────────────────────────

  @Test
  public void createIcon_methodExists() throws Exception {
    Method method = ImageIconFactory.class.getDeclaredMethod("createIcon", Dimension.class);

    assertNotNull(method);
    assertTrue(Modifier.isProtected(method.getModifiers()));
  }

  @Test
  public void getDefaultSize_methodExists() throws Exception {
    Method method = ImageIconFactory.class.getMethod("getDefaultSize", Dimension.class);

    assertNotNull(method);
    assertTrue(Modifier.isPublic(method.getModifiers()));
  }

  // ── Behavior ───────────────────────────────────────────────────────

  @Test
  public void multipleInstances_areIndependent() {
    ImageIconFactory first = new ImageIconFactory(new BufferedImage(12, 8, BufferedImage.TYPE_INT_ARGB));
    ImageIconFactory second = new ImageIconFactory(new BufferedImage(30, 20, BufferedImage.TYPE_INT_ARGB));

    assertNotSame(first, second);
    assertEquals(new Dimension(12, 8), first.getDefaultSize(new Dimension(1, 1)));
    assertEquals(new Dimension(30, 20), second.getDefaultSize(new Dimension(1, 1)));
  }

  @Test
  public void nullIcon_handling() {
    ImageIconFactory nullFactory = new ImageIconFactory((Icon) null);
    Dimension fallback = new Dimension(9, 11);

    assertNull(nullFactory.getSourceImageIcon());
    assertSame(fallback, nullFactory.getDefaultSize(fallback));

    Icon generated = nullFactory.getIconExactSize(new Dimension(7, 5));
    assertNotNull(generated);
    assertEquals(7, generated.getIconWidth());
    assertEquals(5, generated.getIconHeight());
  }
}
