package com.realestatetracker.entity

import com.fasterxml.jackson.annotation.JsonProperty

trait HttpEntity {

}

/*
  Realtor.ca API
 */

case class RealtorResponse(
  ErrorCode: RealtorErrorCode,
  Paging: RealtorPaging,
  Results: Array[RealtorResult]
) extends HttpEntity

case class RealtorErrorCode(
  Id: Int,
  Description: String,
  Status: String
) extends HttpEntity

case class RealtorPaging(
  RecordsPerPage: Int,
  CurrentPage: Int,
  TotalRecords: Int,
  MaxRecords: Int,
  TotalPages: Int,
  RecordsShowing: Int,
  Pins: Int
) extends HttpEntity

case class RealtorResult(
  Id: Int,
  MlsNumber: String,
  PublicRemarks: String,
  Building: Building,
  Individual: Array[Individual],
  Property: Property,
  PostalCode: String,
  StatusId: Int
) extends HttpEntity

case class Building(
  BathroomTotal: String,
  Bedrooms: String,
  Type: String
) extends HttpEntity

case class Individual(
  IndividualID: Int,
  Name: String,
  Organization: Organization
) extends HttpEntity

case class Organization(
  OrganizationID: Int,
  Name: String,
  Designation: String,
) extends HttpEntity

case class Property(
  Price: String,
  Type: String,
  Address: Address,
  TypeId: String,
  OwnershipType: String
) extends HttpEntity

case class Address(
  AddressText: String,
  Longitude: Float,
  Latitude: Float
) extends HttpEntity


/*
  House Sigma (housesigma.com)
 */

case class HouseSigmaRequest(
  lang: String,
  house_type: String,
  days: Int,
  bedroom_range: Array[Int],
  price_min: Int,
  price_max: Int,
  bathroom_min: Int,
  garage_min: Int,
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
  ml_num: String,
  lat: Float,
  lng: Float,
  price: String,
  sold: Int,
  hash_id: String
) extends HttpEntity


/*
  Mongo House (mongohouse.com)
 */

// TODO(gdickson): fill this out
case class MongoHouseResponse(
  @JsonProperty("_id")
  mongoId: String,
  @JsonProperty("DOM")
  dom: Int,
  @JsonProperty("_Sold")
  soldPrice: Int,
  @JsonProperty("Sold Date")
  soldDate: String,
  @JsonProperty("Contract Date")
  contractDate: String,
  @JsonProperty("_List")
  listedPrice: Int,
  @JsonProperty("MLS#")
  mlsNumber: String,
) extends HttpEntity
