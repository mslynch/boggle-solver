# Boggle Solver API

A REST API to find all words within a Boggle board and calculate the score of the words found.

## API usage
```shell
curl -X POST {BOGGLE_SOLVER_BASE_URI}/solve \
-H 'Content-Type: application/json' \
--data-raw '{
  "board": [
    ["k","o","n","y"],
    ["a","m","i","s"],
    ["l","e","k","l"],
    ["p","a","z","u"]
  ]
}'
```

## Development

The following are required to develop the application:

- Docker
- docker-compose
- JDK 11

(Or just Docker and Nix using `nix-shell`.)

To run the application in development mode, use:
```shell
./mvnw compile quarkus:dev
```

To build and run the application using Docker, use:
```shell
docker-compose up --build
```

To run an example board against a locally running version of the application, use:
```shell
./bin/solve-board
```

## Resources used
Algorithmic inspiration taken from [this question on Stack Overflow](https://stackoverflow.com/questions/746082/how-to-find-list-of-possible-words-from-a-letter-matrix-boggle-solver).
