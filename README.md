# Monitor App Interview Test for GOLO

### Index
1. The Rules
2. The Problem
3. Quiz Requirements
4. My Notes
5. How to Use

### 1. The Rules
-The test is done by you and only you, the applicant. You have to use the Java programing language.

-Once you receive the test, you have 72 hours to send it back to us.

-You’ll need to give us access to a GitHub repository where the code can be viewed, with easy instructions to launch it.

-Of course, we want you to make the code as robust as possible.


### 2. The Problem
We’re asking you to build a specific monitoring application.

To add merchants to the GOLO app, we use the Paysafe Developer API to register and validate merchants. Here’s the link: https://developer.paysafe.com/en/partners/accounts/api.

In order to inform us of it’s current availability, the server API allows for external monitoring of its status(https://developer.paysafe.com/en/turnkey/accounts/api/#/reference/0/verify-that-the-service-is-accessible/verify-that-the-service-is-accessible ). Your task is to create an application that will monitor the status of the server and report on it when requested. You do not need to create an account, the monitoring endpoint does not need authentication.

### 3. Quiz Requirements
The app should have REST endpoint(s) that allows to start or stop the monitoring of the Paysafe Developer API. In its payload, this endpoint(s) should accept at least these two values:
-	An interval field that tells your server what interval should be used between each request to the server(start).
-	A url field for the url value of the server to monitor. This value should be available when you start and stop the monitoring.

The app should have another REST endpoint which, when requested, gives an overview to the user of the time periods when the server was up and when it was down since the last time it was started.

The resulting monitoring data doesn’t have to be persistent.

### 4. My Notes
It is unclear to me what constitutes the server being up or down. I could have gone with pings in order to lighten the network load, but since the goal is to monitor an API I used the response code 200 for a URL as a proxy for the server being up.

### 5. How to Use

The streaming of data isn't working well on Heroku so I suggest running it locally for the full experience. That being said here is an example of the app: https://monitor-url.herokuapp.com/  

To run locally, have the local port 9000 free for use.

Run with docker:
`docker run -p 9000:9000 veryan4/monitor-url`

Otherwise, make sure you have the necessary dependencies and use:
`./mvnw spring-boot:run`
