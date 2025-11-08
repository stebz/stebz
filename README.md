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
  * [BOM](#bom)
  * [Aspects](#aspects)
* [Documentation](#documentation)
  * [Modules](#modules)
  * [Step objects](#step-objects)
  * [Quick steps](#quick-steps)
  * [Attributes](#attributes)
  * [Annotations](#annotations)
  * [Annotation attributes](#annotation-attributes)
  * [Arrange-Act-Assert style](#arrange-act-assert-style)
  * [Gherkin style](#gherkin-style)
  * [TDD / BDD](#tdd--bdd)
  * [Listeners](#listeners)
    * [Allure listener](#stebz-allure-listener)
    * [Qase listener](#stebz-qase-listener)
    * [ReportPortal listener](#stebz-reportportal-listener)
    * [Test IT listener](#stebz-testit-listener)
  * [Extensions](#extensions)
    * [Clean stack trace extension](#stebz-clean-stack-trace-extension)
    * [Hidden steps extension](#stebz-hidden-steps-extension)
    * [Readable reflective name extension](#stebz-readable-reflective-name-extension)
    * [Repeat and retry extension](#stebz-repeat-and-retry-extension)
  * [Configuration](#configuration)
    * [`stebz-core` module](#stebz-core-module)
    * [`stebz-aaa-keywords`, `stebz-aaa-methods`,
      `stebz-aaa-annotations` modules](#stebz-aaa-keywords-stebz-aaa-methods-stebz-aaa-annotations-modules)
    * [`stebz-gherkin-keywords`, `stebz-gherkin-methods`,
      `stebz-gherkin-annotations` modules](#stebz-gherkin-keywords-stebz-gherkin-methods-stebz-gherkin-annotations-modules)
    * [`stebz-clean-stack-trace` module](#stebz-clean-stack-trace-module)
    * [`stebz-hidden-steps` module](#stebz-hidden-steps-module)
    * [`stebz-readable-reflective-name` module](#stebz-readable-reflective-name-module)
    * [`stebz-repeat-and-retry` module](#stebz-repeat-and-retry-module)
    * [`stebz-allure` module](#stebz-allure-module)
    * [`stebz-qase` module](#stebz-qase-module)
    * [`stebz-reportportal` module](#stebz-reportportal-module)
    * [`stebz-testit` module](#stebz-testit-module)
    * [`stebz-system-out` module](#stebz-system-out-module)
* [Contributing](#contributing)
  * [How to contribute](#how-to-contribute)
  * [Contributors](#contributors)
* [License](#license)

## What is Stebz

The main feature of Stebz is flexible test steps:

<!-- @formatter:off -->
```java
@Test
void separateMethods() {
  step(user_is_authorized_as("user1", "123456")
    .withName("user authorized as simple user"));
  step(user_sees_balance(10000)
    .withComment("user1 balance is always 10000"));
  step(user_sees_logout_button());
}

@Test
void aroundContext() {
  around(
    step(I_send_get_user_request(123))
      .withBefore(() -> AuthUtils.checkAuth())).
    step(response_status_code_should_be(200)).
    step(response_json_path_value_should_be("user.balance", 10000)
      .withName("response user balance should be {value}")
      .withOnFailure(response -> Logger.warn("Incorrect body: " + response.body())));
}
```
<!-- @formatter:on -->

And in the Gherkin style:

<!-- @formatter:off -->
```java
@Test
void separateMethods() {
  When(user_is_authorized_as("user1", "123456")
    .withName("user authorized as simple user"));
  Then(user_sees_balance(10000)
    .withComment("user1 balance is always 10000"));
  And(user_sees_logout_button());
}

@Test
void aroundContext() {
  around(
    When(I_send_get_user_request(123)
      .withBefore(() -> AuthUtils.checkAuth()))).
    Then(response_status_code_should_be(200)).
    And(response_json_path_value_should_be("user.balance", 10000)
      .withName("response user balance should be {value}")
      .withOnFailure(response -> Logger.warn("Incorrect body: " + response.body())));
}
```
<!-- @formatter:on -->

And in the Arrange-Act-Assert style:

<!-- @formatter:off -->
```java
@Test
void separateMethods() {
  Act(user_is_authorized_as("user1", "123456")
    .withName("user authorized as simple user"));
  Assert(user_sees_balance(10000)
    .withComment("user1 balance is always 10000"));
  Assert(user_sees_logout_button());
}

@Test
void aroundContext() {
  around(
    Act(I_send_get_user_request(123)
      .withBefore(() -> AuthUtils.checkAuth()))).
    Assert(response_status_code_should_be(200)).
    Assert(response_json_path_value_should_be("user.balance", 10000)
      .withName("response user balance should be {value}")
      .withOnFailure(response -> Logger.warn("Incorrect body: " + response.body())));
}
```
<!-- @formatter:on -->

Steps in the methods way might look like:

<!-- @formatter:off -->
```java
public static RunnableStep user_is_authorized_as(String username,
                                                 String password) { return RunnableStep.of(
  "user is authorized as {username}", params("username", username, "password", password), () -> {
    // step body
  }
); }
```
<!-- @formatter:on -->

Or like this using annotations:

<!-- @formatter:off -->
```java
@WithName("user is authorized as {username}")
@WithParams
public static RunnableStep user_is_authorized_as(String username,
                                                 String password) { return RunnableStep.of(() -> {
  // step body
}); }
```
<!-- @formatter:on -->

And also allows you to follow the TDD / BDD right in your code:

<!-- @formatter:off -->
```java
import static org.stebz.step.executable.RunnableStep.notImplemented;

class ExampleTest {
  
  @Test
  @Disabled // not implemented
  void manuallyDisabledTest() {
    Given("Step 1", "Expected result 1");
    When("Step 2", "Expected result 2");
    Then("Step 3", "Expected result 3");
  }

  @Test
  void throwingErrorTest() {
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

See [examples](https://github.com/stebz/stebz-maven-junit5-allure-example).

See details in [Documentation](#documentation).

## How to use

Requires Java 8+ version.

In most cases it is enough to add one of the bundle dependencies:

- `stebz`
- `stebz-aaa` (`stebz` with keywords in Arrange-Act-Assert style)
- `stebz-gherkin` (`stebz` with keywords in Gherkin style)

And one of integration dependencies (don't forget to add and configure the dependency of the reporting system):

- `stebz-allure` (uses `io.qameta.allure:allure-java-commons`)
- `stebz-qase` (uses `io.qase:qase-java-commons`)
- `stebz-reportportal` (uses `com.epam.reportportal:client-java`)
- `stebz-testit` (uses `ru.testit:testit-java-commons`)

Maven:

<!-- @formatter:off -->
```xml
<dependencies>
  <dependency>
    <groupId>org.stebz</groupId>
    <artifactId>{module name}</artifactId>
    <version>1.9</version>
  </dependency>
</dependencies>
```
<!-- @formatter:on -->

Gradle (Groovy):

<!-- @formatter:off -->
```groovy
dependencies {
  implementation 'org.stebz:{module name}:1.9'
}
```
<!-- @formatter:on -->

Gradle (Kotlin):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation("org.stebz:{module name}:1.9")
}
```
<!-- @formatter:on -->

Or you can choose only [modules](#modules) those you need.

See [examples](https://github.com/stebz/stebz-maven-junit5-allure-example).

### BOM

Also, you can use BOM (Bill of Materials) to ensure correct versions of all the dependencies are used.

Maven:

<!-- @formatter:off -->
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.stebz</groupId>
      <artifactId>stebz-bom</artifactId>
      <version>1.9</version>
      <scope>import</scope>
      <type>pom</type>
    </dependency>
  </dependencies>
</dependencyManagement>
```
<!-- @formatter:on -->

Gradle (Groovy):

<!-- @formatter:off -->
```groovy
dependencies {
  implementation platform('org.stebz:stebz-bom:1.9')
}
```
<!-- @formatter:on -->

Gradle (Kotlin):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation(platform("org.stebz:stebz-bom:1.9"))
}
```
<!-- @formatter:on -->

### Aspects

If your project already uses Allure / Qase / ReportPortal / Test IT with `@Step` annotations, then you don't need any
additional configuration.

Otherwise, you need to manually enable aspects using the AspectJ library.

Maven:

<!-- @formatter:off -->
```xml
<properties>
  <aspectj.version>1.9.24</aspectj.version> 
</properties>

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.2.3</version>
  <configuration>
    <argLine>
      -javaagent:"${settings.localRepository}/org/aspectj/aspectjweaver/${aspectj.version}/aspectjweaver-${aspectj.version}.jar"
    </argLine>
  </configuration>
  <dependencies>
    <dependency>
      <groupId>org.aspectj</groupId>
      <artifactId>aspectjweaver</artifactId>
      <version>${aspectj.version}</version>
    </dependency>
  </dependencies>
</plugin>
```
<!-- @formatter:on -->

Gradle (Groovy):

<!-- @formatter:off -->
```groovy
def aspectJVersion = '1.9.24'

configurations {
  agent {
    canBeResolved = true
    canBeConsumed = true
  }
}

dependencies {
  agent "org.aspectj:aspectjweaver:$aspectJVersion"
}

test {
  jvmArgs = [ "-javaagent:${configurations.agent.singleFile}" ]
}
```
<!-- @formatter:on -->

Gradle (Kotlin):

<!-- @formatter:off -->
```kotlin
val aspectJVersion = "1.9.24"

val agent: Configuration by configurations.creating {
  isCanBeConsumed = true
  isCanBeResolved = true
}

dependencies {
  agent("org.aspectj:aspectjweaver:${aspectJVersion}")
}

tasks.test {
  jvmArgs = listOf(
    "-javaagent:${agent.singleFile}"
  )
}
```
<!-- @formatter:on -->

## Documentation

### Modules

#### Main:

| module              | include / depends on           | description                                        |
|---------------------|--------------------------------|----------------------------------------------------|
| `stebz-utils`       |                                | Common utils                                       |
| `stebz-core`        | `stebz-utils`                  | Core                                               |
| `stebz-methods`     | `stebz-utils`<br/>`stebz-core` | Methods for executing step objects and quick steps |
| `stebz-annotations` | `stebz-utils`<br/>`stebz-core` | Annotations and aspects                            |

#### Extension:

| module                           | include / depends on                                                                | description                                                                    |
|----------------------------------|-------------------------------------------------------------------------------------|--------------------------------------------------------------------------------|
| `stebz-aaa-keywords`             | `stebz-utils`<br/>`stebz-core`                                                      | Arrange-Act-Assert style keywords                                              |
| `stebz-aaa-methods`              | `stebz-utils`<br/>`stebz-core`<br/>`stebz-aaa-keywords`                             | Methods for executing step objects and quick steps in Arrange-Act-Assert style |
| `stebz-aaa-annotations`          | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`<br/>`stebz-aaa-keywords`     | Annotations in Arrange-Act-Assert style                                        |
| `stebz-gherkin-keywords`         | `stebz-utils`<br/>`stebz-core`                                                      | Gherkin style keywords                                                         |
| `stebz-gherkin-methods`          | `stebz-utils`<br/>`stebz-core`<br/>`stebz-gherkin-keywords`                         | Methods for executing step objects and quick steps in Gherkin style            |
| `stebz-gherkin-annotations`      | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`<br/>`stebz-gherkin-keywords` | Annotations in Gherkin style                                                   |
| `stebz-clean-stack-trace`        | `stebz-utils`<br/>`stebz-core`                                                      | Extension that cleans step exception stack trace from garbage lines            |
| `stebz-hidden-steps`             | `stebz-utils`<br/>`stebz-core`                                                      | Extension that allows to hide several steps                                    |
| `stebz-readable-reflective-name` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations`                              | Extension that converts a reflective step name into a readable form            |
| `stebz-repeat-and-retry`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional)                   | Extension that allows to repeat and retry step bodies                          |

#### Bundle:

| module          | include / depends on                                                                                                                                                                                                                                                                         | description                                         |
|-----------------|----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------|
| `stebz`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-clean-stack-trace`<br/>`stebz-hidden-steps`<br/>`stebz-readable-reflective-name`<br/>`stebz-repeat-and-retry`                                                                                          | Bundle of Stebz modules                             |
| `stebz-aaa`     | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-aaa-keywords`<br/>`stebz-aaa-methods`<br/>`stebz-aaa-annotations`<br/>`stebz-clean-stack-trace`<br/>`stebz-hidden-steps`<br/>`stebz-readable-reflective-name`<br/>`stebz-repeat-and-retry`             | Bundle of Stebz modules in Arrange-Act-Assert style |
| `stebz-gherkin` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-methods`<br/>`stebz-annotations`<br/>`stebz-gherkin-keywords`<br/>`stebz-gherkin-methods`<br/>`stebz-gherkin-annotations`<br/>`stebz-clean-stack-trace`<br/>`stebz-hidden-steps`<br/>`stebz-readable-reflective-name`<br/>`stebz-repeat-and-retry` | Bundle of Stebz modules in Gherkin style            |

#### Integration:

| module               | include / depends on                                              | description                                          |
|----------------------|-------------------------------------------------------------------|------------------------------------------------------|
| `stebz-allure`       | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Allure report integration                            |
| `stebz-qase`         | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Qase report integration                              |
| `stebz-reportportal` | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | ReportPortal report integration                      |
| `stebz-testit`       | `stebz-utils`<br/>`stebz-core`<br/>`stebz-annotations` (optional) | Test IT report integration                           |
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
  .withName(name -> "name prefix " + name)
  .withBodyOf(originBody -> () -> {
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
  .withBefore(() -> Logger.info("..."))
  .withOnSuccess(() -> Logger.info("..."))
  .withOnFailure(() -> Logger.warn("...")));
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

### Arrange-Act-Assert style

To use Stebz in Arrange-Act-Assert style you need to use `stebz-aaa` dependency.

Methods way:

<!-- @formatter:off -->
```java
Arrange(runnableStep);
Act(consumerStep, 123);
Assert("quick step", () -> {
  // step body
});
Assert("quick step", () -> {
  // step body
});
```
<!-- @formatter:on -->

Annotations way:

<!-- @formatter:off -->
```java
@Arrange("step name")
@WithExpectedResult("step expected result")
@WithComment("step comment")
public static RunnableStep methodStep(String parameter) { return RunnableStep.of(() -> {
  // step body
}); }

@Act("step name")
@WithParams
public static void quickStep(String parameter) {
  // step body
}
```
<!-- @formatter:on -->

### Gherkin style

To use Stebz in Gherkin style you need to use `stebz-gherkin` dependency.

Methods way:

<!-- @formatter:off -->
```java
Given(runnableStep);
When(consumerStep, 123);
Then("quick step", () -> {
  // step body
});
And("quick step", () -> {
  // step body
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
public static void quickStep(String parameter) {
  // step body
}
```
<!-- @formatter:on -->

### TDD / BDD

You can write tests before implementing their steps.

<!-- @formatter:off -->
```java
@Test
@Disabled
void simpleSteps() {
  step("user authorized as user1");
  step("user sees balance 10000");
  step("user sees logout button");
}

@Test
@Disabled
void aaaSteps() {
  Act("user authorized as user1");
  Assert("user sees balance 10000");
  Assert("user sees logout button");
}

@Test
@Disabled
void gherkinSteps() {
  When("user authorized as user1");
  Then("user sees balance 10000");
  And("user sees logout button");
}
```
<!-- @formatter:on -->

You can specify additional attributes, such as parameters and expected result.

<!-- @formatter:off -->
```java
step("user authorized as simple user",
  params("username", "user1", "password", "12345"),
  "the authorization form is hidden, the user sees the header");
```
<!-- @formatter:on -->

For greater reliability, you can write tests in a different style. If the step is started, a `StepNotImplementedError`
will be thrown.

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

Later these steps can be implemented and can be used in other tests.

### Listeners

Processing of steps occurs in listeners.

Examples of listener modules: `stebz-allure`, `stebz-qase`, `stebz-reportportal`, `stebz-testit`, `stebz-system-out`.

`StepListener` interface:

<!-- @formatter:off -->
```java
public interface StepListener {

  void onStepStart(StepObj<?> step,
                   NullableOptional<Object> context);

  void onStepSuccess(StepObj<?> step,
                     NullableOptional<Object> context,
                     NullableOptional<Object> result);

  void onStepFailure(StepObj<?> step,
                     NullableOptional<Object> context,
                     Throwable exception);
}
```
<!-- @formatter:on -->

To create a custom listener you need to implement the `org.stebz.listener.StepListener` interface
and [specify it via SPI mechanism or via properties](#stebz-core-module).

#### `stebz-allure` listener

Specify and configure main Allure dependency `io.qameta.allure:allure-java-commons`.

Then specify Stebz dependencies:

- `org.stebz:stebz` / `org.stebz:stebz-aaa` / `org.stebz:stebz-gherkin`
- `org.stebz:stebz-allure`

#### `stebz-qase` listener

Specify and configure main Qase dependency `io.qase:qase-java-commons`.

Then specify Stebz dependencies:

- `org.stebz:stebz` / `org.stebz:stebz-aaa` / `org.stebz:stebz-gherkin`
- `org.stebz:stebz-qase`

#### `stebz-reportportal` listener

Specify and configure main ReportPortal dependency `com.epam.reportportal:client-java`.

Then specify Stebz dependencies:

- `org.stebz:stebz` / `org.stebz:stebz-aaa` / `org.stebz:stebz-gherkin`
- `org.stebz:stebz-reportportal`

#### `stebz-testit` listener

Specify and configure main Test IT dependency `ru.testit:testit-java-commons`.

Then specify Stebz dependencies:

- `org.stebz:stebz` / `org.stebz:stebz-aaa` / `org.stebz:stebz-gherkin`
- `org.stebz:stebz-testit`

### Extensions

Extensions allow you to add additional behavior to steps. For example, replace the step name or body.

Examples of extension modules: `stebz-repeat-and-retry`, `stebz-readable-reflective-name`.

List on extension interfaces:

| interface                | description                                                              |
|--------------------------|--------------------------------------------------------------------------|
| `StebzExtension`         | Base extension interface                                                 |
| `InterceptStepContext`   | Intercepts and replaces step context                                     |
| `InterceptStep`          | Intercepts and replaces step                                             |
| `BeforeStepStart`        | Calling before step start (before `StepListener.onStepStart` method)     |
| `AfterStepStart`         | Calling after step start (after `StepListener.onStepStart` method)       |
| `InterceptStepResult`    | Intercepts and replaces step result                                      |
| `BeforeStepSuccess`      | Calling before step success (before `StepListener.onStepSuccess` method) |
| `AfterStepSuccess`       | Calling after step success (after `StepListener.onStepSuccess` method)   |
| `InterceptStepException` | Intercepts and replaces step exception                                   |
| `BeforeStepFailure`      | Calling before step failure (before `StepListener.onStepFailure` method) |
| `AfterStepFailure`       | Calling after step failure (after `StepListener.onStepFailure` method)   |

For example, the `InterceptStepException` interface.

<!-- @formatter:off -->
```java
public interface InterceptStepException extends StebzExtension {

  Throwable interceptStepException(StepObj<?> step,
                                   NullableOptional<Object> context,
                                   Throwable exception);
}
```
<!-- @formatter:on -->

And `BeforeStepFailure` interface.

<!-- @formatter:off -->
```java
public interface BeforeStepFailure extends StebzExtension {

  void beforeStepFailure(StepObj<?> step,
                         NullableOptional<Object> context,
                         Throwable exception);
}
```
<!-- @formatter:on -->

To create a custom extension you need to implement one or more `org.stebz.extension.StebzExtension` interfaces
and [specify it via SPI mechanism or via properties](#stebz-core-module).

#### `stebz-clean-stack-trace` extension

Removes references to Stebz and AspectJ from step exception stack trace.

<!-- @formatter:off -->
```java
class ExampleTest {

  @Test
  void test() {
    step("Step 1", () -> {
      step("Step 2", () -> {
        step("Step 3", () -> {
          throw new AssertionError();
        });
      });
    });
  }
}
```
<!-- @formatter:on -->

Error stack trace without extension:

<!-- @formatter:off -->
```
java.lang.AssertionError
  at my.project.ExampleTest.lambda$test$0(ExampleTest.java:14)
  at org.stebz.executor.StepExecutor$Of.lambda$execute$11(StepExecutor.java:179)
  at org.stebz.executor.StepExecutor$Of.exec(StepExecutor.java:236)
  at org.stebz.executor.StepExecutor$Of.execute(StepExecutor.java:178)
  at org.stebz.StebzMethods.step(StebzMethods.java:456)
  at my.project.ExampleTest.lambda$test$1(ExampleTest.java:13)
  at org.stebz.executor.StepExecutor$Of.lambda$execute$10(StepExecutor.java:169)
  at org.stebz.executor.StepExecutor$Of.exec(StepExecutor.java:236)
  at org.stebz.executor.StepExecutor$Of.execute(StepExecutor.java:168)
  at org.stebz.StebzMethods.step(StebzMethods.java:313)
  at my.project.ExampleTest.lambda$test$2(ExampleTest.java:12)
  at org.stebz.executor.StepExecutor$Of.lambda$execute$10(StepExecutor.java:169)
  at org.stebz.executor.StepExecutor$Of.exec(StepExecutor.java:236)
  at org.stebz.executor.StepExecutor$Of.execute(StepExecutor.java:168)
  at org.stebz.StebzMethods.step(StebzMethods.java:313)
  at my.project.ExampleTest.test(ExampleTest.java:11)
  at java.base/java.lang.reflect.Method.invoke(Method.java:580)
  at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
  at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
```
<!-- @formatter:on -->

Error stack trace with extension:

<!-- @formatter:off -->
```
java.lang.AssertionError
  at my.project.ExampleTest.lambda$test$0(ExampleTest.java:14)
  at my.project.ExampleTest.lambda$test$1(ExampleTest.java:13)
  at my.project.ExampleTest.lambda$test$2(ExampleTest.java:12)
  at my.project.ExampleTest.test(ExampleTest.java:11)
  at java.base/java.lang.reflect.Method.invoke(Method.java:580)
  at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
  at java.base/java.util.ArrayList.forEach(ArrayList.java:1596)
```
<!-- @formatter:on -->

#### `stebz-hidden-steps` extension

Extension that allows to hide several steps.

<!-- @formatter:off -->
```java
hiddenSteps(() -> {
  step("Step 1");
  step("Step 2");
  step("Step 3");
});
```
<!-- @formatter:on -->

There is also an alias method `hiddenArea`.

#### `stebz-readable-reflective-name` extension

Replaces underscores in a step name with spaces.

<!-- @formatter:off -->
```java
@Step
public static RunnableStep user_is_authorized(String username,
                                              String password) { return RunnableStep.of(() -> {
  // step body
}); }
```
<!-- @formatter:on -->

The name of this step will be "user is authorized".

It is also possible to specify the separator symbols manually.

<!-- @formatter:off -->
```java
@Step
@NameWordSeparator("__")
public static RunnableStep user__is__authorized(String username,
                                                String password) { return RunnableStep.of(() -> {
  // step body
}); }
```
<!-- @formatter:on -->

#### `stebz-repeat-and-retry` extension

Allows to modify the body of a step by adding simple or only on error repetitions.

Via annotations:

<!-- @formatter:off -->
```java
@Step
@WithRetry(count = 3, on = TimeoutException.class)
public static RunnableStep send_unstable_request() { return RunnableStep.of(() -> {
  // step body
}); }

@Step
@WithRepeat(count = 3)
public static RunnableStep send_request_multiple_times() { return RunnableStep.of(() -> {
  // step body
}); }
```
<!-- @formatter:on -->

Via step attributes:

<!-- @formatter:off -->
```java
step(send_request()
  .with(retry, retry().count(3).on(TimeoutException.class)));

step(send_request()
  .with(repeat, repeat().count(3)));
```
<!-- @formatter:on -->

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

#### `stebz-aaa-keywords`, `stebz-aaa-methods`, `stebz-aaa-annotations` modules

| property                      | type      | default value | description               |
|-------------------------------|-----------|---------------|---------------------------|
| `stebz.aaa.keywords.setup`    | `String`  | `Setup:`      | value of Setup keyword    |
| `stebz.aaa.keywords.teardown` | `String`  | `Teardown:`   | value of Teardown keyword |
| `stebz.aaa.keywords.arrange`  | `String`  | `Arrange:`    | value of Arrange keyword  |
| `stebz.aaa.keywords.act`      | `String`  | `Act:`        | value of Act keyword      |
| `stebz.aaa.keywords.assert`   | `String`  | `Assert:`     | value of Assert keyword   |

#### `stebz-gherkin-keywords`, `stebz-gherkin-methods`, `stebz-gherkin-annotations` modules

| property                            | type                     | default value | description                 |
|-------------------------------------|--------------------------|---------------|-----------------------------|
| `stebz.gherkin.keywords.background` | `String`                 | `Background:` | value of Background keyword |
| `stebz.gherkin.keywords.conclusion` | `String`                 | `Conclusion:` | value of Conclusion keyword |
| `stebz.gherkin.keywords.rule`       | `String`                 | `Rule:`       | value of Rule keyword       |
| `stebz.gherkin.keywords.given`      | `String`                 | `Given`       | value of Given keyword      |
| `stebz.gherkin.keywords.when`       | `String`                 | `When`        | value of When keyword       |
| `stebz.gherkin.keywords.then`       | `String`                 | `Then`        | value of Then keyword       |
| `stebz.gherkin.keywords.and`        | `String`                 | `And`         | value of And keyword        |
| `stebz.gherkin.keywords.but`        | `String`                 | `But`         | value of But keyword        |

#### `stebz-clean-stack-trace` module

| property                                        | type      | default value                                   | description                       |
|-------------------------------------------------|-----------|-------------------------------------------------|-----------------------------------|
| `stebz.extensions.cleanStackTrace.enabled`      | `Boolean` | `true`                                          | enable extension                  |
| `stebz.extensions.cleanStackTrace.order`        | `Integer` | `10000`                                         | extension order                   |
| `stebz.extensions.cleanStackTrace.stebzLines`   | `Boolean` | `true` if `stebz-annotations` module is present | removes Stebz stack trace lines   |
| `stebz.extensions.cleanStackTrace.aspectjLines` | `Boolean` | `true` if `stebz-annotations` module is present | removes AspectJ stack trace lines |

#### `stebz-hidden-steps` module

| property                               | type      | default value | description      |
|----------------------------------------|-----------|---------------|------------------|
| `stebz.extensions.hiddenSteps.enabled` | `Boolean` | `true`        | enable extension |
| `stebz.extensions.hiddenSteps.order`   | `Integer` | `10000`       | extension order  |

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

| property                                         | type                  | default value     | description                               |
|--------------------------------------------------|-----------------------|-------------------|-------------------------------------------|
| `stebz.listeners.allure.enabled`                 | `Boolean`             | `true`            | enable listener                           |
| `stebz.listeners.allure.order`                   | `Integer`             | `10000`           | listener order                            |
| `stebz.listeners.allure.onlyKeywordSteps`        | `Boolean`             | `false`           | hide steps without keywords               |
| `stebz.listeners.allure.keywordPosition`         | `AT_START` / `AT_END` | `AT_START`        | position of step keyword relative to name |
| `stebz.listeners.allure.keywordToUppercase`      | `Boolean`             | `false`           | converts keyword value to upper case      |
| `stebz.listeners.allure.processName`             | `Boolean`             | `true`            | process step name with parameters         |
| `stebz.listeners.allure.contextParam`            | `Boolean`             | `true`            | step context as parameter                 |
| `stebz.listeners.allure.contextParamName`        | `String`              | `Context`         | step context parameter name               |
| `stebz.listeners.allure.expectedResultParam`     | `Boolean`             | `true`            | step expected result as parameter         |
| `stebz.listeners.allure.expectedResultParamName` | `String`              | `Expected result` | step expected result parameter name       |
| `stebz.listeners.allure.commentParam`            | `Boolean`             | `true`            | step comment as parameter                 |
| `stebz.listeners.allure.commentParamName`        | `String`              | `Comment`         | step comment parameter name               |

#### `stebz-qase` module

| property                                     | type                  | default value | description                               |
|----------------------------------------------|-----------------------|---------------|-------------------------------------------|
| `stebz.listeners.qase.enabled`               | `Boolean`             | `true`        | enable listener                           |
| `stebz.listeners.qase.order`                 | `Integer`             | `10000`       | listener order                            |
| `stebz.listeners.qase.onlyKeywordSteps`      | `Boolean`             | `false`       | hide steps without keywords               |
| `stebz.listeners.qase.keywordPosition`       | `AT_START` / `AT_END` | `AT_START`    | position of step keyword relative to name |
| `stebz.listeners.qase.keywordToUppercase`    | `Boolean`             | `false`       | converts keyword value to upper case      |
| `stebz.listeners.qase.processName`           | `Boolean`             | `true`        | process step name with parameters         |
| `stebz.listeners.qase.contextParam`          | `Boolean`             | `true`        | step context as parameter                 |
| `stebz.listeners.qase.contextParamName`      | `String`              | `Context`     | step context parameter name               |
| `stebz.listeners.qase.commentAttachment`     | `Boolean`             | `true`        | step comment as attachment                |
| `stebz.listeners.qase.commentAttachmentName` | `String`              | `Comment`     | step comment attachment name              |

#### `stebz-reportportal` module

| property                                              | type                  | default value     | description                                |
|-------------------------------------------------------|-----------------------|-------------------|--------------------------------------------|
| `stebz.listeners.reportportal.enabled`                | `Boolean`             | `true`            | enable listener                            |
| `stebz.listeners.reportportal.order`                  | `Integer`             | `10000`           | listener order                             |
| `stebz.listeners.reportportal.onlyKeywordSteps`       | `Boolean`             | `false`           | hide steps without keywords                |
| `stebz.listeners.reportportal.keywordPosition`        | `AT_START` / `AT_END` | `AT_START`        | position of step keyword relative to name  |
| `stebz.listeners.reportportal.keywordToUppercase`     | `Boolean`             | `false`           | converts keyword value to upper case       |
| `stebz.listeners.reportportal.processName`            | `Boolean`             | `true`            | process step name with parameters          |
| `stebz.listeners.reportportal.contextParam`           | `Boolean`             | `true`            | step context as parameter                  |
| `stebz.listeners.reportportal.contextParamName`       | `String`              | `Context`         | context parameter name                     |
| `stebz.listeners.reportportal.contextDesc`            | `Boolean`             | `false`           | step context as description part           |
| `stebz.listeners.reportportal.contextDescName`        | `String`              | `Context`         | step context description part name         |
| `stebz.listeners.reportportal.expectedResultDesc`     | `Boolean`             | `true`            | step expected result as description part   |
| `stebz.listeners.reportportal.expectedResultDescName` | `String`              | `Expected result` | step expected result description part name |
| `stebz.listeners.reportportal.commentDesc`            | `Boolean`             | `true`            | step comment as description part           |
| `stebz.listeners.reportportal.commentDescName`        | `String`              | `Comment`         | step comment description part name         |

#### `stebz-testit` module

| property                                        | type                  | default value     | description                                |
|-------------------------------------------------|-----------------------|-------------------|--------------------------------------------|
| `stebz.listeners.testit.enabled`                | `Boolean`             | `true`            | enable listener                            |
| `stebz.listeners.testit.order`                  | `Integer`             | `10000`           | listener order                             |
| `stebz.listeners.testit.onlyKeywordSteps`       | `Boolean`             | `false`           | hide steps without keywords                |
| `stebz.listeners.testit.keywordPosition`        | `AT_START` / `AT_END` | `AT_START`        | position of step keyword relative to name  |
| `stebz.listeners.testit.keywordToUppercase`     | `Boolean`             | `false`           | converts keyword value to upper case       |
| `stebz.listeners.testit.processName`            | `Boolean`             | `true`            | process step name with parameters          |
| `stebz.listeners.testit.contextParam`           | `Boolean`             | `true`            | step context as parameter                  |
| `stebz.listeners.testit.contextParamName`       | `String`              | `Context`         | context parameter name                     |
| `stebz.listeners.testit.contextDesc`            | `Boolean`             | `false`           | step context as description part           |
| `stebz.listeners.testit.contextDescName`        | `String`              | `Context`         | step context description part name         |
| `stebz.listeners.testit.expectedResultDesc`     | `Boolean`             | `true`            | step expected result as description part   |
| `stebz.listeners.testit.expectedResultDescName` | `String`              | `Expected result` | step expected result description part name |
| `stebz.listeners.testit.commentDesc`            | `Boolean`             | `true`            | step comment as description part           |
| `stebz.listeners.testit.commentDescName`        | `String`              | `Comment`         | step comment description part name         |

#### `stebz-system-out` module

| property                                     | type                  | default value | description                               |
|----------------------------------------------|-----------------------|---------------|-------------------------------------------|
| `stebz.listeners.systemout.enabled`          | `Boolean`             | `true`        | enable listener                           |
| `stebz.listeners.systemout.order`            | `Integer`             | `10000`       | listener order                            |
| `stebz.listeners.systemout.indent`           | `Integer`             | `2`           | number of spaces in indentation           |
| `stebz.listeners.systemout.onlyKeywordSteps` | `Boolean`             | `false`       | hide steps without keywords               |
| `stebz.listeners.systemout.keywordPosition`  | `AT_START` / `AT_END` | `AT_START`    | position of step keyword relative to name |
| `stebz.listeners.systemout.params`           | `Boolean`             | `true`        | show step params                          |
| `stebz.listeners.systemout.comment`          | `Boolean`             | `true`        | show step comment                         |

## Contributing

### How to contribute

See [CONTRIBUTING.md](CONTRIBUTING.md) for specific guidelines.

### Contributors

* [@evpl](https://github.com/evpl) as Evgenii Plugatar

## License

Stebz is open-source project, and distributed under the [MIT](LICENSE) license.
