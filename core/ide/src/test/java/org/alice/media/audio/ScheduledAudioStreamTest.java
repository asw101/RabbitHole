package org.alice.media.audio;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ScheduledAudioStream} — constructor variants, accessors,
 * and Comparable contract. Tests avoid AudioResource initialization
 * (which requires audio system).
 */
public class ScheduledAudioStreamTest {

  // ---- compareTo contract ----

  @Test
  public void compareTo_earlierStreamIsLess() {
    ScheduledAudioStream a = new ScheduledAudioStream(null, 1.0);
    ScheduledAudioStream b = new ScheduledAudioStream(null, 2.0);
    assertTrue(a.compareTo(b) < 0);
  }

  @Test
  public void compareTo_laterStreamIsGreater() {
    ScheduledAudioStream a = new ScheduledAudioStream(null, 3.0);
    ScheduledAudioStream b = new ScheduledAudioStream(null, 1.0);
    assertTrue(a.compareTo(b) > 0);
  }

  @Test
  public void compareTo_sameTimeIsZero() {
    ScheduledAudioStream a = new ScheduledAudioStream(null, 5.0);
    ScheduledAudioStream b = new ScheduledAudioStream(null, 5.0);
    assertEquals(0, a.compareTo(b));
  }

  // ---- accessors ----

  @Test
  public void getStartTime_returnsConstructorValue() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 3.5);
    assertEquals(3.5, stream.getStartTime(), 0.0);
  }

  @Test
  public void getVolume_defaultsToOne() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 0.0);
    assertEquals(1.0, stream.getVolume(), 0.0);
  }

  @Test
  public void getVolume_respectsExplicitValue() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 0.0, 0.0, 5.0, 0.75);
    assertEquals(0.75, stream.getVolume(), 0.0);
  }

  // ---- constructor variants ----

  @Test
  public void twoArgConstructor_setsDefaults() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 2.0);
    assertEquals(2.0, stream.getStartTime(), 0.0);
    assertEquals(1.0, stream.getVolume(), 0.0);
  }

  @Test
  public void threeArgConstructor_setsEntryPoint() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 1.0, 0.5);
    assertEquals(1.0, stream.getStartTime(), 0.0);
    assertEquals(1.0, stream.getVolume(), 0.0);
  }

  @Test
  public void fourArgConstructor_setsEndPoint() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 1.0, 0.5, 3.0);
    assertEquals(1.0, stream.getStartTime(), 0.0);
    assertEquals(1.0, stream.getVolume(), 0.0);
  }

  // ---- setAudioResource ----

  @Test
  public void setAudioResource_nullDoesNotThrow() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 0.0);
    stream.setAudioResource(null);
    assertNull(stream.getAudioResource());
  }

  @Test
  public void getAudioResource_returnsSetValue() {
    ScheduledAudioStream stream = new ScheduledAudioStream(null, 0.0);
    assertNull(stream.getAudioResource());
  }
}
