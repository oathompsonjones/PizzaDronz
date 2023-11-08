package uk.ac.ed.inf.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.ac.ed.inf.FlightPaths.FlightPathNode;

import java.io.IOException;

/**
 * Serializes a {@link FlightPathNode} object to JSON.
 */
public class FlightPathNodeJSONSerializer extends StdSerializer<FlightPathNode> {
    /**
     * Creates an instance of the {@link FlightPathNodeJSONSerializer} class.
     */
    public FlightPathNodeJSONSerializer() {
        this(null);
    }

    /**
     * Creates an instance of the {@link FlightPathNodeJSONSerializer} class.
     *
     * @param flightPathNodeClass The class of the {@link FlightPathNode} object.
     */
    public FlightPathNodeJSONSerializer(Class<FlightPathNode> flightPathNodeClass) {
        super(flightPathNodeClass);
    }

    /**
     * Serializes a {@link FlightPathNode} object to JSON.
     *
     * @param node       The {@link FlightPathNode} object to serialize.
     * @param json       The {@link JsonGenerator} to use.
     * @param serializer The {@link SerializerProvider} to use.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void serialize(FlightPathNode node, JsonGenerator json, SerializerProvider serializer) throws IOException {
        json.writeStartObject();
        json.writeStringField("orderNo", node.orderNo());
        json.writeNumberField("fromLongitude", node.fromCoordinate().lng());
        json.writeNumberField("fromLatitude", node.fromCoordinate().lat());
        json.writeNumberField("angle", node.angle());
        json.writeNumberField("toLongitude", node.toCoordinate().lng());
        json.writeNumberField("toLatitude", node.toCoordinate().lat());
        json.writeEndObject();
    }
}
