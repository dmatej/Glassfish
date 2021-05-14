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

package org.glassfish.common.util.timer;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Date;
import java.util.Locale;

/**
 *
 * @author mvatkina
 */
public class TimerScheduleTest {

    public TimerScheduleTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSundays() {
        Date fromDate = new Date(112, 9, 16, 10, 35);
        Date timeoutDate = new Date(112, 9, 21, 12, 0);
        Locale localeDefault = Locale.getDefault();
        Locale[] availableLocales = Locale.getAvailableLocales();
        for (Locale l : availableLocales) {
            Locale.setDefault(l);
            testSundays(fromDate, timeoutDate, false);
        }

        // Test couple of locales explicitly - see GLASSFISH-18804 and GLASSFISH-19154
        Locale l1 = new Locale("es", "PE");
        Locale.setDefault(l1);
        testSundays(fromDate, timeoutDate, true);

        l1 = new Locale("it", "IT");
        Locale.setDefault(l1);
        testSundays(fromDate, timeoutDate, true);

        Locale.setDefault(localeDefault);

    }

    @Test
    public void testDays1To5() {
        // 2013 Jul 7 - Sun
        Date fromDate = new Date(113, 6, 7, 10, 35);
        // 2013 Jul 8 - Mon
        Date timeoutDate = new Date(113, 6, 8, 20, 15);
        Locale localeDefault = Locale.getDefault();
        Locale[] availableLocales = Locale.getAvailableLocales();
        for (Locale l : availableLocales) {
            Locale.setDefault(l);
            testDays1To5(fromDate, timeoutDate, false);
        }

        // Test DE_de locale explicitly - see GLASSFISH-20673
        Locale l1 = new Locale("de", "DE");
        Locale.setDefault(l1);
        testDays1To5(fromDate, timeoutDate, true);

        Locale.setDefault(localeDefault);
    }

    private void testSundays(Date fromDate, Date timeoutDate, boolean log) {
        TimerSchedule ts = new TimerSchedule().dayOfWeek("7").
                hour("12").
                minute("0");


        Date newDate = ts.getNextTimeout(fromDate).getTime();
        if (log)
            System.out.println("Expected date: " + timeoutDate + " Got date: " + newDate + " in Locale: "+Locale.getDefault());

        if (!newDate.equals(timeoutDate)) {
            System.out.println("ERROR - Expected date: " + timeoutDate + " Got date: " + newDate + " in Locale: "+Locale.getDefault());
            assert(false);
        } else {
            assert(true);
        }
    }

    private void testDays1To5(Date fromDate, Date timeoutDate, boolean log) {
        TimerSchedule ts = new TimerSchedule().dayOfWeek("1-5").
                hour("20").
                minute("15");


        Date newDate = ts.getNextTimeout(fromDate).getTime();
        if (log)
            System.out.println("Expected date: " + timeoutDate + " Got date: " + newDate + " in Locale: "+Locale.getDefault());

        if (!newDate.equals(timeoutDate)) {
            System.out.println("ERROR - Expected date: " + timeoutDate + " Got date: " + newDate + " in Locale: "+Locale.getDefault());
            assert(false);
        } else {
            assert(true);
        }
    }

}

