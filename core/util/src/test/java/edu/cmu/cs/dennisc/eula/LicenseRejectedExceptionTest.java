package edu.cmu.cs.dennisc.eula;

import org.junit.Test;

import static org.junit.Assert.*;

public class LicenseRejectedExceptionTest {

  @Test
  public void canBeInstantiated() {
    assertNotNull(new LicenseRejectedException());
  }

  @Test
  public void extendsException() {
    assertTrue(new LicenseRejectedException() instanceof Exception);
  }

  @Test
  public void isCheckedExceptionNotRuntime() {
    assertFalse(RuntimeException.class.isAssignableFrom(LicenseRejectedException.class));
  }

  @Test
  public void canBeCaughtAsException() {
    try {
      throw new LicenseRejectedException();
    } catch (Exception e) {
      assertTrue(e instanceof LicenseRejectedException);
    }
  }

  @Test
  public void messageIsNull() {
    assertNull(new LicenseRejectedException().getMessage());
  }

  @Test
  public void causeIsNullByDefault() {
    assertNull(new LicenseRejectedException().getCause());
  }

  @Test
  public void simpleNameMatchesTypeName() {
    assertEquals("LicenseRejectedException", LicenseRejectedException.class.getSimpleName());
  }

  @Test
  public void implementsSerializable() {
    assertTrue(new LicenseRejectedException() instanceof java.io.Serializable);
  }
}
