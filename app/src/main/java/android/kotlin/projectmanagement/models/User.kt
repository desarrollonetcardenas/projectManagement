package android.kotlin.projectmanagement.models

import android.os.Parcel
import android.os.Parcelable
import kotlin.with as with1

data class User (
    val name : String = "",
    val email : String = "",
    val image : String = "",
    val mobile : Long = 0,
    val fcmToken : String = "",
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(dest: Parcel, flags: Int)  = with1(dest){
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}