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
package org.stebz.util.property;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Properties reader.
 * <p>
 * Property names are case-insensitive. Property values are trimmed; an empty or blank property value is treated as a
 * missing property value.
 * <p>
 * If the property does not exist, single-value methods return the default value or throw {@code IllegalStateException},
 * list methods return an empty list. If the property value has an incorrect format for the requested type,
 * {@code IllegalStateException} is thrown.
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
   * Returns a list of String values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of String values for the property; an empty list if the property does not exist
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<String> getStringList(String name,
                             String delimiter);

  /**
   * Returns the Character value of the property. The property value must consist of a single character.
   *
   * @param name the property name
   * @return property value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  Character getCharacter(String name);

  /**
   * Returns the Character value of the property, or the default value if the property does not exist. The property
   * value must consist of a single character.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  Character getCharacter(String name,
                         Character defaultValue);

  /**
   * Returns a list of Character values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of Character values for the property; an empty list if the property does not exist
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
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  Boolean getBoolean(String name);

  /**
   * Returns the Boolean value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  Boolean getBoolean(String name,
                     Boolean defaultValue);

  /**
   * Returns a list of Boolean values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of Boolean values for the property; an empty list if the property does not exist
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
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  Integer getInteger(String name);

  /**
   * Returns the Integer value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  Integer getInteger(String name,
                     Integer defaultValue);

  /**
   * Returns a list of Integer values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of Integer values for the property; an empty list if the property does not exist
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
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  Long getLong(String name);

  /**
   * Returns the Long value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  Long getLong(String name,
               Long defaultValue);

  /**
   * Returns a list of Long values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of Long values for the property; an empty list if the property does not exist
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
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  Double getDouble(String name);

  /**
   * Returns the Double value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  Double getDouble(String name,
                   Double defaultValue);

  /**
   * Returns a list of Double values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of Double values for the property; an empty list if the property does not exist
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Double> getDoubleList(String name,
                             String delimiter);

  /**
   * Returns the Enum value of the property. Enum value is matched by name ignoring case.
   *
   * @param name     the property name
   * @param enumType the enum type
   * @param <E>      the enum type
   * @return property value
   * @throws NullPointerException  if {@code name} arg or {@code enumType} arg is null
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  <E extends Enum<E>> E getEnum(String name,
                                Class<E> enumType);

  /**
   * Returns the Enum value of the property, or the default value if the property does not exist. Enum value is matched
   * by name ignoring case.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @param enumType     the enum type
   * @param <E>          the enum type
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg or {@code enumType} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  <E extends Enum<E>> E getEnum(String name,
                                Class<E> enumType,
                                E defaultValue);

  /**
   * Returns a list of Enum values for the property. List elements are trimmed; empty elements are skipped. Enum values
   * are matched by name ignoring case. The delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param enumType  the enum type
   * @param delimiter the delimiter (regular expression)
   * @param <E>       the enum type
   * @return list of Enum values for the property; an empty list if the property does not exist
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
   * @throws IllegalStateException if property does not exist or property value has incorrect format
   */
  Class<?> getClass(String name);

  /**
   * Returns the Class value of the property, or the default value if the property does not exist.
   *
   * @param name         the property name
   * @param defaultValue the default value
   * @return property value or default value
   * @throws NullPointerException  if {@code name} arg is null
   * @throws IllegalStateException if the property value has incorrect format
   */
  Class<?> getClass(String name,
                    Class<?> defaultValue);

  /**
   * Returns a list of Class values for the property. List elements are trimmed; empty elements are skipped. The
   * delimiter is treated as a regular expression.
   *
   * @param name      the property name
   * @param delimiter the delimiter (regular expression)
   * @return list of Class values for the property; an empty list if the property does not exist
   * @throws NullPointerException if {@code name} arg or {@code delimiter} arg is null
   */
  List<Class<?>> getClassList(String name,
                              String delimiter);

  /**
   * Default PropertiesReader implementation.
   */
  class Of implements PropertiesReader {
    private final Map<String, String> propertiesMap;

    /**
     * Ctor.
     * <p>
     * Each file is searched in the system classloader and then in the context classloader of the current thread;
     * properties loaded later override properties loaded earlier. Missing or unreadable files are silently ignored.
     * System properties are added last, so they override properties from the given files.
     *
     * @param resourcePropertiesFiles resource properties files list
     * @throws NullPointerException if {@code resourcePropertiesFiles} arg is null or contains {@code null} element
     */
    public Of(final String... resourcePropertiesFiles) {
      if (resourcePropertiesFiles == null) {
        throw new NullPointerException("resourcePropertiesFiles arg is null");
      }
      final Properties properties = new Properties();
      for (final String file : resourcePropertiesFiles) {
        if (file == null) { throw new NullPointerException("resourcePropertiesFiles arg contains null element"); }
        try (final InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(file)) {
          if (stream != null) {
            properties.load(stream);
          }
        } catch (final IOException ignored) { }
        final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (contextClassLoader != null) {
          try (final InputStream stream = contextClassLoader.getResourceAsStream(file)) {
            if (stream != null) {
              properties.load(stream);
            }
          } catch (final IOException ignored) { }
        }
      }
      properties.putAll(System.getProperties());
      this.propertiesMap = propertiesAsMap(properties);
    }

    /**
     * Ctor.
     *
     * @param properties the properties
     * @throws NullPointerException if {@code properties} arg is null
     */
    public Of(final Properties properties) {
      if (properties == null) {
        throw new NullPointerException("properties arg is null");
      }
      this.propertiesMap = propertiesAsMap(properties);
    }

    /**
     * Ctor.
     *
     * @param propertiesMap the properties map
     * @throws NullPointerException if {@code propertiesMap} arg is null
     */
    public Of(final Map<String, String> propertiesMap) {
      if (propertiesMap == null) {
        throw new NullPointerException("propertiesMap arg is null");
      }
      final Map<String, String> map = new HashMap<>();
      propertiesMap.forEach((key, value) -> map.put(key == null ? null : key.toLowerCase(Locale.ROOT), value));
      this.propertiesMap = map;
    }

    private static Map<String, String> propertiesAsMap(final Properties properties) {
      final Map<String, String> map = new HashMap<>();
      properties.stringPropertyNames()
        .forEach(name -> map.put(name.toLowerCase(Locale.ROOT), properties.getProperty(name)));
      return map;
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
      if (name == null) {
        throw new NullPointerException("name arg is null");
      }
      final String value = this.propertiesMap.get(name.toLowerCase(Locale.ROOT));
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToCharacter(name, strValue, str))
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToBoolean(name, strValue, str))
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToInteger(name, strValue, str))
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToLong(name, strValue, str))
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToDouble(name, strValue, str))
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
      if (enumType == null) { throw new NullPointerException("enumType arg is null"); }
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      if (enumType == null) { throw new NullPointerException("enumType arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToEnum(name, enumType, strValue, str))
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
      if (delimiter == null) { throw new NullPointerException("delimiter arg is null"); }
      final String strValue = this.getString(name, null);
      if (strValue == null || strValue.isEmpty()) {
        return Collections.emptyList();
      }
      return Arrays.stream(strValue.split(delimiter))
        .map(String::trim)
        .filter(str -> !str.isEmpty())
        .map(str -> convertToClass(name, strValue, str))
        .collect(Collectors.toList());
    }
  }
}
