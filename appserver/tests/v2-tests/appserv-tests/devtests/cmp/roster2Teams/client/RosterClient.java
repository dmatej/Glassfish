/*
 * Copyright (c) 2001, 2018 Oracle and/or its affiliates. All rights reserved.
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

package client;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import util.*;
import roster.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import roster.Roster;
import roster.RosterHome;
import util.LeagueDetails;
import util.PlayerDetails;
import util.TeamDetails;
import java.util.Iterator;
import java.util.Set;

import com.sun.ejte.ccl.reporter.SimpleReporterAdapter;

public class RosterClient {

    private static SimpleReporterAdapter stat =
        new SimpleReporterAdapter("appserv-tests");

   public static void main(String[] args) {
       try {
           System.out.println("START");
           stat.addDescription("rosterJava2DB");

           Context initial = new InitialContext();
           Object objref = initial.lookup("java:comp/env/ejb/SimpleRosterExt");

           RosterHome home =
               (RosterHome)PortableRemoteObject.narrow(objref,
                                            RosterHome.class);

           Roster myRoster = home.create();

           insertInfo(myRoster);
           getSomeInfo(myRoster);

           getMoreInfo(myRoster);

           stat.addStatus("ejbclient rosterJava2DB ", stat.PASS);
           stat.printSummary("rosterJava2DB");

           System.exit(0);

       } catch (Exception ex) {
           System.err.println("Caught an exception:");
           ex.printStackTrace();
           stat.addStatus("ejbclient rosterJava2DB ", stat.FAIL);
           stat.printSummary("rosterJava2DB");
       }
       stat.printSummary("rosterJava2DB");
   } // main


    private static void getSomeInfo(Roster myRoster) {

       try {

           ArrayList playerList;
           ArrayList teamList;
           ArrayList leagueList;

           playerList = myRoster.getPlayersOfTeam("T2");
           printDetailsList(playerList);

           teamList = myRoster.getTeamsOfLeague("L1");
           printDetailsList(teamList);

           playerList = myRoster.getPlayersByPosition("defender");
           printDetailsList(playerList);


           leagueList = myRoster.getLeaguesOfPlayer("P28");
           printDetailsList(leagueList);

       } catch (Exception ex) {
           System.err.println("Caught an exception:");
           ex.printStackTrace();
           stat.addStatus("ejbclient rosterJava2DB ", stat.FAIL);
       }

    } // getSomeInfo

    private static void getMoreInfo(Roster myRoster) {

       try {

           LeagueDetails leagueDetails;
           TeamDetails teamDetails;
           PlayerDetails playerDetails;
           ArrayList playerList;
           ArrayList teamList;
           ArrayList leagueList;
           ArrayList sportList;

           leagueDetails = myRoster.getLeague("L1");
           System.out.println(leagueDetails.toString());
           System.out.println();

           teamDetails = myRoster.getTeam("T3");
           System.out.println(teamDetails.toString());
           System.out.println();

           playerDetails = myRoster.getPlayer("P20");
           System.out.println(playerDetails.toString());
           System.out.println();

           playerList = myRoster.getPlayersOfTeam("T2");
           printDetailsList(playerList);

           teamList = myRoster.getTeamsOfLeague("L1");
           printDetailsList(teamList);

           playerList = myRoster.getPlayersByPosition("defender");
           playerList = myRoster.getAllPlayers();
           playerList = myRoster.getPlayersNotOnTeam();
           playerList = myRoster.getPlayersByPositionAndName("power forward",
               "Jack Patterson");
           playerList = myRoster.getPlayersByCity("Truckee");
           playerList = myRoster.getPlayersBySport("Soccer");
           playerList = myRoster.getPlayersByLeagueId("L1");

           playerList = myRoster.getPlayersByHigherSalary("Ian Carlyle");

           printDetailsList(playerList);

           playerList = myRoster.getPlayersBySalaryRange(500.00, 800.00);
           playerList = myRoster.getPlayersOfTeamCopy("T5");

           leagueList = myRoster.getLeaguesOfPlayer("P28");
           printDetailsList(leagueList);

           sportList = myRoster.getSportsOfPlayer("P28");
           printDetailsList(sportList);

           /****************************************************************
            *
            * new additions!!!!
            *
            ****************************************************************/
           leagueDetails = myRoster.getLeagueByName("Valley");
           System.out.println(leagueDetails.toString());
           System.out.println();

           leagueDetails = myRoster.getLeagueByName("Mountain");
           System.out.println(leagueDetails.toString());
           System.out.println();

           teamList = myRoster.getTeamsByPlayerAndLeague("P1", "L1");
           printDetailsList(teamList);

           Set cities = myRoster.getCitiesOfLeague("L2");
           Iterator it = cities.iterator();
           while (it.hasNext()) {
               System.out.println(it.next());
           }
           System.out.println();


           teamDetails = myRoster.getTeamOfLeagueByCity("L2", "Truckee");
           System.out.println(teamDetails.toString());
           System.out.println();

           System.out.println(myRoster.getTeamsNameOfLeagueByCity("L2", "Truckee"));
           System.out.println();

           System.out.println(myRoster.getSalaryOfPlayerFromTeam("T3", "Ben Shore"));
           System.out.println();

           playerList = myRoster.getPlayersOfLeague("L2");
           printDetailsList(playerList);

           playerList = myRoster.getPlayersWithPositionsGoalkeeperOrDefender();
           printDetailsList(playerList);

           playerList = myRoster.getPlayersWithNameEndingWithON();
           printDetailsList(playerList);

           playerList = myRoster.getPlayersWithNullName();
           printDetailsList(playerList);

           playerList = myRoster.getPlayersWithTeam("T5");
           printDetailsList(playerList);

           System.out.println(myRoster.getTeamNameVariations("T5"));
           System.out.println();


           playerList = myRoster.getPlayersWithSalaryUsingABS(100.1212121);
           printDetailsList(playerList);

           playerList = myRoster.getPlayersWithSalaryUsingSQRT(10000);
           printDetailsList(playerList);


           //remote calls
           teamList = myRoster.getTeamsByPlayerAndLeagueViaRemote("P1", "L1");
           printDetailsList(teamList);

           teamDetails = myRoster.getRemoteTeamOfLeagueByCity("L2", "Truckee");
           System.out.println(teamDetails.toString());
           System.out.println();



           teamList = myRoster.getRemoteTeamsOfLeague("L2");
           printDetailsList(teamList);

           playerList = myRoster.getRemotePlayersOfLeague("L2");
           printDetailsList(playerList);


           // internal NULL - parameter for finder
           playerList = myRoster.getPlayersByLeagueIdWithNULL("L1");
           printDetailsList(playerList);


       } catch (Exception ex) {
           System.err.println("Caught an exception:");
           ex.printStackTrace();
           stat.addStatus("ejbclient rosterJava2DB ", stat.FAIL);
       }

    } // getMoreInfo

    private static void printDetailsList(ArrayList list) {

        Iterator i = list.iterator();
        while (i.hasNext()) {
            Object details = (Object)i.next();
            System.out.println(details.toString());
        }
        System.out.println();
    } // printDetailsList


    private static void insertInfo(Roster myRoster) {

       try {
           // Leagues

           myRoster.createLeague(new LeagueDetails(
              "L1", "Mountain", "Soccer"));

           myRoster.createLeague(new LeagueDetails(
              "L2", "Valley", "Basketball"));

           // Teams

           myRoster.createTeamInLeague(new TeamDetails(
              "T1", "Honey Bees", "Visalia"), "L1");

           myRoster.createTeamInLeague(new TeamDetails(
              "T2", "Gophers", "Manteca"), "L1");

           myRoster.createTeamInLeague(new TeamDetails(
              "T3", "Deer", "Bodie"), "L2");

           myRoster.createTeamInLeague(new TeamDetails(
              "T4", "Trout", "Truckee"), "L2");

           myRoster.createTeamInLeague(new TeamDetails(
              "T5", "Crows", "Orland"), "L1");

           // Players, Team T1

           myRoster.createPlayer(new PlayerDetails(
              "P1", "Phil Jones", "goalkeeper", 100.00));
           myRoster.addPlayer("P1", "T1");

           myRoster.createPlayer(new PlayerDetails(
              "P2", "Alice Smith", "defender", 505.00));
           myRoster.addPlayer("P2", "T1");

           myRoster.createPlayer(new PlayerDetails(
              "P3", "Bob Roberts", "midfielder", 65.00));
           myRoster.addPlayer("P3", "T1");

           myRoster.createPlayer(new PlayerDetails(
              "P4", "Grace Phillips", "forward", 100.00));
           myRoster.addPlayer("P4", "T1");

           myRoster.createPlayer(new PlayerDetails(
              "P5", "Barney Bold", "defender", 100.00));
           myRoster.addPlayer("P5", "T1");

           // Players, Team T2

           myRoster.createPlayer(new PlayerDetails(
              "P6", "Ian Carlyle", "goalkeeper", 555.00));
           myRoster.addPlayer("P6", "T2");

           myRoster.createPlayer(new PlayerDetails(
              "P7", "Rebecca Struthers", "midfielder", 777.00));
           myRoster.addPlayer("P7", "T2");

           myRoster.createPlayer(new PlayerDetails(
              "P8", "Anne Anderson", "forward", 65.00));
           myRoster.addPlayer("P8", "T2");

           myRoster.createPlayer(new PlayerDetails(
              "P9", "Jan Wesley", "defender", 100.00));
           myRoster.addPlayer("P9", "T2");

           myRoster.createPlayer(new PlayerDetails(
              "P10", "Terry Smithson", "midfielder", 100.00));
           myRoster.addPlayer("P10", "T2");

           // Players, Team T3

           myRoster.createPlayer(new PlayerDetails(
              "P11", "Ben Shore", "point guard", 188.00));
           myRoster.addPlayer("P11", "T3");

           myRoster.createPlayer(new PlayerDetails(
              "P12", "Chris Farley", "shooting guard", 577.00));
           myRoster.addPlayer("P12", "T3");

           myRoster.createPlayer(new PlayerDetails(
              "P13", "Audrey Brown", "small forward", 995.00));
           myRoster.addPlayer("P13", "T3");

           myRoster.createPlayer(new PlayerDetails(
              "P14", "Jack Patterson", "power forward", 100.00));
           myRoster.addPlayer("P14", "T3");

           myRoster.createPlayer(new PlayerDetails(
              "P15", "Candace Lewis", "point guard", 100.00));
           myRoster.addPlayer("P15", "T3");

           // Players, Team T4

           myRoster.createPlayer(new PlayerDetails(
              "P16", "Linda Berringer", "point guard", 844.00));
           myRoster.addPlayer("P16", "T4");

           myRoster.createPlayer(new PlayerDetails(
              "P17", "Bertrand Morris", "shooting guard", 452.00));
           myRoster.addPlayer("P17", "T4");

           myRoster.createPlayer(new PlayerDetails(
              "P18", "Nancy White", "small forward", 833.00));
           myRoster.addPlayer("P18", "T4");

           myRoster.createPlayer(new PlayerDetails(
              "P19", "Billy Black", "power forward", 444.00));
           myRoster.addPlayer("P19", "T4");

           myRoster.createPlayer(new PlayerDetails(
              "P20", "Jodie James", "point guard", 100.00));
           myRoster.addPlayer("P20", "T4");

           // Players, Team T5

           myRoster.createPlayer(new PlayerDetails(
              "P21", "Henry Shute", "goalkeeper", 205.00));
           myRoster.addPlayer("P21", "T5");

           myRoster.createPlayer(new PlayerDetails(
              "P22", "Janice Walker", "defender", 857.00));
           myRoster.addPlayer("P22", "T5");

           myRoster.createPlayer(new PlayerDetails(
              "P23", "Wally Hendricks", "midfielder", 748.00));
           myRoster.addPlayer("P23", "T5");

           myRoster.createPlayer(new PlayerDetails(
              "P24", "Gloria Garber", "forward", 777.00));
           myRoster.addPlayer("P24", "T5");

           myRoster.createPlayer(new PlayerDetails(
              "P25", "Frank Fletcher", "defender", 399.00));
           myRoster.addPlayer("P25", "T5");

           // Players, no team

           myRoster.createPlayer(new PlayerDetails(
              "P26", "Hobie Jackson", "pitcher", 582.00));

           myRoster.createPlayer(new PlayerDetails(
              "P27", "Melinda Kendall", "catcher", 677.00));

           myRoster.createPlayer(new PlayerDetails(
              "P99", null, "_", 666.66));

           // Players, multiple teams

           myRoster.createPlayer(new PlayerDetails(
              "P28", "Constance Adams", "substitue", 966.00));
           myRoster.addPlayer("P28", "T1");
           myRoster.addPlayer("P28", "T3");

       } catch (Exception ex) {
           System.err.println("Caught an exception:");
           ex.printStackTrace();
           stat.addStatus("ejbclient rosterJava2DB ", stat.FAIL);
       }

    } // insertInfo


} // class
