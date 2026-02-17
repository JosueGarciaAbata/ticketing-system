package simulations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.http.HttpDsl.http;

/*
* Prueba que se encarga de simular la carga de usuarios autenticados que inician el proceso de checkout. Se simula un escenario donde 1000 usuarios inician sesión y luego intentan iniciar un proceso de checkout al mismo tiempo. El objetivo es comprobar que no se genere ningun error de concurrencia, como por ejemplo se generen sesiones de checkout duplicadas.
*/
public class LoadTestSimulation extends Simulation {

        // Paso 1: protocolo Http
        private static HttpProtocolBuilder httpProtocol = http
                        .baseUrl("http://localhost:8080")
                        .acceptHeader("application/json")
                        .contentTypeHeader("application/json");

        // Paso 2: escenario
        private static ScenarioBuilder scn = scenario("Authenticated Scenario")

                        .exec(http("Login request")
                                        .post("/api/v1/login")
                                        .body(RawFileBody("login.json")).asJson()
                                        .check(jsonPath("$.token").saveAs("authToken"))
                                        .silent()) // No se muestra en los resultados

                        .exec(http("Start checkout")
                                        .post("/api/v1/checkout/db-only")
                                        .body(RawFileBody("start_checkout.json")).asJson()
                                        .header("Authorization", "Bearer #{authToken}")
                                        .check(status().in(200, 409)));

        // Primero se inicializa atributos
        // Luego bloques { }
        // Finalmente el constructor
        {
                setUp(scn.injectOpen(atOnceUsers(1000))).protocols(httpProtocol);
        }

}
