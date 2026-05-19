package edu.cmu.cs.dennisc.crash;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import static org.junit.Assert.*;

public class CrashDetectorTest {

  private CrashDetector detector;

  @Before
  public void setUp() throws BackingStoreException {
    // Use CrashDetectorTest itself as the class key so we don't pollute other prefs
    detector = new CrashDetector(CrashDetectorTest.class);
    // Clean up any leftover state
    Preferences prefs = Preferences.userNodeForPackage(CrashDetectorTest.class);
    prefs.clear();
    prefs.flush();
  }

  @After
  public void tearDown() throws BackingStoreException {
    Preferences prefs = Preferences.userNodeForPackage(CrashDetectorTest.class);
    prefs.clear();
    prefs.flush();
  }

  @Test
  public void freshState_notPreviouslyOpened() {
    assertFalse(detector.isPreviouslyOpenedButNotSucessfullyClosed());
  }

  @Test
  public void afterOpen_reportsCrashPending() {
    detector.open();
    assertTrue(detector.isPreviouslyOpenedButNotSucessfullyClosed());
  }

  @Test
  public void afterOpenAndClose_noCrash() {
    detector.open();
    detector.close();
    assertFalse(detector.isPreviouslyOpenedButNotSucessfullyClosed());
  }

  @Test
  public void openTwiceWithoutClose_stillReportsCrash() {
    detector.open();
    detector.open();
    assertTrue(detector.isPreviouslyOpenedButNotSucessfullyClosed());
  }

  @Test
  public void closeThenReopen_reportsCrashAgain() {
    detector.open();
    detector.close();
    assertFalse(detector.isPreviouslyOpenedButNotSucessfullyClosed());
    detector.open();
    assertTrue(detector.isPreviouslyOpenedButNotSucessfullyClosed());
  }

  @Test
  public void multipleCleanCycles() {
    for (int i = 0; i < 3; i++) {
      detector.open();
      assertTrue(detector.isPreviouslyOpenedButNotSucessfullyClosed());
      detector.close();
      assertFalse(detector.isPreviouslyOpenedButNotSucessfullyClosed());
    }
  }
}
