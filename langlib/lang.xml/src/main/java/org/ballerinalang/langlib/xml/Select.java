/**
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
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
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 **/

package org.ballerinalang.langlib.xml;

import io.ballerina.jvm.scheduling.Strand;
import io.ballerina.jvm.util.exceptions.BLangExceptionHelper;
import io.ballerina.jvm.values.XMLValue;

/**
 * Get all the elements-type items in the given sequence, that matches a given qualified name.
 * 
 * @since 0.88
 */
//@BallerinaFunction(
//        orgName = "ballerina", packageName = "lang.xml",
//        functionName = "select",
//        args = {@Argument(name = "qname", type = TypeKind.STRING)},
//        returnType = {@ReturnType(type = TypeKind.XML)},
//        isPublic = true
//)
public class Select {

    private static final String OPERATION = "select elements from xml";

    public static XMLValue select(Strand strand, XMLValue xml, String qname) {
        try {
            return (XMLValue) xml.elements(qname);
        } catch (Throwable e) {
            BLangExceptionHelper.handleXMLException(OPERATION, e);
        }
        return null;
    }
}
