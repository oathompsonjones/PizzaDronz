package uk.ac.ed.inf.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.ac.ed.inf.FlightPaths.FlightPathNode;

import java.io.IOException;

/**
 * Serializes an array of {@link FlightPathNode} objects to GeoJSON.
 */
public class FlightPathNodeGeoJSONSerializer extends StdSerializer<FlightPathNode[]> {
    /**
     * Creates an instance of the {@link FlightPathNodeGeoJSONSerializer} class.
     */
    public FlightPathNodeGeoJSONSerializer() {
        this(null);
    }

    /**
     * Creates an instance of the {@link FlightPathNodeGeoJSONSerializer} class.
     *
     * @param flightPathNodeArrayClass The class of the array of {@link FlightPathNode} objects.
     */
    public FlightPathNodeGeoJSONSerializer(Class<FlightPathNode[]> flightPathNodeArrayClass) {
        super(flightPathNodeArrayClass);
    }

    /**
     * Serializes an array of {@link FlightPathNode} objects to GeoJSON.
     *
     * @param path       The array of {@link FlightPathNode} objects to serialize.
     * @param json       The {@link JsonGenerator} to use.
     * @param serializer The {@link SerializerProvider} to use.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void serialize(FlightPathNode[] path, JsonGenerator json, SerializerProvider serializer) throws IOException {
        json.writeStartObject();
        json.writeStringField("type", "FeatureCollection");
        json.writeArrayFieldStart("features");
        json.writeStartObject();
        json.writeStringField("type", "Feature");
        json.writeObjectFieldStart("geometry");
        json.writeStringField("type", "LineString");
        json.writeArrayFieldStart("coordinates");
        for (FlightPathNode node : path) {
            json.writeStartArray();
            json.writeNumber(node.fromCoordinate().lng());
            json.writeNumber(node.fromCoordinate().lat());
            json.writeEndArray();
        }
        json.writeEndArray();
        json.writeEndObject();
        json.writeObjectFieldStart("properties");
        json.writeStringField("name", "Flight Path");
        json.writeEndObject();
        json.writeEndObject();
        json.writeEndArray();
        json.writeEndObject();
    }
}
