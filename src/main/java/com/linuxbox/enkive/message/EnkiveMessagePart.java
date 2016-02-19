package com.linuxbox.enkive.message;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;

/**
 *
 */
@Data
@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.WRAPPER_OBJECT, property="type")
@JsonSubTypes({

        @JsonSubTypes.Type(value=EnkiveMessagePartMulti.class, name="multi"),

        @JsonSubTypes.Type(value=EnkiveMessagePartSingle.class, name="single")

})
public abstract class EnkiveMessagePart {

    public abstract String reconstructPart();

}
