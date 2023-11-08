package uk.ac.ed.inf.Serializers;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import uk.ac.ed.inf.ilp.data.Order;

import java.io.IOException;

/**
 * Serializes an {@link Order} object to JSON.
 */
public class OrderJSONSerializer extends StdSerializer<Order> {
    /**
     * Creates an instance of the {@link OrderJSONSerializer} class.
     */
    public OrderJSONSerializer() {
        this(null);
    }

    /**
     * Creates an instance of the {@link OrderJSONSerializer} class.
     *
     * @param orderClass The class of the {@link Order} object.
     */
    public OrderJSONSerializer(Class<Order> orderClass) {
        super(orderClass);
    }

    /**
     * Serializes an {@link Order} object to JSON.
     *
     * @param order      The {@link Order} object to serialize.
     * @param json       The {@link JsonGenerator} to use.
     * @param serializer The {@link SerializerProvider} to use.
     *
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public void serialize(Order order, JsonGenerator json, SerializerProvider serializer) throws IOException {
        json.writeStartObject();
        json.writeStringField("orderNo", order.getOrderNo());
        json.writeStringField("orderStatus", order.getOrderStatus().toString());
        json.writeStringField("orderValidationCode", order.getOrderValidationCode().toString());
        json.writeNumberField("costInPence", order.getPriceTotalInPence());
        json.writeEndObject();
    }
}
