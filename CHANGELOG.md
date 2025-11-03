# Changelog

## 1.8 (03.11.2025)

* #92 bump Java version to 11 for stebz-reportportal module in PR #96
* #97 update `stebz-reportportal` listener for the new version in PR #98
* #101 add listener utility methods to update attributes during step execution in PR #102
* #103 add aliases for attributes in PR #104
* #105 add Rule Gherkin keyword in PR #106
* #107 rename StepObj `withNew...` methods to `with...` in PR #108
* #109 rename StepObj `withNewBody` method to `withBodyOf` in PR #110
* #111 add `ref` alias for the `captured` method in PR #112

## 1.7 (15.09.2025)

* #71 add stebz-hidden-steps module by @evpl in PR #77
* #73 add Arrange-Act-Assert and Gherkin keywords for precondition and postcondition by @evpl in PR #74
* #75 add keyword methods with body only by @evpl in PR #76
* #78 add common extensions to bundle modules by @evpl in PR #79
* #80 add noResult static methods to StebzMethods class by @evpl in PR #81
* #82 add onlyKeywordSteps option for all step listeners by @evpl in PR #83
* #84 add a colon at the end of some keywords by @evpl in PR #85

## 1.6 (04.09.2025)

* #65 add Arrange-Act-Assert keywords modules by @evpl in PR #66
* #67 add keywordToUppercase option to all listeners by @evpl in PR #68
* #69 disable contextParam option by default to all listeners by @evpl in PR #70

## 1.5 (31.08.2025)

* #53 add expected result output to SystemOutStepListener by @evpl in PR #54
* #57 add withOnSuccess and withOnFailure methods by @evpl in PR #58
* #60 add Test IT StepListener by @evpl in PR #61

## 1.4 (18.08.2025)

* #44 add stebz-gherkin-keywords module by @evpl
* #46 add GherkinAround object by @evpl
* #47 add StebObj withParam method by @evpl
* #48 add StebzGherkinMethods gherkinAround method by @evpl
* #49 add expected result attribute by @evpl in PR #50
* #51 add withFinally method by @evpl in PR #52

## 1.3.1 (04.08.2025)

* #37 added support for custom result in the captured method by @evpl in PR #38
* #39 fixed captured constructor step by @evpl in PR #40

## 1.3 (02.08.2025)

* #3 added ability to capture quick steps by @evpl in PR #36
* #32 fixed overriding step name with reflective name if step already has name by @evpl in PR #35

## 1.2 (22.07.2025)

* #4 added `stebz-repeat-and-retry` extension by @evpl in PR #23
* #16 added `withoutParam(String)` and `withoutParams(String[])` `StepObj` methods by @evpl in PR #17
* #18 restructured maven project, added `stebz-bom` and `stebz-aggregator` modules by @evpl in PR #19 PR #21

## 1.1 (19.07.2025)

* #10 made the `CleanStackTraceExtension` `aspectjLines` property true by default only if `stebz-annotations` module is
  used by @evpl in PR #12
* #11 added `withBefore` and `withAfter` step methods by @evpl in PR #13
* #14 moved `without`, `withBefore`, `withAfter`, `noResult` methods to interfaces by @evpl in PR #15

## 1.0.1 (13.07.2025)

* #6 made the `StebzMethods.around()` method public by @evpl in PR #7
* #5 added order property to `SystemOutStepListener` by @evpl in PR #8

## 1.0 (12.07.2025)

Release 1.0
