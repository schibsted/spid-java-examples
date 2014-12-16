# SPiD Java SSO example

A bare-minimum Java implementation of SSO with SPiD.

## Usage

1. **Fill in the configuration**

   ```sh
   cd sso/src/main/resources/
   cp config.properties.sample config.properties
   vi config.properties
   ```

   Replace `clientID` and `clientSecret` with your own credentials.

2. **Start the server**

   ```sh
   mvn package && java -jar target/spid-sso-example-1.0.0.jar
   ```

You'll find the example at http://localhost:8080/


## New Flow URLs
Version => 1.4 only supports new flow URLs, which is generated with the method getFlowURL(name, redirectURL). Current valid flow names are ``login`` and ``checkout``
