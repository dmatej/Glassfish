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

package com.sun.enterprise.admin.monitor.stats;

import org.glassfish.j2ee.statistics.CountStatistic;
import org.glassfish.j2ee.statistics.Stats;
/**
 * Contains Statistical Information for the HttpService
 *
 * @author  nsegura
 */
public interface PWCHttpServiceStats extends Stats {

    /**
     * Returns HttpService Id
     * @return HttpService Id
     */
    StringStatistic getID();

    /**
     * Returns the HttpService Version
     * @return HttpService Version
     */
    StringStatistic getVersionServer();

    /**
     * Returns the Time the HttpService Was Started
     * @return Time HttpService Started
     */
    StringStatistic getTimeStarted();

    /** Returns the amount of seconds the HttpService has been running
     * @return seconds HttpService has been running
     */
    CountStatistic getSecondsRunning();

    /** Returns the max amount of threads
     * @return the max amount of threads
     */
    CountStatistic getMaxThreads();

    /**
     * The Max amount of virtual servers
     * @return Max amount of virtual servers
     */
    CountStatistic getMaxVirtualServers();

    /**
     * Returns 1 if profiling is enabled, otherwise 0
     * @return profile enabled?
     */
    CountStatistic getFlagProfilingEnabled();

    /**
     * Returns 1 if virtual server overflow is enable, otherwise 0
     * @return virtual sever overflow enabled?
     */
    CountStatistic getFlagVirtualServerOverflow();

    /**
     * Returns the average load in the last minute
     * @return average 1 minute load
     */
    CountStatistic getLoad1MinuteAverage();

    /**
     * Returns the average load for the last 5 minutes
     * @return average 5 minute load
     */
    CountStatistic getLoad5MinuteAverage();

    /**
     * Returns the average load for the last 15 minutes
     * @return average 15 minute load
     */
    CountStatistic getLoad15MinuteAverage();

    /**
     * Returns the rate of bytes transmitted
     * @return byte trasmission rate
     */
    CountStatistic getRateBytesTransmitted();

    /**
     * Returns the rate of bytes received
     * @return byte receive rate
     */
    CountStatistic getRateBytesReceived();

}
