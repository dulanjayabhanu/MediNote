package lk.dulanjaya.medinote.model;

public class Validations {
    public Validations(){

    }

    public static boolean isAlphabetic(String text){
        return text.matches("[a-zA-Z]+");
    }

    public static boolean isOnlyLettersAndSpaces(String text){
        return text.matches("^[ A-Za-z]+$");
    }

    public static boolean isMobileValid(String mobile){
        return mobile.matches("^[0]{1}[7]{1}[01245678]{1}[0-9]{7}$");
    }

    public static boolean isStandardMobileValid(String mobile){
        return mobile.matches("^(\\+\\d{1,3}( )?)?((\\(\\d{1,3}\\))|\\d{1,3})[- .]?\\d{3,4}[- .]?\\d{4}$");
    }

    public static boolean isNumeric(String number){
        return number.matches("[0-9]+");
    }
}
