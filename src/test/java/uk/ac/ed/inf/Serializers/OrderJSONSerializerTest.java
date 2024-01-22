package uk.ac.ed.inf.Serializers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import junit.framework.TestCase;
import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.constant.OrderValidationCode;
import uk.ac.ed.inf.ilp.data.CreditCardInformation;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Pizza;

import java.io.IOException;
import java.io.StringWriter;
import java.time.LocalDate;

public class OrderJSONSerializerTest extends TestCase {
    public void testSerializer() throws IOException {
        var jsonWriter         = new StringWriter();
        var module             = new SimpleModule().addSerializer(Order.class, new OrderJSONSerializer());
        var serializerProvider = new ObjectMapper().registerModule(module);
        serializerProvider.writeValue(jsonWriter, new Order(
                "1",
                LocalDate.EPOCH,
                OrderStatus.DELIVERED,
                OrderValidationCode.NO_ERROR,
                500,
                new Pizza[] { new Pizza("Pizza", 500) },
                new CreditCardInformation("4123123412341234", "01/25", "123")
        ));
        var result = "{\"orderNo\":\"1\",\"orderStatus\":\"DELIVERED\",\"orderValidationCode\":\"NO_ERROR\",\"costInPence\":500}";
        assertEquals(jsonWriter.toString(), result);
    }
}
