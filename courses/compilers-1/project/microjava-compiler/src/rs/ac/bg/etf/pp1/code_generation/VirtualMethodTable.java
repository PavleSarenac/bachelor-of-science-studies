package rs.ac.bg.etf.pp1.code_generation;

import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.symboltable.concepts.Obj;
import rs.etf.pp1.symboltable.concepts.Struct;

import java.util.SortedMap;
import java.util.TreeMap;

import static rs.ac.bg.etf.pp1.semantic_analysis.SemanticAnalyzer.METH_FP_POS_IMPLEMENTED_INTERFACE_METHOD;

public class VirtualMethodTable
{
    // each method in the virtual method table has 2 additional metadata entries:
    // (1) '-1' end of method name indicator
    // (2) method address
    private static final int SINGLE_METHOD_ADDITIONAL_METADATA_SIZE = 2;

    // the virtual method table has 1 additional metadata entry:
    // (1) '-2' end of virtual method table indicator
    private static final int VIRTUAL_METHOD_TABLE_ADDITIONAL_METADATA_SIZE = 1;

    private static final int END_OF_METHOD_NAME_INDICATOR = -1;
    private static final int END_OF_VIRTUAL_METHOD_TABLE_INDICATOR = -2;

    private final SortedMap<String, Obj> allMethods = new TreeMap<>();
    private int totalMethodNamesSize = 0;
    private int virtualMethodTableAddress;

    public VirtualMethodTable(Struct classType)
    {
        addAllClassMethods(classType);
        addAllImplementedMethodsFromImplementedInterfaces(classType);
        allocateStaticMemoryForVirtualMethodTable();
    }

    public int getVirtualMethodTableAddress()
    {
        return virtualMethodTableAddress;
    }

    public SortedMap<String, Obj> getAllMethods()
    {
        return allMethods;
    }

    private void addAllClassMethods(Struct classType)
    {
        // add all class methods to the virtual method table, including inherited methods from superclasses
        Struct currentClassType = classType;
        while (currentClassType != null)
        {
            for (Obj classMember : currentClassType.getMembers())
            {
                // polymorphism - if a method with the same name already exists, it means it was overridden in a subclass
                boolean isClassMemberMethod = classMember.getKind() == Obj.Meth;
                boolean isMethodNotOverridden = !allMethods.containsKey(classMember.getName());
                if (isClassMemberMethod && isMethodNotOverridden)
                {
                    allMethods.put(classMember.getName(), classMember);
                    totalMethodNamesSize += classMember.getName().length();
                }
            }
            currentClassType = currentClassType.getElemType();
        }
    }

    private void addAllImplementedMethodsFromImplementedInterfaces(Struct classType)
    {
        // add all implemented methods from interfaces that the class implements to the virtual method table
        for (Struct implementedInterface : classType.getImplementedInterfaces())
        {
            for (Obj interfaceMethod : implementedInterface.getMembers())
            {
                // polymorphism - if a method with the same name already exists, it means it was overridden in the class
                boolean isInterfaceMethodImplemented = interfaceMethod.getFpPos() == METH_FP_POS_IMPLEMENTED_INTERFACE_METHOD;
                boolean isMethodNotOverridden = !allMethods.containsKey(interfaceMethod.getName());
                if (isInterfaceMethodImplemented && isMethodNotOverridden)
                {
                    allMethods.put(interfaceMethod.getName(), interfaceMethod);
                    totalMethodNamesSize += interfaceMethod.getName().length();
                }
            }
        }
    }

    private void allocateStaticMemoryForVirtualMethodTable()
    {
        int totalAdditionalMetadataSize = allMethods.size() * SINGLE_METHOD_ADDITIONAL_METADATA_SIZE + VIRTUAL_METHOD_TABLE_ADDITIONAL_METADATA_SIZE;
        int totalVirtualMethodTableSize = totalMethodNamesSize + totalAdditionalMetadataSize;
        virtualMethodTableAddress = Code.dataSize;
        Code.dataSize += totalVirtualMethodTableSize;
    }

    public void generateVirtualMethodTableIntoStaticMemory()
    {
        int currentStaticMemoryAddress = virtualMethodTableAddress;

        for (Obj method : allMethods.values())
        {
            // generate method name into static memory
            for (char methodNameCharacter : method.getName().toCharArray())
            {
                generateValueIntoStaticMemory(currentStaticMemoryAddress, methodNameCharacter);
                currentStaticMemoryAddress++;
            }

            // generate end of method name indicator into static memory
            generateValueIntoStaticMemory(currentStaticMemoryAddress, END_OF_METHOD_NAME_INDICATOR);
            currentStaticMemoryAddress++;

            // generate method address into static memory
            generateValueIntoStaticMemory(currentStaticMemoryAddress, method.getAdr());
            currentStaticMemoryAddress++;
        }

        // generate end of virtual method table indicator into static memory
        generateValueIntoStaticMemory(currentStaticMemoryAddress, END_OF_VIRTUAL_METHOD_TABLE_INDICATOR);
    }

    private void generateValueIntoStaticMemory(int staticMemoryAddress, int value)
    {
        Code.loadConst(value);
        Code.put(Code.putstatic);
        Code.put2(staticMemoryAddress);
    }
}
