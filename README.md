# 4156 Team Project - ASE Aces

## Building, Running, and Testing a Local Instance

- To run the service: `mvn spring-boot:run`
- To run all tests: `mvn test`
- To see the test coverage: `mvn jacoco:report` and open `target/site/jacoco/index.html` in a
  web browser
- To run the style checker: `mvn checkstyle:check`
- To do static analysis with PMD: `mvn pmd:check`

## A Note to Developers

Thank you for considering using our service! To use it, follow these steps:

1. Set up some form of persistent storage. This is required to store the various IDs returned by the service!
2. Make a call to `/api/clientProfiles/create` to register your client app. Store the `clientId` that is returned.
3. Make calls to `/api/accountProfiles/create` to register some accounts. Store the `accountId`s that are returned.
4. With `clientId` and `accountId`, you can now use the other various endpoints in the API.

Each client app _must_ only have one `clientId`! There is no reason for one client to have multiple.

Note that locations are returned in latitude/longitude form — you'll have to call a geocoding API to turn it into an address.

## Demo URL

[http://34.85.143.68:8080/](http://34.85.143.68:8080/)

---

## Third-Party Code

| Purpose                | Source        | Location in Code                                 |
| ---------------------- | ------------- | ------------------------------------------------ |
| [Haversine formula][1] | [Baeldung][2] | `Location.distance()` and `Location.haversine()` |

[1]: https://en.wikipedia.org/wiki/Haversine_formula
[2]: https://www.baeldung.com/java-find-distance-between-points

---

## Continuous Integration (CI) Reports

The results of the CI loop and the reports mentioned below can be found at:
https://github.com/jason5122/4156-team-project/actions

The workflow automatically

1. Runs all tests (Unit tests, Integration tests, API tests etc)
2. Runs checkstyle and generates a report
3. Runs static analysis and generates a report
4. Runs branch coverage and generates a report

---

## **FoodRequest Endpoints**

### **POST /api/foodRequests/create**

**Expected Input Parameters**:

- `clientId` (Integer): The ID of the client making the request.
- `accountId` (Integer): The ID of the account making the request.
- `listingId` (Integer): The ID of the food listing being requested.
- `quantityRequested` (Integer): The quantity of food being requested.

**Expected Output**:

- A `FoodRequest` object containing details of the newly created request.

This endpoint creates a new food request for the specified account, client, and food listing with
the requested quantity. This is a key step in the process of requesting food from the system.

**Upon Success**:

- **HTTP 201** Status Code is returned along with the created `FoodRequest` in the response body.

**Upon Failure**:

- **HTTP 404** Status Code is returned if any of the specified IDs (clientId, accountId, listingId)
  do not exist.

### **GET /api/foodRequests/get**

**Expected Input Parameters**:

- `requestId` (Integer): The ID of the food request to retrieve.

**Expected Output**:

- A JSON object containing the details of the food request, including:
  - `request_id`: The ID of the food request.
  - `client_id`: The ID of the client making the request.
  - `account_id`: The ID of the account making the request.
  - `listing_id`: The ID of the food listing being requested.
  - `quantity_requested`: The quantity of food requested.
  - `request_time`: The time the request was made.

This endpoint retrieves the details of a food request by its request ID.

**Upon Success**:

- **HTTP 200** Status Code is returned along with the food request details in the response body.

**Upon Failure**:

- **HTTP 404** Status Code is returned if the specified `requestId` does not exist.
- **HTTP 400** Status Code is returned if an error occurs while processing the request.

### **PUT /api/foodRequests/update**

**Expected Input Parameters**:

- `requestId` (Integer): The ID of the food request to update.
- `quantityRequested` (Integer): The new quantity of food requested.

**Expected Output**:

- A message indicating the result of the update operation.

This endpoint updates the quantity of food requested in an existing food request.

**Upon Success**:

- **HTTP 200** Status Code is returned with the message `"Updated Successfully."` in the response
  body.

**Upon Failure**:

- **HTTP 404** Status Code is returned if the specified `requestId` does not exist.

### **Operational Guidelines**

- **Order of API Calls**: The `/create` endpoint should be called before `/get` or `/update` to
  ensure a food request exists.
- **Error Handling**: Proper status codes like `404 Not Found` or `400 Bad Request` will be returned
  for invalid requests or errors.
- **Dependencies**: Ensure valid `clientId`, `accountId`, and `listingId` exist before creating a
  request.

---

## **ClientProfile Endpoints**

### **POST /api/clientProfiles/create**

**Expected Input Parameters**: N/A\
**Expected Output**: A `ClientProfile` object containing the `client_id` and other details.

This endpoint creates a new client profile. Each client is registered with a unique ID, which can be
used in other operations.

**Upon Success**:

- **HTTP 201** Status Code is returned along with the newly created `ClientProfile` in the response
  body.

**Upon Failure**:

- **HTTP 500** Status Code is returned in case of any unexpected errors during profile creation.

### **GET /api/clientProfiles/get**

**Expected Input Parameters**:

- `clientId` (Integer): The ID of the client profile to retrieve.

**Expected Output**: A JSON object containing:

- `client_id`: The ID of the client.

This endpoint retrieves the details of a client profile by the provided client ID.

**Upon Success**:

- **HTTP 200** Status Code is returned along with the client profile details in the response body.

**Upon Failure**:

- **HTTP 404** Status Code is returned if the specified `clientId` does not exist.

### **Operational Guidelines**

- **Order of API Calls**: You should call the `/create` endpoint to create a client profile before
  trying to retrieve it using `/get`.
- **Error Handling**: If a `clientId` does not exist, the service will return a `404 Not Found`
  status code. Ensure that the client ID is valid before making a request to `/get`.
- **Dependencies**: The client profile created by `/create` can be used by other services that
  require a client profile (e.g., creating food requests).

---

## **AccountProfile Endpoints**

### **POST /api/accountProfiles/create**

**Expected Input Parameters**:

- `clientId` (Integer): The ID of the client to associate with the account profile.
- `accountType` (Enum: PROVIDER, RECIPIENT): The type of account being created (e.g., PROVIDER or
  RECIPIENT).
- `phoneNumber` (String): The phone number associated with the account.
- `name` (String): The name of the account holder.

**Expected Output**:

- A JSON object representing the newly created `AccountProfile`, including:
  - `accountId`: The ID of the account profile.
  - `accountType`: The type of account (e.g., PROVIDER or RECIPIENT).
  - `phoneNumber`: The phone number associated with the account.
  - `name`: The name of the account holder.

This endpoint creates a new account profile for a specific client. Each client can have multiple
account profiles, and this operation allows for the creation of those accounts with the specified
type, phone number, and name.

**Upon Success**:

- **HTTP 201** Status Code is returned along with the created `AccountProfile` object in the
  response body.

**Upon Failure**:

- **HTTP 404** Status Code is returned if the `clientId` does not exist in the system.

### **GET /api/accountProfiles/get**

**Expected Input Parameters**:

- `accountId` (Integer): The ID of the account profile to retrieve.

**Expected Output**:

- A JSON object containing:
  - `account_id`: The ID of the account profile.
  - `name`: The name of the account holder.

This endpoint retrieves the details of an existing account profile based on the provided account ID.
The retrieved information includes the account holder's name and the account ID.

**Upon Success**:

- **HTTP 200** Status Code is returned along with the account profile details in the response body.

**Upon Failure**:

- **HTTP 404** Status Code is returned if the specified `accountId` does not exist.

### **Operational Guidelines**

- **Order of API Calls**: The `/create` endpoint must be called before retrieving an account profile
  using `/get` to ensure the account exists.
- **Error Handling**: If an invalid `clientId` or `accountId` is provided, the service will return a
  `404 Not Found` status code. Ensure that valid IDs are used in the requests.

---

## FoodListing Endpoints

### POST /createFoodListing

**Expected Input Parameters:**

- `clientId` (int): The ID of the client to associate with the account
- `accountId` (int): The ID of the (provider) account trying to create a listing
- `foodType` (String): The food type of the listing
- `quantityListed` (int): The quantity of the item listed
- `latitude` (float): The latitude of the pick-up location
- `longitude` (float): The longitude of the pick-up location

**Expected Output:**

- The method attempts to create and store a new food listing for an account with `accountId` in the client with `clientId`.

**Upon success:**

- A status code of `201 Created` and a response body with the message "Food listing created successfully with ID: {listingId}"

**Upon failure:**

- A status code of `404 Not Found` if the specified client or account does not exist, with a response body containing the message:
  ```json
  {
    "error": "Client ID or account ID not found."
  }
  ```
- A status code of `500 Internal Server Error` with a response body containing the message "Failed to create food listing" if an unexpected error occurs during the creation or storage of the listing.

### GET /getFoodListings

**Expected Input Parameters:**

- `clientId` (int): The ID of the client making the request

**Expected Output:**

- The method retrieves all food listings under the client with `clientId`.

**Upon success:**

- A status code of `200 OK` and a response body containing a collection of food listings under the specified client.

**Upon failure:**

- A status code of `404 Not Found` if the specified client does not exist with a response body containing the message:
  ```json
  {
    "error": "Client ID not found."
  }
  ```
- A status code of `404 Not Found` if there are no listings for the specified client

### GET /getNearbyListings

**Expected Input Parameters:**

- `clientId` (int): The ID of client trying to get listings near a query location
- `latitude` (float): The latitude of the query location
- `longitude` (float): The longitude of the query location
- `maxDistance` (int, optional with default value of 5): The maximum distance from the query location to consider when searching for food listings; expected to be greater than 0

**Expected Output:**

- The method retrieves food listings for a client with `clientId` that are within `maxDistance` of a given location (`latitude`, `longitude`).

**Upon success:**

- A status code of `200 OK` and a response body containing a collection of food listings located within a maximum distance from the given location.

**Upon failure:**

- A status code of `404 Not Found` if the specified client does not exist with a response body containing the message:
  ```json
  {
    "error": "Client ID not found."
  }
  ```
- A status code of `404 Not Found` if there are no listings within the specified distance of the specified location

### GET /getFoodListingsUnderAccount

**Expected Input Parameters:**

- `clientId` (int): The ID of the client to associate with the account
- `accountId` (int): The ID of the (provider) account trying to fetch their listings

**Expected Output:**

- The method retrieves all food listings under an account with `accountId` in a client with `clientId`.

**Upon success:**

- A status code of `200 OK` and a response body containing a collection of food listings under the specified account.

**Upon failure:**

- A status code of `404 Not Found` if the specified client or account does not exist with a response body containing the message
  ```json
  {
    "error": "Client ID or account ID not found."
  }
  ```
- A status code of `404 Not Found` if there are no listings under the specified account
- A status code of `401 Unauthorized` if the account with the specified `accountId` is not of type `AccountType.PROVIDER`

### GET /getRequestsForListing

**Expected Input Parameters:**

- `clientId` (int): The ID of the client to associate with the account
- `accountId` (int): The ID of the (provider) account trying to get the requests made for one of their listings
- `listingId` (int): The ID of the listing to find requests for

**Expected Output:**

- The method retrieves all requests made for a listing with `listingId` under a provider account with `accountId` using the client with `clientId`.

**Upon success:**

- A status code of `200 OK` and a response body containing a collection of requests for the specified food listing.

**Upon failure:**

- A status code of `404 Not Found` if there is no listing with `listingId` under the account with `accountId` in the client with `clientId` and a response body with the message
  ```json
  {
    "error": "Listing with ID {listingId} not found under client with ID {clientId} and account with ID {accountId}."
  }
  ```
- A status code of `401 Unauthorized` if the account with the specified `accountId` is not of type `AccountType.PROVIDER`.
  ```json
  {
    "error": "Expected account holder to be a PROVIDER."
  }
  ```
- A status code of `404 Not Found` if there are no requests for the specified listing.

### PATCH /fulfillRequest

**Expected Input Parameters:**

- `clientId` (int): The ID of the client to associate with the account
- `listingId` (int): The ID of the listing that the request is made for
- `quantityRequested` (int): The quantity of items requested

**Expected Output:**

- The method attempts to fulfill a request for a food listing with `listingId` by decrementing the quantity of items listed by the requested amount.

**Upon success:**

- A status code of `200 OK` and a response body containing the message:
  ```json
  {
    "message": "Updated Successfully."
  }
  ```

**Upon failure:**

- A status code of `404 Not Found` if there is no listing with `listingId` in the client with `clientId`.
  ```json
  {
    "error": "Listing with ID {listingId} not found under client with ID {clientId} and account with ID {accountId}."
  }
  ```
- A status code of `401 Unauthorized` if the account with the specified `accountId` is not of type `AccountType.PROVIDER`.
  ```json
  {
    "error": "Expected account holder to be a PROVIDER."
  }
  ```
- A status code of `400 Bad Request` if the current `quantityListed` is less than the `quantityRequested`.
  ```json
  {
    "error": "Quantity listed ({currQuantityListed}) for listing with ID {listingId} cannot satisfy quantity requested ({quantityRequested})."
  }
  ```

### PATCH /updateFoodListing

**Expected Input Parameters:**

- `clientId` (int): The ID of the client to associate with the account
- `accountId` (int): The ID of the account trying to update one of their listings
- `listingId` (int): The ID of the listing to update
- `newFoodType` (String, optional): A new food type for the listing
- `newLatitude` (Float, optional): A new latitude for the listing's pick-up location
- `newLongitude` (Float, optional): A new longitude for the listing's pick-up location
- `newQuantityListed` (Integer, optional): A new quantity for the item listed

**Expected Output:**

- The method updates various attributes of the food listing with `listingId` (under the account with `accountId` in the client with `clientId`), such as food type, location, and quantity listed.

**Upon success:**

- A status code of `200 OK` and a response body containing the message:
  ```json
  {
    "message": "Updated Successfully."
  }
  ```

**Upon failure:**

- A status code of `404 Not Found` if there is no listing with the specified `listingId` under the account with the specified `accountId` in the client with `clientId`.
  ```json
  {
    "error": "Listing with ID {listingId} not found under client with ID {clientId} and account with ID {accountId}."
  }
  ```
- A status code of `401 Unauthorized` if the account with the specified `accountId` is not of type `AccountType.PROVIDER`.
  ```json
  {
    "error": "Expected account holder to be a PROVIDER."
  }
  ```

### Operational Guidelines

- **Order of API calls:**
  - `/getFoodListings` and `/getNearbyListings` should only be called with `clientId` after the corresponding client has been created
  - `/createFoodListing` should only be called with `clientId` and `accountId` after the corresponding client and account have been created
  - `/getListingsUnderAccount`, `/getRequestsForListing`, `/fulfillRequest`, `/updateFoodListing` should only be called with `clientId`, `accountId`, and `listingId` after the corresponding listing has been created by the account in the client
- **Error Handling:** If an invalid `clientId`, `accountId`, and/or `listingId` is provided, the service will return a `404 Not Found` status code with a corresonding error message.

### Trello Board

https://trello.com/b/syksFcSZ/ase-aces
