package rs.ac.bg.etf.pp1;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import rs.ac.bg.etf.pp1.code_generation.CodeGenerator;
import rs.ac.bg.etf.pp1.lexical_analysis.generated.Yylex;
import rs.ac.bg.etf.pp1.util.ImprovedSymbolTableVisitor;
import rs.ac.bg.etf.pp1.semantic_analysis.SemanticAnalyzer;
import rs.ac.bg.etf.pp1.syntax_analysis.generated.Parser;
import rs.ac.bg.etf.pp1.syntax_analysis.generated.ast.Program;
import rs.ac.bg.etf.pp1.util.Log4JUtils;
import java_cup.runtime.Symbol;
import rs.etf.pp1.mj.runtime.Code;
import rs.etf.pp1.mj.runtime.Run;
import rs.etf.pp1.mj.runtime.disasm;
import rs.etf.pp1.symboltable.Tab;
import rs.etf.pp1.symboltable.concepts.Scope;
import rs.etf.pp1.symboltable.visitors.SymbolTableVisitor;

public class Compiler
{
    private static final int RUNTIME_ERROR_CODE_PROGRAM_FILE_PATH_NOT_SPECIFIED = 1;
    private static final int RUNTIME_ERROR_CODE_INPUT_FILE_PATH_NOT_SPECIFIED = 2;
    private static final int RUNTIME_ERROR_CODE_OBJECT_CODE_FILE_PATH_NOT_SPECIFIED = 3;
    private static final int RUNTIME_ERROR_CODE_SYNTAX_ANALYSIS_ERROR = 4;
    private static final int RUNTIME_ERROR_CODE_SEMANTIC_ANALYSIS_ERROR = 5;
    private static final int RUNTIME_ERROR_CODE_GENERIC_RUNTIME_EXCEPTION = 6;
    private static final int RUNTIME_ERROR_CODE_BUILD_COMMAND_MISSING = 7;
    private static final int RUNTIME_ERROR_CODE_PROGRAM_COMMAND_MISSING = 8;
    private static final int RUNTIME_ERROR_CODE_INPUT_COMMAND_MISSING = 9;
    private static final int RUNTIME_ERROR_CODE_OUTPUT_COMMAND_MISSING = 10;
    private static final int RUNTIME_ERROR_CODE_LOGGER_INITIALIZATION_FAILED = 11;
    
    private static Logger logger;

    private static String microJavaProgramFilePath;
    private static Path inputFilePath;
    private static String microJavaObjectCodeFilePath;
    
    private static void initializeLogger()
    {
        Path configPath = Paths.get("config/log4j.xml");
        try
        {
            if (!Files.exists(configPath.getParent()))
            {
                Files.createDirectories(configPath.getParent());
            }

            if (!Files.exists(configPath))
            {
                String configContent =
                        "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                        "<!DOCTYPE log4j:configuration SYSTEM \"log4j.dtd\">\n" +
                        "<log4j:configuration xmlns:log4j=\"http://jakarta.apache.org/log4j/\">\n" +
                        "    <appender name=\"console\" class=\"org.apache.log4j.ConsoleAppender\">\n" +
                        "        <param name=\"Target\" value=\"System.out\"/>\n" +
                        "        <layout class=\"org.apache.log4j.PatternLayout\">\n" +
                        "            <param name=\"ConversionPattern\" value=\"%-5p %d{ABSOLUTE} - %m%n\"/>\n" +
                        "        </layout>\n" +
                        "    </appender>\n" +
                        "    <appender name=\"file\" class=\"org.apache.log4j.DailyRollingFileAppender\">\n" +
                        "        <param name=\"file\" value=\"logs/microjava_compiler.log\"/>\n" +
                        "        <layout class=\"org.apache.log4j.PatternLayout\">\n" +
                        "            <param name=\"ConversionPattern\" value=\"%-5p %d{ABSOLUTE} - %m%n\"/>\n" +
                        "        </layout>\n" +
                        "    </appender>\n" +
                        "    <root>\n" +
                        "        <priority value=\"debug\"/>\n" +
                        "        <appender-ref ref=\"file\"/>\n" +
                        "        <appender-ref ref=\"console\"/>\n" +
                        "    </root>\n" +
                        "</log4j:configuration>\n";
                Files.write(configPath, configContent.getBytes(StandardCharsets.UTF_8));
            }

            DOMConfigurator.configure(configPath.toString());
            Log4JUtils.instance().prepareLogFile(Logger.getRootLogger());
            logger = Logger.getLogger(Compiler.class);
        }
        catch (Exception e)
        {
            System.err.println("Failed to initialize logger: " + e.getMessage());
            System.exit(RUNTIME_ERROR_CODE_LOGGER_INITIALIZATION_FAILED);
        }
    }

    private static void loadFilePaths(String[] commandLineArguments)
    {
        if (!Arrays.asList(commandLineArguments).contains("--program"))
        {
            logger.error("--program command is missing.");
            System.exit(RUNTIME_ERROR_CODE_PROGRAM_COMMAND_MISSING);
        }
        if (!Arrays.asList(commandLineArguments).contains("--input"))
        {
            logger.error("--input command is missing.");
            System.exit(RUNTIME_ERROR_CODE_INPUT_COMMAND_MISSING);
        }
        if (!Arrays.asList(commandLineArguments).contains("--output"))
        {
            logger.error("--output command is missing.");
            System.exit(RUNTIME_ERROR_CODE_OUTPUT_COMMAND_MISSING);
        }

        int programCommandIndex = Arrays.asList(commandLineArguments).indexOf("--program");
        int programFilePathIndex = programCommandIndex + 1;
        if (programCommandIndex != -1 && programFilePathIndex < commandLineArguments.length)
        {
            microJavaProgramFilePath = commandLineArguments[programFilePathIndex];
        }
        else
        {
            logger.error("Path to the MicroJava program file not specified.");
            System.exit(RUNTIME_ERROR_CODE_PROGRAM_FILE_PATH_NOT_SPECIFIED);
        }

        int inputCommandIndex = Arrays.asList(commandLineArguments).indexOf("--input");
        int inputFilePathIndex = inputCommandIndex + 1;
        if (inputCommandIndex != -1 && inputFilePathIndex < commandLineArguments.length)
        {
            inputFilePath = Paths.get(commandLineArguments[inputFilePathIndex]);
        }
        else
        {
            logger.error("Path to the input file for the MicroJava program not specified.");
            System.exit(RUNTIME_ERROR_CODE_INPUT_FILE_PATH_NOT_SPECIFIED);
        }

        int outputCommandIndex = Arrays.asList(commandLineArguments).indexOf("--output");
        int outputFilePathIndex = outputCommandIndex + 1;
        if (outputCommandIndex != -1 && outputFilePathIndex < commandLineArguments.length)
        {
            microJavaObjectCodeFilePath = commandLineArguments[outputFilePathIndex] + "/microjava_program.obj";
        }
        else
        {
            logger.error("Path to the folder where MicroJava object code file should be generated not specified.");
            System.exit(RUNTIME_ERROR_CODE_OBJECT_CODE_FILE_PATH_NOT_SPECIFIED);
        }
    }

    private static void printSymbolTable()
    {
        SymbolTableVisitor symbolTableVisitor = new ImprovedSymbolTableVisitor();
        for (Scope currentScope = Tab.currentScope; currentScope != null; currentScope = currentScope.getOuter())
        {
            currentScope.accept(symbolTableVisitor);
        }
        System.out.println();
        logger.info(symbolTableVisitor.getOutput());
        logger.info("End of symbol table.\n");
    }

    private static void executeBuildCommand(String[] commandLineArguments)
    {
        if (!Arrays.asList(commandLineArguments).contains("--build"))
        {
            logger.error("Command for compiling the MicroJava program (--build) is missing.");
            System.exit(RUNTIME_ERROR_CODE_BUILD_COMMAND_MISSING);
        }

        try (
                FileReader fileReader = new FileReader(microJavaProgramFilePath);
                BufferedReader bufferedReader = new BufferedReader(fileReader))
        {
            logger.debug("Compiling source file: " + microJavaProgramFilePath + "\n");

            Yylex lexer = new Yylex(bufferedReader);
            Parser parser = new Parser(lexer);
            Symbol abstractSyntaxTreeRootSymbol = parser.parse();
            Program programSyntaxNode = (Program) abstractSyntaxTreeRootSymbol.value;
            logger.debug("Start of Abstract Syntax Tree (AST).");
            logger.debug(programSyntaxNode.toString(""));
            logger.debug("End of Abstract Syntax Tree (AST).");
            if (parser.isSyntaxAnalysisErrorDetected())
            {
                logger.error("Errors were detected during syntax analysis, aborting compilation.");
                System.exit(RUNTIME_ERROR_CODE_SYNTAX_ANALYSIS_ERROR);
            }
            logger.debug("Syntax analysis has completed successfully for the source file: " + microJavaProgramFilePath + "\n");

            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
            programSyntaxNode.traverseBottomUp(semanticAnalyzer);
            printSymbolTable();
            if (semanticAnalyzer.isSemanticAnalysisErrorDetected())
            {
                logger.error("Errors were detected during semantic analysis, aborting compilation.");
                System.exit(RUNTIME_ERROR_CODE_SEMANTIC_ANALYSIS_ERROR);
            }
            logger.debug("Semantic analysis has completed successfully for the source file: " + microJavaProgramFilePath + "\n");

            CodeGenerator codeGenerator = new CodeGenerator();
            programSyntaxNode.traverseBottomUp(codeGenerator);
            File objectCodeFile = new File(microJavaObjectCodeFilePath);
            if (objectCodeFile.exists())
            {
                objectCodeFile.delete();
            }
            Code.write(Files.newOutputStream(objectCodeFile.toPath()));
            logger.debug("Code generation has completed successfully for the source file: " + microJavaProgramFilePath + "\n");
        }
        catch (Exception exception)
        {
            logger.error(exception);
            System.exit(RUNTIME_ERROR_CODE_GENERIC_RUNTIME_EXCEPTION);
        }
    }

    private static void executeDisasmCommand(String[] commandLineArguments)
    {
        if (!Arrays.asList(commandLineArguments).contains("--disasm"))
        {
            return;
        }

        try
        {
            logger.debug("Disassembling the generated object code file: " + microJavaObjectCodeFilePath + "\n");
            String[] disassemblyCommandLineArguments = new String[] { microJavaObjectCodeFilePath };
            disasm.main(disassemblyCommandLineArguments);
            System.out.println();
            logger.debug("Disassembly has completed successfully for the generated object code file: " + microJavaObjectCodeFilePath + "\n");
        }
        catch (Exception exception)
        {
            logger.error(exception);
            System.exit(RUNTIME_ERROR_CODE_GENERIC_RUNTIME_EXCEPTION);
        }
    }

    private static void executeDebugCommand(String[] commandLineArguments)
    {
        if (!Arrays.asList(commandLineArguments).contains("--debug"))
        {
            return;
        }

        try
        {
            logger.debug("Running the object code file in debug mode in MicroJava Virtual Machine: " + microJavaObjectCodeFilePath);
            String[] debugCommandLineArguments = new String[] { microJavaObjectCodeFilePath, "-debug" };
            System.setIn(Files.newInputStream(inputFilePath));
            Run.main(debugCommandLineArguments);
            System.out.println();
            System.out.println();
            logger.debug("Run in debug mode has completed successfully for the object code file in MicroJava Virtual Machine: " + microJavaObjectCodeFilePath + "\n");
        }
        catch (Exception exception)
        {
            logger.error(exception);
            System.exit(RUNTIME_ERROR_CODE_GENERIC_RUNTIME_EXCEPTION);
        }
    }

    private static void executeRunCommand(String[] commandLineArguments)
    {
        if (!Arrays.asList(commandLineArguments).contains("--run"))
        {
            return;
        }

        try
        {
            logger.debug("Running the object code file in MicroJava Virtual Machine: " + microJavaObjectCodeFilePath + "\n");
            String[] runCommandLineArguments = new String[] { microJavaObjectCodeFilePath };
            System.setIn(Files.newInputStream(inputFilePath));
            Run.main(runCommandLineArguments);
            System.out.println();
            System.out.println();
            logger.debug("Run has completed successfully for the object code file in MicroJava Virtual Machine: " + microJavaObjectCodeFilePath + "\n");
        }
        catch (Exception exception)
        {
            logger.error(exception);
            System.exit(RUNTIME_ERROR_CODE_GENERIC_RUNTIME_EXCEPTION);
        }
    }

    public static void main(String[] commandLineArguments)
    {
        initializeLogger();
        loadFilePaths(commandLineArguments);
        executeBuildCommand(commandLineArguments);
        executeDisasmCommand(commandLineArguments);
        executeDebugCommand(commandLineArguments);
        executeRunCommand(commandLineArguments);
    }
}
