package com.clusterfactions.clustercore.util.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.clusterfactions.clustercore.persistence.serialization.VariableSerializer;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AlternateSerializable {
	Class<? extends VariableSerializer> value();
}
