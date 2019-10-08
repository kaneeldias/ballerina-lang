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

import org.ballerinalang.jvm.observability.ObserveUtils;
import org.ballerinalang.jvm.observability.ObserverContext;
import org.ballerinalang.jvm.observability.metrics.DefaultMetricRegistry;
import org.ballerinalang.jvm.observability.metrics.MetricId;
import org.ballerinalang.jvm.observability.metrics.MetricRegistry;
import org.ballerinalang.jvm.observability.metrics.Tag;
import org.ballerinalang.jvm.observability.metrics.Tags;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.ballerinalang.jvm.observability.ObservabilityConstants.SERVER_CONNECTOR_WEBSOCKET;


/**
 * Providing observability functionality to WebSockets.
 *
 * @since 1.1.0
 */
public class WebSocketObservability {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketObservability.class);

    //Observability Constants
    private static final String TAG_CONNECTION_ID = "connectionID";
    private static final String TAG_KEY_RESULT = "result";
    private static final String TAG_CLIENT_OR_SERVER = "client_or_server";
    private static final String TAG_SERVICE = "service";
    private static final String TAG_MESSAGE_TYPE = "type";
    private static final String TAG_ERROR_TYPE = "error_type";

    private static final String METRIC_REQUESTS = "requests";
    private static final String METRIC_REQUESTS_DESC = "Number of WebSocket connection requests";

    private static final String METRIC_CONNECTIONS = "connections";
    private static final String METRIC_CONNECTIONS_DESC = "Number of currently active connections";

    private static final String METRIC_MESSAGES_RECEIVED = "messages_received";
    private static final String METRIC_MESSAGES_RECEIVED_DESC = "Number of messages received";

    private static final String METRIC_MESSAGES_SENT = "messages_sent";
    private static final String METRIC_MESSAGES_SENT_DESC = "Number of messages sent";

    public static final String WEBSOCKET_MESSAGE_RESULT_SUCCESS = "success";
    static final String WEBSOCKET_MESSAGE_RESULT_FAILED = "failed";

    private static final String METRIC_ERRORS = "errors";
    private static final String METRIC_ERRORS_DESC = "Number of errors";

    private static final String WEBSOCKET_CLIENT_OR_SERVER_CLIENT = "client";
    private static final String WEBSOCKET_CLIENT_OR_SERVER_SERVER = "server";

    public static final String WEBSOCKET_MESSAGE_TYPE_TEXT = "text";
    public static final String WEBSOCKET_MESSAGE_TYPE_BINARY = "binary";
    public static final String WEBSOCKET_MESSAGE_TYPE_CONTROL = "control";
    public static final String WEBSOCKET_MESSAGE_TYPE_CLOSE = "close";

    static final String WEBSOCKET_ERROR_TYPE_CONNECTION = "connection";
    public static final String WEBSOCKET_ERROR_TYPE_CLOSE = "close";
    public static final String WEBSOCKET_ERROR_TYPE_MESSAGE_SENT = "message_sent";
    static final String WEBSOCKET_ERROR_TYPE_MESSAGE_RECEIVED = "message_received";

    private static final String WEBSOCKET_UNKNOWN = "unknown";

    /**
     * This method observes all incoming WebSocket connection requests.
     *
     * @param connectionInfo information regarding connection.
     * @param result whether the connection was successful or not.
     */
    static void observeRequest(WebSocketOpenConnectionInfo connectionInfo, String result) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = new ObserverContext();
            observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);
            try {
                observerContext.addTag(TAG_CONNECTION_ID, connectionInfo.getWebSocketConnection().getChannelId());
            } catch (IllegalAccessException e) {
                observerContext.addTag(TAG_CONNECTION_ID, WEBSOCKET_UNKNOWN);
            }

            setObserveService(observerContext, connectionInfo);
            observerContext.addTag(TAG_KEY_RESULT, result);
            Map<String, String> tags = observerContext.getTags();
            Set<Tag> allTags = new HashSet<>(tags.size());
            Tags.tags(allTags, observerContext.getTags());

            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();

            //Increment requests metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_REQUESTS,
                                                METRIC_REQUESTS_DESC, allTags)).increment();

            //Log request
            logger.info("WS connection request received");

        }
    }

    /**
     * This method observes successful WebSocket connections.
     *
     * @param connectionInfo information regarding connection.
     */
    static void observeConnection(WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {

            ObserverContext observerContext = new ObserverContext();
            observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);

            try {
                observerContext.addTag(TAG_CONNECTION_ID,
                                       connectionInfo.getWebSocketConnection().getChannelId());
            } catch (IllegalAccessException e) {
                observerContext.addTag(TAG_CONNECTION_ID, WEBSOCKET_UNKNOWN);
            }
            setObserveService(observerContext, connectionInfo);

            Map<String, String> tags = observerContext.getTags();
            Set<Tag> allTags = new HashSet<>(tags.size());
            Tags.tags(allTags, observerContext.getTags());

            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();

            //Increment current connections metric
            metricRegistry.gauge(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_CONNECTIONS,
                                              METRIC_CONNECTIONS_DESC, allTags)).increment();

            //Log connection
            try {
                logger.info("WS new connection established. connectionID: {}, service: {}",
                            connectionInfo.getWebSocketConnection().getChannelId(),
                            tags.get(TAG_SERVICE));
            } catch (IllegalAccessException e) {
                logger.info("WS new connection established. connectionID: {}, service: {}",
                            WEBSOCKET_UNKNOWN,
                            tags.get(TAG_SERVICE));
            }


        }
    }

    /**
     * This method observes messages pushed (sent).
     *
     * @param type type of message pushed (text, binary, control, close).
     * @param result whether the push was successful or not.
     * @param connectionInfo information regarding connection.
     */
    public static void observePush(String type, String result, WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {

            ObserverContext observerContext = new ObserverContext();
            observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);

            //Define type of message (text, binary, control, clsoe) and result (successful, failed)
            observerContext.addTag(TAG_MESSAGE_TYPE, type);
            observerContext.addTag(TAG_KEY_RESULT, result);

            try {
                observerContext.addTag(TAG_CONNECTION_ID, connectionInfo.getWebSocketConnection().getChannelId());
            } catch (IllegalAccessException e) {
                observerContext.addTag(TAG_CONNECTION_ID, WEBSOCKET_UNKNOWN);
            }
            setObserveService(observerContext, connectionInfo);


            Map<String, String> tags = observerContext.getTags();
            Set<Tag> allTags = new HashSet<>(tags.size());
            Tags.tags(allTags, observerContext.getTags());

            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();

            //Increment message sent metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_MESSAGES_SENT,
                                                METRIC_MESSAGES_SENT_DESC, allTags)).increment();

            //Log message sent
            try {
                logger.info("WS message sent. connectionID: {}, service: {}, type: {}",
                            connectionInfo.getWebSocketConnection().getChannelId(),
                            tags.get(TAG_SERVICE),
                            type);
            } catch (IllegalAccessException e) {
                logger.info("WS message sent. connectionID: {}, service: {}, type: {}",
                            WEBSOCKET_UNKNOWN,
                            tags.get(TAG_SERVICE),
                            type);
            }

        }
    }

    /**
     * This method observes messages received.
     *
     * @param type type of message pushed (text, binary, control, close).
     * @param connectionInfo information regarding connection.
     */
    static void observeOnMessage(String type, WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = new ObserverContext();
            observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);

            //Define type of message (text, binary, control, close)
            observerContext.addTag(TAG_MESSAGE_TYPE, type);

            try {
                observerContext.addTag(TAG_CONNECTION_ID, connectionInfo.getWebSocketConnection().getChannelId());
            } catch (IllegalAccessException e) {
                observerContext.addTag(TAG_CONNECTION_ID, WEBSOCKET_UNKNOWN);
            }
            setObserveService(observerContext, connectionInfo);

            Map<String, String> tags = observerContext.getTags();
            Set<Tag> allTags = new HashSet<>(tags.size());
            Tags.tags(allTags, observerContext.getTags());

            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();

            //Increment messages received metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_MESSAGES_RECEIVED,
                                                METRIC_MESSAGES_RECEIVED_DESC, allTags)).increment();

            //Log message received
            try {
                logger.info("WS message received. connectionID: {}, service: {}, type:{}",
                            connectionInfo.getWebSocketConnection().getChannelId(),
                            tags.get(TAG_SERVICE),
                            type);
            } catch (IllegalAccessException e) {
                logger.info("WS message received. connectionID: {}, service: {}, type:{}",
                            WEBSOCKET_UNKNOWN,
                            tags.get(TAG_SERVICE),
                            type);
            }


        }
    }

    /**
     * This method observes WebSocket connection closures.
     *
     * @param connectionInfo information regarding connection.
     */
    static void observeClose(WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = new ObserverContext();
            observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);

            try {
                observerContext.addTag(TAG_CONNECTION_ID, connectionInfo.getWebSocketConnection().getChannelId());
            } catch (IllegalAccessException e) {
                observerContext.addTag(TAG_CONNECTION_ID, WEBSOCKET_UNKNOWN);
            }

            setObserveService(observerContext, connectionInfo);

            Map<String, String> tags = observerContext.getTags();
            Set<Tag> allTags = new HashSet<>(tags.size());
            Tags.tags(allTags, observerContext.getTags());

            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();

            //Decrement current connections metric
            metricRegistry.gauge(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_CONNECTIONS,
                                              METRIC_CONNECTIONS_DESC, allTags)).decrement();

            //Log connection closure
            try {
                logger.info("WS connection closed. connectionID: {}, service: {}",
                            connectionInfo.getWebSocketConnection().getChannelId(),
                            tags.get(TAG_SERVICE));
            } catch (IllegalAccessException e) {
                logger.info("WS connection closed. connectionID: {}, service: {}",
                            WEBSOCKET_UNKNOWN,
                            tags.get(TAG_SERVICE));
            }

        }
    }

    /**
     * This method observes WebSocket errors where the errorType is not related to a message being sent or received,
     * or the type of the message is unknown.
     *
     * @param connectionInfo information regarding connection.
     * @param errorType type of error (connection, closure, message sent/received).
     */
    public static void observeError(WebSocketOpenConnectionInfo connectionInfo, String errorType) {
        observeError(connectionInfo, errorType, null);
    }

    /**
     * This method observes WebSocket errors where the errorType is related to a message being sent or received.
     * and the type of the message is known.
     *
     * @param connectionInfo information regarding connection.
     * @param errorType type of error (connection, closure, message sent/received).
     * @param messageType type of message (text, binary, control, close).
     */
    public static void observeError(WebSocketOpenConnectionInfo connectionInfo, String errorType, String messageType) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = new ObserverContext();
            observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);

            try {
                observerContext.addTag(TAG_CONNECTION_ID, connectionInfo.getWebSocketConnection().getChannelId());
            } catch (IllegalAccessException e) {
                observerContext.addTag(TAG_CONNECTION_ID, WEBSOCKET_UNKNOWN);
            }

            setObserveService(observerContext, connectionInfo);
            observerContext.addTag(TAG_ERROR_TYPE, errorType);

            //If the error is related to sending/receiving a message, set the type of message
            if (messageType != null) {
                observerContext.addTag(TAG_MESSAGE_TYPE, messageType);
            }

            Map<String, String> tags = observerContext.getTags();
            Set<Tag> allTags = new HashSet<>(tags.size());
            Tags.tags(allTags, observerContext.getTags());

            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_ERRORS,
                                                METRIC_ERRORS_DESC, allTags)).increment();

            //Log error
            //TODO: Necessary? Should be handled where error occurs?

        }
    }

    /**
     * This method determines whether the current context is server or client and sets the tags accordingly.
     * If server, the service bas path.
     * If client, the remote URL
     *
     * @param observerContext current observer context
     * @param connectionInfo information regarding connection.
     */
    private static void setObserveService(ObserverContext observerContext, WebSocketOpenConnectionInfo connectionInfo) {
        String service = connectionInfo.getService().getBasePath();

        if (service != null) {
            //If base path is set (i.e. server)
            observerContext.addTag(TAG_CLIENT_OR_SERVER, WEBSOCKET_CLIENT_OR_SERVER_SERVER);
            observerContext.addTag(TAG_SERVICE, service);
        } else {
            //if base path is not set (i.e. client)
            observerContext.addTag(TAG_CLIENT_OR_SERVER, WEBSOCKET_CLIENT_OR_SERVER_CLIENT);
            observerContext.addTag(TAG_SERVICE, connectionInfo.getWebSocketEndpoint().getStringValue("url"));
        }
    }

    private WebSocketObservability(){

    }
}
