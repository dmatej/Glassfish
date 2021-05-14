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
import java.security.AccessControlException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.glassfish.contextpropagation.weblogic.workarea.spi.WorkContextAccessController;
import org.glassfish.contextpropagation.weblogic.workarea.spi.WorkContextEntry;
import org.glassfish.contextpropagation.weblogic.workarea.spi.WorkContextEntryImpl;
import org.glassfish.contextpropagation.weblogic.workarea.spi.WorkContextMapInterceptor;


//import weblogic.diagnostics.debug.DebugLogger;

/**
 * Thread-local implementation of <code>WorkContextMap</code>. This
 * class performs the guts of context management.
 *
 * @see org.glassfish.contextpropagation.weblogic.workarea.WorkContextMap
 */
/* package */ final class WorkContextLocalMap
                implements WorkContextMap, WorkContextMapInterceptor
{
  @SuppressWarnings("rawtypes")
  private final HashMap map = new HashMap();
  private int version = hashCode();
  private static final Logger debugWorkContext = Logger.getLogger("DebugWorkContext");
  // Implementation of weblogic.workarea.WorkContextMap

  @SuppressWarnings("unchecked")
  public WorkContext put(String key, WorkContext workContext,
                  int propagationMode) throws PropertyReadOnlyException
  {

    if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "put(" + key + ", " + workContext + ")");
    }
    if (key == null || key.equals("")) {
      throw new NullPointerException("Cannot use null key");
    }
    if (workContext == null) {
      throw new NullPointerException("Cannot use null WorkContext");
    }

    WorkContextEntry wce = (WorkContextEntry)map.get(key);
    if (wce != null) {
      // Can't modify read-only properties
      if (!WorkContextAccessController.isAccessAllowed(key, WorkContextAccessController.UPDATE)) {
        throw new PropertyReadOnlyException(key);
      }
    } else if (!WorkContextAccessController.isAccessAllowed(key, WorkContextAccessController.CREATE)) {
      throw new AccessControlException("No CREATE permission for key: \"" + key + "\"");
    }
    // Replace whatever is there.
    map.put(key , new WorkContextEntryImpl(key, workContext, propagationMode));
    version++;

    return wce == null ? null : wce.getWorkContext();
  }

  public WorkContext put(String key, WorkContext workContext)
    throws PropertyReadOnlyException {
    return put(key, workContext, PropagationMode.DEFAULT);
  }

  public WorkContext get(String key) {
    if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "get(" + key + ")");
    }
    if (!WorkContextAccessController.isAccessAllowed(key, WorkContextAccessController.READ)) {
      throw new AccessControlException("No READ permission for key: \"" + key + "\"");
    }
    WorkContextEntry wce = getEntry(key);
    if (wce == WorkContextEntry.NULL_CONTEXT) {
      return null;
    }
    return wce.getWorkContext();
  }

  public WorkContext remove(String key) throws NoWorkContextException,
                                        PropertyReadOnlyException {
    if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "remove(" + key + ")");
    }
    WorkContextEntry wce = getEntry(key);
    if (wce == WorkContextEntry.NULL_CONTEXT) {
      throw new NoWorkContextException(key);
    }
    else if (!wce.isOriginator() &&
             !WorkContextAccessController.isAccessAllowed(key,
                                                          WorkContextAccessController.DELETE)) {
      throw new PropertyReadOnlyException("No DELETE permission for key: \"" + key + "\"");
    }
    else {
      map.remove(key);
    }
    version++;
    return wce.getWorkContext();
  }

  public int getPropagationMode(String key) {
    if (!WorkContextAccessController.isAccessAllowed(key, WorkContextAccessController.READ)) {
      throw new AccessControlException("No READ permission for key: \"" + key + "\"");
    }
    return getEntry(key).getPropagationMode();
  }

  @SuppressWarnings("unchecked")
  public boolean isPropagationModePresent(int propMode) {
    boolean status = false;
    for (Iterator<WorkContextEntry> i = map.values().iterator(); i.hasNext();) {
      WorkContextEntry wce = (WorkContextEntry)i.next();
      if ((wce != null) && ((wce.getPropagationMode() & propMode) != 0)) {
        status = true;
        break;
      }
    }
    return status;
  }

  public boolean isEmpty() {
    return map.isEmpty();
  }

  @SuppressWarnings("rawtypes")
  public Iterator iterator() {
    return new WorkContextIterator(this);
  }

  @SuppressWarnings("rawtypes")
  public Iterator keys() {
    return new WorkContextKeys(this);
  }

  private final WorkContextEntry getEntry(String key) {
    WorkContextEntry wce;
    if (map.isEmpty() || (wce = (WorkContextEntry)map.get(key)) == null) {
      return WorkContextEntry.NULL_CONTEXT;
    }
    return wce;
  }

  // Implementation of weblogic.workarea.spi.WorkContextMapInterceptor

  @SuppressWarnings("unchecked")
  public void sendRequest(WorkContextOutput out, int propagationMode)
    throws IOException
  {
    for (Iterator<WorkContextEntry> i = map.values().iterator(); i.hasNext(); ) {
      WorkContextEntry wce = (WorkContextEntry)i.next();
      if ((wce.getPropagationMode() & propagationMode) != 0) {
        if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "sendRequest(" + wce.toString() + ")");
        }
        wce.write(out);
      }
    }
    WorkContextEntry.NULL_CONTEXT.write(out);
  }

  @SuppressWarnings("rawtypes")
  public void sendResponse(WorkContextOutput out, int propagationMode)
    throws IOException
  {
    // Only propagate back NORMAL properties. FIXED and READ_ONLY
    // cannot be changed.
    for (Iterator i = map.values().iterator(); i.hasNext(); ) {
      WorkContextEntry wce = (WorkContextEntry)i.next();
      if ((wce.getPropagationMode() & PropagationMode.ONEWAY) == 0
          && (wce.getPropagationMode() & propagationMode) != 0) {
        if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "sendResponse(" + wce.toString() + ")");
        }
        wce.write(out);
      }
    }
    WorkContextEntry.NULL_CONTEXT.write(out);
  }

  @SuppressWarnings("unchecked")
  public void receiveRequest(WorkContextInput in)
    throws IOException
  {
    while (true) {
      try {
        WorkContextEntry wce = WorkContextEntryImpl.readEntry(in);
        if (wce == WorkContextEntry.NULL_CONTEXT) {
          break;
        }
        String key = wce.getName();
        map.put(key, wce);
        if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "receiveRequest(" + wce.toString() + ")");
        }
      }
      catch (ClassNotFoundException cnfe) {
        if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "receiveRequest : ",cnfe);
        }
        // Ignored and proceed with other entries
        // FIX ME andyp 19-Aug-08 -- need to log this failure.
      }
    }
  }

  @SuppressWarnings("unchecked")
  public void receiveResponse(WorkContextInput in)
    throws IOException
  {
    HashSet<String> propKeySet = new HashSet<String>();
    // First purge the local map of all NORMAL properties since these
    // should come back in the response.
    synchronized(map){
          for (Iterator<WorkContextEntry> i = map.values().iterator(); i.hasNext(); ) {
            WorkContextEntry wce = (WorkContextEntry)i.next();
            int propMode = wce.getPropagationMode();
            // If PropagationMode is "not" ONEWAY and LOCAL, remove the entry
            if (((propMode & PropagationMode.ONEWAY) == 0) && ((propMode  & PropagationMode.LOCAL) == 0)) {
              propKeySet.add(wce.getName());
              i.remove();
              version++;
        }
      }
    }
    // Nothing is as nothing does.
    if (in == null) {
      return;
    }
    // Now re-populate with the elements that came back. We assume that
    // sendResponse() will not propagate FIXED or READ_ONLY
    // properties.
    while (true) {
      try {
        WorkContextEntry wce = WorkContextEntryImpl.readEntry(in);
        version++;
        if (wce == WorkContextEntry.NULL_CONTEXT) {
          break;
        }
        if(propKeySet.contains(wce.getName())) {
          //this allows the caller to remain the originator of the properties.
          map.put(wce.getName(), new WorkContextEntryImpl(wce.getName(),
              wce.getWorkContext(),wce.getPropagationMode()));
        } else {
          map.put(wce.getName(), wce);
        }
      }
      catch (ClassNotFoundException cnfe) {
        if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "receiveResponse : ", cnfe);
        }
        // Ignored and proceed with other entries
        // FIX ME andyp 19-Aug-08 -- need to log this failure.
      }
    }
  }

  @SuppressWarnings("unchecked")
  public WorkContextMapInterceptor copyThreadContexts(int mode) {
    if (map.isEmpty()) {
      return null;
    }
    WorkContextLocalMap copy = new WorkContextLocalMap();
    copy.map.putAll(map);
    copy.version = version;
    for (Iterator<WorkContextEntry> i = copy.map.values().iterator(); i.hasNext(); ) {
      WorkContextEntry e = (WorkContextEntry)i.next();
      if ((e.getPropagationMode() & mode) == 0) {
        i.remove();
      }
      if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "copyThreadContexts(" + e.toString() + ")");
      }
    }
    if (copy.map.isEmpty()) {
      return null;
    }
    else {
      return copy;
    }
  }

  @SuppressWarnings("unchecked")
  public void restoreThreadContexts(WorkContextMapInterceptor contexts) {
    if (debugWorkContext.isLoggable(Level.FINEST)) {
          debugWorkContext.log(Level.FINEST, "restoreThreadContexts(" + (contexts == null ? null
                                             : ((WorkContextLocalMap)contexts).map) + ")");
      }
    if (contexts == this)
      throw new AssertionError("Cyclic attempt to restore thread contexts");
    if (contexts == null) {
      return;
    }
    map.clear();
    map.putAll(((WorkContextLocalMap)contexts).map);
    // REVIEW andyp 1-Dec-04 -- what should the version be?
    version++;
  }

  public WorkContextMapInterceptor suspendThreadContexts() {
    throw new UnsupportedOperationException("suspendThreadContexts()");
  }

  public void resumeThreadContexts(WorkContextMapInterceptor contexts) {
    throw new UnsupportedOperationException("resumeThreadContexts()");
  }

  public int version() {
    return version;
  }

  /**
   * Iterator class for WorkContextEntrys in the map.
   */
  @SuppressWarnings("rawtypes")
  private final class WorkContextIterator implements Iterator {
    final Iterator<WorkContextEntry> iter;

    @SuppressWarnings("unchecked")
    private WorkContextIterator(WorkContextLocalMap map) {
      iter = map.map.values().iterator();
    }

    public void remove() {
      version++;
      iter.remove();
    }

    public boolean hasNext() {
      return iter.hasNext();
    }

    public Object next() {
      WorkContextEntry wce = (WorkContextEntry)iter.next();
      while (wce != null &&
        !WorkContextAccessController.isAccessAllowed(wce.getName(),
          WorkContextAccessController.READ) && hasNext()) {
         wce = (WorkContextEntry)iter.next();
      }
      return wce == null ? null : wce.getWorkContext();
    }
  }

   /**
   * Iterator class for WorkContextEntry keys in the map.
   */
  private final class WorkContextKeys implements Iterator<Object> {
    final Iterator<WorkContextEntry> iter;

    @SuppressWarnings("unchecked")
    private WorkContextKeys(WorkContextLocalMap map) {
      iter = map.map.values().iterator();
    }

    public void remove() {
      version++;
      iter.remove();
    }

    public boolean hasNext() {
      return iter.hasNext();
    }

    public Object next() {
      WorkContextEntry wce = (WorkContextEntry)iter.next();
      while (wce != null &&
        !WorkContextAccessController.isAccessAllowed(wce.getName(),
          WorkContextAccessController.READ) && hasNext()) {
         wce = (WorkContextEntry)iter.next();
      }
      return wce == null ? null : wce.getName();
    }
  }
}
