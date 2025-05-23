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
package org.stebz.util.property;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Properties reader.
 */
public interface PropertiesReader {

  /**
   * Returns the String value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  String getString(String name);

  /**
   * Returns the String value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException if {@code name} arg is null
   */
  String getString(String name,
                   String defaultValue);

  /**
   * Returns a list of String values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of String values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<String> getStringList(String name,
                             String delimiter);

  /**
   * Returns the Character value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  Character getCharacter(String name);

  /**
   * Returns the Character value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  Character getCharacter(String name,
                         Character defaultValue);

  /**
   * Returns a list of Character values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of Character values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Character> getCharacterList(String name,
                                   String delimiter);

  /**
   * Returns the Boolean value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  Boolean getBoolean(String name);

  /**
   * Returns the Boolean value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  Boolean getBoolean(String name,
                     Boolean defaultValue);

  /**
   * Returns a list of Boolean values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of Boolean values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Boolean> getBooleanList(String name,
                               String delimiter);

  /**
   * Returns the Integer value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  Integer getInteger(String name);

  /**
   * Returns the Integer value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  Integer getInteger(String name,
                     Integer defaultValue);

  /**
   * Returns a list of Integer values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of Integer values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Integer> getIntegerList(String name,
                               String delimiter);

  /**
   * Returns the Long value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  Long getLong(String name);

  /**
   * Returns the Long value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  Long getLong(String name,
               Long defaultValue);

  /**
   * Returns a list of Long values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of Long values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Long> getLongList(String name,
                         String delimiter);

  /**
   * Returns the Double value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  Double getDouble(String name);

  /**
   * Returns the Double value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  Double getDouble(String name,
                   Double defaultValue);

  /**
   * Returns a list of Double values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of Double values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Double> getDoubleList(String name,
                             String delimiter);

  /**
   * Returns the Enum value of the property.
   *
   * @param name     the property name
   * @param enumType the enum type
   * @param <E>      the enum type
   * @return property value
   * @throws NullPointerException  if {@code name} arg or {@code enumType} arg is null
   * @throws IllegalStateException if property does not exist
   */

  <E extends Enum<E>> E getEnum(String name,
                                Class<E> enumType);

  /**
   * Returns the Enum value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @param enumType     the enum type
   * @param <E>          the enum type
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg or {@code enumType} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  <E extends Enum<E>> E getEnum(String name,
                                Class<E> enumType,
                                E defaultValue);

  /**
   * Returns a list of Enum values for the property.
   *
   * @param name      the property name
   * @param enumType  the enum type
   * @param delimiter the delimiter
   * @param <E>       the enum type
   * @return list of Enum values for the property
   * @throws NullPointerException if {@code name} arg or {@code enumType} arg or {@code delimiter} arg is null
   */
  <E extends Enum<E>> List<E> getEnumList(String name,
                                          Class<E> enumType,
                                          String delimiter);

  /**
   * Returns the Class value of the property.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist
   */
  Class<?> getClass(String name);

  /**
   * Returns the Class value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property type is not Character
   */
  Class<?> getClass(String name,
                    Class<?> defaultValue);

  /**
   * Returns a list of Class values for the property.
   *
   * @param name      the property name
   * @param delimiter the delimiter
   * @return list of Class values for the property
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Class<?>> getClassList(String name,
                              String delimiter);

  /**
   * Default PropertiesReader implementation.
   */
  class Of implements PropertiesReader {
    private final Properties properties;

    /**
     * Ctor.
     *
     * @param resourcePropertiesFiles resource properties files list
     * @throws NullPointerException if {@code resourcePropertiesFiles} arg is null
     */
    public Of(final String... resourcePropertiesFiles) {
      if (resourcePropertiesFiles == null) { throw new NullPointerException("resourcePropertiesFiles arg is null"); }
      final Properties props = new Properties();
      for (String file : resourcePropertiesFiles) {
        try (final InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(file)) {
          if (stream != null) {
            props.load(stream);
          }
        } catch (final Exception ignored) { }
        try (final InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream(file)) {
          if (stream != null) {
            props.load(stream);
          }
        } catch (final Exception ignored) { }
      }
      props.putAll(System.getProperties());
      this.properties = props;
    }

    /**
     * Ctor.
     *
     * @param properties the properties
     * @throws NullPointerException if {@code properties} arg is null
     */
    public Of(final Properties properties) {
      if (properties == null) { throw new NullPointerException("properties arg is null"); }
      this.properties = properties;
    }

    @Override
    public String getString(final String name) {
      final String value = this.getString(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public String getString(final String name,
                            final String defaultValue) {
      if (name == null) { throw new NullPointerException("name arg is null"); }
      final String value = this.properties.getProperty(name);
      if (value == null || value.isEmpty()) {
        return defaultValue;
      }
      final String trimmedValue = value.trim();
      if (trimmedValue.isEmpty()) {
        return defaultValue;
      }
      return trimmedValue;
    }

    @Override
    public List<String> getStringList(final String name,
                                      final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .collect(Collectors.toList());
    }

    private static Character convertToCharacter(final String name,
                                                final String fullValue,
                                                final String valueToConvert) {
      if (valueToConvert.length() != 1) {
        throw new IllegalStateException("Character property " + name + " has incorrect value: " + fullValue);
      }
      return valueToConvert.charAt(0);
    }

    @Override
    public Character getCharacter(final String name) {
      final Character value = this.getCharacter(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public Character getCharacter(final String name,
                                  final Character defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToCharacter(name, strValue, strValue);
    }

    @Override
    public List<Character> getCharacterList(final String name,
                                            final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(str -> convertToCharacter(name, strValue, str.trim()))
        .collect(Collectors.toList());
    }

    private static Boolean convertToBoolean(final String name,
                                            final String fullValue,
                                            final String valueToConvert) {
      if (valueToConvert.equalsIgnoreCase("true")) {
        return true;
      }
      if (valueToConvert.equalsIgnoreCase("false")) {
        return false;
      }
      throw new IllegalStateException("Boolean property " + name + " has incorrect value: " + fullValue);
    }

    @Override
    public Boolean getBoolean(final String name) {
      final Boolean value = this.getBoolean(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public Boolean getBoolean(final String name,
                              final Boolean defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToBoolean(name, strValue, strValue);
    }

    @Override
    public List<Boolean> getBooleanList(final String name,
                                        final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(str -> convertToBoolean(name, strValue, str.trim()))
        .collect(Collectors.toList());
    }

    private static Integer convertToInteger(final String name,
                                            final String fullValue,
                                            final String valueToConvert) {
      try {
        return Integer.valueOf(valueToConvert);
      } catch (final NumberFormatException ex) {
        throw new IllegalStateException("Integer property " + name + " has incorrect value: " + fullValue, ex);
      }
    }

    @Override
    public Integer getInteger(final String name) {
      final Integer value = this.getInteger(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public Integer getInteger(final String name,
                              final Integer defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToInteger(name, strValue, strValue);
    }

    @Override
    public List<Integer> getIntegerList(final String name,
                                        final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(str -> convertToInteger(name, strValue, str.trim()))
        .collect(Collectors.toList());
    }

    private static Long convertToLong(final String name,
                                      final String fullValue,
                                      final String valueToConvert) {
      try {
        return Long.valueOf(valueToConvert);
      } catch (final NumberFormatException ex) {
        throw new IllegalStateException("Long property " + name + " has incorrect value: " + fullValue, ex);
      }
    }

    @Override
    public Long getLong(final String name) {
      final Long value = this.getLong(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public Long getLong(final String name,
                        final Long defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToLong(name, strValue, strValue);
    }

    @Override
    public List<Long> getLongList(final String name,
                                  final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(str -> convertToLong(name, strValue, str.trim()))
        .collect(Collectors.toList());
    }

    private static Double convertToDouble(final String name,
                                          final String fullValue,
                                          final String valueToConvert) {
      try {
        return Double.valueOf(valueToConvert);
      } catch (final NumberFormatException ex) {
        throw new IllegalStateException("Double property " + name + " has incorrect value: " + fullValue, ex);
      }
    }

    @Override
    public Double getDouble(final String name) {
      final Double value = this.getDouble(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public Double getDouble(final String name,
                            final Double defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToDouble(name, strValue, strValue);
    }

    @Override
    public List<Double> getDoubleList(final String name,
                                      final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(str -> convertToDouble(name, strValue, str.trim()))
        .collect(Collectors.toList());
    }

    private static <E extends Enum<E>> E convertToEnum(final String name,
                                                       final Class<E> enumType,
                                                       final String fullValue,
                                                       final String valueToConvert) {
      for (final E enumValue : enumType.getEnumConstants()) {
        if (enumValue.name().equalsIgnoreCase(valueToConvert)) {
          return enumValue;
        }
      }
      throw new IllegalStateException("Enum property " + name + " of " + enumType.getSimpleName() +
        " type has incorrect value: " + fullValue);
    }

    @Override
    public <E extends Enum<E>> E getEnum(final String name,
                                         final Class<E> enumType) {
      final E value = this.getEnum(name, enumType, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public <E extends Enum<E>> E getEnum(final String name,
                                         final Class<E> enumType,
                                         final E defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToEnum(name, enumType, strValue, strValue);
    }

    @Override
    public <E extends Enum<E>> List<E> getEnumList(final String name,
                                                   final Class<E> enumType,
                                                   final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .map(str -> convertToEnum(name, enumType, strValue, str.trim()))
        .collect(Collectors.toList());
    }

    private static Class<?> convertToClass(final String name,
                                           final String fullValue,
                                           final String valueToConvert) {
      try {
        return Class.forName(valueToConvert);
      } catch (final ClassNotFoundException ex) {
        throw new IllegalStateException("Class property " + name + " has incorrect value: " + fullValue, ex);
      }
    }

    @Override
    public Class<?> getClass(final String name) {
      final Class<?> value = this.getClass(name, null);
      if (value == null) {
        throw new IllegalStateException("Not found property: " + name);
      }
      return value;
    }

    @Override
    public Class<?> getClass(final String name,
                             final Class<?> defaultValue) {
      final String strValue = this.getString(name, null);
      if (strValue == null) {
        return defaultValue;
      }
      return convertToClass(name, strValue, strValue);
    }

    @Override
    public List<Class<?>> getClassList(final String name,
                                       final String delimiter) {
      final String strValue = this.getString(name, null);
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(str -> convertToClass(name, strValue, str.trim()))
        .collect(Collectors.toList());
    }
  }
}
