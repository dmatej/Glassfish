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

package org.glassfish.contextpropagation.weblogic.workarea.spi;

import org.glassfish.contextpropagation.weblogic.workarea.WorkContextMap;

/**
 * SPI helper for checking access control on {@link org.glassfish.contextpropagation.weblogic.workarea.WorkContextMap} keys.
 * On the client this class does nothing, on the server derived implementations will check
 * against applicable security policies.
 * @exclude
 */
public abstract class WorkContextAccessController
{
  public static final int CREATE = 0;
  public static final int READ = 1;
  public static final int UPDATE = 2;
  public static final int DELETE = 3;

  private static WorkContextAccessController SINGLETON;

  protected WorkContextAccessController() {
    if (SINGLETON != null) {
      throw new IllegalStateException("Cannot register two instances of WorkContextAccessController");
    }
    SINGLETON = this;
  }

  /**
   * Check whethe access of type <code>type</code> is allowed
   * on <code>key</code>
   *
   * @param key the key to check access for
   * @param type the type of access required
   * @return true if access is allowed, false otherwise
   */
  public static boolean isAccessAllowed(String key, int type) {
    return getAccessController().checkAccess(key, type);
  }

  public static WorkContextMap getPriviledgedWorkContextMap(WorkContextMap map) {
        return getAccessController().getPriviledgedWrapper(map);
  }

  /**
   * SPI provider implementation of {@link #isAccessAllowed}
   * @param key
   * @param type
   * @return
   */
  protected boolean checkAccess(String key, int type) {
    return true;
  }

  /**
   * SPI provider implementation of {@link #getPriviledgedWorkContextMap}
   * @param map
   * @return
   */
  protected WorkContextMap getPriviledgedWrapper(WorkContextMap map) {
    return map;
  }

  private static WorkContextAccessController getAccessController() {
    if (SINGLETON == null) return new WorkContextAccessController() {};
    return SINGLETON;
  }
}
