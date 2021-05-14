/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package test.ejb.nointerfacebeanview;

import jakarta.enterprise.inject.Alternative;


//marked as Alternative, otherwise there would
//an ambiguous definition for TestLocalInterface between this
//super class and the TestLocalEJB
public class TestSuperClass implements TestInterface{

    @Override
    public boolean m1DefinedInInterface() {
        System.out.println("no interface bean: m1");
        return true;
    }

    public boolean m2DefinedInSuperClass(){
        System.out.println("no interface bean: m2");
        return true;
    }

}
