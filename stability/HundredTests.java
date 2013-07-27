/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package stability;

/**
 *
 * @author bruker
 */
public class HundredTests {

    public static void main(String[] args) {
        for (int i = 0; i < 1000; i++) {
            StabilityTest.main(args);
        }
    }
}
