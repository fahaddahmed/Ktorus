# Simple HTTP Server in Kotlin
This project is a simple multi-threaded HTTP server built in Kotlin. The server supports basic HTTP functionality, including handling requests for echoing messages, returning user-agent headers, and serving files.

## Table of Contents
- [Features](#features)
- [Requirements](#requirements)
- [Setup](#setup)
- [Usage](#usage)
- [Endpoints](#endpoints)
- [Testing](#testing)
- [File Structure](#file-structure)
- [Acknowledgements](#acknowledgements)

## Features
- Multi-threaded: Handles multiple client connections concurrently.
- Echo Endpoint: Echoes back messages with optional GZIP compression.
- User-Agent Endpoint: Returns the User-Agent header from requests.
- File Serving: Serves files from a specified directory and allows file creation via POST requests.
- HTTP Compliance: Supports essential HTTP headers such as Content-Type and Content-Length.

## Requirements
- Kotlin: 1.4 or newer
- JDK: Java Development Kit, version 8 or newer
- Python: Required for running the test script

## Setup
1. Clone the Repository
```
git clone https://github.com/your-username/your-repo.git
cd your-repo
```
2. Compile the Kotlin Code Use Kotlin’s compiler to build the server:

```
kotlinc main.kt HttpServer.kt HttpResponse.kt HttpRequest.kt HttpMethod.kt HttpStatus.kt -include-runtime -d server.jar
```

3. Run the Server Run the compiled server using the provided shell script:

```
./your_program.sh
```
The server will start listening on port 4221. By default, it serves files from /tmp if no directory is specified.

## Usage
The server accepts HTTP requests at various endpoints. You can test it with curl or the provided Python test script.

Example curl request:
```
curl -v http://localhost:4221/user-agent -H "User-Agent: example/1.0"
```

## Endpoints
### Root Path (/)
- Method: GET
- Description: Returns a 200 OK response as a basic server status check.

### Echo Endpoint (/echo/{message})
- Method: GET
- Description: Echoes back the {message} in the URL path.
- Example:
```
curl -v http://localhost:4221/echo/hello
```
- GZIP Compression: If the Accept-Encoding: gzip header is included in the request, the server responds with a GZIP-compressed message.
```
curl -v http://localhost:4221/echo/hello -H "Accept-Encoding: gzip"
```
### User-Agent Endpoint (/user-agent)
- Method: GET
- Description: Returns the User-Agent header value from the request in the response body.
- Example:
```
curl -v http://localhost:4221/user-agent -H "User-Agent: my-app/1.0"
```
### File Retrieval (/files/{filename})
- Method: GET
- Description: Serves a file from the server's specified directory.
- Example:
```
curl -v http://localhost:4221/files/testfile.txt
```
### File Creation (/files/{filename})
- Method: POST
- Description: Accepts a file’s content in the request body and saves it under the specified filename in the server’s directory.
- Example:
```
curl -v -X POST http://localhost:4221/files/newfile.txt -d "File content here"
```

## Testing
A Python test script (test.py) is included to validate the server's functionality. The script tests various endpoints and verifies that the server returns correct HTTP responses.

### Running the Server and Tests
- Open two terminals.
- In the first terminal, start the server:
```
./your_program.sh
```
- In the second terminal, run the test script:
```python3 test.py```

### Test Details
The test script validates the following:

- Root path: Checks that the root path (/) responds with 200 OK.
- Echo endpoint: Tests the /echo/{message} endpoint, with and without GZIP.
- User-Agent endpoint: Verifies that /user-agent returns the correct User-Agent header.
- File Not Found: Ensures a 404 Not Found response is returned for non-existent files.
- File Retrieval and Creation: Checks that existing files can be retrieved and new files can be created via POST.
- Example Output
Each test displays a pass/fail status. For example:

```Testing root / path...
✅ Passed: root / path GET
Testing /echo/{str}...
✅ Passed: /echo/{str} GET
Testing /user-agent...
✅ Passed: /user-agent GET
```

## File Structure
Here's an overview of the main files and their responsibilities:

- Main.kt: Entry point for the application, starts the HTTP server.
- HttpServer.kt: Manages incoming connections, routes requests, and handles GET and POST requests.
- HttpResponse.kt: Constructs HTTP responses, including setting headers and body.
- HttpRequest.kt: Parses incoming HTTP requests from clients.
- HttpMethod.kt: Enumerates HTTP methods supported by the server (e.g., GET, POST).
- HttpStatus.kt: Defines HTTP statuses (e.g., 200 OK, 404 Not Found).
- test.py: Python test script to validate server functionality.

## Acknowledgements
This project was built as an exercise to demonstrate server implementation in Kotlin, covering HTTP basics, multithreading, and simple request parsing.