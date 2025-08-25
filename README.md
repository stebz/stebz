# Stebz

[![Maven Central](https://img.shields.io/maven-central/v/org.stebz/stebz-core?color=blue)](https://central.sonatype.com/search?namespace=org.stebz&sort=name)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg)](https://javadoc.io/doc/org.stebz)
[![License](https://img.shields.io/github/license/stebz/stebz?color=blue)](https://github.com/stebz/stebz/blob/main/LICENSE)
[![Maven Central Last Update](https://img.shields.io/maven-central/last-update/org.stebz/stebz?color=blue)](https://central.sonatype.com/search?namespace=org.stebz&sort=name)

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/stebz/stebz/tests.yml?branch=main)](https://github.com/stebz/stebz/actions/workflows/tests.yml/)
[![GitHub commits since latest release](https://img.shields.io/github/commits-since/stebz/stebz/latest?color=brightgreen)](https://github.com/stebz/stebz/commits/main/)
[![GitHub last commit](https://img.shields.io/github/last-commit/stebz/stebz?color=brightgreen)](https://github.com/stebz/stebz/commits/main/)

[![Telegram](https://img.shields.io/badge/-telegram-black?color=blue&logo=telegram&label=chat)](https://t.me/stebz_en/)
[![Telegram RU](https://img.shields.io/badge/-telegram-black?color=blue&logo=telegram&label=chat%20(ru))](https://t.me/stebz_ru/)

Multi-approach and flexible Java framework for test steps managing.

## Contents

* [What is Stebz](#what-is-stebz)
* [How to use](#how-to-use)
* [Docs](#docs)
  * [Modules](#modules)
  * [Step objects](#step-objects)
  * [Quick steps](#quick-steps)
  * [Attributes](#attributes)
  * [Annotations](#annotations)
  * [Annotation attributes](#annotation-attributes)
  * [Gherkin style](#gherkin-style)
  * [Listeners](#listeners)
  * [Extensions](#extensions)
  * [Configuration](#configuration)

## What is Stebz

The main feature of Stebz is flexible test steps.

<!-- @formatter:off -->
```java
@Test
void separateMethods() {
  step(user_is_authorized_as("user1", "123456")
    .withName("user authorized as simple user"));
  step(user_sees_balance(10000));
  step(user_sees_logout_button());
}

@Test
void aroundContext() {
  around(
    step(I_send_get_user_request(123))).
    step(response_status_code_should_be(200)).
    step(response_json_path_value_should_be("user.balance", 10000)
      .withName("response user should have balance {value}")
      .withBefore(response -> Logger.info("Response body: " + response.body())));
}
```
<!-- @formatter:on -->

And in the Gherkin style too.

<!-- @formatter:off -->
```java
@Test
void separateMethods() {
  When(user_is_authorized_as("user1", "123456")
    .withName("user authorized as simple user"));
  Then(user_sees_balance(10000));
  And(user_sees_logout_button());
}

@Test
void aroundContext() {
  around(
    When(I_send_get_user_request(123))).
    Then(response_status_code_should_be(200)).
    And(response_json_path_value_should_be("user.balance", 10000)
      .withName("response user should have balance {value}")
      .withBefore(response -> Logger.info("Response body: " + response.body())));
}
```
<!-- @formatter:on -->

See details in [Docs](#docs) section.

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
    <version>1.4</version>
  </dependency>
</dependencies>
```
<!-- @formatter:on -->

Gradle:

<!-- @formatter:off -->
```groovy
dependencies {
  implementation 'org.stebz:{module name}:1.4'
}
```
<!-- @formatter:on -->

Gradle (Kotlin DSL):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation("org.stebz:{module name}:1.4")
}
```
<!-- @formatter:on -->

Or you can choose only [modules](#modules) those you need.

Also, you can use BOM.

Maven:

<!-- @formatter:off -->
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.stebz</groupId>
      <artifactId>stebz-bom</artifactId>
      <version>1.4</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>
  </dependencies>
</dependencyManagement>
```
<!-- @formatter:on -->

Gradle:

<!-- @formatter:off -->
```groovy
dependencies {
  implementation platform('org.stebz:stebz-bom:1.4')
}
```
<!-- @formatter:on -->

Gradle (Kotlin DSL):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation(platform("org.stebz:stebz-bom:1.4"))
}
```
<!-- @formatter:on -->

## Docs

### Modules

#### Main:

| module              | include / depends on           | description                                        |
|---------------------|--------------------------------|----------------------------------------------------|
| `stebz-utils`       |                                | Common utils                                       |
| `stebz-core`        | `stebz-utils`                  | Core                                               |
| `stebz-methods`     | `stebz-utils`<br/>`stebz-core` | Methods for executing step objects and quick steps |
| `stebz-annotations` | `stebz-utils`<br/>`stebz-core` | Annotations and aspects                            |

#### Extension:

| module                           | include / depends on                                                                | description                                                         |
|----------------------------------|-------------------------------------------------------------------------------------|---------------------------------------------------------------------|
| `stebz-gherkin-keywords`         | `stebz-utils`<br/>`stebz-core`                                                      | Gherkin keywords                                                    |
| `stebz-gherkin-methods`          | `stebz-utils`<br/>`stebz-core`<br/>`stebz-gherkin-keywords`                         | Methods for executing step objects and quick steps in Gherkin style |
| `stebz-gherkin-annotations`      | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`<br/>`stebz-gherkin-keywords` | Annotations in Gherkin style                                        |
| `stebz-clean-stack-trace`        | `stebz-utils`<br/>`stebz-core`                                                      | Extension that cleans step exception stack trace from garbage lines |
| `stebz-readable-reflective-name` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`                              | Extension that converts a reflective step name into a readable form |
| `stebz-repeat-and-retry`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional)                   | Extension that allows to repeat and retry step bodies               |

#### Bundle:

| module          | include / depends on                                                                                                                                                                              | description                              |
|-----------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------|
| `stebz`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-clean-stack-trace`                                                                                          | Bundle of Stebz modules                  |
| `stebz-gherkin` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-gherkin-keywords`<br/>`stebz-gherkin-methods`<br/>`stebz-gherkin-annotations`<br/>`stebz-clean-stack-trace` | Bundle of Stebz modules in Gherkin style |

#### Integration:

| module               | include / depends on                                              | description                                          |
|----------------------|-------------------------------------------------------------------|------------------------------------------------------|
| `stebz-allure`       | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Allure report integration                            |
| `stebz-qase`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Qase report integration                              |
| `stebz-reportportal` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | ReportPortal report integration                      |
| `stebz-system-out`   | `stebz-utils`<br/>`stebz-core`                                    | System.out report integration (mainly for debugging) |

### Step objects

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
  .withExpectedResult("expected result")
  .withComment("comment")
  .withNewBody(originBody -> () -> {
    // new body
    originBody.run();
  }));
```
<!-- @formatter:on -->

### Quick steps

If there is no need to save the object, but you need to quickly call a step, then this can be done using the
static `step` method.

<!-- @formatter:off -->
```java
step("quick runnable step", "expected result", () -> {
  // step body
});

String result = step("quick supplier step", params("param1", "value1"), () -> {
  // step body
  return "result";
});
```
<!-- @formatter:on -->

### Attributes

There are several default attributes.

| attribute name  | attribute type        |
|-----------------|-----------------------|
| keyword         | `Keyword`             |
| name            | `String`              |
| params          | `Map<String, Object>` |
| expected result | `String`              |
| comment         | `String`              |
| hidden          | `Boolean`             |

It is possible to create custom attributes.

<!-- @formatter:off -->
```java
StepAttribute<String> customAttribute = StepAttribute.nonNull("custom_attribute", "");

RunnableStep stepWithCustomAttribute = step.with(customAttribute, "attribute value");

String attributeValue = stepWithCustomAttribute.get(customAttribute);
```
<!-- @formatter:on -->

It is possible to make test blanks. When running the test, a `StepNotImplementedError` will be thrown.

<!-- @formatter:off -->
```java
import static org.stebz.step.executable.RunnableStep.notImplemented;

class ExampleTest {

  @Test
  void test() {
    step(notImplemented()
      .withName("Step 1")
      .withExpectedResult("Expected result 1"));
    step(notImplemented()
      .withName("Step 2")
      .withExpectedResult("Expected result 2"));
    step(notImplemented()
      .withName("Step 3")
      .withExpectedResult("Expected result 3"));
  }
}
```
<!-- @formatter:on -->

### Annotations

If the annotation is on a field subtype of `StepObj` or a method returning a subtype of `StepObj` or on the constructor
of an object implementing `StepObj`, then the annotation value will be added to the step. Otherwise, if there is an
annotation on a method or constructor, the call itself will be considered a quick step.

Step objects with added attributes:

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

The `captured` method can be combined with the `step` method in your tests:

<!-- @formatter:off -->
```java
step(captured(MyClass::method, "arg")
  .withName("Custom name")
  .withComment("Custom comment"));
```
<!-- @formatter:on -->

### Annotation attributes

There are several default annotations.

| annotation            | description                                                    |
|-----------------------|----------------------------------------------------------------|
| `@WithKeyword`        | added `keyword` attribute                                      |
| `@WithName`           | added `name` attribute                                         |
| `@WithParams`         | added `params` attribute, all method / constructor params      |
| `@WithParam`          | added `params` attribute, custom param                         |
| `@WithParam.List`     | added `params` attribute, custom param list                    |
| `@WithExpectedResult` | added `expected result` attribute                              |
| `@WithComment`        | added `comment` attribute                                      |
| `@WithHidden`         | added `hidden` attribute                                       |
| `@Step`               | alias for `@WithName` and `@WithParams` combination            |
| `@Param`              | modifies the name or value of a method / constructor parameter |

It is possible to create custom attribute annotations.

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

Later this attribute can be extracted:

<!-- @formatter:off -->
```java
StepAttribute<CustomAttribute> attribute = StepAttribute.nullable("custom_attribute");
CustomAttribute value = step.get(attribute);
```
<!-- @formatter:on -->

### Gherkin style

To use Stebz in Gherkin style you need to use `stebz-gherkin` dependency.

Methods way:

<!-- @formatter:off -->
```java
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
@WithExpectedResult("step expected result")
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

It is also convenient to make test blanks in the Gherkin style.

<!-- @formatter:off -->
```java
import static org.stebz.step.executable.RunnableStep.notImplemented;

class ExampleTest {

  @Test
  void test() {
    Given(notImplemented()
      .withName("Step 1")
      .withExpectedResult("Expected result 1"));
    When(notImplemented()
      .withName("Step 2")
      .withExpectedResult("Expected result 2"));
    Then(notImplemented()
      .withName("Step 3")
      .withExpectedResult("Expected result 3"));
  }
}
```
<!-- @formatter:on -->

### Listeners

Processing of steps occurs in listeners.

Examples of listener modules: `stebz-allure`, `stebz-qase`, `stebz-reportportal`, `stebz-system-out`.

To create a custom listener you need to implement the `org.stebz.listener.StepListener` interface
and [specify it via SPI mechanism or via properties](#stebz-core-module).

### Extensions

Extensions allow you to add additional behavior to steps. For example, replace the step name or body.

Examples of extension modules: `stebz-repeat-and-retry`, `stebz-readable-reflective-name`.

List on extension interfaces:

| interface                | description                            |
|--------------------------|----------------------------------------|
| `StebzExtension`         | Base extension interface               |
| `InterceptStepContext`   | Intercepts and replaces step context   |
| `InterceptStep`          | Intercepts and replaces step           |
| `BeforeStepStart`        | Calling before step start              |
| `AfterStepStart`         | Calling after step start               |
| `InterceptStepResult`    | Intercepts and replaces step result    |
| `BeforeStepSuccess`      | Calling before step success            |
| `AfterStepSuccess`       | Calling after step success             |
| `InterceptStepException` | Intercepts and replaces step exception |
| `BeforeStepFailure`      | Calling before step failure            |
| `AfterStepFailure`       | Calling after step failure             |

To create a custom extension you need to implement one or more `org.stebz.extension.StebzExtension` interfaces
and [specify it via SPI mechanism or via properties](#stebz-core-module).

### Configuration

System properties have first priority, file properties have second priority.

#### `stebz-core` module

| property                         | type                            | default value      | description             |
|----------------------------------|---------------------------------|--------------------|-------------------------|
| `stebz.properties.path`          | `String`                        | `stebz.properties` | path to properties file |
| `stebz.extensions.enabled`       | `Boolean`                       | `true`             | enable extensions       |
| `stebz.extensions.list`          | `String` list, delimiter is `,` | empty list         | extensions list         |
| `stebz.extensions.autodetection` | `Boolean`                       | `true`             | enable SPI extensions   |
| `stebz.listeners.enabled`        | `Boolean`                       | `true`             | enable listeners        |
| `stebz.listeners.list`           | `String` list, delimiter is `,` | empty list         | listeners list          |
| `stebz.listeners.autodetection`  | `Boolean`                       | `true`             | enable SPI listeners    |

#### `stebz-gherkin-keywords`, `stebz-gherkin-methods`, `stebz-gherkin-annotations` modules

| property                            | type                     | default value | description                 |
|-------------------------------------|--------------------------|---------------|-----------------------------|
| `stebz.gherkin.keywords.given`      | `String`                 | `Given`       | value of Given keyword      |
| `stebz.gherkin.keywords.when`       | `String`                 | `When`        | value of When keyword       |
| `stebz.gherkin.keywords.then`       | `String`                 | `Then`        | value of Then keyword       |
| `stebz.gherkin.keywords.and`        | `String`                 | `And`         | value of And keyword        |
| `stebz.gherkin.keywords.but`        | `String`                 | `But`         | value of But keyword        |

#### `stebz-clean-stack-trace` module

| property                                        | type      | default value | description                       |
|-------------------------------------------------|-----------|---------------|-----------------------------------|
| `stebz.extensions.cleanStackTrace.enabled`      | `Boolean` | `true`        | enable extension                  |
| `stebz.extensions.cleanStackTrace.order`        | `Integer` | `10000`       | extension order                   |
| `stebz.extensions.cleanStackTrace.stebzLines`   | `Boolean` | `true`        | removes Stebz stack trace lines   |
| `stebz.extensions.cleanStackTrace.aspectjLines` | `Boolean` | `true`        | removes AspectJ stack trace lines |

#### `stebz-readable-reflective-name` module

| property                                                | type      | default value | description            |
|---------------------------------------------------------|-----------|---------------|------------------------|
| `stebz.extensions.readableReflectiveName.enabled`       | `Boolean` | `true`        | enable extension       |
| `stebz.extensions.readableReflectiveName.order`         | `Integer` | `10000`       | extension order        |
| `stebz.extensions.readableReflectiveName.wordSeparator` | `String`  | `_`           | default word separator |

#### `stebz-repeat-and-retry` module

| property                          | type      | default value | description      |
|-----------------------------------|-----------|---------------|------------------|
| `stebz.extensions.repeat.enabled` | `Boolean` | `true`        | enable extension |
| `stebz.extensions.repeat.order`   | `Integer` | `10000`       | extension order  |
| `stebz.extensions.retry.enabled`  | `Boolean` | `true`        | enable extension |
| `stebz.extensions.retry.order`    | `Integer` | `10000`       | extension order  |

#### `stebz-allure` module

| property                                          | type                  | default value | description                                 |
|---------------------------------------------------|-----------------------|---------------|---------------------------------------------|
| `stebz.listeners.allure.enabled`                  | `Boolean`             | `true`        | enable listener                             |
| `stebz.listeners.allure.order`                    | `Integer`             | `10000`       | listener order                              |
| `stebz.listeners.allure.keywordPosition`          | `AT_START` / `AT_END` | `AT_START`    | position of step keyword relative to name   |
| `stebz.listeners.allure.processName`              | `Boolean`             | `true`        | process step name with parameters           |
| `stebz.listeners.allure.contextParam`             | `Boolean`             | `true`        | step context as parameter                   |
| `stebz.listeners.allure.expectedResultAttachment` | `Boolean`             | `true`        | attach the expected result as an attachment |
| `stebz.listeners.allure.commentAttachment`        | `Boolean`             | `true`        | attach the comment as an attachment         |

### `stebz-qase` module

| property                                 | type                  | default value | description                               |
|------------------------------------------|-----------------------|---------------|-------------------------------------------|
| `stebz.listeners.qase.enabled`           | `Boolean`             | `true`        | enable listener                           |
| `stebz.listeners.qase.order`             | `Integer`             | `10000`       | listener order                            |
| `stebz.listeners.qase.keywordPosition`   | `AT_START` / `AT_END` | `AT_START`    | position of step keyword relative to name |
| `stebz.listeners.qase.processName`       | `Boolean`             | `true`        | process step name with parameters         |
| `stebz.listeners.qase.contextParam`      | `Boolean`             | `true`        | step context as parameter                 |
| `stebz.listeners.qase.commentAttachment` | `Boolean`             | `true`        | attach the comment as an attachment       |

#### `stebz-reportportal` module

| property                                          | type                  | default value | description                               |
|---------------------------------------------------|-----------------------|---------------|-------------------------------------------|
| `stebz.listeners.reportportal.enabled`            | `Boolean`             | `true`        | enable listener                           |
| `stebz.listeners.reportportal.order`              | `Integer`             | `10000`       | listener order                            |
| `stebz.listeners.reportportal.keywordPosition`    | `AT_START` / `AT_END` | `AT_START`    | position of step keyword relative to name |
| `stebz.listeners.reportportal.processName`        | `Boolean`             | `true`        | process step name with parameters         |
| `stebz.listeners.reportportal.contextParam`       | `Boolean`             | `true`        | step context as parameter                 |

#### `stebz-system-out` module

| property                                    | type                  | default value | description                               |
|---------------------------------------------|-----------------------|---------------|-------------------------------------------|
| `stebz.listeners.systemout.enabled`         | `Boolean`             | `true`        | enable listener                           |
| `stebz.listeners.systemout.order`           | `Integer`             | `10000`       | listener order                            |
| `stebz.listeners.systemout.indent`          | `Integer`             | `2`           | number of spaces in indentation           |
| `stebz.listeners.systemout.keywordPosition` | `AT_START` / `AT_END` | `AT_START`    | position of step keyword relative to name |
| `stebz.listeners.systemout.params`          | `Boolean`             | `true`        | show step params                          |
| `stebz.listeners.systemout.comment`         | `Boolean`             | `true`        | show step comment                         |
