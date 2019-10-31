package soporte;

import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;

public class TSBHashtableDA<K, V> implements Map<K, V>, Cloneable, Serializable {


    private final static int MAX_SIZE = Integer.MAX_VALUE;


    private Entry<K, V> table[];


    private int states[];


    private int initial_capacity;


    private int count;


    private float load_factor;


    protected transient int modCount;


    public TSBHashtableDA() {
        this(53, 0.75f);
    }


    public TSBHashtableDA(int initial_capacity) {
        this(initial_capacity, 0.75f);
    }


    public TSBHashtableDA(int initial_capacity, float load_factor) {
        if (load_factor <= 0) {
            load_factor = 0.75f;
        }
        if (initial_capacity <= 0) {
            initial_capacity = 53;
        } else {
            if (initial_capacity > TSBHashtableDA.MAX_SIZE) {
                initial_capacity = TSBHashtableDA.MAX_SIZE;
            } else {
                initial_capacity = this.siguientePrimo(initial_capacity);
            }
        }

        this.table = new Entry[initial_capacity];


        states = new int[initial_capacity];


        for (int i = 0; i < states.length; i++) {
            states[i] = 0;
        }

        this.initial_capacity = initial_capacity;
        this.load_factor = load_factor;
        this.count = 0;
        this.modCount = 0;
    }


    public TSBHashtableDA(Map<? extends K, ? extends V> t) {
        this(53, 0.75f);
        this.putAll(t);
    }


    @Override
    public int size() {
        return this.count;
    }


    @Override
    public boolean isEmpty() {
        return (this.count == 0);
    }


    @Override
    public boolean containsKey(Object key) {
        return (this.get((K) key) != null);
    }


    @Override
    public boolean containsValue(Object value) {
        return this.contains(value);
    }


    @Override
    public V get(Object key) {
        if (key == null)
            throw new NullPointerException("get(): parámetro null");

        int ih = this.h((K) key);
        int ic = ih;
        int j = 1;
        V valueReturn = null;


        while (this.states[ic] != 0) {

            if (this.states[ic] == 1) {
                Entry<K, V> entry = this.table[ic];


                if (key.equals(entry.getKey())) {
                    valueReturn = entry.getValue();
                    return valueReturn;
                }
            }
            ic += j * j;
            j++;
            if (ic >= this.table.length) {
                ic %= this.table.length;
            }
        }
        return valueReturn;
    }


    @Override
    public V put(K key, V value) {
        if (key == null || value == null)
            throw new NullPointerException("put(): parámetro null");

        int ih = this.h(key);
        int ic = ih;
        int first_tombstone = -1;
        int j = 1;
        V old = null;


        while (this.states[ic] != 0) {


            if (this.states[ic] == 1) {
                Entry<K, V> entry = this.table[ic];

                if (key.equals(entry.getKey())) {
                    old = entry.getValue();
                    entry.setValue(value);
                    this.count++;
                    this.modCount++;

                    return old;
                }
            }


            if (this.states[ic] == 2 && first_tombstone < 0) first_tombstone = ic;


            ic += j * j;
            j++;
            if (ic >= this.table.length) {
                ic %= this.table.length;
            }
        }

        if (first_tombstone >= 0) ic = first_tombstone;


        this.table[ic] = new Entry<K, V>(key, value);
        this.states[ic] = 1;


        this.count++;
        this.modCount++;


        float fc = (float) count / (float) this.table.length;
        if (fc >= this.load_factor)
            this.rehash();

        return old;
    }


    @Override
    public V remove(Object key) {
        if (key == null)
            throw new NullPointerException("remove(): parámetro null");

        int ih = this.h((K) key);
        int ic = ih;
        int j = 1;
        V old = null;


        while (this.states[ic] != 0) {


            if (this.states[ic] == 1) {
                Entry<K, V> entry = this.table[ic];

                if (key.equals(entry.getKey())) {
                    old = entry.getValue();
                    this.table[ic] = null;
                    this.states[ic] = 2;

                    this.count--;
                    this.modCount++;

                    return old;
                }
            }

            ic += j * j;
            j++;
            if (ic >= this.table.length) {
                ic %= this.table.length;
            }
        }

        return old;
    }


    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for (Map.Entry<? extends K, ? extends V> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }

    @Override
    public void clear() {


        this.table = new Entry[this.initial_capacity];

        states = new int[this.initial_capacity];


        for (int i = 0; i < states.length; i++) {
            states[i] = 0;
        }

        this.count = 0;
        this.modCount++;
    }


    @Override
    public Set<K> keySet() {
        if (keySet == null) {
            keySet = new KeySet();
        }
        return keySet;
    }


    @Override
    public Collection<V> values() {
        if (values == null) {
            values = new ValueCollection();
        }
        return values;
    }


    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        if (entrySet == null) {
            entrySet = new EntrySet();
        }
        return entrySet;
    }

    private class Entry<K, V> implements Map.Entry<K, V> {
        private K key;
        private V value;

        public Entry(K key, V value) {
            if (key == null || value == null) {
                throw new IllegalArgumentException("Entry(): parámetro null...");
            }
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            if (value == null) {
                throw new IllegalArgumentException("setValue(): parámetro null...");
            }

            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 61 * hash + Objects.hashCode(this.key);
            hash = 61 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (this.getClass() != obj.getClass()) {
                return false;
            }

            final Entry other = (Entry) obj;
            if (!Objects.equals(this.key, other.key)) {
                return false;
            }
            if (!Objects.equals(this.value, other.value)) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "(" + key.toString() + ", " + value.toString() + ")";
        }
    }

    private transient Set<K> keySet = null;
    private transient Set<Map.Entry<K, V>> entrySet = null;
    private transient Collection<V> values = null;

    private class KeySet extends AbstractSet<K> {
        @Override
        public Iterator<K> iterator() {
            return new KeySetIterator();
        }

        @Override
        public int size() {
            return TSBHashtableDA.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return TSBHashtableDA.this.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return (TSBHashtableDA.this.remove(o) != null);
        }

        @Override
        public void clear() {
            TSBHashtableDA.this.clear();
        }

        private class KeySetIterator implements Iterator<K> {


            private int last_entry;


            private int current_entry;


            private boolean next_ok;


            private int expected_modCount;


            public KeySetIterator() {
                last_entry = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSBHashtableDA.this.modCount;
            }


            @Override
            public boolean hasNext() {
                Entry<K, V> t[] = TSBHashtableDA.this.table;
                int s[] = TSBHashtableDA.this.states;

                if (current_entry >= t.length) {
                    return false;
                }

                int next_entry = current_entry + 1;
                for (int i = next_entry; i < t.length; i++) {
                    if (s[i] == 1) return true;
                }

                return false;
            }

            @Override
            public K next() {

                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }
                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                Entry<K, V> t[] = TSBHashtableDA.this.table;
                int s[] = TSBHashtableDA.this.states;
                int next_entry = current_entry;
                for (next_entry++; s[next_entry] != 1; next_entry++) ;
                last_entry = current_entry;
                current_entry = next_entry;
                next_ok = true;
                K key = t[current_entry].getKey();
                return key;
            }

            @Override
            public void remove() {
                // control: fail-fast iterator...
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("remove(): modificación inesperada de tabla...");
                }

                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }

                TSBHashtableDA.this.table[current_entry] = null;
                TSBHashtableDA.this.states[current_entry] = 2;
                current_entry = last_entry;
                next_ok = false;
                TSBHashtableDA.this.count--;
                TSBHashtableDA.this.modCount++;
                expected_modCount++;
            }
        }
    }

    private class EntrySet extends AbstractSet<Map.Entry<K, V>> {

        @Override
        public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator();
        }

        @Override
        public boolean contains(Object o) {
            if (o == null) {
                return false;
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> t[] = TSBHashtableDA.this.table;
            int s[] = TSBHashtableDA.this.states;

            Entry<K, V> entry = (Entry<K, V>) o;

            int ih = TSBHashtableDA.this.h(entry.getKey());
            int ic = ih;
            int j = 1;

            while (s[ic] != 0) {
                if (s[ic] == 1) {
                    Entry<K, V> entryTable = t[ic];
                    if (entryTable.equals(entry)) return true;
                }
                ic += j * j;
                j++;
                if (ic >= t.length) {
                    ic %= t.length;
                }
            }
            return false;
        }

        @Override
        public boolean remove(Object o) {
            if (o == null) {
                throw new NullPointerException("remove(): parámetro null");
            }
            if (!(o instanceof Entry)) {
                return false;
            }
            Entry<K, V> t[] = TSBHashtableDA.this.table;
            int s[] = TSBHashtableDA.this.states;
            Entry<K, V> entry = (Entry<K, V>) o;
            int ih = TSBHashtableDA.this.h(entry.getKey());
            int ic = ih;
            int j = 1;

            while (s[ic] != 0) {
                if (s[ic] == 1) {
                    Entry<K, V> entryTable = t[ic];
                    if (entryTable.equals(entry)) {
                        t[ic] = null;
                        s[ic] = 2;

                        TSBHashtableDA.this.count--;
                        TSBHashtableDA.this.modCount++;

                        return true;
                    }
                }
                ic += j * j;
                j++;
                if (ic >= t.length) {
                    ic %= t.length;
                }
            }
            return false;
        }

        @Override
        public int size() {
            return TSBHashtableDA.this.count;
        }

        @Override
        public void clear() {
            TSBHashtableDA.this.clear();
        }

        private class EntrySetIterator implements Iterator<Map.Entry<K, V>> {
            private int last_entry;
            private int current_entry;
            private boolean next_ok;
            private int expected_modCount;

            public EntrySetIterator() {
                last_entry = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSBHashtableDA.this.modCount;
            }

            @Override
            public boolean hasNext() {
                Entry<K, V> t[] = TSBHashtableDA.this.table;
                int s[] = TSBHashtableDA.this.states;

                if (current_entry >= t.length) {
                    return false;
                }
                int next_entry = current_entry + 1;
                for (int i = next_entry; i < t.length; i++) {
                    if (s[i] == 1) return true;
                }
                return false;
            }

            @Override
            public Entry<K, V> next() {
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                Entry<K, V> t[] = TSBHashtableDA.this.table;
                int s[] = TSBHashtableDA.this.states;
                int next_entry = current_entry;
                for (next_entry++; s[next_entry] != 1; next_entry++) ;
                last_entry = current_entry;
                current_entry = next_entry;
                next_ok = true;

                // y retornar el entry alcanzado...
                return t[current_entry];
            }

            @Override
            public void remove() {
                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }
                TSBHashtableDA.this.table[current_entry] = null;
                TSBHashtableDA.this.states[current_entry] = 2;
                current_entry = last_entry;
                next_ok = false;
                TSBHashtableDA.this.count--;
                TSBHashtableDA.this.modCount++;
                expected_modCount++;
            }
        }
    }

    private class ValueCollection extends AbstractCollection<V> {
        @Override
        public Iterator<V> iterator() {
            return new ValueCollectionIterator();
        }

        @Override
        public int size() {
            return TSBHashtableDA.this.count;
        }

        @Override
        public boolean contains(Object o) {
            return TSBHashtableDA.this.containsValue(o);
        }

        @Override
        public void clear() {
            TSBHashtableDA.this.clear();
        }

        private class ValueCollectionIterator implements Iterator<V> {
            private int last_entry;
            private int current_entry;
            private boolean next_ok;
            private int expected_modCount;

            public ValueCollectionIterator() {
                last_entry = 0;
                current_entry = -1;
                next_ok = false;
                expected_modCount = TSBHashtableDA.this.modCount;
            }

            @Override
            public boolean hasNext() {
                Entry<K, V> t[] = TSBHashtableDA.this.table;
                int s[] = TSBHashtableDA.this.states;

                if (current_entry >= t.length) {
                    return false;
                }

                int next_entry = current_entry + 1;
                for (int i = next_entry; i < t.length; i++) {
                    if (s[i] == 1) return true;
                }
                return false;
            }

            @Override
            public V next() {
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("next(): modificación inesperada de tabla...");
                }

                if (!hasNext()) {
                    throw new NoSuchElementException("next(): no existe el elemento pedido...");
                }
                Entry<K, V> t[] = TSBHashtableDA.this.table;
                int s[] = TSBHashtableDA.this.states;
                int next_entry = current_entry;
                for (next_entry++; s[next_entry] != 1; next_entry++) ;
                last_entry = current_entry;
                current_entry = next_entry;
                next_ok = true;
                V value = t[current_entry].getValue();

                return value;
            }

            @Override
            public void remove() {
                // control: fail-fast iterator...
                if (TSBHashtableDA.this.modCount != expected_modCount) {
                    throw new ConcurrentModificationException("remove(): modificación inesperada de tabla...");
                }

                if (!next_ok) {
                    throw new IllegalStateException("remove(): debe invocar a next() antes de remove()...");
                }
                TSBHashtableDA.this.table[current_entry] = null;
                TSBHashtableDA.this.states[current_entry] = 2;
                current_entry = last_entry;
                next_ok = false;
                TSBHashtableDA.this.count--;
                TSBHashtableDA.this.modCount++;
                expected_modCount++;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Map)) {
            return false;
        }

        Map<K, V> t = (Map<K, V>) obj;
        if (t.size() != this.size()) {
            return false;
        }

        try {
            Iterator<Map.Entry<K, V>> i = this.entrySet.iterator();
            while (i.hasNext()) {
                Map.Entry<K, V> e = i.next();
                K key = e.getKey();
                V value = e.getValue();
                if (t.get(key) == null) {
                    return false;
                } else {
                    if (!value.equals(t.get(key))) {
                        return false;
                    }
                }
            }
        } catch (ClassCastException e) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        if (this.isEmpty()) return 0;
        return Arrays.hashCode(this.table);
    }

    @Override
    public String toString() {
        StringBuilder cad = new StringBuilder("");
        cad.append("\nTabla: {\n");
        for (int i = 0; i < this.table.length; i++) {
            if (this.table[i] == null) {
                cad.append("\t()\n");
            } else {
                cad.append("\t").append(this.table[i].toString()).append("\n");
            }
        }
        cad.append("}");
        return cad.toString();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        TSBHashtableDA<K, V> t = new TSBHashtableDA<>(this.table.length, this.load_factor);
        for (Map.Entry<K, V> entry : this.entrySet()) {
            t.put(entry.getKey(), entry.getValue());
        }

        return t;
    }

    protected void rehash() {
        int old_length = this.table.length;
        int new_length = siguientePrimo(old_length * 2 + 1);
        if (new_length > TSBHashtableDA.MAX_SIZE)
            new_length = TSBHashtableDA.MAX_SIZE;
        Entry<K, V> tempTable[] = new Entry[new_length];
        int tempStates[] = new int[new_length];
        for (int i = 0; i < tempStates.length; i++) tempStates[i] = 0;
        this.modCount++;
        for (int i = 0; i < this.table.length; i++) {
            if (this.states[i] == 1) {

                Entry<K, V> x = this.table[i];

                K key = x.getKey();
                int y = this.h(key, tempTable.length);
                int ic = y, j = 1;
                while (tempStates[ic] != 0) {
                    ic += j * j;
                    j++;
                    if (ic >= tempTable.length) {
                        ic %= tempTable.length;
                    }
                }

                tempTable[ic] = x;
                tempStates[ic] = 1;
            }
        }

        this.table = tempTable;
        this.states = tempStates;
    }

    public boolean contains(Object value) {
        if (value == null)
            return false;

        Iterator<Map.Entry<K, V>> it = this.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<K, V> entry = it.next();
            if (value.equals(entry.getValue()))
                return true;
        }

        return false;
    }

    private int h(int k) {
        return h(k, this.table.length);
    }


    private int h(K key) {
        return h(key.hashCode(), this.table.length);
    }

    private int h(K key, int t) {
        return h(key.hashCode(), t);
    }

    private int h(int k, int t) {
        if (k < 0)
            k *= -1;
        return k % t;
    }

    private int siguientePrimo(int n) {
        if (n % 2 == 0) n++;
        for (; !esPrimo(n); n += 2) ;
        return n;
    }

    private boolean esPrimo(int n) {
        for (int i = 3; i < (int) Math.sqrt(n); i += 2) {
            if (n % i == 0) return false;
        }
        return true;
    }
}
