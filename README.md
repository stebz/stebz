# Stebz

[![Maven Central](https://img.shields.io/maven-central/v/org.stebz/stebz-core?color=blue)](https://central.sonatype.com/search?namespace=org.stebz&sort=name)
[![Javadoc](https://javadoc.io/badge2/org.stebz/stebz-core/javadoc.svg?color=blue)](https://javadoc.io/doc/org.stebz)
[![License](https://img.shields.io/github/license/stebz/stebz?color=blue)](https://github.com/stebz/stebz/blob/main/LICENSE)

![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/stebz/stebz/tests.yml?branch=main)

Multi-approach and flexible Java framework for test steps managing.

## Table of Contents

* [How to use](#how-to-use)
* [Modules](#modules)
* [Step objects](#step-objects)
* [Quick steps](#quick-steps)
* [Attributes](#attributes)
* [Annotations](#annotations)
* [Annotation attributes](#annotation-attributes)
* [Gherkin](#gherkin)
* [Listeners](#listeners)
* [Extensions](#extensions)
* [Configuration](#configuration)

## How to use

Requires Java 8+ version.

In most cases it is enough to add one of the bundle dependencies:

- `stebz`
- `stebz-gherkin`

One of integration dependencies:

- `stebz-allure`
- `stebz-qase`
- `stebz-reportportal`

And maybe some extensions:

- `stebz-readable-reflective-name`
- `stebz-repeat-and-retry`

Maven:

<!-- @formatter:off -->
```xml
<dependencies>
  <dependency>
    <groupId>org.stebz</groupId>
    <artifactId>{module name}</artifactId>
    <version>1.3</version>
    <scope>test</scope>
  </dependency>
</dependencies>
```
<!-- @formatter:on -->

Gradle (Groovy DSL):

<!-- @formatter:off -->
```groovy
dependencies {
  testImplementation 'org.stebz:{module name}:1.3'
}
```
<!-- @formatter:on -->

Gradle (Kotlin DSL):

<!-- @formatter:off -->
```kotlin
dependencies {
  testImplementation("org.stebz:{module name}:1.3")
}
```
<!-- @formatter:on -->

Or you can choose only [modules](#modules) those you need.

Also, you can use BOM for version control.

<!-- @formatter:off -->

Maven:

```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.stebz</groupId>
      <artifactId>stebz-bom</artifactId>
      <version>1.3</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>
  </dependencies>
</dependencyManagement>
```
<!-- @formatter:on -->

Gradle (Groovy DSL):

<!-- @formatter:off -->
```groovy
dependencies {
  implementation platform('org.stebz:stebz-bom:1.3')
}
```
<!-- @formatter:on -->

Gradle (Kotlin DSL):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation(platform("org.stebz:stebz-bom:1.3"))
}
```
<!-- @formatter:on -->

## Modules

### Main:

| module              | include / depends on           | description                                        |
|---------------------|--------------------------------|----------------------------------------------------|
| `stebz-utils`       |                                | Common utils                                       |
| `stebz-core`        | `stebz-utils`                  | Core                                               |
| `stebz-methods`     | `stebz-utils`<br/>`stebz-core` | Methods for executing step objects and quick steps |
| `stebz-annotations` | `stebz-utils`<br/>`stebz-core` | Annotations and aspects                            |

### Extension:

| module                           | include / depends on                                              | description                                                         |
|----------------------------------|-------------------------------------------------------------------|---------------------------------------------------------------------|
| `stebz-gherkin-methods`          | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`                | Methods for executing step objects and quick steps in Gherkin style |
| `stebz-gherkin-annotations`      | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`            | Annotations in Gherkin style                                        |
| `stebz-clean-stack-trace`        | `stebz-utils`<br/>`stebz-core`                                    | Extension that cleans stack trace exceptions from garbage lines     |
| `stebz-readable-reflective-name` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`            | Extension that converts a reflective step name into a readable form |
| `stebz-repeat-and-retry`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Extension that allows to repeat and retry step bodies               |

### Bundle:

| module          | include / depends on                                                                                                                                                 | description                              |
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|
| `stebz`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-clean-stack-trace`                                                             | Bundle of Stebz modules                  |
| `stebz-gherkin` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-gherkin-methods`<br/>`stebz-gherkin-annotations`<br/>`stebz-clean-stack-trace` | Bundle of Stebz modules in Gherkin style |

### Integration:

| module               | include / depends on                                              | description                                          |
|----------------------|-------------------------------------------------------------------|------------------------------------------------------|
| `stebz-allure`       | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Allure report integration                            |
| `stebz-qase`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Qase report integration                              |
| `stebz-reportportal` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | ReportPortal report integration                      |
| `stebz-system-out`   | `stebz-utils`<br/>`stebz-core`                                    | System.out report integration (mainly for debugging) |

## Step objects

Steps are immutable objects containing attributes and a body. There are 4 types of steps according to their body type:
`RunnableStep`, `SupplierStep`, `ConsumerStep`, `FunctionStep`.

<!-- @formatter:off -->
```java
RunnableStep runnableStep = RunnableStep.of("runnable step", () -> {
  // step body
});
SupplierStep<String> supplierStep = SupplierStep.of("supplier step", () -> {
  // step body
  return "result";
});
ConsumerStep<Integer> consumerStep = ConsumerStep.of("consumer step", integer -> {
  // step body
});
FunctionStep<Integer, String> functionStep = FunctionStep.of("function step", integer -> {
  // step body
  return "result";
});
```
<!-- @formatter:on -->

A new step can be created based on an existing one with modified attributes or body.

<!-- @formatter:off -->
```java
RunnableStep newStep = step
  .withName("new name")
  .withBody(() -> {
    // new body
  });
```
<!-- @formatter:on -->

Or more convenient methods can be used.

<!-- @formatter:off -->
```java
RunnableStep newStep = step
  .withNewName(name -> "name prefix " + name)
  .withNewBody(originBody -> () -> {
    // new body
    originBody.run();
  });
```
<!-- @formatter:on -->

Steps can be called using the static `step` method.

<!-- @formatter:off -->
```java
step(runnableStep);

String supplierStepResult = step(supplierStep);

step(consumerStep, 123);

String functionStepResult = step(functionStep, 123);
```
<!-- @formatter:on -->

Or using the Around object.
<!-- @formatter:off -->
```java
String result = around(123)
  .step(runnableStep)
  .step(supplierStep.noResult())
  .step(consumerStep)
  .step(functionStep);
```
<!-- @formatter:on -->

It is convenient to add attributes right before calling a step.

<!-- @formatter:off -->
```java
step(runnableStep
  .withName("new name")
  .withComment("comment")
  .withNewBody(originBody -> () -> {
    // new body
    originBody.run();
  }));
```
<!-- @formatter:on -->

## Quick steps

If there is no need to save the object, but you need to quickly call a step, then this can be done using the
static `step` method.

<!-- @formatter:off -->
```java
step("quick runnable step", () -> {
  // step body
});

String result = step("quick supplier step", () -> {
  // step body
  return "result";
});
```
<!-- @formatter:on -->

## Attributes

There are several default attributes.

| attribute name | attribute type        |
|----------------|-----------------------|
| keyword        | `Keyword`             |
| name           | `String`              |
| params         | `Map<String, Object>` |
| comment        | `String`              |
| hidden         | `Boolean`             |

It is possible to create custom attributes.

<!-- @formatter:off -->
```java
StepAttribute<String> customAttribute = StepAttribute.nonNull("custom_attribute", "");

RunnableStep stepWithCustomAttribute = step.with(customAttribute, "attribute value");

String attributeValue = stepWithCustomAttribute.get(customAttribute);
```
<!-- @formatter:on -->

## Annotations

If the annotation is on a field subtype of `StepObj` or a method returning a subtype of `StepObj` or on the constructor
of an object implementing `StepObj`, then the annotation value will be added to the step. Otherwise, if there is an
annotation on a method or constructor, the call itself will be considered a quick step.

Step objects:

<!-- @formatter:off -->
```java
@WithName("step name")
@WithComment("step comment")
@WithParam(name = "custom param", value = "custom param value")
public static final RunnableStep fieldStep = RunnableStep.of(() -> {
  // step body
});

@WithName("step name")
@WithComment("step comment")
@WithParams
@WithParam(name = "custom param", value = "custom param value")
public static RunnableStep methodStep(String parameter) { return RunnableStep.of(() -> {
  // step body
}); }

public class StepImpl extends RunnableStep.Of {

  @WithName("step name")
  @WithComment("step comment")
  @WithParams
  @WithParam(name = "custom param", value = "custom param value")
  public StepImpl(String parameter) { super(() -> {
    // step body
  }); }
}
```
<!-- @formatter:on -->

Quick steps:

<!-- @formatter:off -->
```java
@WithName("step name")
@WithComment("step comment")
@WithParams
@WithParam(name = "custom param", value = "custom param value")
public static void method(String parameter) {
  // step body
}

public class MyObject {

  @WithName("step name")
  @WithComment("step comment")
  @WithParams
  @WithParam(name = "custom param", value = "custom param value")
  public MyObject(String parameter) {
    // step body
  }
}
```
<!-- @formatter:on -->

Quick steps can also be retrieved as objects using the `captured` method.

Via the method reference:

<!-- @formatter:off -->
```java
RunnableStep runnableStep = captured(MyClass::method, "arg");

SupplierStep<MyObject> supplierStep = captured(MyObject::new, "arg");
```
<!-- @formatter:on -->

Or via lambda expression:

<!-- @formatter:off -->
```java
RunnableStep runnableStep = captured(() -> MyClass.method("arg"));

SupplierStep<MyObject> supplierStep = captured(() -> new MyObject("arg"));
```
<!-- @formatter:on -->

The `captured` method can be combined with the `captured` method in your tests:

<!-- @formatter:off -->
```java
step(captured(MyClass::method, "arg")
  .withName("Custom name")
  .withComment("Custom comment"));
```
<!-- @formatter:on -->

## Annotation attributes

There are several default annotation attributes.

| annotation       | corresponding attributes                          |
|------------------|---------------------------------------------------|
| `WithKeyword`    | keyword                                           |
| `WithName`       | name                                              |
| `WithParams`     | params                                            |
| `WithParam`      | params                                            |
| `WithParam.List` | params                                            |
| `WithComment`    | comment                                           |
| `WithHidden`     | hidden                                            |
| `Step`           | alias for `WithName` and `WithParams` combination |

It is possible to create custom attributes.

<!-- @formatter:off -->
```java
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@StepAttributeAnnotation("custom_attribute")
public @interface CustomAttribute {

  String value();
}
```
<!-- @formatter:on -->

Later this attribute can be extracted in this way:

<!-- @formatter:off -->
```java
StepAttribute<CustomAttribute> attribute = StepAttribute.nullable("custom_attribute");
CustomAttribute value = step.get(attribute);
```
<!-- @formatter:on -->

## Gherkin

To use Stebz in Gherkin style you need to use `stebz-gherkin` dependency.

Methods way:

<!-- @formatter:off -->
```java
Background(runnableStep);

String supplierStepResult = Given(supplierStep);

When(consumerStep, 123);

String functionStepResult = Then(functionStep, 123);

And("quick runnable step", () -> {
  // step body
});

String result = But("quick supplier step", () -> {
  // step body
  return "result";
});
```
<!-- @formatter:on -->

Annotations way:

<!-- @formatter:off -->
```java
@Given("step name")
@WithComment("step comment")
public static RunnableStep methodStep(String parameter) { return RunnableStep.of(() -> {
  // step body
}); }

@When("step name")
@WithParams
public static void method(String parameter) {
  // step body
}
```
<!-- @formatter:on -->

It is possible to make test blanks. When running the test, a `StepNotImplementedError` will be thrown.

<!-- @formatter:off -->
```java
import static org.stebz.step.executable.RunnableStep.notImplemented;

class ExampleTest {

  @Test
  void test() {
    Given("Step 1", notImplemented());
    When("Step 2", notImplemented());
    Then("Step 3", notImplemented());
    And("Step 3", notImplemented());
  }
}
```
<!-- @formatter:on -->

## Listeners

Processing of steps occurs in listeners. Listeners can be specified via SPI mechanism or via properties.

## Extensions

Extensions allow you to add additional behavior to steps. For example, replace the value of some attribute or body.
Extensions can be specified via SPI mechanism or via properties.

## Configuration

The first priority is system properties, then properties from the properties file.

### `stebz-core` module

| property                         | type                            | default value      | description                        |
|----------------------------------|---------------------------------|--------------------|------------------------------------|
| `stebz.properties.path`          | `String`                        | `stebz.properties` | path to additional properties file |
| `stebz.extensions.enabled`       | `Boolean`                       | `true`             | are all extensions enabled         |
| `stebz.extensions.list`          | `String` list, delimiter is `,` | empty list         | extensions list                    |
| `stebz.extensions.autodetection` | `Boolean`                       | `true`             | extensions by SPI mechanism        |
| `stebz.listeners.enabled`        | `Boolean`                       | `true`             | are all listeners enabled          |
| `stebz.listeners.list`           | `String` list, delimiter is `,` | empty list         | listeners list                     |
| `stebz.listeners.autodetection`  | `Boolean`                       | `true`             | listeners by SPI mechanism         |

### `stebz-gherkin-methods` and `stebz-gherkin-annotations` modules

| property                            | type                     | default value | description                 |
|-------------------------------------|--------------------------|---------------|-----------------------------|
| `stebz.gherkin.keywords.given`      | `String`                 | `Given`       | value of Given keyword      |
| `stebz.gherkin.keywords.when`       | `String`                 | `When`        | value of When keyword       |
| `stebz.gherkin.keywords.then`       | `String`                 | `Then`        | value of Then keyword       |
| `stebz.gherkin.keywords.and`        | `String`                 | `And`         | value of And keyword        |
| `stebz.gherkin.keywords.but`        | `String`                 | `But`         | value of But keyword        |
| `stebz.gherkin.keywords.background` | `String`                 | `Background`  | value of Background keyword |
| `stebz.gherkin.keywords.asterisk`   | `String`                 | `*`           | value of asterisk keyword   |

### `CleanStackTraceExtension` (`stebz-clean-stack-trace` module)

| property                                        | type      | default value | description              |
|-------------------------------------------------|-----------|---------------|--------------------------|
| `stebz.extensions.cleanStackTrace.enabled`      | `Boolean` | `true`        | is the extension enabled |
| `stebz.extensions.cleanStackTrace.order`        | `Integer` | `10000`       | extension order          |
| `stebz.extensions.cleanStackTrace.stebzLines`   | `Boolean` | `true`        | removes Stebz lines      |
| `stebz.extensions.cleanStackTrace.aspectjLines` | `Boolean` | `true`        | removes AspectJ lines    |

### `ReadableReflectiveNameExtension` (`stebz-readable-reflective-name` module)

| property                                                | type      | default value | description              |
|---------------------------------------------------------|-----------|---------------|--------------------------|
| `stebz.extensions.readableReflectiveName.enabled`       | `Boolean` | `true`        | is the extension enabled |
| `stebz.extensions.readableReflectiveName.order`         | `Integer` | `10000`       | extension order          |
| `stebz.extensions.readableReflectiveName.wordSeparator` | `String`  | `_`           | default word separator   |

### `RepeatExtension` (`stebz-repeat-and-retry` module)

| property                          | type      | default value | description              |
|-----------------------------------|-----------|---------------|--------------------------|
| `stebz.extensions.repeat.enabled` | `Boolean` | `true`        | is the extension enabled |
| `stebz.extensions.repeat.order`   | `Integer` | `10000`       | extension order          |

### `RetryExtension` (`stebz-repeat-and-retry` module)

| property                         | type      | default value | description              |
|----------------------------------|-----------|---------------|--------------------------|
| `stebz.extensions.retry.enabled` | `Boolean` | `true`        | is the extension enabled |
| `stebz.extensions.retry.order`   | `Integer` | `10000`       | extension order          |

### `AllureListener` (`stebz-allure` module)

| property                                   | type                  | default value | description                          |
|--------------------------------------------|-----------------------|---------------|--------------------------------------|
| `stebz.listeners.allure.enabled`           | `Boolean`             | `true`        | is the listener enabled              |
| `stebz.listeners.allure.order`             | `Integer`             | `10000`       | listener order                       |
| `stebz.listeners.allure.keywordPosition`   | `AT_START` / `AT_END` | `AT_START`    | position of keyword relative to name |
| `stebz.listeners.allure.processName`       | `Boolean`             | `true`        | process step name with parameters    |
| `stebz.listeners.allure.contextParam`      | `Boolean`             | `true`        | step context as parameter            |
| `stebz.listeners.allure.commentAttachment` | `Boolean`             | `true`        | attach the comment as an attachment  |

### `QaseListener` (`stebz-qase` module)

| property                                 | type                  | default value | description                          |
|------------------------------------------|-----------------------|---------------|--------------------------------------|
| `stebz.listeners.qase.enabled`           | `Boolean`             | `true`        | is the listener enabled              |
| `stebz.listeners.qase.order`             | `Integer`             | `10000`       | listener order                       |
| `stebz.listeners.qase.keywordPosition`   | `AT_START` / `AT_END` | `AT_START`    | position of keyword relative to name |
| `stebz.listeners.qase.processName`       | `Boolean`             | `true`        | process step name with parameters    |
| `stebz.listeners.qase.contextParam`      | `Boolean`             | `true`        | step context as parameter            |
| `stebz.listeners.qase.commentAttachment` | `Boolean`             | `true`        | attach the comment as an attachment  |

### `ReportPortalListener` (`stebz-reportportal` module)

| property                                          | type                  | default value | description                          |
|---------------------------------------------------|-----------------------|---------------|--------------------------------------|
| `stebz.listeners.reportportal.enabled`            | `Boolean`             | `true`        | is the listener enabled              |
| `stebz.listeners.reportportal.order`              | `Integer`             | `10000`       | listener order                       |
| `stebz.listeners.reportportal.keywordPosition`    | `AT_START` / `AT_END` | `AT_START`    | position of keyword relative to name |
| `stebz.listeners.reportportal.processName`        | `Boolean`             | `true`        | process step name with parameters    |
| `stebz.listeners.reportportal.contextParam`       | `Boolean`             | `true`        | step context as parameter            |
| `stebz.listeners.reportportal.commentDescription` | `Boolean`             | `true`        | attach the comment as a description  |

### `SystemOutListener` (`stebz-system-out` module)

| property                                    | type                  | default value | description                          |
|---------------------------------------------|-----------------------|---------------|--------------------------------------|
| `stebz.listeners.systemout.enabled`         | `Boolean`             | `true`        | is the listener enabled              |
| `stebz.listeners.systemout.order`           | `Integer`             | `10000`       | listener order                       |
| `stebz.listeners.systemout.indent`          | `Integer`             | `2`           | number of spaces in indentation      |
| `stebz.listeners.systemout.keywordPosition` | `AT_START` / `AT_END` | `AT_START`    | position of keyword relative to name |
| `stebz.listeners.systemout.params`          | `Boolean`             | `true`        | show step params                     |
| `stebz.listeners.systemout.comment`         | `Boolean`             | `true`        | show step comment                    |
