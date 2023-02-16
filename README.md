# NIKParser
NIK offline validator

# How To Use
```kotlin
val nikParser: NIKParser = NIKParserImpl(context)
val nikParseResult = nikParser.parseNik(string)
```

# parseNik(string) Result
```json
{
  "nik": "3276055708900002",
  "isValid": true,
  "province": {
    "id": "32",
    "name": "Jawa Barat"
  },
  "regency": {
    "id": "3276",
    "name": "Kota Depok"
  },
  "district": {
    "id": "327605",
    "name": "Sukmajaya",
    "zipCode": "16417"
  },
  "birthDate": "1990-08-17",
  "gender": "female",
  "uniqueCode": "0002"
}
```
