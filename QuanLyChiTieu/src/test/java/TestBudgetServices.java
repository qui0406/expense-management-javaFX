
import com.qlct.services.BudgetServices;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/**
 *
 * @author DELL
 */
public class TestBudgetServices {

    private BudgetServices budgetServices = new BudgetServices();

    @Test
    public void testValidateInput_validInput() {
        double result = budgetServices.validateInput("100000");
        Assertions.assertEquals(100000, result);
    }

    @Test
    public void testValidateInput_inputWithLeadingZeros() {
        double result = budgetServices.validateInput("0000123");
        Assertions.assertEquals(123, result);
    }

    @Test
    public void testValidateInput_invalidInputString() {
        double result = budgetServices.validateInput("abc123");
        Assertions.assertEquals(0.0, result);
    }

    @Test
    public void testValidateInput_emptyInput() {
        double result = budgetServices.validateInput("");
        Assertions.assertEquals(0.0, result);
    }
    
    
}
