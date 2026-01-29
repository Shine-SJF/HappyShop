    package ci553.happyshop.login;


/**
 * stores the state of the currently logged-in user
 * Singleton based so info is secure and can be accessed consistently throughout the application
 * CurrentUser is an instance of UserAccount, it checks if password or 2FA stage has been verified, this allows certain roles acsess to get certain clients
 */

    public class CurrentUser {
        private static CurrentUser instance; //creates instance to keep cohesive

        private UserAccount account; //aggregates UserAccount
        private boolean passwordVerified;
        private boolean twoFactorVerified;

        private CurrentUser() {}

        public static CurrentUser getInstance() {
            if (instance == null) instance = new CurrentUser();
            return instance;
        }

        public void startSession(UserAccount account) {
            this.account = account;
            this.passwordVerified = true;
            this.twoFactorVerified = false;
        }

        public void verifyTwoFactor() {
            this.twoFactorVerified = true;
        }

        public void clear() { //clears user data
            this.account = null;
            this.passwordVerified = false;
            this.twoFactorVerified = false;
        }

        public UserAccount getAccountOrNull() {
            return account;
        }

        public String getUsernameOrNull() {
            return account == null ? null : account.getUsername();
        }

        public UserRoles getRoleOrNull() {
            return account == null ? null : account.getRole();
        }
        public boolean isFullyAuthenticated() {
            if (account == null) return false; 
            if (!passwordVerified) return false;
            if (account.isTwoFactorEnabled() && !twoFactorVerified) return false;
            return true;
        }
    }