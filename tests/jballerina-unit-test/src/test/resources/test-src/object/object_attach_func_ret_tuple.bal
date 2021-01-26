// Copyright (c) 2019, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
//
// WSO2 Inc. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.package internal;

import ballerina/jballerina.java;

public function testReturningTuple() returns [string, string] {
    Person p = new;
    return p.returnTuple();
}

class Person {

    function returnTuple() returns [string, string] {
        return [self.nonBlockingCall(), "secondValue"];
    }

    function nonBlockingCall() returns string {
        sleep(10);
        return "firstValue";
    }

}

public function sleep(int millis) = @java:Method {
    'class: "org.ballerinalang.test.utils.interop.Sleep"
} external;
