package uk.ac.ed.inf.UnitTests.Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import junit.framework.TestCase;
import uk.ac.ed.inf.FlightPaths.FlightPathNode;
import uk.ac.ed.inf.Serializers.FlightPathNodeJSONSerializer;
import uk.ac.ed.inf.ilp.data.LngLat;

import java.io.IOException;
import java.io.StringWriter;

public class FlightPathNodeJSONSerializerTest extends TestCase {
    public void testSerializer() throws IOException {
        var jsonWriter = new StringWriter();
        var module = new SimpleModule().addSerializer(
                FlightPathNode.class,
                new FlightPathNodeJSONSerializer()
                                                     );
        var serializerProvider = new ObjectMapper().registerModule(module);
        serializerProvider.writeValue(jsonWriter, new FlightPathNode("1", new LngLat(0, 0), 90, new LngLat(0, 1)));
        var result = "{\"orderNo\":\"1\",\"fromLongitude\":0.0,\"fromLatitude\":0.0,\"angle\":90.0,\"toLongitude\":0.0,\"toLatitude\":1.0}";
        assertEquals(jsonWriter.toString(), result);
    }
}
