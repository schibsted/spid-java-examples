# Getting started with the SPiD Java SDK

The following is a minimal example of using the Java API client. It checks the status of the email
supplied as the third argument.

**NB!** To run the example, you need to know your client ID and API secret. The ```spidBaseUrl``` points to the environment where your credentials originate from, set to stage/pre by default.

## Usage

```sh
mvn install -q exec:java -Dexec.mainClass="no.spid.examples.GettingStarted" -Dexec.args="<client-id> <secret> <email>" -e
```

Replace pointy bracketed items with your credentials.

This will check the status of the email in SPiD for the environment chosen and print the result to standard output.
