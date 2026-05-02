package rs.ac.bg.etf.pp1.util;

import rs.ac.bg.etf.pp1.semantic_analysis.SemanticAnalyzer;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.concepts.Struct;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;

import java.util.*;


public class ImprovedSymbolTableVisitor extends SymbolTableVisitor {

    protected StringBuilder output = new StringBuilder("Start of symbol table.\n");
    protected final String indent = "   ";
    protected StringBuilder currentIndent = new StringBuilder();

    private static final String BOLD_UNDERLINE = "";
    private static final String RESET = "";

    private Obj currentObj = null;
    private final Set<Obj> classTypeSymbols = new HashSet<>();
    private final Set<Obj> interfaceTypeSymbols = new HashSet<>();

    protected void nextIndentationLevel() {
        currentIndent.append(indent);
    }

    protected void previousIndentationLevel() {
        if (currentIndent.length() > 0)
            currentIndent.setLength(currentIndent.length() - indent.length());
    }

    @Override
    public void visitObjNode(Obj objToVisit) {
        currentObj = objToVisit;

        output.append("[");
        output.append(BOLD_UNDERLINE).append("KIND").append(RESET);
        output.append(": ");

        switch (objToVisit.getKind()) {
            case Obj.Con:
                output.append("Con; ");
                break;
            case Obj.Var:
                output.append("Var; ");
                break;
            case Obj.Type:
                output.append("Type; ");
                if (objToVisit.getType().getKind() == Struct.Class)
                {
                    classTypeSymbols.add(objToVisit);
                }
                else if (objToVisit.getType().getKind() == Struct.Interface)
                {
                    interfaceTypeSymbols.add(objToVisit);
                }
                break;
            case Obj.Meth:
                output.append("Meth; ");
                break;
            case Obj.Fld:
                output.append("Fld; ");
                break;
            case Obj.Prog:
                output.append("Prog; ");
                break;
        }

        output.append(BOLD_UNDERLINE).append("NAME").append(RESET);
        output.append(": ");
        output.append(objToVisit.getName());
        output.append("; ");

        output.append(BOLD_UNDERLINE).append("ADR").append(RESET);
        output.append(": ");
        output.append(objToVisit.getAdr());
        output.append("; ");
        output.append(BOLD_UNDERLINE).append("LEVEL").append(RESET);
        output.append(": ");
        output.append(objToVisit.getLevel());
        output.append("; ");
        output.append(BOLD_UNDERLINE).append("FPPOS").append(RESET);
        output.append(": ");
        output.append(objToVisit.getFpPos());
        output.append("; ");

        objToVisit.getType().accept(this);

        output.append("]");

        Iterator<Obj> localSymbolsIterator = objToVisit.getLocalSymbols().iterator();

        if (objToVisit.getKind() == Obj.Prog || objToVisit.getKind() == Obj.Meth) {
            if (localSymbolsIterator.hasNext())
            {
                output.append("\n");
            }
            nextIndentationLevel();
        }

        while (localSymbolsIterator.hasNext())
        {
            Obj o = localSymbolsIterator.next();
            output.append(currentIndent.toString());
            o.accept(this);
            if (localSymbolsIterator.hasNext())
            {
                output.append("\n");
            }
        }

        if (objToVisit.getKind() == Obj.Prog || objToVisit.getKind() == Obj.Meth)
            previousIndentationLevel();
    }

    @Override
    public void visitScopeNode(Scope scope) {
        for (Obj o : scope.values()) {
            o.accept(this);
            output.append("\n");
        }
    }

    @Override
    public void visitStructNode(Struct structToVisit) {
        output.append(BOLD_UNDERLINE).append("TYPE").append(RESET);
        output.append(": ");
        switch (structToVisit.getKind()) {
            case Struct.None:
                output.append("void");
                break;
            case Struct.Int:
                output.append("int");
                break;
            case Struct.Char:
                output.append("char");
                break;
            case Struct.Bool:
                output.append("bool");
                break;
            case Struct.Array:
                switch (structToVisit.getElemType().getKind()) {
                    case Struct.None:
                        output.append("void");
                        break;
                    case Struct.Int:
                        output.append("int");
                        break;
                    case Struct.Char:
                        output.append("char");
                        break;
                    case Struct.Class:
                        boolean isClassTypeFound = false;
                        for (Obj classTypeSymbol : classTypeSymbols)
                        {
                            if (classTypeSymbol.getType() == structToVisit.getElemType())
                            {
                                output.append(classTypeSymbol.getName());
                                isClassTypeFound = true;
                                break;
                            }
                        }
                        if (!isClassTypeFound)
                        {
                            output.append("Class");
                        }
                        break;
                    case Struct.Bool:
                        output.append("bool");
                        break;
                    case Struct.Interface:
                        boolean isInterfaceTypeFound = false;
                        for (Obj interfaceTypeSymbol : interfaceTypeSymbols)
                        {
                            if (interfaceTypeSymbol.getType() == structToVisit.getElemType())
                            {
                                output.append(interfaceTypeSymbol.getName());
                                isInterfaceTypeFound = true;
                                break;
                            }
                        }
                        if (!isInterfaceTypeFound)
                        {
                            output.append("Interface");
                        }
                        break;
                    case SemanticAnalyzer.Set:
                        output.append("set");
                        break;
                }
                output.append("[]");
                break;
            case SemanticAnalyzer.Set:
                output.append("int{}");
                break;
            case Struct.Class:
                if (currentObj.getKind() == Obj.Type)
                {
                    output.append("Class {");
                    List<Obj> classMembers = new ArrayList<>(structToVisit.getMembers());
                    if (!classMembers.isEmpty())
                    {
                        output.append("\n");
                        nextIndentationLevel();
                    }
                    for (Obj classMember : classMembers) {
                        output.append(currentIndent.toString());
                        classMember.accept(this);
                        output.append("\n");
                    }
                    if (!classMembers.isEmpty())
                    {
                        previousIndentationLevel();
                    }
                    if (!classMembers.isEmpty())
                    {
                        output.append(currentIndent.toString());
                    }
                    output.append("}");
                }
                else if (currentObj.getKind() == Obj.Con)
                {
                    output.append("Class {}");
                }
                else
                {
                    for (Obj classTypeSymbol : classTypeSymbols)
                    {
                        if (classTypeSymbol.getType() == currentObj.getType())
                        {
                            output.append(classTypeSymbol.getName());
                            break;
                        }
                    }
                }
                break;
            case Struct.Interface:
                if (currentObj.getKind() == Obj.Type)
                {
                    output.append("Interface {");
                    List<Obj> interfaceMethods = new ArrayList<>(structToVisit.getMembers());
                    if (!interfaceMethods.isEmpty())
                    {
                        output.append("\n");
                        nextIndentationLevel();
                    }
                    for (Obj interfaceMethod : interfaceMethods) {
                        output.append(currentIndent.toString());
                        interfaceMethod.accept(this);
                        output.append("\n");
                    }
                    if (!interfaceMethods.isEmpty())
                    {
                        previousIndentationLevel();
                    }
                    if (!interfaceMethods.isEmpty())
                    {
                        output.append(currentIndent.toString());
                    }
                    output.append("}");
                }
                else
                {
                    for (Obj interfaceTypeSymbol : interfaceTypeSymbols)
                    {
                        if (interfaceTypeSymbol.getType() == currentObj.getType())
                        {
                            output.append(interfaceTypeSymbol.getName());
                            break;
                        }
                    }
                }
                break;
        }

    }

    public String getOutput() {
        return output.substring(0, output.length() - 1);
    }
}
