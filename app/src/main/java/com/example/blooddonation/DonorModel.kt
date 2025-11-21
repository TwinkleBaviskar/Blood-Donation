import java.io.Serializable

data class DonorModel(
    val userId: String? = null,
    val fullName: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val age: String? = null,
    val gender: String? = null,
    val bloodGroup: String? = null,
    val weight: String? = null,
    val hemoglobin: String? = null,
    val lastDonation: String? = null,
    val livesSaved: Int? = 0,
    val totalDonations: Int? = 0,
    val profileImage: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null
) : Serializable
