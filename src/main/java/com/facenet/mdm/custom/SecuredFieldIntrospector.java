package com.facenet.mdm.custom;

import com.facenet.mdm.anotation.SecuredField;
import com.facenet.mdm.security.SecurityUtils;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;

public class SecuredFieldIntrospector extends JacksonAnnotationIntrospector {

    @Override
    public boolean hasIgnoreMarker(AnnotatedMember m) {
        SecuredField securedField = _findAnnotation(m, SecuredField.class);
        if (securedField != null) {
            if (!SecurityUtils.hasCurrentUserAnyOfAuthorities(securedField.value())) {
                return true;
            }
        }
        return _isIgnorable(m);
    }
}
