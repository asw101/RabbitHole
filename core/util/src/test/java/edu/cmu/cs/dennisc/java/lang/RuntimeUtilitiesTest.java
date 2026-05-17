package edu.cmu.cs.dennisc.java.lang;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class RuntimeUtilitiesTest {

  @Test
  public void exec_echoReturnsZero() {
    int rc = RuntimeUtilities.exec("echo", "hello");
    assertEquals(0, rc);
  }

  @Test
  public void exec_withWorkingDirectory() {
    File dir = new File(System.getProperty("user.dir"));
    int rc = RuntimeUtilities.exec(dir, "echo", "test");
    assertEquals(0, rc);
  }

  @Test
  public void exec_withEnvironment() {
    File nullDir = null;
    Map<String, String> env = new HashMap<>();
    env.put("MY_TEST_VAR", "hello");
    int rc = RuntimeUtilities.exec(nullDir, env, "echo", "envtest");
    assertEquals(0, rc);
  }

  @Test
  public void exec_withWorkingDirAndEnv() {
    File dir = new File(System.getProperty("user.dir"));
    Map<String, String> env = new HashMap<>();
    env.put("FOO", "bar");
    int rc = RuntimeUtilities.exec(dir, env, "echo", "both");
    assertEquals(0, rc);
  }

  @Test
  public void exec_capturesStdout() {
    ByteArrayOutputStream capture = new ByteArrayOutputStream();
    try (PrintStream ps = new PrintStream(capture)) {
      File nullDir = null;
      Map<String, String> nullEnv = null;
      int rc = RuntimeUtilities.exec(nullDir, nullEnv, new String[]{"echo", "captured"}, ps);
      assertEquals(0, rc);
    }
    assertTrue("Should capture stdout content", capture.toString().contains("captured"));
  }

  @Test
  public void exec_capturesStdoutAndStderr() {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    ByteArrayOutputStream err = new ByteArrayOutputStream();
    try (PrintStream outPs = new PrintStream(out); PrintStream errPs = new PrintStream(err)) {
      File nullDir = null;
      Map<String, String> nullEnv = null;
      int rc = RuntimeUtilities.exec(nullDir, nullEnv, new String[]{"echo", "both"}, outPs, errPs);
      assertEquals(0, rc);
    }
    assertTrue("Should capture stdout content", out.toString().contains("both"));
  }

  @Test
  public void exec_nonZeroExitCode() {
    int rc = RuntimeUtilities.exec("false");
    assertNotEquals(0, rc);
  }

  @Test
  public void execSilent_echoReturnsZero() {
    int rc = RuntimeUtilities.execSilent("echo", "silent");
    assertEquals(0, rc);
  }

  @Test
  public void execSilent_withWorkingDirectory() {
    File dir = new File(System.getProperty("user.dir"));
    int rc = RuntimeUtilities.execSilent(dir, "echo", "silent-dir");
    assertEquals(0, rc);
  }

  @Test
  public void execSilent_withWorkingDirAndEnv() {
    File dir = new File(System.getProperty("user.dir"));
    Map<String, String> env = new HashMap<>();
    env.put("BAZ", "qux");
    int rc = RuntimeUtilities.execSilent(dir, env, "echo", "silent-env");
    assertEquals(0, rc);
  }

  @Test
  public void exec_nullWorkingDirectory() {
    File nullDir = null;
    int rc = RuntimeUtilities.exec(nullDir, "echo", "null-dir");
    assertEquals(0, rc);
  }

  @Test
  public void exec_nullEnvironment() {
    File nullDir = null;
    Map<String, String> nullEnv = null;
    int rc = RuntimeUtilities.exec(nullDir, nullEnv, "echo", "null-env");
    assertEquals(0, rc);
  }

  @Test
  public void exec_nullOutputStreams() {
    File nullDir = null;
    Map<String, String> nullEnv = null;
    int rc = RuntimeUtilities.exec(nullDir, nullEnv, new String[]{"echo", "null-streams"}, null, null);
    assertEquals(0, rc);
  }
}
