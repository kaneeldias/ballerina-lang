/*
 *  Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package org.ballerinalang.observe.nativeimpl;

import io.ballerina.jvm.api.BStringUtils;
import io.ballerina.jvm.api.BValueCreator;
import io.ballerina.jvm.api.Types;
import io.ballerina.jvm.api.types.Type;
import io.ballerina.jvm.api.values.BMap;
import io.ballerina.jvm.api.values.BString;
import io.ballerina.jvm.observability.metrics.Counter;
import io.ballerina.jvm.observability.metrics.DefaultMetricRegistry;
import io.ballerina.jvm.observability.metrics.Gauge;
import io.ballerina.jvm.observability.metrics.Metric;
import io.ballerina.jvm.observability.metrics.MetricConstants;
import io.ballerina.jvm.observability.metrics.MetricId;
import io.ballerina.jvm.observability.metrics.PolledGauge;
import io.ballerina.jvm.observability.metrics.Tag;
import io.ballerina.jvm.types.BArrayType;
import io.ballerina.jvm.types.BMapType;
import io.ballerina.jvm.values.ArrayValue;
import io.ballerina.jvm.values.ArrayValueImpl;
import io.ballerina.jvm.values.MapValue;
import io.ballerina.jvm.values.MapValueImpl;

import java.util.Set;

/**
 * This is the getAllMetrics function native implementation for the registered metrics.
 * This can be used by the metric reporters to report the metrics.
 *
 * @since 0.980.0
 */

public class GetAllMetrics {

    private static final Type METRIC_TYPE = BValueCreator
            .createRecordValue(ObserveNativeImplConstants.OBSERVE_PACKAGE_ID, ObserveNativeImplConstants.METRIC)
            .getType();

    public static ArrayValue getAllMetrics() {
        Metric[] metrics = DefaultMetricRegistry.getInstance().getAllMetrics();

        ArrayValue bMetrics = new ArrayValueImpl(new BArrayType(METRIC_TYPE));
        int metricIndex = 0;
        for (Metric metric : metrics) {
            MetricId metricId = metric.getId();
            Object metricValue = null;
            String metricType = null;
            ArrayValue summary = null;
            if (metric instanceof Counter) {
                metricValue = ((Counter) metric).getValue();
                metricType = MetricConstants.COUNTER;
            } else if (metric instanceof Gauge) {
                Gauge gauge = (Gauge) metric;
                metricValue = gauge.getValue();
                metricType = MetricConstants.GAUGE;
                summary = Utils.createBSnapshots(gauge.getSnapshots());
            } else if (metric instanceof PolledGauge) {
                PolledGauge gauge = (PolledGauge) metric;
                metricValue = gauge.getValue();
                metricType = MetricConstants.GAUGE;
            }
            if (metricValue != null) {
                BMap<BString, Object> metricStruct = BValueCreator.createRecordValue(
                        ObserveNativeImplConstants.OBSERVE_PACKAGE_ID, ObserveNativeImplConstants.METRIC);
                metricStruct.put(BStringUtils.fromString("name"), BStringUtils.fromString(metricId.getName()));
                metricStruct.put(BStringUtils.fromString("desc"), BStringUtils.fromString(metricId.getDescription()));
                metricStruct.put(BStringUtils.fromString("tags"), getTags(metricId));
                metricStruct.put(BStringUtils.fromString("metricType"), BStringUtils.fromString(metricType));
                metricStruct.put(BStringUtils.fromString("value"), metricValue);
                metricStruct.put(BStringUtils.fromString("summary"), summary);
                bMetrics.add(metricIndex, metricStruct);
                metricIndex++;
            }
        }

        return bMetrics;
    }

    private static MapValue<BString, Object> getTags(MetricId metricId) {
        MapValue<BString, Object> bTags = new MapValueImpl<>(new BMapType(Types.TYPE_STRING));
        Set<Tag> tags = metricId.getTags();
        for (Tag tag : tags) {
            bTags.put(BStringUtils.fromString(tag.getKey()), BStringUtils.fromString(tag.getValue()));
        }
        return bTags;
    }

}
