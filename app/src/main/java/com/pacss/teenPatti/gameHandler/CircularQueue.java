package com.pacss.teenPatti.gameHandler;

import com.pacss.teenPatti.dataHandler.FirebaseManager;

public class CircularQueue {

    private String[] Object;
    private int queueSize;
    private int startPosition;
    private int currentPosition;
    private int endPosition;
    private int pointer = 0;
    private int previousToMe = 0;
    private int nextToMe = 0;

    public String[] returnString() {
        return Object;
    }

    public CircularQueue(final int queueSize) {
        Object = new String[queueSize];
        for (int i = 0; i < queueSize; i++) {
            Object[i] = "";
        }
        this.queueSize = queueSize;
        startPosition = -1;
        currentPosition = -1;
        endPosition = -1;
    }

    public void addListData(final String[] stringList) {
        for (String s : stringList) {
            add(s);
        }
    }

    private void add(final String addData) {
        if (currentPosition == -1) {
            startPosition = 0;
            currentPosition = 0;
            endPosition = 0;
            Object[currentPosition] = addData;
        } else if (!isFull()) {
            currentPosition++;
            Object[currentPosition] = addData;
            endPosition = currentPosition;
        }
    }

    private void remove() {
        if (startPosition == endPosition && startPosition != -1) {
            Object[startPosition] = "";
            startPosition = -1;
            currentPosition = -1;
            endPosition = -1;
        } else if (startPosition == (queueSize - 1)) {
            Object[startPosition] = "";
            startPosition = 0;
        } else if (!isEmpty()) {
            Object[startPosition] = "";
            startPosition++;
        }
    }

    private boolean isFull() {
        if (startPosition == 0 && endPosition == (queueSize - 1)) {
            return true;
        } else if ((currentPosition + 1) == startPosition) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isEmpty() {
        if (currentPosition == -1) {
            return true;
        } else {
            return false;
        }
    }

    String getStart() {
        return Object[startPosition];
    }

    String getNextToStart() {
        return Object[startPosition + 1];
    }

    public int setPointer() {
        for (int i = 0; i < queueSize; i++) {
            if (Object[i].equals(FirebaseManager.UserID)) {
                pointer = i;
                break;
            }
        }
        if (pointer == (queueSize - 1)) {
            nextToMe = 0;
        } else {
            nextToMe = pointer + 1;
        }
        if (pointer == 0) {
            previousToMe = queueSize - 1;
        } else {
            previousToMe = pointer - 1;
        }
        return pointer;
    }

    void setPointer(int pointer) {
        this.pointer = pointer;
    }

    String get() {
        String returnableObject;
        if (pointer == queueSize - 1) {
            returnableObject = Object[pointer];
            pointer = 0;
        } else {
            returnableObject = Object[pointer];
            pointer++;
        }
        return returnableObject;
    }

    public String nextToMe() {
        return Object[nextToMe];
    }

    String previousToMe() {
        return Object[previousToMe];
    }
}
