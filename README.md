# Stebz

[![Maven Central](https://img.shields.io/maven-central/v/org.stebz/stebz)](https://central.sonatype.com/search?namespace=org.stebz)
[![Javadoc](https://javadoc.io/badge2/org.stebz/stebz/javadoc.svg?color=blue)](https://javadoc.io/doc/org.stebz)
![License](https://img.shields.io/github/license/stebz/stebz?logoSize=auto)

Multi-approach and flexible Java framework for test steps managing.

## Table of Contents

* [How to use](#how-to-use)
* [Modules](#modules)

## How to use

Requires Java 8+ version.

In most cases it is enough to add one of the bundle dependencies:

- `stebz`
- `stebz-gherkin`

And one of integration dependencies:

- `stebz-allure`
- `stebz-qase`
- `stebz-reportportal`

Maven:

```xml
<dependency>
  <groupId>org.stebz</groupId>
  <artifactId>{module name}</artifactId>
  <version>1.0</version>
  <scope>test</scope>
</dependency>
```

Gradle:

```groovy
dependencies {
  testImplementation 'org.stebz:{module name}:1.0'
}
```

Or you can choose only [modules](#modules) those you need.

## Modules

| module                           | include / depends on                                                                                                                                                 | description                                                         |
|----------------------------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| `stebz-utils`                    |                                                                                                                                                                      | Common utils                                                        |
| `stebz-core`                     | `stebz-utils`                                                                                                                                                        | Core                                                                |
| `stebz-methods`                  | `stebz-utils`<br/>`stebz-core`                                                                                                                                       | Methods for executing step objects and quick steps                  |
| `stebz-annotations`              | `stebz-utils`<br/>`stebz-core`                                                                                                                                       | Annotations and aspects                                             |
| `stebz-gherkin-methods`          | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`                                                                                                                   | Methods for executing step objects and quick steps in Gherkin style |
| `stebz-gherkin-annotations`      | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`                                                                                                               | Annotations in Gherkin style                                        |
| `stebz-clean-stack-trace`        | `stebz-utils`<br/>`stebz-core`                                                                                                                                       | Extension that cleans stack trace exceptions from garbage lines     |
| `stebz-readable-reflective-name` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`                                                                                                               | Extension that converts a reflective step name into a readable form |
| `stebz`                          | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-clean-stack-trace`                                                             | Bundle of Stebz modules                                             |
| `stebz-gherkin`                  | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-gherkin-methods`<br/>`stebz-gherkin-annotations`<br/>`stebz-clean-stack-trace` | Bundle of Stebz modules in Gherkin style                            |
| `stebz-allure`                   | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (provided)                                                                                                    | Allure report integration                                           |
| `stebz-qase`                     | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (provided)                                                                                                    | Qase report integration                                             |
| `stebz-reportportal`             | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (provided)                                                                                                    | ReportPortal report integration                                     |
| `stebz-system-out`               | `stebz-utils`<br/>`stebz-core`                                                                                                                                       | System.out report integration (mainly for debugging)                |
