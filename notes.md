
# realtor.ca API

### Search Post
```POST https://api2.realtor.ca/Listing.svc/PropertySearch_Post```

Parameters
```
CultureId=1
ApplicationId=1
RecordsPerPage=9
MaximumResults=9
PropertySearchTypeId=1
PriceMin=500000
PriceMax=800000
TransactionTypeId=2
StoreyRange=0-0
BedRange=0-0
BathRange=0-0
LongitudeMin=-79.3793425201294
LongitudeMax=-79.37307687987061
LatitudeMin=43.6425293479763
LatitudeMax=43.644559606748174
SortOrder=A
SortBy=1
viewState=m
Longitude=-79.3762097
Latitude=43.6428923
CurrentPage=1
ZoomLevel=18
PropertyTypeGroupID=1
Token=D6TmfZprLI/3v358QgeiE0VBs2+Zm9yNMNTWKjAQBhQ=
GUID=9b5f3d0a-890e-4504-92f9-b2ccf4d3a65a
Version=6.0
```

### Toronto Coordinate Bounds

top left: 43.677901, -79.443024

bottom right: 43.633278, -79.342944


### TODOS

- Powershell script
- Run powershell script on a daily schedule
- logging pass
- Config directory
  - JVM argument
  - Contains config file
- Email reports
  - Password encryption/decryption?
- Execution logging
