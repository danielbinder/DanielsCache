import java.util.*;
import java.util.function.Function;

public class DanielsCache<O, R> {
    private final Map<O, Node> cache;
    private Node first;
    private Node last;
    private final int maxSize;
    private final Function<O, R> mapper;

    public DanielsCache(int maxSize, Function<O, R> mapper) {
        cache = HashMap.newHashMap(maxSize);
        this.maxSize = maxSize;
        this.mapper = mapper;
    }

    public R get(O object) {
        return cache.computeIfAbsent(object, Node::new).moveToEnd();
    }

    private class Node {
        private final O object;
        private final R result;
        private Node prev;
        private Node next;

        private Node(O object) {
            this.object = object;
            this.result = mapper.apply(object);

            if(first == null) first = this;
            else {
                prev = last;
                prev.next = this;
                // next = null can be omitted, since we never iterate over the queue
            }

            last = this;
        }

        private R moveToEnd() {
            if(cache.size() > maxSize) {
                cache.remove(first.object);
                first = first.next;
                first.prev = null;
            }

            if(this == last) return result;

            if(prev != null) prev.next = next;
            last.next = this;

            prev = last;
            // next = null can be omitted, since we never iterate over the queue

            last = this;

            return result;
        }
    }
}
