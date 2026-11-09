# Changelog

## 2.0 (2026/11/09)

* #2 increase test coverage by @evpl in PR #270
* #154 add stebz-empty-steps module to all bundle modules by @evpl in PR #155
* #156 add method overload with step name generator parameter by @evpl in PR #157
* #158 remove duplicate packages in different maven modules by @evpl in PR #167
* #159 add step methods in Pascal Case style by @evpl in PR #162
* #165 disable catching of non-step exceptions in the `SoftAssertedStepsExtension` by @evpl in PR #166
* #166 rename Setup and Teardown keywords to Set Up and Tear Down by @evpl in PR #164
* #168 rename config properties according to package hierarchy by @evpl in PR #170
* #169 ignore case in config property names by @evpl in PR #171
* #172 add `StepObj` with method for default attribute value by @evpl in PR #174
* #173 rename `StepObj` body method to getBody by @evpl in PR #175
* #178 add input and output type for `StepAttribute` by @evpl in PR #180
* #179 add Asterisk keyword for aaa and gherkin keywords by @evpl in PR #181
* #182 add subtype aliases for step objects by @evpl in PR #183
* #182 add org.stebz.core.step.executable.alias missing package info by @evpl
* #182 fix return types for FunctionStep by @evpl in PR #197
* #184 add `StepObj` is method by @evpl in PR #186
* #185 rename `StepObj` withHidden method to withHiding, getHidden to isHidden by @evpl in PR #187
* #185 rename HIDDEN attribute to HIDING by @evpl in PR #188
* #189 add `StepObj` withIgnored method by @evpl in PR #190
* #189 add `StepObj` withIgnored method with multiple parameters by @evpl in PR #196
* #191 rename `StepObj` attributes method to getAttributes by @evpl in PR #192
* #194 add `StepObj` withRethrown method by @evpl in PR #195
* #200 add `StepObj` withMaskedParam and withMaskedParams methods by @evpl in PR #203
* #201 add Technical Gherkin keyword by @evpl in PR #202
* #204 add withContext and withoutResult methods by @evpl in PR #205
* #206 hide context parameter in report by default by @evpl in PR #207
* #210 allow RunnableStep and SupplierStep to save the context by @evpl in PR #211
* #212 refactor the `Param` and `WithParam` annotations by @evpl in PR #213
* #214 remove unused `StepCaptor` ref method by @evpl in PR #215
* #216 global refactoring by @evpl in PR #217 #218 #224 #225 #226 #227 #228 #229 #230 #231 #232 #233
* #220 add methods for creating a step attribute that uses a class name as a key by @evpl in PR #221
* #222 add a class as a key for `StepAttributeMarker` by @evpl in PR #223
* #271 update Allure major version to 3.1.1 by @evpl in PR #272
* bump AspectJ version to 1.9.27 in PR #209 #219
* bump Qase version to 4.1.66 in PR #269
* bump ReportPortal version to 5.4.15 in PR #264
* bump Test IT version to 4.2.4 in PR #193 #199 #208 #267
* bump Maven plugins versions in PR #238 #240 #247 #249 #250 #251 #254 #256 #259 #261
* bump GitHub Actions versions in PR #234 #235 #236 #237
* add CODEOWNERS by @evpl
* fix dependabot.yml by @evpl
* change line separator to LF by @evpl
* rewrite README.md by @evpl

## 1.13 (2026/02/24)

* #147 add steps and stepsResult methods by @evpl in PR #148
* #150 add emptySteps and emptyStepsResult methods by @evpl in PR #151
* #152 add each option for EmptyStepsExtension by @evpl in PR #153

## 1.12.1 (2026/02/02)

* #142 rename `softAssertedSteps` method with result to `softAssertedStepsResult` by @evpl in PR #143
* #144 add hiddenSteps method with value by @evpl in PR #145

## 1.12 (2026/02/01)

* #137 add step context tuples by @evpl in PR #141
* #138 add clearRelated `CleanStackTraceExtension` option by @evpl in PR #140
* #134 add `stebz-soft-asserted-steps` extension by @evpl in PR #136 #139
* #132 add `withEmptyBody`, `withEmptyBodyReturning` and `withNotImplementedBody` step methods by @evpl in PR #133

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
* bump AspectJ version to 1.9.25 in PR #113

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
* #47 add StepObj withParam method by @evpl
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
