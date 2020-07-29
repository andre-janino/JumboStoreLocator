<p align="center">
  <img src="images/JumboBanner.jpg" title="Jumbo" alt="Jumbo supermarkten"/>
</p>

# Jumbo Store Locator

A private repository for Jumbo coding assessment: Create an application that shows the 5 closest Jumbo stores to a given position.
Powered by Spring Boot + Vue.js.

TODO: improve the readme as the project starts to take shape, adding missing microservices, tech stacks and any other relevant information.

## Index :pushpin:
- [About the project](#about)
- [Design](#design)
- [How to run](#run)
- [Future enhancements](#future)

## About the project <a name="about"></a> :scroll:

This project was developed using _`Spring Boot (2.3.2.RELEASE)`_ for the back-end microservices and _`Vue.js 2.6.11`_ for the front-end components.

The solution is divided into the _`frontend`_ project for the user interface and _`user-service, api-gateway, store-service`_ for the back-end:
- frontend: Vue.js project with a _`login`_ and _`find store`_ page. Communicates directly with the _`api-gateway`_.
- api-gateway: deals with user authentication and redirects requests to _`user-service and store-service`_.
- user-service: customer entity/controller/service/etc with a simple in-memory h2 database.
- store-service: store entity/controller/service/etc with a mongodb database implementation, returns N closest stores to a given location.

Why have microservices for such a small application? Creating small, independent microservices allow us to integrate/replace/maintain functionality in a bigger project with minimal effort. In this particular case, what is really being asked is that a store locator API is implemented, but as I have no access to Jumbo's API Gateway, front-end, user/customer service, etc, it makes sense to create my own and keep them separate, simply acting as palceholders for an MVP demonstration.

Several libraries were used to fulfill the needed business logics; the main ones are listed in the following sub-sections.

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

## Design <a name="design"></a> :memo:

When designing a new feature, it makes sense to understand what is already in place, both in your business and elsewhere. For Jumbo, such feature is already available, as seen here: https://www.jumbo.com/winkels (powered by google maps). Upon loading, it displays all existing stores and allows several different filters: 
- Open now.
- Open on Sundays.
- Open until 19/20/21/22h.
- New store.
- Pickup-point.
- Store.
- Store + pickup-point.

It has a wide range of filters, but it lacks what is being asked here: to bring only the 5 nearest stores. Therefore, it makes sense to implement that filter in a way that is easy to incorporate it among the others. From what I've observed, stores are already ordered by distance on the existing page.

On the details of each found store, the page also allows the user to navigate to 2 other pages:
- Store details (e.g. https://www.jumbo.com/winkel/jumbo-heinkenszand-stenevate)
- Reserve pick-up time (e.g. https://www.jumbo.com/INTERSHOP/web/WFS/Jumbo-Grocery-Site/nl_NL/-/EUR/ViewDeliveryOptions-Start?storeUUID=8E0KYx4XCQQAAAFoMTtWMVlg)

The store information I can work with is provided in a json format, and contains the following fields: "city", "postalCode", "street", "street2", "street3", "addressName", "uuid", "longitude", "latitude", "complexNumber", "showWarningMessage", "todayOpen", "locationType", "collectionPoint", "sapStoreID" and "todayClose". 

It seems to have most of the fields necessary to replicate a similar list to the one provided at Jumbo's website, except by the open/close times on other days of the week. The uuid field would also allow the "reserve pick-up time" button to be implemented. However, the data needed for the store details button does not seem to be available. 

I also compared Jumbo's store finder with Walmart's. The UI is very similar, having a search-box within the map that displays an expandable list of stores, the main difference being that Walmart's does not have such a wide range of filters and does not display any store until the user provides a location (probably due to the ammount of stores).

Therefore, a simpler but functional version of the existing Jumbo store finder will be created here.

<p align="center">
  <img src="images/Jumbo Store Locator.png" title="Proposed design" alt="Proposed design"/>
</p>

## How to run <a name="run"></a> :wrench:

TODO: add run instructions, needed software (e.g. Java 8, Maven), etc.

## Future enhancements <a name="future"></a> :clock130:

- Migrate the h2 database into another database. PostgreeSQL and MySQL are valid options in terms of SQL, while MongoDB and DynamoDB are appealing ones in terms of NoSQL.
- Add user roles so that admin accounts may be managed, allowing for store registering, user management and so forth.
- Add new functionalities (link to store details, other filters, etc.)