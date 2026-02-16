package simulations;

import static io.gatling.javaapi.core.CoreDsl.*;
import static io.gatling.javaapi.http.HttpDsl.*;

import io.gatling.javaapi.core.*;
import io.gatling.javaapi.http.*;

import static io.gatling.javaapi.http.HttpDsl.http;

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
                                        .check(jsonPath("$.token").saveAs("authToken")))
                        .pause(1L)

                        .exec(http("Get shows")
                                        .get("/api/v1/shows/")
                                        .header("Authorization", "Bearer #{authToken}")
                                        .check(status().is(200)));

        // Primero se inicializa atributos
        // Luego bloques { }
        // Finalmente el constructor
        {
                setUp(scn.injectOpen(constantUsersPerSec(10).during(30))).protocols(httpProtocol);
        }

}
