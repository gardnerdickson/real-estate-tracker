package com.realestatetracker.entity


trait ApplicationEntity {

}


object PropertyListing {
  def apply(realtorResult: RealtorResult): PropertyListing = {

    def normalizeAndConvertPrice(price: String): Int = {
      price
        .replace("$", "")
        .replace(",", "")
        .replace("+ HST", "")
        .replace("+ GST", "")
        .replace(" ", "")
        .toInt
    }

    new PropertyListing(
      realtorResult.Id,
      realtorResult.MlsNumber,
      realtorResult.PublicRemarks,
      realtorResult.Building.BathroomTotal,
      realtorResult.Building.Bedrooms,
      realtorResult.Building.Type,
      normalizeAndConvertPrice(realtorResult.Property.Price),
      realtorResult.Property.Address.AddressText,
      realtorResult.Property.Address.Longitude,
      realtorResult.Property.Address.Latitude,
      realtorResult.PostalCode
    )
  }

  def apply(realtorResults: List[RealtorResult]): List[PropertyListing] = {
    realtorResults.map(PropertyListing.apply)
  }
}


case class PropertyListing(
  realtorId: Long,
  mlsNumber: String,
  description: String,
  numberOfBathrooms: String,
  numberOfBedrooms: String,
  buildingType: String,
  price: Int,
  address: String,
  longitude: Float,
  latitude: Float,
  postalCode: String,
) extends ApplicationEntity



