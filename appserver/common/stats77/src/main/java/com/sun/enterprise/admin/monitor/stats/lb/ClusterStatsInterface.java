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

package com.sun.enterprise.admin.monitor.stats.lb;

/**
 * This interface has all of the bean info accessor methods.
 */
public interface ClusterStatsInterface {

    java.lang.String getId();


    void setInstanceStats(com.sun.enterprise.admin.monitor.stats.lb.InstanceStats[] value);


    com.sun.enterprise.admin.monitor.stats.lb.InstanceStats[] getInstanceStats();


    void setInstanceStats(int index, com.sun.enterprise.admin.monitor.stats.lb.InstanceStats value);


    com.sun.enterprise.admin.monitor.stats.lb.InstanceStats getInstanceStats(int index);


    int sizeInstanceStats();


    java.util.List fetchInstanceStatsList();


    int addInstanceStats(com.sun.enterprise.admin.monitor.stats.lb.InstanceStats value);


    int removeInstanceStats(com.sun.enterprise.admin.monitor.stats.lb.InstanceStats value);


    void setId(java.lang.String value);

}
