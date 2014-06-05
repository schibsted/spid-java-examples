# SPiD Java Paylinks example

A Java bare-minimum implementation of Paylinks with SPiD.

## Usage

1. **Fill in the configuration**

   ```sh
   cd paylinks/src/main/resources/
   cp config.properties.sample config.properties
   vim config.properties
   ```

   Replace `clientID` and `clientSecret` with your own credentials.

2. **Start the server**

   ```sh
   mvn package && java -jar target/spid-paylinks-example-1.0.0.jar
   ```

You'll find the example at http://localhost:8081/
