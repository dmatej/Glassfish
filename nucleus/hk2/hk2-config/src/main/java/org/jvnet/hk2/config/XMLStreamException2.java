/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.config;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.Location;

/**
 * To fix the problem in StAX API where exceptions are not properly chained.
 *
 * @author Kohsuke Kawaguchi
 */
public class XMLStreamException2 extends XMLStreamException {
    public XMLStreamException2(String string) {
        super(string);
    }

    public XMLStreamException2(Throwable throwable) {
        super(throwable);
        initCause(throwable);
    }

    public XMLStreamException2(String string, Throwable throwable) {
        super(string, throwable);
        initCause(throwable);
    }

    public XMLStreamException2(String string, Location location, Throwable throwable) {
        super(string, location, throwable);
        initCause(throwable);
    }

    public XMLStreamException2(String string, Location location) {
        super(string, location);
    }
}
