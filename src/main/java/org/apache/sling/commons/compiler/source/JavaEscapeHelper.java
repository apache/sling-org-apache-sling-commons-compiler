/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Licensed to the Apache Software Foundation (ASF) under one
 ~ or more contributor license agreements.  See the NOTICE file
 ~ distributed with this work for additional information
 ~ regarding copyright ownership.  The ASF licenses this file
 ~ to you under the Apache License, Version 2.0 (the
 ~ "License"); you may not use this file except in compliance
 ~ with the License.  You may obtain a copy of the License at
 ~
 ~   http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing,
 ~ software distributed under the License is distributed on an
 ~ "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 ~ KIND, either express or implied.  See the License for the
 ~ specific language governing permissions and limitations
 ~ under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package org.apache.sling.commons.compiler.source;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jetbrains.annotations.NotNull;

public final class JavaEscapeHelper {

    private static final Set<String> javaKeywords = new HashSet<>();
    private static final Set<String> literals = new HashSet<>();
    private static final Set<String> specialIdentifiers = new HashSet<>();
    private static final Pattern ESCAPED_CHAR_PATTERN = Pattern.compile("(__[0-9a-f]{4}__)");

    private JavaEscapeHelper() {
    }

    static {
        javaKeywords.add("abstract");
        javaKeywords.add("assert");
        javaKeywords.add("boolean");
        javaKeywords.add("break");
        javaKeywords.add("byte");
        javaKeywords.add("case");
        javaKeywords.add("catch");
        javaKeywords.add("char");
        javaKeywords.add("class");
        javaKeywords.add("const");
        javaKeywords.add("continue");
        javaKeywords.add("default");
        javaKeywords.add("do");
        javaKeywords.add("double");
        javaKeywords.add("else");
        javaKeywords.add("enum");
        javaKeywords.add("extends");
        javaKeywords.add("final");
        javaKeywords.add("finally");
        javaKeywords.add("float");
        javaKeywords.add("for");
        javaKeywords.add("goto");
        javaKeywords.add("if");
        javaKeywords.add("implements");
        javaKeywords.add("import");
        javaKeywords.add("instanceof");
        javaKeywords.add("int");
        javaKeywords.add("interface");
        javaKeywords.add("long");
        javaKeywords.add("native");
        javaKeywords.add("new");
        javaKeywords.add("package");
        javaKeywords.add("private");
        javaKeywords.add("protected");
        javaKeywords.add("public");
        javaKeywords.add("return");
        javaKeywords.add("short");
        javaKeywords.add("static");
        javaKeywords.add("strictfp");
        javaKeywords.add("super");
        javaKeywords.add("switch");
        javaKeywords.add("synchronized");
        javaKeywords.add("this");
        javaKeywords.add("throw");
        javaKeywords.add("throws");
        javaKeywords.add("transient");
        javaKeywords.add("try");
        javaKeywords.add("void");
        javaKeywords.add("volatile");
        javaKeywords.add("while");
        javaKeywords.add("_");

        literals.add("true");
        literals.add("false");
        literals.add("null");

        specialIdentifiers.add("var");
    }

    /**
     * Converts the given identifier to a legal Java identifier.
     *
     * @param identifier the identifier to convert
     * @return legal Java identifier corresponding to the given identifier
     */
    public static @NotNull String getJavaIdentifier(@NotNull String identifier) {
        StringBuilder modifiedIdentifier = new StringBuilder();
        char[] identifierChars = new char[identifier.length()];
        identifier.getChars(0, identifier.length(), identifierChars, 0);
        for (int i = 0; i < identifierChars.length; i++) {
            char ch = identifierChars[i];
            if (i == 0 && !Character.isJavaIdentifierStart(ch)) {
                modifiedIdentifier.append(escapeChar(ch));
            } else {
                if (!Character.isJavaIdentifierPart(ch)) {
                    modifiedIdentifier.append(escapeChar(ch));
                } else {
                    modifiedIdentifier.append(ch);
                }
            }
        }
        String currentIdentifier = modifiedIdentifier.toString();
        if (isJavaKeyword(currentIdentifier) || isJavaLiteral(currentIdentifier) || isSpecialIdentifier(currentIdentifier)) {
            return escapeChar(currentIdentifier.charAt(0)) + currentIdentifier.substring(1);
        }
        return currentIdentifier;
    }

    /**
     * Escapes the provided character so that it's a valid Java identifier character.
     *
     * @param ch the character to escape
     * @return the escaped character representation
     */
    public static @NotNull String escapeChar(char ch) {
        return String.format("__%04x__", (int) ch);
    }

    /**
     * Provided an escaped string (obtained by calling {@link #escapeChar(char)}) it will will return the character that was
     * escaped.
     *
     * @param escaped the escaped string
     * @return the original character
     */
    public static char unescape(@NotNull String escaped) {
        String toProcess = escaped.replace("__", "");
        return (char) Integer.parseInt(toProcess, 16);
    }

    /**
     * Provided a string which could contain characters escaped through {@link #escapeChar(char)}, this method will unescape all escaped
     * characters.
     *
     * @param escaped a string containing escaped characters
     * @return a string with all escaped sequences produced by {@link #escapeChar(char)} replaced by the original character
     */
    public static @NotNull String unescapeAll(@NotNull String escaped) {
        String unescaped = escaped;
        Matcher matcher = ESCAPED_CHAR_PATTERN.matcher(unescaped);
        while (matcher.find()) {
            String group = matcher.group();
            char unescapedChar = unescape(group);
            unescaped = unescaped.replace(group, Character.toString(unescapedChar));
        }
        return unescaped;
    }

    /**
     * Converts the given {@code path} to a Java package or fully-qualified class name, depending on the {@code path}'s value.
     *
     * @param path the path to convert
     * @return Java package corresponding to the given path
     */
    public static @NotNull String makeJavaPackage(@NotNull String path) {
        String[] classNameComponents = path.split("/|\\\\");
        StringBuilder legalClassNames = new StringBuilder();
        for (int i = 0; i < classNameComponents.length; i++) {
            String classNameComponent = classNameComponents[i];
            if (classNameComponent.isEmpty()) {
                continue;
            }
            legalClassNames.append(getJavaIdentifier(classNameComponent));
            if (i < classNameComponents.length - 1) {
                legalClassNames.append('.');
            }
        }
        return legalClassNames.toString();
    }

    /**
     * Test whether the argument is a Java keyword.
     *
     * @param key the String to test
     * @return {@code true} if the String is a Java keyword, {@code false} otherwise
     */
    public static boolean isJavaKeyword(@NotNull String key) {
        return javaKeywords.contains(key);
    }

    /**
     * Test whether the argument is a Java literal (i.e. {@code true}, {@code false}, {@code null}).
     *
     * @param key the String to test
     * @return {@code true} if the String is a Java literal, {@code false} otherwise
     */
    public static boolean isJavaLiteral(@NotNull String key) {
        return literals.contains(key);
    }

    /**
     * Test whether the argument is a special identifier (i.e. {@code var}).
     *
     * @param key the String to test
     * @return {@code true} if the String is a Java special identifier, {@code false} otherwise
     */
    public static boolean isSpecialIdentifier(@NotNull String key) {
        return specialIdentifiers.contains(key);
    }


}
