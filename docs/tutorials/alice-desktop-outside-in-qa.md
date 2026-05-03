# Alice desktop outside-in QA tutorial

This tutorial walks through an outside-in QA evidence pass: validate the catalog, collect real launch evidence, and complete manual save/load evidence.

## What you will do

You will:

1. Validate the scenario catalog.
2. Run Alice under Xvfb for the launch workflow.
3. Generate a save/load evidence checklist.
4. Add user-visible evidence to the generated run directory.

## Before you start

Open a terminal at the repository root and confirm the build prerequisites:

```bash
java -version
mvn -version
git submodule update --init tweedle-lang
test -d tweedle-lang/Grammar
```

## Step 1: Validate scenarios

Run:

```bash
qa/outside-in/alice-desktop/runners/validate-scenarios.sh
```

The catalog is ready when the command reports all six scenarios as valid.

## Step 2: List the scenario catalog

Run:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh list
```

The list includes:

```text
alice-desktop-launch
alice-desktop-instructor-student-setup
alice-desktop-scene-creation
alice-desktop-run-debug
alice-desktop-save-load
alice-desktop-export
```

## Step 3: Run Alice under Xvfb

Run the launch scenario:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-launch
```

When the launch is successful, the runner prints the evidence directory. Open that directory and review:

```text
environment.txt
launch.log
status.txt
xvfb.log
screenshot.png or screenshot.xwd
```

The screenshot captures the observed desktop state. The launch log and status file explain how the runner started Alice, whether it detected a visible window, and whether the process stayed alive through evidence capture. Review these artifacts before accepting the launch evidence.

## Step 4: Generate a save/load checklist

Run:

```bash
qa/outside-in/alice-desktop/runners/run-scenario.sh run alice-desktop-save-load
```

The runner creates a manual evidence checklist because save/load uses real Swing interactions that are not automated by this lane. Checklist generation is preparation, not completion.

Open:

```text
qa/outside-in/alice-desktop/evidence/alice-desktop-save-load/<timestamp>/manual-evidence-checklist.txt
```

## Step 5: Perform the save/load workflow

Follow the checklist in Alice:

1. Launch Alice.
2. Create or open a small project.
3. Save the project as an `.a3p` file in the run directory.
4. Close the project or restart Alice.
5. Open the saved `.a3p` file.
6. Confirm the loaded scene or program state matches the saved state.

Add these files to the same timestamped run directory:

```text
before-save.png
after-reopen.png
saved-project.a3p
save-load-notes.txt
```

`save-load-notes.txt` should identify the visible object, template, or program state you used to compare the saved and loaded project.

The save/load scenario is complete only after the workflow has been performed in Alice and the required evidence has been added to the run directory.

## Step 6: Keep evidence out of commits

Evidence files are local run artifacts. Keep them for review or attach them to the relevant review record, but do not commit them.

Commit only documentation, scenario YAML, schema changes, and runner changes.
