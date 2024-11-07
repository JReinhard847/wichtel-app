package org.example.backend.util;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.util.HashSet;
import java.util.Set;

public class UpdateUtil {

    private UpdateUtil(){}


    public static void updateIgnoringNulls(Object update,Object original){
        final BeanWrapper src = new BeanWrapperImpl(update);
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> nullAttributes = new HashSet<>();
        for (PropertyDescriptor pd : pds) {
            Object srcValue = src.getPropertyValue(pd.getName());
            if (srcValue == null) nullAttributes.add(pd.getName());
        }
        BeanUtils.copyProperties(update,original,nullAttributes.toArray(new String[0]));
    }
}
