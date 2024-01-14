+ Добавить выделение заданий и пунктов  в мд файл
+ добавить картинки в мд файл
Все Файлы упомянутые в данной работе я прикрепляю в папках с кодом задания для которого они использовались.

Ex1 TCC vs LCC

TCC and LCC will be approximately equal for a class when there is a high degree of interdependence between its methods and, at the same time, a low degree of responsibility separation within the class.

An example of such a situation may occur in a small project where all classes are parts of a unified functionality, and the interaction between them is very high. In this case, both TCC and LCC will be equal to 1 or close to 1.

For example, let's use simple code like a calculator:"

public class CalculatorExample {
    private double result;

    public double add(double a, double b) {
        result = a + b;
        return result;
    }

    public double subtract(double a, double b) {
        result = a - b;
        return result;
    }

    public double multiply(double a, double b) {
        result = a * b;
        return result;
    }

    public double divide(double a, double b) {
        if (b != 0) {
            result = a / b;
        } else {
            System.out.println("Cannot divide by zero.");
        }
        return result;
    }

    private void logOperation(String operation) {
        System.out.println("Performed " + operation + ". Result: " + result);
    }

    public static void main(String[] args) {
        CalculatorExample calculator = new CalculatorExample();
        calculator.add(5, 3);
    }
}

Thus, TCC = (N - 1) - N + 2 * 1 = 1.
And LCC is simply the number of conditions in the code; in our case, we invoked the method of our class only once.
It is impossible for LCC (logical code complexity) to be less than TCC (total cyclomatic complexity) for a given class. TCC includes the number of decision points in the code, which directly contributes to logical complexity.

Ex2 Using PMD

Several issues were identified from the project PMD issue. One of the identified issues, which can be considered a true positive, concerns the naming of method parameters in the DAO.java class. The PMD reports that method parameter names do not follow the CamelCase pattern. EX_2_Figure_1.png.

For example, in the code of the class DAO.java:

java
Copy code
public class DAO {
    public void someMethod(int student_id) {
    }

    public void anotherMethod(int prof_id) {
    }
}
The names of the student_id and prof_id parameters do not match the recommended naming pattern.

To fix this problem, you can rename the method parameters to match the required pattern, for example:

java
Copy code
public class DAO {
    public void someMethod(int studentId) {
    }

    public void anotherMethod(int profId) {
    }
}
The parameter names studentId and profId now conform to the common variable naming style in Java.

However, among the issues found, there are also cases that may be false positives. For example, PMD points to an unused import of jakarta.persistence.* in the Prof.java class. This import may actually be used in other parts of the project or may be left as a pre-requisite for future code. In such cases, removing this import may be an unnecessary and wrong action, as it may be used in other parts of the project in the future.

Ex3

To define a new rule for PMD using XPath, we did the following. We opened PMD Designer, wrote the code there and exported it to an .xml file. After exporting, the result file had the following contents:
<rule name="TripleIFOrMore"
      language="java"
      message="Use of nested conditions "
      class="net.sourceforge.pmd.lang.rule.XPathRule">
   <description>

   </description>
   <priority>3</priority>
   <properties>
      <property name="version" value="3.1"/>
      <property name="xpath">
         <value>
<![CDATA[
//IfStatement//IfStatement//IfStatement

]]>
         </value>
      </property>
   </properties>
</rule>

Next, we added a new rule to the quickstart.xml file to consolidate all rules in one file. To test the newly written PMD test, we used the file /home/asuka/Project/VV/VV-ISTIC-TP2/commons-collections-master/src/main/java/org/apache/commons/collections4/ArrayUtils.java. In this file, the 'if' statements follow one another but are not nested. As a result, the testing produces a result as shown in the screenshot 'EX_3_Figure_1.png' indicating that the rule is not violated.

If you run the testing for the entire directory, the result will be as shown in the 'EX_3_Figure_2.png' screenshot. Navigating to the file with the detected issue and going to the relevant line, we see the code as shown in the 'EX_3_Figure_3.png' screenshot. From the code, it is evident that our test identified an error, although at first glance, it may not seem to fit our condition. Upon closer inspection, we understand that the code written in lines 1503 to 1514, during compilation and conversion to bytecode, becomes the same nested 'if' statement.

Ex4
To write a program that should examine a Java file and, in case of its absence, record the class's field, the package in which it is located, and the class name in a results file.
The main program code extends the VoidVisitorAdapter class from the JavaParser library to scan fields and methods of classes for further checking for the presence of public getter methods.
The execution result is saved in a txt file. To test the written code, three files were used: "NoGetterTest.java, Booking.java, PassengerServiceImpl.java," which are attached in the response folder.
To use the program:
- Open the Maven project "EX4" in IntelliJ IDEA.
- In the run configurations, add the path to the files you want to check.
- Run the programme

After running the program, a file will be created with the name:
Result_No_Getter 2024-01-13T18:52:02.372215284Z.txt
Containing:
Field: bigLuggageCount, Class: Booking, Package: com.entis.travelbot.entity.db
Field: seats, Class: Booking, Package: com.entis.travelbot.entity.db
Field: age, Class: Person, Package: Default Package

From the file, it can be seen that in the files that were not attached, there are three fields without public getter methods.

Ex5
Cyclomatic Complexity with JavaParser

To implement the cyclomatic sequence using java parser, we created a class  CyclomaticComplexityAnalyzer. This class analyzes the cyclomatic complexity of methods in source code files.
The program extends the VoidVisitorAdapter class from the JavaParser library to visit and analyze the methods in the source code.

Cyclomatic complexity is calculated for each method based on various control flow structures such as if, switch, for, while, and logical binary expressions.

To demonstrate how the programme works, a report is generated in CSV format containing information about the package, class, method, parameters, cyclomatic complexity and a histogram showing the complexity.

To use the program:
- Open the Maven project "EX5" in IntelliJ IDEA.
- In the run configurations, add the path to the files you want to check.
- Run the programme

After running the program, a CSV report file named ReportCC.csv will be generated, containing information about each method's cyclomatic complexity.

An example of the generated file can be found in the answer folder. Also below is a picture of the answer "EX_5_Figure_1.png". It is better to open the generated file in tabular editors such as Google tables.


