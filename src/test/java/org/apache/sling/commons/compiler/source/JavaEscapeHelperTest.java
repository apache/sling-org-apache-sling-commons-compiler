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

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class JavaEscapeHelperTest {

    @Test
    public void testGetClassNameFromScriptPath() {
        assertEquals("apps.projects.script__002e__html", JavaEscapeHelper.makeJavaPackage("/apps/projects/script.html"));
        assertEquals("apps.projects.script__002e__html", JavaEscapeHelper.makeJavaPackage("\\apps\\projects\\script.html"));
        assertEquals("apps.my__002d__project.script__002e__html", JavaEscapeHelper.makeJavaPackage("/apps/my-project/script.html"));
        assertEquals("apps.my__002d__project.script__002e__html", JavaEscapeHelper.makeJavaPackage("\\apps\\my-project\\script.html"));
        assertEquals("apps.my_project.script__002e__html", JavaEscapeHelper.makeJavaPackage("/apps/my_project/script.html"));
        assertEquals("apps.my_project.script__002e__html", JavaEscapeHelper.makeJavaPackage("\\apps\\my_project\\script.html"));
        assertEquals("apps.projects.__0031__.script__002e__html", JavaEscapeHelper.makeJavaPackage("/apps/projects/1/script.html"));
        assertEquals("apps.projects.__0031__.script__002e__html", JavaEscapeHelper.makeJavaPackage("\\apps\\projects\\1\\script.html"));
        assertEquals("apps.projects.__0073__witch.script__002e__html", JavaEscapeHelper.makeJavaPackage("/apps/projects/switch/script.html"));
        assertEquals("apps.projects.__0073__witch.script__002e__html", JavaEscapeHelper.makeJavaPackage("\\apps\\projects\\switch\\script.html"));
    }

    @Test
    public void testMakeJavaIdentifier() {
        assertEquals("a__0020__b", JavaEscapeHelper.getJavaIdentifier("a b"));
        assertEquals("__0074__rue", JavaEscapeHelper.getJavaIdentifier("true"));
        assertEquals("__0076__ar", JavaEscapeHelper.getJavaIdentifier("var"));
    }

    @Test
    public void testUnmangle() {
        assertEquals('.', JavaEscapeHelper.unescape(JavaEscapeHelper.escapeChar('.')));
        assertEquals('.', JavaEscapeHelper.unescape("__002e__"));
    }

    @Test
    public void testUnescapeAll() {
        assertEquals("apps.projects.switch.script.html", JavaEscapeHelper.unescapeAll("apps.projects.__0073__witch.script__002e__html"));
        assertEquals("apps.projects.switch.script.html", JavaEscapeHelper.unescapeAll("apps.projects.switch.script.html"));
    }
}
