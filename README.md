# Monitor App Interview Test for GOLO

### Index
1. The Problem
2. Quiz Requirements
3. My Notes
4. Monitor App Requirements
5. How to Use

### 1. The Problem
We’re asking you to build a specific monitoring application.

To add merchants to the GOLO app, we use the Paysafe Developer API to register and validate merchants. Here’s the link: https://developer.paysafe.com/en/partners/accounts/api.

In order to inform us of it’s current availability, the server API allows for external monitoring of its status(https://developer.paysafe.com/en/turnkey/accounts/api/#/reference/0/verify-that-the-service-is-accessible/verify-that-the-service-is-accessible ). Your task is to create an application that will monitor the status of the server and report on it when requested. You do not need to create an account, the monitoring endpoint does not need authentication.

### 2. Quiz Requirements
The app should have REST endpoint(s) that allows to start or stop the monitoring of the Paysafe Developer API. In its payload, this endpoint(s) should accept at least these two values:
-	An interval field that tells your server what interval should be used between each request to the server(start).
-	A url field for the url value of the server to monitor. This value should be available when you start and stop the monitoring.

The app should have another REST endpoint which, when requested, gives an overview to the user of the time periods when the server was up and when it was down since the last time it was started.

The resulting monitoring data doesn’t have to be persistent.

### 3. My Notes
It is unclear to me what constitutes the server being up or down. I could've went with pings in order to lighten the network load, but since the goal is to monitor an API, I used the response code 200 for the URL as a proxy for the server being up.

### 4. Monitor App Requirements

I used SDKMAN to control versions, I recommend giving it a look: https://sdkman.io/

Maven: 3.6.2

Java: 1.8.0_232

Spring-boot: 2.2.0

Have the local port 9000 free for use.

### 5. How to Use

You can use:
`./mvnw spring-boot:run`

Then lookup the following in your browser:
`localhost:9000`
