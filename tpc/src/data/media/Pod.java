package data.media;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import static data.ReprUtil.repr;

/**
 * @author nickma
 * @version 1.0
 * @since 05/02/16
 */
@SuppressWarnings("serial")
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "JaxbMedia")
public class Pod  implements java.io.Serializable {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Pod getPod() {
        return pod;
    }

    public void setPod(Pod pod) {
        this.pod = pod;
    }

    public String message;
    public Pod pod;

    public Pod() {}

    public Pod(String message, Pod pod) {
        this.message = message;
        this.pod = pod;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Pod pod = (Pod) o;

        if (message != null ? !message.equals(pod.message) : pod.message != null) return false;
        if (pod != null ? !pod.equals(pod.pod) : pod.pod != null) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result = message != null ? message.hashCode() : 0;
        result = 31 * result + (pod != null ? pod.hashCode() : 0);
        return result;
    }

    public String toString () {
        StringBuilder sb = new StringBuilder();
        sb.append("[Pod ");
        sb.append("message=").append(repr(message));
        sb.append(", pod=").append(pod);
        sb.append("]");
        return sb.toString();
    }
}
