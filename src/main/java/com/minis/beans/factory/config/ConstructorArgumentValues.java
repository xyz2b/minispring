package com.minis.beans.factory.config;

import java.util.*;

public class ConstructorArgumentValues {
    private final List<ConstructorArgumentValue> constructorArgumentValueList = new LinkedList<>();

    public void addArgumentValue(ConstructorArgumentValue constructorArgumentValue) {
        constructorArgumentValueList.add(constructorArgumentValue);
    }

    public ConstructorArgumentValue getIndexedArgumentValue(int index) {
        ConstructorArgumentValue constructorArgumentValue = constructorArgumentValueList.get(index);
        return constructorArgumentValue;
    }

    public int getArgumentCount() {
        return constructorArgumentValueList.size();
    }

    public boolean isEmpty() {
        return constructorArgumentValueList.isEmpty();
    }
}
