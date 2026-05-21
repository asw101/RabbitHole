package org.alice.ide.croquet.codecs;

import edu.cmu.cs.dennisc.codec.InputStreamBinaryDecoder;
import edu.cmu.cs.dennisc.codec.OutputStreamBinaryEncoder;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;

public class SingletonCodecCoverageTest {
  public static final class SampleSingleton {
    private static final SampleSingleton INSTANCE = new SampleSingleton();

    public static SampleSingleton getInstance() {
      return INSTANCE;
    }

    @Override
    public String toString() {
      return "sample-singleton";
    }
  }

  @Test
  public void getInstance_sameClass_preservesValueClassEvenWhenDistinct() {
    assertNotSame(SingletonCodec.getInstance(SampleSingleton.class), SingletonCodec.getInstance(SampleSingleton.class));
    assertEquals(SampleSingleton.class, SingletonCodec.getInstance(SampleSingleton.class).getValueClass());
  }

  @Test
  public void roundTrip_nonNullSingleton_returnsSharedInstance() {
    SingletonCodec<SampleSingleton> codec = SingletonCodec.getInstance(SampleSingleton.class);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    OutputStreamBinaryEncoder encoder = new OutputStreamBinaryEncoder(baos);
    codec.encodeValue(encoder, SampleSingleton.getInstance());
    encoder.flush();
    InputStreamBinaryDecoder decoder = new InputStreamBinaryDecoder(new ByteArrayInputStream(baos.toByteArray()));
    assertSame(SampleSingleton.getInstance(), codec.decodeValue(decoder));
  }

  @Test
  public void appendRepresentation_usesToString() {
    StringBuilder sb = new StringBuilder();
    SingletonCodec.getInstance(SampleSingleton.class).appendRepresentation(sb, SampleSingleton.getInstance());
    assertEquals("sample-singleton", sb.toString());
  }
}
