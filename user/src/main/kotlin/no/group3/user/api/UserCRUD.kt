package no.group3.user.api

import io.swagger.annotations.Api
import io.swagger.annotations.ApiOperation
import io.swagger.annotations.ApiParam
import io.swagger.annotations.ApiResponse
import no.group3.user.model.dto.UserConverter
import no.group3.user.model.dto.UserDto
import no.group3.user.model.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.ConstraintViolationException

const val BASE_JSON = MediaType.APPLICATION_JSON_VALUE
const val USER_JSON = "application/vnd.group3.user+json;charset=UTF-8;version=2"
const val ID_PARAM = "Id of user"


// Creates base url. example: www.someurl.com/user
@Api(value = "/users", description = "CRUD for User") //Creates documentation of API with Swagger
@RequestMapping(
        path = arrayOf("/users"),
        produces = arrayOf(BASE_JSON)
)
@RestController
class UserCRUD {

    @Autowired
    private lateinit var userRepository: UserRepository

    // TODO remove this
    @GetMapping("")
    fun something(): String {
        return "User endpoint works"
    }

    @ApiOperation("Creates a new user") //Swagger documentation
    @PostMapping(consumes = arrayOf(BASE_JSON))
    @ApiResponse(code= 201, message = "Returns the ID of the new user") //Swagger expected return value
    fun createUser(
            @ApiParam("null, username, firstname, lastname, email, password")
            @RequestBody
            userDto: UserDto): ResponseEntity<Long> {


        //Checks of fields in object from client

        if(userDto.id != null){
            return ResponseEntity.status(400).build() // Id should be null. Id is generated by database.
        }
        if(userDto.userName.isNullOrEmpty() || userDto.firstName.isNullOrEmpty() || userDto.lastName.isNullOrEmpty() || userDto.email.isNullOrEmpty() || userDto.password.isNullOrEmpty()){
            return ResponseEntity.status(400).build()
        }

        //Trying to create user

        val userId: Long?
        try {
                userId = userRepository.createUser(userDto.userName!!, userDto.firstName!!, userDto.lastName!!, userDto.email!!, userDto.password!!)
        }catch (e: ConstraintViolationException){
            e.stackTrace
            return ResponseEntity.status(400).build()
        }

        // Checks userId from database

        if(userId == null){
            return ResponseEntity.status(500).build()
        }

        // Returns userId generated by database

        return ResponseEntity.status(201).body(userId)
    }

    //Get one user with Id
    @ApiOperation("Get one user by id")
    @GetMapping(path = arrayOf("/{id}"))
    @ApiResponse(code = 200, message = "user-object")
    fun getUserWithId(@ApiParam(ID_PARAM) //documentation for swagger
                      @PathVariable("id")  //The actual parameter passed in by url
                      userId: String?): ResponseEntity<UserDto>  //input and Return-values
    {
        val id: Long
        try {
            id = userId!!.toLong() //Cast String(userId) to Long
        } catch (exception: Exception) {
            return ResponseEntity.status(404).build() //User not found
        }

        //Check if user exists
        val userDto = userRepository.findOne(id) ?: return ResponseEntity.status(404).build()

        //Return 200 header and userDto after creating it.
        return ResponseEntity.ok(UserConverter.transform(userDto))
    }


    //Delete user with Id

    //Put User with Id

    //Post New User

}