package cvsi.com.updatemaster.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by rivasyshyn on 16.09.2014.
 */

public class Resource implements Parcelable {

    public Resource() {
        resources = new ArrayList<Resource>(3);
    }

    public Resource(Parcel source) {
        type = ResourceType.valueOf(source.readString());
        icon = source.readString();
        name = source.readString();
        description = source.readString();
        url = source.readString();

        int size = source.readInt();
        Resource[] res = new Resource[size];
        source.readTypedArray(res, CREATOR);
        resources = new ArrayList<Resource>();
        resources.addAll(Arrays.asList(res));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type.name());
        dest.writeString(icon);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(url);
        dest.writeInt(resources.size());
        dest.writeTypedArray(resources.toArray(new Resource[0]), flags);
    }

    private static Creator<Resource> CREATOR = new Creator<Resource>() {
        @Override
        public Resource createFromParcel(Parcel source) {
            return new Resource(source);
        }

        @Override
        public Resource[] newArray(int size) {
            return new Resource[size];
        }
    };

    public static enum ResourceType {
        REPOSITORY, APPLICATION, PACKAGE
    }

    @Expose
    private ResourceType type;
    @Expose
    private String icon;
    @Expose
    private String name;
    @Expose
    private String description;
    @Expose
    private String url;

    @Expose
    private List<Resource> resources;

    public ResourceType getType() {
        return type;
    }

    public void setType(ResourceType type) {
        this.type = type;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }

    @Override
    public String toString() {
        return "Resource{" +
                "type=" + type +
                ", icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                ", resources=" + resources +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;

        Resource resource = (Resource) o;

        if (description != null ? !description.equals(resource.description) : resource.description != null)
            return false;
        if (icon != null ? !icon.equals(resource.icon) : resource.icon != null) return false;
        if (name != null ? !name.equals(resource.name) : resource.name != null) return false;
        if (type != resource.type) return false;
        if (url != null ? !url.equals(resource.url) : resource.url != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = type != null ? type.hashCode() : 0;
        result = 31 * result + (icon != null ? icon.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
