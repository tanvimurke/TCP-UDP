/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
import java.net.*;
import java.io.*;
import java.util.Scanner;

public class RemoteVariableClientTCP {
    //use port 7777
    static int serverPort = 7777;
    public static void main(String args[]) {
        // arguments supply hostname
        //code from Coulouris text
        //start of the code
        System.out.println("The TCP client is running.");
        //initializes flag
        boolean flag = true;
        while (flag) {
            //prints console for user to chose menu
            System.out.println("1. Add a value to your sum.");
            System.out.println("2. Subtract a value from your sum.");
            System.out.println("3. Get your sum.");
            System.out.println("4. Exit client.");
            //gets user choice
            Scanner sc1 = new Scanner(System.in);
            int choice = sc1.nextInt();
            //initializes id, number, operation
            int id = 0;
            int number = 0;
            String operation = "";

            switch (choice) {
                case 1:
                    //gets number to add
                    System.out.print("Enter value to add: ");
                    number = sc1.nextInt();
                    //gets id
                    System.out.print("Enter your ID (between 0-999): ");
                    id = sc1.nextInt();
                    //sets operation as add
                    operation = "add";
                    break;
                case 2:
                    //gets number to subtract
                    System.out.print("Enter value to subtract: ");
                    number = sc1.nextInt();
                    //gets id
                    System.out.print("Enter your ID (between 0-999): ");
                    id = sc1.nextInt();
                    //sets operation as subtract
                    operation = "subtract";
                    break;
                case 3:
                    //gets id
                    System.out.print("Enter your ID (between 0-999): ");
                    id = sc1.nextInt();
                    //sets operation get
                    operation = "get";
                    break;
                case 4:
                    //exit
                    flag = false;
                    break;
                default:
                    System.out.println("Invalid option.");
            }
            String requestString = "";
            if (!operation.equals("get")) {
                //if add or subtract then pass id, operation, number
                requestString = id + "," + operation + "," + number;
            } else {
                //if get then pass id, operation
                requestString = id + "," + operation;
            }
            //get data from connection method
            String data = connection(requestString);
            //prints the data
            System.out.println("The result is : " + data);

        }

    }

    //connection method encapsulates the socket communication
    public static String connection(String requestString) {
        //initialize socket
        Socket clientSocket = null;
        String data = "";
        try {
            //bind socket to port
            clientSocket = new Socket("localhost", serverPort);
            //get the input using sockets
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //creates output stream using socket
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            //stream request string in output
            out.println(requestString);
            //flush
            out.flush();
            // read a line of data from the stream
            data = in.readLine();

            //closes the socket
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
        //returns data
        return data;
    }
}