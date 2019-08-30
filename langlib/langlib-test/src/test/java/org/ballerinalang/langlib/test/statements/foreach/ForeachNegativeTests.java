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
package org.ballerinalang.langlib.test.statements.foreach;

import org.ballerinalang.test.util.BAssertUtil;
import org.ballerinalang.test.util.BCompileUtil;
import org.ballerinalang.test.util.CompileResult;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Negative test cases for Foreach Statement.
 *
 * @since 0.96.0
 */
public class ForeachNegativeTests {

    @Test
    public void testSemanticErrors() {
        CompileResult compile = BCompileUtil.compile("test-src/statements/foreach/foreach-negative.bal");
        Assert.assertEquals(compile.getErrorCount(), 23);
        int index = 0;
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'string' in type definition", 37, 17);
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'string' in type definition", 46, 17);
        BAssertUtil.validateError(compile, index++, "redeclared symbol 'i'", 46, 18);
        BAssertUtil.validateError(compile, index++, "redeclared symbol 's'", 46, 21);
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'string' in type definition", 53, 17);
        BAssertUtil.validateError(compile, index++, "undefined symbol 'i'", 56, 13);
        BAssertUtil.validateError(compile, index++, "incompatible types: 'int' is not an iterable collection", 61,
                22);
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'string' in type definition", 74, 17);
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'string' in type definition", 82, 17);
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'json' in type definition", 91, 17);
        BAssertUtil.validateError(compile, index++, "unreachable code", 104, 9);
        BAssertUtil.validateError(compile, index++, "unreachable code", 109, 9);
        BAssertUtil.validateError(compile, index++, "continue cannot be used outside of a loop", 111, 5);
        BAssertUtil.validateError(compile, index++, "invalid tuple variable; expecting a tuple type but found " +
                "'string' in type definition", 117, 17);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'v'", 127, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'reason'", 146, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'fatal'", 147, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'message'", 148, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'id'", 155, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'name'", 156, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'salary'", 157, 9);
        BAssertUtil.validateError(compile, index++, "cannot assign a value to final 'v'", 168, 13);
        BAssertUtil.validateError(compile, index, "cannot assign a value to final 'v'", 182, 13);
    }
}
