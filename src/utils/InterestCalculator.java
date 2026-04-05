package utils;

/**
 * Utility class to calculate interest rates.
 */
public class InterestCalculator {

    /**
     * Calculates simple interest.
     *
     * @param principal The principal amount.
     * @param rate      The annual interest rate (in percentage).
     * @param time      The time in years.
     * @return The simple interest calculated.
     */
    public static double simpleInterest(double principal, double rate, double time) {
        return (principal * rate * time) / 100.0;
    }

    /**
     * Calculates compound interest.
     *
     * @param principal The principal amount.
     * @param rate      The annual interest rate (in percentage).
     * @param time      The time in years.
     * @param n         Number of times interest applied per time period.
     * @return The compound interest earned.
     */
    public static double compoundInterest(double principal, double rate, double time, int n) {
        double amount = principal * Math.pow(1 + (rate / (100 * n)), n * time);
        return amount - principal;
    }
}
