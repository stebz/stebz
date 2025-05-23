/*
 * MIT License
 *
 * Copyright (c) 2025 Evgenii Plugatar
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
package org.stebz.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.FieldSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.stebz.annotation.Param;
import org.stebz.annotation.Step;
import org.stebz.annotation.StepAttributeAnnotation;
import org.stebz.annotation.WithComment;
import org.stebz.annotation.WithHidden;
import org.stebz.annotation.WithKeyword;
import org.stebz.annotation.WithName;
import org.stebz.annotation.WithParam;
import org.stebz.annotation.WithParams;
import org.stebz.attribute.Keyword;
import org.stebz.attribute.StepAttribute;
import org.stebz.attribute.StepAttributes;
import org.stebz.attribute.StepSourceType;
import org.stebz.executor.StepExecutor;
import org.stebz.step.StepObj;
import org.stebz.step.executable.ConsumerStep;
import org.stebz.step.executable.FunctionStep;
import org.stebz.step.executable.RunnableStep;
import org.stebz.step.executable.SupplierStep;
import org.stebz.util.function.ThrowingSupplier;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.stebz.attribute.ReflectiveStepAttributes.DECLARED_ANNOTATIONS;
import static org.stebz.attribute.ReflectiveStepAttributes.JOIN_POINT;
import static org.stebz.attribute.ReflectiveStepAttributes.REFLECTIVE_NAME;
import static org.stebz.attribute.ReflectiveStepAttributes.STEP_SOURCE;
import static org.stebz.attribute.ReflectiveStepAttributes.STEP_SOURCE_TYPE;
import static org.stebz.attribute.StepAttribute.COMMENT;
import static org.stebz.attribute.StepAttribute.HIDDEN;
import static org.stebz.attribute.StepAttribute.KEYWORD;
import static org.stebz.attribute.StepAttribute.NAME;
import static org.stebz.attribute.StepAttribute.PARAMS;

/**
 * Step aspects.
 */
@Aspect
public class StepAspects {
  private static final Map<String, StepAttribute<?>> CACHED_CUSTOM_ATTRS = new ConcurrentHashMap<>();
  private static final Map<String, Keyword> CACHED_KEYWORDS = new ConcurrentHashMap<>();
  private static final ThrowingSupplier.Caching<StepAttributesSetters> STEP_ATTRIBUTES_SETTERS =
    new ThrowingSupplier.Caching<>(() -> {
      final MethodHandles.Lookup lookup = MethodHandles.lookup();
      return new StepAttributesSetters(
        attributesFieldSetter(lookup, RunnableStep.Of.class),
        attributesFieldSetter(lookup, ConsumerStep.Of.class),
        attributesFieldSetter(lookup, SupplierStep.Of.class),
        attributesFieldSetter(lookup, FunctionStep.Of.class)
      );
    });

  private static MethodHandle attributesFieldSetter(final MethodHandles.Lookup lookup,
                                                    final Class<?> cls
  ) throws NoSuchFieldException, IllegalAccessException {
    final Field field = cls.getDeclaredField("attributes");
    field.setAccessible(true);
    return lookup.unreflectSetter(field);
  }

  /**
   * Executes step field getter, creates {@code StepObj} and adds step attributes to step.
   *
   * @param joinPoint the joint point
   * @return step with attributes
   * @throws Throwable never
   */
  @Around("get(@(@org.stebz.annotation.StepAttributeAnnotation *) org.stebz.step.StepObj+ *)")
  public Object fieldStepObjWithAttributes(final ProceedingJoinPoint joinPoint) throws Throwable {
    final StepObj<?> step = (StepObj<?>) joinPoint.proceed();
    if (step == null) {
      return null;
    }
    final FieldSignature signature = (FieldSignature) joinPoint.getSignature();
    final Field field = signature.getField();
    final StepAttributes attributes = extractAttributes(
      step.attributes(),
      joinPoint, StepSourceType.FIELD, field,
      field.getDeclaredAnnotations(),
      field.getName(),
      null, null, null
    );
    return step.withAttributes(attributes);
  }

  /**
   * Executes method, creates {@code StepObj} and adds step attributes to step.
   *
   * @param joinPoint the joint point
   * @return step with attributes
   * @throws Throwable if the method throws an exception
   */
  @Around("execution(@(@org.stebz.annotation.StepAttributeAnnotation *) org.stebz.step.StepObj+ *(..))")
  public Object methodStepObjWithAttribute(final ProceedingJoinPoint joinPoint) throws Throwable {
    final StepObj<?> step = (StepObj<?>) joinPoint.proceed();
    if (step == null) {
      return null;
    }
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();
    final StepAttributes attributes = extractAttributes(
      step.attributes(),
      joinPoint, StepSourceType.METHOD, method,
      method.getDeclaredAnnotations(),
      method.getName(),
      method.getParameters(), signature.getParameterNames(), joinPoint.getArgs()
    );
    return step.withAttributes(attributes);
  }

  /**
   * Executes constructor, creates {@code StepObj} and adds step attributes to step.
   *
   * @param joinPoint the joint point
   * @throws Throwable if the constructor throws an exception
   */
  @AfterReturning(
    "execution(@(@org.stebz.annotation.StepAttributeAnnotation *) org.stebz.step.executable..*.Of+.new(..))"
  )
  public void ctorStepObjWithAttributes(final JoinPoint joinPoint) throws Throwable {
    final StepObj<?> step = (StepObj<?>) joinPoint.getThis();
    final ConstructorSignature signature = (ConstructorSignature) joinPoint.getSignature();
    final Constructor<?> constructor = signature.getConstructor();
    final StepAttributes attributes = extractAttributes(
      step.attributes(),
      joinPoint, StepSourceType.CONSTRUCTOR, constructor,
      constructor.getDeclaredAnnotations(),
      constructor.getDeclaringClass().getSimpleName(),
      constructor.getParameters(), signature.getParameterNames(), joinPoint.getArgs()
    );
    STEP_ATTRIBUTES_SETTERS.get().setAttributes(step, attributes);
  }

  /**
   * Executes method as step.
   *
   * @param joinPoint the joint point
   * @return method result
   */
  @Around("execution(@(@org.stebz.annotation.StepAttributeAnnotation *) !org.stebz.step.StepObj+ *(..))")
  public Object quickMethodStep(final ProceedingJoinPoint joinPoint) {
    final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    final Method method = signature.getMethod();
    final StepAttributes attributes = extractAttributes(
      StepAttributes.empty(),
      joinPoint, StepSourceType.METHOD, method,
      method.getDeclaredAnnotations(),
      method.getName(),
      method.getParameters(), signature.getParameterNames(), joinPoint.getArgs()
    );
    if (signature.getReturnType() == void.class) {
      StepExecutor.get().execute(new RunnableStep.Of(attributes, joinPoint::proceed));
      return null;
    } else {
      return StepExecutor.get().execute(new SupplierStep.Of<>(attributes, joinPoint::proceed));
    }
  }

  /**
   * Executes constructor as step.
   *
   * @param joinPoint the joint point
   * @return constructor result
   */
  @Around("execution(@(@org.stebz.annotation.StepAttributeAnnotation *) !org.stebz.step.StepObj+.new(..))")
  public Object quickCtorStep(final ProceedingJoinPoint joinPoint) {
    final ConstructorSignature signature = (ConstructorSignature) joinPoint.getSignature();
    final Constructor<?> constructor = signature.getConstructor();
    final StepAttributes attributes = extractAttributes(
      StepAttributes.empty(),
      joinPoint, StepSourceType.CONSTRUCTOR, constructor,
      constructor.getDeclaredAnnotations(),
      constructor.getDeclaringClass().getSimpleName(),
      constructor.getParameters(), signature.getParameterNames(), joinPoint.getArgs()
    );
    return StepExecutor.get().execute(new SupplierStep.Of<>(attributes, () -> {
      joinPoint.proceed();
      return joinPoint.getThis();
    }));
  }

  @SuppressWarnings("unchecked")
  private static StepAttributes extractAttributes(final StepAttributes originAttributes,
                                                  final JoinPoint joinPoint,
                                                  final StepSourceType stepSourceType,
                                                  final Object stepSource,
                                                  final Annotation[] declaredAnnotations,
                                                  final String reflectiveName,
                                                  final Parameter[] parameters, /* nullable */
                                                  final String[] parameterNames, /* nullable */
                                                  final Object[] parameterValues /* nullable */) {
    final Map<String, Annotation> attrAnnotations = extractAttrAnnotations(declaredAnnotations);
    final Keyword keywordAttrValue = extractKeyword(attrAnnotations);
    final String nameAttrValue = extractName(attrAnnotations, reflectiveName);
    final String commentAttrValue = extractComment(attrAnnotations);
    final Boolean hiddenAttrValue = extractHidden(attrAnnotations);
    final Map<String, Object> paramsAttrValue = new LinkedHashMap<>();
    if (stepSourceType != StepSourceType.FIELD) {
      extractParams(attrAnnotations, paramsAttrValue, parameters, parameterNames, parameterValues);
    }
    extractCustomParams(attrAnnotations, paramsAttrValue);
    removeDefaultAnnotations(attrAnnotations);

    final StepAttributes.Builder builder = originAttributes.asBuilder();
    builder.add(JOIN_POINT, joinPoint);
    builder.add(STEP_SOURCE_TYPE, stepSourceType);
    builder.add(STEP_SOURCE, stepSource);
    builder.add(DECLARED_ANNOTATIONS, declaredAnnotations);
    builder.add(REFLECTIVE_NAME, reflectiveName);
    if (keywordAttrValue != null) {
      builder.add(KEYWORD, keywordAttrValue);
    }
    if (nameAttrValue != null && !nameAttrValue.isEmpty()) {
      builder.add(NAME, nameAttrValue);
    }
    if (commentAttrValue != null && !commentAttrValue.isEmpty()) {
      builder.add(COMMENT, commentAttrValue);
    }
    if (!paramsAttrValue.isEmpty()) {
      paramsAttrValue.putAll(originAttributes.get(PARAMS));
      builder.add(PARAMS, paramsAttrValue);
    }
    if (!attrAnnotations.isEmpty()) {
      attrAnnotations.forEach((key, value) -> builder.add(
        (StepAttribute<Object>) CACHED_CUSTOM_ATTRS.computeIfAbsent(key, StepAttribute::nullable),
        value
      ));
    }
    if (hiddenAttrValue == Boolean.TRUE) {
      builder.add(HIDDEN, true);
    }
    return builder.build();
  }

  private static Map<String, Annotation> extractAttrAnnotations(final Annotation[] annotations) {
    final HashMap<String, Annotation> map = new HashMap<>();
    for (final Annotation annotation : annotations) {
      final StepAttributeAnnotation attributeAnnotation =
        annotation.annotationType().getAnnotation(StepAttributeAnnotation.class);
      if (attributeAnnotation != null) {
        map.put(attributeAnnotation.value(), annotation);
      }
    }
    return map;
  }

  private static void removeDefaultAnnotations(final Map<String, Annotation> annotations) {
    annotations.remove(Step.KEY);
    annotations.remove(WithComment.KEY);
    annotations.remove(WithHidden.KEY);
    annotations.remove(WithKeyword.KEY);
    annotations.remove(WithName.KEY);
    annotations.remove(WithParam.KEY);
    annotations.remove(WithParam.List.KEY);
    annotations.remove(WithParams.KEY);
  }

  private static Keyword extractKeyword(final Map<String, Annotation> annotations) {
    final WithKeyword annotation = (WithKeyword) annotations.get(WithKeyword.KEY);
    if (annotation != null) {
      final String value = annotation.value();
      return value.isEmpty()
        ? Keyword.empty()
        : CACHED_KEYWORDS.computeIfAbsent(value, Keyword.Of::new);
    }
    return null;
  }

  private static String extractName(final Map<String, Annotation> annotations,
                                    final String reflectiveName) {
    final Step stepAnnotation = (Step) annotations.get(Step.KEY);
    if (stepAnnotation != null) {
      final String value = stepAnnotation.value();
      if (!value.isEmpty()) {
        return value;
      }
    }
    final WithName nameAnnotation = (WithName) annotations.get(WithName.KEY);
    if (nameAnnotation != null) {
      final String value = nameAnnotation.value();
      if (!value.isEmpty()) {
        return value;
      }
    }
    return reflectiveName;
  }

  private static String extractComment(final Map<String, Annotation> annotations) {
    final WithComment annotation = (WithComment) annotations.get(WithComment.KEY);
    return annotation == null
      ? null
      : annotation.value();
  }

  private static Boolean extractHidden(final Map<String, Annotation> annotations) {
    final WithHidden annotation = (WithHidden) annotations.get(WithHidden.KEY);
    return annotation == null
      ? null
      : annotation.value();
  }

  private static Map<String, Object> extractCustomParams(final Map<String, Annotation> annotations,
                                                         final Map<String, Object> params) {
    final WithParam paramAnnotation = (WithParam) annotations.get(WithParam.KEY);
    if (paramAnnotation != null) {
      params.put(paramAnnotation.name(), paramAnnotation.value());
    }
    final WithParam.List listAnnotation = (WithParam.List) annotations.get(WithParam.List.KEY);
    if (listAnnotation != null) {
      for (final WithParam param : listAnnotation.value()) {
        params.put(param.name(), param.value());
      }
    }
    return params;
  }

  private static Map<String, Object> extractParams(final Map<String, Annotation> annotations,
                                                   final Map<String, Object> params,
                                                   final Parameter[] parameters,
                                                   final String[] names,
                                                   final Object[] values) {
    if (values.length == 0 || (annotations.get(WithParams.KEY) == null && annotations.get(Step.KEY) == null)) {
      return params;
    }
    for (int idx = 0; idx < parameters.length; idx++) {
      final Parameter parameter = parameters[idx];
      final Param annot = parameter.getAnnotation(Param.class);
      String name;
      Object value;
      if (annot == null) {
        name = names[idx];
        value = values[idx];
      } else {
        if (annot.hide()) {
          continue;
        }
        name = annot.name();
        if (name.isEmpty()) {
          name = names[idx];
        }
        final String annotValue = annot.value();
        value = annotValue.isEmpty()
          ? values[idx]
          : annotValue;
      }
      params.put(name, value);
    }
    return params;
  }

  private static final class StepAttributesSetters {
    private final MethodHandle runnableStepSetter;
    private final MethodHandle consumerStepSetter;
    private final MethodHandle supplierStepSetter;
    private final MethodHandle functionStepSetter;

    private StepAttributesSetters(final MethodHandle runnableStepSetter,
                                  final MethodHandle consumerStepSetter,
                                  final MethodHandle supplierStepSetter,
                                  final MethodHandle functionStepSetter) {
      this.runnableStepSetter = runnableStepSetter;
      this.consumerStepSetter = consumerStepSetter;
      this.supplierStepSetter = supplierStepSetter;
      this.functionStepSetter = functionStepSetter;
    }

    private void setAttributes(final StepObj<?> step,
                               final StepAttributes attributes) throws Throwable {
      if (step instanceof RunnableStep.Of) {
        this.runnableStepSetter.invoke(step, attributes);
      } else if (step instanceof ConsumerStep.Of) {
        this.consumerStepSetter.invoke(step, attributes);
      } else if (step instanceof SupplierStep.Of) {
        this.supplierStepSetter.invoke(step, attributes);
      } else if (step instanceof FunctionStep.Of) {
        this.functionStepSetter.invoke(step, attributes);
      }
    }
  }
}
