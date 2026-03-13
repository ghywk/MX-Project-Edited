package kireiko.dev.anticheat.utils.container;

/**
 * @author kevin
 * @since 2026/3/7
 */

public class CircularBuffer {
    private final float[] buffer;
    private final int capacity;
    private int head = 0;  // 下一个写入位置
    private int size = 0;  // 当前有效数据量

    public CircularBuffer(int capacity) {
        this.capacity = capacity;
        this.buffer = new float[capacity];
    }

    public void add(float value) {
        buffer[head] = value;
        head = (head + 1) % capacity;
        if (size < capacity) size++;
    }

    public float getLatest(int index) {
        if (index >= size) throw new IndexOutOfBoundsException();
        // 从head-1往前数
        int pos = (head - 1 - index + capacity) % capacity;
        return buffer[pos];
    }

    public float[] getAllLatest() {
        float[] result = new float[size];
        for (int i = 0; i < size; i++) {
            result[i] = getLatest(i);
        }
        return result;
    }

    public int size() { return size; }
    public boolean isFull() { return size == capacity; }
    public void clear() { size = 0; head = 0; }
}
