/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
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

/*
 * Servlet.java
 *
 * Created on December 23, 2002, 3:20 PM
 */

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import javax.naming.*;
import javax.rmi.PortableRemoteObject;
/**
 *
 * @author  mvatkina
 * @version
 */
public class Servlet extends HttpServlet {

    /** Initializes the servlet.
     */
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }

    /** Destroys the servlet.
     */
    public void destroy() {

    }

    /** Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        response.setContentType("text/html");
        java.io.PrintWriter out = response.getWriter();

        out.println("<html>");
        out.println("<head>");
        out.println("<title>Servlet</title>");
        out.println("</head>");
        out.println("<body>");

        out.println("</body>");
        out.println("</html>");
        try {
            Context initial = new InitialContext();
            Object objref = initial.lookup("java:comp/env/ejb/RemoteA");
            cascadeDelete.AHome ahome =
            (cascadeDelete.AHome)PortableRemoteObject.narrow(objref,
            cascadeDelete.AHome.class);

            objref = initial.lookup("java:comp/env/ejb/RemoteB");
            cascadeDelete.BHome bhome =
            (cascadeDelete.BHome)PortableRemoteObject.narrow(objref,
            cascadeDelete.BHome.class);

            objref = initial.lookup("java:comp/env/ejb/RemoteC");
            cascadeDelete.CHome chome =
            (cascadeDelete.CHome)PortableRemoteObject.narrow(objref,
            cascadeDelete.CHome.class);

            objref = initial.lookup("java:comp/env/ejb/RemoteD");
            cascadeDelete.DHome dhome =
            (cascadeDelete.DHome)PortableRemoteObject.narrow(objref,
            cascadeDelete.DHome.class);


            cascadeDelete.A abean = ahome.create(new Integer(1), "A1");

            cascadeDelete.B bbean = bhome.create(new Integer(100), "B100");
            bbean = bhome.create(new Integer(200), "B200");

            cascadeDelete.C cbean = chome.create(new Integer(100), "C100");
            cbean = chome.create(new Integer(200), "C200");

            cascadeDelete.D dbean = dhome.create(new Integer(1000), "D1000");
            dbean = dhome.create(new Integer(1100), "D1100");
            dbean = dhome.create(new Integer(2000), "D2000");
            dbean = dhome.create(new Integer(2200), "D2200");

            abean.addAll();

            out.println("<pre>");

            out.println("Created " + ahome.findAll().size() + " As.");
            out.println("Created " + bhome.findAll().size() + " Bs.");
            out.println("Created " + chome.findAll().size() + " Cs.");
            out.println("Created " + dhome.findAll().size() + " Ds.");

            out.println("Removing last C...");
            cbean.remove();

            out.println("Left " + ahome.findAll().size() + " As.");
            out.println("Left " + bhome.findAll().size() + " Bs.");
            out.println("Left " + chome.findAll().size() + " Cs.");
            out.println("Left " + dhome.findAll().size() + " Ds.");


            out.println("Removing A...");
            abean.remove();

            out.println("Left " + ahome.findAll().size() + " As.");
            out.println("Left " + bhome.findAll().size() + " Bs.");
            out.println("Left " + chome.findAll().size() + " Cs.");
            out.println("Left " + dhome.findAll().size() + " Ds.");

            out.println("</pre>");

        } catch (Exception ex) {
            System.err.println("Caught an exception:");
            ex.printStackTrace();
        }

        out.close();
    }

    /** Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    /** Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, java.io.IOException {
        processRequest(request, response);
    }

    /** Returns a short description of the servlet.
     */
    public String getServletInfo() {
        return "Short description";
    }

}
