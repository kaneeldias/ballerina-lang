/*
 * Copyright (c) 2020, WSO2 Inc. (http://wso2.com) All Rights Reserved.
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
package org.ballerinalang.langserver.completions.providers.context;

import io.ballerina.compiler.api.symbols.ClassSymbol;
import io.ballerina.compiler.api.symbols.FunctionTypeSymbol;
import io.ballerina.compiler.api.symbols.Symbol;
import io.ballerina.compiler.api.symbols.SymbolKind;
import io.ballerina.compiler.api.symbols.TypeSymbol;
import io.ballerina.compiler.api.symbols.VariableSymbol;
import io.ballerina.compiler.syntax.tree.AssignmentStatementNode;
import io.ballerina.compiler.syntax.tree.QualifiedNameReferenceNode;
import org.ballerinalang.annotation.JavaSPIService;
import org.ballerinalang.langserver.common.utils.CommonUtil;
import org.ballerinalang.langserver.common.utils.SymbolUtil;
import org.ballerinalang.langserver.common.utils.completion.QNameReferenceUtil;
import org.ballerinalang.langserver.commons.BallerinaCompletionContext;
import org.ballerinalang.langserver.commons.completion.LSCompletionException;
import org.ballerinalang.langserver.commons.completion.LSCompletionItem;
import org.ballerinalang.langserver.completions.SymbolCompletionItem;
import org.ballerinalang.langserver.completions.providers.AbstractCompletionProvider;
import org.ballerinalang.langserver.completions.util.CompletionUtil;
import org.ballerinalang.langserver.completions.util.ContextTypeResolver;
import org.ballerinalang.langserver.completions.util.SortingUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Handles the completions for {@link AssignmentStatementNode} context.
 *
 * @since 2.0.0
 */
@JavaSPIService("org.ballerinalang.langserver.commons.completion.spi.BallerinaCompletionProvider")
public class AssignmentStatementNodeContext extends AbstractCompletionProvider<AssignmentStatementNode> {

    public AssignmentStatementNodeContext() {
        super(AssignmentStatementNode.class);
    }

    @Override
    public List<LSCompletionItem> getCompletions(BallerinaCompletionContext context, AssignmentStatementNode node)
            throws LSCompletionException {
        if (this.cursorWithinLHS(context, node)) {
            return CompletionUtil.route(context, node.parent());
        }

        List<LSCompletionItem> completionItems = new ArrayList<>();
        if (this.onQualifiedNameIdentifier(context, node.expression())) {
            /*
            Captures the following cases
            (1) [module:]TypeName c = module:<cursor>
            (2) [module:]TypeName c = module:a<cursor>
             */
            QualifiedNameReferenceNode qNameRef = (QualifiedNameReferenceNode) node.expression();
            Predicate<Symbol> filter = symbol -> symbol instanceof VariableSymbol
                    || symbol.kind() == SymbolKind.FUNCTION;
            List<Symbol> moduleContent = QNameReferenceUtil.getModuleContent(context, qNameRef, filter);
            completionItems.addAll(this.getCompletionItemList(moduleContent, context));
        } else {
            /*
            Captures the following cases
            (1) [module:]TypeName c = <cursor>
            (2) [module:]TypeName c = a<cursor>
             */
            completionItems.addAll(this.actionKWCompletions(context));
            completionItems.addAll(this.expressionCompletions(context));
            completionItems.addAll(this.getNewExprCompletionItems(context, node));
        }
        this.sort(context, node, completionItems);

        return completionItems;
    }

    @Override
    public boolean onPreValidation(BallerinaCompletionContext context, AssignmentStatementNode node) {
        return !node.equalsToken().isMissing();
    }

    @Override
    public void sort(BallerinaCompletionContext context, AssignmentStatementNode node, 
                     List<LSCompletionItem> completionItems) {
        Optional<TypeSymbol> typeSymbolAtCursor = context.currentSemanticModel()
                .flatMap(semanticModel -> semanticModel.symbol(node.varRef()))
                .flatMap(SymbolUtil::getTypeDescriptor);

        if (typeSymbolAtCursor.isEmpty()) {
            super.sort(context, node, completionItems);
            return;
        }

        TypeSymbol symbol = typeSymbolAtCursor.get();
        completionItems.forEach(completionItem -> {
            int rank = SortingUtil.toRank(completionItem, 1);

            // If a completion item is a symbol and is assignable to the variable at left hand side, 
            // this assigns the highest rank to such variables and methods.
            if (completionItem.getType() == LSCompletionItem.CompletionItemType.SYMBOL) {
                SymbolCompletionItem symbolCompletionItem = (SymbolCompletionItem) completionItem;

                Optional<TypeSymbol> completionItemType =
                        SymbolUtil.getTypeDescriptor(symbolCompletionItem.getSymbol());
                if (completionItemType.isPresent() && completionItemType.get() instanceof FunctionTypeSymbol) {
                    completionItemType = ((FunctionTypeSymbol) completionItemType.get()).returnTypeDescriptor();
                }

                if (completionItemType.isPresent() && completionItemType.get().assignableTo(symbol)) {
                    rank = 1;
                }
            }

            completionItem.getCompletionItem().setSortText(SortingUtil.genSortText(rank));
        });
    }

    private List<LSCompletionItem> getNewExprCompletionItems(BallerinaCompletionContext context,
                                                             AssignmentStatementNode node) {
        List<LSCompletionItem> completionItems = new ArrayList<>();
        ContextTypeResolver typeResolver = new ContextTypeResolver(context);
        Optional<TypeSymbol> type = node.apply(typeResolver);
        if (type.isEmpty()) {
            return completionItems;
        }
        TypeSymbol rawType = CommonUtil.getRawType(type.get());
        if (rawType.kind() == SymbolKind.CLASS) {
            completionItems.add(this.getImplicitNewCompletionItem((ClassSymbol) rawType, context));
        }

        return completionItems;
    }

    private boolean cursorWithinLHS(BallerinaCompletionContext context, AssignmentStatementNode node) {
        int equalToken = node.equalsToken().textRange().endOffset();
        int cursor = context.getCursorPositionInTree();

        return cursor < equalToken;
    }
}
