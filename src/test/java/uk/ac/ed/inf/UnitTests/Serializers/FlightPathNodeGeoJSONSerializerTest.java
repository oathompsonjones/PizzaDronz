package uk.ac.ed.inf.UnitTests.Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import junit.framework.TestCase;
import uk.ac.ed.inf.FlightPaths.FlightPathNode;
import uk.ac.ed.inf.Serializers.FlightPathNodeGeoJSONSerializer;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.io.IOException;
import java.io.StringWriter;

public class FlightPathNodeGeoJSONSerializerTest extends TestCase {
    public void testSerializer() throws IOException {
        var jsonWriter         = new StringWriter();
        var module             = new SimpleModule().addSerializer(
                FlightPathNode[].class,
                new FlightPathNodeGeoJSONSerializer()
                                                                 );
        var serializerProvider = new ObjectMapper().registerModule(module);
        serializerProvider.writeValue(jsonWriter, new FlightPathNode[] {
                new FlightPathNode("1", new LngLat(0, 0), 90, new LngLat(0, 1)),
                new FlightPathNode("2", new LngLat(0, 1), 180, new LngLat(0, 2)),
                new FlightPathNode("3", new LngLat(0, 2), 270, new LngLat(0, 3)),
                new FlightPathNode("4", new LngLat(0, 3), 0, new LngLat(0, 4)),
                new FlightPathNode("5", new LngLat(0, 4), 90, new LngLat(0, 5)),
                });
        var result = "{\"type\":\"FeatureCollection\",\"features\":[{\"type\":\"Feature\","
                     + "\"geometry\":{\"type\":\"LineString\",\"coordinates\":[[0.0,0.0],[0.0,1.0],[0.0,2.0],[0.0,3.0],[0.0,4.0]]},\"properties\":{\"name\":\"Flight Path\"}}]}";
        assertEquals(jsonWriter.toString(), result);
    }
}
