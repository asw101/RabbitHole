package edu.cmu.cs.dennisc.render.gl.imp.adapters;

import org.junit.Assume;
import java.awt.GraphicsEnvironment;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import edu.cmu.cs.dennisc.color.Color4f;
import edu.cmu.cs.dennisc.render.gl.imp.PickContext;
import edu.cmu.cs.dennisc.render.gl.imp.RenderContext;
import edu.cmu.cs.dennisc.render.gl.imp.testing.RecordingGL2;
import edu.cmu.cs.dennisc.scenegraph.AmbientLight;
import edu.cmu.cs.dennisc.scenegraph.OrthographicCamera;
import edu.cmu.cs.dennisc.scenegraph.PointLight;
import edu.cmu.cs.dennisc.scenegraph.SpotLight;
import edu.cmu.cs.dennisc.scenegraph.SymmetricPerspectiveCamera;
import edu.cmu.cs.dennisc.scenegraph.TriangleArray;
import edu.cmu.cs.dennisc.scenegraph.Transformable;
import edu.cmu.cs.dennisc.scenegraph.Vertex;
import org.alice.math.immutable.AffineMatrix4x4;
import org.alice.math.immutable.AngleInRadians;
import org.alice.math.immutable.ClippedZPlane;
import org.alice.math.immutable.Point3;
import org.alice.math.immutable.Ray;
import org.alice.math.immutable.Vector3f;
import org.junit.Assert;
import org.junit.Test;

import java.awt.Rectangle;
import java.util.List;

public class CameraAndLightMathTest {

  @org.junit.Before
  public void skipIfHeadless() {
    Assume.assumeTrue("Requires display", !GraphicsEnvironment.isHeadless());
  }
  private static final double EPSILON = 0.000001;

  @Test
  public void symmetricPerspectiveCameraComputesLetterboxedViewportRayAndProjection() {
    SymmetricPerspectiveCamera camera = new SymmetricPerspectiveCamera();
    camera.nearClippingPlaneDistance.setValue(1.0);
    camera.farClippingPlaneDistance.setValue(11.0);
    camera.horizontalViewingAngle.setValue(new AngleInRadians(1.2));
    camera.verticalViewingAngle.setValue(new AngleInRadians(0.8));

    GlrSymmetricPerspectiveCamera adapter = new GlrSymmetricPerspectiveCamera();
    adapter.initialize(camera);
    adapter.propertyChanged(camera.nearClippingPlaneDistance);
    adapter.propertyChanged(camera.farClippingPlaneDistance);
    adapter.propertyChanged(camera.horizontalViewingAngle);
    adapter.propertyChanged(camera.verticalViewingAngle);

    Rectangle actualViewport = adapter.getActualViewport(200, 100);
    Assert.assertEquals(new Rectangle(25, 0, 150, 100), actualViewport);

    Ray centerRay = adapter.getRayAtViewportPixel(100, 50, actualViewport);
    Assert.assertEquals(0.0, centerRay.origin().x(), EPSILON);
    Assert.assertEquals(0.0, centerRay.origin().y(), EPSILON);
    Assert.assertEquals(-1.0, centerRay.origin().z(), EPSILON);
    Assert.assertEquals(0.0, centerRay.direction().x(), EPSILON);
    Assert.assertEquals(0.0, centerRay.direction().y(), EPSILON);
    Assert.assertEquals(-1.0, centerRay.direction().z(), EPSILON);

    org.alice.math.immutable.Matrix4x4 projection = adapter.getActualProjectionMatrix(actualViewport);
    double f = 1.0 / Math.tan(0.8 / 2.0);
    double[] projectionArray = projection.asColumnMajorArray16();
    Assert.assertEquals(f / 1.5, projectionArray[0], EPSILON);
    Assert.assertEquals(f, projectionArray[5], EPSILON);
    Assert.assertEquals((11.0 + 1.0) / (1.0 - 11.0), projectionArray[10], EPSILON);
    Assert.assertEquals((2.0 * 11.0 * 1.0) / (1.0 - 11.0), projectionArray[14], EPSILON);
    Assert.assertNotNull(camera.getEffectiveHorizontalViewingAngle());
    Assert.assertNotNull(camera.getEffectiveVerticalViewingAngle());

    RecordingGL2 gl = new RecordingGL2();
    RenderContext context = new RenderContext();
    context.setGL(gl);
    adapter.setupProjection(context, actualViewport);

    Assert.assertEquals(1, gl.calls("glMultMatrixd").size());
    Assert.assertArrayEquals(projectionArray, gl.doubleBufferArgument("glMultMatrixd"), EPSILON);
  }

  @Test
  public void orthographicCameraComputesPicturePlaneRayAndProjection() {
    OrthographicCamera camera = new OrthographicCamera();
    camera.picturePlane.setValue(ClippedZPlane.createWithHeight(2.0));
    camera.nearClippingPlaneDistance.setValue(0.5);
    camera.farClippingPlaneDistance.setValue(5.0);

    GlrOrthographicCamera adapter = new GlrOrthographicCamera();
    adapter.initialize(camera);
    adapter.propertyChanged(camera.nearClippingPlaneDistance);
    adapter.propertyChanged(camera.farClippingPlaneDistance);

    Rectangle viewport = new Rectangle(0, 0, 200, 100);
    Ray ray = adapter.getRayAtViewportPixel(200, 100, viewport);
    Assert.assertEquals(2.0, ray.origin().x(), EPSILON);
    Assert.assertEquals(1.0, ray.origin().y(), EPSILON);
    Assert.assertEquals(0.5, ray.origin().z(), EPSILON);
    Assert.assertEquals(0.0, ray.direction().x(), EPSILON);
    Assert.assertEquals(0.0, ray.direction().y(), EPSILON);
    Assert.assertEquals(-1.0, ray.direction().z(), EPSILON);

    org.alice.math.immutable.Matrix4x4 projection = adapter.getActualProjectionMatrix(viewport);
    double[] projectionArray = projection.asColumnMajorArray16();
    Assert.assertEquals(0.5, projectionArray[0], EPSILON);
    Assert.assertEquals(1.0, projectionArray[5], EPSILON);
    Assert.assertEquals(-2.0 / (5.0 - 0.5), projectionArray[10], EPSILON);

    RecordingGL2 gl = new RecordingGL2();
    RenderContext context = new RenderContext();
    context.setGL(gl);
    adapter.setupProjection(context, viewport);

    Assert.assertTrue(gl.wasCalledWith("glOrtho", -2.0, 2.0, -1.0, 1.0, 0.5, 5.0));
  }

  @Test
  public void ambientPointAndSpotLightsTranslatePropertiesIntoMathValues() {
    RecordingGL2 gl = new RecordingGL2();
    RenderContext context = new RenderContext();
    context.setGL(gl);
    context.setGlobalBrightness(0.5f);

    AmbientLight ambientLight = new AmbientLight();
    ambientLight.color.setValue(new Color4f(0.2f, 0.4f, 0.6f, 1.0f));
    ambientLight.brightness.setValue(0.5f);
    GlrAmbientLight ambientAdapter = new GlrAmbientLight();
    ambientAdapter.initialize(ambientLight);
    ambientAdapter.propertyChanged(ambientLight.color);
    ambientAdapter.propertyChanged(ambientLight.brightness);
    context.beginAffectorSetup();
    ambientAdapter.setupAffectors(context);
    Assert.assertArrayEquals(new float[] {0.1f, 0.2f, 0.3f, 1.0f}, context.getAmbient(new float[4]), 0.00001f);

    Transformable parent = new Transformable();
    parent.localTransformation.setValue(AffineMatrix4x4.createTranslation(2.0, 3.0, 4.0));

    PointLight pointLight = new PointLight();
    pointLight.setParent(parent);
    pointLight.color.setValue(new Color4f(0.5f, 0.25f, 1.0f, 1.0f));
    pointLight.brightness.setValue(0.4f);
    pointLight.constantAttenuation.setValue(2.0);
    pointLight.linearAttenuation.setValue(3.0);
    pointLight.quadraticAttenuation.setValue(4.0);

    GlrPointLight<PointLight> pointAdapter = new GlrPointLight<>();
    pointAdapter.initialize(pointLight);
    pointAdapter.propertyChanged(pointLight.color);
    pointAdapter.propertyChanged(pointLight.brightness);
    pointAdapter.propertyChanged(pointLight.constantAttenuation);
    pointAdapter.propertyChanged(pointLight.linearAttenuation);
    pointAdapter.propertyChanged(pointLight.quadraticAttenuation);
    pointAdapter.setupAffectors(context);

    Assert.assertArrayEquals(new float[] {2.0f, 3.0f, 4.0f, 1.0f}, pointAdapter.getPosition(new float[4]), 0.00001f);
    Assert.assertEquals(2.0f, pointAdapter.getConstantAttenuation(), 0.0f);
    Assert.assertEquals(3.0f, pointAdapter.getLinearAttenuation(), 0.0f);
    Assert.assertEquals(4.0f, pointAdapter.getQuadraticAttenuation(), 0.0f);
    Assert.assertTrue(gl.wasCalledWith("glEnable", GL2.GL_LIGHT0));
    Assert.assertArrayEquals(new float[] {0.1f, 0.05f, 0.2f, 0.2f}, gl.floatArrayArgument("glLightfv", GL2.GL_DIFFUSE), 0.00001f);
    Assert.assertArrayEquals(new float[] {2.0f, 3.0f, 4.0f, 1.0f}, gl.floatArrayArgument("glLightfv", GL2.GL_POSITION), 0.00001f);
    Assert.assertTrue(gl.wasCalledWith("glLightf", GL2.GL_LIGHT0, GL2.GL_CONSTANT_ATTENUATION, 2.0f));
    Assert.assertTrue(gl.wasCalledWith("glLightf", GL2.GL_LIGHT0, GL2.GL_LINEAR_ATTENUATION, 3.0f));
    Assert.assertTrue(gl.wasCalledWith("glLightf", GL2.GL_LIGHT0, GL2.GL_QUADRATIC_ATTENUATION, 4.0f));

    SpotLight spotLight = new SpotLight();
    spotLight.setParent(parent);
    spotLight.outerBeamAngle.setValue(new AngleInRadians(1.0));
    GlrSpotLight spotAdapter = new GlrSpotLight();
    spotAdapter.initialize(spotLight);
    spotAdapter.propertyChanged(spotLight.outerBeamAngle);
    Assert.assertArrayEquals(new float[] {0.0f, 0.0f, 1.0f}, spotAdapter.getSpotDirection(new float[3]), 0.00001f);
    Assert.assertEquals((float) Math.toDegrees(1.0), spotAdapter.getSpotCutoff(), 0.0001f);
  }

  @Test
  public void triangleArrayAdapterTracksAlphaBlendAndInvokesRenderAndPickLoops() {
    TriangleArray geometry = new TriangleArray();
    GlrTriangleArray adapter = new GlrTriangleArray();
    adapter.initialize(geometry);

    Vertex[] vertices = new Vertex[] {
        Vertex.createXYZRGBA(0.0, 0.0, 0.0, 1.0f, 0.0f, 0.0f, 0.5f),
        Vertex.createXYZIJK(1.0, 0.0, 0.0, 0.0f, 0.0f, 1.0f),
        Vertex.createXYZ(0.0, 1.0, 0.0)
    };
    geometry.vertices.setValue(vertices);
    adapter.propertyChanged(geometry.vertices);

    Assert.assertTrue(adapter.isAlphaBlended());
    Assert.assertSame(vertices[1], adapter.accessVertexAt(1));

    RecordingGL2 renderGl = new RecordingGL2();
    RenderContext renderContext = new RenderContext();
    renderContext.setGL(renderGl);
    renderContext.setIsShadingEnabled(true);
    adapter.renderPrimitive(renderContext, GL.GL_TRIANGLES);

    Assert.assertTrue(renderGl.wasCalledWith("glBegin", GL.GL_TRIANGLES));
    Assert.assertEquals(3, renderGl.calls("glVertex3d").size());
    Assert.assertEquals(1, renderGl.calls("glColor4f").size());
    Assert.assertEquals(3, renderGl.calls("glNormal3f").size());
    Assert.assertTrue(renderGl.wasCalledWith("glEnd"));

    RecordingGL2 pickGl = new RecordingGL2();
    PickContext pickContext = new PickContext(true);
    pickContext.setGL(pickGl);
    adapter.pickPrimitive(pickContext, GL.GL_TRIANGLES);

    Assert.assertTrue(pickGl.wasCalledWith("glPushName", -1));
    Assert.assertTrue(pickGl.wasCalledWith("glBegin", GL.GL_TRIANGLES));
    Assert.assertEquals(3, pickGl.calls("glVertex3d").size());
    Assert.assertTrue(pickGl.wasCalledWith("glPopName"));
  }

}
