package stock.research;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Delete {

    public static void main(String[] args) throws Exception {
        //String command = "powershell.exe  your command";
        //Getting the version
        String st = "PETR4.SA,WEGE3.SA,BBAS3.SA,BPAC11.SA";
        String command = "powershell.exe  " + System.getProperty("user.dir") + "\\src\\main\\resources\\YF\\yfiance.ps1 " + "'"+  st + "'" ;
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

         // Getting the results
        powerShellProcess.getOutputStream().close();
        String line;
        System.out.println("Standard Output:");
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            System.out.println("Output -> \n" + line);
        }
        stdout.close();
        System.out.println("Standard Error:");
        BufferedReader stderr = new BufferedReader(new InputStreamReader(
                powerShellProcess.getErrorStream()));
        while ((line = stderr.readLine()) != null) {
            System.out.println(line);
        }
        stderr.close();
        System.out.println("Done");

    }
}
