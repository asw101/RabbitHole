package org.alice.ide;

import org.alice.ide.uricontent.UriProjectLoader;
import org.lgna.croquet.history.UserActivity;
import org.lgna.project.Project;
import org.lgna.project.ast.NamedUserType;
import org.lgna.project.ast.UserField;
import org.lgna.project.ast.UserMethod;

import java.io.File;
import java.net.URI;
import java.util.ListIterator;
import java.util.Set;

final class ProjectState {
  private UriProjectLoader uriProjectLoader;
  private UserActivity projectActivity;

  URI getUri() {
    return this.uriProjectLoader != null ? this.uriProjectLoader.getUri() : null;
  }

  boolean isNewProject() {
    return this.uriProjectLoader != null && this.uriProjectLoader.isNewProject();
  }

  boolean isBackup() {
    return this.uriProjectLoader != null && this.uriProjectLoader.isBackup();
  }

  File getMainProjectFile() {
    return this.uriProjectLoader != null ? this.uriProjectLoader.getMainProjectFile() : null;
  }

  boolean hasUriProjectLoaderThatShouldBeSaved() {
    return this.uriProjectLoader != null && this.uriProjectLoader.shouldBeSaved();
  }

  void setUriProjectLoader(UriProjectLoader loader) {
    this.uriProjectLoader = loader;
  }

  UriProjectLoader getUriProjectLoader() {
    return this.uriProjectLoader;
  }

  String sanitizeProject(Project project) {
    StringBuilder sb = new StringBuilder();
    Set<NamedUserType> types = project.getNamedUserTypes();
    for (NamedUserType type : types) {
      boolean wasNullMethodRemoved = false;
      ListIterator<UserMethod> methodIterator = type.getDeclaredMethods().listIterator();
      while (methodIterator.hasNext()) {
        if (methodIterator.next() == null) {
          methodIterator.remove();
          wasNullMethodRemoved = true;
        }
      }
      boolean wasNullFieldRemoved = false;
      ListIterator<UserField> fieldIterator = type.getDeclaredFields().listIterator();
      while (fieldIterator.hasNext()) {
        if (fieldIterator.next() == null) {
          fieldIterator.remove();
          wasNullFieldRemoved = true;
        }
      }
      if (wasNullMethodRemoved) {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append("null method was removed from ");
        sb.append(type.getName());
        sb.append('.');
      }
      if (wasNullFieldRemoved) {
        if (sb.length() > 0) {
          sb.append("\n");
        }
        sb.append("null field was removed from ");
        sb.append(type.getName());
        sb.append('.');
      }
    }
    return sb.toString();
  }

  UserActivity beginNewProject(UserActivity overallUserActivity, UserActivity openActivity) {
    if (openActivity != null) {
      openActivity.finish();
    }
    if (this.projectActivity != null) {
      this.projectActivity.finish();
    }
    this.projectActivity = overallUserActivity.newChildActivity();
    return this.projectActivity;
  }

  UserActivity getVisibleOpenActivity(UserActivity latestActivity) {
    return latestActivity == this.projectActivity ? null : latestActivity;
  }

  UserActivity getProjectActivity() {
    return this.projectActivity;
  }
}
