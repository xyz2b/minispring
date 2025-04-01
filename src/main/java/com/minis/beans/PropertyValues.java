package com.minis.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PropertyValues {
    private final List<PropertyValue> propertyValueList;

    public PropertyValues() {
        this.propertyValueList = new ArrayList<PropertyValue>(10);
    }

    public PropertyValues(Map<String, Object> map) {
        this.propertyValueList = new ArrayList<>(map.size());
        for(Map.Entry<String, Object> e : map.entrySet()) {
            PropertyValue pv = new PropertyValue(e.getKey(), e.getValue());
            this.propertyValueList.add(pv);
        }
    }

    public List<PropertyValue> getPropertyValueList() {
        return propertyValueList;
    }

    public int size() {
        return propertyValueList.size();
    }

    public void addPropertyValue(PropertyValue pv) {
        propertyValueList.add(pv);
    }

    public void addPropertyValue(String propertyType, String propertyName, Object propertyValue, boolean isRef) {
        addPropertyValue(new PropertyValue(propertyType, propertyName, propertyValue, isRef));
    }

    public void removePropertyValue(PropertyValue pv) {
        propertyValueList.remove(pv);
    }

    public PropertyValue[] getPropertyValues() {
        return propertyValueList.toArray(new PropertyValue[propertyValueList.size()]);
    }

    public PropertyValue getPropertyValue(String propertyName) {
        for(PropertyValue pv : propertyValueList) {
            if(pv.getName().equals(propertyName)) {
                return pv;
            }
        }
        return null;
    }

    public Object get(String propertyName) {
        PropertyValue pv = getPropertyValue(propertyName);
        return pv != null ? pv.getValue() : null;
    }

    public boolean contains(String propertyName) {
        return getPropertyValue(propertyName) != null;
    }

    public boolean isEmpty() {
        return propertyValueList.isEmpty();
    }
}
