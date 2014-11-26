# Getting started with the SPiD Java SDK

The following is a minimal example of using the Java API client. It fetches the
`/endpoints` endpoint, which returns a description of all available endpoints.

**NB!** To run the example, you need to know your client ID and API secret. And that ```spidBaseUrl``` points to the environment where your credentials originate from.

## Usage

```sh
mvn install -q exec:java -Dexec.mainClass="no.spid.examples.GettingStarted" -Dexec.args="<client-id> <secret>" -e
```

Replace pointy bracketed items with your credentials.

This will print the JSON-decoded response from the server, which shows all
available endpoints along with details on how to interact with them.
