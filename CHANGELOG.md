# Changelog

## 1.3 (02.08.2025)

* #3 added ability to capture quick steps by @evpl in PR #36
* #32 fixed overriding step name with reflective name if step already has name by @evpl in PR #35
* Bump io.qase:qase-java-commons from 4.1.12 to 4.1.13 in /stebz-parent by @dependabot[bot] in PR #31

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

* Bump io.qase:qase-java-commons from 4.1.10 to 4.1.12 by @dependabot[bot] in PR #1
* #6 made the `StebzMethods.around()` method public by @evpl in PR #7
* #5 added order property to `SystemOutStepListener` by @evpl in PR #8

## 1.0 (12.07.2025)

Release 1.0
