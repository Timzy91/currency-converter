# currency-converter
This application supports the following two functionalities:
1. Store a Purchase Transaction (Always in US Dollars)
2. Retrieve a Purchase Transaction in a Specified Country’s currency

**Note:** 
1. Exchange rate used from https://fiscaldata.treasury.gov/datasets/treasury-reporting-rates-exchange/treasury-reporting-rates-of-exchange
2. The Country's currency values must match the values from Country Currency field when retrieving a transaction
3. The exchange rate closest before the transaction date is considered for conversion


# Steps to get started
1. Clone this git repo
2. On command line, navigate to repo directory and run the below command to download dependencies and setup the application
   `./mvnw clean package`
3. To start the application run the below command
    `java -jar currencies-0.0.1-SNAPSHOT.jar`
4. To use the application API's. Open your browser and navigate to the below URL
   [http://localhost:8080/swagger-ui/index.html](swagger-UI)