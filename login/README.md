# SPiD Java SSO example

A bare-minimum Java implementation of SSO with SPiD.

## Usage

1. **Fill in the configuration**

   ```sh
   cd login/src/main/resources/
   cp config.properties.sample config.properties
   vim config.properties
   ```

   Replace `clientID` and `clientSecret` with your own credentials.

2. **Start the server**

   ```sh
   mvn package && java -jar target/spid-login-example-1.0.0.jar
   ```

You'll find the example at http://localhost:8080/
