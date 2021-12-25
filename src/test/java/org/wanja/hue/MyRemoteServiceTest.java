package org.wanja.hue;

import io.quarkus.test.junit.QuarkusTest;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import javax.inject.Inject;
import java.util.Set;

@QuarkusTest
public class MyRemoteServiceTest {

    @Inject
    @RestClient
    HueRemoteService myRemoteService;

    @Test
    public void testExtensionsRestClientEndpoint() {
    }
}
