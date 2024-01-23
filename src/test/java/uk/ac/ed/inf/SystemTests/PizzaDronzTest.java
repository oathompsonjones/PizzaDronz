package uk.ac.ed.inf.SystemTests;

import junit.framework.TestCase;
import uk.ac.ed.inf.PizzaDronz;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Objects;
import java.util.regex.Pattern;

public class PizzaDronzTest extends TestCase {
    private final String emptyDate = "2023-01-01";

    private final String validDate = "2023-09-01";

    private void resetResultFilesDirectory() {
        var directory = new File("resultfiles");
        for (var file : Objects.requireNonNull(directory.listFiles()))
            file.delete();
    }

    private void runSystem(String date) {
        resetResultFilesDirectory();
        PizzaDronz.main(new String[] { date, "https://ilp-rest.azurewebsites.net" });
    }

    private String readFile(String filename) {
        try (var br = new BufferedReader(new FileReader("resultfiles/" + filename))) {
            StringBuilder sb = new StringBuilder();
            String        line = br.readLine();
            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            return sb.toString().replaceAll("\\s+", "");
        } catch (Exception e) {
            return "";
        }
    }

    private int countRegexMatches(String regex, String string) {
        return Pattern.compile(regex).matcher(string).results().toArray().length;
    }

    public void testFileOutputCount() {
        runSystem(emptyDate);
        var directory = new File("resultfiles");
        assertEquals(3, Objects.requireNonNull(directory.listFiles()).length);
    }

    public void testFileOutputNames() {
        runSystem(emptyDate);
        var directory = new File("resultfiles");
        var files     = Objects.requireNonNull(directory.listFiles());
        assertEquals("deliveries-" + emptyDate + ".json", files[0].getName());
        assertEquals("drone-" + emptyDate + ".geojson", files[1].getName());
        assertEquals("flightpath-" + emptyDate + ".json", files[2].getName());
    }

    public void testEmptyFileContents() {
        runSystem(emptyDate);
        assertEquals(readFile("deliveries-" + emptyDate + ".json"), "[ ]");
        assertEquals(readFile("drone-" + emptyDate + ".geojson"), """
                                                                  {
                                                                    "type" : "FeatureCollection",
                                                                    "features" : [ {
                                                                      "type" : "Feature",
                                                                      "geometry" : {
                                                                        "type" : "LineString",
                                                                        "coordinates" : [ ]
                                                                      },
                                                                      "properties" : {
                                                                        "name" : "Flight Path"
                                                                      }
                                                                    } ]
                                                                  }""");
        assertEquals(readFile("flightpath-" + emptyDate + ".json"), "[ ]");
    }

    public void testFileContents() {
        runSystem(validDate);
        assertEquals(countRegexMatches("\\{[^\\}]+\\}", readFile("deliveries-" + validDate + ".json")), 58);
        assertEquals(countRegexMatches("\\[-?\\d+(\\.\\d+)?,\\d+(\\.\\d+)?\\]",
                                       readFile("drone-" + validDate + ".geojson")
                                      ), 5484);
        assertEquals(countRegexMatches("\\{[^\\}]+\\}", readFile("flightpath-" + validDate + ".json")), 5484);
    }
}
