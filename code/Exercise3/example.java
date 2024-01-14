ublic class ComplexNestedIfTest {
    public static void main(String[] args) {
        int x = 10;
        int y = 20;
        int z = 30;

        if (x > 5) {
            if (y < 25) {
                if (z == 30) {
                    for (int i = 0; i < 5; i++) {
                        if (i % 2 == 0) {
                            while (x > 0) {
                                if (y < 30) {
                                    if (z > 25) {
                                        System.out.println("Deeply nested if statements");
                                    }
                                }
                                x--;
                            }
                        }
                    }
                }
            }
        }
    }
}
