/*
 *  Copyright (c) 2019 WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 *
 */
package org.ballerinalang.stdlib.task.listener.service;

import org.ballerinalang.bre.Context;
import org.ballerinalang.bre.bvm.BlockingNativeCallableUnit;
import org.ballerinalang.connector.api.BLangConnectorSPIUtil;
import org.ballerinalang.connector.api.Service;
import org.ballerinalang.model.types.TypeKind;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.ballerinalang.natives.annotations.BallerinaFunction;
import org.ballerinalang.natives.annotations.Receiver;
import org.ballerinalang.stdlib.task.listener.objects.ServiceWithParameters;
import org.ballerinalang.stdlib.task.listener.objects.Task;

import java.util.Objects;

import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.FIELD_SERVICE_PARAMETER;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.LISTENER_STRUCT_NAME;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.NATIVE_DATA_TASK_OBJECT;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.ORGANIZATION_NAME;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.PACKAGE_NAME;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.PACKAGE_STRUCK_NAME;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.REF_ARG_INDEX_TASK_SERVICE;
import static org.ballerinalang.stdlib.task.listener.utils.TaskConstants.REF_ARG_INDEX_TASK_STRUCT;
import static org.ballerinalang.stdlib.task.listener.utils.Utils.validateService;

/**
 * Native function to attach a service to the listener.
 */
@BallerinaFunction(
        orgName = ORGANIZATION_NAME,
        packageName = PACKAGE_NAME,
        functionName = "register",
        receiver = @Receiver(
                type = TypeKind.OBJECT,
                structType = LISTENER_STRUCT_NAME,
                structPackage = PACKAGE_STRUCK_NAME),
        isPublic = true
)
public class Register extends BlockingNativeCallableUnit {

    @Override
    @SuppressWarnings("unchecked")
    public void execute(Context context) {
        Service service = BLangConnectorSPIUtil.getServiceRegistered(context);
        BMap<String, BValue> taskStruct = (BMap<String, BValue>) context.getRefArgument(REF_ARG_INDEX_TASK_STRUCT);
        BValue serviceParameters = ((BMap<String, BValue>) context.getRefArgument(REF_ARG_INDEX_TASK_SERVICE))
                .get(FIELD_SERVICE_PARAMETER);
        ServiceWithParameters serviceWithParameters;
        if (Objects.nonNull(serviceParameters)) {
            serviceWithParameters = new ServiceWithParameters(service, serviceParameters);
        } else {
            serviceWithParameters = new ServiceWithParameters(service);
        }

        /* TODO:
         * Validate service at runtime, as compiler plugin not available.
         * When compiler plugin is available, remove this.
         */
        validateService(service);

        Task task = (Task) taskStruct.getNativeData(NATIVE_DATA_TASK_OBJECT);
        task.addService(serviceWithParameters);
    }
}
