package rs.ac.bg.etf.pp1.code_generation;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.semantic_analysis.SemanticAnalyzer;
import rs.ac.bg.etf.pp1.syntax_analysis.generated.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.*;

public class CodeGenerator extends VisitorAdaptor
{
    // <editor-fold desc="[CodeGenerator class attributes]">

    private static final int EXPRESSION_STACK_VALUE_PRINT_DEFAULT_WIDTH = 0;
    private static final int EXPRESSION_STACK_VALUE_INCREMENT_VALUE = 1;
    private static final int EXPRESSION_STACK_VALUE_DECREMENT_VALUE = -1;
    private static final int EXPRESSION_STACK_VALUE_SET_ADDITIONAL_WORD = 1;
    private static final int EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX = 0;
    private static final int EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INITIAL_VALUE = 0;
    private static final int EXPRESSION_STACK_VALUE_LOOP_COUNTER_INITIAL_VALUE = 0;
    private static final int EXPRESSION_STACK_VALUE_TRUE_BOOLEAN_VALUE = 1;
    private static final int EXPRESSION_STACK_VALUE_TOTAL_RESULTS_SUM_INITIAL_VALUE = 0;

    private static final int OPERAND_VALUE_NEWARRAY_CHAR_ARRAY = 0;
    private static final int OPERAND_VALUE_NEWARRAY_NOT_CHAR_ARRAY = 1;
    private static final int OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER = 0;
    private static final int OPERAND_VALUE_VMT_POINTER_CLASS_FIELD_INDEX = 0;
    private static final int OPERAND_VALUE_END_OF_INVOKE_VIRTUAL_INSTRUCTION_INDICATOR = -1;

    private static final int OPERAND_SIZE_SHORT = 2;
    private static final int OPERAND_SIZE_WORD = 4;

    private static final int RUNTIME_ERROR_CODE_MISSING_RETURN_STATEMENT = 1;

    private static final int INVALID_RELOP_CODE = -1;

    private int missingReturnStatementErrorHandlerAddress;

    private final Obj chrMethodSymbol = Tab.find("chr");
    private final Obj ordMethodSymbol = Tab.find("ord");
    private final Obj lenMethodSymbol = Tab.find("len");
    private final Obj addMethodSymbol = Tab.find("add");
    private final Obj addAllMethodSymbol = Tab.find("addAll");
    private final Obj printSetMethodSymbol = Tab.find("printSet");
    private final Obj setsUnionMethodSymbol = Tab.find("setsUnion");

    private final List<Integer> skipCondFactsJumpAddressesPlaceholders = new ArrayList<>();
    private final List<Integer> skipCondTermsJumpAddressesPlaceholders = new ArrayList<>();
    private final Deque<Integer> skipThenStatementJumpAddressesPlaceholders = new ArrayDeque<>();
    private final Deque<Integer> skipElseStatementJumpAddressesPlaceholders = new ArrayDeque<>();

    private int doWhileLoopExitJumpAddressPlaceholder;
    private final Deque<Integer> doWhileLoopIterationStartAddresses = new ArrayDeque<>();
    private final Deque<List<Integer>> doWhileLoopContinueJumpAddressesPlaceholders = new ArrayDeque<>();
    private final Deque<List<Integer>> doWhileLoopBreakJumpAddressesPlaceholders = new ArrayDeque<>();

    private final SortedMap<String, VirtualMethodTable> allVirtualMethodTables = new TreeMap<>();
    private Obj currentClassSymbol;
    private boolean areClassMethodActParsBeingProcessed = false;

    private final Logger logger = Logger.getLogger(getClass());

    // </editor-fold>

    // <editor-fold desc="[CodeGenerator initializers]">

    public CodeGenerator()
    {
        generateBuiltInMethods();
        generateMissingReturnStatementErrorHandler();
    }

    // </editor-fold>

    // <editor-fold desc="[CodeGenerator initializers' helper methods]">

    private void generateBuiltInMethods()
    {
        generateChrMethod();
        generateOrdMethod();
        generateLenMethod();
        generateAddMethod();
        generateAddAllMethod();
        generatePrintSetMethod();
        generateSetsUnionMethod();
    }

    private void generateChrMethod()
    {
        // optimization: no need for Code.enter and Code.exit instructions because arguments and local variables aren't explicitly used
        chrMethodSymbol.setAdr(Code.pc);
        Code.put(Code.return_);
    }

    private void generateOrdMethod()
    {
        // optimization: no need for Code.enter and Code.exit instructions because arguments and local variables aren't explicitly used
        ordMethodSymbol.setAdr(Code.pc);
        Code.put(Code.return_);
    }

    private void generateLenMethod()
    {
        // optimization: no need for Code.enter and Code.exit instructions because arguments and local variables aren't explicitly used
        lenMethodSymbol.setAdr(Code.pc);
        Code.put(Code.arraylength);
        Code.put(Code.return_);
    }

    private void generateAddMethod()
    {
        addMethodSymbol.setAdr(Code.pc);

        Code.put(Code.enter);
        Code.put(addMethodSymbol.getLevel());
        Code.put(addMethodSymbol.getLocalSymbols().size());

        Iterator<Obj> localSymbolsIterator = addMethodSymbol.getLocalSymbols().iterator();

        // load method arguments
        Obj destinationSet = localSymbolsIterator.next();
        Obj elementToAdd = localSymbolsIterator.next();

        // load method local variables
        Obj setSize = localSymbolsIterator.next();
        Obj currentSetIndex = localSymbolsIterator.next();

        // setSize = destinationSet[0]
        Code.load(destinationSet);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.put(Code.aload);
        Code.store(setSize);

        // currentSetIndex = 1
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentSetIndex);

        int loopIterationStartAddress = Code.pc;

        // if (currentSetIndex > setSize) exit loop and add element to set
        Code.load(currentSetIndex);
        Code.load(setSize);
        Code.putFalseJump(Code.le, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int exitLoopAndAddElementJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // if (destinationSet[currentSetIndex] == elementToAdd) exit loop and ignore adding element to set
        Code.load(destinationSet);
        Code.load(currentSetIndex);
        Code.put(Code.aload);
        Code.load(elementToAdd);
        Code.putFalseJump(Code.ne, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int exitLoopAndIgnoreAddingElementJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // currentSetIndex++
        Code.load(currentSetIndex);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentSetIndex);

        Code.putJump(loopIterationStartAddress);
        Code.fixup(exitLoopAndAddElementJumpAddressPlaceholder);
        addElementToSet(destinationSet, setSize, elementToAdd);
        Code.fixup(exitLoopAndIgnoreAddingElementJumpAddressPlaceholder);

        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    private void addElementToSet(Obj destinationSet, Obj setSize, Obj elementToAdd)
    {
        // setSize++
        Code.load(setSize);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(setSize);

        // destinationSet[setSize] = elementToAdd
        Code.load(destinationSet);
        Code.load(setSize);
        Code.load(elementToAdd);
        Code.put(Code.astore);

        // destinationSet[0] = setSize
        Code.load(destinationSet);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.load(setSize);
        Code.put(Code.astore);
    }

    private void generateAddAllMethod()
    {
        addAllMethodSymbol.setAdr(Code.pc);

        Code.put(Code.enter);
        Code.put(addAllMethodSymbol.getLevel());
        Code.put(addAllMethodSymbol.getLocalSymbols().size());

        Iterator<Obj> localSymbolsIterator = addAllMethodSymbol.getLocalSymbols().iterator();

        // load method arguments
        Obj destinationSet = localSymbolsIterator.next();
        Obj arrayOfIntegersToAdd = localSymbolsIterator.next();

        // load method local variables
        Obj currentArrayIndex = localSymbolsIterator.next();

        // currentArrayIndex = 0
        Code.loadConst(EXPRESSION_STACK_VALUE_LOOP_COUNTER_INITIAL_VALUE);
        Code.store(currentArrayIndex);

        int loopIterationStartAddress = Code.pc;

        // if (currentArrayIndex >= len(arrayOfIntegersToAdd)) exit loop
        Code.load(currentArrayIndex);
        Code.load(arrayOfIntegersToAdd);
        callGlobalMethod(lenMethodSymbol);
        Code.putFalseJump(Code.lt, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int loopExitJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // add(destinationSet, arrayOfIntegersToAdd[currentArrayIndex])
        Code.load(destinationSet);
        Code.load(arrayOfIntegersToAdd);
        Code.load(currentArrayIndex);
        Code.put(Code.aload);
        callGlobalMethod(addMethodSymbol);

        // currentArrayIndex++
        Code.load(currentArrayIndex);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentArrayIndex);

        Code.putJump(loopIterationStartAddress);
        Code.fixup(loopExitJumpAddressPlaceholder);

        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    private void generatePrintSetMethod()
    {
        printSetMethodSymbol.setAdr(Code.pc);

        Code.put(Code.enter);
        Code.put(printSetMethodSymbol.getLevel());
        Code.put(printSetMethodSymbol.getLocalSymbols().size());

        Iterator<Obj> localSymbolsIterator = printSetMethodSymbol.getLocalSymbols().iterator();

        // load method arguments
        Obj setAddress = localSymbolsIterator.next();
        Obj printWidth = localSymbolsIterator.next();

        // load method local variables
        Obj setSize = localSymbolsIterator.next();
        Obj currentSetIndex = localSymbolsIterator.next();

        // setSize = destinationSet[0]
        Code.load(setAddress);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.put(Code.aload);
        Code.store(setSize);

        // currentSetIndex = 1
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentSetIndex);

        printCharacter('{', printWidth);

        int loopIterationStartAddress = Code.pc;

        // if (currentSetIndex > setSize) exit loop
        Code.load(currentSetIndex);
        Code.load(setSize);
        Code.putFalseJump(Code.le, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int loopExitJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // print(setAddress[currentSetIndex])
        Code.load(setAddress);
        Code.load(currentSetIndex);
        Code.put(Code.aload);
        Code.loadConst(EXPRESSION_STACK_VALUE_PRINT_DEFAULT_WIDTH);
        Code.put(Code.print);

        // if (currentSetIndex == setSize) don't print comma and space
        Code.load(currentSetIndex);
        Code.load(setSize);
        Code.putFalseJump(Code.ne, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int skipPrintingCommaJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        printCharacter(',', null);
        printCharacter(' ', null);
        Code.fixup(skipPrintingCommaJumpAddressPlaceholder);

        // currentSetIndex++
        Code.load(currentSetIndex);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentSetIndex);

        Code.putJump(loopIterationStartAddress);
        Code.fixup(loopExitJumpAddressPlaceholder);

        printCharacter('}', null);

        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    private void generateSetsUnionMethod()
    {
        setsUnionMethodSymbol.setAdr(Code.pc);

        Code.put(Code.enter);
        Code.put(setsUnionMethodSymbol.getLevel());
        Code.put(setsUnionMethodSymbol.getLocalSymbols().size());

        Iterator<Obj> localSymbolsIterator = setsUnionMethodSymbol.getLocalSymbols().iterator();

        // load method arguments
        Obj destinationSet = localSymbolsIterator.next();
        Obj leftSet = localSymbolsIterator.next();
        Obj rightSet = localSymbolsIterator.next();

        // load method local variables
        Obj sourceSetSize = localSymbolsIterator.next();
        Obj currentSourceSetIndex = localSymbolsIterator.next();

        // if (destinationSet != leftSet && destinationSet != rightSet) clear destinationSet
        // e.g. s3 = s1 union s2
        clearDestinationSetIfNotEqualToLeftAndRightSet(destinationSet, leftSet, rightSet);

        // if (destinationSet == leftSet) don't add leftSet to destinationSet
        // e.g. s1 = s1 union s2
        Code.load(destinationSet);
        Code.load(leftSet);
        Code.putFalseJump(Code.ne, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int destinationEqualToLeftJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        addSourceToDestinationSet(destinationSet, leftSet, sourceSetSize, currentSourceSetIndex);
        Code.fixup(destinationEqualToLeftJumpAddressPlaceholder);

        // if (destinationSet == rightSet) don't add rightSet to destinationSet
        // e.g. s2 = s1 union s2
        Code.load(destinationSet);
        Code.load(rightSet);
        Code.putFalseJump(Code.ne, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int destinationEqualToRightJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        addSourceToDestinationSet(destinationSet, rightSet, sourceSetSize, currentSourceSetIndex);
        Code.fixup(destinationEqualToRightJumpAddressPlaceholder);

        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    private void clearDestinationSetIfNotEqualToLeftAndRightSet(Obj destinationSet, Obj leftSet, Obj rightSet)
    {
        // if (destinationSet == leftSet) don't clear destinationSet
        Code.load(destinationSet);
        Code.load(leftSet);
        Code.putFalseJump(Code.ne, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int destinationEqualToLeftJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // if (destinationSet == rightSet) don't clear destinationSet
        Code.load(destinationSet);
        Code.load(rightSet);
        Code.putFalseJump(Code.ne, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int destinationEqualToRightJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // destinationSet[0] = 0 (clear destinationSet)
        Code.load(destinationSet);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INITIAL_VALUE);
        Code.put(Code.astore);

        Code.fixup(destinationEqualToLeftJumpAddressPlaceholder);
        Code.fixup(destinationEqualToRightJumpAddressPlaceholder);
    }

    private void addSourceToDestinationSet(Obj destinationSet, Obj sourceSet, Obj sourceSetSize, Obj currentSourceSetIndex)
    {
        // sourceSetSize = sourceSet[0]
        Code.load(sourceSet);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.put(Code.aload);
        Code.store(sourceSetSize);

        // currentSourceSetIndex = 1
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentSourceSetIndex);

        int loopIterationStartAddress = Code.pc;

        // if (currentSourceSetIndex > sourceSetSize) exit loop
        Code.load(currentSourceSetIndex);
        Code.load(sourceSetSize);
        Code.putFalseJump(Code.le, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        int exitLoopJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // add(destinationSet, sourceSet[currentSourceSetIndex])
        Code.load(destinationSet);
        Code.load(sourceSet);
        Code.load(currentSourceSetIndex);
        Code.put(Code.aload);
        callGlobalMethod(addMethodSymbol);

        // currentSourceSetIndex++
        Code.load(currentSourceSetIndex);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(currentSourceSetIndex);

        Code.putJump(loopIterationStartAddress);
        Code.fixup(exitLoopJumpAddressPlaceholder);
    }

    private void generateMissingReturnStatementErrorHandler()
    {
        missingReturnStatementErrorHandlerAddress = Code.pc;
        String errorMessage = "ERROR - [MethodDecl] Return statement has not been executed in a non-void method.";
        for (char errorMessageCharacter : errorMessage.toCharArray())
        {
            Code.loadConst(errorMessageCharacter);
            Code.loadConst(EXPRESSION_STACK_VALUE_PRINT_DEFAULT_WIDTH);
            Code.put(Code.bprint);
        }
        Code.put(Code.trap);
        Code.put(RUNTIME_ERROR_CODE_MISSING_RETURN_STATEMENT);
    }

    private void generateAllVirtualMethodTablesIntoStaticMemory()
    {
        for (VirtualMethodTable virtualMethodTable : allVirtualMethodTables.values())
        {
            virtualMethodTable.generateVirtualMethodTableIntoStaticMemory();
        }
        logVirtualMethodTables();
    }

    // </editor-fold>

    // <editor-fold desc="[CodeGenerator other helper methods]">

    private void allocateMemoryForSetCurrentSizeCounter()
    {
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_ADDITIONAL_WORD);
        Code.put(Code.add);
    }

    private void initializeSetCurrentSizeCounter()
    {
        Code.put(Code.dup);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INDEX);
        Code.loadConst(EXPRESSION_STACK_VALUE_SET_CURRENT_SIZE_COUNTER_INITIAL_VALUE);
        Code.put(Code.astore);
    }

    private void printCharacter(char character, Obj printWidth)
    {
        Code.loadConst(character);
        if (printWidth == null)
        {
            Code.loadConst(EXPRESSION_STACK_VALUE_PRINT_DEFAULT_WIDTH);
        }
        else
        {
            Code.load(printWidth);
        }
        Code.put(Code.bprint);
    }

    private void callGlobalMethod(Obj methodSymbol)
    {
        int methodOffsetFromPc = methodSymbol.getAdr() - Code.pc;
        Code.put(Code.call);
        Code.put2(methodOffsetFromPc);
    }

    private void callClassMethod(Obj methodSymbol)
    {
        // load virtualMethodTableAddress for the current object
        Code.put(Code.getfield);
        Code.put2(OPERAND_VALUE_VMT_POINTER_CLASS_FIELD_INDEX);  // expression stack: [virtualMethodTableAddress]

        // call class method
        Code.put(Code.invokevirtual);
        for (char classMethodNameCharacter : methodSymbol.getName().toCharArray())
        {
            Code.put4(classMethodNameCharacter);
        }
        Code.put4(OPERAND_VALUE_END_OF_INVOKE_VIRTUAL_INSTRUCTION_INDICATOR);  // expression stack:
    }

    private int getRelopCode(Relop relop)
    {
        if (relop instanceof RelopEquals)
        {
            return Code.eq;
        }
        else if (relop instanceof RelopNotEquals)
        {
            return Code.ne;
        }
        else if (relop instanceof RelopGreaterThan)
        {
            return Code.gt;
        }
        else if (relop instanceof RelopGreaterThanOrEqual)
        {
            return Code.ge;
        }
        else if (relop instanceof RelopLessThan)
        {
            return Code.lt;
        }
        else if (relop instanceof RelopLessThanOrEqual)
        {
            return Code.le;
        }
        else
        {
            return INVALID_RELOP_CODE;
        }
    }

    private void swapTopTwoExpressionStackValues()
    {
        Code.put(Code.dup_x1);
        Code.put(Code.pop);
    }

    private void logVirtualMethodTables()
    {
        String indentation = "    ";
        StringBuilder stringBuilder = new StringBuilder("Start of all virtual method tables (VMTs).\n\n");
        for (Map.Entry<String, VirtualMethodTable> entry : allVirtualMethodTables.entrySet())
        {
            String className = entry.getKey();
            VirtualMethodTable virtualMethodTable = entry.getValue();
            stringBuilder.append("Virtual Method Table (VMT) for class '").append(className).append("':\n");
            int methodNumber = 1;
            for (Obj method : virtualMethodTable.getAllMethods().values())
            {
                stringBuilder.append(indentation).append("(").append(methodNumber++).append(") ");
                stringBuilder.append("static memory address: ").append(method.getAdr());
                stringBuilder.append("; method name: '").append(method.getName()).append("'\n");
            }
            stringBuilder.append("\n");
        }
        stringBuilder.append("End of all virtual method tables (VMTs).\n");
        logger.info(stringBuilder.toString());
    }

    // </editor-fold>

    // <editor-fold desc="[ClassDecl]">

    @Override
    public void visit(ClassName className)
    {
        currentClassSymbol = ((ClassDecl)className.getParent()).obj;
    }

    @Override
    public void visit(ClassNameExtendsBaseTypeName classNameExtendsBaseTypeName)
    {
        currentClassSymbol = ((ClassDecl)classNameExtendsBaseTypeName.getParent()).obj;
    }

    @Override
    public void visit(ClassDecl classDecl)
    {
        allVirtualMethodTables.put(classDecl.obj.getName(), new VirtualMethodTable(classDecl.obj.getType()));
        currentClassSymbol = null;
    }

    // </editor-fold>

    // <editor-fold desc="[MethodSignature]">

    @Override
    public void visit(MethodName methodName)
    {
        methodName.obj.setAdr(Code.pc);
        if (methodName.getMethodNameString().equals("main") && currentClassSymbol == null)
        {
            Code.mainPc = Code.pc;
            // all virtual method tables must be generated into static memory before main method execution for class method calls to work
            generateAllVirtualMethodTablesIntoStaticMemory();
        }
        Code.put(Code.enter);
        Code.put(methodName.obj.getLevel());
        Code.put(methodName.obj.getLocalSymbols().size());
    }

    // </editor-fold>

    // <editor-fold desc="[MethodDecl]">

    @Override
    public void visit(MethodDecl methodDecl)
    {
        boolean isMethodReturnTypeVoid = methodDecl.getMethodSignature().getMethodName().obj.getType() == SemanticAnalyzer.voidType;
        if (isMethodReturnTypeVoid)
        {
            Code.put(Code.exit);
            Code.put(Code.return_);
        }
        else
        {
            Code.putJump(missingReturnStatementErrorHandlerAddress);
        }
    }

    // </editor-fold>

    // <editor-fold desc="[Statement]">

    @Override
    public void visit(ThenStatementIndicator thenStatementIndicator)
    {
        // if (all CondTerms in current Condition are false) skip then statement
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        skipThenStatementJumpAddressesPlaceholders.push(Code.pc - OPERAND_SIZE_SHORT);

        // if (some CondTerm in current Condition is true) execute then statement
        while (!skipCondTermsJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(skipCondTermsJumpAddressesPlaceholders.remove(0));
        }
    }

    @Override
    public void visit(NoElseStatementIndicator noElseStatementIndicator)
    {
        // if (then statement not executed) execute next statement
        Code.fixup(skipThenStatementJumpAddressesPlaceholders.pop());
    }

    @Override
    public void visit(ElseStatementIndicator elseStatementIndicator)
    {
        // if (then statement executed) skip else statement
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        skipElseStatementJumpAddressesPlaceholders.push(Code.pc - OPERAND_SIZE_SHORT);

        // if (then statement not executed) execute else statement
        Code.fixup(skipThenStatementJumpAddressesPlaceholders.pop());
    }

    @Override
    public void visit(StatementIfElse statementIfElse)
    {
        // if (then statement executed) skip else statement
        Code.fixup(skipElseStatementJumpAddressesPlaceholders.pop());
    }

    @Override
    public void visit(StatementReturnWithoutExpr statementReturnWithoutExpr)
    {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    @Override
    public void visit(StatementReturnWithExpr statementReturnWithExpr)
    {
        Code.put(Code.exit);
        Code.put(Code.return_);
    }

    @Override
    public void visit(StatementRead statementRead)
    {
        Obj designatorSymbol = statementRead.getDesignator().obj;
        if (designatorSymbol.getType() == SemanticAnalyzer.charType)
        {
            Code.put(Code.bread);
        }
        else
        {
            Code.put(Code.read);
        }
        Code.store(designatorSymbol);
    }

    @Override
    public void visit(StatementPrintWithoutParameter statementPrintWithoutParameter)
    {
        Code.loadConst(EXPRESSION_STACK_VALUE_PRINT_DEFAULT_WIDTH);
        if (statementPrintWithoutParameter.getExpr().obj.getType() == SemanticAnalyzer.charType)
        {
            Code.put(Code.bprint);
        }
        else if (statementPrintWithoutParameter.getExpr().obj.getType() == SemanticAnalyzer.setType)
        {
            callGlobalMethod(printSetMethodSymbol);
        }
        else
        {
            Code.put(Code.print);
        }
    }

    @Override
    public void visit(StatementPrintWithParameter statementPrintWithParameter)
    {
        Code.loadConst(statementPrintWithParameter.getNumberParameter());
        if (statementPrintWithParameter.getExpr().obj.getType() == SemanticAnalyzer.charType)
        {
            Code.put(Code.bprint);
        }
        else if (statementPrintWithParameter.getExpr().obj.getType() == SemanticAnalyzer.setType)
        {
            callGlobalMethod(printSetMethodSymbol);
        }
        else
        {
            Code.put(Code.print);
        }
    }

    @Override
    public void visit(StatementBreak statementBreak)
    {
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        List<Integer> currentDoWhileLoopBreakJumpAddressesPlaceholders = doWhileLoopBreakJumpAddressesPlaceholders.peek();
        currentDoWhileLoopBreakJumpAddressesPlaceholders.add(Code.pc - OPERAND_SIZE_SHORT);
    }

    @Override
    public void visit(StatementContinue statementContinue)
    {
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        List<Integer> currentDoWhileLoopContinueJumpAddressesPlaceholders = doWhileLoopContinueJumpAddressesPlaceholders.peek();
        currentDoWhileLoopContinueJumpAddressesPlaceholders.add(Code.pc - OPERAND_SIZE_SHORT);
    }

    @Override
    public void visit(DoKeyword doKeyword)
    {
        doWhileLoopIterationStartAddresses.push(Code.pc);
        doWhileLoopContinueJumpAddressesPlaceholders.push(new ArrayList<>());
        doWhileLoopBreakJumpAddressesPlaceholders.push(new ArrayList<>());
    }

    @Override
    public void visit(WhileKeyword whileKeyword)
    {
        // fixup all 'continue' statements to skip the rest of the current loop iteration and jump to checking the loop condition for the next iteration
        List<Integer> currentDoWhileLoopContinueJumpAddressesPlaceholders = doWhileLoopContinueJumpAddressesPlaceholders.pop();
        while (!currentDoWhileLoopContinueJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(currentDoWhileLoopContinueJumpAddressesPlaceholders.remove(0));
        }
    }

    @Override
    public void visit(DoWhileNoConditionLeftParentheses doWhileNoConditionLeftParentheses)
    {
        // unconditional jump to the beginning of the next loop iteration for infinite loops
        Code.putJump(doWhileLoopIterationStartAddresses.pop());
    }

    @Override
    public void visit(DoWhileRightParentheses doWhileRightParentheses)
    {
        // fixup all 'break' statements to exit the loop
        List<Integer> currentDoWhileLoopBreakJumpAddressesPlaceholders = doWhileLoopBreakJumpAddressesPlaceholders.pop();
        while (!currentDoWhileLoopBreakJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(currentDoWhileLoopBreakJumpAddressesPlaceholders.remove(0));
        }
    }

    @Override
    public void visit(AfterDoWhileConditionNoDesignatorStatementIndicator afterDoWhileConditionNoDesignatorStatementIndicator)
    {
        // if (all CondTerms in current Condition are false) exit loop
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        doWhileLoopExitJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // if (some CondTerm in current Condition is true) execute next loop iteration
        while (!skipCondTermsJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(skipCondTermsJumpAddressesPlaceholders.remove(0));
        }
        Code.putJump(doWhileLoopIterationStartAddresses.pop());

        // if (all CondTerms in current Condition are false) exit loop
        Code.fixup(doWhileLoopExitJumpAddressPlaceholder);
    }

    @Override
    public void visit(AfterDoWhileConditionWithDesignatorStatementIndicator afterDoWhileConditionWithDesignatorStatementIndicator)
    {
        // if (all CondTerms in current Condition are false) skip designator statement and exit loop
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        doWhileLoopExitJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // if (some CondTerm in current Condition is true) execute designator statement and next loop iteration
        while (!skipCondTermsJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(skipCondTermsJumpAddressesPlaceholders.remove(0));
        }
    }

    @Override
    public void visit(AfterDoWhileDesignatorStatementIndicator afterDoWhileDesignatorStatementIndicator)
    {
        // if (some CondTerm in current Condition is true) execute designator statement and next loop iteration
        Code.putJump(doWhileLoopIterationStartAddresses.pop());

        // if (all CondTerms in current Condition are false) skip designator statement and exit loop
        Code.fixup(doWhileLoopExitJumpAddressPlaceholder);
    }

    // </editor-fold>

    // <editor-fold desc="[DesignatorStatement]">

    @Override
    public void visit(DesignatorStatementAssignopExpr designatorStatementAssignopExpr)
    {
        Code.store(designatorStatementAssignopExpr.getDesignator().obj);
    }

    @Override
    public void visit(DesignatorStatementFunctionCall designatorStatementFunctionCall)
    {
        Obj designatorSymbol = designatorStatementFunctionCall.getDesignator().obj;

        boolean isProgramInsideClassMethod = currentClassSymbol != null;
        boolean isDesignatorCurrentClassMethod = currentClassSymbol != null && currentClassSymbol.getType().getMembersTable().searchKey(designatorSymbol.getName()) != null;
        boolean isClassMethodExplicitlyAccessed = designatorStatementFunctionCall.getDesignator() instanceof DesignatorUserTypeAccess;
        boolean isClassMethodImplicitlyAccessed = isProgramInsideClassMethod && isDesignatorCurrentClassMethod && !isClassMethodExplicitlyAccessed;
        boolean isClassMethod = isClassMethodExplicitlyAccessed || isClassMethodImplicitlyAccessed;

        if (isClassMethod)
        {
            callClassMethod(designatorSymbol);
        }
        else
        {
            callGlobalMethod(designatorSymbol);
        }

        // remove function call result from expression stack if function return type is not void because it's not used
        if (designatorSymbol.getType() != SemanticAnalyzer.voidType)
        {
            Code.put(Code.pop);
        }
    }

    @Override
    public void visit(DesignatorStatementIncrement designatorStatementIncrement)
    {
        Obj designatorSymbol = designatorStatementIncrement.getDesignator().obj;
        if (designatorSymbol.getKind() == Obj.Elem)
        {
            Code.put(Code.dup2);
        }
        else if (designatorSymbol.getKind() == Obj.Fld)
        {
            Code.put(Code.dup);
        }
        Code.load(designatorSymbol);
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);
        Code.put(Code.add);
        Code.store(designatorSymbol);
    }

    @Override
    public void visit(DesignatorStatementDecrement designatorStatementDecrement)
    {
        Obj designatorSymbol = designatorStatementDecrement.getDesignator().obj;
        if (designatorSymbol.getKind() == Obj.Elem)
        {
            Code.put(Code.dup2);
        }
        else if (designatorSymbol.getKind() == Obj.Fld)
        {
            Code.put(Code.dup);
        }
        Code.load(designatorSymbol);
        Code.loadConst(EXPRESSION_STACK_VALUE_DECREMENT_VALUE);
        Code.put(Code.add);
        Code.store(designatorSymbol);
    }

    @Override
    public void visit(DesignatorStatementSetop designatorStatementSetop)
    {
        Code.load(designatorStatementSetop.getDesignator().obj);
        Code.load(designatorStatementSetop.getDesignator1().obj);
        Code.load(designatorStatementSetop.getDesignator2().obj);
        callGlobalMethod(setsUnionMethodSymbol);
    }

    // </editor-fold>

    // <editor-fold desc="[ActPars]">

    @Override
    public void visit(ActParsExpr actParsExpr)
    {
        if (areClassMethodActParsBeingProcessed)
        {
            // we are doing this because 'this' object reference must remain on top of the stack to be ready for the invokevirtual instruction
            // expression stack: [actPar] -> [this] -> [this] -> ...
            swapTopTwoExpressionStackValues();
            // expression stack: [this] -> [actPar] -> [this] -> ...
        }
    }

    @Override
    public void visit(ActParsExprCommaExpr actParsExprCommaExpr)
    {
        if (areClassMethodActParsBeingProcessed)
        {
            // we are doing this because 'this' object reference must remain on top of the stack to be ready for the invokevirtual instruction
            // expression stack: [actPar] -> [this] -> [actPar] -> ... -> [this] -> ...
            swapTopTwoExpressionStackValues();
            // expression stack: [this] -> [actPar] -> [actPar] -> ... -> [this] -> ...
        }
    }

    @Override
    public void visit(FunctionCallRightParentheses functionCallRightParentheses)
    {
        areClassMethodActParsBeingProcessed = false;
    }

    // </editor-fold>

    // <editor-fold desc="[Condition]">

    @Override
    public void visit(ConditionCondTerm conditionCondTerm)
    {
        // if (all CondFacts in current CondTerm are true) skip all following CondTerms in current Condition
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        skipCondTermsJumpAddressesPlaceholders.add(Code.pc - OPERAND_SIZE_SHORT);

        // if (some CondFact in current CondTerm is false) skip all following CondFacts in current CondTerm
        while (!skipCondFactsJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(skipCondFactsJumpAddressesPlaceholders.remove(0));
        }
    }

    @Override
    public void visit(ConditionCondTermOrCondTerm conditionCondTermOrCondTerm)
    {
        // if (all CondFacts in current CondTerm are true) skip all following CondTerms in current Condition
        Code.putJump(OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        skipCondTermsJumpAddressesPlaceholders.add(Code.pc - OPERAND_SIZE_SHORT);

        // if (some CondFact in current CondTerm is false) skip all following CondFacts in current CondTerm
        while (!skipCondFactsJumpAddressesPlaceholders.isEmpty())
        {
            Code.fixup(skipCondFactsJumpAddressesPlaceholders.remove(0));
        }
    }

    // </editor-fold>

    // <editor-fold desc="[CondFact]">

    @Override
    public void visit(CondFactExpr condFactExpr)
    {
        // if (CondFact == false) skip all following CondFacts in current CondTerm
        Code.loadConst(EXPRESSION_STACK_VALUE_TRUE_BOOLEAN_VALUE);
        Code.putFalseJump(Code.eq, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        skipCondFactsJumpAddressesPlaceholders.add(Code.pc - OPERAND_SIZE_SHORT);
    }

    @Override
    public void visit(CondFactExprRelopExpr condFactExprRelopExpr)
    {
        // if (CondFact == false) skip all following CondFacts in current CondTerm
        Code.putFalseJump(getRelopCode(condFactExprRelopExpr.getRelop()), OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);
        skipCondFactsJumpAddressesPlaceholders.add(Code.pc - OPERAND_SIZE_SHORT);
    }

    // </editor-fold>

    // <editor-fold desc="[Expr]">

    @Override
    public void visit(ExprAddopMinusTerm exprAddopMinusTerm)
    {
        Code.put(Code.neg);
    }

    @Override
    public void visit(ExprExprAddopTerm exprExprAddopTerm)
    {
        if (exprExprAddopTerm.getAddop() instanceof AddopPlus)
        {
            Code.put(Code.add);
        }
        else if (exprExprAddopTerm.getAddop() instanceof AddopMinus)
        {
            Code.put(Code.sub);
        }
    }

    @Override
    public void visit(ExprDesignatorMapDesignator exprDesignatorMapDesignator)
    {
        int designatorMethodAddress = exprDesignatorMapDesignator.getDesignator().obj.getAdr();
        Obj designatorArraySymbol = exprDesignatorMapDesignator.getDesignator1().obj;

        // totalResultsSum = 0; push totalResultsSum to expression stack
        Code.loadConst(EXPRESSION_STACK_VALUE_TOTAL_RESULTS_SUM_INITIAL_VALUE);  // expression stack: [totalResultsSum]

        // currentArrayIndex = 0; push currentArrayIndex to expression stack
        Code.loadConst(EXPRESSION_STACK_VALUE_LOOP_COUNTER_INITIAL_VALUE);  // expression stack: [currentArrayIndex] -> [totalResultsSum]

        int loopIterationStartAddress = Code.pc;  // expression stack: [currentArrayIndex] -> [totalResultsSum]

        // if (currentArrayIndex >= len(designatorArray)) exit loop
        Code.put(Code.dup);  // expression stack: [currentArrayIndex] -> [currentArrayIndex] -> [totalResultsSum]
        Code.load(designatorArraySymbol);  // expression stack: [designatorArray] -> [currentArrayIndex] -> [currentArrayIndex] -> [totalResultsSum]
        callGlobalMethod(lenMethodSymbol);  // expression stack: [arrayLength] -> [currentArrayIndex] -> [currentArrayIndex] -> [totalResultsSum]
        Code.putFalseJump(Code.lt, OPERAND_VALUE_JUMP_UNKNOWN_ADDRESS_PLACEHOLDER);  // expression stack: [currentArrayIndex] -> [totalResultsSum]
        int loopExitJumpAddressPlaceholder = Code.pc - OPERAND_SIZE_SHORT;

        // totalResultsSum += designatorMapDesignatorMethod(designatorArray[currentArrayIndex])
        Code.put(Code.dup2);  // expression stack: [currentArrayIndex] -> [totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum]
        Code.load(designatorArraySymbol);  // expression stack: [designatorArray] -> [currentArrayIndex] -> [totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum]
        swapTopTwoExpressionStackValues();  // expression stack: [currentArrayIndex] -> [designatorArray] -> [totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum]
        Code.put(Code.aload);  // expression stack: [designatorArray[currentArrayIndex]] -> [totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum]
        int methodOffsetFromPc = designatorMethodAddress - Code.pc;
        Code.put(Code.call);
        Code.put2(methodOffsetFromPc);  // expression stack: [result] -> [totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum]
        Code.put(Code.add);  // expression stack: [result + totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum]
        Code.put(Code.dup_x2);  // expression stack: [result + totalResultsSum] -> [currentArrayIndex] -> [totalResultsSum] -> [result + totalResultsSum]
        Code.put(Code.pop);  // expression stack: [currentArrayIndex] -> [totalResultsSum] -> [result + totalResultsSum]
        swapTopTwoExpressionStackValues();  // expression stack: [totalResultsSum] -> [currentArrayIndex] -> [result + totalResultsSum]
        Code.put(Code.pop);  // expression stack: [currentArrayIndex] -> [result + totalResultsSum]

        // currentArrayIndex++
        Code.loadConst(EXPRESSION_STACK_VALUE_INCREMENT_VALUE);  // expression stack: [1] -> [currentArrayIndex] -> [result + totalResultsSum]
        Code.put(Code.add);  // expression stack: [currentArrayIndex + 1] -> [result + totalResultsSum]

        Code.putJump(loopIterationStartAddress);
        Code.fixup(loopExitJumpAddressPlaceholder);

        Code.put(Code.pop);  // expression stack: [totalResultsSum]
    }

    // </editor-fold>

    // <editor-fold desc="[Term]">

    @Override
    public void visit(TermTermMulopFactor termTermMulopFactor)
    {
        if (termTermMulopFactor.getMulop() instanceof MulopMultiply)
        {
            Code.put(Code.mul);
        }
        else if (termTermMulopFactor.getMulop() instanceof MulopDivide)
        {
            Code.put(Code.div);
        }
        else if (termTermMulopFactor.getMulop() instanceof MulopModulo)
        {
            Code.put(Code.rem);
        }
    }

    // </editor-fold>

    // <editor-fold desc="[Factor]">

    @Override
    public void visit(FactorDesignator factorDesignator)
    {
        // non-method designators always need to be loaded to expression stack because they are always used in expressions
        Code.load(factorDesignator.obj);
    }

    @Override
    public void visit(FactorFunctionCall factorFunctionCall)
    {
        Obj designatorSymbol = factorFunctionCall.getDesignator().obj;

        boolean isProgramInsideClassMethod = currentClassSymbol != null;
        boolean isDesignatorCurrentClassMethod = currentClassSymbol != null && currentClassSymbol.getType().getMembersTable().searchKey(designatorSymbol.getName()) != null;
        boolean isClassMethodExplicitlyAccessed = factorFunctionCall.getDesignator() instanceof DesignatorUserTypeAccess;
        boolean isClassMethodImplicitlyAccessed = isProgramInsideClassMethod && isDesignatorCurrentClassMethod && !isClassMethodExplicitlyAccessed;
        boolean isClassMethod = isClassMethodExplicitlyAccessed || isClassMethodImplicitlyAccessed;

        if (isClassMethod)
        {
            callClassMethod(designatorSymbol);
        }
        else
        {
            callGlobalMethod(designatorSymbol);
        }
    }

    @Override
    public void visit(FactorConstant factorConstant)
    {
        // constants always need to be loaded to expression stack because they are always used in expressions
        Code.load(factorConstant.obj);
    }

    @Override
    public void visit(FactorCollectionInstantiation factorCollectionInstantiation)
    {
        if (factorCollectionInstantiation.obj.getType().getKind() == Struct.Array)
        {
            Code.put(Code.newarray);
            Struct arrayElementType = factorCollectionInstantiation.obj.getType().getElemType();
            Code.put(arrayElementType == SemanticAnalyzer.charType ? OPERAND_VALUE_NEWARRAY_CHAR_ARRAY : OPERAND_VALUE_NEWARRAY_NOT_CHAR_ARRAY);
        }
        else if (factorCollectionInstantiation.obj.getType().getKind() == SemanticAnalyzer.Set)
        {
            allocateMemoryForSetCurrentSizeCounter();
            Code.put(Code.newarray);
            Code.put(OPERAND_VALUE_NEWARRAY_NOT_CHAR_ARRAY);
            initializeSetCurrentSizeCounter();
        }
    }

    @Override
    public void visit(FactorObjectInstantiation factorObjectInstantiation)
    {
        Code.put(Code.new_);
        Code.put2(factorObjectInstantiation.obj.getType().getNumberOfFields() * OPERAND_SIZE_WORD);  // expression stack: [objectAddress]

        String className = factorObjectInstantiation.obj.getName();
        int virtualMethodTableAddress = allVirtualMethodTables.get(className).getVirtualMethodTableAddress();

        // set object's virtual method table pointer
        Code.put(Code.dup);  // expression stack: [objectAddress] -> [objectAddress]
        Code.loadConst(virtualMethodTableAddress);  // expression stack: [virtualMethodTableAddress] -> [objectAddress] -> [objectAddress]
        Code.put(Code.putfield);
        Code.put2(OPERAND_VALUE_VMT_POINTER_CLASS_FIELD_INDEX);  // expression stack: [objectAddress]
    }

    // </editor-fold>

    // <editor-fold desc="[Designator]">

    @Override
    public void visit(DesignatorIdentificator designatorIdentificator)
    {
        // if array element is being accessed, always push the array address to expression stack because it is always needed both for load and store operations of array elements
        // e.g. print(integerArray[3]); => push integerArray to expression stack
        // e.g. integerArray[3] = 5; => push integerArray to expression stack
        if (designatorIdentificator.getParent() instanceof DesignatorArrayAccess)
        {
            Code.load(designatorIdentificator.obj);  // expression stack: [objectAddress]
            return;
        }

        boolean isClassMemberExplicitlyAccessed = designatorIdentificator.getParent() instanceof DesignatorUserTypeAccess;
        boolean isClassMethodExplicitlyAccessed =
                designatorIdentificator.getParent().getParent() instanceof FactorFunctionCall ||
                designatorIdentificator.getParent().getParent() instanceof DesignatorStatementFunctionCall;

        // if object field is being explicitly accessed, always push the object address to expression stack because it is always needed both for load and store operations of class fields
        // e.g. print(object.integerObjectField); => push object to expression stack
        // e.g. object.integerObjectField = 5; => push object to expression stack
        // e.g. print(this.integerObjectField); => push 'this' to expression stack
        // e.g. this.integerObjectField = 5; => push 'this' to expression stack
        if (isClassMemberExplicitlyAccessed && !isClassMethodExplicitlyAccessed)
        {
            Code.load(designatorIdentificator.obj);  // expression stack: [objectAddress]
            return;
        }

        // if object method is being explicitly accessed, always push the object address to expression stack because it is the implicit 'this' argument for the method call
        // e.g. object.objectMethod(); => push object to expression stack
        // e.g. this.objectMethod(); => push 'this' to expression stack
        if (isClassMemberExplicitlyAccessed)
        {
            Code.load(designatorIdentificator.obj);  // expression stack: [objectAddress]
            // pushing another instance of the object address to expression stack because it will later be needed to load the virtual method table pointer from the object for invokevirtual instruction
            Code.put(Code.dup);  // expression stack: [objectAddress] -> [objectAddress]
            areClassMethodActParsBeingProcessed = true;
            return;
        }

        boolean isProgramCurrentlyInsideClassMethod = currentClassSymbol != null;
        if (!isProgramCurrentlyInsideClassMethod)
        {
            // if we are not currently inside a class method, implicit class member accesses are not possible so just return
            return;
        }

        Obj designatorSymbolFromCurrentClassLocals = currentClassSymbol.getType().getMembersTable().searchKey(designatorIdentificator.obj.getName());
        // it is important to compare designatorIdentificator.obj and designatorSymbolFromCurrentClassLocals by reference to be sure that designatorIdentificator.obj is a class member and not a local variable or method argument with the same name as the class member
        boolean isDesignatorCurrentClassMember = designatorIdentificator.obj == designatorSymbolFromCurrentClassLocals;
        boolean isDesignatorCurrentClassMethod = isDesignatorCurrentClassMember && designatorIdentificator.obj.getKind() == Obj.Meth;
        boolean isDesignatorCurrentClassField = isDesignatorCurrentClassMember && designatorIdentificator.obj.getKind() == Obj.Fld;

        // if current class method is being implicitly accessed inside a class method, push the implicit 'this' argument to expression stack because it is always the first argument of every class method
        // Code.load_n instruction correctly loads the 'this' argument because it is always the first argument of every class method and method arguments are always stored before local variables in the local array
        // e.g. Class1 { void objectMethod1() { } void objectMethod2() { objectMethod1(); } } => push 'this' to expression stack
        if (isDesignatorCurrentClassMethod)
        {
            Code.put(Code.load_n);  // expression stack: [objectAddress]
            // pushing another instance of the object address to expression stack because it will later be needed to load the virtual method table pointer from the object for invokevirtual instruction
            Code.put(Code.dup);  // expression stack: [objectAddress] -> [objectAddress]
            areClassMethodActParsBeingProcessed = true;
            return;
        }

        // if current class field is being implicitly accessed inside a class method, push the implicit 'this' argument to expression stack because it is needed for both load and store operations of class fields
        // Code.load_n instruction correctly loads the 'this' argument because it is always the first argument of every class method and method arguments are always stored before local variables in the local array
        // e.g. Class1 { int field1; void objectMethod1() { field1 = 5; } } => push 'this' to expression stack
        if (isDesignatorCurrentClassField)
        {
            Code.put(Code.load_n);  // expression stack: [objectAddress]
        }
    }

    @Override
    public void visit(DesignatorArrayAccess designatorArrayAccess)
    {
        boolean isAccessedArrayElementAnAccessedObject = designatorArrayAccess.getParent() instanceof DesignatorUserTypeAccess;
        boolean isClassMethodAccessed =
                designatorArrayAccess.getParent().getParent() instanceof FactorFunctionCall ||
                designatorArrayAccess.getParent().getParent() instanceof DesignatorStatementFunctionCall;

        // if current array element that is being accessed is an object whose field is being accessed, push the object address to expression stack
        // e.g. object1.object2.object3.array3[3].integerField => push object1.object2.object3.array3[3] to expression stack
        if (isAccessedArrayElementAnAccessedObject && !isClassMethodAccessed)
        {
            Code.load(designatorArrayAccess.obj);
            return;
        }

        // if current array element that is being accessed is an object whose method is being accessed, always push the object address to expression stack because it is the implicit 'this' argument for the method call
        // e.g. object1.object2.object3.array3[3].objectMethod() => push object1.object2.object3.array3[3] to expression stack
        if (isAccessedArrayElementAnAccessedObject)
        {
            Code.load(designatorArrayAccess.obj);  // expression stack: [objectAddress]
            // pushing another instance of the object address to expression stack because it will later be needed to load the virtual method table pointer from the object for invokevirtual instruction
            Code.put(Code.dup);  // expression stack: [objectAddress] -> [objectAddress]
            areClassMethodActParsBeingProcessed = true;
        }
    }

    @Override
    public void visit(DesignatorUserTypeAccess designatorUserTypeAccess)
    {
        // if current object field that is being accessed is an array whose element is being accessed, push the array address to expression stack
        // e.g. object1.array1[3].object2.array2[5] => push object1.array1[3].object2.array2 to expression stack
        if (designatorUserTypeAccess.getParent() instanceof DesignatorArrayAccess)
        {
            Code.load(designatorUserTypeAccess.obj);
            return;
        }

        boolean isAccessedFieldAnAccessedObject = designatorUserTypeAccess.getParent() instanceof DesignatorUserTypeAccess;
        boolean isClassMethodAccessed =
                designatorUserTypeAccess.getParent().getParent() instanceof FactorFunctionCall ||
                designatorUserTypeAccess.getParent().getParent() instanceof DesignatorStatementFunctionCall;

        // if current object's field that is being accessed is an object whose field is being accessed, push that object's address to expression stack
        // e.g. object1.object2.object3.object4.integerField => push object1.object2.object3.object4 to expression stack
        if (isAccessedFieldAnAccessedObject && !isClassMethodAccessed)
        {
            Code.load(designatorUserTypeAccess.obj);
            return;
        }

        // if current object's method is being invoked, push the object's address to expression stack because it is the implicit 'this' argument for the method call
        // e.g. object1.object2.object3.object4.objectMethod() => push object1.object2.object3.object4 to expression stack
        if (isAccessedFieldAnAccessedObject)
        {
            Code.load(designatorUserTypeAccess.obj);  // expression stack: [objectAddress]
            // pushing another instance of the object address to expression stack because it will later be needed to load the virtual method table pointer from the object for invokevirtual instruction
            Code.put(Code.dup);  // expression stack: [objectAddress] -> [objectAddress]
            areClassMethodActParsBeingProcessed = true;
        }
    }

    // </editor-fold>
}
