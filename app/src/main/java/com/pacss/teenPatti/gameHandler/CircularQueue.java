package com.pacss.teenPatti.gameHandler;

public class CircularQueue {

    private String[] Object;
    private int queueSize;
    private int startPosition;
    private int currentPosition;
    private int endPosition;

    public CircularQueue(final int queueSize) {
        Object = new String[queueSize];
        this.queueSize = queueSize;
        startPosition = -1;
        currentPosition = -1;
        endPosition = -1;
    }

    public boolean add(final String addData) {
        if (currentPosition == -1) {
            startPosition = 0;
            currentPosition = 0;
            endPosition = 0;
            Object[currentPosition] = addData;
            return true;
        } else if (!isFull()) {
            currentPosition++;
            Object[currentPosition] = addData;
            endPosition = currentPosition;
            return true;
        } else {
            return false;
        }
    }

    public boolean remove() {
        if (startPosition == endPosition && startPosition != -1) {
            Object[startPosition] = "";
            startPosition = -1;
            currentPosition = -1;
            endPosition = -1;
            return true;
        } else if (startPosition == (queueSize - 1)) {
            Object[startPosition] = "";
            startPosition = 0;
            return true;
        } else if (!isEmpty()) {
            Object[startPosition] = "";
            startPosition++;
            return true;
        } else {
            return false;
        }
    }

    public boolean isFull() {
        if (startPosition == 0 && endPosition == (queueSize - 1)) {
            return true;
        } else if ((currentPosition + 1) == startPosition) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isEmpty() {
        if (currentPosition == -1) {
            return true;
        } else {
            return false;
        }
    }

    public int getQueueSize() {
        return queueSize;
    }

    public int getQueueLength() {
        return ((endPosition - startPosition) + 1);
    }
}
