/*
 * Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.http;

/**
 * Providing observability functionality to WebSockets.
 *
 * @since 1.1.0
 */
public class WebSocketObservabilityConstants {
    static final String TAG_CONNECTION_ID = "connectionID";
    static final String TAG_KEY_RESULT = "result";
    static final String TAG_CLIENT_OR_SERVER = "client_or_server";
    static final String TAG_SERVICE = "service";
    static final String TAG_MESSAGE_TYPE = "type";
    static final String TAG_ERROR_TYPE = "error_type";

    static final String METRIC_REQUESTS = "requests";
    static final String METRIC_REQUESTS_DESC = "Number of WebSocket connection requests";

    static final String METRIC_CONNECTIONS = "connections";
    static final String METRIC_CONNECTIONS_DESC = "Number of currently active connections";

    static final String METRIC_MESSAGES_RECEIVED = "messages_received";
    static final String METRIC_MESSAGES_RECEIVED_DESC = "Number of messages received";

    static final String METRIC_MESSAGES_SENT = "messages_sent";
    static final String METRIC_MESSAGES_SENT_DESC = "Number of messages sent";

    public static final String MESSAGE_RESULT_SUCCESS = "success";

    static final String METRIC_ERRORS = "errors";
    static final String METRIC_ERRORS_DESC = "Number of errors";

    static final String CLIENT_OR_SERVER_CLIENT = "client";
    static final String CLIENT_OR_SERVER_SERVER = "server";

    public static final String MESSAGE_TYPE_TEXT = "text";
    public static final String MESSAGE_TYPE_BINARY = "binary";
    public static final String MESSAGE_TYPE_CONTROL = "control";
    public static final String MESSAGE_TYPE_CLOSE = "close";

    static final String ERROR_TYPE_CONNECTION = "connection";
    public static final String ERROR_TYPE_CLOSE = "close";
    public static final String ERROR_TYPE_MESSAGE_SENT = "message_sent";
    static final String ERROR_TYPE_MESSAGE_RECEIVED = "message_received";
    static final String ERROR_TYPE_UNEXPECTED = "unexpected";

    static final String UNKNOWN = "unknown";

    private WebSocketObservabilityConstants(){
    }
}
