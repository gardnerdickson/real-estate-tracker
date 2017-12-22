package com.realestatetracker.request

trait HttpEntity {

}

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
