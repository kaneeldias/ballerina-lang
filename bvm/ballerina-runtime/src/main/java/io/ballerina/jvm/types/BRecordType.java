/*
 *  Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package io.ballerina.jvm.types;

import io.ballerina.jvm.api.BStringUtils;
import io.ballerina.jvm.api.BValueCreator;
import io.ballerina.jvm.api.TypeFlags;
import io.ballerina.jvm.api.TypeTags;
import io.ballerina.jvm.api.runtime.Module;
import io.ballerina.jvm.api.types.IntersectionType;
import io.ballerina.jvm.api.types.RecordType;
import io.ballerina.jvm.api.types.Type;
import io.ballerina.jvm.api.values.BString;
import io.ballerina.jvm.util.Flags;
import io.ballerina.jvm.values.MapValue;
import io.ballerina.jvm.values.MapValueImpl;

import java.util.Map;

/**
 * {@code BRecordType} represents a user defined record type in Ballerina.
 *
 * @since 0.995.0
 */
public class BRecordType extends BStructureType implements RecordType {

    public boolean sealed;
    public Type restFieldType;
    public int typeFlags;
    private final boolean readonly;
    private IntersectionType immutableType;

    /**
     * Create a {@code BRecordType} which represents the user defined record type.
     *
     * @param typeName string name of the record type
     * @param pkg package of the record type
     * @param flags of the record type
     * @param sealed flag indicating the sealed status
     * @param typeFlags flags associated with the type
     */
    public BRecordType(String typeName, Module pkg, int flags, boolean sealed, int typeFlags) {
        super(typeName, pkg, flags, MapValueImpl.class);
        this.sealed = sealed;
        this.typeFlags = typeFlags;
        this.readonly = Flags.isFlagOn(flags, Flags.READONLY);
    }

    /**
     * Create a {@code BRecordType} which represents the user defined record type.
     *
     * @param typeName string name of the record type
     * @param pkg package of the record type
     * @param flags of the record type
     * @param fields record fields
     * @param restFieldType type of the rest field
     * @param sealed flag to indicate whether the record is sealed
     * @param typeFlags flags associated with the type
     */
    public BRecordType(String typeName, Module pkg, int flags, Map<String, BField> fields, Type restFieldType,
                       boolean sealed, int typeFlags) {
        super(typeName, pkg, flags, MapValueImpl.class, fields);
        this.restFieldType = restFieldType;
        this.sealed = sealed;
        this.typeFlags = typeFlags;
        this.readonly = Flags.isFlagOn(flags, Flags.READONLY);
    }

    @Override
    public <V extends Object> V getZeroValue() {
        return (V) BValueCreator.createRecordValue(this.pkg, this.typeName);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V extends Object> V getEmptyValue() {
        MapValue<BString, Object> implicitInitValue = new MapValueImpl<>(this);
        this.fields.entrySet().stream()
                .filter(entry -> !Flags.isFlagOn(entry.getValue().flags, Flags.OPTIONAL))
                .forEach(entry -> {
                    Object value = entry.getValue().getFieldType().getEmptyValue();
                    implicitInitValue.put(BStringUtils.fromString(entry.getKey()), value);
                });
        return (V) implicitInitValue;
    }

    @Override
    public int getTag() {
        return TypeTags.RECORD_TYPE_TAG;
    }

    @Override
    public String getAnnotationKey() {
        return this.typeName;
    }

    @Override
    public boolean isAnydata() {
        return TypeFlags.isFlagOn(this.typeFlags, TypeFlags.ANYDATA);
    }

    @Override
    public boolean isPureType() {
        return TypeFlags.isFlagOn(this.typeFlags, TypeFlags.PURETYPE);
    }

    @Override
    public boolean isReadOnly() {
        return this.readonly;
    }

    @Override
    public Type getImmutableType() {
        return this.immutableType;
    }

    @Override
    public void setImmutableType(IntersectionType immutableType) {
        this.immutableType = immutableType;
    }
}
