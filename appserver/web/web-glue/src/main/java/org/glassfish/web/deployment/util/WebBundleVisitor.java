/*
 * Copyright (c) 1997, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.web.deployment.util;

import com.sun.enterprise.deployment.WebBundleDescriptor;
import com.sun.enterprise.deployment.util.ComponentVisitor;

/**
 * This interface defines the visitor APIs for the web application descriptor
 *
 * @author  Jerome Dochez
 * @version
 */
public interface WebBundleVisitor extends ComponentVisitor {

    /**
     * visit a web bundle descriptor
     *
     * @param the web bundle descriptor
     */
    public void accept(WebBundleDescriptor descriptor);
}
