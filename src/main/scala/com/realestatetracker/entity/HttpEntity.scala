package com.realestatetracker.entity

import com.fasterxml.jackson.annotation.JsonProperty

trait HttpEntity {

}

/*
  Realtor.ca API
 */

case class RealtorResponse(
  @JsonProperty("ErrorCode")
  errorCode: RealtorErrorCode,
  @JsonProperty("Paging")
  paging: RealtorPaging,
  @JsonProperty("Results")
  results: Array[RealtorResult]
) extends HttpEntity

case class RealtorErrorCode(
  @JsonProperty("Id")
  id: Int,
  @JsonProperty("Description")
  description: String,
  @JsonProperty("Status")
  status: String
) extends HttpEntity

case class RealtorPaging(
  @JsonProperty("RecordsPerPage")
  recordsPerPage: Int,
  @JsonProperty("CurrentPage")
  currentPage: Int,
  @JsonProperty("TotalRecords")
  totalRecords: Int,
  @JsonProperty("MaxRecords")
  maxRecords: Int,
  @JsonProperty("TotalPages")
  totalPages: Int,
  @JsonProperty("RecordsShowing")
  recordsShowing: Int,
  @JsonProperty("Pins")
  pins: Int
) extends HttpEntity

case class RealtorResult(
  @JsonProperty("Id")
  id: Int,
  @JsonProperty("MlsNumber")
  mlsNumber: String,
  @JsonProperty("PublicRemarks")
  publicRemarks: String,
  @JsonProperty("Building")
  building: Building,
  @JsonProperty("Individual")
  individual: Array[Individual],
  @JsonProperty("Property")
  property: Property,
  @JsonProperty("PostalCode")
  postalCode: String,
  @JsonProperty("StatusId")
  statusId: Int
) extends HttpEntity

case class Building(
  @JsonProperty("BathroomTotal")
  bathroomTotal: String,
  @JsonProperty("Bedrooms")
  bedrooms: String,
  @JsonProperty("Type")
  buildingType: String
) extends HttpEntity

case class Individual(
  @JsonProperty("IndividualID")
  individualID: Int,
  @JsonProperty("Name")
  name: String,
  @JsonProperty("Organization")
  organization: Organization
) extends HttpEntity

case class Organization(
  @JsonProperty("OrganizationID")
  organizationID: Int,
  @JsonProperty("Name")
  name: String,
  @JsonProperty("Designation")
  designation: String,
) extends HttpEntity

case class Property(
  @JsonProperty("Price")
  price: String,
  @JsonProperty("Type")
  propertyType: String,
  @JsonProperty("Address")
  address: Address,
  @JsonProperty("TypeId")
  typeId: String,
  @JsonProperty("OwnershipType")
  ownershipType: String
) extends HttpEntity

case class Address(
  @JsonProperty("AddressText")
  addressText: String,
  @JsonProperty("Longitude")
  longitude: Float,
  @JsonProperty("Latitude")
  latitude: Float
) extends HttpEntity


/*
  House Sigma (housesigma.com)
 */

case class HouseSigmaRequest(
  lang: String,
  @JsonProperty("house_type")
  houseType: String,
  days: Int,
  @JsonProperty("bedroom_range")
  bedroomRange: Array[Int],
  @JsonProperty("price_min")
  minPrice: Int,
  @JsonProperty("price_max")
  maxPrice: Int,
  @JsonProperty("bathroom_min")
  minBathrooms: Int,
  @JsonProperty("garage_min")
  minGarages: Int,
  lat1: Float,
  lon1: Float,
  lat2: Float,
  lon2: Float
) extends HttpEntity

case class HouseSigmaResponse(
  status: Boolean,
  data: HouseSigmaData
) extends HttpEntity

case class HouseSigmaData(
  message: String,
  list: Array[HouseSigmaSoldRecord]
) extends HttpEntity

case class HouseSigmaSoldRecord(
  @JsonProperty("ml_num")
  mlsNumber: String,
  lat: Float,
  lng: Float,
  price: String,
  sold: Int,
  @JsonProperty("hash_id")
  hashId: String
) extends HttpEntity


/*
  Mongo House (mongohouse.com)
 */

case class MongoHouseResponse(
  @JsonProperty("_id")
  mongoId: String,
  @JsonProperty("DOM")
  dom: Int,
  @JsonProperty("_List")
  listedPrice: Int,
  @JsonProperty("_Sold")
  soldPrice: Int,
  @JsonProperty("Sold Date")
  soldDate: String,
  @JsonProperty("Contract Date")
  contractDate: String,
  @JsonProperty("MLS#")
  mlsNumber: String,
) extends HttpEntity
