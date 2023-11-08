package uk.ac.ed.inf.RestService;


/**
 * A record to represent an endpoint.
 *
 * @param <T> The type of the response from the endpoint.
 */
public record Endpoint<T>(
        String url,
        Class<T> clazz
) {}
