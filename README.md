# ean-product-consult

## 📖 Introduction

RESTful API for consult data of products by its ENS.

## 📖 Endpoints

### Events
* [GET] ```/api/v1/products```: Returns basic data of the product, supplier and destination.

## 🚀  Deployment

### Requirements
- Java 19.0.2 or higher, you can download it from the following link (https://www.oracle.com/java/technologies/javase/jdk19-archive-downloads.html)
- Maven 3.9 or higher (https://maven.apache.org/download.cgi)

### Start up

Set the following environment variables:

- *SPRING_DATASOURCE_URL*= Path to DB

- *SPRING_DATASOURCE_USERNAME*=Username of DB

- *SPRING_DATASOURCE_PASSWORD*=Password of the user DB

To set these environment variables on your system, you can create a .env file in the root of the project and then export the variables to your system. If your OS is Linux or MacOS, you can run the following command: ```bash
export $(cat .env | xargs)```. In case your OS is Windows, you can run the following one: ```ln -s .env env.properties```.

Finally, the API REST can be deployed by running ```mvn spring-boot:run```