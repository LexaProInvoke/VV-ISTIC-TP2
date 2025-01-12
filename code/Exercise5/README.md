# Code of your exercise

Put here all the code created for this exercise


## Cyclomatic Complexity with JavaParser

To implement the cyclomatic sequence using java parser, we created a class  CyclomaticComplexityAnalyzer. This class analyzes the cyclomatic complexity of methods in source code files.
The program extends the VoidVisitorAdapter class from the JavaParser library to visit and analyze the methods in the source code.

Cyclomatic complexity is calculated for each method based on various control flow structures such as if, switch, for, while, and logical binary expressions.

To demonstrate how the programme works, a report is generated in CSV format containing information about the package, class, method, parameters, cyclomatic complexity and a histogram showing the complexity.

## To use the program:
 ### Openjdk 21 was used to run the project.

- Open the Maven project "EX5" in IntelliJ IDEA.
- In the run configurations, add the path to the files you want to check.
- Run the programme

After running the program, a CSV report file named ReportCC.csv will be generated, containing information about each method's cyclomatic complexity.

An example of the generated file can be found in the answer folder. Also below is a picture of the answer "EX_5_Figure_1.png". It is better to open the generated file in tabular editors such as Google tables.

## Programme code 
```
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
```
## Code of the pom file 
```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>org.example</groupId>
    <artifactId>EX5</artifactId>
    <version>1.0-SNAPSHOT</version>

    <properties>
        <maven.compiler.source>11</maven.compiler.source>
        <maven.compiler.target>11</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <dependency>
            <groupId>com.github.javaparser</groupId>
            <artifactId>javaparser-symbol-solver-core</artifactId>
            <version>3.25.8</version>
        </dependency>
    </dependencies>

</project>
```
## Programme output
![alt ex 5](EX_5_Figure_1.png)
This screenshot shows the histogram of the 2 projets com.entis.travelbot.entity.db and the NoGetterTest.java example projets.

The classes from the com.entis.travelbot.entity.db package mostly contain methods with low Cyclomatic Complexity (CC), which may indicate their simplicity and lack of complex branching.

The PassengerServiceImpl class from the com.entis.travelbot.travelbot.service.db.impl package has an update method with a high Cyclomatic Complexity (CC) of 6, which may indicate that there are complex branches within the method.

The classes from the org.example package contain methods with low and medium Cyclomatic Complexity (CC). For example, the analyseFields method from the FieldGetterAnalyzer class has CC = 3, which may indicate the presence of complex branches.

