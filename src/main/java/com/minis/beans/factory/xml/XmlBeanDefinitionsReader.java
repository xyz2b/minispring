package com.minis.beans.factory.xml;

import com.minis.beans.factory.support.AbstractBeanFactory;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.core.Resource;
import org.dom4j.Element;

import java.util.ArrayList;
import java.util.List;

public class XmlBeanDefinitionsReader {
    private static final String BEAN_ID = "id";
    private static final String BEAN_CLASS = "class";
    private static final String BEAN_PROPERTY = "property";
    private static final String BEAN_PROPERTY_TYPE = "type";
    private static final String BEAN_PROPERTY_NAME = "name";
    private static final String BEAN_PROPERTY_VALUE = "value";
    private static final String BEAN_PROPERTY_REF= "ref";

    private static final String BEAN_CONSTRUCTOR_ARG = "constructor-arg";

    private AbstractBeanFactory bf;

    public XmlBeanDefinitionsReader(AbstractBeanFactory bf) {
        this.bf = bf;
    }

    public void loadBeanDefinitions(Resource resource) {
        while (resource.hasNext()) {
            Element element = (Element) resource.next();
            String beanID = element.attributeValue(BEAN_ID);
            String beanClassName = element.attributeValue(BEAN_CLASS);
            BeanDefinition beanDefinition = new BeanDefinition(beanID, beanClassName);

            // 处理属性
            List<Element> propertyElements = element.elements(BEAN_PROPERTY);
            PropertyValues PVS = new PropertyValues();
            List<String> refs = new ArrayList<>();
            for (Element e : propertyElements) {
                String pType = e.attributeValue(BEAN_PROPERTY_TYPE);
                String pName = e.attributeValue(BEAN_PROPERTY_NAME);
                String pValue = e.attributeValue(BEAN_PROPERTY_VALUE);
                String pRef = e.attributeValue(BEAN_PROPERTY_REF);
                String pV = "";
                boolean isRef = false;
                if(pValue != null && !pValue.equals("")) {
                    isRef = false;
                    pV = pValue;
                } else if(pRef != null && !pRef.equals("")) {
                    isRef = true;
                    pV = pRef;
                    refs.add(pRef);
                }
                PVS.addPropertyValue(new PropertyValue(pType, pName, pV, isRef));
            }
            beanDefinition.setPropertyValues(PVS);
            String[] refArray = refs.toArray(new String[0]);
            beanDefinition.setDependsOn(refArray);

            // 处理构造器参数
            List<Element> constructorElements = element.elements(BEAN_CONSTRUCTOR_ARG);
            ConstructorArgumentValues AVS = new ConstructorArgumentValues();
            for(Element e : constructorElements) {
                String aType = e.attributeValue(BEAN_PROPERTY_TYPE);
                String aName = e.attributeValue(BEAN_PROPERTY_NAME);
                String aValue = e.attributeValue(BEAN_PROPERTY_VALUE);
                String pRef = e.attributeValue(BEAN_PROPERTY_REF);
                String aV = "";
                boolean isRef = false;
                if(aValue != null && !aValue.equals("")) {
                    isRef = false;
                    aV = aValue;
                } else if(pRef != null && !pRef.equals("")) {
                    isRef = true;
                    aV = pRef;
                    refs.add(pRef);
                }
                AVS.addArgumentValue(new ConstructorArgumentValue(aType, aName, aV, isRef));
            }
            beanDefinition.setConstructorArgumentValues(AVS);

            this.bf.registerBeanDefinition(beanID, beanDefinition);
        }
    }
}
