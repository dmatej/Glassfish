/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.api.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.glassfish.api.Param;
import org.glassfish.api.admin.AdminCommand;
import org.glassfish.api.admin.AdminCommandContext;
import org.glassfish.api.admin.WrappedAdminCommand;
import org.junit.Test;

/**
 *
 * @author Andriy Zhdanov
 *
 */
public class WrappedAdminCommandTest {

    /*
     * Test getParamValue from wrapped command.
     */
    @Test
    public void getParamValueTest() {
        DummyAdminCommand dac = new DummyAdminCommand();
        dac.foo = "test";
        WrappedAdminCommand wrappedCommand = new WrappedAdminCommand(dac) {
            @Override
            public void execute(AdminCommandContext context) {
                // nothing todo
            }
        };
        assertEquals("set param value", "test", CommandSupport.getParamValue(wrappedCommand, "foo"));
        // note, after resolver it must be not null
        assertNull("unset param value must be null", CommandSupport.getParamValue(wrappedCommand, "foobar"));
        assertNull("non existent param value must be null", CommandSupport.getParamValue(wrappedCommand, "dummy"));
    }

    private class DummyAdminCommand implements AdminCommand {
        @Param(optional = false)
        String foo;

        @Param(name = "bar", defaultValue = "false", optional = true)
        String foobar;

        @Override
        public void execute(AdminCommandContext context) {
        }
    }
}
