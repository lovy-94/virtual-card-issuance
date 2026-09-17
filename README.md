# Virtual Card Issuance Platform

A backend service of virtual cards 
— supporting card issuance, top-ups, spending, and transaction tracking — built with Java and Spring Boot, designed to remain correct under concurrent access and to scale to a large number of users.

url: http://localhost:8080

DB : http://localhost:8080/h2-console

## API Endpoints

All endpoints follow REST conventions and return standard HTTP status codes with a consistent JSON error body for failures.

Method | Endpoint
POST | /api/cards - create a new card
GET  | /api/cards/{cardId} - retrieve card details  
POST | /api/cards/{cardId}/topup - top up card balance 
POST | /api/cards/{cardId}/spend - spend from card balance 
GET  | /api/cards/{cardId}/transactions - retrieve card transactions


Business Constraints
- A card’s balance can never go below zero has been achieved 
- All financial operations must be idempotent added an idempotency key to the request header
- Spending and top-ups should only be possible on active cards validatio check applied 
- The system must behave correctly when accessed by many users at the same time concurrency added
- Ensure the application can scale to support thousands of users. this needs more time to implement
- Clean seperation of concerns between the API layer, business logic, and persistence layer has been achieved
- Tried to achieve modularization as good practice, added junits for all scenarios and logging mechanism.

Future ENhancements:
- Can have Asynchronous processing or kafka for handling high volume of transactions.
- Can be evolved into a microservices architecture in later point of time.
- Can have a more error handling and logging mechanism.
- Can be created as more modular and loosely coupled/
- Can have security + rate limitng
- Can have better observability and monitoring of the application.
