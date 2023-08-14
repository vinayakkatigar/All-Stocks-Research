package stock.research;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class Delete {

    public static void main(String[] args) throws Exception {
        //String command = "powershell.exe  your command";
        //Getting the version
        String command = "powershell.exe  " + "C:\\Delete\\yfiance.ps1 TSLA";
        // Executing the command
        Process powerShellProcess = Runtime.getRuntime().exec(command);

         // Getting the results
        powerShellProcess.getOutputStream().close();
        String line;
        System.out.println("Standard Output:");
        BufferedReader stdout = new BufferedReader(new InputStreamReader(
                powerShellProcess.getInputStream()));
        while ((line = stdout.readLine()) != null) {
            System.out.println("Output -> " + line);
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
