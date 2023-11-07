package uk.ac.ed.inf;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

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
     * @param flightPathNode The {@link FlightPathNode} object to serialize.
     * @param jsonGenerator  The {@link JsonGenerator} to use.
     * @param serializer     The {@link SerializerProvider} to use.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void serialize(FlightPathNode flightPathNode, JsonGenerator jsonGenerator, SerializerProvider serializer) throws IOException {
        jsonGenerator.writeStartObject();
        jsonGenerator.writeStringField("orderNo", flightPathNode.orderNo());
        jsonGenerator.writeNumberField("fromLongitude", flightPathNode.fromCoordinate() == null ? null : flightPathNode.fromCoordinate().lng());
        jsonGenerator.writeNumberField("fromLatitude", flightPathNode.fromCoordinate() == null ? null : flightPathNode.fromCoordinate().lat());
        jsonGenerator.writeNumberField("angle", flightPathNode.angle());
        jsonGenerator.writeNumberField("toLongitude", flightPathNode.toCoordinate() == null ? null : flightPathNode.toCoordinate().lng());
        jsonGenerator.writeNumberField("toLatitude", flightPathNode.toCoordinate() == null ? null : flightPathNode.toCoordinate().lat());
        jsonGenerator.writeNumberField("ticksSinceStartOfCalculation", flightPathNode.ticksSinceStartOfCalculation());
        jsonGenerator.writeEndObject();
    }
}
