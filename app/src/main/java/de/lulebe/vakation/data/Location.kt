package de.lulebe.vakation.data

import android.location.Address
import com.google.android.gms.location.places.Place

data class Location(val name: String, val address: String, val latitude: Double, val longitude: Double) {

    fun toDbLocation(tripId: Long, entityId: Long?): DbLocation =
            DbLocation(entityId, tripId, latitude, longitude, name, address)


    companion object {

        fun fromDbLocation(dbLoc: DbLocation): Location =
                Location(
                        dbLoc.name ?: "unknown",
                        dbLoc.address ?: "unknown",
                        dbLoc.latitude,
                        dbLoc.longitude
                )

        fun fromAddress(ad: Address): Location {
            val name = if (ad.maxAddressLineIndex >= 0) ad.getAddressLine(0) else "unknown"
            var address = ad.latitude.toString() + ", " + ad.longitude.toString()
            if (ad.maxAddressLineIndex >= 0 || !ad.locality.isEmpty() || !ad.countryName.isEmpty()) {
                val addressList = mutableListOf<String>()
                if (ad.maxAddressLineIndex >= 0)
                    addressList.add(ad.getAddressLine(0))
                if (ad.locality != null && !ad.locality.isEmpty())
                    addressList.add(ad.locality)
                if (ad.countryName != null && !ad.countryName.isEmpty())
                    addressList.add(ad.countryName)
                address = addressList.joinToString(", ")
            }
            return Location(name, address, ad.latitude, ad.longitude)
        }

        fun fromPlace(place: Place): Location {
            return Location(place.name.toString(), place.address.toString(), place.latLng.latitude, place.latLng.longitude)
        }

        fun fromCoordinates(latitude: Double, longitude: Double): Location {
            return Location(
                    "unknown",
                    latitude.toString() + ", " + longitude.toString(),
                    latitude,
                    longitude
            )
        }

    }


}