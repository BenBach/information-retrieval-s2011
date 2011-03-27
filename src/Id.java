/**
 * @author patrick
 */
public class Id {
    public String clazz;
    public String name;

    public Id(String clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Id id = (Id) o;

        if (!clazz.equals(id.clazz)) return false;
        if (!name.equals(id.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = clazz.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }
}
