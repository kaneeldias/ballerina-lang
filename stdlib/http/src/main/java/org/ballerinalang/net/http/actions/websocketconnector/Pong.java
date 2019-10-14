/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ballerinalang.net.http.actions.websocketconnector;

import io.netty.channel.ChannelFuture;
import org.ballerinalang.jvm.scheduling.Strand;
import org.ballerinalang.jvm.values.ArrayValue;
import org.ballerinalang.jvm.values.ObjectValue;
import org.ballerinalang.jvm.values.connector.NonBlockingCallback;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.net.http.WebSocketConstants;
import org.ballerinalang.net.http.WebSocketObservabilityUtil;
import org.ballerinalang.net.http.WebSocketOpenConnectionInfo;
import org.ballerinalang.net.http.WebSocketUtil;
import org.ballerinalang.net.http.exception.WebSocketException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

import static org.ballerinalang.net.http.WebSocketConstants.ErrorCode.WsConnectionError;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.WEBSOCKET_ERROR_TYPE_MESSAGE_SENT;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.WEBSOCKET_MESSAGE_RESULT_SUCCESS;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.WEBSOCKET_MESSAGE_TYPE_CONTROL;

/**
 * {@code Get} is the GET action implementation of the HTTP Connector.
 */
@BallerinaFunction(
        orgName = WebSocketConstants.BALLERINA_ORG,
        packageName = WebSocketConstants.PACKAGE_HTTP,
        functionName = "pong",
        receiver = @Receiver(
                type = TypeKind.OBJECT,
                structType = WebSocketConstants.WEBSOCKET_CONNECTOR,
                structPackage = WebSocketConstants.FULL_PACKAGE_HTTP
        )
)
public class Pong {
    private static final Logger log = LoggerFactory.getLogger(Pong.class);

    public static Object pong(Strand strand, ObjectValue wsConnection, ArrayValue binaryData) {
        NonBlockingCallback callback = new NonBlockingCallback(strand);

        try {
            WebSocketOpenConnectionInfo connectionInfo = (WebSocketOpenConnectionInfo) wsConnection
                    .getNativeData(WebSocketConstants.NATIVE_DATA_WEBSOCKET_CONNECTION_INFO);
            ChannelFuture future = connectionInfo.getWebSocketConnection().pong(ByteBuffer.wrap(binaryData.getBytes()));
            WebSocketUtil.handleWebSocketCallback(callback, future, log);

            //Observe pong message sent
            WebSocketObservabilityUtil.observePush(WEBSOCKET_MESSAGE_TYPE_CONTROL, WEBSOCKET_MESSAGE_RESULT_SUCCESS,
                                                   connectionInfo);
        } catch (Exception e) {
            callback.setReturnValues(new WebSocketException(WsConnectionError, e.getMessage()));
            callback.notifySuccess();

            //Observe error when sending pong message
            WebSocketObservabilityUtil.observeError((WebSocketOpenConnectionInfo) wsConnection
                                 .getNativeData(WebSocketConstants.NATIVE_DATA_WEBSOCKET_CONNECTION_INFO),
                                                    WEBSOCKET_ERROR_TYPE_MESSAGE_SENT, WEBSOCKET_MESSAGE_TYPE_CONTROL,
                                                    e.getMessage());
        }
        return null;
    }

    private Pong() {
    }
}
