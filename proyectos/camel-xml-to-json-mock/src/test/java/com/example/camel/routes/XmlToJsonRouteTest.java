package com.example.camel.routes;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.AdviceWith;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.UseAdviceWith;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test de integración para la ruta XmlToJsonTransformRoute.
 * 
 * Usa @UseAdviceWith para mockear el endpoint 'file:' y reemplazarlo
 * con 'direct:start' para facilitar el testing.
 */
@CamelSpringBootTest
@SpringBootTest
@UseAdviceWith // Permite aconsejar la ruta antes de arrancar el contexto
class XmlToJsonTransformRouteTest {

  @Autowired
  CamelContext camelContext;

  @Autowired
  ProducerTemplate producerTemplate;

  @EndpointInject("mock:out")
  MockEndpoint mockOut;

  private static final String XML =
      "<Person>" +
      "  <id>123</id>" +
      "  <name> Ada Lovelace </name>" +
      "  <age>36</age>" +
      "</Person>";

  @BeforeEach
  void setup() throws Exception {
    // En Camel 4.x usamos AdviceWith directamente con el routeId
    AdviceWith.adviceWith(camelContext, XmlToJsonTransformRoute.ROUTE_ID, a -> {
            a.replaceFromWith("direct:start");
            // Evitamos tocar disco: añadimos un mock al final
            a.weaveAddLast().to("mock:out");
        });
    camelContext.start();
  }

  @Test
  void xmlToJson_ok() throws Exception {
    mockOut.expectedMessageCount(1);

    producerTemplate.sendBody("direct:start", XML);

    mockOut.assertIsSatisfied();

    String json = mockOut.getExchanges().get(0).getIn().getBody(String.class);

    // Asserts básicos sobre el JSON (podrías parsearlo con Jackson si prefieres)
    org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"name\":\"Ada Lovelace\""));
    org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"id\":\"123\""));
    org.junit.jupiter.api.Assertions.assertTrue(json.contains("\"age\":36"));
  }
}
