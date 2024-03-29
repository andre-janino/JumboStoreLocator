<p align="center">
  <img src="_resources/JumboBanner.jpg" title="Jumbo" alt="Jumbo supermarkten"/>
</p>

# Jumbo Store Locator

A private repository for Jumbo coding assessment: "Create an application that shows the 5 closest Jumbo stores to a given position".
Powered by Spring Boot + Vue.js. 

Current release: [v1.1](https://github.com/andre-janino/JumboStoreLocator/releases/tag/v1.1)

## Index :pushpin:
- [Design](#design)
- [Architecture](#about)
- [How to run](#run)
- [Testing](#testing)
- [Future enhancements](#future)

## Design <a name="design"></a> :memo:

#### State of the art

A store finder application is already available at Jumbo's website, as seen here: https://www.jumbo.com/winkels. Upon loading, it displays all existing stores and allows several different filters:  _`Open now`_, _`Open on Sundays`_, _`Open until 20:00`_, _`New store`_, _`Pickup-point`_, _`Store`_, and _`Store + pickup-point`_, as well as _`store type`_ filters*.

**Note: Either I didn't understand how the _`store type`_ filter works, or there's a bug on it. When I filter by "Pick-up point", I still get stores like [Jumbo Den Burg (Texel) Vogelenzang](https://www.jumbo.com/winkel/jumbo-den-burg-texel-vogelenzang?redirect=true), which (as far as I can see) is not a pick-up point.* 

On the details pane of each store, the user is allowed the following actions:
- Favoriting at store.
- [Visualize store details](https://www.jumbo.com/winkel/jumbo-heinkenszand-stenevate).
- [Reserve pick-up time](https://www.jumbo.com/INTERSHOP/web/WFS/Jumbo-Grocery-Site/nl_NL/-/EUR/ViewDeliveryOptions-Start?storeUUID=8E0KYx4XCQQAAAFoMTtWMVlg) (if applicable).

The store information we can work with is provided in a json format, and contains the following fields: _`city`_, _`postalCode`_, _`street`_, _`street2`_, _`street3`_, _`addressName`_, _`uuid`_, _`longitude`_, _`latitude`_, _`complexNumber`_, _`showWarningMessage`_, _`todayOpen`_, _`locationType`_, _`collectionPoint`_, _`sapStoreID`_ and _`todayClose`_. 

It seems to have most of the fields necessary to replicate a similar list to the one provided at Jumbo's website, except by the open/close times on other days of the week and filtering by new stores. The _`uuid`_ field allows for the _`reserve pick-up time`_ button to be implemented, while the _`store details button`_ seems to requires information we not have; for example: when the same _`addressName`_ field is used twice (e.g. Jumbo Hulst Stationsplein *22H* and *30*), the _`street2`_ field is embedded on the url: https://www.jumbo.com/winkel/jumbo-hulst-stationsplein-30. This is problematic with the provided test data because *Jumbo Hulst Stationsplein 22H* is missing, so there is no good way to know if the `_street2_` field should be appended or not on the url.

I also compared Jumbo's store finder with the one provided by Walmart. The UI is very similar, having a search-box within the map that displays an expandable list of stores; the main difference being that Walmart's store finder does not have such a wide range of filters and does not display any store until the user provides a location (probably due to the amount of stores). The UI also has different quirks in terms of how the details of a shop are displayed.

#### Proposed design

In terms of functionality, the original system offers much more than the scope of this project. The filters are very specific and seem to convey that it was something that was built over time, by listening to customer feedback. As for the UI: it makes sense to keep the search bar within the map, as mobile users do not have much screen real-state and need to have a compact and responsive UI (although, on a mobile, the search pane should ideally fully replace the map, depending on the screen size). Therefore, a simpler but functional version of the existing Jumbo store finder was designed for this project.

<p align="center">
  <img src="_resources/Jumbo Store Locator.png" title="Proposed design" alt="Proposed design"/>
</p>

Geneal premisses:
- Users are able to log-in through email/password or with a guest user (for this MVP, no registration page is provided). 
- After logging in, the user is presented with a map centered at the Netherlands (52.370216,4.895168), displaying all available stores on the map. 
- The map displays a list of found stores (unfiltered by default), and contains a search panel and allows filtering. 
- The search panel does not display any stores until the user makes his first interaction (filter, change tabs or click on a marker). 
- When the user clicks on a store on the panel, the map pans and zooms to the location, and the marker icon changes to help the user identify the selected store.
- When the user clicks on a marker, the search panel scrolls to the selected store to display its detailed information.
- A selected store has a different marker to make it easier to see the user selection.
- Users are able to favorite a store.

The following filters are supported:
- All stores: query all stores, ordered by distance of the provided address.
- Nearby stores: query the 5 nearest stores by default (but adjustable to other amounts), ordered by distance of the provided address.
- Favorite stores*: query the favorited stores, ordered by distance of the provided address.
- Store types: in combination to the aforementioned filters, the user is able to specify which store types should be fetched: _`store`_, _`pick-up point`_ and _`drive-through of walk-in pick-up point`_ (which I renamed to simply drive-through for brevity; maybe it would be a good idea to add a tooltip to add more info to it?).

**Note: the favorite store query is only enabled if the user is logged in; guest users can't favorite a store.*

The "collapsed" details of a store displays the following info:
- Title: _`addressName`_.
- Line 1: _`street`_ +  _`street2`_.
- Line 2: _`city`_.
- Line 3: Open today: _`todayOpen`_ - _`todayClose`_.
- Side info: store type indicator (icon) with a link to pick-up reservation (if applicable)
- Side info: distance with a google maps link to display the route.

When expanded, additional info is displayed:
- Hardcoded open/close times (since we have no data for it).
- Warning about pick-up prices (if applicable).
- Side info: favorite/unfavorite the store (for non-guest users).

The "nearest stores" logic is managed by the back-end, while the front-end concerns itself with translating addresses into geolocation (google maps API).

Disclaimer: several icons, styling and texts were obtained from Jumbo's website and are not to be utilized outside of this private repository.

## Architecture <a name="about"></a> :scroll:

This project was developed using _`Spring Boot (2.3.2.RELEASE)`_ for the back-end microservices and _`Vue.js 2.6.11`_ for the front-end components. The back-end modules were implemented with a microservice architecture in mind, distributed in the following projects:
- _`finder-client`_: Vue.js project with a _`login`_ form and _`find store`_ page. Communicates directly with the _`api-gateway`_.
- _`discovery-service`_: eureka discovery service that all other services subscribe to.
- _`api-gateway`_: deals with token validation and redirects requests to other microservices.
- _`auth-service`_: validates user credentials and issue tokens.
- _`user-service`_: customer entity/controller/service/etc with a simple in-memory h2 database, used in conjunction with auth-service.
- _`store-service`_: store entity/controller/service/etc with mongodb (atlas), returns N closest stores (by type) to a given location.
- _`config-service`_: centralized configuration service that store properties used by other microservices.

<p align="center">
  <img src="_resources/Architecture.png" title="System architecture" alt="Architecture"/>
</p>

It is important to note that, in a production environment, each of these projects would be located in separate repositories, allowing teams to work on modules independently and isolating all the moving parts of the product's architecture. However, they were kept together for this project to make it easier for a reviewer to analyze everything what was done on this small project.

Why have microservices for such a small application? Creating small, independent microservices allow us to integrate/replace/maintain functionality in a bigger project with minimal effort. In this particular case, what is really being asked is that a store locator API is implemented, but as I have no access to Jumbo's API Gateway, front-end, user/customer service, etc., it makes sense to create my own and keep them separate, simply acting as placeholders for an MVP demonstration.

Several libraries were used to fulfill the needed business logics; the main ones are listed below:

#### Netflix Zuul

- _`Spring Zuul`_ is employed as an _`API Gateway`_. It uses Eureka to discover microservices' instances and redirects requests accordingly.
- _`JWT tokens`_ are validated by this module, preventing that requests delve much deeper into the architecture if not necessary. 
- _`Authorization`_ rules are put in place to prevent non-admins from accessing restriced APIs. Guest users are restricted even further.

#### Netflix Eureka

- _`Service Discovery`_ is performed by _`Netflix Eureka`_. This allows microservices to communicate with one-another through Feign Client (which has not yet been used on this project, as RabbitMQ was employed instead). 
- Even if _`Feign Client`_ is not used, it is useful to enable load balancing on Zuul. The latter is still not applied, as the application was only tested locally with dev settings; however, it is important to leave such things in place when the need to setup a production environment with multiple instances arises. Why not outright removing it for now, then? Because as any project that is worth something, you *have* to think of an eventual production environment when things are ready to ship, and having it already setup is a good thing.

#### H2 Database Engine

- _`H2`_ was employed as the SQL database for the _`user-service`_ to store login information and on the _`config-service`_ to store application properties.
- Although it has little place in production envrionments, it is a great tool to present an MVP as it is very easy to migrate to another solution (e.g. MySQL).

#### MongoDB Atlas

- _`MongoDB`_ is a NoSQL database that is very suitable for restful applications, as data is already store in a json format. _`MongoDB Atlas`_ is a cloud solution that hosts _`MongoDB`_ databases, being not only suitable for quickly testing an application (little to no setup time involved) and escalating well for production environments.
- In this particular application, _`MongoDB`_ is used by _`store-service`_ to hold the store data. 
- While google maps platform documentation presents a very fitting [SQL solution](https://developers.google.com/maps/solutions/store-locator/clothing-store-locator) to query the distance between an address and the stored locations, it is also feasible to implement the same functionality with [GeoJSON](https://geojson.org/). The needed _`2dsphere`_ index was created directly on the database, but it should also have been created on a configuration class.
- As GeoJSON is being used, the fields "latitude" and "longitude" had to be put into a different structure:
  ```json
  "position": {
      "type": "Point",
      "coordinates": [125.6, 10.1]
   }
  ```
  In order to achieve that, I've performed the following search and replace operations on _`stores.json`_, before inserting it at the stores MongoDB document:
  - "longitude":" => "position": { "type": "Point", "coordinates": [
  - ","latitude":" => ,
  - ","complexNumber" => ]},"complexNumber"
  
  Alternatively, a conversion service could have been created to import "legacy" data, convert it to the new model and save it all on the database, but it felt unecessary due to the one-shot nature of this process.
- Indexes were created for the _`position`_ and _`locationType`_ attributes for the _`stores`_ collection, and on the _`userName`_ attribute for the _`favorites`_ collection.

#### RabbitMQ

- _`RabbitMQ`_ is is usually employed for asynchronous message exchange between microservices. However, in this project, is it used to establish synchronous RPC communication between _`user-service`_ and _`auth-service`_.
- Using message-based RPC, services have no direct dependencies on other services; a service only depends on a response to a message request it makes to that queue. What it does is send a string to a queue (the start of asynchronous communication) and waiting for a message in a different queue to send it back as an HTTP response.
- Generally speaking, it would be better to employ CQRS + event sourcing. This way, user-service would issue events whenever a user is registered/altered/deleted, and auth-service would capture these events and store a relation of user emails/passwords/roles. However, this is a fairly small application, and it seemed overkill to implement such pattern.

#### Spring Cloud Config

- _`Spring Cloud Config`_ implements the third factor in the Twelve-Factor App Methodology, which ensures separation between code and configuration.
- As there is little concern about dev/prod environments on this project, only a single profile was set (default). Not much was done also in terms of security (encryption/authentication) for the stored properties, which is mandatory in a productioin environment.

#### Google Maps API

- _`Maps JavaScript API`_ enables the use of google maps on an html/javascript front-end.
- _`Geocoding API`_ is used to convert addresses (e.g. Netherlands) into geographic coordinates.
- _`Places API`_ helps with address auto-complete.

#### Sleuth

- _`Sleuth`_ is a logging solution that allows tracing actions across multiple services through unique trace IDs (done automatically), which makes it a perfect solution for a microservice architecture. 
- Logs can be visualized in a dashboard through [zipkin](https://zipkin.io/), if so desired.

#### Other honorable mentions

- _`Circuit breaker`_: I initially planned to employ _`Netflix Hystrix`_ to implement _`Circuit Breaker`_ design pattern, that is, if a microservice is unavailable, a _`fallback`_ method is called to prevent a systematic failure. My first idea was to do that within _`auth-service`_, but automatically logging the user in as a guest if _`user-service`_ is down and unble to provide user info. However, it feels a bit cluncky from the user perspective, so I decided against it in the end. 
- _`Caching`_: Returning over 500 stores takes a while, and users can be lose interest really quickly if the page is not responsive. With that in mind, store listing (both filtered and unfiltered) is cached with _`Cache-Control`_ _`max-age=3600`_ (1 hour), which prevents the user from reaching the server for the same queries, over and over. One hour seems like a reasonable cache duration (perhaps it could even be expanded), unless store information changes more often than that. That being said, favorited stores are not cached due to its dynamic nature.
- _`Zuul`_, by default, ignores a few headers from the services it routes to, _`cache-control`_ being one of them. To solve this issue, it is necessary to set zuul.ignoreHeaders property to _`"X-Content-Type-Options,Strict-Transport-Security,X-Frame-Options,X-XSS-Protection"`_, leaving _`cache-control`_ out of it.
- _`Foreign keys`_: Using _`NoSQL`_ for the _`store-service`_ makes the "favorite store" functionality a little bit harder to implement; in an SQL database, a simple n-to-n (users with stores) relationship should do the trick, but it cannot be accomplished in the same way for _`MongoDB`_. With that in mind, to avoid having complex aggregates, the _`favorites`_ collection is loaded when the user opens the page; changes are notified to the server and a local copy is kept on the client-side. Moreover, when the user wants to see a list of favorite stores, the IDs are passed as parameters and used to filter the _`stores collection`_. However, having these data objects completely separate have its own benefits, as we can cache lists of stores without worrying about providing the user with an outdated outlook on favorite stores.

## How to run <a name="run"></a> :wrench:

This project has several pre-requisites and can either be built/ruin with Docker or manually on the command line, as described on the two following sub-sections.

Having it all up and running, you can [run and test](#manual) the application by typing http://localhost:8081.

#### The easy way :mortar_board:

If you have [Docker](https://www.docker.com/products/docker-desktop) installed, you can start the project by either:
1. Downloading [the prebuilt release](https://github.com/andre-janino/JumboStoreLocator/releases/tag/v1.1), extracting it and running _`docker-compose up -d`_.
2. Running _`docker-compose up -d`_ on the root folder of this git project. 

If you go with alternative (2), the start-up may be a bit slow on the first run due to maven artifacts download, but option (1) also has its own drawbacks due to the release.rar file size, as it contains all the jars and node_modules.

#### The hard way :hammer:

If you don't want to use docker, make sure you have:
- [Java 8 SDK](https://www.oracle.com/br/java/technologies/javase/javase-jdk8-downloads.html) installed and properly configured on JAVA_HOME.
- [RabbitMQ](https://www.rabbitmq.com/download.html) and [Erlang](https://www.rabbitmq.com/which-erlang.html).
- [npm](https://www.npmjs.com/get-npm) for the Vue.js front-end application.
- [Maven](http://maven.apache.org/download.cgi) installed and properly configured on your PATH variable.

Having the pre-requisites fulfilled, you'll have to:
- Get _`RabbitMQ`_ up and running by executing _`rabbitmq-server`_ on its installation folder (it usually already self-starts after installing).
- Execute _`mvn package -Dmaven.test.skip=true`_ on each of the microservices to build the jar, followed by _`java -jar`_ to start the spring application.
- Execute _`npm i`_ on _`finder-client`_ to download all the modules, followed by _`npm run serve`_ to start the Vue.js application.

## Testing <a name="testing"></a> :beetle:

This project was tested in three ways:
- Controllers, Services and Repositories were tested with JUnit.
- API calls were tested with [Postman](https://www.postman.com/).
- And last but not least, the UI was tested manually.

#### JUnit

_`JUnit`_ was used alongside _`Mockito`_ for the _`Services`_, _`Controllers`_ and _`Repositories`_ unit/integration tests. _`MockMvc`_ was used to test _`Controllers`_ response. _`TestEntityManager`_ was used to simulate _`SQL repositories`_.

#### Postman

This tool facilitates API testing by allowing the creation of get/post/put/delete/etc requests collections, which helps back-end developers a lot when a frontend is not yet available.

<p align="center">
  <img src="_resources/Postman.png" title="Postman API tests" alt="Postman API tests"/>
</p>

#### Manual tests <a name="manual"></a>

With the application [up and running](#run), head towards http://localhost:8081 on your favorite browser* and you will be greeted with the store locator login page.

***Note about browser support: It seems that the webp icons I borrowed from Jumbo are not playing nicely with Edge, as they are not loaded at all (the same issue is present in Jumbo's store search website). Searching a little bit, there seems to be a few workarounds that could be tried, so that goes into the TODO list.*

<p align="center">
  <img src="_resources/Test-Login.png" title="Login page" alt="Login page"/>
</p>

You can choose to login with your user/pass, or to proceed as a guest (in which case, favoriting a store won't be allowed, but everything else works the same). The available users are:

- andre.janino@gmail.com / Password1
- marijn.deromph@jumbo.com / Password1
- tamara.duric@jumbo.com / Password1
- gustavo.henriquesmartins@jumbo.com / Password1

After logging in, you should be greeted by a map filled with all Jumbo stores with the _`All Stores`_ option pre-selected, as shown below:

<p align="center">
  <img src="_resources/Test-MainPage.png" title="Main page" alt="Main page"/>
</p>

Stores can be filtered by type: _`Supermarkten/Pickup point/Drive through`_. If an address is provided, stores are ordered by distance of the given location:

<p align="center">
  <img src="_resources/Test-Filters-AddressSearch.png" title="Searching for an address" alt="Searching"/>
</p>

The feature that was requested on this coding assignment is available on the _`Nearby Stores`_ tab. It has the same functionalities as _`All Stores`_, but it limits the amount of results by _`5, 10, 25 or 50`_, depending on the user selection:

<p align="center">
  <img src="_resources/Test-Filters-Nearest.png" title="Nearest stores" alt="Nearest stores"/>
</p>

It is interesting to note that the _`Nearby stores`_ is, at this point, a little bit redundant; it functions exactly as the "All stores" tab does, but it offers a way to limit the number of results. I've only kept both to showcase different ways the search panel can be setup. Eventually, if more filters are added, it would be a good idea to change the design, either moving this option to the header (and eliminating redundant tabs) or by creating a filter button that displays advanced options.

As for the other functionalities: upon clicking on a store (either on a search panel or on a marker), the search panel focus on and expands the selected store, displaying its details. The open hours fields (over the week) are hardcoded, but the favorite toggle button works and enables favorited stores to be seen on the third tab, under _`Favorite Stores`_:  

<p align="center">
  <img src="_resources/Test-Filters-Nearest-25.png" title="Nearest stores" alt="Nearest stores"/>
</p> 

Clicking on the _`directions`_ and _`pick-up`_ buttons opens a new tab, displaying a google maps page and a Jumbo's pick-up reservation page, respectively; both are dynamic and respond correctly to the provided data. When pick-up reservation is not available, the former simply displays a "supermarket" icon, so that the user knows the location type.

## Future enhancements <a name="future"></a> :clock130:

- Migrate the _`h2 database`_ into _`MySQL/PostgreeSQL/etc`_, as h2 is only suitable for simple tests.
- Add admin-specific pages for managing users and stores. When this is done, also implement _`event-sourcing`_ to synchronize services which rely on this data (eliminate querying across services as much as possible and allowing better decoupling).
- Add security for _`Spring Cloud Config`_, and move the config to a github repo (JDBC-based Spring Cloud Config is interesting, but harder to configure).
- Improve logging by using the _`ELK Stack`_.
- Improve the search panel responsiveness for mobile (need to add some @media breakpoint behaviors to displaty it differently for phones).
- Add _`selenium`_ tests (that is, don't rely on JUnit and Postman alone).
- Fix the bug with webp icons on Edge.

## Closing notes (Hallo Jumbo!)

It has been a fun journey! When I started, I knew nothing about how to query the closest geographical points (nor that it could/should be done in a database), so it was very interesting to learn the trade-offs of each approach and, ultimately, implement a working solution that perfoms well and feels nice to use. 

Every module was developed from scratch, and although I did not get to apply all the patterns I wanted (looking at you, _`Circuit breaker`_!), the system is well decoupled, documented and organized. While it is not a one-to-one representation of what my production code looks like (e.g. the lack of security on _`Spring Cloud Config`_ and an overly simple authentication method), it demonstrates how I think about a problem, design a solution, communicate my thoughts, implement and document it.

The system is not perfect and could undergo several performance improvements (e.g. filtering by store-type could be fully done on the front-end with some tweaking), as well as UI improvements (the search panel does not fit a phone screen with the current design), but I'm mostly happy with how it turned out given the ammount of time I had to implement it.

Lastly, I would like to thank everyone from Jumbo for this opportunity! If you have any questions (or improvement suggestions), feel free to contact me.
