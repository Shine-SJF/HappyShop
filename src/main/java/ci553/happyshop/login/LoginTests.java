package ci553.happyshop.login;

public class LoginTests {

    public static void main(String[] args) {
        System.out.println("running Login Tests");
        System.out.println(SetDefaultUsers.initializeDefaultUsers()); //initializes default users to make sure users.txt exists

        LogInAuthenticator auth = new LogInAuthenticator();

        assertTrue("customer correct password", //test 1 password correct?
                auth.authenticate("customer1", "customerPassword", "Customer", null));

    
        assertFalse("customer wrong password", //test 2 wrong password fails
                auth.authenticate("customer1", "warehousePassword", "Customer", null));

        assertFalse("wrong role should fail", //test 3 wrong role fails
                auth.authenticate("customer1", "customerPassword", "Admin", null));

        //test 4 requires2FA 
        assertTrue("admin requires 2FA", auth.requires2FA("Admin"));
        assertTrue("picker requires 2FA", auth.requires2FA("Picker"));
        assertTrue("warehouse requires 2FA", auth.requires2FA("Warehouse"));
        assertFalse("customer does not require 2FA", auth.requires2FA("Customer"));

        //test 5 2FA generation and cjeck
        TwoFA twoFA = new TwoFA();
        String code = twoFA.generateAndWrite("admin1", "Admin");
        assertTrue("2FA correct code verifies", twoFA.verify(code));
        assertFalse("2FA wrong code rejects", twoFA.verify("000000"));


    }
    private static void assertTrue(String name, boolean condition) {
        if (condition) {
            System.out.println("[PASS] " + name);
        } else {
            System.out.println("[FAIL] " + name);
        }
    }

    private static void assertFalse(String name, boolean condition) {
        assertTrue(name, !condition);
    }
}
