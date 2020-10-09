/*
 *  Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
package io.ballerina.jvm.internal;

import io.ballerina.jvm.TypeChecker;
import io.ballerina.jvm.api.BStringUtils;
import io.ballerina.jvm.api.Types;
import io.ballerina.jvm.api.types.Type;
import io.ballerina.jvm.api.values.BError;
import io.ballerina.jvm.api.values.BMap;
import io.ballerina.jvm.api.values.BString;
import io.ballerina.jvm.util.exceptions.BLangExceptionHelper;
import io.ballerina.jvm.util.exceptions.BallerinaErrorReasons;
import io.ballerina.jvm.util.exceptions.RuntimeErrors;
import io.ballerina.jvm.values.ErrorValue;
import io.ballerina.jvm.values.MapValueImpl;

import static io.ballerina.jvm.api.BErrorCreator.createError;
import static io.ballerina.jvm.util.exceptions.BallerinaErrorReasons.BALLERINA_PREFIXED_CONVERSION_ERROR;
import static io.ballerina.jvm.util.exceptions.RuntimeErrors.INCOMPATIBLE_CONVERT_OPERATION;

/**
 * This class contains internal methods used by codegen and runtime classes to handle errors.
 *
 * @since 2.0.0
 */

public class ErrorUtils {

    private static final BString ERROR_MESSAGE_FIELD = BStringUtils.fromString("message");
    private static final BString ERROR_CAUSE_FIELD = BStringUtils.fromString("cause");
    private static final BString NULL_REF_EXCEPTION = BStringUtils.fromString("NullReferenceException");


    /**
     * Create balleria error using java exception for interop.
     *
     * @param e java exception
     * @return ballerina error
     */
    public static ErrorValue createInteropError(Throwable e) {
        BMap<BString, Object> detailMap = new MapValueImpl<>(Types.TYPE_ERROR_DETAIL);
        if (e.getMessage() != null) {
            detailMap.put(ERROR_MESSAGE_FIELD, BStringUtils.fromString(e.getMessage()));
        }
        if (e.getCause() != null) {
            detailMap.put(ERROR_CAUSE_FIELD, createError(BStringUtils.fromString(e.getCause().getClass().getName()),
                                                         BStringUtils.fromString(e.getCause().getMessage())));
        }

        return (ErrorValue) createError(BStringUtils.fromString(e.getClass().getName()), detailMap);
    }

    public static Object handleResourceError(Object returnValue) {
        if (returnValue instanceof BError) {
            throw (BError) returnValue;
        }
        return returnValue;
    }

    public static ErrorValue trapError(Throwable throwable) {
        // Used to trap and create error value for non error value exceptions. At the moment, we can trap
        // stack overflow exceptions in addition to error value.
        // In the future, if we need to trap more exception types, we need to check instance of each exception and
        // handle accordingly.
        BError error = createError(BallerinaErrorReasons.STACK_OVERFLOW_ERROR);
        error.setStackTrace(throwable.getStackTrace());
        return (ErrorValue) error;
    }

    public static ErrorValue createCancelledFutureError() {
        return (ErrorValue) createError(BallerinaErrorReasons.FUTURE_CANCELLED);
    }

    public static BError createConversionError(Object inputValue, Type targetType) {
        return createError(BALLERINA_PREFIXED_CONVERSION_ERROR,
                           BLangExceptionHelper.getErrorMessage(INCOMPATIBLE_CONVERT_OPERATION,
                                                                TypeChecker.getType(inputValue), targetType));
    }

    public static BError createTypeCastError(Object sourceVal, Type targetType) {
        throw createError(BallerinaErrorReasons.TYPE_CAST_ERROR,
                          BLangExceptionHelper.getErrorMessage(RuntimeErrors.TYPE_CAST_ERROR,
                                                               TypeChecker.getType(sourceVal), targetType));

    }

    public static BError createBToJTypeCastError(Object sourceVal, String targetType) {
        throw createError(BallerinaErrorReasons.TYPE_CAST_ERROR,
                          BLangExceptionHelper.getErrorMessage(RuntimeErrors.J_TYPE_CAST_ERROR,
                                                               TypeChecker.getType(sourceVal), targetType));
    }

    public static BError createNumericConversionError(Object inputValue, Type targetType) {
        throw createError(BallerinaErrorReasons.NUMBER_CONVERSION_ERROR,
                          BLangExceptionHelper.getErrorMessage(
                                  RuntimeErrors.INCOMPATIBLE_SIMPLE_TYPE_CONVERT_OPERATION,
                                  TypeChecker.getType(inputValue), inputValue, targetType));
    }

    public static BError createNumericConversionError(Object inputValue, Type inputType, Type targetType) {
        throw createError(BallerinaErrorReasons.NUMBER_CONVERSION_ERROR, BLangExceptionHelper.getErrorMessage(
                RuntimeErrors.INCOMPATIBLE_SIMPLE_TYPE_CONVERT_OPERATION, inputType, inputValue, targetType));
    }

    public static BError createNullReferenceError() {
        return createError(NULL_REF_EXCEPTION);
    }
}

