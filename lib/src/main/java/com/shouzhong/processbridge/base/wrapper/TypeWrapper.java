package com.shouzhong.processbridge.base.wrapper;

import android.os.Parcel;
import android.os.Parcelable;

import com.shouzhong.processbridge.base.annotation.ClassId;
import com.shouzhong.processbridge.base.util.TypeUtils;

public class TypeWrapper extends BaseWrapper implements Parcelable {

    public static final Creator<TypeWrapper> CREATOR
            = new Creator<TypeWrapper>() {
        public TypeWrapper createFromParcel(Parcel in) {
            TypeWrapper typeWrapper = new TypeWrapper();
            typeWrapper.readFromParcel(in);
            return typeWrapper;
        }
        public TypeWrapper[] newArray(int size) {
            return new TypeWrapper[size];
        }
    };

    private TypeWrapper() {

    }

    public TypeWrapper(Class<?> clazz) {
        setName(!clazz.isAnnotationPresent(ClassId.class), TypeUtils.getClassId(clazz));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);
    }

    public void readFromParcel(Parcel in) {
        super.readFromParcel(in);
    }

}
