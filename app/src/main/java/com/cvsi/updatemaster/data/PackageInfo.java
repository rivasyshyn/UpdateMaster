package com.cvsi.updatemaster.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
* Created by rivasyshyn on 19.09.2014.
*/
public class PackageInfo implements Parcelable, Item {

    @Expose
    @SerializedName("apk_version")
    String apkVersion;
    @Expose
    @SerializedName("size")
    String apkSize;
    @Expose
    @SerializedName("package_name")
    String packageName;
    @Expose
    @SerializedName("file_name")
    String apkName;
    @Expose
    @SerializedName("release_notes")
    String releaseNotes;
    @Expose
    @SerializedName("apk_link")
    String apkLink;

    public String getApkVersion() {
        return apkVersion;
    }

    public String getApkSize() {
        return apkSize;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getApkName() {
        return apkName;
    }

    public String getReleaseNotes() {
        return releaseNotes;
    }

    public String getApkLink() {
        return apkLink;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(apkLink);
        dest.writeString(apkName);
        dest.writeString(apkSize);
        dest.writeString(apkVersion);
        dest.writeString(packageName);
        dest.writeString(releaseNotes);
    }

    public PackageInfo(Parcel source) {
        apkLink = source.readString();
        apkName = source.readString();
        apkSize = source.readString();
        apkVersion = source.readString();
        packageName = source.readString();
        releaseNotes = source.readString();
    }

    public static Creator<PackageInfo> CREATOR = new Creator<PackageInfo>() {
        @Override
        public PackageInfo createFromParcel(Parcel source) {
            return new PackageInfo(source);
        }

        @Override
        public PackageInfo[] newArray(int size) {
            return new PackageInfo[size];
        }
    };
}
