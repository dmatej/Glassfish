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

package org.glassfish.contextpropagation.weblogic.workarea;

import java.io.IOException;
import java.util.Iterator;

import org.glassfish.contextpropagation.weblogic.workarea.spi.WorkContextMapInterceptor;


/**
 * Implementation of <code>WorkContextMap</code>. This instance holds
 * a thread-local reference to the actual <code>WorkContextMap</code>
 * implementation that does most of the work.
 *
 * @see org.glassfish.contextpropagation.weblogic.workarea.WorkContextMap
 */
/* package */ final class WorkContextMapImpl
                implements WorkContextMap, WorkContextMapInterceptor
{
  @SuppressWarnings("rawtypes")
  private final static ThreadLocal localContextMap = new ThreadLocal();
    /*= AuditableThreadLocalFactory.createThreadLocal(
      new ThreadLocalInitialValue() {
        protected Object childValue(Object parentValue) {
          if (parentValue == null) return null;
          return ((WorkContextMapInterceptor)parentValue).
            copyThreadContexts(PropagationMode.WORK);
        }
      }
    );*/

  /* package */ WorkContextMapImpl() {
  }

  // Implementation of weblogic.workarea.WorkContextMap
  public WorkContext put(String key, WorkContext workContext,
                  int propagationMode) throws PropertyReadOnlyException {
    try {
      return getMap().put(key, workContext, propagationMode);
    }
    finally {
      if (getMapMaybe().isEmpty()) {
        reset();
      }
    }
  }

  public WorkContext put(String key, WorkContext workContext)
    throws PropertyReadOnlyException {
    try {
      return getMap().put(key, workContext);
    }
    finally {
      if (getMapMaybe().isEmpty()) {
        reset();
      }
    }
  }

  public WorkContext get(String key) {
    WorkContextMap map = getMapMaybe();
    if (map == null) {
      return null;
    }
    return map.get(key);
  }

  public WorkContext remove(String key) throws NoWorkContextException,
                                        PropertyReadOnlyException {
    WorkContextMap map = getMapMaybe();
    if (map == null) {
      throw new NoWorkContextException();
    }
    WorkContext prev = map.remove(key);
    // If that was the last element then remove the context from the
    // thread.
    if (map.isEmpty()) {
      reset();
    }

    return prev;
  }

  public int getPropagationMode(String key) {
    if(isEmpty())
      return PropagationMode.LOCAL;
    return getMapMaybe().getPropagationMode(key);
  }

  public boolean isPropagationModePresent(int propMode) {
    return getMapMaybe().isPropagationModePresent(propMode);
  }

  public boolean isEmpty() {
    return (getMapMaybe() == null);
  }

  @SuppressWarnings("unchecked")
  private void reset() {
    localContextMap.set(null);
  }

  @SuppressWarnings("rawtypes")
  public Iterator iterator() {
    WorkContextMap map = getMapMaybe();
    // REVIEW vmehra@bea.com 2004-Apr-23 -- instead of returning null
    // shouldn't we return empty iterator?
    return map == null ? null : map.iterator();
  }

  @SuppressWarnings("rawtypes")
  public Iterator keys() {
    WorkContextMap map = getMapMaybe();
    // REVIEW vmehra@bea.com 2004-Apr-23 -- instead of returning null
    // shouldn't we return empty iterator?
    return map == null ? null : map.keys();
  }

  public int version() {
    WorkContextMapInterceptor map
      = (WorkContextMapInterceptor)localContextMap.get();
    return (map != null ? map.version() : 0);
  }

  private final WorkContextMap getMapMaybe() {
    return (WorkContextMap)localContextMap.get();
  }

  @SuppressWarnings("unchecked")
  private final WorkContextMap getMap() {
    WorkContextMap map = (WorkContextMap)localContextMap.get();
    if (map == null) {
      map = new WorkContextLocalMap();
      localContextMap.set(map);
    }
    return map;
  }

  public WorkContextMapInterceptor getInterceptor() {
    return (WorkContextMapInterceptor)getMapMaybe();
  }

  @SuppressWarnings("unchecked")
  public void setInterceptor(WorkContextMapInterceptor interceptor) {
    localContextMap.set(interceptor);
  }

  // Implementation of weblogic.workarea.spi.WorkContextMapInterceptor

  public void sendRequest(WorkContextOutput out, int propagationMode) throws IOException {
    WorkContextMapInterceptor inter = getInterceptor();
    if (inter != null) {
      inter.sendRequest(out, propagationMode);
    }
  }

  public void sendResponse(WorkContextOutput out, int propagationMode) throws IOException {
    WorkContextMapInterceptor inter = getInterceptor();
    if (inter != null) {
      inter.sendResponse(out, PropagationMode.RMI);
    }
  }

  public void receiveRequest(WorkContextInput in)
    throws IOException
  {
    ((WorkContextMapInterceptor)getMap()).receiveRequest(in);
  }

  @SuppressWarnings("unchecked")
  public void receiveResponse(WorkContextInput in)
    throws IOException
  {
    // If we receive a null context back again but didn't have
    // anything then this is a no-op
    WorkContextMap map = getMapMaybe();
    if (in == null && map == null) {
      return;
    }
    else {
      // We need a map now if in is non-null
      if (map == null) {
        map = new WorkContextLocalMap();
        localContextMap.set(map);
      }
      // Merge all of the contexts into our thread-local map.
      ((WorkContextMapInterceptor)map).receiveResponse(in);
      // Importing actuall deleted everything so remove the thread-local
      if (map.isEmpty()) {
        reset();
      }
    }
  }

  public WorkContextMapInterceptor copyThreadContexts(int mode) {
    WorkContextMapInterceptor inter = getInterceptor();
    if (inter != null) {
      return inter.copyThreadContexts(mode);
    }
    return null;
  }

  public void restoreThreadContexts(WorkContextMapInterceptor contexts) {
    if (contexts != null) {
      ((WorkContextMapInterceptor)getMap()).restoreThreadContexts(contexts);
    }
  }

  public WorkContextMapInterceptor suspendThreadContexts() {
    WorkContextMapInterceptor map = getInterceptor();
    if (map != null) {
      reset();
    }
    return map;
  }

  @SuppressWarnings("unchecked")
  public void resumeThreadContexts(WorkContextMapInterceptor contexts) {
    localContextMap.set(contexts);
  }
}
