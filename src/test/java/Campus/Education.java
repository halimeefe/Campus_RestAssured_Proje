package Campus;

import com.github.javafaker.Faker;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.baseURI;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;

public class Education {


    Faker faker=new Faker();
    RequestSpecification recSpec;
    String educationID;

    String educationName;


    @BeforeClass
    public void Setup()  {
        baseURI="https://test.mersys.io";

        Map<String,String> userCredential=new HashMap<>();
        userCredential.put("username","turkeyts");
        userCredential.put("password","TechnoStudy123");
        userCredential.put("rememberMe","true");

        Cookies cookies=
                given()
                        .contentType(ContentType.JSON)
                        .body(userCredential)

                        .when()
                        .post("/auth/login")

                        .then()
                        //.log().all()
                        .statusCode(200)
                        .extract().response().getDetailedCookies()
                ;

        recSpec= new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .addCookies(cookies)
                .build();
    }


    @Test
    public void createEducation(){

        Map<String,String> education=new HashMap<>();
        educationName=faker.address().country()+faker.number().digits(5);
        education.put("name",educationName);
        education.put("code",faker.address().countryCode()+faker.number().digits(5));
        educationID=
                given()
                        .spec(recSpec)
                        .body(education)
                        .log().body()

                        .when()
                        .post("school-service/api/subject-categories")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().path("id");
        ;

        System.out.println("educationID = " + educationID);
    }


    @Test(dependsOnMethods = "createEducation")
    public void createEducationNegative(){
        Map<String,String> education=new HashMap<>();
        education.put("name",educationName);
        education.put("code",faker.address().countryCode()+faker.number().digits(5));

        given()
                .spec(recSpec)
                .body(education)
                .log().body()

                .when()
                .post("school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(400)
                .body("message", containsString("already"))  // gelen body deki.
        ;

    }

    @Test(dependsOnMethods = "createEducationNegative")
    public void updateEducation(){
        Map<String,String> education=new HashMap<>();
        education.put("id",educationID);

        educationName="kral reis"+faker.number().digits(7);
        education.put("name",educationName);
        education.put("code",faker.address().countryCode()+faker.number().digits(5));

        given()

                .spec(recSpec)
                .body(education)
                .log().body()

                .when()
                .put("school-service/api/subject-categories")

                .then()
                .log().body()
                .statusCode(200)
                .body("name",equalTo(educationName))
        ;

    }

    @Test(dependsOnMethods = "updateEducation")
    public void deleteEducation(){
        given()

                .spec(recSpec)
                .pathParam("educationID",educationID)
                .log().uri()

                .when()
                .delete("school-service/api/subject-categories/{educationID}")

                .then()
                .log().body()
                .statusCode(200)

        ;



    }

    @Test(dependsOnMethods = "deleteEducation")
    public void deleteEducationNegative(){
        given()

                .spec(recSpec)
                .pathParam("educationID",educationID)
                .log().uri()

                .when()
                .delete("school-service/api/subject-categories/{educationID}")

                .then()
                .log().body()
                .statusCode(400)
        // .body("message",equalTo(""))
        ;
    }

}




