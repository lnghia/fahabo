package Messages;

public class ResponseMsg {
    public static class Authentication{
        public static enum SignIn{
            success, fail;

            @Override
            public String toString() {
                return "authentication.signIn." + this.name();
            }
        }
        public static enum SignUp{
            success, fail, emailExists;

            @Override
            public String toString() {
                return "authentication.signUp." + this.name();
            }
        }
        public static enum PinCode{
            availableIn5mins,
            success,
            fail;

            @Override
            public String toString() {
                return "authentication.pinCode." + this.name();
            }
        }
        public static enum ForgotPassword{
            accountNotExist,
            success,
            fail;

            @Override
            public String toString() {
                return "authentication.forgotPassword." + this.name();
            }
        }
        public static enum RefreshToken{
            fail;

            @Override
            public String toString() {
                return "authentication.refreshToken." + this.name();
            }
        }
        public static enum Verification{
            fail;

            @Override
            public String toString() {
                return "authentication.verification." + this.name();
            }
        }
        public static enum FetchOTP{
            fail;

            @Override
            public String toString() {
                return "authentication.fetchOTP." + this.name();
            }
        }
    }

    public static enum System{
        fail;

        @Override
        public String toString() {
            return "system." + this.name();
        }
    }

    public static enum Validation{
        passwordRequired,
        usernameRequired,
        unauthorized,
        emailRequired,
        nameRequired,
        birthdayRequired,
        languageCodeRequired,
        confirmPasswordMustMatch,
        passwordInvalid,
        usernameInvalid;

        @Override
        public String toString(){
            return "validation." + this.name();
        }
    }
}
