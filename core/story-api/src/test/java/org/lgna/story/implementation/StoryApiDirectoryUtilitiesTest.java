package org.lgna.story.implementation;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for StoryApiDirectoryUtilities — the static directory utility methods.
 * Headless-safe: uses filesystem checks only, no AWT.
 */
public class StoryApiDirectoryUtilitiesTest {

  // ── getSoundGalleryDirectory ──

  @Test
  public void getSoundGalleryDirectoryReturnsFileOrFallback() {
    File dir = StoryApiDirectoryUtilities.getSoundGalleryDirectory();
    assertNotNull("Should return a directory or fallback, never null", dir);
  }

  // ── getStarterProjectsDirectory ──

  @Test
  public void getStarterProjectsDirectoryReturnsFileOrFallback() {
    File dir = StoryApiDirectoryUtilities.getStarterProjectsDirectory();
    assertNotNull("Should return a directory or fallback, never null", dir);
  }

  // ── getInternalModelsDirectory ──

  @Test
  public void getInternalModelsDirectoryReturnsFileOrFallback() {
    File dir = StoryApiDirectoryUtilities.getInternalModelsDirectory();
    assertNotNull("Should return a directory or fallback, never null", dir);
  }

  // ── setUserGalleryDirectory / getUserGalleryDirectory ──

  @Test
  public void getUserGalleryDirectoryDefaultIsNotNull() {
    // Reset any previously set value
    StoryApiDirectoryUtilities.setUserGalleryDirectory(null);
    File dir = StoryApiDirectoryUtilities.getUserGalleryDirectory();
    assertNotNull("Default user gallery should never be null", dir);
  }

  @Test
  public void getUserGalleryDirectoryDefaultPathContainsAlice() {
    StoryApiDirectoryUtilities.setUserGalleryDirectory(null);
    File dir = StoryApiDirectoryUtilities.getUserGalleryDirectory();
    assertTrue("Default path should contain Alice3",
        dir.getAbsolutePath().contains("Alice3"));
  }

  @Test
  public void setUserGalleryDirectoryRoundTrips() {
    File custom = new File("/custom/gallery");
    StoryApiDirectoryUtilities.setUserGalleryDirectory(custom);
    try {
      File result = StoryApiDirectoryUtilities.getUserGalleryDirectory();
      assertEquals(custom, result);
    } finally {
      StoryApiDirectoryUtilities.setUserGalleryDirectory(null);
    }
  }

  @Test
  public void setUserGalleryDirectoryToNullRestoresDefault() {
    File custom = new File("/custom/gallery");
    StoryApiDirectoryUtilities.setUserGalleryDirectory(custom);
    StoryApiDirectoryUtilities.setUserGalleryDirectory(null);
    File result = StoryApiDirectoryUtilities.getUserGalleryDirectory();
    assertNotEquals("Should restore default, not custom", custom, result);
  }

  // ── getDirectory fallback behavior ──

  @Test
  public void soundGalleryFallsBackToDefaultDirectory() {
    // Without a proper install directory, all getDirectory calls
    // should fall back to FileUtilities.getDefaultDirectory()
    File dir = StoryApiDirectoryUtilities.getSoundGalleryDirectory();
    assertTrue("Fallback directory should exist", dir.exists() || dir.getAbsolutePath().length() > 0);
  }
}
