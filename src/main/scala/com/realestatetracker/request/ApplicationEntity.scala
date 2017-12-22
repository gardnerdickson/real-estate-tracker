package com.realestatetracker.request

trait ApplicationEntity {

}


object RealtorProperty {
  def apply(realtorResult: RealtorResult): RealtorProperty = {
    new RealtorProperty(
      realtorResult.Id,
      realtorResult.MlsNumber,
      realtorResult.PublicRemarks,
      realtorResult.Building.BathroomTotal,
      realtorResult.Building.Bedrooms,
      realtorResult.Building.Type,
      realtorResult.Property.Price,
      realtorResult.Property.Address.AddressText,
      realtorResult.Property.Address.Longitude,
      realtorResult.Property.Address.Latitude,
      realtorResult.PostalCode
    )
  }

  def apply(realtorResults: List[RealtorResult]): List[RealtorProperty] = {
    realtorResults.map(RealtorProperty.apply)
  }
}


case class RealtorProperty(
  realtorId: Long,
  mlsNumber: String,
  description: String,
  numberOfBathrooms: String,
  numberOfBedrooms: String,
  buildingType: String,
  price: String, // TODO: change this back to Int, need to parse out stuff like "+ HST" from response field.
  address: String,
  longitude: Float,
  latitude: Float,
  postalCode: String,
) extends ApplicationEntity



