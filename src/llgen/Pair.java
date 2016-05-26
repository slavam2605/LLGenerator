package llgen;

import com.sun.istack.internal.NotNull;

/**
 * @author Моклев Вячеслав
 */
public class Pair<A, B> implements Comparable<Pair<A, B>>{
    public final A a;
    public final B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + a +
                ", b=" + b +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Pair<?, ?> pair = (Pair<?, ?>) o;
        return !(a != null ? !a.equals(pair.a) : pair.a != null) && !(b != null ? !b.equals(pair.b) : pair.b != null);

    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public int compareTo(Pair<A, B> o) {
        int diff1 = ((Comparable<A>) a).compareTo(o.a);
        if (diff1 != 0) {
            return diff1;
        }
        return ((Comparable<B>) b).compareTo(o.b);
    }
}
