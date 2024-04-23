package otus.homework.customview

import android.os.Parcel
import android.os.Parcelable
import android.view.View

class SelectedCategoryState : View.BaseSavedState {
    var selectedCagetory: Int = 0

    constructor(parcel: Parcel) : super(parcel) {
        selectedCagetory = parcel.readInt()
    }

    constructor (parcelable: Parcelable?) : super(parcelable)

    override fun writeToParcel(out: Parcel, flags: Int) {
        out.writeInt(selectedCagetory)
    }

    companion object CREATOR : Parcelable.Creator<SelectedCategoryState?> {
        override fun createFromParcel(parcel: Parcel): SelectedCategoryState {
            return SelectedCategoryState(parcel)
        }

        override fun newArray(size: Int): Array<SelectedCategoryState?> {
            return arrayOfNulls(size)
        }
    }
}
