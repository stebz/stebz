# Changelog

## 1.12.1 (2026/02/02)

* #142 rename `softAssertedSteps` method with result to `softAssertedStepsResult` by @evpl in PR #143
* #144 add hiddenSteps method with value by @evpl in PR #145

## 1.12 (2026/02/01)

* #137 add step context tuples by @evpl in PR #141
* #138 add clearRelated `CleanStackTraceExtension` option by @evpl in PR #140
* #134 add `stebz-soft-assertions` extension by @evpl in PR #136 #139
* #132 add `withEmptyBody`, `withEmptyBodyReturning` and `withNotImplementedBody` step methods by @evpl in PR #133

## 1.11 (2025/12/07)

* #127 add `stepOf` alias methods to create steps by @evpl in PR #128

## 1.11 (2025/12/07)

* #127 add `stepOf` alias methods to create steps by @evpl in PR #128

## 1.10 (2025/11/14)

* #120 add a parameter annotated with @Param even if there are no @WithParams or @Step annotations by @evpl in PR #121
* #122 ensure that keyword annotations extensions are performed before ReadableReflectiveNameExtension by @evpl in PR
  #123
* #124 replace org.stebz.util.function functional interfaces with dev.jlet.function by @evpl in PR #125

## 1.9 (2025/11/08)

* #114 step listeners attributes refactoring by @evpl in PR #115
* #116 `stebz-repeat-and-retry` module refactoring by @evpl in PR #117
* #118 add overloads of the `captured` method for 7 and 8 values by @evpl in PR #119
* bump Aspectj version to 1.9.25 in PR #113

## 1.8 (2025/11/03)

* #92 bump Java version to 11 for stebz-reportportal module by @evpl in PR #96
* #97 update `stebz-reportportal` listener for the new version by @evpl in PR #98
* #101 add listener utility methods to update attributes during step execution by @evpl in PR #102
* #103 add aliases for attributes by @evpl in PR #104
* #105 add Rule Gherkin keyword by @evpl in PR #106
* #107 rename StepObj `withNew...` methods to `with...` by @evpl in PR #108
* #109 rename StepObj `withNewBody` method to `withBodyOf` by @evpl in PR #110
* #111 add `ref` alias for the `captured` method by @evpl in PR #112

## 1.7 (2025/09/15)

* #71 add stebz-hidden-steps module by @evpl in PR #77
* #73 add Arrange-Act-Assert and Gherkin keywords for precondition and postcondition by @evpl in PR #74
* #75 add keyword methods with body only by @evpl in PR #76
* #78 add common extensions to bundle modules by @evpl in PR #79
* #80 add noResult static methods to StebzMethods class by @evpl in PR #81
* #82 add onlyKeywordSteps option for all step listeners by @evpl in PR #83
* #84 add a colon at the end of some keywords by @evpl in PR #85

## 1.6 (2025/09/04)

* #65 add Arrange-Act-Assert keywords modules by @evpl in PR #66
* #67 add keywordToUppercase option to all listeners by @evpl in PR #68
* #69 disable contextParam option by default to all listeners by @evpl in PR #70

## 1.5 (2025/08/31)

* #53 add expected result output to SystemOutStepListener by @evpl in PR #54
* #57 add withOnSuccess and withOnFailure methods by @evpl in PR #58
* #60 add Test IT StepListener by @evpl in PR #61

## 1.4 (2025/08/18)

* #44 add stebz-gherkin-keywords module by @evpl
* #46 add GherkinAround object by @evpl
* #47 add StebObj withParam method by @evpl
* #48 add StebzGherkinMethods gherkinAround method by @evpl
* #49 add expected result attribute by @evpl in PR #50
* #51 add withFinally method by @evpl in PR #52

## 1.3.1 (2025/08/04)

* #37 added support for custom result in the captured method by @evpl in PR #38
* #39 fixed captured constructor step by @evpl in PR #40

## 1.3 (2025/08/02)

* #3 added ability to capture quick steps by @evpl in PR #36
* #32 fixed overriding step name with reflective name if step already has name by @evpl in PR #35

## 1.2 (2025/07/22)

* #4 added `stebz-repeat-and-retry` extension by @evpl in PR #23
* #16 added `withoutParam(String)` and `withoutParams(String[])` `StepObj` methods by @evpl in PR #17
* #18 restructured maven project, added `stebz-bom` and `stebz-aggregator` modules by @evpl in PR #19 PR #21

## 1.1 (2025/07/19)

* #10 made the `CleanStackTraceExtension` `aspectjLines` property true by default only if `stebz-annotations` module is
  used by @evpl in PR #12
* #11 added `withBefore` and `withAfter` step methods by @evpl in PR #13
* #14 moved `without`, `withBefore`, `withAfter`, `noResult` methods to interfaces by @evpl in PR #15

## 1.0.1 (2025/07/13)

* #6 made the `StebzMethods.around()` method public by @evpl in PR #7
* #5 added order property to `SystemOutStepListener` by @evpl in PR #8

## 1.0 (2025/07/12)

Release 1.0
