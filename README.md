# Paint-Splat
# lobby
### A brief description
get userId and game information
### Request URL

```http://asepaint.kkdev.org:8080/lobby```

### Request method

```get```

### Return example

```
{
  "seed": 451,
  "userColor": 2,
  "roundStatus": 0,
  "roundId": 13,
  "userId": 132
 }
 ```


### Return parameter description

|parameter name|Types of|	Description|
|---|---|---|
|seed|	string|	orbit seed, use to generate canvas moving orbit|
|userColor|	string|	userColor, mark color|
|roundStatus|	string|	game round status, if return 1 means game round has already turned on, need to wait|
|roundId|	string|	|
|userId	|string||








--------
# webSocket(connect/hit/inform/boardcast)
### Client-Connect Request

```http://asepaint.kkdev.org:8080/websocket/114-NIEJUN```

### Server-GameStart
#### GameStart example
```
{
    "eventType":"gameStart",
    "userName":{
        "111":"Anny",
        "112":"Xiaokang Wang",
        "113":"Rui",
        "114":"NIEJUN"
    },
    "gamerNum":4
}
```
#### parameter description
|parameter name|Types of|	Description|
|---|---|---|
|eventType|	string|	|
|userName|	object|	the map relationship between userId:userName|
|111|	string|userId : userName, userId [roundId*10+the order of joining game ]	|
|gamerNum|	string|	|

### Client-Hit
#### Hit example
```
{
    "eventType":"Hit",
    "detail":{
        "Size":30,
        "LocationX":250,
        "LocationY":566
    }
}
```
#### parameter description
|parameter name|Types of|	Description|
|---|---|---|
|eventType|	string|	Hit|
|detail	|object|	|
|Size	|string|	|
|LocationX|	string|	|
|LocationY	|string|	|


### Server-Hitreceive
#### Hitreceive example
```
{
    "eventType":"HitPos",
    "detail":{
        "LocationX":136,
        "LocationY":510,
        "Size":30
    },
    "userId":"111"
}
```
#### parameter description
|parameter name	|Types of|	Description|
|---|---|---|
|eventType|	string|if hit sucess HitPos,type boardCast;if hit fail HitNeg, type  orientate message |
|detail|	object	||
|LocationX|	string|	|
|LocationY|	string|	|
|Size|	string|	|
|userId	|string|	|

### Server-GameOver
#### GameOver example
```
{
    "score":{
        "Anny":7,
        "Xiaokang Wang":6,
        "Rui":3,
        "NIEJUN":4
    },
    "eventType":"gameOver"
}
```
#### parameter description
|parameter name|Types of|	Description|
|---|---|---|
|eventType|	string|	|
|score|	object|the map relationship between userName:userScore|
|Anny	|string	||
|Xiaokang Wang	|string	||
|Rui|	string||
|NIEJUN	|string	||





