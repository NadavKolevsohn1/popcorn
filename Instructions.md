# Popcorn Palace Movie Ticket Booking System
This document provides the instructions necessary to build, run and test the Popcorn Palace assignment.

## SETUP
1. Clone the repository to your local machine
2. DataBase setup: This assignment is configured by default to connect to a PostgreSQL database.
    Thus, you rather use yourlLocal PostgreSQL.


## PREREQUISITES
1. JDK
2. Apache Maven
3. PostgreSQL Database

## STRUCTURE
The project is organized as follows:
popcorn-palace/
├── src/
│   ├── main/
│   │   ├── java/com/att/tdp/popcorn_palace/    # Application source code
│   │   └── resources/                          # Configuration files
│   └── test/
│        └── java/com/att/tdp/popcorn_palace/      # Test classes
│        └── resources/               
├── pom.xml                                     # Maven build file
└── Instructions.md                             # This instructions file

## BUILD
To build the project, go to the project root directory and run the following command:
* mvn clean install

## RUN
start the application using the following command:
* mvn spring-boot:run

## TEST
To run all tests simply use:
* mvn test
