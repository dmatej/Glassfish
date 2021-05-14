#!/bin/sh
#
# Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
#
# This program and the accompanying materials are made available under the
# terms of the Eclipse Public License v. 2.0, which is available at
# http://www.eclipse.org/legal/epl-2.0.
#
# This Source Code may also be made available under the following Secondary
# Licenses when the conditions for such availability set forth in the
# Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
# version 2 with the GNU Classpath Exception, which is available at
# https://www.gnu.org/software/classpath/license.html.
#
# SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
#


#set v3 home
v3home=${S1AS_HOME}

#set databases home
databaseshome=/tmp/jdbc_devtests/databases

v3jdbcdevtestshome=$APS_HOME/devtests/jdbc/v3

#set war location
war2deploy=$v3jdbcdevtestshome/v3_jdbc_dev_tests/dist/v3_jdbc_dev_tests.war

#set Test Results Page
reconfigResult=/tmp/jdbc_devtests/reconfig-results.html

cd $v3home

if [ "$1" = "test1_and_2" ]; then

echo "*******************************************************************************************************************\n"
echo "\nExecuting Reconfiguration Tests \n\n"

echo "Creating Pools \n\n"
echo create pool jdbc-dev-test-pool
./bin/asadmin create-jdbc-connection-pool --datasourceclassname=org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource --restype=javax.sql.ConnectionPoolDataSource --property="Password=APP:User=APP:DatabaseName=$databaseshome/sun-appserv-samples:serverName=localhost:connectionAttributes=\;create\\=true" jdbc-dev-test-pool

echo create resource jdbc/jdbc-dev-test-resource
./bin/asadmin create-jdbc-resource --connectionpoolid=jdbc-dev-test-pool jdbc/jdbc-dev-test-resource

echo create pool jdbc-reconfig-test-pool-1
./bin/asadmin create-jdbc-connection-pool --datasourceclassname=org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource --restype=javax.sql.ConnectionPoolDataSource --property="Password=APP:User=APP:DatabaseName=$databaseshome/sample-db:serverName=localhost" jdbc-reconfig-test-pool-1

echo create resource jdbc/jdbc-reconfig-test-resource-1
./bin/asadmin create-jdbc-resource --connectionpoolid=jdbc-reconfig-test-pool-1 jdbc/jdbc-reconfig-test-resource-1

echo create pool jdbc-reconfig-test-pool-2
./bin/asadmin create-jdbc-connection-pool --datasourceclassname=org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource --restype=javax.sql.ConnectionPoolDataSource --property="Password=rpassword:User=ruser:DatabaseName=$databaseshome/reconfig-db:serverName=localhost" jdbc-reconfig-test-pool-2

echo create resource jdbc/jdbc-reconfig-test-resource-2
./bin/asadmin create-jdbc-resource --connectionpoolid=jdbc-reconfig-test-pool-2 jdbc/jdbc-reconfig-test-resource-2

echo create pool pool1
./bin/asadmin create-jdbc-connection-pool --datasourceclassname=org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource --restype=javax.sql.ConnectionPoolDataSource --property="Password=APP:User=APP:DatabaseName=$databaseshome/sample-db:serverName=localhost:connectionAttributes=\;create\\=true" pool1

echo create resource jdbc/res1
./bin/asadmin create-jdbc-resource --connectionpoolid=pool1 jdbc/res1

echo create pool pool2
./bin/asadmin create-jdbc-connection-pool --datasourceclassname=org.apache.derby.jdbc.EmbeddedConnectionPoolDataSource --restype=javax.sql.ConnectionPoolDataSource --property="Password=rpassword:User=ruser:DatabaseName=$databaseshome/reconfig-db:serverName=localhost" pool2

echo create resource jdbc/res2
./bin/asadmin create-jdbc-resource --connectionpoolid=pool2 jdbc/res2

echo "\n\n****************************************************************************************************************\n"

echo "\nExecuting TEST1 : JDBC Connection Pool Attribute (max-pool-size) Change \n"
echo "\nasadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-pool-size=40 \n"
./bin/asadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-pool-size=40

#also set max-wait-time-in-millis to a smaller value so that tests run faster
echo "\nasadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-wait-time-in-millis=1000 \n"
./bin/asadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-wait-time-in-millis=1000

echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?maxPoolSize=40\&throwException=true\&testId=1 \n"
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?maxPoolSize=40\&throwException=true\&testId=1 > $reconfigResult
echo "\n"

#asadmin set max-pool-size to 10 before running test for the second time
echo "\nasadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-pool-size=10 \n"
./bin/asadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-pool-size=10

echo "\nasadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-wait-time-in-millis=1000 \n"
./bin/asadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-wait-time-in-millis=1000

echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?maxPoolSize=10\&throwException=true\&testId=1 \n"
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?maxPoolSize=10\&throwException=true\&testId=1 >> $reconfigResult
echo "\n"

#asadmin set max-pool-size to 20 before running test for the second time
echo "\nasadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-pool-size=20 \n"
./bin/asadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.max-pool-size=20

echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?maxPoolSize=19\&throwException=false\&testId=1 \n"
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?maxPoolSize=19\&throwException=false\&testId=1 >> $reconfigResult
echo "\n"

echo "\nTEST1 executed successfully\n\n"
echo "\n******************************************************************************************************************\n"

echo "\nExecuting TEST2 : JDBC Connection Pool Property Change \n"
#asadmin set property User to a wrong value and try to get a connection
echo "\nasadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.property.User=APP2 \n"
./bin/asadmin set resources.jdbc-connection-pool.jdbc-dev-test-pool.property.User=APP2

echo "\nRedeploying war... \n"
./bin/asadmin undeploy v3_jdbc_dev_tests
./bin/asadmin deploy --force=true $war2deploy
echo "\n"

echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=true\&testId=2 \n"
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=true\&testId=2 >> $reconfigResult

echo "\nTEST2 executed successfully\n\n"
echo "\n******************************************************************************************************************\n"

echo "\nExecuting TEST3 : JDBC Resource reconfiguration\n"
echo "\nStatus : jdbc-reconfig-test-resource-2 is set with jdbc-reconfig-test-pool-2 (DB: reconfig-db with table reconfigTestTable\n"
echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=false\&testId=3 \n"
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=false\&testId=3 >> $reconfigResult
echo "\n"

#asadmin change pool-name before running test for the second time
echo "\nasadmin set resources.jdbc-resource.jdbc/jdbc-reconfig-test-resource-2.pool-name=jdbc-reconfig-test-pool-1 \n"
./bin/asadmin set resources.jdbc-resource.jdbc/jdbc-reconfig-test-resource-2.pool-name=jdbc-reconfig-test-pool-1

# the server needs to restarted after the pool name change.

elif [ "$1" = "test3" ]; then

echo "\nStatus : jdbc-reconfig-test-resource-2 is set with jdbc-reconfig-test-pool-1 (DB : sample-db with table sampleTable\n"
echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=true\&testId=3 \n"
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=true\&testId=3 >> $reconfigResult
sleep 5
echo view the test results at file://${reconfigResult}
echo "\n"
echo "\nTEST3 executed successfully\n\n"
echo "\n******************************************************************************************************************\n"


#asadmin change pool-name before running TEST4
echo "\nExecuting TEST4 : JDBC Resource reconfiguration\n"
echo "\nTesting if First resource undergoes change in the pool-name with an asadmin set\n"
echo "\nasadmin set resources.jdbc-resource.jdbc/res1.pool-name=pool2\n"
./bin/asadmin set resources.jdbc-resource.jdbc/res1.pool-name=pool2

# server needs to be restarted after the pool name change.


elif [ "$1" = "test4" ]; then

echo "\nGET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=false\&testId=4\n"
#Test should fail when table is sample-db and pass when table is reconfig-db
GET http://localhost:8080/v3_jdbc_dev_tests/ReconfigTestServlet?throwException=false\&testId=4 >> $reconfigResult

echo "\nTEST4 executed successfully\n\n"
echo "\n******************************************************************************************************************\n"

echo view the test results at file://${reconfigResult}
echo "\n"

fi
