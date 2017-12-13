package no.group3.springQuiz.highscore.api

import io.restassured.RestAssured.get
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import no.group3.springQuiz.highscore.model.dto.PatchScoreDto
import org.hamcrest.CoreMatchers.equalTo
import no.group3.springQuiz.highscore.model.dto.ScoreDto
import org.junit.Test
import org.springframework.test.annotation.DirtiesContext

/**
 * Created by johannes on 08.11.2017.
 */

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class ScoreTest : ScoreTestBase(){
    companion object {
        val HIGHSCORE_PATH = "/highscore"
    }

    @Test
    fun testGet(){
        get(HIGHSCORE_PATH)
                .then()
                .body("size()", equalTo(0))
    }

    @Test
    fun testPost(){
        get(HIGHSCORE_PATH).then().body("size()", equalTo(0))

        val dto = ScoreDto(user="Svein", score = 7)
        given().contentType(ContentType.JSON)
                .body(dto)
                .post(HIGHSCORE_PATH)
                .then()
                .statusCode(201)

        get(HIGHSCORE_PATH).then().body("size()", equalTo(1))
    }

    @Test
    fun testGetSorted()
    {
        get(HIGHSCORE_PATH).then().body("size()", equalTo(0))
        val score1 = addScore(user="Kjell", score=5)
        val score2 = addScore(user="Lars", score=3)
        val score3 = addScore(user="Jonas", score=8)
        get(HIGHSCORE_PATH).then().body("size()", equalTo(3))
        val output = get(HIGHSCORE_PATH).then().extract().asString()
        //Not sure how to test this, but you can see in the testoutput in the console that the scores are sorted.
        println(output)

    }

    @Test
    fun testDelete(){
        get(HIGHSCORE_PATH).then().body("size()", equalTo(0))

        val scoreID = addScore(user="Kjell", score=5)
        get(HIGHSCORE_PATH).then().body("size()", equalTo(1))

        given().pathParam("id", scoreID)
                .delete(HIGHSCORE_PATH + "/{id}")
                .then().statusCode(204)
        get(HIGHSCORE_PATH).then().body("size()", equalTo(0))

    }

    @Test
    fun testPut() {
        get(HIGHSCORE_PATH).then().body("size()", equalTo(0))

        val scoreDto = addScoreDto(user="Per", score=9)

        scoreDto.score = 7

        given().pathParam("id", scoreDto.id)
                .contentType(ContentType.JSON)
                .body(scoreDto)
                .put(HIGHSCORE_PATH + "/{id}")
                .then()
                .statusCode(204)

    }

    @Test
    fun testPatch() {
        get(HIGHSCORE_PATH).then().body("size()", equalTo(0))

        val scoreDto = addScoreDto(user="Leif", score=0)
        val patch = PatchScoreDto(score = 3)

        given().pathParam("id", scoreDto.id)
                .contentType(ContentType.JSON)
                .body(patch)
                .patch(HIGHSCORE_PATH + "/{id}")
                .then()
                .statusCode(204)
    }


}