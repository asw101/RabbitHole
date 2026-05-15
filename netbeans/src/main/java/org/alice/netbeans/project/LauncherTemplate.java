/*******************************************************************************
 * Copyright (c) 2006, 2016, Carnegie Mellon University. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. Products derived from the software may not be called "Alice", nor may
 *    "Alice" appear in their name, without prior written permission of
 *    Carnegie Mellon University.
 *
 * 4. All advertising materials mentioning features or use of this software must
 *    display the following acknowledgement: "This product includes software
 *    developed by Carnegie Mellon University"
 *
 * 5. The gallery of art assets and animations provided with this software is
 *    contributed by Electronic Arts Inc. and may be used for personal,
 *    non-commercial, and academic use only. Redistributions of any program
 *    source code that utilizes The Sims 2 Assets must also retain the copyright
 *    notice, list of conditions and the disclaimer contained in
 *    The Alice 3.0 Art Gallery License.
 *
 * DISCLAIMER:
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND.
 * ANY AND ALL EXPRESS, STATUTORY OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY,  FITNESS FOR A
 * PARTICULAR PURPOSE, TITLE, AND NON-INFRINGEMENT ARE DISCLAIMED. IN NO EVENT
 * SHALL THE AUTHORS, COPYRIGHT OWNERS OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, PUNITIVE OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING FROM OR OTHERWISE RELATING TO
 * THE USE OF OR OTHER DEALINGS WITH THE SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *******************************************************************************/

package org.alice.netbeans.project;

import edu.cmu.cs.dennisc.java.io.TextFileUtilities;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;

import java.io.File;

/**
 * Holds the JavaFX launcher template and generates the launcher source file.
 * Extracted from {@link ProjectCodeGenerator} to reduce file size.
 */
final class LauncherTemplate {

  static final String FILE_NAME = "AliceJavaFXLauncher.java";

  static FileObject generate(File javaSrcDirectory) {
    File file = new File(javaSrcDirectory, FILE_NAME);
    TextFileUtilities.write(file, FILE_CONTENT);
    return FileUtil.toFileObject(file);
  }

  static final String FILE_CONTENT =
"""
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.Window;

// If this project will not build and run make sure it is using
// a JDK that includes JavaFX, such as Bellsoft's Liberica JDK.
public class AliceJavaFXLauncher extends Application {
    private static final String EVIDENCE_PREFIX = "ALICE_LAUNCHER_EVIDENCE";
    private static final String NO_GO_PREFIX = "ALICE_LAUNCHER_NO_GO";
    private static final String RENDER_OBSERVATION_PREFIX = "ALICE_LAUNCHER_RENDER_OBSERVATION";
    private static final Color OBSERVATION_MARKER_COLOR = Color.rgb(32, 96, 160);
    private static String[] startingArgs;

    @Override
    public void start(Stage primaryStage) throws Exception {
        evidence("javafx-application-started");
        if (primaryStage == null) {
            renderObservation(
                    "render-target-absent",
                    false,
                    false,
                    "javafx-primary-stage",
                    "JavaFX did not provide a primary Stage; no render target exists.");
            noGo("primary-stage-unavailable");
            return;
        }
        evidence("stage-received");
        Scene scene = createObservationScene();
        primaryStage.setScene(scene);
        evidence("scene-configured observation-marker");
        evidence("stage-show-attempted");
        try {
            primaryStage.show();
        } catch (RuntimeException | Error showFailure) {
            if (isRenderTargetUnavailableFailure(showFailure)) {
                renderObservation(
                        "render-target-absent",
                        false,
                        false,
                        "stage-show",
                        "Stage.show failed before a render target could be observed.");
                noGo("render-target-unavailable");
                return;
            }
            throw showFailure;
        }
        if (!primaryStage.isShowing()) {
            renderObservation(
                    "render-target-absent",
                    false,
                    false,
                    "stage-is-showing",
                    "Stage.show returned, but Stage.isShowing was false; no shown render target was observed.");
            noGo("render-target-unavailable");
            return;
        }
        PixelObservation pixelObservation = observeShownScenePixel(scene);
        renderObservation(
                pixelObservation.status,
                true,
                pixelObservation.pixelsObserved,
                pixelObservation.missingObservationMechanism,
                pixelObservation.detail);
        if (pixelObservation.pixelsObserved) {
            evidence("pixels-observed shown-stage-marker");
        } else {
            noGo(pixelObservation.status);
            evidence("render-target-ready pixels-not-observed");
            return;
        }
        delegateProgramMain();
    }

    private static Scene createObservationScene() {
        Group root = new Group(new Rectangle(64.0, 64.0, OBSERVATION_MARKER_COLOR));
        return new Scene(root, 64.0, 64.0, OBSERVATION_MARKER_COLOR);
    }

    private static PixelObservation observeShownScenePixel(Scene scene) {
        try {
            Window window = scene.getWindow();
            if (window == null) {
                return PixelObservation.notObserved(
                        "pixel-observation-unavailable",
                        "stage-window-screen-bounds",
                        "Shown Scene did not expose a Window for screen coordinate sampling.");
            }
            double sampleX = 8.0;
            double sampleY = 8.0;
            if ((scene.getWidth() <= sampleX) || (scene.getHeight() <= sampleY)) {
                return PixelObservation.notObserved(
                        "pixel-observation-unavailable",
                        "stage-window-screen-bounds",
                        "Shown Scene dimensions were too small for the launcher marker sample point.");
            }
            double screenX = window.getX() + scene.getX() + sampleX;
            double screenY = window.getY() + scene.getY() + sampleY;
            if (!Double.isFinite(screenX) || !Double.isFinite(screenY)) {
                return PixelObservation.notObserved(
                        "pixel-observation-unavailable",
                        "stage-window-screen-bounds",
                        "Shown Scene did not provide finite screen coordinates for pixel sampling.");
            }
            Robot robot = new Robot();
            Color observedColor = robot.getPixelColor(screenX, screenY);
            if (observedColor == null) {
                return PixelObservation.notObserved(
                        "pixel-observation-unavailable",
                        "javafx.scene.robot.Robot",
                        "JavaFX Robot returned no color for the shown Scene sample point.");
            }
            if (matchesObservationMarker(observedColor)) {
                return PixelObservation.observed(
                        "Screen capture sampled launcher marker at "
                                + coordinateDetail(screenX, screenY)
                                + " with color " + colorDetail(observedColor) + ".");
            }
            return PixelObservation.notObserved(
                    "pixel-observation-mismatch",
                    "expected-observation-marker-pixel",
                    "Screen capture sampled " + colorDetail(observedColor)
                            + " at " + coordinateDetail(screenX, screenY)
                            + " instead of launcher marker " + colorDetail(OBSERVATION_MARKER_COLOR) + ".");
        } catch (RuntimeException | Error pixelFailure) {
            if (isPixelObservationUnsupportedFailure(pixelFailure)) {
                return PixelObservation.notObserved(
                        "pixel-observation-unsupported",
                        "javafx.scene.robot.Robot",
                        "JavaFX Robot screen capture was unavailable: " + describeFailure(pixelFailure));
            }
            throw pixelFailure;
        }
    }

    private static void delegateProgramMain() {
        Thread thread = new Thread(() -> {
            evidence("program-main-delegated rendering-not-asserted");
            Program.main(startingArgs);
        }, "AliceJavaFXLauncher-ProgramMain");
        thread.start();
    }

    private static void evidence(String marker) {
        System.out.println(EVIDENCE_PREFIX + " " + marker);
    }

    private static void noGo(String marker) {
        System.out.println(NO_GO_PREFIX + " " + marker);
    }

    private static void renderObservation(
            String status,
            boolean renderTargetShowing,
            boolean pixelsObserved,
            String missingObservationMechanism,
            String detail) {
        System.out.println(RENDER_OBSERVATION_PREFIX
                + " {"
                + jsonField("schema_version", "alice.launcher.render-observation/v1") + ","
                + jsonField("status", status) + ","
                + jsonField("renderTargetShowing", renderTargetShowing) + ","
                + jsonField("pixelsObserved", pixelsObserved) + ","
                + jsonField("missingObservationMechanism", missingObservationMechanism) + ","
                + jsonField("detail", detail)
                + "}");
    }

    private static final class PixelObservation {
        private final String status;
        private final boolean pixelsObserved;
        private final String missingObservationMechanism;
        private final String detail;

        private PixelObservation(
                String status,
                boolean pixelsObserved,
                String missingObservationMechanism,
                String detail) {
            this.status = status;
            this.pixelsObserved = pixelsObserved;
            this.missingObservationMechanism = missingObservationMechanism;
            this.detail = detail;
        }

        private static PixelObservation observed(String detail) {
            return new PixelObservation(
                    "shown-target-pixel-observed",
                    true,
                    "none",
                    detail);
        }

        private static PixelObservation notObserved(
                String status,
                String missingObservationMechanism,
                String detail) {
            return new PixelObservation(status, false, missingObservationMechanism, detail);
        }
    }

    private static boolean matchesObservationMarker(Color observedColor) {
        return Math.abs(observedColor.getRed() - OBSERVATION_MARKER_COLOR.getRed()) <= 0.01
                && Math.abs(observedColor.getGreen() - OBSERVATION_MARKER_COLOR.getGreen()) <= 0.01
                && Math.abs(observedColor.getBlue() - OBSERVATION_MARKER_COLOR.getBlue()) <= 0.01
                && observedColor.getOpacity() > 0.99;
    }

    private static String colorDetail(Color color) {
        return "rgb("
                + Math.round(color.getRed() * 255.0) + ","
                + Math.round(color.getGreen() * 255.0) + ","
                + Math.round(color.getBlue() * 255.0) + ")";
    }

    private static String coordinateDetail(double screenX, double screenY) {
        return "(" + Math.round(screenX) + "," + Math.round(screenY) + ")";
    }

    private static String jsonField(String name, String value) {
        return (char) 34 + name + (char) 34 + ':' + (char) 34 + escapeJson(value) + (char) 34;
    }

    private static String jsonField(String name, boolean value) {
        return (char) 34 + name + (char) 34 + ':' + value;
    }

    private static String escapeJson(String value) {
        StringBuilder builder = null;
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            if ((ch != (char) 34) && (ch != (char) 92) && (ch >= (char) 32)) {
                if (builder != null) {
                    builder.append(ch);
                }
            } else {
                if (builder == null) {
                    builder = new StringBuilder(value.length() + 8);
                    builder.append(value, 0, i);
                }
                appendEscapedJsonCharacter(builder, ch);
            }
        }
        return builder == null ? value : builder.toString();
    }

    private static void appendEscapedJsonCharacter(StringBuilder builder, char ch) {
        if ((ch == (char) 34) || (ch == (char) 92)) {
            builder.append((char) 92).append(ch);
        } else if (ch == (char) 8) {
            builder.append((char) 92).append('b');
        } else if (ch == (char) 9) {
            builder.append((char) 92).append('t');
        } else if (ch == (char) 10) {
            builder.append((char) 92).append('n');
        } else if (ch == (char) 12) {
            builder.append((char) 92).append('f');
        } else if (ch == (char) 13) {
            builder.append((char) 92).append('r');
        } else {
            builder.append((char) 92).append('u');
            String hex = Integer.toHexString(ch);
            for (int padding = hex.length(); padding < 4; padding++) {
                builder.append('0');
            }
            builder.append(hex);
        }
    }

    private static boolean isPixelObservationUnsupportedFailure(Throwable throwable) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            String className = current.getClass().getName();
            if ((current instanceof UnsupportedOperationException)
                    || "java.awt.HeadlessException".equals(className)
                    || "java.lang.NoClassDefFoundError".equals(className)
                    || "java.lang.NoSuchMethodError".equals(className)) {
                return true;
            }
            String message = current.getMessage();
            if (message != null) {
                String normalized = message.toLowerCase(java.util.Locale.ROOT);
                if (normalized.contains("robot")
                        || normalized.contains("screen capture")
                        || isDisplayUnavailableNormalizedMessage(normalized)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static String describeFailure(Throwable throwable) {
        String message = throwable.getMessage();
        if ((message == null) || message.isBlank()) {
            return throwable.getClass().getName();
        }
        return throwable.getClass().getName() + ": " + message;
    }

    private static boolean isDisplayUnavailableFailure(Throwable throwable) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (current instanceof UnsupportedOperationException
                    && isDisplayUnavailableMessage(current.getMessage())) {
                return true;
            }
            String className = current.getClass().getName();
            if ("java.awt.HeadlessException".equals(className)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRenderTargetUnavailableFailure(Throwable throwable) {
        for (Throwable current = throwable; current != null; current = current.getCause()) {
            if (current instanceof UnsupportedOperationException
                    && isRenderTargetUnavailableMessage(current.getMessage())) {
                return true;
            }
            String className = current.getClass().getName();
            if ("java.awt.HeadlessException".equals(className)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isRenderTargetUnavailableMessage(String message) {
        if (message == null) {
            return false;
        }
        String normalized = message.toLowerCase(java.util.Locale.ROOT);
        return normalized.contains("render target")
            || isDisplayUnavailableNormalizedMessage(normalized);
    }

    private static boolean isDisplayUnavailableMessage(String message) {
        if (message == null) {
            return false;
        }
        return isDisplayUnavailableNormalizedMessage(message.toLowerCase(java.util.Locale.ROOT));
    }

    private static boolean isDisplayUnavailableNormalizedMessage(String normalized) {
        return normalized.contains("unable to open display")
            || normalized.contains("no display")
            || normalized.contains("headless");
    }

    public static void main(final String[] args) {
        startingArgs = args;
        evidence("main-entered");
        try {
            evidence("javafx-launch-attempted");
            Application.launch(args);
        } catch (RuntimeException | Error launchFailure) {
            if (isDisplayUnavailableFailure(launchFailure)) {
                renderObservation(
                        "render-target-absent",
                        false,
                        false,
                        "javafx-display",
                        "JavaFX launch failed before a Stage/render target was available.");
                noGo("display-unavailable");
                return;
            }
            throw launchFailure;
        }
    }
}""";

  private LauncherTemplate() {
  }
}
