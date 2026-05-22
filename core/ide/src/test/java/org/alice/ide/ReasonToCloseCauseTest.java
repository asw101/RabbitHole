package org.alice.ide;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class ReasonToCloseCauseTest {
  private static final String CLASS_NAME = "org.alice.ide.ReasonToCloseCause";
  private static final Path SOURCE_PATH = Paths.get("src", "main", "java", "org", "alice", "ide", "ReasonToCloseCause.java");

  @Test
  public void sourceFileIsAbsentFromIdeModule() {
    assertFalse(Files.exists(SOURCE_PATH));
  }

  @Test
  public void classCannotBeLoadedByName() {
    try {
      Class.forName(CLASS_NAME);
      fail("Expected " + CLASS_NAME + " to be absent");
    } catch (ClassNotFoundException expected) {
      assertEquals(CLASS_NAME, expected.getMessage());
    }
  }

  @Test
  public void missingTypeProducesNoEnumConstants() {
    assertArrayEquals(new String[0], loadEnumConstantNames());
  }

  private static String[] loadEnumConstantNames() {
    try {
      Class<?> cls = Class.forName(CLASS_NAME);
      Object[] constants = cls.getEnumConstants();
      if (constants == null) {
        return new String[0];
      }
      String[] names = new String[constants.length];
      for (int i = 0; i < constants.length; i++) {
        names[i] = String.valueOf(constants[i]);
      }
      return names;
    } catch (ClassNotFoundException expected) {
      return new String[0];
    }
  }
}
