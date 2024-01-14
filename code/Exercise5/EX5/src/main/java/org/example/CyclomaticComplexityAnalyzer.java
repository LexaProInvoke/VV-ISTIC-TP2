package org.example;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.stmt.DoStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.SwitchStmt;
import com.github.javaparser.ast.stmt.WhileStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import com.github.javaparser.utils.SourceRoot;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CyclomaticComplexityAnalyzer extends VoidVisitorAdapter<Void> {
    private List<MethodData> methodDataList = new ArrayList<>();

    public List<MethodData> getMethodDataList() {
        return methodDataList;
    }

    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Incorrect source file location path ");
            System.exit(1);
        }

        File file = new File(args[0]);
        if (!file.exists() || !file.isDirectory() || !file.canRead()) {
            System.err.println("Unspecified path to the source directory");
            System.exit(2);
        }

        SourceRoot sourceRoot = new SourceRoot(file.toPath());
        CyclomaticComplexityAnalyzer analyzer = new CyclomaticComplexityAnalyzer();
        sourceRoot.parse("", (localPath, absolutePath, result) -> {
            result.ifSuccessful(unit -> unit.accept(analyzer, null));
            return SourceRoot.Callback.Result.DONT_SAVE;
        });

        generateCSVReport(analyzer.getMethodDataList(), "ReportCC.csv");
    }

    @Override
    public void visit(MethodDeclaration methodDeclaration, Void arg) {
        super.visit(methodDeclaration, arg);

        int cc = calculateCyclomaticComplexity(methodDeclaration);

        String packageName = methodDeclaration.findCompilationUnit()
                .flatMap(CompilationUnit::getPackageDeclaration)
                .map(pd -> pd.getName().asString())
                .orElse("");
        String className = methodDeclaration.findAncestor(ClassOrInterfaceDeclaration.class)
                .map(ClassOrInterfaceDeclaration::getNameAsString)
                .orElse("");
        String methodName = methodDeclaration.getNameAsString();
        String parameters = methodDeclaration.getParameters().toString();

        MethodData methodData = new MethodData(packageName, className, methodName, parameters, cc);
        methodDataList.add(methodData);
    }

    private int calculateCyclomaticComplexity(MethodDeclaration methodDeclaration) {
        int complexity = 1;
        List<IfStmt> ifStatements = methodDeclaration.findAll(IfStmt.class);
        complexity += ifStatements.size();
        List<SwitchStmt> switchStatements = methodDeclaration.findAll(SwitchStmt.class);
        complexity += switchStatements.size();
        List<ForStmt> forStatements = methodDeclaration.findAll(ForStmt.class);
        List<WhileStmt> whileStatements = methodDeclaration.findAll(WhileStmt.class);
        List<DoStmt> doStatements = methodDeclaration.findAll(DoStmt.class);
        complexity += forStatements.size() + whileStatements.size() + doStatements.size();
        List<BinaryExpr> binaryExpressions = methodDeclaration.findAll(BinaryExpr.class,
                expr -> expr.getOperator() == BinaryExpr.Operator.AND || expr.getOperator() == BinaryExpr.Operator.OR);
        complexity += binaryExpressions.size();

        return complexity;
    }

    private static void generateCSVReport(List<MethodData> methodDataList, String outputPath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath))) {
            writer.write("Package,Class,Method,Parameters,Cyclomatic Complexity,Histogram\n");

            for (MethodData methodData : methodDataList) {
                writer.write(
                        methodData.getPackageName() + "," +
                                methodData.getClassName() + "," +
                                methodData.getMethodName() + "," +
                                methodData.getParameters().replace(",", ";") + "," +
                                methodData.getCyclomaticComplexity() + "," +
                                generateHistogram(methodData.getCyclomaticComplexity()) + "\n"
                );
            }

            System.out.println("CSV report with histogram generated successfully: " + outputPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String generateHistogram(int complexity) {
        return "█ ".repeat(complexity);
    }

    private static class MethodData {
        private String packageName;
        private String className;
        private String methodName;
        private String parameters;
        private int cyclomaticComplexity;

        public MethodData(String packageName, String className, String methodName, String parameters, int cyclomaticComplexity) {
            this.packageName = packageName;
            this.className = className;
            this.methodName = methodName;
            this.parameters = parameters;
            this.cyclomaticComplexity = cyclomaticComplexity;
        }

        public String getPackageName() {
            return packageName;
        }

        public String getClassName() {
            return className;
        }

        public String getMethodName() {
            return methodName;
        }

        public String getParameters() {
            return parameters;
        }

        public int getCyclomaticComplexity() {
            return cyclomaticComplexity;
        }
    }
}
