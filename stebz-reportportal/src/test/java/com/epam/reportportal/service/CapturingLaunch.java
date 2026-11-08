/*
 * MIT License
 *
 * Copyright (c) 2025-2026 Evgenii Plugatar
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.epam.reportportal.service;

import com.epam.reportportal.listeners.ItemStatus;
import com.epam.reportportal.listeners.ListenerParameters;
import com.epam.reportportal.service.step.StepReporter;
import com.epam.reportportal.utils.formatting.templating.TemplateConfiguration;
import com.epam.ta.reportportal.ws.model.FinishExecutionRQ;
import com.epam.ta.reportportal.ws.model.FinishTestItemRQ;
import com.epam.ta.reportportal.ws.model.OperationCompletionRS;
import com.epam.ta.reportportal.ws.model.StartTestItemRQ;
import com.epam.ta.reportportal.ws.model.log.SaveLogRQ;
import io.reactivex.Maybe;

import java.io.File;
import java.lang.reflect.Proxy;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

/**
 * Test {@code Launch} implementation which captures reported nested steps. Not a part of the public API.
 */
public final class CapturingLaunch extends Launch {
  private final CapturingStepReporter capturingStepReporter;

  /**
   * Ctor. Sets this launch as the current launch for the current thread.
   */
  public CapturingLaunch() {
    super(
      (ReportPortalClient) Proxy.newProxyInstance(
        Launch.class.getClassLoader(),
        new Class<?>[]{ReportPortalClient.class},
        new DummyReportPortalClientHandler()
      ),
      new ListenerParameters(),
      new CapturingStepReporter()
    );
    this.capturingStepReporter = (CapturingStepReporter) this.getStepReporter();
  }

  /**
   * Returns the last started nested step request or null.
   *
   * @return the last started nested step request or null
   */
  public StartTestItemRQ lastStartedStep() {
    return this.capturingStepReporter.startedStep.get();
  }

  /**
   * Returns the finish event of the last finished nested step or null.
   *
   * @return the finish event of the last finished nested step or null
   */
  public Object lastFinishedStep() {
    return this.capturingStepReporter.finishedStep.get();
  }

  @Override
  public boolean useMicroseconds() {
    return true;
  }

  @Override
  public TemplateConfiguration getTemplateConfiguration() {
    return new TemplateConfiguration();
  }

  @Override
  public Maybe<String> start() {
    return Maybe.empty();
  }

  @Override
  public void finish(@SuppressWarnings("UnnecessaryFullyQualifiedName") final FinishExecutionRQ rq) {
    // no-op
  }

  @Override
  public Maybe<String> createVirtualItem() {
    return Maybe.empty();
  }

  @Override
  public Maybe<String> startTestItem(final StartTestItemRQ rq) {
    return Maybe.empty();
  }

  @Override
  public Maybe<String> startTestItem(final Maybe<String> parentId, final StartTestItemRQ rq) {
    return Maybe.empty();
  }

  @Override
  public Maybe<String> startTestItem(final Maybe<String> parentId,
                                     final Maybe<String> retryOf,
                                     final StartTestItemRQ rq) {
    return Maybe.empty();
  }

  @Override
  public Maybe<String> startVirtualTestItem(final Maybe<String> virtualItem, final StartTestItemRQ rq) {
    return Maybe.empty();
  }

  @Override
  public Maybe<String> startVirtualTestItem(final Maybe<String> parentId,
                                            final Maybe<String> virtualItem,
                                            final StartTestItemRQ rq) {
    return Maybe.empty();
  }

  @Override
  public void log(final SaveLogRQ rq) {
    // no-op
  }

  @Override
  public void log(final Function<String, SaveLogRQ> logSupplier) {
    // no-op
  }

  @Override
  public void log(final Maybe<String> logItemUuid, final Function<String, SaveLogRQ> logSupplier) {
    // no-op
  }

  @Override
  public Maybe<OperationCompletionRS> finishTestItem(final Maybe<String> itemId, final FinishTestItemRQ rq) {
    return Maybe.empty();
  }

  @Override
  public Maybe<String> getLaunch() {
    return Maybe.empty();
  }

  private static final class CapturingStepReporter implements StepReporter {
    final AtomicReference<StartTestItemRQ> startedStep = new AtomicReference<>();
    final AtomicReference<Object> finishedStep = new AtomicReference<>();

    @Override
    public void setParent(final Maybe<String> parentUuid) {
      // no-op
    }

    @Override
    public Maybe<String> getParent() {
      return Maybe.empty();
    }

    @Override
    public void removeParent(final Maybe<String> parentUuid) {
      // no-op
    }

    @Override
    public boolean isFailed(final Maybe<String> parentId) {
      return false;
    }

    @Override
    public Maybe<String> sendStep(final String name) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final String name, final String... logs) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final ItemStatus status, final String name) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final ItemStatus status, final String name, final String... logs) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final ItemStatus status, final String name, final Throwable throwable) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final String name, final File... files) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final ItemStatus status, final String name, final File... files) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> sendStep(final ItemStatus status,
                                  final String name,
                                  final Throwable throwable,
                                  final File... files) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> finishPreviousStep(final ItemStatus status) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> finishPreviousStep() {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> startNestedStep(final StartTestItemRQ startStepRequest) {
      this.startedStep.set(startStepRequest);
      return Maybe.empty();
    }

    @Override
    public Maybe<OperationCompletionRS> finishNestedStep(final ItemStatus status) {
      this.finishedStep.set(status);
      return Maybe.empty();
    }

    @Override
    public Maybe<OperationCompletionRS> finishNestedStep() {
      this.finishedStep.set("finished");
      return Maybe.empty();
    }

    @Override
    public Maybe<OperationCompletionRS> finishNestedStep(final Throwable throwable) {
      this.finishedStep.set(throwable);
      return Maybe.empty();
    }

    @Override
    public Maybe<OperationCompletionRS> finishNestedStep(final FinishTestItemRQ finishStepRequest) {
      this.finishedStep.set(finishStepRequest);
      return Maybe.empty();
    }

    @Override
    public Maybe<String> step(final String name) {
      return Maybe.empty();
    }

    @Override
    public Maybe<String> step(final ItemStatus status, final String name) {
      return Maybe.empty();
    }

    @Override
    public <T> T step(final ItemStatus stepSuccessStatus, final String name, final java.util.function.Supplier<T> actions) {
      return actions.get();
    }

    @Override
    public <T> T step(final String name, final java.util.function.Supplier<T> actions) {
      return actions.get();
    }
  }
}
