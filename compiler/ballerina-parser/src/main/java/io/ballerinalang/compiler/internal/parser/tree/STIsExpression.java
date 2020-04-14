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
package io.ballerinalang.compiler.internal.parser.tree;

import io.ballerinalang.compiler.syntax.tree.Node;
import io.ballerinalang.compiler.syntax.tree.NonTerminalNode;
import io.ballerinalang.compiler.syntax.tree.SyntaxKind;
import io.ballerinalang.compiler.syntax.tree.IsExpression;

/**
 * This is a generated internal syntax tree node.
 *
 * @since 1.3.0
 */
public class STIsExpression extends STExpression {
    public final STNode expression;
    public final STNode isKeyword;
    public final STNode typeDescriptor;

    STIsExpression(
            STNode expression,
            STNode isKeyword,
            STNode typeDescriptor) {
        super(SyntaxKind.IS_EXPRESSION);
        this.expression = expression;
        this.isKeyword = isKeyword;
        this.typeDescriptor = typeDescriptor;

        addChildren(
                expression,
                isKeyword,
                typeDescriptor);
    }

    @Override
    public Node createFacade(int position, NonTerminalNode parent) {
        return new IsExpression(this, position, parent);
    }
}
