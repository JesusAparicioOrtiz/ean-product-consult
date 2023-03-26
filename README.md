# ean-product-consult

## 📖 Introduction

RESTful API for consult data of products by its ENS.

## 📌 Endpoints

### Product
* [GET] ```/api/v1/product/{ean}```: Returns basic data of the product, provider and destination by ean.
* [POST] ```/api/v1/product```: Creates a product based on the JSON payloads with attributes: id, name, price and description.
* [PUT] ```/api/v1/product```: Updates a product based on the JSON payloads with attributes: id, name, price and description.
* [DELETE] ```/api/v1/product/{ean}```: Deletes a product by ean.

### Provider
* [GET] ```/api/v1/provider/{id}```: Returns basic data of the provider by id.
* [POST] ```/api/v1/provider```: Creates a provider based on the JSON payloads with attributes: id and name.
* [PUT] ```/api/v1/provider```: Updates a provider based on the JSON payloads with attributes: id and name.
* [DELETE] ```/api/v1/provider/{id}```: Deletes a provider by id.

### Destination
* [GET] ```/api/v1/destination/{id}```: Returns basic data of the destination by id.
* [POST] ```/api/v1/destination```: Creates a destination based on the JSON payloads with attributes: id.
* [PUT] ```/api/v1/destination```: Updates a destination based on the JSON payloads with attributes: id.
* [DELETE] ```/api/v1/destination/{id}```: Deletes a destination by id.


## 🤖 Requirements
- Java 19.0.2 or higher, you can download it from the following link (https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
- Maven 3.9 or higher (https://maven.apache.org/download.cgi)

## ⚙ Environment

Set the following environment variables:

- *SPRING_DATASOURCE_URL*= Path to DB

- *SPRING_DATASOURCE_USERNAME*=Username of DB

- *SPRING_DATASOURCE_PASSWORD*=Password of the user DB

## 🚀 Start up

To set these environment variables on your system, you can create a .env file in the root of the project and then export the variables to your system. If your OS is Linux or MacOS, you can run the following command: ```bash
export $(cat .env | xargs)```. In case your OS is Windows, you can run the following one: ```Get-Content .env | ForEach-Object { [Environment]::SetEnvironmentVariable($_.Split('=')[0], $_.Split('=')[1], 'User') }```.

Before starting the project, it is necessary to make sure that the Spring Datasource URL path indicated in the .env file exists.

Finally, the API REST can be deployed by running ```mvn spring-boot:run```

## 🧪 Testing

Controller tests have been coded with Junit mocking services with Mockito. To execute tests run the following line: ``` mvn tests ```