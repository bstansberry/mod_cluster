/*
 *  mod_cluster
 *
 *  Copyright(c) 2008 Red Hat Middleware, LLC,
 *  and individual contributors as indicated by the @authors tag.
 *  See the copyright.txt in the distribution for a
 *  full listing of individual contributors.
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library in the file COPYING.LIB;
 *  if not, write to the Free Software Foundation, Inc.,
 *  59 Temple Place - Suite 330, Boston, MA 02111-1307, USA
 *
 * @author Jean-Frederic Clere
 * @version $Revision$
 */

package org.jboss.mod_cluster;

import java.io.IOException;

import junit.framework.TestCase;

import org.apache.catalina.Engine;
import org.apache.catalina.Service;
import org.jboss.modcluster.ModClusterService;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.StandardServer;

public class TestStickyForce extends TestCase {

    /* Test failover */
    public void testStickyForce() {

        Maintest.testPort(8010);
        Maintest.testPort(8011);

        boolean clienterror = false;
        StandardServer server = new StandardServer();
        JBossWeb service = null;
        JBossWeb service2 = null;
        Connector connector = null;
        Connector connector2 = null;
        ModClusterService cluster = null;
        System.out.println("TestStickyForce Started");
        System.setProperty("org.apache.catalina.core.StandardService.DELAY_CONNECTOR_STARTUP", "false");
        try {

            service = new JBossWeb("sticky3",  "localhost");
            connector = service.addConnector(8010);
            connector.setProperty("connectionTimeout", "3000");
            server.addService(service);

            service2 = new JBossWeb("sticky4",  "localhost");
            connector2 = service2.addConnector(8011);
            connector2.setProperty("connectionTimeout", "3000");
            server.addService(service2);

            cluster = Maintest.createClusterListener(server, "224.0.1.105", 23364, false, null, true, false, true, "secret");

        } catch(Exception ex) {
            ex.printStackTrace();
            fail("can't start service");
        }

        // start the server thread.
        ServerThread wait = new ServerThread(3000, server);
        wait.start();

        // Wait until httpd as received the nodes information.
        String [] nodes = new String[2];
        nodes[0] = "sticky3";
        nodes[1] = "sticky4";
        if (!Maintest.TestForNodes(cluster, nodes))
            fail("can't start nodes");

        // Start the client and wait for it.
        Client client = new Client();

        // Wait for it.
        try {
            if (client.runit("/MyCount", 10, false, true) != 0)
                clienterror = true;
        } catch (Exception ex) {
            ex.printStackTrace();
            clienterror = true;
        }
        if (clienterror)
            fail("Client error");

        // Stop the connector that has received the request...
        String node = client.getnode();
        int port;
        if ("sticky4".equals(node)) {
            connector = connector2;
            node = "sticky3";
            port = 8011;
        } else {
            node = "sticky4";
            port = 8010;
        }
        if (connector != null) {
            try {
                connector.stop();
            } catch (Exception ex) {
                ex.printStackTrace();
                fail("can't stop connector");
            }
            /* wait until the connector has stopped */
            int countinfo = 0;
            while (Maintest.testPort(port) && countinfo < 20) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                countinfo++;
            }
            if (countinfo == 20)
                fail("can't stop connector");
        }

        // Run a test on it. (it waits until httpd as received the nodes information).
        client.setnode(node);
        try {
            client.setdelay(30000);
            client.start();
            client.join();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (client.getresultok())
            System.out.println("Test DONE");
        else {
            System.out.println("Test FAILED");
            clienterror = true;
        }

        // Stop the server or services.
        try {
            wait.stopit();
            wait.join();
            server.removeService(service);
            server.removeService(service2);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        // Wait until httpd as received the stop messages.
        Maintest.testPort(8010);
        Maintest.testPort(8011);
        if (!Maintest.TestForNodes(cluster, null))
            System.out.println("Can't stop nodes");
        Maintest.StopClusterListener();
        /* XXX:  In fact it doesn't stop correctly ... Something needs to be fixed */

        // Test client result.
        if ( !clienterror && client.httpResponseCode != 503 ) 
            fail("Client test should have failed");
        Maintest.waitn();
        System.out.println("TestStickyForce Done");
    }
}
