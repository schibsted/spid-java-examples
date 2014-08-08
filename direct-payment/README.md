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

2. **Run the recurring payments batch process**

   ```sh
   mvn package && java -jar target/spid-direct-payment-example-1.0.0.jar
   mvn install -q exec:java -Dexec.mainClass="no.spid.examples.RecurringPaymentProcessor" -e
   ```
