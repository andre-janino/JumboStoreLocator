<p align="center">
  <img src="https://scontent.fbfh3-3.fna.fbcdn.net/v/t1.0-9/36340456_687081898309540_8209585383621525504_o.jpg?_nc_cat=104&_nc_sid=dd9801&_nc_ohc=1ACkEuhUoKMAX9gvYzB&_nc_ht=scontent.fbfh3-3.fna&oh=56b1f4d8cc22941418bb294f422ef044&oe=5F4758A7" title="Jumbo" alt="Jumbo supermarkten"/>
</p>

# Jumbo Store Locator

A private repository for Jumbo coding assessment: Create an application that shows the 5 closest Jumbo stores to a given position.
Powered by Spring Boot + Vue.js.

TODO: improve the readme as the project starts to take shape, adding missing microservices, tech stacks and any other relevant information.

## Index :pushpin:
- [About the project](#about)
- [How to run](#run)
- [Future enhancements](#future)

## About the project <a name="about"></a> :scroll:

This project was developed using _`Spring Boot (2.3.2.RELEASE)`_ for the back-end microservices and _`Vue.js 2.6.11`_ for the front-end components. Several libraries were used to fulfill the needed business logics; the main ones are listed in the following sub-sections.

The solution is divided into the _`frontend`_ project for the user interface and _`user-service, api-gateway, store-service`_ for the back-end:
- frontend: Vue.js project with a _`login`_ and _`find store`_ page. Communicates directly with the _`api-gateway`_.
- api-gateway: deals with user authentication and redirects requests to _`user-service and store-service`_.
- user-service: customer entity/controller/service/etc with a simple in-memory h2 database.
- store-service: store entity/controller/service/etc with a mongodb database implementation, returns N closest stores to a given location.

#### Netflix Eureka

- _`Service Discovery`_ is performed by _`Netflix Eureka`_. This is needed due to the microservice architecture that was adopted.

#### Netflix Hystrix

- _`Netflix Hystrix`_ is used to implement the _`Circuit Breaker`_ design pattern, that is, if a microservice is unavailable, a _`fallback`_ method is called to prevent a systematic failure.
- _`Bulkhead`_ design pattern is also covered by this library, by isolating threads through Hystrix's _`threadPoolKey`_.

#### Netflix Zuul

- _`Spring Zuul`_ is employed as an _`API Gateway`_. It uses Eureka to discover microservices' instances and redirects requests accordingly.

#### Google Maps API

- _`Google maps API`_ is used to retrieve the 5 closes stores based on a given location, as wel as displaying said stores on an embeeded map on the _`frontend`_ project.

#### TODO: add remainder of the used tech stack (still to be defined)

## How to run <a name="run"></a> :wrench:

TODO: add run instructions, needed software (e.g. Java 8, Maven), etc.

## Future enhancements <a name="future"></a> :clock130:

- Migrate the h2 database into another database. PostgreeSQL and MySQL are valid options in terms of SQL, while MongoDB and DynamoDB are appealing ones in terms of NoSQL.
- Add user roles so that admin accounts may be managed, allowing for store registering, user management and so forth.
- TODO: add further missing enhancements.
