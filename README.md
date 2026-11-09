# Stebz

[![Maven Central](https://img.shields.io/maven-central/v/org.stebz/stebz-core?color=blue)](https://central.sonatype.com/search?namespace=org.stebz&sort=name)
[![Javadoc](https://img.shields.io/badge/javadoc-latest-blue.svg)](https://javadoc.io/doc/org.stebz)
[![License](https://img.shields.io/github/license/stebz/stebz?color=blue)](https://github.com/stebz/stebz/blob/main/LICENSE)
[![Maven Central Last Update](https://img.shields.io/maven-central/last-update/org.stebz/stebz-core?color=blue)](https://central.sonatype.com/search?namespace=org.stebz&sort=name)

[![GitHub Actions Workflow Status](https://img.shields.io/github/actions/workflow/status/stebz/stebz/tests.yml?branch=main)](https://github.com/stebz/stebz/actions/workflows/tests.yml/)
[![Codecov](https://codecov.io/gh/stebz/stebz/graph/badge.svg)](https://codecov.io/gh/stebz/stebz)
[![GitHub commits since latest release](https://img.shields.io/github/commits-since/stebz/stebz/latest?color=brightgreen)](https://github.com/stebz/stebz/commits/main/)
[![GitHub last commit](https://img.shields.io/github/last-commit/stebz/stebz?color=brightgreen)](https://github.com/stebz/stebz/commits/main/)

[![Telegram](https://img.shields.io/badge/-telegram-black?color=blue&logo=telegram&label=chat)](https://t.me/stebz_en/)
[![Telegram RU](https://img.shields.io/badge/-telegram-black?color=blue&logo=telegram&label=chat%20(ru))](https://t.me/stebz_ru/)

**Stebz is a lightweight, modular Java framework that makes test steps first-class objects — readable, enrichable
with extra attributes on the fly, and automatically reported to any popular test management system.**

With Stebz, your tests read like scenarios instead of a wall of statements: every action and every check is an
explicit step with a name, parameters, an expected result and a comment. Steps are immutable objects you define
once, reuse everywhere and enrich right before execution — add a name prefix, mask a password parameter, attach an
expected result or an on-failure handler — without touching the original step definition. Write your steps in plain
Java, in Arrange-Act-Assert or in Gherkin style, sketch them before the implementation even exists (TDD/BDD), and
let listener integrations stream every step, with all of its attributes, into Allure, Qase, ReportPortal or
Test IT. Stebz runs on Java 8 or later, works with any test runner and build tool, and ships as small modules so
you only ever add what you actually need.

## Contents

* [Highlights](#highlights)
* [A taste of Stebz](#a-taste-of-stebz)
* [Requirements](#requirements)
* [Installation](#installation)
  * [Step bundles](#step-bundles)
  * [Reporting integrations](#reporting-integrations)
  * [BOM](#bom)
  * [AspectJ weaving](#aspectj-weaving)
* [Modules](#modules)
* [Step objects](#step-objects)
* [Enriching steps on the fly](#enriching-steps-on-the-fly)
* [Running steps](#running-steps)
* [Step context and tuples](#step-context-and-tuples)
* [Attributes](#attributes)
  * [Default attributes](#default-attributes)
  * [Custom attributes](#custom-attributes)
* [Annotations](#annotations)
  * [Annotating step objects](#annotating-step-objects)
  * [Annotating methods and constructors](#annotating-methods-and-constructors)
  * [Default annotations](#default-annotations)
  * [Custom attribute annotations](#custom-attribute-annotations)
  * [Capturing annotated calls as step objects](#capturing-annotated-calls-as-step-objects)
* [Writing styles](#writing-styles)
  * [Arrange-Act-Assert style](#arrange-act-assert-style)
  * [Gherkin style](#gherkin-style)
  * [TDD / BDD](#tdd--bdd)
* [Extensions](#extensions)
  * [How extensions work](#how-extensions-work)
  * [`stebz-clean-stack-trace`](#stebz-clean-stack-trace)
  * [`stebz-empty-steps`](#stebz-empty-steps)
  * [`stebz-hidden-steps`](#stebz-hidden-steps)
  * [`stebz-soft-asserted-steps`](#stebz-soft-asserted-steps)
  * [`stebz-readable-reflective-name`](#stebz-readable-reflective-name)
  * [`stebz-repeat-and-retry`](#stebz-repeat-and-retry)
  * [Writing your own extension](#writing-your-own-extension)
* [Listeners](#listeners)
  * [How listeners work](#how-listeners-work)
  * [Writing your own listener](#writing-your-own-listener)
* [Configuration](#configuration)
  * [How configuration is resolved](#how-configuration-is-resolved)
  * [Core properties](#core-properties)
  * [Registering extensions and listeners](#registering-extensions-and-listeners)
  * [Keyword properties](#keyword-properties)
  * [Annotation module properties](#annotation-module-properties)
  * [Extension module properties](#extension-module-properties)
  * [Listener module properties](#listener-module-properties)
* [Examples](#examples)
* [Contributing](#contributing)
* [Support](#support)
* [License](#license)

## Highlights

* **Steps are objects.** A step is an immutable value with attributes (name, params, expected result, comment,
  hidden, keyword) and a body. Define it once, reuse it everywhere, pass it around, compose it.
* **Enrichment on the fly.** Any step can be customized at the call site — rename it, add or mask a param, attach
  an expected result or a failure handler — while the original definition stays untouched.
* **Three writing styles.** Plain Java, Arrange-Act-Assert (`Arrange` / `Act` / `Assert`) and Gherkin (`Given` /
  `When` / `Then` / `And` / `But`). The same step objects, the same reports — pick the style your team
  prefers.
* **Annotations.** Prefer declarative steps? Annotate fields, methods and constructors with `@Step`, `@WithName`,
  `@WithParams` and friends, and Stebz turns them into full-fledged steps via AspectJ.
* **Extensions.** Cross-cutting step behavior — hiding, soft assertions, repeating, retrying, clean stack traces,
  readable reflective names — is plugged in as extensions, and you can write your own.
* **Reporting integrations.** Every step is streamed to Allure, Qase, ReportPortal or Test IT (or just to the
  console) by a listener, so your reports become structured, human-readable scenarios rather than raw logs.
* **Runs everywhere.** Java 8+, any test runner (JUnit, TestNG, a plain `main` method…), Maven or Gradle.
* **Modular.** Take a bundle with everything included, or assemble your own set from small single-purpose modules.

## A taste of Stebz

A JUnit 5 test written with Stebz. Reusable steps live in helper methods, quick checks are created inline, and
attributes are added on the fly exactly where they are relevant:

<!-- @formatter:off -->
```java
import static org.stebz.core.attribute.StepAttribute.params;
import static org.stebz.core.step.StepObj.stepOf;
import static org.stebz.method.StebzMethods.step;

class BalanceTest {

  @Test
  void userSeesUpToDateBalance() {
    // reusable step, enriched on the fly: the password is masked in reports
    step(user_is_authorized_as("user1", "123456")
      .withMaskedParam("password"));

    // the same reusable step, with an expected result and a comment added just for this test
    step(user_sees_balance(10_000L)
      .withExpectedResult("the header shows the current balance")
      .withComment("user1 always has 10 000 in the demo environment"));

    // a quick step: no reusable object needed
    step("user sees the logout button", "the header contains a logout button", () -> {
      // assertions
    });
  }

  static RunnableStep user_is_authorized_as(String username, String password) {
    return stepOf("user is authorized as {username}",
      params("username", username, "password", password), () -> {
        // authorization logic
      });
  }

  static ConsumerStep<Long> user_sees_balance(long expected) {
    return stepOf("user sees their balance", userId -> {
      // assertions
    }).withAddedParam("expected", expected);
  }
}
```
<!-- @formatter:on -->

Steps can also be executed against a shared context with `around`: consumer steps receive the context and keep
the chain going, function steps transform it and return the result:

<!-- @formatter:off -->
```java
User user = around("user1")
  .step(authorize_current_session())       // RunnableStep, the chain continues
  .step(find_user_by_name());              // FunctionStep<String, User>, receives "user1", returns User

around(user)
  .step(user_should_have_role("ADMIN"))    // ConsumerStep<User>, receives "user"
  .step(user_should_be_active());          // ConsumerStep<User>
```
<!-- @formatter:on -->

Prefer BDD vocabulary? The same steps read naturally in the Gherkin style:

<!-- @formatter:off -->
```java
When(user_is_authorized_as("user1", "123456").withMaskedParam("password"));
Then(user_sees_balance(10_000L));
And(user_sees_the_logout_button());
```
<!-- @formatter:on -->

Or in the Arrange-Act-Assert style:

<!-- @formatter:off -->
```java
Act(user_is_authorized_as("user1", "123456").withMaskedParam("password"));
Assert(user_sees_balance(10_000L));
Assert(user_sees_the_logout_button());
```
<!-- @formatter:on -->

See a complete runnable project in the
[examples repository](https://github.com/stebz/stebz-maven-junit5-allure-example).

## Requirements

Stebz requires **Java 8 or later** and works with any test runner and build tool. The annotations support is
built on AspectJ, so projects using annotations need the AspectJ weaver — see
[AspectJ weaving](#aspectj-weaving).

## Installation

### Step bundles

In most cases it is enough to add one bundle dependency:

| bundle          | style              | what it includes                                                        |
| --------------- | ------------------ | ----------------------------------------------------------------------- |
| `stebz`         | plain              | core, methods, annotations and all extensions (see [Modules](#modules)) |
| `stebz-aaa`     | Arrange-Act-Assert | everything from `stebz` plus AAA keywords, methods and annotations      |
| `stebz-gherkin` | Gherkin            | everything from `stebz` plus Gherkin keywords, methods and annotations  |

Maven:

<!-- @formatter:off -->
```xml
<dependencies>
  <dependency>
    <groupId>org.stebz</groupId>
    <artifactId>stebz</artifactId> <!-- or stebz-aaa / stebz-gherkin -->
    <version>2.0</version>
  </dependency>
</dependencies>
```
<!-- @formatter:on -->

Gradle (Groovy):

<!-- @formatter:off -->
```groovy
dependencies {
  implementation 'org.stebz:stebz:2.0'
}
```
<!-- @formatter:on -->

Gradle (Kotlin):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation("org.stebz:stebz:2.0")
}
```
<!-- @formatter:on -->

### Reporting integrations

To send steps to a reporting system, add one of the integration modules **together with the reporting system's own
dependency** (Stebz does not pull it in for you). The listener registers itself automatically — no code changes
needed:

| module                | reporting system   | you also add and configure                         |
| --------------------- | ------------------ | -------------------------------------------------- |
| `stebz-allure`        | Allure             | `io.qameta.allure:allure-java-commons`             |
| `stebz-qase`          | Qase               | `io.qase:qase-java-commons`                        |
| `stebz-reportportal`  | ReportPortal       | `com.epam.reportportal:client-java`                |
| `stebz-testit`        | Test IT            | `ru.testit:testit-java-commons`                    |
| `stebz-system-out`    | console output     | nothing — handy for local debugging                |

Example — Allure with the Gherkin bundle:

<!-- @formatter:off -->
```xml
<dependencies>
  <dependency>
    <groupId>org.stebz</groupId>
    <artifactId>stebz-gherkin</artifactId>
    <version>2.0</version>
  </dependency>
  <dependency>
    <groupId>org.stebz</groupId>
    <artifactId>stebz-allure</artifactId>
    <version>2.0</version>
  </dependency>
  <dependency>
    <groupId>io.qameta.allure</groupId>
    <artifactId>allure-java-commons</artifactId>
    <version>3.1.1</version>
  </dependency>
</dependencies>
```
<!-- @formatter:on -->

Each listener is fine-tuned with `stebz.*` properties — see [Listener module properties](#listener-module-properties).

### BOM

To keep all Stebz module versions consistent, import the BOM and omit versions in your dependencies:

Maven:

<!-- @formatter:off -->
```xml
<dependencyManagement>
  <dependencies>
    <dependency>
      <groupId>org.stebz</groupId>
      <artifactId>stebz-bom</artifactId>
      <version>2.0</version>
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
  implementation platform('org.stebz:stebz-bom:2.0')
}
```
<!-- @formatter:on -->

Gradle (Kotlin):

<!-- @formatter:off -->
```kotlin
dependencies {
  implementation(platform("org.stebz:stebz-bom:2.0"))
}
```
<!-- @formatter:on -->

### AspectJ weaving

If your project already uses Allure / Qase / ReportPortal / Test IT with their `@Step` annotations, the AspectJ
weaver is already configured and Stebz annotations work out of the box.

Otherwise, enable load-time weaving with the AspectJ agent. Maven:

<!-- @formatter:off -->
```xml
<properties>
  <aspectj.version>1.9.27</aspectj.version>
</properties>

<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-surefire-plugin</artifactId>
  <version>3.6.1</version>
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
def aspectJVersion = '1.9.27'

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
val aspectJVersion = "1.9.27"

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

## Modules

All modules are published to Maven Central under the `org.stebz` group. Bundles exist so that you usually don't
have to think about this table — but every module can also be consumed individually.

**Main:**

| module                | description                                                           |
| --------------------- | --------------------------------------------------------------------- |
| `stebz-utils`         | Common utilities shared by all modules                                |
| `stebz-core`          | Steps, attributes, the step executor, extension and listener APIs     |
| `stebz-methods`       | `step`, `around` and friends for running step objects and quick steps |
| `stebz-annotations`   | Annotations and the AspectJ aspects that process them                 |

**Styles:**

| module                       | depends on                                    | description                                           |
| ---------------------------- | --------------------------------------------- | ----------------------------------------------------- |
| `stebz-aaa-keywords`         | `stebz-core`                                  | Arrange-Act-Assert keywords                           |
| `stebz-aaa-methods`          | `stebz-core`, `stebz-aaa-keywords`            | `Arrange` / `Act` / `Assert` step-running methods     |
| `stebz-aaa-annotations`      | `stebz-annotations`, `stebz-aaa-keywords`     | `@Arrange` / `@Act` / `@Assert` annotations           |
| `stebz-gherkin-keywords`     | `stebz-core`                                  | Gherkin keywords                                      |
| `stebz-gherkin-methods`      | `stebz-core`, `stebz-gherkin-keywords`        | `Given` / `When` / `Then` step-running methods        |
| `stebz-gherkin-annotations`  | `stebz-annotations`, `stebz-gherkin-keywords` | `@Given` / `@When` / `@Then` annotations              |

**Extensions:**

| module                             | description                                                                  |
| ---------------------------------- | ---------------------------------------------------------------------------- |
| `stebz-clean-stack-trace`          | Strips Stebz and AspectJ lines from step exception stack traces              |
| `stebz-empty-steps`                | Runs steps with their bodies replaced by empty ones                          |
| `stebz-hidden-steps`               | Hides several steps from reports                                             |
| `stebz-soft-asserted-steps`        | Soft-asserts several steps: collects failures instead of failing fast        |
| `stebz-readable-reflective-name`   | Turns reflective step names like `user_is_authorized` into readable ones     |
| `stebz-repeat-and-retry`           | Repeats step bodies, or retries them on specific error types                 |

**Bundles:**

| module             | includes                                            |
| ------------------ | --------------------------------------------------- |
| `stebz`            | main modules + all extensions                       |
| `stebz-aaa`        | `stebz` + AAA keywords, methods and annotations     |
| `stebz-gherkin`    | `stebz` + Gherkin keywords, methods and annotations |

**Integrations:**

| module                 | description                                                 |
| ---------------------- | ----------------------------------------------------------- |
| `stebz-allure`         | Allure report integration                                   |
| `stebz-qase`           | Qase report integration                                     |
| `stebz-reportportal`   | ReportPortal report integration                             |
| `stebz-testit`         | Test IT report integration                                  |
| `stebz-system-out`     | Console output of every step (mainly for debugging)         |

## Step objects

A step is an immutable object that holds attributes and a body. There are four step types, by body shape:

| type                      | body                    | result        |
| ------------------------- | ----------------------- | ------------- |
| `RunnableStep`            | `() -> {}`              | none          |
| `SupplierStep<R>`         | `() -> R`               | `R`           |
| `ConsumerStep<T>`         | `T -> {}`               | none          |
| `FunctionStep<T, R>`      | `T -> R`                | `R`           |

Create steps with the `stepOf` factory (each type also has a short alias: `RStep`, `SStep`, `CStep`, `FStep`):

<!-- @formatter:off -->
```java
import static org.stebz.core.step.StepObj.stepOf;

RunnableStep runnableStep = stepOf("runnable step", () -> {
  // step body
});
SupplierStep<String> supplierStep = stepOf("supplier step", () -> {
  // step body
  return "result";
});
ConsumerStep<Integer> consumerStep = stepOf("consumer step", integer -> {
  // step body
});
FunctionStep<Integer, String> functionStep = stepOf("function step", integer -> {
  // step body
  return "result";
});
```
<!-- @formatter:on -->

Every "modification" of a step produces a **new** object — the original is never changed. Together with the
`with*` methods below, this is what makes on-the-fly enrichment safe and side-effect free.

Step bodies may throw any checked exception: Stebz rethrows it unchecked, so step helpers don't need
`throws` declarations.

## Enriching steps on the fly

This is the core idea of Stebz: define a step once, then adapt it to the exact test at the call site. All
`with*` methods return a new step and never mutate the original:

<!-- @formatter:off -->
```java
step(user_is_authorized_as("user1", "123456")
  .withName(name -> "auth as admin: " + name)                  // name, or a function of the current name
  .withExpectedResult("the user is logged in")                 // expected result
  .withComment("the demo environment never locks accounts")    // comment
  .withMaskedParam("password")                                 // param shown as *** in reports
  .withBefore(() -> AuthUtils.checkConnectivity())             // runs before the body
  .withOnSuccess(() -> Logger.info("authorized"))              // runs after a successful body
  .withOnFailure(() -> Logger.warn("authorization failed")));  // runs after a failed body
```
<!-- @formatter:on -->

Keyword attributes (`withKeyword(...)`) work the same way — the style modules simply set them for you. Other
highlights of the `with*` family:

* `withParam(name, value)` (an alias of `withAddedParam`) and `withAddedParams(map)` **add** to the existing
  params; `withParams(map)` **replaces** the whole params map; and `withParams(p -> ...)` hands you the map for
  arbitrary updates.
* `withMaskedParam("password")` / `withMaskedParams("login", "password")` / `withMaskedParams()` (mask everything)
  — masked values are rendered as `***` by listeners instead of the real value.
* Every attribute has a generator form (`withName(name -> ...)`) that receives the current value, so enrichment
  composes: applying `withName` twice prefixes and suffixes instead of overwriting.
* `without(attribute)` removes an attribute; `withBody(...)` / `withBodyOf(originBody -> ...)` replace or wrap
  the body.

## Running steps

Run a step with the static `step` method — the appropriate overload is chosen by the step type:

<!-- @formatter:off -->
```java
import static org.stebz.method.StebzMethods.step;

step(runnableStep);                          // RunnableStep

String result = step(supplierStep);          // SupplierStep<R> -> R

step(consumerStep, 123);                     // ConsumerStep<T> with a context

String result2 = step(functionStep, 123);    // FunctionStep<T, R> -> R
```
<!-- @formatter:on -->

If you don't need a reusable object, create a **quick step** inline — the `step` method accepts a name, an
expected result, params and the body directly:

<!-- @formatter:off -->
```java
step("open the dashboard", () -> {
  // step body
});

step("user sees the logout button", "the header contains a logout button", () -> {
  // step body
});

String id = step("create a new user",
  params("role", "admin", "name", "John"),    // step params
  "the user appears in the admin list",
  () -> createUser("John", "admin"));         // SupplierStep-like body, returns the id
```
<!-- @formatter:on -->

The `{paramName}` placeholders in a step name are filled from the step params when the name is processed for a
report, so `"user is authorized as {username}"` becomes `user is authorized as user1`.

### Running steps against a shared context

The `around` object executes steps against a shared context value: `RunnableStep` and `ConsumerStep` receive the
context and return the `around` object so steps can be chained, while `SupplierStep` and `FunctionStep` return
their result and end the chain:

<!-- @formatter:off -->
```java
import static org.stebz.method.StebzMethods.around;

User user = around("user1")                    // the initial context
  .step(user_is_authorized())                  // RunnableStep, the chain continues
  .step(find_user_by_name());                  // FunctionStep<String, User> -> User

around(user)
  .step(user_should_be_active())               // ConsumerStep<User>, receives the context
  .step(user_role());                          // FunctionStep<User, String> -> "admin"
```
<!-- @formatter:on -->

A whole block of steps can also be executed as a unit with `steps(...)` — or with `stepsResult(...)` when the
block should return a value:

<!-- @formatter:off -->
```java
steps(() -> {
  step(firstStep);
  step(secondStep);
});
```
<!-- @formatter:on -->

The [`hiddenSteps`](#stebz-hidden-steps) and [`softAssertedSteps`](#stebz-soft-asserted-steps) extensions use the
same block form to apply their behavior to a group of steps.

## Step context and tuples

When a step needs several input values, combine them into a tuple context instead of building a wrapper class:

<!-- @formatter:off -->
```java
import static org.stebz.core.context.Context.context;

ConsumerStep<Of2<Integer, String>> myStep = stepOf("my step", context((amount, currency) -> {
  // step body: amount is Integer, currency is String
}));

step(myStep, context(100, "USD"));

around(context(100, "USD"))
  .step(myStep);
```
<!-- @formatter:on -->

Tuples from `Of2` to `Of8` are supported out of the box.

## Attributes

### Default attributes

| attribute         | type                   | description                                                 |
| ----------------- | ---------------------- | ----------------------------------------------------------- |
| `keyword`         | `Keyword`              | style marker: `Given`, `When`, `Act`, …                     |
| `name`            | `String`               | human-readable step name, shown in reports                  |
| `params`          | `Map<String, Object>`  | step parameters, rendered by listeners                      |
| `expected result` | `String`               | what the step is expected to achieve                        |
| `comment`         | `String`               | free-form note for the report                               |
| `hidden`          | `Boolean`              | hides the step from reports (see [Extensions](#extensions)) |

### Custom attributes

Attributes are typed keys, not a fixed set — define your own and attach them to any step:

<!-- @formatter:off -->
```java
import org.stebz.core.attribute.SimpleStepAttribute;

static final SimpleStepAttribute<String> ISSUE = SimpleStepAttribute.nonNull("issue", "");

RunnableStep step = user_is_authorized_as("user1", "123456")
  .with(ISSUE, "PRJ-42");          // attach

String issue = step.get(ISSUE);    // read back
```
<!-- @formatter:on -->

Custom attributes shine in combination with custom listeners and extensions: your listener can read `ISSUE` from
every step and, say, link it to a ticket in your TMS.

## Annotations

If you'd rather declare step metadata than build it in code, use annotations. They are processed by AspectJ
aspects, so make sure the [weaver is enabled](#aspectj-weaving).

### Annotating step objects

Annotations on a field of a `StepObj` subtype, on a method returning one, or on the constructor of a class
implementing `StepObj` add attributes to the produced step:

<!-- @formatter:off -->
```java
@WithName("user is authorized")
@WithComment("uses the demo environment")
@WithParam(name = "environment", value = "demo")
public static final RunnableStep FIELD_STEP = stepOf(() -> {
  // step body
});

@WithName("user is authorized as {username}")
@WithParams
public static RunnableStep methodStep(String username, String password) {
  return stepOf(() -> {
    // step body
  });
}
```
<!-- @formatter:on -->

### Annotating methods and constructors

Put an annotation on any method or constructor and the call itself becomes a step — a **quick step** without a
step object:

<!-- @formatter:off -->
```java
@WithName("open the dashboard")
@WithParams
public static void openDashboard(String url) {
  // step body
}

public class User {

  @WithName("create a new user")
  @WithParams
  public User(String name, String role) {
    // step body
  }
}
```
<!-- @formatter:on -->

### Default annotations

| annotation              | effect                                                          |
| ----------------------- | --------------------------------------------------------------- |
| `@Step`                 | alias for `@WithName` (from the method name) + `@WithParams`    |
| `@WithName`             | sets the `name` attribute; supports `{param}` placeholders      |
| `@WithKeyword`          | sets the `keyword` attribute                                    |
| `@WithParams`           | adds all method / constructor parameters as params              |
| `@WithParam`            | adds one custom param; `@WithParam.List` adds several           |
| `@WithExpectedResult`   | sets the `expected result` attribute                            |
| `@WithComment`          | sets the `comment` attribute                                    |
| `@WithHidden`           | sets the `hidden` attribute                                     |
| `@Param`                | renames a method / constructor parameter in the report          |

### Custom attribute annotations

Annotations themselves can become step attributes. Mark your annotation with `@StepAttributeMarker` and its
instances will land in the step's attributes, keyed by the annotation class:

<!-- @formatter:off -->
```java
import org.stebz.annotation.StepAttributeMarker;

@Target({ElementType.METHOD, ElementType.CONSTRUCTOR, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@StepAttributeMarker
public @interface Issue {
  String value();
}
```

```java
@Issue("PRJ-42")
public static RunnableStep open_dashboard() { return stepOf(() -> {
  // step body
}); }
```

```java
import org.stebz.core.attribute.SimpleStepAttribute;

SimpleStepAttribute<Issue> issueAttr = SimpleStepAttribute.nullable(Issue.class);
Issue issue = step.get(issueAttr);   // Issue(value=PRJ-42)
```
<!-- @formatter:on -->

### Capturing annotated calls as step objects

Annotated methods and constructors can also be **captured** — turned into a step object without executing the
body, so you can enrich and run it later:

<!-- @formatter:off -->
```java
import static org.stebz.annotation.aspect.StepCaptor.captured;

RunnableStep step = captured(MySteps::openDashboard, "https://example.com");

step(captured(MySteps::openDashboard, "https://example.com")
  .withName("open the admin dashboard"));
```
<!-- @formatter:on -->

A captured constructor step creates a new instance on every execution of the returned step.

## Writing styles

The plain style (`step(...)`) is always available. Two styled dialects are built in; they use the same step
objects and the same listeners, they only add keywords to your steps.

### Arrange-Act-Assert style

Add the `stebz-aaa` dependency (or the `stebz-aaa-methods` / `stebz-aaa-annotations` modules). Keywords:
`SetUp` / `TearDown` / `Arrange` / `Act` / `Assert` (plus `*` for style-free steps).

Methods way:

<!-- @formatter:off -->
```java
import static org.stebz.aaa.method.StebzAAAMethods.*;

Arrange(a_user_exists("user1"));                 // Arrange or SetUp
Act(user_is_authorized_as("user1", "123456"));
Assert(user_sees_balance(10_000L));
Assert("user sees the logout button", () -> {
  // assertions
});
```
<!-- @formatter:on -->

Annotations way:

<!-- @formatter:off -->
```java
@Arrange("a user with role {role} exists")
@WithParams
public static RunnableStep a_user_exists(String role) { return stepOf(() -> {
  // step body
}); }

@Act("user is authorized as {username}")
@WithParams
public static void authorize(String username, String password) {
  // step body
}
```
<!-- @formatter:on -->

The style also has its own `around(...)` — every step in the chain gets the style keyword:

<!-- @formatter:off -->
```java
around(user)
  .Arrange(a_user_exists("user1"))
  .Act(user_is_authorized_as("user1", "123456"))
  .Assert(user_sees_balance(10_000L));
```
<!-- @formatter:on -->

### Gherkin style

Add the `stebz-gherkin` dependency (or the `stebz-gherkin-methods` / `stebz-gherkin-annotations` modules).
Keywords: `Background` / `Rule` / `Given` / `When` / `Then` / `And` / `But` (plus `Technical` and `*`).

Methods way:

<!-- @formatter:off -->
```java
import static org.stebz.gherkin.method.StebzGherkinMethods.*;

Given(a_user_exists("user1"));
When(user_is_authorized_as("user1", "123456"));
Then(user_sees_balance(10_000L));
And(user_sees_the_logout_button());
```
<!-- @formatter:on -->

Annotations way:

<!-- @formatter:off -->
```java
@Given("a user with role {role} exists")
@WithParams
public static RunnableStep a_user_exists(String role) { return stepOf(() -> {
  // step body
}); }

@When("user is authorized as {username}")
@WithParams
public static void authorize(String username, String password) {
  // step body
}
```
<!-- @formatter:on -->

The style also has its own `around(...)` — every step in the chain gets the style keyword:

<!-- @formatter:off -->
```java
around(user)
  .Given(a_user_exists("user1"))
  .When(user_is_authorized_as("user1", "123456"))
  .Then(user_sees_balance(10_000L));
```
<!-- @formatter:on -->

### TDD / BDD

You can write the scenario before any step is implemented. A bare `step("...")` call reports a step with an
empty body — nothing is executed, but the scenario shows up in reports, which makes early drafts readable in your
TMS:

<!-- @formatter:off -->
```java
@Test
@Disabled("not implemented yet")
void draft() {
  Given("a user with a positive balance");
  When("the user transfers the whole balance");
  Then("the balance is zero");
}
```
<!-- @formatter:on -->

For a louder contract, use not-implemented steps: they render in reports like normal steps, but throw
`StepNotImplementedError` the moment execution reaches them, so an unfinished scenario can never pass silently:

<!-- @formatter:off -->
```java
import static org.stebz.core.step.executable.RunnableStep.notImplemented;

@Test
void transferScenario() {
  Given(notImplemented().withName("a user with a positive balance"));
  When(notImplemented().withName("the user transfers the whole balance"));
  Then(notImplemented()
    .withName("the balance is zero")
    .withExpectedResult("the balance widget shows 0.00"));
}
```
<!-- @formatter:on -->

Implement the steps later and reuse them across tests.

## Extensions

### How extensions work

Extensions plug cross-cutting behavior into the step execution pipeline. Stebz ships with six extension modules;
you can add your own without touching the core. An extension implements `org.stebz.core.extension.StebzExtension`
plus one or more of the interception interfaces:

| interface                  | hook                                                       |
| -------------------------- | ---------------------------------------------------------- |
| `InterceptStepContext`     | replaces the step context before execution                 |
| `InterceptStep`            | replaces the step itself (attributes, body)                |
| `BeforeStepStart`          | runs before `StepListener.onStepStart`                     |
| `AfterStepStart`           | runs after `StepListener.onStepStart`                      |
| `InterceptStepResult`      | replaces the step result                                   |
| `BeforeStepSuccess`        | runs before `StepListener.onStepSuccess`                   |
| `AfterStepSuccess`         | runs after `StepListener.onStepSuccess`                    |
| `InterceptStepException`   | replaces / hides / swallows the step exception             |
| `BeforeStepFailure`        | runs before `StepListener.onStepFailure`                   |
| `AfterStepFailure`         | runs after `StepListener.onStepFailure`                    |

Extensions are ordered by their `order()` value (lower runs earlier); each shipped extension can be disabled or
reordered with properties (see [Extension module properties](#extension-module-properties)).

### `stebz-clean-stack-trace`

Without it, a failing nested step produces a stack trace polluted with Stebz and AspectJ frames:

<!-- @formatter:off -->
```
java.lang.AssertionError
  at my.project.ExampleTest.lambda$test$0(ExampleTest.java:14)
  at org.stebz.core.executor.StepExecutor$Of.lambda$execute$11(StepExecutor.java:179)
  at org.stebz.core.executor.StepExecutor$Of.exec(StepExecutor.java:236)
  ... 15 more framework lines
```
<!-- @formatter:on -->

The extension strips those lines, leaving only the frames that matter — your test and your step bodies. Related
exceptions (suppressed and cause chains) are cleaned as well. Enabled by default in every bundle.

### `stebz-empty-steps`

Runs a block of steps with every step body replaced by an empty one — the steps still appear in reports with
all their attributes, but nothing executes. Handy for dry-running a scenario:

<!-- @formatter:off -->
```java
emptySteps(() -> {
  step(send_payment(500));
  step(user_sees_balance(0));
});
```
<!-- @formatter:on -->

### `stebz-hidden-steps`

Hides a block of steps from reports:

<!-- @formatter:off -->
```java
hiddenSteps(() -> {
  step(prepare_test_data());
  step(warm_up_the_cache());
});
// reported steps continue here
step(user_sees_balance(10_000L));
```
<!-- @formatter:on -->

### `stebz-soft-asserted-steps`

Collects failures of a block of steps instead of failing at the first one, then throws a combined error at the
end — like AssertJ's `SoftAssertions`, but for steps:

<!-- @formatter:off -->
```java
softAssertedSteps(() -> {
  step(user_sees_balance(10_000L));
  step(user_sees_the_logout_button());
  step(user_sees_empty_cart());
});
```
<!-- @formatter:on -->

### `stebz-readable-reflective-name`

A step object created without an explicit name gets its name from the reflective call site — usually with
underscores, like `user_is_authorized`. The extension turns such names into readable ones:
`user is authorized`. Enabled by default in every bundle; it only touches names that still equal their
reflective origin.

### `stebz-repeat-and-retry`

Adds repetition to step bodies. Via annotations:

<!-- @formatter:off -->
```java
@Step
@WithRepeat(count = 3)
public static RunnableStep send_request_multiple_times() { return stepOf(() -> {
  // step body
}); }

@Step
@WithRetry(count = 3, delay = 500, on = TimeoutException.class)
public static RunnableStep send_unstable_request() { return stepOf(() -> {
  // step body
}); }
```
<!-- @formatter:on -->

* `@WithRepeat` runs the body `count` times (`skip` / `but` refine which exceptions count as failures).
* `@WithRetry` reruns the body on failure `count` times, with an optional `delay` between attempts, `on` the
  listed exception types (`but` excludes some of them).

Or via step attributes, for one-off cases:

<!-- @formatter:off -->
```java
import static org.stebz.repeatandretry.RepeatExtension.repeat;
import static org.stebz.repeatandretry.RetryExtension.retry;

step(send_unstable_request().with(retry, retry()
  .count(3)
  .delay(Duration.ofMillis(500))
  .on(TimeoutException.class)));

step(send_request().with(repeat, repeat().count(3)));
```
<!-- @formatter:on -->

### Writing your own extension

Implement `StebzExtension` plus the hooks you need. Example — an extension that adds a timestamp param to every
step:

<!-- @formatter:off -->
```java
import org.stebz.core.extension.StebzExtension;
import org.stebz.core.extension.InterceptStep;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;

public final class TimestampExtension implements InterceptStep {

  @Override
  public StepObj<?> interceptStep(StepObj<?> step,
                                  NullableOptional<Object> context) {
    return step.withAddedParam("timestamp", System.currentTimeMillis());
  }
}
```
<!-- @formatter:on -->

Register it via the Java SPI (create `META-INF/services/org.stebz.core.extension.StebzExtension` containing
`org.example.TimestampExtension`) or via the `stebz.extension.list` property — see
[Registering extensions and listeners](#registering-extensions-and-listeners).

## Listeners

### How listeners work

Listeners observe the step lifecycle and typically forward steps to a reporting system. That is the only way
Stebz itself touches your reports — all five integrations (`stebz-allure`, `stebz-qase`,
`stebz-reportportal`, `stebz-testit`, `stebz-system-out`) are just listeners, registered automatically via SPI
when you add the dependency.

The `StepListener` interface:

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

### Writing your own listener

Example — a minimal listener that prints every step to the console:

<!-- @formatter:off -->
```java
import org.stebz.core.listener.StepListener;
import org.stebz.core.step.StepObj;
import org.stebz.util.container.NullableOptional;

public final class ConsoleListener implements StepListener {

  @Override
  public void onStepStart(StepObj<?> step, NullableOptional<Object> context) {
    System.out.println("START " + step.getName());
  }

  @Override
  public void onStepSuccess(StepObj<?> step, NullableOptional<Object> context,
                            NullableOptional<Object> result) {
    System.out.println("OK    " + step.getName());
  }

  @Override
  public void onStepFailure(StepObj<?> step, NullableOptional<Object> context,
                            Throwable exception) {
    System.out.println("FAIL  " + step.getName() + " - " + exception);
  }
}
```
<!-- @formatter:on -->

Register it via the Java SPI (create `META-INF/services/org.stebz.core.listener.StepListener` containing
`org.example.ConsoleListener`) or via the `stebz.listener.list` property. Note that `stebz-system-out` already
does exactly this, with formatting options — reach for a custom listener when you have a system of your own.

## Configuration

### How configuration is resolved

Stebz reads its configuration from **system properties** first and from a **properties file** second. The file
path defaults to `stebz.properties` on the classpath and can be changed with the `stebz.properties.path`
system property.

<!-- @formatter:off -->
```properties
# stebz.properties
stebz.extension.autodetection=false
stebz.listener.list=org.example.ConsoleListener

stebz.gherkin.keyword.given=Дано
stebz.gherkin.keyword.when=Если
stebz.gherkin.keyword.then=Тогда

stebz.allure.onlyKeywordSteps=true
stebz.allure.keywordToUppercase=true
```
<!-- @formatter:on -->

### Core properties

| property                           | type                              | default             | description                           |
| ---------------------------------- | --------------------------------- | ------------------- | ------------------------------------- |
| `stebz.properties.path`            | `String`                          | `stebz.properties`  | path to the properties file           |
| `stebz.extension.enabled`          | `Boolean`                         | `true`              | enable extensions                     |
| `stebz.extension.list`             | class names, `,`-separated        | empty               | extensions to register explicitly     |
| `stebz.extension.autodetection`    | `Boolean`                         | `true`              | discover extensions via SPI           |
| `stebz.listener.enabled`           | `Boolean`                         | `true`              | enable listeners                      |
| `stebz.listener.list`              | class names, `,`-separated        | empty               | listeners to register explicitly      |
| `stebz.listener.autodetection`     | `Boolean`                         | `true`              | discover listeners via SPI            |

### Registering extensions and listeners

Two mechanisms, combinable:

* **SPI (default).** Libraries and integration modules register themselves through standard Java service files:
  `META-INF/services/org.stebz.core.extension.StebzExtension` for extensions and
  `META-INF/services/org.stebz.core.listener.StepListener` for listeners.
* **Explicit list.** Set `stebz.extension.list` / `stebz.listener.list` to a comma-separated list of class
  names. Combine with `stebz.extension.autodetection=false` to fully control which extensions are active:

<!-- @formatter:off -->
```properties
stebz.extension.autodetection=false
stebz.extension.list=org.stebz.readablereflectivename.ReadableReflectiveNameExtension,org.example.TimestampExtension
```
<!-- @formatter:on -->

### Keyword properties

Keyword values are plain strings — localize or restyle them as you like.

`stebz-aaa-keywords`:

| property                        | default        |
| ------------------------------- | -------------- |
| `stebz.aaa.keyword.setUp`       | `Set Up:`      |
| `stebz.aaa.keyword.tearDown`    | `Tear Down:`   |
| `stebz.aaa.keyword.arrange`     | `Arrange:`     |
| `stebz.aaa.keyword.act`         | `Act:`         |
| `stebz.aaa.keyword.assert`      | `Assert:`      |
| `stebz.aaa.keyword.asterisk`    | `*`            |

`stebz-gherkin-keywords`:

| property                            | default         |
| ----------------------------------- | --------------- |
| `stebz.gherkin.keyword.background`  | `Background:`   |
| `stebz.gherkin.keyword.rule`        | `Rule:`         |
| `stebz.gherkin.keyword.given`       | `Given`         |
| `stebz.gherkin.keyword.when`        | `When`          |
| `stebz.gherkin.keyword.then`        | `Then`          |
| `stebz.gherkin.keyword.and`         | `And`           |
| `stebz.gherkin.keyword.but`         | `But`           |
| `stebz.gherkin.keyword.asterisk`    | `*`             |
| `stebz.gherkin.keyword.technical`   | `Technical:`    |

### Annotation module properties

| property                              | type        | default   | description                                        |
| ------------------------------------- | ----------- | --------- | -------------------------------------------------- |
| `stebz.aaa.annotation.enabled`        | `Boolean`   | `true`    | enable `@Arrange` / `@Act` / `@Assert` / … support |
| `stebz.aaa.annotation.order`          | `Integer`   | `5000`    | extension order                                    |
| `stebz.gherkin.annotation.enabled`    | `Boolean`   | `true`    | enable `@Given` / `@When` / `@Then` / … support    |
| `stebz.gherkin.annotation.order`      | `Integer`   | `5000`    | extension order                                    |

### Extension module properties

All extensions follow the same pattern: `enabled`, `order`, plus their own options.

`stebz-clean-stack-trace`:

| property                                    | type        | default                                  | description                      |
| ------------------------------------------- | ----------- | ---------------------------------------- | -------------------------------- |
| `stebz.cleanStackTrace.enabled`             | `Boolean`   | `true`                                   | enable the extension             |
| `stebz.cleanStackTrace.order`               | `Integer`   | `10000`                                  | extension order                  |
| `stebz.cleanStackTrace.clearRelated`        | `Boolean`   | `true`                                   | clean cause / suppressed chains  |
| `stebz.cleanStackTrace.clearStebzLines`     | `Boolean`   | `true`                                   | strip Stebz frames               |
| `stebz.cleanStackTrace.clearAspectjLines`   | `Boolean`   | `true` if `stebz-annotations` is present | strip AspectJ frames             |

`stebz-empty-steps`:

| property                          | type        | default    | description                                |
| --------------------------------- | ----------- | ---------- | ------------------------------------------ |
| `stebz.emptySteps.enabled`        | `Boolean`   | `true`     | enable the extension                       |
| `stebz.emptySteps.order`          | `Integer`   | `20000`    | extension order                            |
| `stebz.emptySteps.forEach`        | `Boolean`   | `false`    | empty **every** step in the application    |

`stebz-hidden-steps`:

| property                         | type        | default    | description                |
| -------------------------------- | ----------- | ---------- | -------------------------- |
| `stebz.hiddenSteps.enabled`      | `Boolean`   | `true`     | enable the extension       |
| `stebz.hiddenSteps.order`        | `Integer`   | `10000`    | extension order            |

`stebz-soft-asserted-steps`:

| property                               | type        | default    | description                |
| -------------------------------------- | ----------- | ---------- | -------------------------- |
| `stebz.softAssertedSteps.enabled`      | `Boolean`   | `true`     | enable the extension       |
| `stebz.softAssertedSteps.order`        | `Integer`   | `20000`    | extension order            |

`stebz-readable-reflective-name`:

| property                                 | type        | default    | description                |
| ---------------------------------------- | ----------- | ---------- | -------------------------- |
| `stebz.readableReflectiveName.enabled`   | `Boolean`   | `true`     | enable the extension       |
| `stebz.readableReflectiveName.order`     | `Integer`   | `10000`    | extension order            |

`stebz-repeat-and-retry`:

| property                     | type        | default    | description                |
| ---------------------------- | ----------- | ---------- | -------------------------- |
| `stebz.repeat.enabled`       | `Boolean`   | `true`     | enable repeating           |
| `stebz.repeat.order`         | `Integer`   | `10000`    | extension order            |
| `stebz.retry.enabled`        | `Boolean`   | `true`     | enable retrying            |
| `stebz.retry.order`          | `Integer`   | `10000`    | extension order            |

### Listener module properties

All listeners share a common set of formatting options: `order`, `onlyKeywordSteps` (report only steps that
have a keyword), `keywordPosition` (`AT_START` / `AT_END`), `keywordToUppercase`, and `processName` (fill
`{param}` placeholders in names from step params, default `true`).

`stebz-allure`:

| property                                   | type                  | default             | description                             |
| ------------------------------------------ | --------------------- | ------------------- | --------------------------------------- |
| `stebz.allure.enabled`                     | `Boolean`             | `true`              | enable the listener                     |
| `stebz.allure.order`                       | `Integer`             | `10000`             | listener order                          |
| `stebz.allure.onlyKeywordSteps`            | `Boolean`             | `false`             | report only steps with a keyword        |
| `stebz.allure.keywordPosition`             | `AT_START` / `AT_END` | `AT_START`          | keyword position relative to the name   |
| `stebz.allure.keywordToUppercase`          | `Boolean`             | `false`             | uppercase the keyword                   |
| `stebz.allure.processName`                 | `Boolean`             | `true`              | fill `{param}` placeholders in names    |
| `stebz.allure.contextParam`                | `Boolean`             | `false`             | add the step context as a param         |
| `stebz.allure.contextParamName`            | `String`              | `Context`           | name of the context param               |
| `stebz.allure.expectedResultParam`         | `Boolean`             | `true`              | add the expected result as a param      |
| `stebz.allure.expectedResultParamName`     | `String`              | `Expected result`   | name of the expected result param       |
| `stebz.allure.commentParam`                | `Boolean`             | `true`              | add the comment as a param              |
| `stebz.allure.commentParamName`            | `String`              | `Comment`           | name of the comment param               |

`stebz-qase`:

| property                                     | type                  | default      | description                             |
| -------------------------------------------- | --------------------- | ------------ | --------------------------------------- |
| `stebz.qase.enabled`                         | `Boolean`             | `true`       | enable the listener                     |
| `stebz.qase.order`                           | `Integer`             | `10000`      | listener order                          |
| `stebz.qase.onlyKeywordSteps`                | `Boolean`             | `false`      | report only steps with a keyword        |
| `stebz.qase.keywordPosition`                 | `AT_START` / `AT_END` | `AT_START`   | keyword position relative to the name   |
| `stebz.qase.keywordToUppercase`              | `Boolean`             | `false`      | uppercase the keyword                   |
| `stebz.qase.processName`                     | `Boolean`             | `true`       | fill `{param}` placeholders in names    |
| `stebz.qase.contextParam`                    | `Boolean`             | `false`      | add the step context as a param         |
| `stebz.qase.contextParamName`                | `String`              | `Context`    | name of the context param               |
| `stebz.qase.commentAttachment`               | `Boolean`             | `true`       | add the comment as an attachment        |
| `stebz.qase.commentAttachmentName`           | `String`              | `Comment`    | name of the comment attachment          |

`stebz-reportportal`:

| property                                          | type                  | default             | description                                |
| ------------------------------------------------- | --------------------- | ------------------- | ------------------------------------------ |
| `stebz.reportportal.enabled`                      | `Boolean`             | `true`              | enable the listener                        |
| `stebz.reportportal.order`                        | `Integer`             | `10000`             | listener order                             |
| `stebz.reportportal.onlyKeywordSteps`             | `Boolean`             | `false`             | report only steps with a keyword           |
| `stebz.reportportal.keywordPosition`              | `AT_START` / `AT_END` | `AT_START`          | keyword position relative to the name      |
| `stebz.reportportal.keywordToUppercase`           | `Boolean`             | `false`             | uppercase the keyword                      |
| `stebz.reportportal.processName`                  | `Boolean`             | `true`              | fill `{param}` placeholders in names       |
| `stebz.reportportal.contextParam`                 | `Boolean`             | `false`             | add the step context as a param            |
| `stebz.reportportal.contextParamName`             | `String`              | `Context`           | name of the context param                  |
| `stebz.reportportal.contextDesc`                  | `Boolean`             | `false`             | add the step context to the description    |
| `stebz.reportportal.contextDescName`              | `String`              | `Context`           | label of the context description part      |
| `stebz.reportportal.expectedResultDesc`           | `Boolean`             | `true`              | add the expected result to the description |
| `stebz.reportportal.expectedResultDescName`       | `String`              | `Expected result`   | label of the expected result part          |
| `stebz.reportportal.commentDesc`                  | `Boolean`             | `true`              | add the comment to the description         |
| `stebz.reportportal.commentDescName`              | `String`              | `Comment`           | label of the comment part                  |

`stebz-testit`:

| property                                      | type                  | default             | description                                |
| --------------------------------------------- | --------------------- | ------------------- | ------------------------------------------ |
| `stebz.testit.enabled`                        | `Boolean`             | `true`              | enable the listener                        |
| `stebz.testit.order`                          | `Integer`             | `10000`             | listener order                             |
| `stebz.testit.onlyKeywordSteps`               | `Boolean`             | `false`             | report only steps with a keyword           |
| `stebz.testit.keywordPosition`                | `AT_START` / `AT_END` | `AT_START`          | keyword position relative to the name      |
| `stebz.testit.keywordToUppercase`             | `Boolean`             | `false`             | uppercase the keyword                      |
| `stebz.testit.processName`                    | `Boolean`             | `true`              | fill `{param}` placeholders in names       |
| `stebz.testit.contextParam`                   | `Boolean`             | `false`             | add the step context as a param            |
| `stebz.testit.contextParamName`               | `String`              | `Context`           | name of the context param                  |
| `stebz.testit.contextDesc`                    | `Boolean`             | `false`             | add the step context to the description    |
| `stebz.testit.contextDescName`                | `String`              | `Context`           | label of the context description part      |
| `stebz.testit.expectedResultDesc`             | `Boolean`             | `true`              | add the expected result to the description |
| `stebz.testit.expectedResultDescName`         | `String`              | `Expected result`   | label of the expected result part          |
| `stebz.testit.commentDesc`                    | `Boolean`             | `true`              | add the comment to the description         |
| `stebz.testit.commentDescName`                | `String`              | `Comment`           | label of the comment part                  |

`stebz-system-out`:

| property                                 | type                  | default      | description                             |
| ---------------------------------------- | --------------------- | ------------ | --------------------------------------- |
| `stebz.systemOut.enabled`                | `Boolean`             | `true`       | enable the listener                     |
| `stebz.systemOut.order`                  | `Integer`             | `10000`      | listener order                          |
| `stebz.systemOut.onlyKeywordSteps`       | `Boolean`             | `false`      | report only steps with a keyword        |
| `stebz.systemOut.keywordPosition`        | `AT_START` / `AT_END` | `AT_START`   | keyword position relative to the name   |
| `stebz.systemOut.keywordToUppercase`     | `Boolean`             | `false`      | uppercase the keyword                   |
| `stebz.systemOut.indent`                 | `Integer`             | `2`          | indentation per nesting level, spaces   |
| `stebz.systemOut.params`                 | `Boolean`             | `true`       | print step params                       |
| `stebz.systemOut.expectedResult`         | `Boolean`             | `true`       | print the expected result               |
| `stebz.systemOut.comment`                | `Boolean`             | `true`       | print the comment                       |

## Examples

A complete runnable project — Maven, JUnit 5 and Allure — lives in the
[examples repository](https://github.com/stebz/stebz-maven-junit5-allure-example).

## Contributing

Contributions are welcome! See [CONTRIBUTING.md](CONTRIBUTING.md) for guidelines.

Contributors:

* [@evpl](https://github.com/evpl) as Evgenii Plugatar

## Support

* [Telegram chat (EN)](https://t.me/stebz_en/)
* [Telegram chat (RU)](https://t.me/stebz_ru/)

## License

Stebz is an open-source project distributed under the [MIT license](LICENSE).
