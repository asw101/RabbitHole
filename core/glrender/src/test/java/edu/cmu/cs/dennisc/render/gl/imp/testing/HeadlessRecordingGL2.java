package edu.cmu.cs.dennisc.render.gl.imp.testing;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

public class HeadlessRecordingGL2 extends RecordingGL2 {
  private final Map<Integer, Integer> integerValues = new HashMap<>();
  private final Map<String, Boolean> extensions = new HashMap<>();

  public HeadlessRecordingGL2 withInteger(int pname, int value) {
    this.integerValues.put(pname, value);
    return this;
  }

  public HeadlessRecordingGL2 withExtension(String name, boolean available) {
    this.extensions.put(name, available);
    return this;
  }

  @Override
  public GL getGL() {
    return this;
  }

  @Override
  public GL2 getGL2() {
    return this;
  }

  @Override
  public void glGetIntegerv(int pname, int[] params, int offset) {
    super.glGetIntegerv(pname, params, offset);
    params[offset] = this.integerValues.getOrDefault(pname, 0);
  }

  @Override
  public void glGetIntegerv(int pname, IntBuffer params) {
    super.glGetIntegerv(pname, params);
    if (params.remaining() > 0) {
      params.put(params.position(), this.integerValues.getOrDefault(pname, 0));
    }
  }

  @Override
  public boolean isExtensionAvailable(String name) {
    return this.extensions.getOrDefault(name, false);
  }
}
