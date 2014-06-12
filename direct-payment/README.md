# SPiD Java direct payment example

A bare-minimum Java implementation of direct payment with SPiD.

## Usage

1. **Fill in the configuration**

   ```sh
   cd direct-payment/src/main/resources/
   cp config.properties.sample config.properties
   vi config.properties
   ```

   Replace `clientID` and `clientSecret` with your own credentials.

2. **Start the server**

   ```sh
   mvn package && java -jar target/spid-direct-payment-example-1.0.0.jar
   ```

You'll find the example at http://localhost:8080/
