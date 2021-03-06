/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.s1asdev.jdbc.reconfig.maxpoolsize.client;

import javax.naming.*;
import java.rmi.*;
import java.util.*;

import com.sun.s1asdev.jdbc.reconfig.maxpoolsize.ejb.SimpleBMPHome;
import com.sun.s1asdev.jdbc.reconfig.maxpoolsize.ejb.SimpleBMP;
import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

import java.io.*;

public class Client {

    public static void main(String[] argv)
        throws Exception {
        SimpleReporterAdapter stat = new SimpleReporterAdapter();
        String testSuite = "ReconfigMaxPoolSize ";

        InitialContext ic = new InitialContext();
        Object objRef = ic.lookup("java:comp/env/ejb/SimpleBMPHome");
        SimpleBMPHome simpleBMPHome = (SimpleBMPHome)
        javax.rmi.PortableRemoteObject.narrow(objRef, SimpleBMPHome.class);

        SimpleBMP simpleBMP = simpleBMPHome.create();
        stat.addDescription("Reconfig MaxPoolSize tests");
        /*
         * Tests 1,2 use non-xa pool - so the 3rd param "useXA" is false
         * Tests 3,4 use xa pool - so the 3rd param "useXA" is true
         */
        if ("1".equals(argv[0]) ) {
            if (simpleBMP.test1(10, true, false ) ) {
                stat.addStatus(testSuite+" test1 : ", stat.PASS);
            } else {
                stat.addStatus(testSuite+" test1 : ", stat.FAIL);
            }
        } else if ("2".equals(argv[0])) {
            if (simpleBMP.test1(19, false, false) ) {
                stat.addStatus(testSuite+" test2 : ", stat.PASS);
            } else {
                stat.addStatus(testSuite+" test2 : ", stat.FAIL);
            }
        } else if ("3".equals(argv[0])) {
            if (simpleBMP.test1(10, false, true) ) {
                stat.addStatus(testSuite+" test3 : ", stat.PASS);
            } else {
                stat.addStatus(testSuite+" test3 : ", stat.FAIL);
            }
        } else if ("4".equals(argv[0])) {
            if (simpleBMP.test1(19, false, true) ) {
                stat.addStatus(testSuite+" test4 : ", stat.PASS);
            } else {
                stat.addStatus(testSuite+" test4 : ", stat.FAIL);
            }
        }


        System.out.println("jdbc reconfig-maxpoolsize status: ");
        stat.printSummary();
    }
}
