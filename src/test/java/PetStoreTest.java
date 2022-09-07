import dto.petstore.Category;
import dto.petstore.Pet;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PetStoreTest {

    // testPet для создания нового питомца и createdPet для получения результата создания применяются во всех тестах
    private Pet testPet;
    private Pet createdPet;

    @Test
    @BeforeEach
    public void createPet() {
        testPet = new Pet();
        Category dog = new Category();
        dog.setName("Dog");
        testPet.setName("Sharik");
        testPet.setCategory(dog);
        createdPet = RestAssured.given()
                .body(testPet)
                .contentType(ContentType.JSON)
                .post("https://petstore.swagger.io/v2/pet")
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
    }

    // если работает get /pet/{petId}, то создаем нового питомца и тут же проверяем с помощью get /pet/{petId}
    // что питомец создан и существует
    @Test
    public void createPetTest() {
        Pet checkPet = RestAssured.given()
                .get("https://petstore.swagger.io/v2/pet/" + createdPet.getId())
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
        Assertions.assertEquals(createdPet, checkPet);
    }

    // если работает post /pet, то создаем нового питомца и тут же проверяем, что get /pet/{petId} действительно
    // возвращает только что созданного питомца
    @Test
    public void getPetTest() {
        Pet checkPet = RestAssured.given()
                .get("https://petstore.swagger.io/v2/pet/" + createdPet.getId())
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
        Assertions.assertEquals(createdPet, checkPet);
    }

    // если работают post /pet и get /pet/{petId}, то создаем питомца, обновляем с помощью put /pet и getoм /pet/{petId}
    // проверяем, что он обновился
    @Test
    public void putPetTest() {
        testPet.setId(createdPet.getId());
        Category tovarisch = new Category();
        tovarisch.setName("Dog");
        tovarisch.setName("Tovarisch");
        testPet.setName("Sharikov");
        testPet.setCategory(tovarisch);
        RestAssured.given()
                .body(testPet)
                .contentType(ContentType.JSON)
                .put("https://petstore.swagger.io/v2/pet")
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
        Pet checkPet = RestAssured.given()
                .get("https://petstore.swagger.io/v2/pet/" + createdPet.getId())
                .then()
                .statusCode(200)
                .extract().as(Pet.class);
        Assertions.assertEquals(checkPet, testPet);
    }

    // если работают post /pet и get /pet/{petId}, то создаем питомца, удаляем и проверяем, что его больше нет
    @Test
    public void deletePetTest() {
        RestAssured.given()
                .delete("https://petstore.swagger.io/v2/pet/" + createdPet.getId())
                .then()
                .statusCode(200);
        RestAssured.given()
                .get("https://petstore.swagger.io/v2/pet/" + createdPet.getId())
                .then()
                .statusCode(404);
    }

}
