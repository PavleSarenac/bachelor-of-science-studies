package rs.ac.bg.etf.pp1.semantic_analysis;

import org.apache.log4j.Logger;
import rs.ac.bg.etf.pp1.syntax_analysis.generated.ast.*;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.*;
import rs.etf.pp1.symboltable.concepts.*;

import java.util.*;
import java.util.stream.Collectors;

public class SemanticAnalyzer extends VisitorAdaptor
{
    // <editor-fold desc="[SemanticAnalyzer class attributes]">

    public static final int Set = 8;

    private static final int VAR_LEVEL_GLOBAL_VARIABLE = 0;
    private static final int VAR_LEVEL_LOCAL_VARIABLE_GLOBAL_METHOD = 1;
    private static final int VAR_LEVEL_FORMAL_PARAMETER_GLOBAL_METHOD = 1;
    private static final int VAR_LEVEL_LOCAL_VARIABLE_CLASS_OR_INTERFACE_METHOD = 2;
    private static final int VAR_LEVEL_FORMAL_PARAMETER_CLASS_OR_INTERFACE_METHOD = 2;

    private static final int VAR_FP_POS_GLOBAL_VARIABLE = 1;
    private static final int VAR_FP_POS_LOCAL_VARIABLE = 2;
    private static final int VAR_FP_POS_FORMAL_PARAMETER = 3;

    private static final int METH_FP_POS_UNIMPLEMENTED_INTERFACE_METHOD = 1;
    private static final int METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD = 2;
    public static final int METH_FP_POS_IMPLEMENTED_INTERFACE_METHOD = 3;
    private static final int METH_FP_POS_IMPLEMENTED_CLASS_METHOD = 4;

    public static final Obj voidSymbol = Tab.noObj;
    public static final Struct voidType = Tab.noType;
    public static final Struct nullType = Tab.nullType;
    public static final Struct intType = Tab.intType;
    public static final Struct charType = Tab.charType;
    public static final Struct boolType = new Struct(Struct.Bool);
    public static final Struct setType = new Struct(Set);

    private Obj currentTypeSymbol = null;
    private Obj currentMethodSymbol = null;
    private Obj currentClassSymbol = null;
    private Obj currentInterfaceSymbol = null;

    private boolean isErrorDetected = false;
    private boolean isMainMethodDetected = false;
    private boolean isReturnStatementDetected = false;

    private int whileLoopNestingLevel = 0;

    private final Deque<List<Struct>> allFunctionCallArgumentsTypes = new ArrayDeque<>();

    private final Logger logger = Logger.getLogger(getClass());
    private final Set<Obj> classTypeSymbols = new HashSet<>();
    private final Set<Obj> interfaceTypeSymbols = new HashSet<>();
    private static final String BOLD_UNDERLINE = "", RESET = "";
    private static final String[] objKindStrings = { "Con", "Var", "Type", "Meth", "Fld", "Elem", "Prog"};
    private static final String[] structKindStrings = { "void", "int", "char", "Array", "Class", "bool", "enum", "Interface", "Set" };

    // </editor-fold>

    // <editor-fold desc="[SemanticAnalyzer initializers]">

    public SemanticAnalyzer()
    {
        initializeSymbolTable();
    }

    // </editor-fold>

    // <editor-fold desc="[SemanticAnalyzer initializers' helper methods]">

    private void initializeSymbolTable()
    {
        Tab.init();

        Tab.find("eol").setLevel(-1);
        Tab.find("null").setLevel(-1);

        Obj boolTypeSymbol = Tab.insert(Obj.Type, "bool", boolType);
        boolTypeSymbol.setAdr(-1);
        boolTypeSymbol.setLevel(-1);

        setType.setElementType(intType);
        Obj setTypeSymbol = Tab.insert(Obj.Type, "set", setType);
        setTypeSymbol.setAdr(-1);
        setTypeSymbol.setLevel(-1);

        Tab.find("chr").setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        Tab.find("ord").setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        Tab.find("len").setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);

        Tab.find("chr").getLocalSymbols().iterator().next().setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
        Tab.find("ord").getLocalSymbols().iterator().next().setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
        Tab.find("len").getLocalSymbols().iterator().next().setFpPos(VAR_FP_POS_FORMAL_PARAMETER);

        Obj addMethodSymbol = new Obj(Obj.Meth, "add", voidType, 0, 2);
        addMethodSymbol.setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        Tab.currentScope.addToLocals(addMethodSymbol);
        {
            Tab.openScope();

            Obj formalParameterSymbol1 = new Obj(Obj.Var, "destinationSet", setType, 0, 1);
            Obj formalParameterSymbol2 = new Obj(Obj.Var, "integerToAdd", intType, 1, 1);

            formalParameterSymbol1.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
            formalParameterSymbol2.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);

            Obj localVariableSymbol1 = new Obj(Obj.Var, "setSize", intType, 2, 1);
            Obj localVariableSymbol2 = new Obj(Obj.Var, "currentSetIndex", intType, 3, 1);

            localVariableSymbol1.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);
            localVariableSymbol2.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);

            Tab.currentScope.addToLocals(formalParameterSymbol1);
            Tab.currentScope.addToLocals(formalParameterSymbol2);
            Tab.currentScope.addToLocals(localVariableSymbol1);
            Tab.currentScope.addToLocals(localVariableSymbol2);
            addMethodSymbol.setLocals(Tab.currentScope.getLocals());

            Tab.closeScope();
        }

        Obj addAllMethodSymbol = new Obj(Obj.Meth, "addAll", voidType, 0, 2);
        addAllMethodSymbol.setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        Tab.currentScope.addToLocals(addAllMethodSymbol);
        {
            Tab.openScope();

            Struct intArrayType = new Struct(Struct.Array, intType);
            Obj formalParameterSymbol1 = new Obj(Obj.Var, "destinationSet", setType, 0, 1);
            Obj formalParameterSymbol2 = new Obj(Obj.Var, "arrayOfIntegersToAdd", intArrayType, 1, 1);

            formalParameterSymbol1.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
            formalParameterSymbol2.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);

            Obj localVariableSymbol1 = new Obj(Obj.Var, "currentArrayIndex", intType, 2, 1);

            localVariableSymbol1.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);

            Tab.currentScope.addToLocals(formalParameterSymbol1);
            Tab.currentScope.addToLocals(formalParameterSymbol2);
            Tab.currentScope.addToLocals(localVariableSymbol1);
            addAllMethodSymbol.setLocals(Tab.currentScope.getLocals());

            Tab.closeScope();
        }

        Obj printSetMethodSymbol = new Obj(Obj.Meth, "printSet", voidType, 0, 2);
        printSetMethodSymbol.setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        Tab.currentScope.addToLocals(printSetMethodSymbol);
        {
            Tab.openScope();

            Obj formalParameterSymbol1 = new Obj(Obj.Var, "setAddress", setType, 0, 1);
            Obj formalParameterSymbol2 = new Obj(Obj.Var, "printWidth", intType, 1, 1);

            formalParameterSymbol1.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
            formalParameterSymbol2.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);

            Obj localVariableSymbol1 = new Obj(Obj.Var, "setSize", intType, 2, 1);
            Obj localVariableSymbol2 = new Obj(Obj.Var, "currentSetIndex", intType, 3, 1);

            localVariableSymbol1.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);
            localVariableSymbol2.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);

            Tab.currentScope.addToLocals(formalParameterSymbol1);
            Tab.currentScope.addToLocals(formalParameterSymbol2);
            Tab.currentScope.addToLocals(localVariableSymbol1);
            Tab.currentScope.addToLocals(localVariableSymbol2);
            printSetMethodSymbol.setLocals(Tab.currentScope.getLocals());

            Tab.closeScope();
        }

        Obj setsUnionMethodSymbol = new Obj(Obj.Meth, "setsUnion", voidType, 0, 3);
        setsUnionMethodSymbol.setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        Tab.currentScope.addToLocals(setsUnionMethodSymbol);
        {
            Tab.openScope();

            Obj formalParameterSymbol1 = new Obj(Obj.Var, "destinationSet", setType, 0, 1);
            Obj formalParameterSymbol2 = new Obj(Obj.Var, "leftSet", setType, 1, 1);
            Obj formalParameterSymbol3 = new Obj(Obj.Var, "rightSet", setType, 2, 1);

            formalParameterSymbol1.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
            formalParameterSymbol2.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
            formalParameterSymbol3.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);

            Obj localVariableSymbol1 = new Obj(Obj.Var, "sourceSetSize", intType, 3, 1);
            Obj localVariableSymbol2 = new Obj(Obj.Var, "currentSourceSetIndex", intType, 4, 1);

            localVariableSymbol1.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);
            localVariableSymbol2.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);

            Tab.currentScope.addToLocals(formalParameterSymbol1);
            Tab.currentScope.addToLocals(formalParameterSymbol2);
            Tab.currentScope.addToLocals(formalParameterSymbol3);
            Tab.currentScope.addToLocals(localVariableSymbol1);
            Tab.currentScope.addToLocals(localVariableSymbol2);
            setsUnionMethodSymbol.setLocals(Tab.currentScope.getLocals());

            Tab.closeScope();
        }

        logDebug("Symbol table initialized.",null);
    }

    // </editor-fold>

    // <editor-fold desc="[SemanticAnalyzer logging helper methods]">

    private void logSymbolUsageDetection(Obj symbol, SyntaxNode syntaxNode)
    {
        switch (symbol.getKind())
        {
            case Obj.Con:
                logSymbol("global constant usage detected", symbol, syntaxNode);
                break;
            case Obj.Var:
                if (symbol.getFpPos() == VAR_FP_POS_GLOBAL_VARIABLE)
                {
                    logSymbol("global variable usage detected", symbol, syntaxNode);
                }
                else if (symbol.getFpPos() == VAR_FP_POS_LOCAL_VARIABLE)
                {
                    logSymbol("local variable usage detected", symbol, syntaxNode);
                }
                else if (symbol.getFpPos() == VAR_FP_POS_FORMAL_PARAMETER)
                {
                    logSymbol("formal parameter usage detected", symbol, syntaxNode);
                }
                break;
            case Obj.Fld:
                logSymbol("class attribute usage detected", symbol, syntaxNode);
                break;
            case Obj.Meth:
                if (symbol.getFpPos() == METH_FP_POS_UNIMPLEMENTED_INTERFACE_METHOD)
                {
                    logSymbol("unimplemented interface method usage detected", symbol, syntaxNode);
                }
                else if (symbol.getFpPos() == METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD)
                {
                    logSymbol("global method usage detected", symbol, syntaxNode);
                }
                else if (symbol.getFpPos() == METH_FP_POS_IMPLEMENTED_INTERFACE_METHOD)
                {
                    logSymbol("implemented interface method usage detected", symbol, syntaxNode);
                }
                else if (symbol.getFpPos() == METH_FP_POS_IMPLEMENTED_CLASS_METHOD)
                {
                    logSymbol("class method usage detected", symbol, syntaxNode);
                }
                break;
        }
    }

    private void logSymbol(String message, Obj symbol, SyntaxNode syntaxNode)
    {
        String typeName = structKindStrings[symbol.getType().getKind()];
        if (symbol.getType().getKind() == Struct.Array && symbol.getType().getElemType().getKind() == Struct.Class)
        {
            for (Obj classTypeSymbol : classTypeSymbols)
            {
                if (classTypeSymbol.getType() == symbol.getType().getElemType())
                {
                    typeName = classTypeSymbol.getName() + "[]";
                    break;
                }
            }
        }
        else if (symbol.getType().getKind() == Struct.Array && symbol.getType().getElemType().getKind() == Struct.Interface)
        {
            for (Obj interfaceTypeSymbol : interfaceTypeSymbols)
            {
                if (interfaceTypeSymbol.getType() == symbol.getType().getElemType())
                {
                    typeName = interfaceTypeSymbol.getName() + "[]";
                    break;
                }
            }
        }
        else if (symbol.getType().getKind() == Struct.Array)
        {
            typeName = structKindStrings[symbol.getType().getElemType().getKind()] + "[]";
        }
        else if (symbol.getType().getKind() == Set)
        {
            typeName = structKindStrings[symbol.getType().getElemType().getKind()] + "{}";
        }
        else if (symbol.getKind() != Obj.Type && symbol.getType().getKind() == Struct.Class)
        {
            for (Obj classTypeSymbol : classTypeSymbols)
            {
                if (classTypeSymbol.getType() == symbol.getType())
                {
                    typeName = classTypeSymbol.getName();
                    break;
                }
            }
        }
        else if (symbol.getKind() != Obj.Type && symbol.getType().getKind() == Struct.Interface)
        {
            for (Obj interfaceTypeSymbol : interfaceTypeSymbols)
            {
                if (interfaceTypeSymbol.getType() == symbol.getType())
                {
                    typeName = interfaceTypeSymbol.getName();
                    break;
                }
            }
        }
        String symbolInfoBuilder = "[" +
                syntaxNode.getClass().getSimpleName() +
                "] " +
                message +
                ": [" +
                BOLD_UNDERLINE + "NAME" + RESET + ": " +
                symbol.getName() +
                "; " + BOLD_UNDERLINE + "KIND" + RESET + ": " +
                objKindStrings[symbol.getKind()] +
                "; " + BOLD_UNDERLINE + "ADR" + RESET + ": " +
                symbol.getAdr() +
                "; " + BOLD_UNDERLINE + "LEVEL" + RESET + ": " +
                symbol.getLevel() +
                "; " + BOLD_UNDERLINE + "FPPOS" + RESET + ": " +
                symbol.getFpPos() +
                "; " + BOLD_UNDERLINE + "TYPE" + RESET + ": " +
                typeName +
                "] (line " +
                syntaxNode.getLine() +
                ")";
        logger.info(symbolInfoBuilder);
    }

    private void logDebug(String message, SyntaxNode syntaxNode)
    {
        StringBuilder infoMessageBuilder = new StringBuilder(message);
        if (syntaxNode != null)
        {
            infoMessageBuilder
                    .append(" (line ")
                    .append(syntaxNode.getLine())
                    .append(")");
        }
        logger.debug(infoMessageBuilder.toString());
    }

    private void logError(String message, SyntaxNode syntaxNode)
    {
        isErrorDetected = true;
        StringBuilder errorMessageBuilder = new StringBuilder(message);
        if (syntaxNode != null)
        {
            errorMessageBuilder
                    .append(" (line ")
                    .append(syntaxNode.getLine())
                    .append(")");
        }
        logger.error(errorMessageBuilder.toString());
    }

    // </editor-fold>

    // <editor-fold desc="[SemanticAnalyzer other helper methods]">

    public boolean isSemanticAnalysisErrorDetected()
    {
        return isErrorDetected;
    }

    private boolean isReferenceType(Struct type)
    {
        return type.getKind() == Struct.Class || type.getKind() == Struct.Array || type.getKind() == Set || type.getKind() == Struct.Interface;
    }

    private boolean areTypesEqual(Struct type1, Struct type2)
    {
        boolean areBothTypesArrays = type1.getKind() == Struct.Array && type2.getKind() == Struct.Array;
        if (areBothTypesArrays)
        {
            return type1.getElemType() == type2.getElemType();
        }
        return type1 == type2;
    }

    private boolean areTypesNotCompatible(Struct type1, Struct type2)
    {
        return !(areTypesEqual(type1, type2) || (areTypesEqual(type1, nullType) && isReferenceType(type2)) || (isReferenceType(type1) && areTypesEqual(type2, nullType)));
    }

    private boolean isSourceNotAssignableToDestination(Struct destination, Struct source)
    {
        if (areTypesEqual(destination, source))
        {
            return false;
        }
        boolean isNullAssignedToReference = isReferenceType(destination) &&  areTypesEqual(source, nullType);
        if (isNullAssignedToReference)
        {
            return false;
        }
        boolean isArrayAssignedToVoidArray = destination.getKind() == Struct.Array && source.getKind() == Struct.Array && areTypesEqual(destination.getElemType(), voidType);
        if (isArrayAssignedToVoidArray)
        {
            return false;
        }
        boolean areBothTypesClasses = destination.getKind() == Struct.Class && source.getKind() == Struct.Class;
        if (areBothTypesClasses && isSourceDerivedClassFromDestination(destination, source))
        {
            return false;
        }
        boolean isClassAssignedToInterface = destination.getKind() == Struct.Interface && source.getKind() == Struct.Class;
        return !(isClassAssignedToInterface && doesExprClassImplementDesignatorInterface(destination, source));
    }

    private boolean isSourceDerivedClassFromDestination(Struct destination, Struct source)
    {
        Struct currentSourceType = source.getElemType();
        while (currentSourceType != null)
        {
            if (areTypesEqual(destination, currentSourceType))
            {
                return true;
            }
            currentSourceType = currentSourceType.getElemType();
        }
        return false;
    }

    private boolean doesExprClassImplementDesignatorInterface(Struct designatorType, Struct exprType)
    {
        for (Struct implementedInterface : exprType.getImplementedInterfaces())
        {
            if (areTypesEqual(designatorType, implementedInterface))
            {
                return true;
            }
        }
        return false;
    }

    private boolean isDesignatorNotModifiable(Obj designatorSymbol)
    {
        return designatorSymbol.getKind() != Obj.Var && designatorSymbol.getKind() != Obj.Elem && designatorSymbol.getKind() != Obj.Fld;
    }

    private boolean isSymbolNotInteger(Obj symbol)
    {
        return symbol.getKind() == Obj.Type || symbol.getKind() == Obj.Meth || !areTypesEqual(symbol.getType(), intType);
    }

    private boolean isExprSymbolInvalidForPrinting(Obj exprSymbol)
    {
        Struct exprSymbolType = exprSymbol.getType();
        return !areTypesEqual(exprSymbolType, intType) && !areTypesEqual(exprSymbolType, charType) && !areTypesEqual(exprSymbolType, boolType) && !areTypesEqual(exprSymbolType, setType);
    }

    private boolean isDesignatorSymbolTypeInvalidForReading(Obj designatorSymbol)
    {
        Struct designatorType = designatorSymbol.getType();
        return !areTypesEqual(designatorType, intType) && !areTypesEqual(designatorType, charType) && !areTypesEqual(designatorType, boolType);
    }

    private Collection<Obj> getMethodFormalParameters(Obj methodSymbol)
    {
        return methodSymbol.getLocalSymbols().stream()
                .filter(localSymbol -> localSymbol.getFpPos() == VAR_FP_POS_FORMAL_PARAMETER)
                .collect(Collectors.toList());
    }

    private boolean isDesignatorMappingFunctionInvalid(Obj leftDesignatorSymbol)
    {
        if (leftDesignatorSymbol.getKind() != Obj.Meth)
        {
            return true;
        }
        if (!areTypesEqual(leftDesignatorSymbol.getType(), intType))
        {
            return true;
        }
        Collection<Obj> methodFormalParameters = getMethodFormalParameters(leftDesignatorSymbol);
        if (methodFormalParameters.size() != 1)
        {
            return true;
        }
        return !areTypesEqual(methodFormalParameters.iterator().next().getType(), intType);
    }

    private boolean isBaseTypeInvalid(Obj baseTypeSymbol, ClassNameExtendsBaseTypeName classNameExtendsBaseTypeName)
    {
        String baseTypeString = classNameExtendsBaseTypeName.getBaseTypeString();
        if (baseTypeSymbol == voidSymbol)
        {
            logError("[ClassNameExtendsBaseTypeName] syntax node ERROR: Base type name '" + baseTypeString + "' is not defined", classNameExtendsBaseTypeName);
            return true;
        }
        if (baseTypeSymbol.getKind() != Obj.Type || (baseTypeSymbol.getType().getKind() != Struct.Class && baseTypeSymbol.getType().getKind() != Struct.Interface))
        {
            logError("[ClassNameExtendsBaseTypeName] syntax node ERROR: Base type name '" + baseTypeString + "' is neither a class nor an interface", classNameExtendsBaseTypeName);
            return true;
        }
        return false;
    }

    private Obj getInheritedClassMethod(Struct classType, String methodName)
    {
        Struct currentClassType = classType.getElemType();
        while (currentClassType != null)
        {
            for (Obj classMember : currentClassType.getMembers())
            {
                if (classMember.getKind() == Obj.Meth && classMember.getName().equals(methodName))
                {
                    return classMember;
                }
            }
            currentClassType = currentClassType.getElemType();
        }
        return voidSymbol;
    }

    private Obj getInheritedInterfaceImplementedMethod(Struct classType, String methodName)
    {
        for (Struct implementedInterface : classType.getImplementedInterfaces())
        {
            for (Obj interfaceMethod : implementedInterface.getMembers())
            {
                if (interfaceMethod.getFpPos() == METH_FP_POS_IMPLEMENTED_INTERFACE_METHOD && interfaceMethod.getName().equals(methodName))
                {
                    return interfaceMethod;
                }
            }
        }
        return voidSymbol;
    }
    
    private boolean isDesignatorClassAccessInvalid(DesignatorUserTypeAccess designatorClassAccess, Obj designatorIdentificatorSymbol)
    {
        boolean isCurrentClassAccessedInCurrentClassMethod = currentClassSymbol != null && currentMethodSymbol != null && areTypesEqual(designatorIdentificatorSymbol.getType(), currentClassSymbol.getType());
        Collection<Obj> designatorClassMembers = isCurrentClassAccessedInCurrentClassMethod ? Tab.currentScope.getOuter().getLocals().symbols() : designatorIdentificatorSymbol.getType().getMembers();
        for (Obj designatorClassMember : designatorClassMembers)
        {
            if (designatorClassMember.getName().equals(designatorClassAccess.getMemberNameString()))
            {
                designatorClassAccess.obj = designatorClassMember;
                logSymbol("class " + (designatorClassMember.getKind() == Obj.Fld ? "attribute" : "method") + " access detected", designatorClassAccess.obj, designatorClassAccess);
                return false;
            }
        }
        Obj inheritedClassMethodSymbol = getInheritedClassMethod(designatorIdentificatorSymbol.getType(), designatorClassAccess.getMemberNameString());
        if (inheritedClassMethodSymbol != voidSymbol)
        {
            designatorClassAccess.obj = inheritedClassMethodSymbol;
            logSymbol("class method (implementation inherited from base class) access detected", designatorClassAccess.obj, designatorClassAccess);
            return false;
        }
        Obj inheritedInterfaceImplementedMethodSymbol = getInheritedInterfaceImplementedMethod(designatorIdentificatorSymbol.getType(), designatorClassAccess.getMemberNameString());
        if (inheritedInterfaceImplementedMethodSymbol != voidSymbol)
        {
            designatorClassAccess.obj = inheritedInterfaceImplementedMethodSymbol;
            logSymbol("class method (implementation inherited from implemented interface) access deteced", designatorClassAccess.obj, designatorClassAccess);
            return false;
        }
        logError("[DesignatorUserTypeAccess] Class member '" + designatorClassAccess.getMemberNameString() + "' is not defined in the '" + designatorIdentificatorSymbol.getName() + "' object", designatorClassAccess);
        designatorClassAccess.obj = voidSymbol;
        return true;
    }

    private boolean isDesignatorInterfaceAccessInvalid(DesignatorUserTypeAccess designatorInterfaceAccess, Obj designatorIdentificatorSymbol)
    {
        boolean isCurrentInterfaceAccessedInCurrentInterfaceMethod = currentInterfaceSymbol != null && currentMethodSymbol != null && areTypesEqual(designatorIdentificatorSymbol.getType(), currentInterfaceSymbol.getType());
        Collection<Obj> designatorInterfaceMethods = isCurrentInterfaceAccessedInCurrentInterfaceMethod ? Tab.currentScope.getOuter().getLocals().symbols() : designatorIdentificatorSymbol.getType().getMembers();
        for (Obj designatorInterfaceMethod : designatorInterfaceMethods)
        {
            if (designatorInterfaceMethod.getName().equals(designatorInterfaceAccess.getMemberNameString()))
            {
                designatorInterfaceAccess.obj = designatorInterfaceMethod;
                String logSymbolMessage = designatorInterfaceMethod.getFpPos() == METH_FP_POS_UNIMPLEMENTED_INTERFACE_METHOD ? "unimplemented interface method access detected" : "implemented interface method access detected";
                logSymbol(logSymbolMessage, designatorInterfaceAccess.obj, designatorInterfaceAccess);
                return false;
            }
        }
        logError("[DesignatorUserTypeAccess] Interface method '" + designatorInterfaceAccess.getMemberNameString() + "' is not defined in the '" + designatorIdentificatorSymbol.getName() + "' interface", designatorInterfaceAccess);
        designatorInterfaceAccess.obj = voidSymbol;
        return true;
    }

    private boolean doesClassNotImplementAllUnimplementedInterfaceMethods()
    {
        Struct currentClassType = currentClassSymbol.getType();
        List<Obj> unimplementedInterfaceMethods = getUnimplementedInterfaceMethods(currentClassType);
        List<Obj> implementedClassMethods = getImplementedClassMethods(currentClassType);
        for (Obj unimplementedInterfaceMethod : unimplementedInterfaceMethods)
        {
            boolean isMethodImplemented = false;
            for (Obj implementedClassMethod : implementedClassMethods)
            {
                if (areMethodSignaturesEquivalent(unimplementedInterfaceMethod, implementedClassMethod))
                {
                    isMethodImplemented = true;
                    break;
                }
            }
            if (!isMethodImplemented)
            {
                return true;
            }
        }
        return false;
    }

    private List<Obj> getUnimplementedInterfaceMethods(Struct classType)
    {
        List<Obj> unimplementedInterfaceMethods = new ArrayList<>();
        for (Struct implementedInterface : classType.getImplementedInterfaces())
        {
            for (Obj interfaceMethod : implementedInterface.getMembers())
            {
                if (interfaceMethod.getFpPos() == METH_FP_POS_UNIMPLEMENTED_INTERFACE_METHOD)
                {
                    unimplementedInterfaceMethods.add(interfaceMethod);
                }
            }
        }
        return unimplementedInterfaceMethods;
    }

    private List<Obj> getImplementedClassMethods(Struct classType)
    {
        Struct currentClassType = classType;
        List<Obj> implementedClassMethods = new ArrayList<>();
        while (currentClassType != null)
        {
            for (Obj classMember : currentClassType.getMembers())
            {
                if (classMember.getKind() == Obj.Meth && classMember.getFpPos() == METH_FP_POS_IMPLEMENTED_CLASS_METHOD)
                {
                    implementedClassMethods.add(classMember);
                }
            }
            currentClassType = currentClassType.getElemType();
        }
        return implementedClassMethods;
    }

    private boolean areMethodSignaturesEquivalent(Obj interfaceMethodSymbol, Obj classMethodSymbol)
    {
        boolean areMethodReturnTypesEqual = areTypesEqual(interfaceMethodSymbol.getType(), classMethodSymbol.getType());
        if (!areMethodReturnTypesEqual)
        {
            return false;
        }
        boolean areMethodNamesEqual = interfaceMethodSymbol.getName().equals(classMethodSymbol.getName());
        if (!areMethodNamesEqual)
        {
            return false;
        }
        List<Obj> interfaceMethodFormalParameters = new ArrayList<>(getMethodFormalParameters(interfaceMethodSymbol));
        List<Obj> classMethodFormalParameters = new ArrayList<>(getMethodFormalParameters(classMethodSymbol));
        interfaceMethodFormalParameters.removeIf(formalParameter -> formalParameter.getName().equals("this"));
        classMethodFormalParameters.removeIf(formalParameter -> formalParameter.getName().equals("this"));
        boolean areFormalParametersListSizesEqual = interfaceMethodFormalParameters.size() == classMethodFormalParameters.size();
        if (!areFormalParametersListSizesEqual)
        {
            return false;
        }
        Iterator<Obj> method1FormalParametersIterator = interfaceMethodFormalParameters.iterator();
        Iterator<Obj> method2FormalParametersIterator = classMethodFormalParameters.iterator();
        while (method1FormalParametersIterator.hasNext() && method2FormalParametersIterator.hasNext())
        {
            Obj method1FormalParameter = method1FormalParametersIterator.next();
            Obj method2FormalParameter = method2FormalParametersIterator.next();
            boolean areFormalParameterTypesEqual = areTypesEqual(method1FormalParameter.getType(), method2FormalParameter.getType());
            if (!areFormalParameterTypesEqual)
            {
                return false;
            }
        }
        return true;
    }

    private boolean areFunctionCallArgumentsInvalid(Obj methodSymbol)
    {
        List<Obj> methodFormalParameters = new ArrayList<>(getMethodFormalParameters(methodSymbol));
        methodFormalParameters.removeIf(formalParameter -> formalParameter.getName().equals("this"));
        List<Struct> currentFunctionCallArgumentsTypes = allFunctionCallArgumentsTypes.pop();
        
        if (methodFormalParameters.size() != currentFunctionCallArgumentsTypes.size())
        {
            return true;
        }

        Iterator<Obj> methodFormalParametersIterator = methodFormalParameters.iterator();
        Iterator<Struct> currentFunctionCallArgumentsTypesIterator = currentFunctionCallArgumentsTypes.iterator();

        while (methodFormalParametersIterator.hasNext() && currentFunctionCallArgumentsTypesIterator.hasNext())
        {
            Obj methodFormalParameter = methodFormalParametersIterator.next();
            Struct functionCallArgumentType = currentFunctionCallArgumentsTypesIterator.next();
            boolean isFunctionCallArgumentNotAssignableToFormalParameter = isSourceNotAssignableToDestination(methodFormalParameter.getType(), functionCallArgumentType);
            if (isFunctionCallArgumentNotAssignableToFormalParameter)
            {
                return true;
            }
        }

        return false;
    }

    // </editor-fold>

    // <editor-fold desc="[Program]">

    @Override
    public void visit(ProgramName programName)
    {
        boolean isProgramNameAlreadyUsed = !(Tab.find(programName.getProgramNameString()) == voidSymbol);
        String finalProgramName = isProgramNameAlreadyUsed ? "InvalidProgramName" : programName.getProgramNameString();
        programName.obj = Tab.insert(Obj.Prog, finalProgramName, voidType);
        logSymbol("program symbol inserted", programName.obj, programName);
        Tab.openScope();
        if (isProgramNameAlreadyUsed)
        {
            logError("[ProgramName] syntax node ERROR: Built-in keyword '" + programName.getProgramNameString() + "' is not a valid program name", programName);
            return;
        }
        logDebug("[ProgramName] syntax node SUCCESS", programName);
    }

    @Override
    public void visit(Program program)
    {
        program.obj = program.getProgramName().obj;
        Code.dataSize = Tab.currentScope.getnVars();
        Tab.chainLocalSymbols(program.obj);
        Tab.closeScope();
        if (!isMainMethodDetected)
        {
            logError("[Program] syntax node ERROR: Main method is not defined", program);
            return;
        }
        logDebug("[Program] syntax node SUCCESS", program);
    }

    // </editor-fold>

    // <editor-fold desc="[ConstDecl]">

    @Override
    public void visit(ConstantNumber constantNumber)
    {
        constantNumber.obj = new Obj(
                Obj.Con,
                "ConstantNumber",
                intType,
                constantNumber.getValue(),
                0
        );
        logDebug("[ConstantNumber] syntax node SUCCESS", constantNumber);
    }

    @Override
    public void visit(ConstantCharacter constantCharacter)
    {
        constantCharacter.obj = new Obj(
                Obj.Con,
                "ConstantCharacter",
                charType,
                constantCharacter.getValue(),
                0
        );
        logDebug("[ConstantCharacter] syntax node SUCCESS", constantCharacter);
    }

    @Override
    public void visit(ConstantBoolean constantBoolean)
    {
        constantBoolean.obj = new Obj(
                Obj.Con,
                "ConstantBoolean",
                boolType,
                constantBoolean.getValue() ? 1 : 0,
                0
        );
        logDebug("[ConstantBoolean] syntax node SUCCESS", constantBoolean);
    }

    @Override
    public void visit(ConstantAssignment constantAssignment)
    {
        String constantNameString = constantAssignment.getConstantNameString();
        if (Tab.currentScope.findSymbol(constantNameString) != null)
        {
            logError("[ConstantAssignment] syntax node ERROR: Name '" + constantNameString + "' already defined in the current scope", constantAssignment);
            return;
        }

        Obj constantAssignmentSymbol = Tab.insert(
                Obj.Con,
                constantNameString,
                currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType
        );
        constantAssignmentSymbol.setAdr(constantAssignment.getConstant().obj.getAdr());
        logSymbol("global constant symbol inserted", constantAssignmentSymbol, constantAssignment);

        if (currentTypeSymbol == null)
        {
            logError("[ConstantAssignment] syntax node ERROR: Invalid type when defining constant '" + constantNameString + "'", constantAssignment);
            return;
        }
        if (!areTypesEqual(constantAssignment.getConstant().obj.getType(), currentTypeSymbol.getType()))
        {
            logError("[ConstantAssignment] syntax node ERROR: Incompatible types when defining constant '" + constantNameString + "'", constantAssignment);
            return;
        }
        logDebug("[ConstantAssignment] syntax node SUCCESS", constantAssignment);
    }

    @Override
    public void visit(ConstDecl constDecl)
    {
        if (currentTypeSymbol == null)
        {
            logError("[ConstDecl] syntax node ERROR: Invalid type when defining global constants", constDecl);
            return;
        }
        currentTypeSymbol = null;
        logDebug("[ConstDecl] syntax node SUCCESS", constDecl);
    }

    // </editor-fold>

    // <editor-fold desc="[VarDecl]">

    @Override
    public void visit(VariableSingle variableSingle)
    {
        String variableNameString = variableSingle.getVariableNameString();
        if (Tab.currentScope.findSymbol(variableNameString) != null)
        {
            logError("[VariableSingle] syntax node ERROR: Name '" + variableNameString + "' already defined in the current scope", variableSingle);
            return;
        }

        Obj variableSingleSymbol = Tab.insert(
                Obj.Var,
                variableNameString,
                currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType
        );

        boolean isMethodLocalVariable = currentMethodSymbol != null;
        boolean isClassMethodLocalVariable = currentClassSymbol != null && isMethodLocalVariable;
        boolean isInterfaceMethodLocalVariable = currentInterfaceSymbol != null && isMethodLocalVariable;
        if (isClassMethodLocalVariable || isInterfaceMethodLocalVariable)
        {
            variableSingleSymbol.setLevel(VAR_LEVEL_LOCAL_VARIABLE_CLASS_OR_INTERFACE_METHOD);
        }
        else if (isMethodLocalVariable)
        {
            variableSingleSymbol.setLevel(VAR_LEVEL_LOCAL_VARIABLE_GLOBAL_METHOD);
        }
        else
        {
            variableSingleSymbol.setLevel(VAR_LEVEL_GLOBAL_VARIABLE);
        }

        if (variableSingleSymbol.getLevel() == 0)
        {
            variableSingleSymbol.setFpPos(VAR_FP_POS_GLOBAL_VARIABLE);
        }
        else
        {
            variableSingleSymbol.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);
        }
        logSymbol((variableSingleSymbol.getFpPos() == VAR_FP_POS_GLOBAL_VARIABLE ? "global" : "local") + " variable symbol inserted", variableSingleSymbol, variableSingle);

        if (currentTypeSymbol == null)
        {
            logError("[VariableSingle] syntax node ERROR: Invalid type when defining variable '" + variableNameString + "'", variableSingle);
            return;
        }
        logDebug("[VariableSingle] syntax node SUCCESS", variableSingle);
    }

    @Override
    public void visit(VariableArray variableArray)
    {
        String variableNameString = variableArray.getVariableNameString();
        if (Tab.currentScope.findSymbol(variableNameString) != null)
        {
            logError("[VariableArray] syntax node ERROR: Name '" + variableNameString + "' already defined in the current scope", variableArray);
            return;
        }

        Obj variableArraySymbol = Tab.insert(
                Obj.Var,
                variableNameString,
                new Struct(Struct.Array, currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType)
        );

        boolean isMethodLocalVariable = currentMethodSymbol != null;
        boolean isClassMethodLocalVariable = currentClassSymbol != null && isMethodLocalVariable;
        boolean isInterfaceMethodLocalVariable = currentInterfaceSymbol != null && isMethodLocalVariable;
        if (isClassMethodLocalVariable || isInterfaceMethodLocalVariable)
        {
            variableArraySymbol.setLevel(VAR_LEVEL_LOCAL_VARIABLE_CLASS_OR_INTERFACE_METHOD);
        }
        else if (isMethodLocalVariable)
        {
            variableArraySymbol.setLevel(VAR_LEVEL_LOCAL_VARIABLE_GLOBAL_METHOD);
        }
        else
        {
            variableArraySymbol.setLevel(VAR_LEVEL_GLOBAL_VARIABLE);
        }


        if (variableArraySymbol.getLevel() == 0)
        {
            variableArraySymbol.setFpPos(VAR_FP_POS_GLOBAL_VARIABLE);
        }
        else
        {
            variableArraySymbol.setFpPos(VAR_FP_POS_LOCAL_VARIABLE);
        }
        logSymbol((variableArraySymbol.getFpPos() == VAR_FP_POS_GLOBAL_VARIABLE ? "global" : "local") + " variable symbol inserted", variableArraySymbol, variableArray);

        if (currentTypeSymbol == null)
        {
            logError("[VariableArray] syntax node ERROR: Invalid type when defining variable '" + variableNameString + "'", variableArray);
            return;
        }
        logDebug("[VariableArray] syntax node SUCCESS", variableArray);
    }

    @Override
    public void visit(VarDecl varDecl)
    {
        if (currentTypeSymbol == null)
        {
            logError("[VarDecl] syntax node ERROR: Invalid type when defining variables", varDecl);
            return;
        }
        currentTypeSymbol = null;
        logDebug("[VarDecl] syntax node SUCCESS", varDecl);
    }

    // </editor-fold>

    // <editor-fold desc="[ClassDecl]">

    @Override
    public void visit(ClassName className)
    {
        if (Tab.currentScope.findSymbol(className.getClassNameString()) != null)
        {
            logError("[ClassName] syntax node ERROR: Name '" + className.getClassNameString() + "' already defined in the current scope", className);
            return;
        }
        currentClassSymbol = Tab.insert(Obj.Type, className.getClassNameString(), new Struct(Struct.Class));
        classTypeSymbols.add(currentClassSymbol);
        Tab.openScope();
        Tab.insert(Obj.Fld, "[virtualMethodTableAddress]", intType);
        logSymbol("class symbol inserted", currentClassSymbol, className);
        logDebug("[ClassName] syntax node SUCCESS", className);
    }

    @Override
    public void visit(ClassNameExtendsBaseTypeName classNameExtendsBaseTypeName)
    {
        String classNameString = classNameExtendsBaseTypeName.getClassNameString();
        if (Tab.currentScope.findSymbol(classNameString) != null)
        {
            logError("[ClassNameExtendsBaseTypeName] syntax node ERROR: Name '" + classNameString + "' already defined in the current scope", classNameExtendsBaseTypeName);
            return;
        }

        currentClassSymbol = Tab.insert(Obj.Type, classNameString, new Struct(Struct.Class));
        classTypeSymbols.add(currentClassSymbol);
        Tab.openScope();
        Tab.insert(Obj.Fld, "[virtualMethodTableAddress]", intType);
        logSymbol("class symbol with inheritance inserted", currentClassSymbol, classNameExtendsBaseTypeName);

        Obj baseTypeSymbol = Tab.find(classNameExtendsBaseTypeName.getBaseTypeString());
        if (isBaseTypeInvalid(baseTypeSymbol, classNameExtendsBaseTypeName))
        {
            return;
        }

        if (baseTypeSymbol.getType().getKind() == Struct.Class)
        {
            currentClassSymbol.getType().setElementType(baseTypeSymbol.getType());
            // inheritance - copy base class implemented interfaces to the derived class
            for (Struct implementedInterface : baseTypeSymbol.getType().getImplementedInterfaces())
            {
                currentClassSymbol.getType().addImplementedInterface(implementedInterface);
            }
        }
        else
        {
            currentClassSymbol.getType().addImplementedInterface(baseTypeSymbol.getType());
        }

        // inheritance - copy base class attributes to the derived class
        for (Obj member: baseTypeSymbol.getType().getMembers())
        {
            if (member.getKind() == Obj.Fld)
            {
                Tab.currentScope.addToLocals(member);
            }
        }

        logDebug("[ClassNameExtendsBaseTypeName] syntax node SUCCESS", classNameExtendsBaseTypeName);
    }

    @Override
    public void visit(ClassAttributeSingle classAttributeSingle)
    {
        String classAttributeNameString = classAttributeSingle.getClassAttributeNameString();
        if (Tab.currentScope.findSymbol(classAttributeNameString) != null)
        {
            logError("[ClassAttributeSingle] syntax node ERROR: Name '" + classAttributeNameString + "' already defined in the current scope", classAttributeSingle);
            return;
        }

        Obj classAttributeSymbol = Tab.insert(
                Obj.Fld,
                classAttributeNameString,
                currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType
        );
        logSymbol("class attribute symbol inserted", classAttributeSymbol, classAttributeSingle);

        if (currentTypeSymbol == null)
        {
            logError("[ClassAttributeSingle] syntax node ERROR: Invalid type when defining class attribute '" + classAttributeNameString + "'", classAttributeSingle);
            return;
        }
        logDebug("[ClassAttributeSingle] syntax node SUCCESS", classAttributeSingle);
    }

    @Override
    public void visit(ClassAttributeArray classAttributeArray)
    {
        String classAttributeNameString = classAttributeArray.getClassAttributeNameString();
        if (Tab.currentScope.findSymbol(classAttributeNameString) != null)
        {
            logError("[ClassAttributeArray] syntax node ERROR: Name '" + classAttributeNameString + "' already defined in the current scope", classAttributeArray);
            return;
        }

        Obj classAttributeSymbol = Tab.insert(
                Obj.Fld,
                classAttributeNameString,
                new Struct(Struct.Array, currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType)
        );
        logSymbol("class attribute symbol inserted", classAttributeSymbol, classAttributeArray);

        if (currentTypeSymbol == null)
        {
            logError("[ClassAttributeArray] syntax node ERROR: Invalid type when defining class attribute '" + classAttributeNameString + "'", classAttributeArray);
            return;
        }
        logDebug("[ClassAttributeArray] syntax node SUCCESS", classAttributeArray);
    }

    @Override
    public void visit(ClassAttributeDeclSingle classAttributeDeclSingle)
    {
        if (currentTypeSymbol == null)
        {
            logError("[ClassAttributeDeclSingle] syntax node ERROR: Invalid type when defining class attributes", classAttributeDeclSingle);
            return;
        }
        currentTypeSymbol = null;
        logDebug("[ClassAttributeDeclSingle] syntax node SUCCESS", classAttributeDeclSingle);
    }

    @Override
    public void visit(ClassDecl classDecl)
    {
        if (currentClassSymbol == null)
        {
            logError("[ClassDecl] syntax node ERROR: Name used for the class is already defined in the current scope", classDecl);
            return;
        }
        classDecl.obj = currentClassSymbol;
        Tab.chainLocalSymbols(currentClassSymbol.getType());
        Tab.closeScope();
        if (doesClassNotImplementAllUnimplementedInterfaceMethods())
        {
            logError("[ClassDecl] syntax node ERROR: Class '" + currentClassSymbol.getName() + "' does not implement all unimplemented interface methods", classDecl);
        }
        else
        {
            logDebug("[ClassDecl] syntax node SUCCESS", classDecl);
        }
        currentClassSymbol = null;
    }

    // </editor-fold>

    // <editor-fold desc="[InterfaceDecl]">

    @Override
    public void visit(InterfaceName interfaceName)
    {
        String interfaceNameString = interfaceName.getInterfaceNameString();
        if (Tab.currentScope.findSymbol(interfaceNameString) != null)
        {
            logError("[InterfaceName] syntax node ERROR: Name '" + interfaceNameString + "' already defined in the current scope", interfaceName);
            return;
        }
        currentInterfaceSymbol = Tab.insert(Obj.Type, interfaceNameString, new Struct(Struct.Interface));
        interfaceTypeSymbols.add(currentInterfaceSymbol);
        Tab.openScope();
        logSymbol("interface symbol inserted", currentInterfaceSymbol, interfaceName);
        logDebug("[InterfaceName] syntax node SUCCESS", interfaceName);
    }

    @Override
    public void visit(InterfaceMethodMethodSignature interfaceMethodMethodSignature)
    {
        String methodName = interfaceMethodMethodSignature.getMethodSignature().getMethodName().getMethodNameString();
        if (currentMethodSymbol == null)
        {
            logError("[InterfaceMethodMethodSignature] syntax node ERROR: Name '" + methodName + "' already defined in the current scope", interfaceMethodMethodSignature);
            return;
        }
        currentMethodSymbol.setFpPos(METH_FP_POS_UNIMPLEMENTED_INTERFACE_METHOD);
        Tab.chainLocalSymbols(currentMethodSymbol);
        Tab.closeScope();
        currentMethodSymbol = null;
        logDebug("[InterfaceMethodMethodSignature] syntax node SUCCESS", interfaceMethodMethodSignature);
    }

    @Override
    public void visit(InterfaceDecl interfaceDecl)
    {
        if (currentInterfaceSymbol == null)
        {
            logError("[InterfaceDecl] syntax node ERROR: Name used for the interface is already defined in the current scope", interfaceDecl);
            return;
        }
        Tab.chainLocalSymbols(currentInterfaceSymbol.getType());
        Tab.closeScope();
        currentInterfaceSymbol = null;
        logDebug("[InterfaceDecl] syntax node SUCCESS", interfaceDecl);
    }

    // </editor-fold>

    // <editor-fold desc="[MethodSignature]">

    @Override
    public void visit(MethodReturnTypeVoid methodReturnTypeVoid)
    {
        currentTypeSymbol = new Obj(Obj.Type, "void", voidType);
        logDebug("[MethodReturnTypeVoid] syntax node SUCCESS", methodReturnTypeVoid);
    }

    @Override
    public void visit(MethodName methodName)
    {
        String methodNameString = methodName.getMethodNameString();
        if (Tab.currentScope.findSymbol(methodNameString) != null)
        {
            logError("[MethodName] syntax node ERROR: Name '" + methodNameString + "' already defined in the current scope", methodName);
            return;
        }

        boolean isMethodNameMain = methodNameString.equals("main");
        boolean isReturnTypeInvalid = currentTypeSymbol == null;
        boolean isReturnTypeVoid = !isReturnTypeInvalid && areTypesEqual(currentTypeSymbol.getType(), voidType);

        currentMethodSymbol = Tab.insert(Obj.Meth, methodNameString, isReturnTypeInvalid ? nullType : currentTypeSymbol.getType());
        currentMethodSymbol.setLevel(0);
        currentMethodSymbol.setFpPos(METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD);
        currentTypeSymbol = null;
        Tab.openScope();
        logSymbol("method symbol inserted", currentMethodSymbol, methodName);

        if (currentClassSymbol != null || currentInterfaceSymbol != null)
        {
            Obj thisFormalParameterSymbol = Tab.insert(
                    Obj.Var,
                    "this",
                    currentClassSymbol != null ? currentClassSymbol.getType() : currentInterfaceSymbol.getType()
            );
            currentMethodSymbol.setLevel(currentMethodSymbol.getLevel() + 1);
            currentMethodSymbol.setFpPos(currentClassSymbol != null ? METH_FP_POS_IMPLEMENTED_CLASS_METHOD : METH_FP_POS_IMPLEMENTED_INTERFACE_METHOD);
            thisFormalParameterSymbol.setLevel(VAR_LEVEL_FORMAL_PARAMETER_CLASS_OR_INTERFACE_METHOD);
            thisFormalParameterSymbol.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
            logSymbol("formal parameter symbol inserted", thisFormalParameterSymbol, methodName);
        }
        if (isMethodNameMain && currentClassSymbol == null && currentInterfaceSymbol == null)
        {
            isMainMethodDetected = true;
        }
        if (isReturnTypeInvalid)
        {
            logError("[MethodName] syntax node ERROR: Invalid return type when defining method '" + methodNameString + "'", methodName);
            return;
        }
        if (isMethodNameMain && !isReturnTypeVoid && currentClassSymbol == null && currentInterfaceSymbol == null)
        {
            logError("[MethodName] syntax node ERROR: Main method must have void as return type", methodName);
            return;
        }
        methodName.obj = currentMethodSymbol;
        logDebug("[MethodName] syntax node SUCCESS", methodName);
    }

    @Override
    public void visit(MethodSignature methodSignature)
    {
        String methodName = methodSignature.getMethodName().getMethodNameString();
        if (currentMethodSymbol == null)
        {
            logError("[MethodSignature] syntax node ERROR: Name '" + methodName + "' already defined in the current scope", methodSignature);
            return;
        }
        boolean isMethodNameMain = methodName.equals("main");
        boolean areFormalParametersPresent = methodSignature.getOptionalFormPars() instanceof OptionalFormParsSingle;
        if (isMethodNameMain && areFormalParametersPresent && currentClassSymbol == null && currentInterfaceSymbol == null)
        {
            logError("[MethodSignature] syntax node ERROR: Main method must not have formal parameters", methodSignature);
            return;
        }
        logDebug("[MethodSignature] syntax node SUCCESS", methodSignature);
    }

    // </editor-fold>

    // <editor-fold desc="[MethodDecl]">

    @Override
    public void visit(MethodDecl methodDecl)
    {
        if (currentMethodSymbol == null)
        {
            logError("[MethodDecl] syntax node ERROR: Name '" + methodDecl.getMethodSignature().getMethodName().getMethodNameString() + "' already defined in the current scope", methodDecl);
            return;
        }

        boolean isReturnTypeNotVoid = !areTypesEqual(currentMethodSymbol.getType(), voidType);
        if (isReturnTypeNotVoid && !isReturnStatementDetected)
        {
            logError("[MethodDecl] syntax node ERROR: Method '" + methodDecl.getMethodSignature().getMethodName().getMethodNameString() + "' must have a return statement", methodDecl);
        }
        else
        {
            logDebug("[MethodDecl] syntax node SUCCESS", methodDecl);
        }

        Tab.chainLocalSymbols(currentMethodSymbol);
        Tab.closeScope();
        currentMethodSymbol = null;
        isReturnStatementDetected = false;
    }

    // </editor-fold>

    // <editor-fold desc="[FormPars]">

    @Override
    public void visit(FormalParameterSingle formalParameterSingle)
    {
        if (currentMethodSymbol == null)
        {
            logError("[FormalParameterSingle] syntax node ERROR: Invalid definition of the current method", formalParameterSingle);
            return;
        }
        String formalParameterNameString = formalParameterSingle.getFormalParameterNameString();
        if (Tab.currentScope.findSymbol(formalParameterNameString) != null)
        {
            logError("[FormalParameterSingle] syntax node ERROR: Name '" + formalParameterNameString + "' already defined in the current scope", formalParameterSingle);
            return;
        }
        if (formalParameterNameString.equals("this"))
        {
            logError("[FormalParameterSingle] syntax node ERROR: Name 'this' cannot be used as a formal parameter name", formalParameterSingle);
            return;
        }

        Obj formalParameterSingleSymbol = Tab.insert(
                Obj.Var,
                formalParameterNameString,
                currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType
        );
        currentMethodSymbol.setLevel(currentMethodSymbol.getLevel() + 1);

        if (currentClassSymbol != null || currentInterfaceSymbol != null)
        {
            formalParameterSingleSymbol.setLevel(VAR_LEVEL_FORMAL_PARAMETER_CLASS_OR_INTERFACE_METHOD);
        }
        else
        {
            formalParameterSingleSymbol.setLevel(VAR_LEVEL_FORMAL_PARAMETER_GLOBAL_METHOD);
        }
        formalParameterSingleSymbol.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
        logSymbol("formal parameter symbol inserted", formalParameterSingleSymbol, formalParameterSingle);

        if (currentTypeSymbol == null)
        {
            logError("[FormalParameterSingle] syntax node ERROR: Invalid type when defining variable '" + formalParameterNameString + "'", formalParameterSingle);
        }
        else
        {
            logDebug("[FormalParameterSingle] syntax node SUCCESS", formalParameterSingle);
        }
        currentTypeSymbol = null;
    }

    @Override
    public void visit(FormalParameterArray formalParameterArray)
    {
        if (currentMethodSymbol == null)
        {
            logError("[FormalParameterArray] syntax node ERROR: Invalid definition of the current method", formalParameterArray);
            return;
        }
        String formalParameterNameString = formalParameterArray.getFormalParameterNameString();
        if (Tab.currentScope.findSymbol(formalParameterNameString) != null)
        {
            logError("[FormalParameterArray] syntax node ERROR: Name '" + formalParameterNameString + "' already defined in the current scope", formalParameterArray);
            return;
        }
        if (formalParameterNameString.equals("this"))
        {
            logError("[FormalParameterArray] syntax node ERROR: Name 'this' cannot be used as a formal parameter name", formalParameterArray);
            return;
        }

        Obj formalParameterArraySymbol = Tab.insert(
                Obj.Var,
                formalParameterNameString,
                new Struct(Struct.Array, currentTypeSymbol != null ? currentTypeSymbol.getType() : voidType)
        );
        currentMethodSymbol.setLevel(currentMethodSymbol.getLevel() + 1);

        if (currentClassSymbol != null || currentInterfaceSymbol != null)
        {
            formalParameterArraySymbol.setLevel(VAR_LEVEL_FORMAL_PARAMETER_CLASS_OR_INTERFACE_METHOD);
        }
        else
        {
            formalParameterArraySymbol.setLevel(VAR_LEVEL_FORMAL_PARAMETER_GLOBAL_METHOD);
        }
        formalParameterArraySymbol.setFpPos(VAR_FP_POS_FORMAL_PARAMETER);
        logSymbol("formal parameter symbol inserted", formalParameterArraySymbol, formalParameterArray);

        if (currentTypeSymbol == null)
        {
            logError("[FormalParameterArray] syntax node ERROR: Invalid type when defining variable '" + formalParameterNameString + "'", formalParameterArray);
        }
        else
        {
            logDebug("[FormalParameterArray] syntax node SUCCESS", formalParameterArray);
        }
        currentTypeSymbol = null;
    }

    // </editor-fold>

    // <editor-fold desc="[Type]">

    @Override
    public void visit(Type type)
    {
        Obj typeSymbol;
        String typeNameString = type.getTypeNameString();
        typeSymbol = Tab.find(typeNameString);
        if (typeSymbol == voidSymbol)
        {
            currentTypeSymbol = null;
            logError("[Type] syntax node ERROR: Type '" + typeNameString + "' used before declaration", type);
            return;
        }
        if (typeSymbol.getKind() != Obj.Type)
        {
            currentTypeSymbol = null;
            logError("[Type] syntax node ERROR: '" + typeNameString + "' is not a type", type);
            return;
        }
        currentTypeSymbol = typeSymbol;
        logDebug("[Type] syntax node SUCCESS", type);
    }

    // </editor-fold>

    // <editor-fold desc="[Statement]">

    @Override
    public void visit(StatementIf statementIf)
    {
        if (!areTypesEqual(statementIf.getCondition().struct, boolType))
        {
            logError("[StatementIf] syntax node ERROR: Condition of an 'if' statement must be of type 'bool'", statementIf);
            return;
        }
        logDebug("[StatementIf] syntax node SUCCESS", statementIf);
    }

    @Override
    public void visit(StatementIfElse statementIfElse)
    {
        if (!areTypesEqual(statementIfElse.getCondition().struct, boolType))
        {
            logError("[StatementIfElse] syntax node ERROR: Condition of an 'if' statement must be of type 'bool'", statementIfElse);
            return;
        }
        logDebug("[StatementIfElse] syntax node SUCCESS", statementIfElse);
    }

    @Override
    public void visit(StatementBreak statementBreak)
    {
        if (whileLoopNestingLevel == 0)
        {
            logError("[StatementBreak] syntax node ERROR: Break statement can only be used inside of a loop", statementBreak);
            return;
        }
        logDebug("[StatementBreak] syntax node SUCCESS", statementBreak);
    }

    @Override
    public void visit(StatementContinue statementContinue)
    {
        if (whileLoopNestingLevel == 0)
        {
            logError("[StatementContinue] syntax node ERROR: Continue statement can only be used inside of a loop", statementContinue);
            return;
        }
        logDebug("[StatementContinue] syntax node SUCCESS", statementContinue);
    }

    @Override
    public void visit(StatementReturnWithoutExpr statementReturnWithoutExpr)
    {
        if (currentMethodSymbol == null)
        {
            logError("[StatementReturnWithoutExpr] syntax node ERROR: Return statement can only be used inside of a method", statementReturnWithoutExpr);
            return;
        }
        if (!areTypesEqual(currentMethodSymbol.getType(), voidType))
        {
            logError("[StatementReturnWithoutExpr] syntax node ERROR: Return statement without expression can only be used in methods with 'void' return type", statementReturnWithoutExpr);
            return;
        }
        isReturnStatementDetected = true;
        logDebug("[StatementReturnWithoutExpr] syntax node SUCCESS", statementReturnWithoutExpr);
    }

    @Override
    public void visit(StatementReturnWithExpr statementReturnWithExpr)
    {
        if (currentMethodSymbol == null)
        {
            logError("[StatementReturnWithExpr] syntax node ERROR: Return statement can only be used inside of a method", statementReturnWithExpr);
            return;
        }
        if (!areTypesEqual(currentMethodSymbol.getType(), statementReturnWithExpr.getExpr().obj.getType()))
        {
            logError("[StatementReturnWithExpr] syntax node ERROR: Return statement expression type does not match method return type", statementReturnWithExpr);
            return;
        }
        isReturnStatementDetected = true;
        logDebug("[StatementReturnWithExpr] syntax node SUCCESS", statementReturnWithExpr);
    }

    @Override
    public void visit(StatementRead statementRead)
    {
        Obj designatorSymbol = statementRead.getDesignator().obj;
        if (isDesignatorNotModifiable(designatorSymbol))
        {
            logError("[StatementRead] syntax node ERROR: Designator in a read statement must be modifiable (variable, class attribute or array element)", statementRead);
            return;
        }
        if (isDesignatorSymbolTypeInvalidForReading(designatorSymbol))
        {
            logError("[StatementRead] syntax node ERROR: Read statement can only read values of 'int', 'char' or 'bool' types", statementRead);
            return;
        }
        logDebug("[StatementRead] syntax node SUCCESS", statementRead);
    }

    @Override
    public void visit(StatementPrintWithoutParameter statementPrintWithoutParameter)
    {
        Obj exprSymbol = statementPrintWithoutParameter.getExpr().obj;
        if (isExprSymbolInvalidForPrinting(exprSymbol))
        {
            logError("[StatementPrintWithoutParameter] syntax node ERROR: Print statement can only print values of 'int', 'char', 'bool' or 'set' types", statementPrintWithoutParameter);
            return;
        }
        logDebug("[StatementPrintWithoutParameter] syntax node SUCCESS", statementPrintWithoutParameter);
    }

    @Override
    public void visit(StatementPrintWithParameter statementPrintWithParameter)
    {
        Obj exprSymbol = statementPrintWithParameter.getExpr().obj;
        if (isExprSymbolInvalidForPrinting(exprSymbol))
        {
            logError("[StatementPrintWithParameter] syntax node ERROR: Print statement can only print values of 'int', 'char', 'bool' or 'set' types", statementPrintWithParameter);
            return;
        }
        logDebug("[StatementPrintWithParameter] syntax node SUCCESS", statementPrintWithParameter);
    }

    @Override
    public void visit(DoKeyword doKeyword)
    {
        whileLoopNestingLevel++;
        logDebug("[DoKeyword] syntax node SUCCESS", doKeyword);
    }

    @Override
    public void visit(StatementDoWhileNoCondition statementDoWhileNoCondition)
    {
        whileLoopNestingLevel--;
        logDebug("[StatementDoWhileNoCondition] syntax node SUCCESS", statementDoWhileNoCondition);
    }

    @Override
    public void visit(StatementDoWhileCondition statementDoWhileCondition)
    {
        whileLoopNestingLevel--;
        if (!areTypesEqual(statementDoWhileCondition.getCondition().struct, boolType))
        {
            logError("[StatementDoWhileWithCondition] syntax node ERROR: Condition of a 'do-while' statement must be of type 'bool'", statementDoWhileCondition);
            return;
        }
        logDebug("[StatementDoWhileWithCondition] syntax node SUCCESS", statementDoWhileCondition);
    }

    @Override
    public void visit(StatementDoWhileConditionAndDesignatorStatement statementDoWhileConditionAndDesignatorStatement)
    {
        whileLoopNestingLevel--;
        if (!areTypesEqual(statementDoWhileConditionAndDesignatorStatement.getCondition().struct, boolType))
        {
            logError("[StatementDoWhileConditionAndDesignatorStatement] syntax node ERROR: Condition of a 'do-while' statement must be of type 'bool'", statementDoWhileConditionAndDesignatorStatement);
            return;
        }
        logDebug("[StatementDoWhileConditionAndDesignatorStatement] syntax node SUCCESS", statementDoWhileConditionAndDesignatorStatement);
    }

    // </editor-fold>

    // <editor-fold desc="[DesignatorStatement]">

    public void visit(DesignatorStatementAssignopExpr designatorStatementAssignopExpr)
    {
        Obj designatorSymbol = designatorStatementAssignopExpr.getDesignator().obj;
        if (designatorSymbol == voidSymbol)
        {
            logError("[DesignatorStatementAssignopExpr] syntax node ERROR: Designator is invalid", designatorStatementAssignopExpr);
            return;
        }
        if (isDesignatorNotModifiable(designatorSymbol))
        {
            logError("[DesignatorStatementAssignopExpr] syntax node ERROR: Designator in an assignment statement must be modifiable (variable, class attribute or array element)", designatorStatementAssignopExpr);
            return;
        }
        Obj exprSymbol = designatorStatementAssignopExpr.getExpr().obj;
        if (isSourceNotAssignableToDestination(designatorSymbol.getType(), exprSymbol.getType()))
        {
            logError("[DesignatorStatementAssignopExpr] syntax node ERROR: Expression is not assignable to the designator", designatorStatementAssignopExpr);
            return;
        }
        logSymbol("assignment to designator", designatorSymbol, designatorStatementAssignopExpr);
        logDebug("[DesignatorStatementAssignopExpr] syntax node SUCCESS", designatorStatementAssignopExpr);
    }

    @Override
    public void visit(DesignatorStatementFunctionCall designatorStatementFunctionCall)
    {
        Obj designatorSymbol = designatorStatementFunctionCall.getDesignator().obj;
        if (designatorSymbol.getKind() != Obj.Meth)
        {
            logError("[DesignatorStatementFunctionCall] syntax node ERROR: Designator in a function call must be a method", designatorStatementFunctionCall);
            return;
        }
        if (areFunctionCallArgumentsInvalid(designatorSymbol))
        {
            logError("[DesignatorStatementFunctionCall] syntax node ERROR: Function call arguments do not match the method signature's formal parameters", designatorStatementFunctionCall);
            return;
        }

        if (designatorSymbol.getFpPos() == METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD)
        {
            logSymbol("global method call detected", designatorSymbol, designatorStatementFunctionCall);
        }
        else
        {
            logSymbol("class method call detected", designatorSymbol, designatorStatementFunctionCall);
        }
        logDebug("[DesignatorStatementFunctionCall] syntax node SUCCESS", designatorStatementFunctionCall);
    }

    @Override
    public void visit(DesignatorStatementIncrement designatorStatementIncrement)
    {
        Obj designatorSymbol = designatorStatementIncrement.getDesignator().obj;
        if (designatorSymbol == voidSymbol)
        {
            logError("[DesignatorStatementIncrement] syntax node ERROR: Designator is invalid", designatorStatementIncrement);
            return;
        }
        if (isDesignatorNotModifiable(designatorSymbol))
        {
            logError("[DesignatorStatementIncrement] syntax node ERROR: Designator in an increment statement must be modifiable (variable, class attribute or array element)", designatorStatementIncrement);
            return;
        }
        if (!areTypesEqual(designatorSymbol.getType(), intType))
        {
            logError("[DesignatorStatementIncrement] syntax node ERROR: Increment statement can only be applied to integer designators", designatorStatementIncrement);
            return;
        }
        logDebug("[DesignatorStatementIncrement] syntax node SUCCESS", designatorStatementIncrement);
    }

    @Override
    public void visit(DesignatorStatementDecrement designatorStatementDecrement)
    {
        Obj designatorSymbol = designatorStatementDecrement.getDesignator().obj;
        if (designatorSymbol == voidSymbol)
        {
            logError("[DesignatorStatementDecrement] syntax node ERROR: Designator is invalid", designatorStatementDecrement);
            return;
        }
        if (isDesignatorNotModifiable(designatorSymbol))
        {
            logError("[DesignatorStatementDecrement] syntax node ERROR: Designator in a decrement statement must be modifiable (variable, class attribute or array element)", designatorStatementDecrement);
            return;
        }
        if (!areTypesEqual(designatorSymbol.getType(), intType))
        {
            logError("[DesignatorStatementDecrement] syntax node ERROR: Decrement statement can only be applied to integer designators", designatorStatementDecrement);
            return;
        }
        logDebug("[DesignatorStatementDecrement] syntax node SUCCESS", designatorStatementDecrement);
    }

    @Override
    public void visit(DesignatorStatementSetop designatorStatementSetop)
    {
        Obj designatorSymbol1 = designatorStatementSetop.getDesignator().obj;
        Obj designatorSymbol2 = designatorStatementSetop.getDesignator1().obj;
        Obj designatorSymbol3 = designatorStatementSetop.getDesignator2().obj;
        if (designatorSymbol1 == voidSymbol || designatorSymbol2 == voidSymbol || designatorSymbol3 == voidSymbol)
        {
            logError("[DesignatorStatementSetop] syntax node ERROR: One or more designators are invalid", designatorStatementSetop);
            return;
        }
        if (!areTypesEqual(designatorSymbol1.getType(), setType) || !areTypesEqual(designatorSymbol2.getType(), setType) || !areTypesEqual(designatorSymbol3.getType(), setType))
        {
            logError("[DesignatorStatementSetop] syntax node ERROR: All designators in a union operation must be of type 'set'", designatorStatementSetop);
            return;
        }
        logDebug("[DesignatorStatementSetop] syntax node SUCCESS", designatorStatementSetop);
    }

    // </editor-fold>

    // <editor-fold desc="[ActPars]">

    @Override
    public void visit(ActParsExpr actParsExpr)
    {
        List<Struct> currentFunctionCallArgumentsTypes = new ArrayList<>();
        currentFunctionCallArgumentsTypes.add(actParsExpr.getExpr().obj.getType());
        allFunctionCallArgumentsTypes.push(currentFunctionCallArgumentsTypes);
        logDebug("[ActParsExpr] syntax node SUCCESS", actParsExpr);
    }

    @Override
    public void visit(ActParsExprCommaExpr actParsExprCommaExpr)
    {
        List<Struct> currentFunctionCallArgumentsTypes = allFunctionCallArgumentsTypes.peek();
        currentFunctionCallArgumentsTypes.add(actParsExprCommaExpr.getExpr().obj.getType());
        logDebug("[ActParsExprCommaExpr] syntax node SUCCESS", actParsExprCommaExpr);
    }

    @Override
    public void visit(OptionalActParsEpsilon optionalActParsEpsilon)
    {
        allFunctionCallArgumentsTypes.push(new ArrayList<>());
        logDebug("[OptionalActParsEpsilon] syntax node SUCCESS", optionalActParsEpsilon);
    }

    // </editor-fold>

    // <editor-fold desc="[Condition]">

    @Override
    public void visit(ConditionCondTerm conditionCondTerm)
    {
        conditionCondTerm.struct = conditionCondTerm.getCondTerm().struct;
        if (areTypesEqual(conditionCondTerm.struct, voidType))
        {
            logError("[ConditionCondTerm] syntax node ERROR: CondTerm type is not defined", conditionCondTerm);
            return;
        }
        logDebug("[ConditionCondTerm] syntax node SUCCESS", conditionCondTerm);
    }

    @Override
    public void visit(ConditionCondTermOrCondTerm conditionCondTermOrCondTerm)
    {
        Struct leftCondTermType = conditionCondTermOrCondTerm.getCondition().struct;
        Struct rightCondTermType = conditionCondTermOrCondTerm.getCondTerm().struct;
        if (areTypesEqual(leftCondTermType, voidType) || areTypesEqual(rightCondTermType, voidType))
        {
            conditionCondTermOrCondTerm.struct = voidType;
            logError("[ConditionCondTermOrCondTerm] syntax node ERROR: The types of both CondTerms in a logical or operation (||) must be defined", conditionCondTermOrCondTerm);
            return;
        }
        if (!areTypesEqual(leftCondTermType, boolType) || !areTypesEqual(rightCondTermType, boolType))
        {
            conditionCondTermOrCondTerm.struct = voidType;
            logError("[ConditionCondTermOrCondTerm] syntax node ERROR: Both CondTerms in a logical or operation (||) must be of type 'bool'", conditionCondTermOrCondTerm);
            return;
        }
        conditionCondTermOrCondTerm.struct = leftCondTermType;
        logDebug("[ConditionCondTermOrCondTerm] syntax node SUCCESS", conditionCondTermOrCondTerm);
    }

    // </editor-fold>

    // <editor-fold desc="[CondTerm]">

    @Override
    public void visit(CondTermCondFact condTermCondFact)
    {
        condTermCondFact.struct = condTermCondFact.getCondFact().struct;
        if (areTypesEqual(condTermCondFact.struct, voidType))
        {
            logError("[CondTermCondFact] syntax node ERROR: CondFact type is not defined", condTermCondFact);
            return;
        }
        logDebug("[CondTermCondFact] syntax node SUCCESS", condTermCondFact);
    }

    @Override
    public void visit(CondTermCondFactAndCondFact condTermCondFactAndCondFact)
    {
        Struct leftCondFactType = condTermCondFactAndCondFact.getCondTerm().struct;
        Struct rightCondFactType = condTermCondFactAndCondFact.getCondFact().struct;
        if (areTypesEqual(leftCondFactType, voidType) || areTypesEqual(rightCondFactType, voidType))
        {
            condTermCondFactAndCondFact.struct = voidType;
            logError("[CondTermCondFactAndCondFact] syntax node ERROR: The types of both CondFacts in a logical and operation (&&) must be defined", condTermCondFactAndCondFact);
            return;
        }
        if (!areTypesEqual(leftCondFactType, boolType) || !areTypesEqual(rightCondFactType, boolType))
        {
            condTermCondFactAndCondFact.struct = voidType;
            logError("[CondTermCondFactAndCondFact] syntax node ERROR: Both CondFacts in a logical and operation (&&) must be of type 'bool'", condTermCondFactAndCondFact);
            return;
        }
        condTermCondFactAndCondFact.struct = leftCondFactType;
        logDebug("[CondTermCondFactAndCondFact] syntax node SUCCESS", condTermCondFactAndCondFact);
    }

    // </editor-fold>

    // <editor-fold desc="[CondFact]">

    @Override
    public void visit(CondFactExpr condFactExpr)
    {
        condFactExpr.struct = condFactExpr.getExpr().obj.getType();
        if (areTypesEqual(condFactExpr.struct, voidType))
        {
            logError("[CondFactExpr] syntax node ERROR: Expr type is not defined", condFactExpr);
            return;
        }
        logDebug("[CondFactExpr] syntax node SUCCESS", condFactExpr);
    }

    @Override
    public void visit(CondFactExprRelopExpr condFactExprRelopExpr)
    {
        Struct leftExprType = condFactExprRelopExpr.getExpr().obj.getType();
        Struct rightExprType = condFactExprRelopExpr.getExpr1().obj.getType();
        if (areTypesEqual(leftExprType, voidType) || areTypesEqual(rightExprType, voidType))
        {
            condFactExprRelopExpr.struct = voidType;
            logError("[CondFactExprRelopExpr] syntax node ERROR: The types of both expressions in a relational operation must be defined", condFactExprRelopExpr);
            return;
        }
        if (areTypesNotCompatible(leftExprType, rightExprType))
        {
            condFactExprRelopExpr.struct = voidType;
            logError("[CondFactExprRelopExpr] syntax node ERROR: Expressions of a relational operation must be compatible", condFactExprRelopExpr);
            return;
        }
        boolean isSomeExpressionOfReferencetype = isReferenceType(leftExprType) || isReferenceType(rightExprType);
        boolean isRelationalOperatorOfEqualityType = condFactExprRelopExpr.getRelop() instanceof RelopEquals || condFactExprRelopExpr.getRelop() instanceof RelopNotEquals;
        if (isSomeExpressionOfReferencetype && !isRelationalOperatorOfEqualityType)
        {
            condFactExprRelopExpr.struct = voidType;
            logError("[CondFactExprRelopExpr] syntax node ERROR: Relational operations on reference types (Class, Array, Set) must be equality operations", condFactExprRelopExpr);
            return;
        }
        condFactExprRelopExpr.struct = boolType;
        logDebug("[CondFactExprRelopExpr] syntax node SUCCESS", condFactExprRelopExpr);
    }

    // </editor-fold>

    // <editor-fold desc="[Expr]">

    @Override
    public void visit(ExprTerm exprTerm)
    {
        exprTerm.obj = exprTerm.getTerm().obj;
        if (exprTerm.obj == voidSymbol)
        {
            logError("[ExprTerm] syntax node ERROR: Term type is not defined", exprTerm);
            return;
        }
        logDebug("[ExprTerm] syntax node SUCCESS", exprTerm);
    }

    @Override
    public void visit(ExprAddopMinusTerm exprAddopMinusTerm)
    {
        Obj termSymbol = exprAddopMinusTerm.getTerm().obj;
        if (isSymbolNotInteger(termSymbol))
        {
            exprAddopMinusTerm.obj = voidSymbol;
            logError("[ExprAddopMinusTerm] syntax node ERROR: Only terms of 'int' type can be negated", exprAddopMinusTerm);
            return;
        }
        exprAddopMinusTerm.obj = termSymbol;
        logDebug("[ExprAddopMinusTerm] syntax node SUCCESS", exprAddopMinusTerm);
    }

    @Override
    public void visit(ExprExprAddopTerm exprExprAddopTerm)
    {
        Obj leftTermSymbol = exprExprAddopTerm.getExpr().obj;
        Obj rightTermSymbol = exprExprAddopTerm.getTerm().obj;
        if (isSymbolNotInteger(leftTermSymbol) || isSymbolNotInteger(rightTermSymbol))
        {
            exprExprAddopTerm.obj = voidSymbol;
            logError("[ExprExprAddopTerm] syntax node ERROR: Both terms of an addition operation must be values of type 'int'", exprExprAddopTerm);
            return;
        }
        exprExprAddopTerm.obj = leftTermSymbol;
        logDebug("[ExprExprAddopTerm] syntax node SUCCESS", exprExprAddopTerm);
    }

    @Override
    public void visit(ExprDesignatorMapDesignator exprDesignatorMapDesignator)
    {
        Obj leftDesignatorSymbol = exprDesignatorMapDesignator.getDesignator().obj;
        Obj rightDesignatorSymbol = exprDesignatorMapDesignator.getDesignator1().obj;

        if (leftDesignatorSymbol == voidSymbol || rightDesignatorSymbol == voidSymbol)
        {
            exprDesignatorMapDesignator.obj = voidSymbol;
            logError("[ExprDesignatorMapDesignator] syntax node ERROR: Some of the designators' identificators are invalid", exprDesignatorMapDesignator);
            return;
        }

        if (isDesignatorMappingFunctionInvalid(leftDesignatorSymbol))
        {
            exprDesignatorMapDesignator.obj = voidSymbol;
            logError("[ExprDesignatorMapDesignator] syntax node ERROR: Left designator in a mapping operation must be a function with this signature: int func(int param)", exprDesignatorMapDesignator);
            return;
        }

        if (rightDesignatorSymbol.getType().getKind() != Struct.Array || !areTypesEqual(rightDesignatorSymbol.getType().getElemType(), intType))
        {
            exprDesignatorMapDesignator.obj = voidSymbol;
            logError("[ExprDesignatorMapDesignator] syntax node ERROR: Right designator in a mapping operation must be an array of integers", exprDesignatorMapDesignator);
            return;
        }

        exprDesignatorMapDesignator.obj = new Obj(Obj.Var, "int", intType);
        logSymbol("designator mapping calculation", exprDesignatorMapDesignator.obj, exprDesignatorMapDesignator);
        logDebug("[ExprDesignatorMapDesignator] syntax node SUCCESS", exprDesignatorMapDesignator);
    }

    // </editor-fold>

    // <editor-fold desc="[Term]">

    @Override
    public void visit(TermFactor termFactor)
    {
        termFactor.obj = termFactor.getFactor().obj;
        if (termFactor.obj == voidSymbol)
        {
            logError("[TermFactor] syntax node ERROR: Factor type is not defined", termFactor);
            return;
        }
        logDebug("[TermFactor] syntax node SUCCESS", termFactor);
    }

    @Override
    public void visit(TermTermMulopFactor termTermMulopFactor)
    {
        Obj leftFactorSymbol = termTermMulopFactor.getTerm().obj;
        Obj rightFactorSymbol = termTermMulopFactor.getFactor().obj;
        if (isSymbolNotInteger(leftFactorSymbol) || isSymbolNotInteger(rightFactorSymbol))
        {
            termTermMulopFactor.obj = voidSymbol;
            logError("[TermTermMulopFactor] syntax node ERROR: Both factors of a multiplication operation must be values of type 'int'", termTermMulopFactor);
            return;
        }

        termTermMulopFactor.obj = leftFactorSymbol;
        logDebug("[TermTermMulopFactor] syntax node SUCCESS", termTermMulopFactor);
    }

    // </editor-fold>

    // <editor-fold desc="[Factor]">

    @Override
    public void visit(FactorDesignator factorDesignator)
    {
        Obj designatorSymbol = factorDesignator.getDesignator().obj;
        if (designatorSymbol == voidSymbol || designatorSymbol.getKind() == Obj.Type || designatorSymbol.getKind() == Obj.Meth)
        {
            factorDesignator.obj = voidSymbol;
            logError("[FactorDesignator] syntax node ERROR: Some of the designator's identificators are invalid", factorDesignator);
            return;
        }
        factorDesignator.obj = factorDesignator.getDesignator().obj;
        logDebug("[FactorDesignator] syntax node SUCCESS", factorDesignator);
    }

    @Override
    public void visit(FactorFunctionCall factorFunctionCall)
    {
        Obj designatorSymbol = factorFunctionCall.getDesignator().obj;
        if (designatorSymbol.getKind() != Obj.Meth)
        {
            factorFunctionCall.obj = voidSymbol;
            logError("[FactorFunctionCall] syntax node ERROR: Designator in a function call must be a method", factorFunctionCall);
            return;
        }
        if (areFunctionCallArgumentsInvalid(designatorSymbol))
        {
            factorFunctionCall.obj = voidSymbol;
            logError("[FactorFunctionCall] syntax node ERROR: Function call arguments do not match the method signature's formal parameters", factorFunctionCall);
            return;
        }

        factorFunctionCall.obj = new Obj(Obj.Var, designatorSymbol.getName() + "(?)", designatorSymbol.getType());
        if (designatorSymbol.getFpPos() == METH_FP_POS_IMPLEMENTED_GLOBAL_METHOD)
        {
            logSymbol("global method call detected", factorFunctionCall.obj, factorFunctionCall);
        }
        else
        {
            logSymbol("class method call detected", factorFunctionCall.obj, factorFunctionCall);
        }
        logDebug("[FactorFunctionCall] syntax node SUCCESS", factorFunctionCall);
    }

    @Override
    public void visit(FactorConstant factorConstant)
    {
        factorConstant.obj = factorConstant.getConstant().obj;
        logDebug("[FactorConstant] syntax node SUCCESS", factorConstant);
    }

    @Override
    public void visit(FactorCollectionInstantiation factorCollectionInstantiation)
    {
        if (currentTypeSymbol == null)
        {
            factorCollectionInstantiation.obj = voidSymbol;
            logError("[FactorCollectionInstantiation] syntax node ERROR: Invalid type when instantiating a collection", factorCollectionInstantiation);
            return;
        }
        
        if (isSymbolNotInteger(factorCollectionInstantiation.getExpr().obj))
        {
            factorCollectionInstantiation.obj = voidSymbol;
            logError("[FactorCollectionInstantiation] syntax node ERROR: Collection size expression must be an integer", factorCollectionInstantiation);
            return;
        }

        String collectionName;
        Struct collectionType;
        if (currentTypeSymbol.getType().getKind() == Set)
        {
            collectionName = "int{}";
            collectionType = currentTypeSymbol.getType();
        }
        else
        {
            collectionName = currentTypeSymbol.getName() + "[]";
            collectionType = new Struct(Struct.Array, currentTypeSymbol.getType());
        }
        factorCollectionInstantiation.obj = new Obj(Obj.Var, collectionName, collectionType);
        logSymbol("collection instantiation detected", factorCollectionInstantiation.obj, factorCollectionInstantiation);
        logDebug("[FactorCollectionInstantiation] syntax node SUCCESS", factorCollectionInstantiation);
    }

    @Override
    public void visit(FactorObjectInstantiation factorObjectInstantiation)
    {
        if (currentTypeSymbol == null)
        {
            factorObjectInstantiation.obj = voidSymbol;
            logError("[FactorObjectInstantiation] syntax node ERROR: Invalid type when instantiating an object", factorObjectInstantiation);
            return;
        }

        if (currentTypeSymbol.getType().getKind() != Struct.Class)
        {
            factorObjectInstantiation.obj = voidSymbol;
            logError("[FactorObjectInstantiation] syntax node ERROR: Type '" + currentTypeSymbol.getName() + "' is not a class", factorObjectInstantiation);
            return;
        }

        factorObjectInstantiation.obj = new Obj(Obj.Var, currentTypeSymbol.getName(), currentTypeSymbol.getType());
        logSymbol("object instantiation detected", factorObjectInstantiation.obj, factorObjectInstantiation);
        logDebug("[FactorObjectInstantiation] syntax node SUCCESS", factorObjectInstantiation);
    }

    @Override
    public void visit(FactorExpr factorExpr)
    {
        factorExpr.obj = factorExpr.getExpr().obj;
        if (factorExpr.obj == voidSymbol)
        {
            logError("[FactorExpr] syntax node ERROR: Expr type is not defined", factorExpr);
            return;
        }
        logDebug("[FactorExpr] syntax node SUCCESS", factorExpr);
    }

    // </editor-fold>

    // <editor-fold desc="[Designator]">

    @Override
    public void visit(DesignatorIdentificator designatorIdentificator)
    {
        String designatorIdentificatorString = designatorIdentificator.getDesignatorIdentificatorString();
        Obj designatorIdentificatorSymbol = Tab.find(designatorIdentificatorString);
        if (designatorIdentificatorSymbol == voidSymbol)
        {
            designatorIdentificator.obj = voidSymbol;
            logError("[DesignatorIdentificator] syntax node ERROR: Name '" + designatorIdentificatorString + "' not found in the symbols table", designatorIdentificator);
            return;
        }
        if (designatorIdentificatorSymbol.getKind() == Obj.Prog)
        {
            designatorIdentificator.obj = voidSymbol;
            logError("[DesignatorIdentificator] syntax node ERROR: Name '" + designatorIdentificatorString + "' is not a valid designator identificator", designatorIdentificator);
            return;
        }
        designatorIdentificator.obj = designatorIdentificatorSymbol;
        logSymbolUsageDetection(designatorIdentificator.obj, designatorIdentificator);
        logDebug("[DesignatorIdentificator] syntax node SUCCESS", designatorIdentificator);
    }

    @Override
    public void visit(DesignatorArrayAccess designatorArrayAccess)
    {
        Obj designatorIdentificatorSymbol = designatorArrayAccess.getDesignator().obj;
        if (designatorIdentificatorSymbol == voidSymbol)
        {
            designatorArrayAccess.obj = voidSymbol;
            logError("[DesignatorArrayAccess] syntax node ERROR: Designator identificator is invalid", designatorArrayAccess);
            return;
        }

        if (designatorIdentificatorSymbol.getType().getKind() != Struct.Array)
        {
            designatorArrayAccess.obj = voidSymbol;
            logError("[DesignatorArrayAccess] syntax node ERROR: Array designator identificator must be an array variable", designatorArrayAccess);
            return;
        }
        
        if (isSymbolNotInteger(designatorArrayAccess.getExpr().obj))
        {
            designatorArrayAccess.obj = voidSymbol;
            logError("[DesignatorArrayAccess] syntax node ERROR: Array index expression must be an integer", designatorArrayAccess);
            return;
        }

        String arrayElementSymbolName = designatorIdentificatorSymbol.getName() + "[?]";
        designatorArrayAccess.obj = new Obj(Obj.Elem, arrayElementSymbolName, designatorIdentificatorSymbol.getType().getElemType());
        logSymbol("array element access detected", designatorArrayAccess.obj, designatorArrayAccess);
        logDebug("[DesignatorArrayAccess] syntax node SUCCESS", designatorArrayAccess);
    }

    @Override
    public void visit(DesignatorUserTypeAccess designatorUserTypeAccess)
    {
        Obj designatorIdentificatorSymbol = designatorUserTypeAccess.getDesignator().obj;
        if (designatorIdentificatorSymbol == voidSymbol)
        {
            designatorUserTypeAccess.obj = voidSymbol;
            logError("[DesignatorUserTypeAccess] syntax node ERROR: Designator identificator is invalid", designatorUserTypeAccess);
            return;
        }
        if (isDesignatorNotModifiable(designatorIdentificatorSymbol))
        {
            designatorUserTypeAccess.obj = voidSymbol;
            logError("[DesignatorUserTypeAccess] syntax node ERROR: Designator identificator must be a variable, class attribute or array element to be accessed", designatorUserTypeAccess);
            return;
        }
        if (designatorIdentificatorSymbol.getType().getKind() != Struct.Class && designatorIdentificatorSymbol.getType().getKind() != Struct.Interface)
        {
            designatorUserTypeAccess.obj = voidSymbol;
            logError("[DesignatorUserTypeAccess] syntax node ERROR: Designator identificator must be an object", designatorUserTypeAccess);
            return;
        }
        if (designatorIdentificatorSymbol.getType().getKind() == Struct.Class && isDesignatorClassAccessInvalid(designatorUserTypeAccess, designatorIdentificatorSymbol))
        {
            return;
        }
        if (designatorIdentificatorSymbol.getType().getKind() == Struct.Interface && isDesignatorInterfaceAccessInvalid(designatorUserTypeAccess, designatorIdentificatorSymbol))
        {
            return;
        }
        logDebug("[DesignatorUserTypeAccess] syntax node SUCCESS", designatorUserTypeAccess);
    }

    // </editor-fold>
}