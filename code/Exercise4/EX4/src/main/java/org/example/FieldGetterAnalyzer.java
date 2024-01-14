package org.example;
import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FieldGetterAnalyzer {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java FieldGetterAnalyzer <path-to-source>");
            return;
        }
        String outputName = "Result_No_Getter %s.txt".formatted(Instant.now().toString());
        Arrays.stream(args).forEach(x->analyzeFields(x,outputName));

    }

    private static void analyzeFields(String sourcePath, String outputFileName) {
        try {
            List<String> report = new ArrayList<>();

            Files.walk(new File(sourcePath).toPath())
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".java"))
                    .forEach(filePath -> {
                        try {
                            ParseResult<CompilationUnit> parseResult = new JavaParser().parse(filePath);

                            if (parseResult.isSuccessful()) {
                                CompilationUnit compilationUnit = parseResult.getResult().orElse(null);
                                if (compilationUnit != null) {
                                    FieldGetterVisitor visitor = new FieldGetterVisitor();
                                    visitor.visit(compilationUnit, report);
                                }
                            } else {
                                System.err.println("Failed to parse file: " + filePath);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            generateReport(report, outputFileName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void generateReport(List<String> report, String fileName ) {

        try (
                FileWriter fstream = new FileWriter(fileName,true);
                BufferedWriter out = new BufferedWriter(fstream);
        ) {
            for (String s : report) {
                out.write(s+ "\n");
                out.flush();            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        report.forEach(System.out::println);
    }

    private static class FieldGetterVisitor extends VoidVisitorAdapter<List<String>> {
        @Override
        public void visit(FieldDeclaration fieldDeclaration, List<String> report) {
            String fieldName = fieldDeclaration.getVariables().getFirst().orElseThrow().getNameAsString();
            String className = fieldDeclaration.findAncestor(TypeDeclaration.class)
                    .map(TypeDeclaration::getNameAsString)
                    .orElse("Unknown Class");

            if (!hasPublicGetterMethod(fieldDeclaration, className)) {
                String packageName = fieldDeclaration.findCompilationUnit()
                        .map(compilationUnit -> compilationUnit.getPackageDeclaration().map(pd -> pd.getNameAsString())
                                .orElse("Default Package"))
                        .orElse("Default Package");

                String result = String.format("Field: %s, Class: %s, Package: %s", fieldName, className, packageName);
                report.add(result);
            }
        }

        private boolean hasPublicGetterMethod(FieldDeclaration fieldDeclaration, String className) {
            String fieldName = fieldDeclaration.getVariables().getFirst().orElseThrow().getNameAsString();
            String getterMethodName = "get" + Character.toUpperCase(fieldName.charAt(0)) + fieldName.substring(1);

            return fieldDeclaration.findAncestor(TypeDeclaration.class)
                    .map(td -> td.findAll(MethodDeclaration.class)
                            .stream()
                            .anyMatch(methodDeclaration -> methodDeclaration.getNameAsString().equals(getterMethodName)))
                    .orElse(false);
        }
    }
}
