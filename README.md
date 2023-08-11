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
POST /api/user/register
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
  '/api/User/register' 
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
location: /api/User/dc76cd13-ca29-45ab-a1c2-00ba5020476c 
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
GET /api/user/activate/{id}/{token}
``

Request Example:
````
curl -X 'GET' 
  '/api/User/activate/dc76cd13-ca29-45ab-a1c2-00ba5020476c/A1BE039FD4E37DE93B766620FC25F3CE8DA82D61D0A5684B8B83B58420362932' 
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
POST /api/user/login
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
  '/api/User/login' 
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
