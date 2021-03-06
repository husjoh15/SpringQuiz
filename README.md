# SpringQuiz # 
<p> <!-- description of the project --> 
SpringQuiz is a quiz game implemented with micro-service architecture using 
springboot.

</p>

## Services:  
### main components 
#### Quiz service 

<p>This module takes care of the actual questions, categories and quizzes and handles
the creation, deletion and delivery of quizzes as well as checking submitted answers.

There is a more detailed description of this module available 
[here](quiz/README.md).
</p>

#### Highscore service

<p>The highscore module can create a new score and then sort all scores with the
highest score on top.

You can view the documentation of this module
[here](highscore/README.md).
</p>


#### User service 
<p>Handles user-details associated with users. 
It does not handle passwords.

more indepth documentation is 
[here](user/README.md).
</p>

## Load balancing and routing ##
#### eureka 
<p>
Netflix eureka is used the load-balance all springboot instances. 
Quiz and User is running on 1 instance while highscore runs with 2.  
</p>

#### zuul 
<p>
Netflix zuul is used as a gateway and is the single service that is open for 
outside connections. It is running on port 80 and provides the following routes.
<br/>
<code> quiz/** </code> 

All routes to the quiz service. See link below for more details. 
[quiz service](quiz/README.md)

<br/>
<code> user/details/** </code>


All routes to the user service see link below for more details. 
[user service](user/README.md)

<br/>
<code> highscore/** </code>

All routes to the highscore service see link below for more details.
[highscore service](link to highscore readme)

</p>

### Security ###
<p>
Zuul is the single entrypoint and no other ports than 80 can be accessed outside of
the overlay network docker provides. Zuul also implements basic authentication and has 
a api called auth handling requests. <br/>

<code> POST /register </code> (register a new user) <br/>
<code> POST /signIn </code> (sign in as existing user) <br/>
<code> GET /user </code> (get information about the authenticated user) <br/>
<br/>

Since the credentials is handled by zuul it has it's own db server(postgres).
I is started with docker-compose.

<i><b> Basic auth: </b></i> <br/>
Authentication is handled by spring security and the sessions 
are stored in a redis store which is shared among the components 
that will need it. 

The actual user credentials(username, password etc) is stored in a postgresql 
db.

<i><b> XSRF protection: </b></i> <br/>
All PUT, POST and PATCH methods that is routed through zuul are secured from XSRF 
attacks, this is proven in the e2e tests where unsafe methods has XSRF-token in both
the header and the cookie.

</p>

## Other features

#### AMQP ####
<p>
 The quiz module communicates with the highscore using AMQP(rabbitmq). Highscore subscribes to the
 queue containing new entries to be inserted(patched) into the database of the highscore service.
 
 <br/>
 Go to the following links to see the implementations: 
 <br/>
 
 [(producer in quiz) /quiz/api/quizzes/{id}/check](quiz/src/main/kotlin/no/group3/springQuiz/quiz/api/QuizController.kt) 
 
 [(receiver in highscore) AMQPScoreListener](highscore/src/main/kotlin/no/group3/springQuiz/highscore/AMQPScoreListener.kt)
 
 This feature is also included in both of the tests in the [e2e module](e2e/src/test/kotlin/no.group3.SpringQuiz.e2e)
 
 
</p>

#### Persistence management ####
 Every service that needs to persist data has it's own database dedicated to
 it. They are all using postgres(besides within the local tests) and all the
 postgres images is started using docker-compose. <br/>
 We struggled a bit to figure out how to handle the persistence when using
 replicas I.e with the case of highscore that is actually started twice in 
 docker-compose, was not sure how to set it up with JPA configuration etc.
 It seems to work fine with having a shared postgres instance with 
 hibernate set to <code>ddl-auto:"update"</code> so we went with this approach. </br> 
 A couple of hours before the delivery the e2e started to fail and we discovered that the highscore instances
 actually inserts duplicates I.e both instances creates a new entry.. I can understand why this is happening the weird thing was that it did not
 before.. </br>
 Unfortunately we did not manage to fix this problem, will probably need an extra mechanism to synchronize
 the insertions, which I could not find any solutions for searching in the material covered in class.
 We did however make sure that the <i>amqp</i> implementation providing this feature actually works
  with a slightly modified test.

## Testing: ##
<p>
Quiz, highscore and user modules all contains rest assured tests. Atleast one per endpoint
 all the test can be run seperately using <code> mvn package </code> in the given module.
 To run all tests run use the same command in the root module. 
 <br/>
 To run all test including e2e run <code>mvn install</code> in root.
</p>

#### e2e ####
<p>
The e2e tests can be found in the e2e module and consist of one test class that performs
test with the whole system running. 
<br/>
The system test is using the docker-compose file in the root folder and is quite heavy to run
since it starts 12 docker containers. If this test is run on a computer with low CPU and/or
 available memory, it might actually fail.
<br/>
The test will take about 2-3 minutes to start up all the services. This is partly because
of all the containers and partly because eureka takes some time to get all instances registered.
<br/> 
<br/>
<br/>
In some cases <code>mvn clean install</code> have given errors because the computer could not manage to
load all the services needed(e.g status 500 because zuul failed to route to the instance because it 
did not start properly). 
The solution to that has been(as seen in the video) to first do 
<code>mvn clean package</code> and then run the tests after package is finished. 
<br/>

Because of the fact that this might happen a link to when the test are run on a computer
with sufficient CPU and memory is available(to prove that they actually work) here:
<br/>

[link to youtube](https://youtu.be/T-dNrG7SBkg)


[amqp-test](e2e/src/test/kotlin/no.group3.SpringQuiz.e2e/HighscoreQuizAmqpIT.kt)
<br/> 
[system-test](e2e/src/test/kotlin/no.group3.SpringQuiz.e2e/SpringQuizIT.kt)

</p>

## Other ##

### Git Repo ###
<p>
Our git repo for the project can be found here https://github.com/Bragalund/SpringQuiz
</p>

### Jenkins ###
<p>
The jenkins-server runs at AWS and listens to this repository. <br/>
It automaticly runs the steps in the Jenkinsfile if it notices any changes and would have deployed to a container-cluster in google-cloud<br/>
The jenkinsfile is runnable, <br/>
but deployment is not possible because of the cost associated with the datapower this project needs to do the steps in the Jenkinsfile. <br/>
The deployment-step is therefore not tested.
</p>

![alt text](./user/images/Jenkins_Build_taking_to_long.bmp "Picture of Jenkins-server build time.")

### Contributions ###

#### Joakim ####
<p>
Github Username: josoder <br/>
Main service: Quiz
<br/>
Additional features:
AMQP, 
eureka,
zuul, 
e2e, 
docs,
docker-compose,
security
</p>

#### Henrik ####
<p>
Github Username: Bragalund <br/>
Main service: User <br>
Additional features:
e2e,
docker/docker-compose,
docs,
Jenkins

</p>

#### Johannes ####
<p>
Github Username: husjoh15 <br/>
Main service: Highscore <br/>
Contributions: I have been responsible for the highscore microservice and all documentation about that.
I have not been able to help alot with setting the services together due to it having problems running on
a slow windows computer. I tried to run the whole project from my computer with docker-compose, and it took
30 minutes before it crashed. So I have tried to help in other ways.
</p>
