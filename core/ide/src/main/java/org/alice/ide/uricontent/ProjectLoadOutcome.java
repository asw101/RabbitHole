/*******************************************************************************
 * Copyright (c) 2006, 2015, Carnegie Mellon University. All rights reserved.
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
package org.alice.ide.uricontent;

import org.lgna.project.Project;
import org.lgna.project.Version;

import java.io.File;
import java.util.Objects;

/**
 * Typed result of one project-load attempt.
 *
 * <p>The outcome records what happened while loading. Callers still own user
 * interface and recovery decisions, so existing dialogs and backup behavior can
 * be preserved while failure reasons become explicit and testable.</p>
 */
public final class ProjectLoadOutcome {
  /**
   * Broad success/failure classification for project load routing.
   */
  public enum Kind {
    SUCCESS,
    FAILURE
  }

  /**
   * Specific result of a project load attempt.
   */
  public enum Status {
    LOADED,
    MISSING_FILE,
    ALICE2_WORLD,
    TYPE_FILE_NOT_PROJECT,
    FUTURE_VERSION_DECLINED,
    VERSION_NOT_SUPPORTED,
    IO_FAILURE,
    RUNTIME_EXCEPTION,
    UNKNOWN_FAILURE
  }

  private ProjectLoadOutcome(Kind kind, Status status, Project project, File file, Version futureVersion, Exception exception) {
    this.kind = Objects.requireNonNull(kind);
    this.status = Objects.requireNonNull(status);
    this.project = project;
    this.file = file;
    this.futureVersion = futureVersion;
    this.exception = exception;
    if (kind == Kind.SUCCESS) {
      if (status != Status.LOADED) {
        throw new IllegalArgumentException("Successful project loads must use LOADED status.");
      }
      Objects.requireNonNull(project, "Successful project loads must include a project.");
    } else if (status == Status.LOADED) {
      throw new IllegalArgumentException("Failed project loads cannot use LOADED status.");
    }
  }

  public static ProjectLoadOutcome success(Project project) {
    return success(project, null);
  }

  public static ProjectLoadOutcome success(Project project, File file) {
    return new ProjectLoadOutcome(Kind.SUCCESS, Status.LOADED, Objects.requireNonNull(project), file, null, null);
  }

  private static ProjectLoadOutcome failure(Status status, File file) {
    return failure(status, file, null);
  }

  private static ProjectLoadOutcome failure(Status status, File file, Exception exception) {
    return new ProjectLoadOutcome(Kind.FAILURE, status, null, file, null, exception);
  }

  public static ProjectLoadOutcome missingFile(File file) {
    return failure(Status.MISSING_FILE, file);
  }

  public static ProjectLoadOutcome alice2World(File file) {
    return failure(Status.ALICE2_WORLD, file);
  }

  public static ProjectLoadOutcome typeFileNotProject(File file) {
    return failure(Status.TYPE_FILE_NOT_PROJECT, file);
  }

  public static ProjectLoadOutcome futureVersionDeclined(File file, Version futureVersion) {
    return new ProjectLoadOutcome(Kind.FAILURE, Status.FUTURE_VERSION_DECLINED, null, file, Objects.requireNonNull(futureVersion), null);
  }

  public static ProjectLoadOutcome versionNotSupported(File file, Exception exception) {
    return failure(Status.VERSION_NOT_SUPPORTED, file, Objects.requireNonNull(exception));
  }

  public static ProjectLoadOutcome ioFailure(File file, Exception exception) {
    return failure(Status.IO_FAILURE, file, Objects.requireNonNull(exception));
  }

  public static ProjectLoadOutcome runtimeException(File file, RuntimeException exception) {
    return failure(Status.RUNTIME_EXCEPTION, file, Objects.requireNonNull(exception));
  }

  public static ProjectLoadOutcome unknownFailure(File file) {
    return failure(Status.UNKNOWN_FAILURE, file);
  }

  public Kind getKind() {
    return kind;
  }

  public Status getStatus() {
    return status;
  }

  public Project getProject() {
    return project;
  }

  /**
   * Returns the source file for diagnostics; callers choose any user-facing text.
   */
  public File getFile() {
    return file;
  }

  public Version getFutureVersion() {
    return futureVersion;
  }

  /**
   * Returns the captured load exception for diagnostics; not user-facing text.
   */
  public Exception getException() {
    return exception;
  }

  private final Kind kind;
  private final Status status;
  private final Project project;
  private final File file;
  private final Version futureVersion;
  private final Exception exception;
}
