/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.deployment.common;

import java.util.*;

/**
 * A class that can answer the following queries WITHOUT loading any classes
 *
 * Given a class C
 * 1. Find out all sub classes of C
 *
 * 2. Find out all classes that implemetns OR extends C
 *
 * 3. Find out all classes that are annotated with C
 *
 *
 * Usage:
 *
 * ClassDependencyBuilder cdb = new GraphBuilder();
 *      cdb.loadClassData(c1);
 *      cdb.loadClassData(c2);
 *      cdb.loadClassData(c3);
 *      ...
 *      ...
 *
 *      cdb.computeResult(c); // c can be any fully qualified class name (internal format or java format)
 *
 * @author Mahesh Kannan
 */
public class ClassDependencyBuilder {

    private Map<String, NodeInfo> mappings = new HashMap<String, NodeInfo>();

    public ClassDependencyBuilder() {
    }

    public Set<String> computeResult(String name) {
        name = name.replace('.', '/');
        NodeInfo ni = mappings.get(name);
        if (ni != null) {
            if (ni.isAnnotation()) {
                return getAnnotatedClasses(ni);
            } else if (ni.isClass() || ni.isInterface()) {
                return getSubClasses(ni, ni.isInterface());
            } else {
                //System.out.println("????");
            }
        } else {
            //System.out.println("No info about: " + name);
        }

        return new HashSet<String>();
    }

    private Set<String> getAnnotatedClasses(NodeInfo ni) {
        Set<String> annotatedClasses = new HashSet<String>();
        if (ni != null) {
            Set<NodeInfo> nInfos = ni.getDirectImplementors();
            if ((nInfos != null) && (nInfos.size() > 0)) {
                for (NodeInfo n : nInfos) {
                    annotatedClasses.add(n.getClassName());
                }
            }
        }

        return annotatedClasses;
    }

    private Set<String> getSubClasses(NodeInfo node, boolean addImplementors) {
        Set<String> assignables = new HashSet<String>();
        if (node != null) {
            List<NodeInfo> list = new LinkedList<NodeInfo>();
            while (true) {
                Set<NodeInfo> set = node.getDirectSubClass();
                if ((set != null) && (set.size() > 0)) {
                    for (NodeInfo n : set) {
                        list.add(n);
                    }
                }

                if (addImplementors) {
                    Set<NodeInfo> intfs = node.getDirectImplementors();
                    if ((intfs != null) && (intfs.size() > 0)) {
                        for (NodeInfo n : intfs) {
                            list.add(n);
                        }
                    }
                }

                if (list.size() > 0) {
                    node = list.remove(0);
                    String name = node.getClassName();
                    if (!assignables.contains(name)) {
                        assignables.add(name);
                    }
                } else {
                    break;
                }
            }
        }
        return assignables;
    }

    public void loadClassData(byte[] classData)
            throws Exception {

        NodeInfo node = new NodeInfo(classData);
        String cname = node.getClassName();
        NodeInfo nodeInfo = mappings.get(cname);
        if ((nodeInfo == null) || (!nodeInfo.isParsed())) {
            if (nodeInfo == null) {
                mappings.put(cname, node);
                nodeInfo = node;
            } else {
                nodeInfo.load(classData);
            }
            populateMapping(nodeInfo);
        }
    }

    public int size() {
        return mappings.size();
    }

    private void populateMapping(NodeInfo nodeInfo) {
        String superName = nodeInfo.getSuperClassName();
        if (superName != null) {
            NodeInfo superNodeInfo = mappings.get(superName);
            if (superNodeInfo == null) {
                superNodeInfo = new NodeInfo(superName);
                if (nodeInfo.isClass()) {
                    superNodeInfo.markAsClassType();
                } else {
                    superNodeInfo.markAsInterfaceType();
                }
                mappings.put(superName, superNodeInfo);
            }
            superNodeInfo.addDirectSubClass(nodeInfo);
        }

        String[] interfaces = nodeInfo.getInterfaces();
        if ((interfaces != null) && (interfaces.length > 0)) {
            for (String interf : interfaces) {
                NodeInfo interfNodeInfo = mappings.get(interf);
                if (interfNodeInfo == null) {
                    interfNodeInfo = new NodeInfo(interf);
                    interfNodeInfo.markAsInterfaceType();
                    mappings.put(interf, interfNodeInfo);
                }
                interfNodeInfo.addDirectImplementor(nodeInfo);
            }
        }

        List<String> anns = nodeInfo.getClassLevelAnnotations();
        if ((anns != null) && (anns.size() > 0)) {
            for (String ann : anns) {
                NodeInfo annNode = mappings.get(ann);
                if (annNode == null) {
                    annNode = new NodeInfo(ann);
                    annNode.markAsAnnotaionType();
                    mappings.put(ann, annNode);
                }
                annNode.addDirectImplementor(nodeInfo);
            }
        }

    }

    void printInfo() {
        for (NodeInfo node : mappings.values()) {
            System.out.println(node.toString());
        }
    }


}
