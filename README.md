# PasswordManagerAPI

This is a REST API applciaton for managing passwords. 
It allows users to safely store their passwords and receive 
them when they request. 

# REST API
API endpoints

## Register

Register new user to Password Manager.

### Request

``
POST /api/v1/user/register
``
`````json
{
  "communicationAddress": "test@example.com",
  "password": "password"
}
`````
Request example:
````
curl -X 'POST' 
  '/api/v1/User/register' 
  -H 'accept: */*' 
  -H 'Content-Type: application/json' 
  -d '{
  "communicationAddress": "batuhankertmen@sabanciuniv.edu",
  "password": "123"
}'
````

### Response
```
HTTP/1.1 201 Created
content-type: application/json; charset=utf-8 
date: Thu,10 Aug 2023 20:58:24 GMT 
location: /api/v1/User/dc76cd13-ca29-45ab-a1c2-00ba5020476c 
server: Kestrel 

{
  "id": "dc76cd13-ca29-45ab-a1c2-00ba5020476c",
  "communicationAddress": "batuhankertmen@sabanciuniv.edu",
  "active": false
}
```

## Account Activation

Activating account by clicking the link that is sent to contact address.

### Request
``
GET /api/v1/user/activate/?id=user-id&securityToken=security-token
``

Request Example:
````
curl -X 'GET' \
  '/api/v1/User/activate?id=0b7ca6b8-005d-4d80-b9b7-836122fe4133&securityToken=671C496538F60D0EA6C1D60F65A8DC43486D987EDD8EDFB86F3CA853E171492A' \
  -H 'accept: */*'
````

### Respone
````
HTTP/1.1 20O OK
content-type: text/plain; charset=utf-8 
date: Thu,10 Aug 2023 21:05:56 GMT 
server: Kestrel 
````

## Login

Receive JWT token by providing correct email and password.

### Request
``
POST /api/v1/user/login
``
`````json
{
  "communicationAddress": "test@example.com",
  "password": "password"
}
`````

Request Eample:
````
curl -X 'POST' 
  '/api/v1/User/login' 
  -H 'accept: */*' 
  -H 'Content-Type: application/json' 
  -d '{
  "communicationAddress": "batuhankertmen@sabanciuniv.edu",
  "password": "15011999b?"
}'
````

### Response

````
HTTP/1.1 200 OK
content-type: application/json; charset=utf-8 
date: Fri,11 Aug 2023 11:19:10 GMT 
server: Kestrel 

{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3ByaW1hcnlzaWQiOiI4ZmZkZWFiNi1kZDE5LTQ0YTctYTcwOS1jY2VjZmI4NDc0NmIiLCJleHAiOjE2OTE3NTM2NTEsImlzcyI6Imh0dHBzOi8vbG9jYWxob3N0OjcwODEvIiwiYXVkIjoiaHR0cHM6Ly9sb2NhbGhvc3Q6NzA4MS8ifQ.uGto5wMGY2E6US_IvJk3hJChChZ1V4OZygWJB9Aj0PM"
}

````

## Save Username and Password
### Request
``
POST /api/v1/logininformation
``
````json
{
  "userName": "encrypted-username",
  "password": "encrpted-password",
  "domain": "domain",
  "saltHexString": "f984451d917f44df86169f91c83a8eacc7e400b088f4cb650076ed77fd012eaf",
  "memorySize": 54,
  "degreeOfParallelism": 1,
  "iterations": 1
}
````

Request Eample:
````
curl -X 'POST' 
  '/api/v1/LoginInformation' 
  -H 'accept: */*' 
  -H 'Authorization: Bearer jwt-token 
  -H 'Content-Type: application/json' 
  -d '{
  "userName": "encrypted-username",
  "password": "encrpted-password",
  "domain": "domain",
  "saltHexString": "f984451d917f44df86169f91c83a8eacc7e400b088f4cb650076ed77fd012eaf",
  "memorySize": 54,
  "degreeOfParallelism": 1,
  "iterations": 1
}'
````

### Response
````
HTTP/1.1 200 OK
content-type: application/json; charset=utf-8 
date: Tue,15 Aug 2023 17:07:48 GMT 
server: Kestrel 
{
  "domain": "string"
}
````

## Get username and password by domain
### Request
``
GET /api/v1/LoginInformation?Domain=domain-name
``

Request Example:
````
curl -X 'GET' \
  '/api/v1/LoginInformation?Domain=domain-name'
  -H 'accept: */*' 
  -H 'Authorization: Bearer jwt-token'
````

### Response
````
HTTP/1.1 200 OK
content-type: application/json; charset=utf-8
date: Wed,16 Aug 2023 08:36:50 GMT
server: Kestrel
[
  {
    "usernameEncrypted": "string",
    "passwordEncrypted": "string",
    "saltHexString": "ABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEFABCDEF1111",
    "memorySize": 1024,
    "degreeOfParallelism": 32,
    "iterations": 32
  }
]
````