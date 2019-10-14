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
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_CONNECTIONS;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_CONNECTIONS_DESC;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_ERRORS;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_ERRORS_DESC;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_MESSAGES_RECEIVED;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_MESSAGES_RECEIVED_DESC;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_MESSAGES_SENT;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_MESSAGES_SENT_DESC;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_REQUESTS;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.METRIC_REQUESTS_DESC;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.TAG_CLIENT_OR_SERVER;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.TAG_CONNECTION_ID;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.TAG_ERROR_TYPE;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.TAG_KEY_RESULT;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.TAG_MESSAGE_TYPE;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.TAG_SERVICE;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.WEBSOCKET_CLIENT_OR_SERVER_CLIENT;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.WEBSOCKET_CLIENT_OR_SERVER_SERVER;
import static org.ballerinalang.net.http.WebSocketObservabilityConstants.WEBSOCKET_UNKNOWN;

/**
 * Providing observability functionality to WebSockets.
 *
 * @since 1.1.0
 */
public class WebSocketObservabilityUtil {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketObservabilityUtil.class);

    /**
     * Observes incoming WebSocket connection requests.
     *
     * @param connectionInfo information regarding connection.
     */
    static void observeRequest(WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = initializeObserverContext(connectionInfo);
            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            Set<Tag> tags = getAllTags(observerContext);
            //Increment requests metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_REQUESTS,
                                                METRIC_REQUESTS_DESC, tags)).increment();

            //Log request
            logger.info("WS connection request received");
        }
    }

    /**
     * Observes successful WebSocket connections.
     *
     * @param connectionInfo information regarding connection.
     */
    static void observeConnection(WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = initializeObserverContext(connectionInfo);
            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            Set<Tag> tags = getAllTags(observerContext);
            //Increment current connections metric
            metricRegistry.gauge(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_CONNECTIONS,
                                              METRIC_CONNECTIONS_DESC, tags)).increment();

            //Log connection
            logger.info("WS new connection established. connectionID: {}, service/url: {}",
                        getConnectionId(connectionInfo),
                        getServicePathOrClientUrl(connectionInfo));
        }
    }

    /**
     * Observes messages pushed (sent).
     *
     * @param type type of message pushed (text, binary, control, close).
     * @param result whether the push was successful or not.
     * @param connectionInfo information regarding connection.
     */
    public static void observePush(String type, String result, WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = initializeObserverContext(connectionInfo);
            //Define type of message (text, binary, control, clsoe) and result (successful, failed)
            observerContext.addTag(TAG_MESSAGE_TYPE, type);
            observerContext.addTag(TAG_KEY_RESULT, result);
            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            Set<Tag> tags = getAllTags(observerContext);
            //Increment message sent metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_MESSAGES_SENT,
                                                METRIC_MESSAGES_SENT_DESC, tags)).increment();

            //Log message sent
            logger.info("WS message sent. connectionID: {}, service/url: {}, type: {}",
                        getConnectionId(connectionInfo),
                        getServicePathOrClientUrl(connectionInfo),
                        type);
        }
    }

    /**
     * Observes messages received.
     *
     * @param type type of message pushed (text, binary, control, close).
     * @param connectionInfo information regarding connection.
     */
    static void observeOnMessage(String type, WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = initializeObserverContext(connectionInfo);
            //Define type of message (text, binary, control, close)
            observerContext.addTag(TAG_MESSAGE_TYPE, type);
            Set<Tag> tags = getAllTags(observerContext);
            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            //Increment messages received metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_MESSAGES_RECEIVED,
                                                METRIC_MESSAGES_RECEIVED_DESC, tags)).increment();

            //Log message received
            logger.info("WS message received. connectionID: {}, service/url: {}, type:{}",
                        getConnectionId(connectionInfo),
                        getServicePathOrClientUrl(connectionInfo),
                        type);
        }
    }

    /**
     * Observes WebSocket connection closures.
     *
     * @param connectionInfo information regarding connection.
     */
    static void observeClose(WebSocketOpenConnectionInfo connectionInfo) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = initializeObserverContext(connectionInfo);
            Set<Tag> tags = getAllTags(observerContext);
            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            //Decrement current connections metric
            metricRegistry.gauge(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_CONNECTIONS,
                                              METRIC_CONNECTIONS_DESC, tags)).decrement();

            //Log connection closure
            logger.info("WS connection closed. connectionID: {}, service/url: {}",
                        getConnectionId(connectionInfo),
                        getServicePathOrClientUrl(connectionInfo));
        }
    }

    /**
     * Observes WebSocket errors where the errorType is not related to a message being sent or received,
     * or the type of the message is unknown.
     *
     * @param connectionInfo information regarding connection.
     * @param errorType type of error (connection, closure, message sent/received).
     * @param errorMessage error message.
     */
    public static void observeError(WebSocketOpenConnectionInfo connectionInfo, String errorType, String errorMessage) {
        observeError(connectionInfo, errorType, null, errorMessage);
    }

    /**
     * Observes WebSocket errors where the errorType is related to a message being sent or received.
     * and the type of the message is known.
     *
     * @param connectionInfo information regarding connection.
     * @param errorType type of error (connection, closure, message sent/received).
     * @param messageType type of message (text, binary, control, close).
     * @param errorMessage error message.
     */
    public static void observeError(WebSocketOpenConnectionInfo connectionInfo, String errorType, String messageType,
                                    String errorMessage) {
        if (ObserveUtils.isObservabilityEnabled()) {
            ObserverContext observerContext = initializeObserverContext(connectionInfo);
            observerContext.addTag(TAG_ERROR_TYPE, errorType);
            //If the error is related to sending/receiving a message, set the type of message
            if (messageType != null) {
                observerContext.addTag(TAG_MESSAGE_TYPE, messageType);
            }
            Set<Tag> tags = getAllTags(observerContext);
            MetricRegistry metricRegistry = DefaultMetricRegistry.getInstance();
            //Increment errors metric
            metricRegistry.counter(new MetricId(SERVER_CONNECTOR_WEBSOCKET + "_" + METRIC_ERRORS,
                                                METRIC_ERRORS_DESC, tags)).increment();

            //Log error
            if (messageType == null) {
                logger.error("type:{}, message: {}, connectionId: {}, service/url:{}",
                             errorType, errorMessage, getConnectionId(connectionInfo),
                             getServicePathOrClientUrl(connectionInfo));
            } else {
                logger.error("type:{}/{}, message: {}, connectionId: {}, service/url:{}",
                             errorType, messageType, errorMessage, getConnectionId(connectionInfo),
                             getServicePathOrClientUrl(connectionInfo));
            }
        }
    }
    /**
     * Initializes the observer context object by setting the connector name, connection ID (if available) and service.
     *
     * @param connectionInfo information regarding connection.
     */

    private static ObserverContext initializeObserverContext(WebSocketOpenConnectionInfo connectionInfo) {
        ObserverContext observerContext = new ObserverContext();
        observerContext.setConnectorName(SERVER_CONNECTOR_WEBSOCKET);
        observerContext.addTag(TAG_CONNECTION_ID, getConnectionId(connectionInfo));
        setObserveServiceOrURL(observerContext, connectionInfo);
        return observerContext;
    }

    /**
     * Determines whether the current context is server or client and sets the tags accordingly.
     * If server, the service bas path.
     * If client, the remote URL
     *
     * @param observerContext current observer context
     * @param connectionInfo information regarding connection.
     */
    private static void setObserveServiceOrURL(ObserverContext observerContext,
                                               WebSocketOpenConnectionInfo connectionInfo) {
        try {
            String service = connectionInfo.getService().getBasePath();
            if (service != null) {
                //If base path is set (i.e. server)
                observerContext.addTag(TAG_CLIENT_OR_SERVER, WEBSOCKET_CLIENT_OR_SERVER_SERVER);
                observerContext.addTag(TAG_SERVICE, service);
            } else {
                //if base path is not set (i.e. client)
                observerContext.addTag(TAG_CLIENT_OR_SERVER, WEBSOCKET_CLIENT_OR_SERVER_CLIENT);
                observerContext.addTag(TAG_SERVICE,
                                       connectionInfo.getWebSocketEndpoint().getStringValue("url"));
            }
        } catch (NullPointerException e) {
            observerContext.addTag(TAG_CLIENT_OR_SERVER, WEBSOCKET_UNKNOWN);
            observerContext.addTag(TAG_SERVICE, WEBSOCKET_UNKNOWN);
        }

    }

    private static Set<Tag> getAllTags(ObserverContext observerContext) {
        Map<String, String> tags = observerContext.getTags();
        Set<Tag> allTags = new HashSet<>(tags.size());
        Tags.tags(allTags, observerContext.getTags());
        return allTags;
    }

    private static String getServicePathOrClientUrl(WebSocketOpenConnectionInfo connectionInfo) {
        try {
            String service = connectionInfo.getService().getBasePath();
            if (service != null) {
                return service;
            }
            return connectionInfo.getWebSocketEndpoint().getStringValue("url");
        } catch (Exception e) {
            return WEBSOCKET_UNKNOWN;
        }
    }

    private static String getConnectionId(WebSocketOpenConnectionInfo connectionInfo) {
        try {
            return connectionInfo.getWebSocketConnection().getChannelId();
        } catch (Exception e) {
            return WEBSOCKET_UNKNOWN;
        }
    }
    private WebSocketObservabilityUtil(){

    }
}
