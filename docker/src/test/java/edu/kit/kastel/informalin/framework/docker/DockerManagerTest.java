/* Licensed under MIT 2022. */
package edu.kit.kastel.informalin.framework.docker;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

class DockerManagerTest {
    private DockerManager dm;

    @Test
    void testCreation() throws Exception {
        dm = new DockerManager("tests");
        Assertions.assertTrue(dm.getContainerIds().isEmpty());
        var id = dm.createContainerByImage("httpd:2.4", 12345);
        Assertions.assertNotNull(id);
        Assertions.assertFalse(id.isBlank());
        Assertions.assertEquals(1, dm.getContainerIds().size());
        Assertions.assertEquals(dm.getContainerIds().get(0), id);

        // Verify that the service runs ..
        URL url = new URL("http://127.0.0.1:12345");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        InputStream is = con.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
        String line;
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\n');
        }
        rd.close();
        var data = response.toString();
        Assertions.assertTrue(data.contains("It works!"));
    }

    @AfterEach
    void tearDown() {
        dm.shutdownAll();
        Assertions.assertTrue(dm.getContainerIds().isEmpty());
    }
}
