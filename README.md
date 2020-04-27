# KRY code assignment

One of our developers built a simple service poller.
The service consists of a backend service written in Vert.x (https://vertx.io/) that keeps a list of services (defined by a URL), and periodically does a HTTP GET to each and saves the response ("OK" or "FAIL").

Unfortunately, the original developer din't finish the job, and it's now up to you to complete the thing.
Some of the issues are critical, and absolutely need to be fixed for this assignment to be considered complete.
There is also a wishlist of features in two separate tracks - if you have time left, please choose *one* of the tracks and complete as many of those issues as you can.

Critical issues (required to complete the assignment):

- Whenever the server is restarted, any added services disappear
- There's no way to delete individual services
- We want to be able to name services and remember when they were added
- The HTTP poller is not implemented

Frontend/Web track:
- We want full create/update/delete functionality for services
- The results from the poller are not automatically shown to the user (you have to reload the page to see results)
- We want to have informative and nice looking animations on add/remove services

Backend track
- Simultaneous writes sometimes causes strange behavior
- Protect the poller from misbehaving services (for example answering really slowly)
- Service URL's are not validated in any way ("sdgf" is probably not a valid service)
- A user (with a different cookie/local storage) should not see the services added by another user

Spend maximum four hours working on this assignment - make sure to finish the issues you start.

Put the code in a git repo on GitHub and send us the link (niklas.holmqvist@kry.se) when you are done.

Good luck!

# Building
We recommend using IntelliJ as it's what we use day to day at the KRY office.
In intelliJ, choose
```
New -> New from existing sources -> Import project from external model -> Gradle -> select "use gradle wrapper configuration"
```

You can also run gradle directly from the command line:
```
./gradlew clean run
```

## Solution - Saurabh Seth

This is my first exposure to vert.x so could not work on all frontend and backend tasks within 4 hours. Refer below implementation details:

1. BackgroundPoller has been implemented
2. To setup DB, DBMigration main class is used
3. Project folder already has poller.db file which has few services
4. ServicePollerRepository is added to query opeartions
5. Few test cases are added in test directory
6. html and js file is updated to display services, add, delete
7. refer few examples below


### Tasks Done


- Whenever the server is restarted, any added services disappear - Done
- There's no way to delete individual services - Done
- We want to be able to name services and remember when they were added - Done
- The HTTP poller is not implemented - Done

- We want full create/update/delete functionality for services -
    
    __Works withs save button. Future scope to improve update funcionality may be by having inline update__
- The results from the poller are not automatically shown to the user (you have to reload the page to see results)
    
    __Works with interval which loads page every 5 sec.This can be improved either using web sockets or SSE__

- Service URL's are not validated in any way ("sdgf" is probably not a valid service)

## Examples

1. Initial 
KRY status poller

Url	                Name	 Date	     Status

http://hello12.com		    2020-04-26	 FAIL	Delete

http://hello123.com		    2020-04-26	 OK	    Delete

http://hello.com	hello	2020-04-26	 OK	    Delete

2. Enter url and click Save button to create new service
3. Click delete button to delete a service
4. Name can be added in name field
5. Service can be updated using existing url and click save button
6. Entering an invalid url will prompt an alert window

## How to run

You can also run gradle directly from the command line:
```
./gradlew clean run
```

Access in browser:
```
http://localhost:8080
```
