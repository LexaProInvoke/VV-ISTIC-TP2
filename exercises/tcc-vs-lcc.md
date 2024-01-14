# TCC *vs* LCC

Explain under which circumstances *Tight Class Cohesion* (TCC) and *Loose Class Cohesion* (LCC) metrics produce the same value for a given Java class. Build an example of such as class and include the code below or find one example in an open-source project from Github and include the link to the class below. Could LCC be lower than TCC for any given class? Explain.

A refresher on TCC and LCC is available in the [course notes](https://oscarlvp.github.io/vandv-classes/#cohesion-graph).

## Answer

TCC and LCC will be approximately equal for a class when there is a high degree of interdependence between its methods and, at the same time, a low degree of responsibility separation within the class.

An example of such a situation may occur in a small project where all classes are parts of a unified functionality, and the interaction between them is very high. In this case, both TCC and LCC will be equal to 1 or close to 1.

For example, let's use simple code like a calculator:

```
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
```
Thus, TCC = (N - 1) - N + 2 * 1 = 1.
And LCC is simply the number of conditions in the code; in our case, we invoked the method of our class only once.
It is impossible for LCC (logical code complexity) to be less than TCC (total cyclomatic complexity) for a given class. TCC includes the number of decision points in the code, which directly contributes to logical complexity.
