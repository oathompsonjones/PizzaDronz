package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
     * @param flightPathNodes The array of {@link FlightPathNode} objects to serialize.
     * @param jsonGenerator   The {@link JsonGenerator} to use.
     * @param serializer      The {@link SerializerProvider} to use.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void serialize(FlightPathNode[] flightPathNodes, JsonGenerator jsonGenerator, SerializerProvider serializer) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", "FeatureCollection");
        jsonGenerator.writeArrayFieldStart("features");
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("type", "Feature");
        jsonGenerator.writeObjectFieldStart("geometry");
        jsonGenerator.writeStringField("type", "LineString");
        jsonGenerator.writeArrayFieldStart("coordinates");
        for (FlightPathNode flightPathNode : flightPathNodes) {
            jsonGenerator.writeStartArray();
            jsonGenerator.writeNumber(flightPathNode.fromCoordinate().lng());
            jsonGenerator.writeNumber(flightPathNode.fromCoordinate().lat());
            jsonGenerator.writeEndArray();
        }
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
        jsonGenerator.writeObjectFieldStart("properties");
        jsonGenerator.writeStringField("name", "Flight Path");
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndObject();
        jsonGenerator.writeEndArray();
        jsonGenerator.writeEndObject();
    }
}
