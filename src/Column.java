/**
 * @author patrick
 */
public class Column implements Comparable<Column> {

    String token;
    double weight;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Column column = (Column) o;

        if (Double.compare(column.weight, weight) != 0) return false;
        if (!token.equals(column.token)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = token.hashCode();
        temp = weight != +0.0d ? Double.doubleToLongBits(weight) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public int compareTo(Column o) {
        return token.compareTo(o.token);
    }
}
