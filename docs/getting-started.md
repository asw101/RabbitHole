# Getting started

Use this guide to get RabbitHole building on a fresh machine.

## What you need

- Java 21
- Maven 3.9.9 or later
- Git
- Git LFS
- The `tweedle-lang` submodule
- Optional: Install4J 10 if you need installer builds

## Clone the repository

```bash
git clone --recurse-submodules https://github.com/rysweet/RabbitHole.git
cd RabbitHole
git submodule update --init tweedle-lang
git lfs pull
```

If you already cloned the repository without submodules, run this check before
you build:

```bash
git submodule status tweedle-lang
test -d tweedle-lang/Grammar && echo "tweedle grammar present"
```

If the grammar directory is missing, initialize the submodule again:

```bash
git submodule update --init tweedle-lang
```

## Build the project

Build every Maven module and install the artifacts in your local Maven cache:

```bash
mvn compile install
```

If you want the no-Sims path that CI uses for most validation:

```bash
mvn -DincludeSims=false -Dinstall4j.skip clean install
```

## Run tests

Run the full test suite:

```bash
mvn test
```

Run the no-Sims, headless-friendly test lane used in CI:

```bash
mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Djava.awt.headless=true clean test
```

Run Checkstyle on the whole reactor:

```bash
mvn checkstyle:check -Dcheckstyle.config.location=checkstyle.xml
```

## Launch Alice

After a successful build, you can start the desktop IDE from the `alice-ide`
module:

```bash
cd alice-ide
mvn exec:java -Dalice-ide
```

## Coverage and quick diagnostics

Generate the no-Sims JaCoCo report used by the modernization coverage lane:

```bash
mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Dmdep.skip=true -Pcoverage verify
python3 scripts/summarize-jacoco-coverage.py \
  --output coverage-summary.md \
  --evidence-manifest coverage-evidence-manifest.json \
  --target-aggregate-line-percent 70.0 \
  --min-aggregate-line-percent 8.0 \
  --min-module-line-percent core/ast=18.0 \
  --min-module-line-percent core/model-loading=10.0 \
  --min-module-line-percent core/story-api-migration=75.0 \
  --min-module-line-percent core/tweedle=50.0 \
  --min-module-line-percent core/scenegraph=10.0 \
  --min-module-line-percent netbeans=25.0
```

Key output files:

- `coverage-report/target/site/jacoco-aggregate/index.html`
- `coverage-summary.md`
- `coverage-evidence-manifest.json`

## Everyday command list

| Task | Command |
| --- | --- |
| Build everything | `mvn compile install` |
| Run all tests | `mvn test` |
| Run CI-like headless tests | `mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Djava.awt.headless=true clean test` |
| Run Checkstyle | `mvn checkstyle:check -Dcheckstyle.config.location=checkstyle.xml` |
| Generate coverage | `mvn -DincludeSims=false -Dinstall4j.skip -Dcheckstyle.skip -Dmdep.skip=true -Pcoverage verify` |
| Start the IDE | `cd alice-ide && mvn exec:java -Dalice-ide` |
