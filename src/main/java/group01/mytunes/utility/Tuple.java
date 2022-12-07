package group01.mytunes.utility;

public class Tuple<K,V> {
    private K first;
    private V second;

    /**
     * Class to return more then one type of value, from a method
     * @param first can be any type
     * @param second
     * can return either of the two values
     */
    public Tuple(K first, V second) {
        this.first = first;
        this.second = second;
    }

    public K getFirst() {
        return first;
    }

    public V getSecond() {
        return second;
    }
}
