
# NIKParser
[![](https://jitpack.io/v/dzakdzaks/NIKParser.svg)](https://jitpack.io/#dzakdzaks/NIKParser)
![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)

NIK offline validator

# How To Use
Add it in your root build.gradle at the end of repositories:
```groovy  
allprojects {  
 repositories {
  // other code 
  maven { url 'https://jitpack.io' }
 }
}  
```  
Or if using settings.gradle:
```groovy  
dependencyResolutionManagement {  
 // other code
 repositories { 
  // other code 
  maven { url 'https://jitpack.io' } 
 }
}  
```
Next add the dependency:
```groovy
dependencies {
    implementation 'com.github.dzakdzaks:NIKParser:{LATEST_VERSION}'
}
```  
Then use it:
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
