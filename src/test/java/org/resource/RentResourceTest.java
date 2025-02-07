package org.resource;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.inject.Inject;
import org.hibernate.metamodel.RepresentationMode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.representation.CustomerRepresentation;
import org.representation.RentRepresentation;
import org.representation.TechnicalCheckRepresentation;
import org.representation.VehicleRepresentation;
import org.util.*;

import java.time.LocalDate;
import java.util.List;

import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class RentResourceTest extends IntegrationBase {

    Integer rentId;
    LocalDate startDate;
    Integer compId;

    @BeforeEach
    public void setup() {
        rentId = 4000;
        compId = 2000;
        startDate = LocalDate.of(2023, 12, 5);
    }

    // ---------- GET ----------

    //rent get

    @Test
    public void listAllRents() {
        List<RentRepresentation> rents = when().get("/rent")
                .then()
                .extract()
                .as(new TypeRef<List<RentRepresentation>>() {});

        assertEquals(2, rents.size());
    }

    @Test
    public void listRentsByMonth() {
        List<RentRepresentation> rents = when().get("/rent?month=12")
                .then()
                .extract()
                .as(new TypeRef<List<RentRepresentation>>() {});

        assertEquals(2, rents.size());
    }

    @Test
    public void listRentsByMonthInvalid() {
        List<RentRepresentation> rents = when().get("/rent?month=13")  //month 13 does not exist, should return listAll
                .then()
                .extract()
                .as(new TypeRef<List<RentRepresentation>>() {});

        assertEquals(2, rents.size());
    }

    @Test
    public void listRentByIdValid() {
        RentRepresentation representation = when().get("/rent/"+rentId)
                .then()
                .extract()
                .as(RentRepresentation.class);

        assertEquals(rentId, representation.id);
    }

    @Test
    public void listRentByIdInvalid() {
        when().get("/rent/" + 4005)  //id 4005 not in db
                .then()
                .statusCode(404);

    }

    //customer get

    @Test
    public void listCustomerOfRentValid() {
        CustomerRepresentation representation = when().get("/rent/"+rentId+"/customer")
                .then()
                .extract()
                .as(CustomerRepresentation.class);

        assertEquals(1000, representation.id);
    }

    @Test
    public void listCustomerOfRentInvalid() {
        when().get("/rent/"+4005+"/customer")  //id 4005 not in db
                .then()
                .statusCode(404);

        when().get("/rent/"+null+"/customer")  //id null invalid
                .then()
                .statusCode(404);
    }

    //vehicle get

    @Test
    public void listVehicleOfRentValid() {
        VehicleRepresentation representation = when().get("/rent/"+rentId+"/vehicle")
                .then()
                .extract()
                .as(VehicleRepresentation.class);

        assertEquals(3000, representation.id);
    }

    @Test
    public void listVehicleOfRentInvalid() {
        when().get("/rent/"+4005+"/vehicle")  //id 4005 not in db
                .then()
                .statusCode(404);

        when().get("/rent/"+null+"/vehicle")  //id null invalid
                .then()
                .statusCode(404);
    }

    //technical check get

    @Test
    public void listTechnicalCheckOfRentValid() {
        TechnicalCheckRepresentation representation = when().get("/rent/"+rentId+"/technicalCheck")
                .then()
                .extract()
                .as(TechnicalCheckRepresentation.class);

        assertEquals(5000, representation.id);
    }

    @Test
    public void listTechnicalCheckOfRentInvalid() {
        when().get("/rent/"+4005+"/technicalCheck")  //id 4005 not in db
                .then()
                .statusCode(404);

        when().get("/rent/"+null+"/echnicalCheck")  //id null invalid
                .then()
                .statusCode(404);
    }

    // ---------- PUT ----------

    //@Test
    public void createRentValid() {
        RentRepresentation representation = createRentRepresentation((Integer) 4002);

        RentRepresentation created = given()
                .contentType(ContentType.JSON)
                .body(representation)
                .when().put("/rent/")
                .then().statusCode(201).header("Location", Constants.API_ROOT + "/rent/" + representation.id)
                .extract().as(RentRepresentation.class);

        assertEquals(4002, created.id);
    }

    //@Test
    public void createRentInvalid() {
        RentRepresentation representation = createRentRepresentation(4000);  //4000 already in db

        given()
            .contentType(ContentType.JSON)
            .body(representation)
            .when().put("/rent")
            .then().statusCode(404);
    }

    @Test
    public void updateRentValid() {
        //get the resource
        RentRepresentation representation = when().get("/rent/"+rentId)
                .then().statusCode(200).extract().as(RentRepresentation.class);

        assertEquals(rentId, representation.id);

        //make the update
        String newStartDate = LocalDate.of(2023, 12, 4).toString();  //change startDate day from 5 to 4
        representation.startDate = newStartDate;

        //send the update
        given()
            .contentType(ContentType.JSON)
            .body(representation)
            .when().put("/rent/"+rentId)
            .then().statusCode(204);

        //get the resource again to validate
        RentRepresentation updated = when().get("/rent/"+rentId)
                .then().statusCode(200).extract().as(RentRepresentation.class);

        assertEquals(rentId, updated.id);
        assertEquals(newStartDate, updated.startDate);
    }

    // ---------- DELETE ----------

    @Test
    public void deleteAllRents() {
        when().delete("/rent/")  //deletes all companies
                .then().statusCode(200);

        when().get("/rent/"+4000)
                .then().statusCode(404);  //get must return 404
        when().get("/rent/"+4001)
                .then().statusCode(404);  //get must return 404

        List<RentRepresentation> rents = when().get("rent/")
                .then().extract().as(new TypeRef<List<RentRepresentation>>() {});

        assertEquals(0, rents.size());

        List<TechnicalCheckRepresentation> technicalChecks = when().get("/technicalCheck/")
                .then().extract().as(new TypeRef<List<TechnicalCheckRepresentation>>() {});
        assertEquals(0, technicalChecks.size());
    }

    @Test
    public void deleteOneRentValid() {
        when().delete("/rent/" + rentId)
                .then().statusCode(200);

        when().get("/rent/" + rentId)
                .then().statusCode(404);  //get must return 404
        when().get("/technicalCheck/" + 5000)
                .then().statusCode(404);  //get must return 404
    }

    @Test
    public void deleteOneRentInvalid() {
        when().get("/rent/" + 4005)  //4005 not in db
                .then().statusCode(404);  //get must return 404
    }


    // ---------- misc ----------

    private RentRepresentation createRentRepresentation(Integer id) {
        RentRepresentation representation = new RentRepresentation();
        representation.id = id;
        representation.startDate = LocalDate.of(2023,10,10).toString();
        representation.endDate = LocalDate.of(2023,10,20).toString();
        representation.rentState = RentState.Finished;
        representation.fixedCost = new Money(770);  //assume vehicle with id 3007 is rented
        representation.miles = 130;  //company with id 2001 which owns the vehicles 3007, policy: .15 -> 100, .25 -> 200
        representation.mileageCost = new Money(45.25);
        representation.damageCost = new Money(0);  //assume no damage
        representation.totalCost = new Money(representation.mileageCost.getAmount() +
                representation.fixedCost.getAmount() + representation.damageCost.getAmount());
        representation.rentedVehicle = 3007;
        representation.customer = 1000;
        representation.technicalCheck = 1;
        return representation;
    }

    private VehicleRepresentation createVehicleRepresentation(Integer id) {
        VehicleRepresentation representation = new VehicleRepresentation();
        representation.id = id;
        representation.manufacturer = "AUDI";
        representation.model = "A7";
        representation.year = 2021;
        representation.miles = 100000;
        representation.plateNumber = "MMA-8745";
        representation.vehicleType = VehicleType.Sedan;
        representation.vehicleState = VehicleState.Available;
        representation.fixedCharge = new Money(70);
        return representation;
    }

    private CustomerRepresentation createCustomerRepresentation(Integer id) {
        CustomerRepresentation representation = new CustomerRepresentation();
        representation.id = id;
        representation.name = "ΙΩΑΝΝΗΣ";
        representation.email = "evangellou@gmail.com";
        representation.password = "johnjohn";
        representation.phone = "6941603677";
        representation.street = "ΛΕΥΚΑΔΟΣ 22";
        representation.city = "ΑΘΗΝΑ";
        representation.zipcode = "35896";
        representation.AFM = "166008282";
        representation.surname = "ΕΥΑΓΓΕΛΟΥ";
        representation.expirationDate = LocalDate.of(2027,11,26).toString();
        representation.number = "7894665213797564";
        representation.holderName = "ΙΩΑΝΝΗΣ ΕΥΑΓΓΕΛΟΥ";
        return representation;
    }

    private TechnicalCheckRepresentation createTechnicalCheckRepresentation(Integer id) {
        TechnicalCheckRepresentation representation = new TechnicalCheckRepresentation();
        representation.id = 5002;
        representation.damageType = DamageType.NoDamage;
        return representation;
    }

/*makeRent Tests */

@Test
public void makeRent() {
    CustomerRepresentation customerRepresentation = createCustomerRepresentation(1000);
    VehicleRepresentation vehicleRepresentation = createVehicleRepresentation(3000);

    Response response = given().contentType(ContentType.JSON)
            .queryParam("startDate", LocalDate.now().toString())
            .queryParam("endDate", LocalDate.now().plusDays(5).toString())
            .queryParam("vehicleId", vehicleRepresentation.id)
            .when().post("/rent/newRent/"+customerRepresentation.id)
            .then().extract().response();
    System.out.println("Response: " + response.getBody().asString());

    assertEquals(200, response.getStatusCode());
    assertEquals("You rented the vehicle", response.getBody().asString());

    VehicleRepresentation v = when().get("/vehicle/" + vehicleRepresentation.id)
            .then()
            .extract()
            .as(VehicleRepresentation.class);
    assertEquals(vehicleRepresentation.id, v.id);
    assertEquals(v.vehicleState, VehicleState.Rented);
}

    @Test
    public void makeRentWithInvalidDates() {
        CustomerRepresentation customerRepresentation = createCustomerRepresentation(1001);
        VehicleRepresentation vehicleRepresentation = createVehicleRepresentation(3001);

        Response response = given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().plusDays(5).toString())
                .queryParam("endDate", LocalDate.now().toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/newRent/"+customerRepresentation.id)
                .then().extract().response();

        System.out.println("Response: " + response.getBody().asString());
        assertTrue(response.getBody().asString().contains("Not good dates"));
    }

    @Test
    public void makeRentInvalidVehicle() {
        CustomerRepresentation customerRepresentation = createCustomerRepresentation(1001);
        VehicleRepresentation vehicleRepresentation = createVehicleRepresentation(null);  //id null invalid

        given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/newRent/"+customerRepresentation.id)
                .then().statusCode(404);

        vehicleRepresentation = createVehicleRepresentation(3020);  //3020 not in db
        given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/newRent/"+customerRepresentation.id)
                .then().statusCode(400);
    }

    @Test
    public void makeRentInvalidCustomer() {
        CustomerRepresentation customerRepresentation = createCustomerRepresentation(null);
        VehicleRepresentation vehicleRepresentation = createVehicleRepresentation(3001);  //id null invalid

        given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/customer/" + customerRepresentation.id + "/rent/")
                .then().statusCode(404);

        vehicleRepresentation = createVehicleRepresentation(1007);  //1007 not in db
        given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/customer/" + customerRepresentation.id + "/rent/")
                .then().statusCode(404);
    }
/*returnVehicle tests*/

    @Test
    public void returnVehicle() {
        //first make a rent
        CustomerRepresentation customerRepresentation = createCustomerRepresentation(1000);
        VehicleRepresentation vehicleRepresentation = createVehicleRepresentation(3000);

        Response response = given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/newRent/"+customerRepresentation.id)
                .then().extract().response();
        System.out.println("Response: " + response.getBody().asString());

        assertEquals(200, response.getStatusCode());
        assertEquals("You rented the vehicle", response.getBody().asString());

        //then return it
        response = given().contentType(ContentType.JSON)
                .queryParam("miles", 50.0f)
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/returnVehicle/"+customerRepresentation.id)
                .then().extract().response();
        System.out.println(response.getBody().asString());
        assertEquals(200, response.getStatusCode());
        assertEquals("Vehicle returned", response.getBody().asString());
    }

    @Test
    public void returnVehicleInvalidCustomer() {
        //first make a rent
        CustomerRepresentation customerRepresentation = createCustomerRepresentation(1000);
        VehicleRepresentation vehicleRepresentation =createVehicleRepresentation(3000);

        Response response = given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/newRent/"+customerRepresentation.id)
                .then().extract().response();
        System.out.println("Response: " + response.getBody().asString());

        assertEquals(200, response.getStatusCode());
        assertEquals("You rented the vehicle", response.getBody().asString());

        //then return it with invalid customer representation
        customerRepresentation.id = 1010;
        response = given().contentType(ContentType.JSON)
                .queryParam("miles", 50.0f)
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/returnVehicle/"+customerRepresentation.id)
                .then().extract().response();
        assertEquals(400, response.getStatusCode());

        customerRepresentation.id = null;
        response = given().contentType(ContentType.JSON)
                .queryParam("miles", 50.0f)
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/returnVehicle/"+customerRepresentation.id)
                .then().extract().response();
        assertEquals(404, response.getStatusCode());

        //now make miles <= 0
        customerRepresentation.id = 1000;
        response = given().contentType(ContentType.JSON)
                .queryParam("miles", -10.0f)
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/returnVehicle/"+customerRepresentation.id)
                .then().extract().response();
        assertEquals(400, response.getStatusCode());
    }

    @Test
    public void returnVehicleInvalidVehicle() {
        //first make a rent
        CustomerRepresentation customerRepresentation = createCustomerRepresentation(1000);
        VehicleRepresentation vehicleRepresentation = createVehicleRepresentation(3000);

        Response response = given().contentType(ContentType.JSON)
                .queryParam("startDate", LocalDate.now().toString())
                .queryParam("endDate", LocalDate.now().plusDays(5).toString())
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/newRent/"+customerRepresentation.id)
                .then().extract().response();
        System.out.println("Response: " + response.getBody().asString());

        //now return it with wrong vehicle representation
        vehicleRepresentation.id = 3001;
        response = given().contentType(ContentType.JSON)
                .queryParam("miles", 50.0f)
                .queryParam("vehicleId", vehicleRepresentation.id)
                .when().post("/rent/returnVehicle/"+customerRepresentation.id)
                .then().extract().response();
        assertEquals("This vehicle cannot be returned", response.getBody().asString());
    }
}