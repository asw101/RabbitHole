Feature: Alice project save-load and export archives
  Alice users need saved projects and exported player archives to preserve the
  meaningful project data they can observe later. The archive contract is
  validated through executable characterization tests without specifying trivial
  menu or dialog mechanics.

  Background:
    Given a synthetic Alice project named "Program"
    And the project uses the WindowCamera scene camera type

  @save @editor-archive
  Scenario: Saving a project produces a readable editor archive
    Given the project contains a text resource named "note.txt" with content "hello alice"
    When Alice saves a copy of the project to "saved-copy.a3p"
    Then the archive contains "version.txt"
    And the archive contains "manifest.json"
    And the archive contains "programType.xml"
    And the archive contains "resources.xml"
    And the archive contains "resources/note.txt"
    And the manifest names the project "Program"
    And Alice can reopen "saved-copy.a3p" as an editor project
    And the reopened project has program type "Program"
    And the reopened project contains resource "note.txt" with content "hello alice"

  @save @editor-archive @thumbnail
  Scenario: Saving includes a thumbnail when thumbnail creation succeeds
    Given thumbnail creation returns a valid PNG image
    When Alice saves a copy of the project to "saved-with-thumbnail.a3p"
    Then the archive contains "thumbnail.png"
    And the manifest icon is "thumbnail.png"

  @save @editor-archive @thumbnail
  Scenario: Saving remains valid when thumbnail creation is unavailable
    Given thumbnail creation is unavailable
    When Alice saves a copy of the project to "saved-without-thumbnail.a3p"
    Then Alice can reopen "saved-without-thumbnail.a3p" as an editor project
    And the reopened project has program type "Program"

  @export @player-archive
  Scenario: Exporting a project produces a player archive with Tweedle source
    When Alice exports the project to "exported.a3w"
    Then the archive contains "version.txt"
    And the archive contains "manifest.json"
    And the archive contains "src/Program.twe"
    And the manifest names the project "Program"

  @export @resources
  Scenario: Exporting preserves referenced image resources for player archive readers
    Given the project references an image resource named "picture.png"
    When Alice exports the project to "exported-resource.a3w"
    Then the archive contains "resources/picture.png"
    And the manifest references "resources/picture.png"
    And Alice can read resources from "exported-resource.a3w"
    And the read resources include image "picture.png"
    But the player archive reader is not required to decode the Tweedle program type

  @export @resources @safety
  Scenario: Exporting resources uses safe distinct archive entries
    Given the project contains two resources with original file name "image.png"
    And the project contains a resource with original file name "../folder/picture.png"
    When Alice exports the project to "safe-resource-entries.a3w"
    Then the archive contains distinct entries for both "image.png" resources
    And the archive contains a sanitized entry for "../folder/picture.png"
    And the archive does not contain "resources/../folder/picture.png"
    And Alice can read each exported resource by its resource identity

  @export @resources @security
  Scenario: Exporting resources does not persist local filesystem paths
    Given the project contains an image resource imported from "/Users/alice/private/picture.png"
    When Alice exports the project to "no-local-paths.a3w"
    Then the archive contains a sanitized relative resource entry for the imported image
    And the manifest references only relative archive entries
    And the archive does not contain "/Users/alice/private/picture.png"

  @load @failure @security
  Scenario: A resource reference with a traversal entry name is rejected
    Given "traversal-resource.a3w" is a player archive with a manifest resource entry "../outside.png"
    When Alice reads resources from "traversal-resource.a3w"
    Then loading fails with an error that identifies the unsafe resource entry
    And Alice does not read a file outside the archive

  @load @failure @security
  Scenario: A malformed archive does not replace the current project state
    Given Alice has a valid current project named "Current"
    And "malicious.a3p" has malformed archive metadata
    When Alice attempts to load "malicious.a3p"
    Then Alice reports the project could not be loaded
    And the current project remains "Current" until the user chooses a recovery or new-project outcome

  @load @failure
  Scenario: A future-version player archive reports its unsupported version
    Given "future-export.a3w" is a player archive with version "999.0.0.0"
    When Alice checks the archive version
    Then the reported future version is "999.0.0.0"
    And Alice does not silently treat the archive as a current editable project

  @load @failure
  Scenario: A player archive missing version metadata fails predictably
    Given "missing-version-export.a3w" is a player archive with a manifest
    And the archive does not contain "version.txt"
    When Alice checks the archive version
    Then loading fails with an error that identifies "version.txt"

  @load @failure
  Scenario: A corrupt JSON manifest does not fall back to the editor XML reader
    Given "corrupt-manifest-export.a3w" contains "version.txt"
    And "corrupt-manifest-export.a3w" contains an unreadable "manifest.json"
    When Alice chooses a project archive reader
    Then loading fails with an error that identifies "manifest.json"
    And Alice does not continue by looking for "programType.xml"

  @load @backup-recovery
  Scenario: Corrupt primary project offers the newest readable backup
    Given "world.a3p" is not a readable project archive
    And the backup directory "world.bak" contains these backups newest first:
      | backup                  | readable |
      | auto20240102_140000.a3p | no       |
      | auto20240102_130000.a3p | yes      |
    When Alice attempts to load "world.a3p"
    Then Alice marks "world.a3p" as unloadable
    And Alice marks backup "auto20240102_140000.a3p" unloadable during candidate selection
    And Alice offers backup "auto20240102_130000.a3p" for recovery
    When the user accepts the backup recovery prompt
    Then Alice loads "auto20240102_130000.a3p" as the recovered project
    And the original corrupt project is not silently written over

  @load @backup-recovery
  Scenario: Declining backup recovery opens a new project instead of loading corrupt data
    Given "world.a3p" is not a readable project archive
    And the backup directory "world.bak" contains a readable backup "auto20240102_130000.a3p"
    When Alice attempts to load "world.a3p"
    And Alice offers backup "auto20240102_130000.a3p" for recovery
    When the user declines the backup recovery prompt
    Then Alice does not load the corrupt project
    And Alice shows a new-project workflow

  @load @backup-recovery
  Scenario: All failed backups lead to a single failure outcome
    Given "world.a3p" is not a readable project archive
    And every backup in "world.bak" is unloadable
    When Alice attempts to load "world.a3p"
    Then Alice reports that the project and all backups could not be loaded
    And Alice shows a new-project workflow exactly once

  @load @backup-recovery @security
  Scenario: Backup recovery ignores a candidate that escapes the backup directory
    Given "world.a3p" is not a readable project archive
    And the backup directory "world.bak" contains an escaping candidate "auto20240102_140000.a3p"
    And the backup directory "world.bak" contains a readable backup "auto20240102_130000.a3p"
    When Alice attempts to load "world.a3p"
    Then Alice does not offer the escaping backup candidate
    And Alice offers backup "auto20240102_130000.a3p" for recovery
