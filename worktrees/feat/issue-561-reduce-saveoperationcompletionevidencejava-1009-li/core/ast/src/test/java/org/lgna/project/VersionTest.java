package org.lgna.project;

import edu.cmu.cs.dennisc.java.io.TextFileUtilities;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest {

    @Test
    public void willReadValidVersionFromValidString() {
        assertTrue(new Version("3.14.0.4-alpha.2+build.local").isValid());
    }

    @Test
    public void willReadInvalidVersionFromInvalidString() {
        assertFalse(new Version("3vhs14.0.4-alpha.2+build.local").isValid());
    }

    @Test
    public void willReadInvalidVersionFromInvalidString2() {
        assertFalse(new Version("3.14.0.4alpha.2+build.local").isValid());
    }

    @Test
    public void willCreateVersionFromStringAndGetBackMatchingString() {
        String versionString = "3.14.0.4-alpha.2+build.local";
        Version v = new Version(versionString);

        assertEquals(versionString, v.toString());
    }

    @Test
    public void willReadCurrentVersionFromFileAndGetCorrectAndValidVersion() {
        String currentVersionString = TextFileUtilities.read(Version.class.getResourceAsStream("Version.txt")).trim();
        Version v = new Version(currentVersionString);

        assertTrue(v.isValid());
        assertEquals(v.toString(), currentVersionString);
    }

    @Test
    public void willRoundTripRepresentativeHistoricalAliceVersions() {
        String[] historicalVersions = {
            "3.1.8.0.0",
            "3.1.20.0.0",
            "3.1.39.0.0",
            "3.2.113.0.0",
            "3.3.0.0.0",
            "3.4.0.0",
            "3.5.0.0",
            "3.6.0.0",
            "3.9.0.0"
        };

        for (String versionString : historicalVersions) {
            Version version = new Version(versionString);

            assertTrue(versionString, version.isValid());
            assertEquals(versionString, version.toString());
        }
    }

    @Test
    public void willCompareLegacyVersionsWithTrailingZeroSegmentsAsEquivalent() {
        assertEquals(0, new Version("3.3.0.0.0").compareTo(new Version("3.3.0.0")));
    }

    @Test
    public void willOrderPrereleaseBeforeMatchingReleaseAndIgnoreMetadataForOrdering() {
        assertTrue(new Version("3.14.0.4-alpha.2").compareTo(new Version("3.14.0.4")) < 0);
        assertEquals(0, new Version("3.14.0.4+build.one").compareTo(new Version("3.14.0.4+build.two")));
    }
}
