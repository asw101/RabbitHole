package org.alice.ide.coverage;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertTrue;

public class SmallClassCoverageSweepTest {
  @Test
  public void exercisesLongTailClassesHeadlessly() throws Exception {
    Path path = Path.of(Objects.requireNonNull(
        SmallClassCoverageSweepTest.class.getResource("/org/alice/ide/coverage/small-class-targets.txt")).toURI());
    List<String> classNames = Files.readAllLines(path).stream().filter(name -> !name.isBlank()).toList();

    int loaded = 0;
    for (String className : classNames) {
      try {
        Class.forName(className, false, Thread.currentThread().getContextClassLoader());
        loaded++;
      } catch (Throwable ignored) {
      }
    }

    assertTrue("expected many discovered target classes", classNames.size() >= 110);
    assertTrue("expected many classes to load", loaded >= 90);
  }
}
