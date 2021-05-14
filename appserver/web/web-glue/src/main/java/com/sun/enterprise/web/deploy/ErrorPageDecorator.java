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

package com.sun.enterprise.web.deploy;

import org.apache.catalina.deploy.ErrorPage;
import org.apache.catalina.util.RequestUtil;
import org.glassfish.web.deployment.descriptor.ErrorPageDescriptor;

/**
 * Decorator of class <code>org.apache.catalina.deploy.ErrorPage</code>
 *
 * @author Jean-Francois Arcand
 */

public class ErrorPageDecorator extends ErrorPage {

    private ErrorPageDescriptor decoree;

    private String location;

    public ErrorPageDecorator(ErrorPageDescriptor decoree){
        this.decoree = decoree;
        if (decoree.getErrorCode() > 0) {
            setErrorCode(decoree.getErrorCode());
        } else if (decoree.getExceptionType() != null &&
                !"".equals(decoree.getExceptionType())) {
            setExceptionType(decoree.getExceptionType());
        }

        setLocation(RequestUtil.urlDecode(decoree.getLocation()));
    }
}
