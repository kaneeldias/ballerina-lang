/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.ballerina.jvm.values;

import io.ballerina.jvm.api.Types;
import io.ballerina.jvm.api.types.Type;
import io.ballerina.jvm.api.values.BIterator;
import io.ballerina.jvm.api.values.BLink;

import java.util.Map;

/**
 * <p>
 * Represents an iterator of a Ballerina {@code {@link CollectionValue}}.
 * </p>
 * <p>
 * <i>Note: This is an internal API and may change in future versions.</i>
 * </p>
 *  
 * @since 0.995.0
 */
public interface IteratorValue extends RefValue, BIterator {

    /* Default implementation */

    @Override
    default Type getType() {
        return Types.TYPE_ITERATOR;
    }

    @Override
    default String stringValue(BLink parent) {
        return "iterator " + getType().toString();
    }

    @Override
    default String expressionStringValue(BLink parent) {
        return stringValue(parent);
    }

    @Override
    default Object copy(Map<Object, Object> refs) {
        throw new UnsupportedOperationException();
    }

    @Override
    default Object frozenCopy(Map<Object, Object> refs) {
        throw new UnsupportedOperationException();
    }
}
