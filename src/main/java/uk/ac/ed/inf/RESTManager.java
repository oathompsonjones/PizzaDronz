package uk.ac.ed.inf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;

public class RESTManager {
    private final ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());
    private       String       baseUrl;

    public RESTManager(String baseUrl) {
        this.baseUrl = baseUrl;
        if (!this.baseUrl.endsWith("/"))
            this.baseUrl += "/";
    }

    private Object GET(Endpoints endpoint) {
        return GET(endpoint, "");
    }

    private Object GET(Endpoints endpoint, String args) {
        try {
            return mapper.readValue(new URL(baseUrl + endpoint.getUrl() + args), endpoint.getReturnType());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isAlive() {
        return (boolean) GET(Endpoints.IS_ALIVE);
    }

    public Restaurant[] getRestaurants() {
        return (Restaurant[]) GET(Endpoints.RESTAURANTS);
    }

    public Order[] getOrders() {
        return (Order[]) GET(Endpoints.ORDERS);
    }

    public Order[] getOrders(LocalDate date) {
        return (Order[]) GET(Endpoints.ORDERS, "/" + date);
    }

    public NamedRegion getCentralArea() {
        return (NamedRegion) GET(Endpoints.CENTRAL_AREA);
    }

    public NamedRegion[] getNoFlyZones() {
        return (NamedRegion[]) GET(Endpoints.NO_FLY_ZONES);
    }

    private enum Endpoints {
        IS_ALIVE("isAlive", boolean.class),
        RESTAURANTS("restaurants", Restaurant[].class),
        ORDERS("orders", Order[].class),
        CENTRAL_AREA("centralArea", NamedRegion.class),
        NO_FLY_ZONES("noFlyZones", NamedRegion[].class);

        private final String   url;
        private final Class<?> returnType;

        Endpoints(String url, Class<?> returnType) {
            this.url = url;
            this.returnType = returnType;
        }

        public String getUrl() {
            return url;
        }

        public Class<?> getReturnType() {
            return returnType;
        }
    }
}
