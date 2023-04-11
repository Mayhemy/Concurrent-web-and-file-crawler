package kids.domaci1;

import java.io.File;

public class Validator {

    //fields
    private String[] validArguments;

    public Validator() {
        this.validArguments = new String[]{"ad", "aw", "get", "query", "cws", "cfs", "stop"};
    }

    public boolean isValid(String command) {

        if(command == null)
            return false;

        boolean exists = false;
        String keyword = command.split(" ")[0];

        for(String c: validArguments)
            if(keyword.equals(c)) {
                exists = true;
                break;
            }

        if(!exists) {
            System.out.println("Input not valid.");
            return false;
        }


        String argument;
        int argCount = command.split(" ").length;
        if(keyword.equals("ad")) {

            if(argCount != 2) {
                System.out.println("Input " + keyword + " - invalid number of args");
                return false;
            }

            argument = command.split(" ")[1];
            File file = new File("src/main/resources/" + argument);

            if(!file.exists()) {
                System.out.println("Input " + keyword + " - directory " + argument + " doesn't exist");
                return false;
            }
        }

        if(keyword.equals("aw")) {

            if(argCount != 2) {
                System.out.println("Input " + keyword + " - invalid number of args");
                return false;
            }
        }

        if(keyword.equals("get") || keyword.equals("query")) {

            if(argCount != 2) {
                System.out.println("Input " + keyword + " - invalid number of args");
                return false;
            }

            argument = command.split(" ")[1];
            if(!argument.startsWith("file|") && !argument.startsWith("web|")) {
                System.out.println("Input "+keyword+" - invalid argument");
                return false;
            }
        }
        if(keyword.equals("cws") || keyword.equals("cfs")){
            if(argCount != 1){
                System.out.println("Input " + keyword + " - invalid number of args");
                return false;
            }
        }
        return true;
    }
}