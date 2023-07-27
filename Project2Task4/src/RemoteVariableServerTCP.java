/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
import java.net.*;
import java.io.*;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class RemoteVariableServerTCP {
    //code from Coulouris text
    //initializes sum
    private static int sum = 0;
    //initializes TreeMap
    private static Map<Integer, Integer> map = new TreeMap<Integer, Integer>();
    public static void main(String args[]) {
        //start the code
        System.out.println("The TCP server is running.");
        //initialize socket
        Socket clientSocket = null;

    }

    public static String doOperations(String request){
        String[] requests = request.split(",");
        int id = Integer.parseInt(requests[0]);
        String operation = requests[1];
        int number = 0;

        if (operation.equals("get")) {
            // If operation is get return the sum for the given id
            if (map.containsKey(id)) {
                number = map.get(id);
            }
        } else {
            // If operation is add or subtract, update the sum for the given id
            number = Integer.parseInt(requests[2]);
            if (!map.containsKey(id)) {
                map.put(id, 0);
            }
            int sum = map.get(id);
            if (operation.equals("add")) {
                //performs addition
                sum += number;
            } else if (operation.equals("subtract")) {
                //performs subtraction
                sum -= number;
            }
            //puts id and sum in map
            map.put(id, sum);
        }
        //generates the string and returns it
        String responseString = Integer.toString(map.get(id));
        return responseString;
    }
}