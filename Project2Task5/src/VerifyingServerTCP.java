/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

public class VerifyingServerTCP {
    //initialize sum
    private static int sum = 0;
    //initialize TreeMap
    private static Map<String, Integer> map = new TreeMap<String, Integer>();

    public static void main(String args[]) throws NoSuchAlgorithmException {
        //start the code
        System.out.println("The TCP server is running.");
        //initialize the socket
        Socket clientSocket = null;
        try {
            int serverPort = 7777; // the server port we are using
            // Create a new server socket
            ServerSocket listenSocket = new ServerSocket(serverPort);
            /*
             * Block waiting for a new connection request from a client.
             * When the request is received, "accept" it, and the rest
             * the tcp protocol handshake will then take place, making
             * the socket ready for reading and writing.
             */
            while (true) {
                clientSocket = listenSocket.accept();
                // If we get here, then we are now connected to a client.
                // Set up "in" to read from the client socket
                Scanner in;
                in = new Scanner(clientSocket.getInputStream());
                String request = in.nextLine();
                String responseString = doOperations(request);
                // Set up "out" to write to the client socket
                PrintWriter out;
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
                /*
                 * Forever,
                 *   read a line from the socket
                 *   print it to the console
                 *   echo it (i.e. write it) back to the client
                 */
                out.println(responseString);
                out.flush();
            }
            // Handle exceptions
        } catch (IOException e) {
            System.out.println("IO Exception:" + e.getMessage());
            // If quitting (typically by you sending quit signal) clean up sockets
        } finally {
            try {
                if (clientSocket != null) {
                    clientSocket.close();
                }
            } catch (IOException e) {
                // ignore exception on close
            }
        }
    }

    //method to perform opeartions
    public static String doOperations(String request) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //split the string by ,
        String[] requests = request.split(",");
        //get id
        String id = requests[0];
        //get operation
        String operation = requests[1];
        //initialize number, public key, signature
        int number = 0;
        String publicKeyString = "";
        String signatureString = "";

        if (operation.equals("get")) {
            // If operation is get return the sum for the given ID
            if (map.containsKey(id)) {
                number = map.get(id);
            }
            //get public key, signature
            publicKeyString = requests[2];
            signatureString = requests[3];
        } else {
            // If operation is add or subtract, update the sum for the given ID
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
            //put it in map
            map.put(id, sum);
            //get public key, signature
            publicKeyString = requests[3];
            signatureString = requests[4];
        }
        String message = "";
        if (!operation.equals("get")) {
            //if add or subtract then pass id, operation, number, public key
            message = id + "," + operation + "," + number + "," + publicKeyString;
        } else {
            //if get then pass id, operation, public key
            message = id + "," + operation + "," + publicKeyString;
        }
        //prints public key
        System.out.println("Public Key Visitor: " + publicKeyString);
        //initializes flag
        boolean check1flag, check2flag;
        //sets flag from the check methods
        check1flag = check1Hash(publicKeyString, id);
        check2flag = check2Verify(message, signatureString, publicKeyString);
        //initialize fkag
        int flag = 0;
        // Verify signature
        if (check1flag == true && check2flag == true) {
            System.out.println("Verified");
            flag = 1;
        }
        String responseString = "";
        //if verified then only proceed with request
        if (flag == 1) {
            responseString = Integer.toString(map.get(id));
        } else {
            responseString = "Error in request";
        }

        return responseString;
    }

    //method to check if public key hash to the ID
    public static boolean check1Hash(String publicKeyString, String id) {
        //message digest to implement SHA-256
        //code from project github
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        //create byte array for public key
        byte[] bigDigestpublic = md.digest(publicKeyString.replace(";", "").getBytes());
        //create id
        byte[] idserver = new byte[20];
        //copies array
        System.arraycopy(bigDigestpublic, bigDigestpublic.length - 20, idserver, 0, 20);
        String clientidcheck = null;
        try {
            //converts client id
            clientidcheck = new String(idserver, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        //checks the clientid of client and server
        if (clientidcheck.equals(id)) {
            return true;
        }else{
            return false;
        }
    }


    //method to check if it is properly signed
    public static boolean check2Verify(String message, String signatureString, String publicKeyString) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        //split public key to get e and n
        String[] publicKeyParts = publicKeyString.split(";");
        String e = publicKeyParts[0];
        String n = publicKeyParts[1];
        //code from project github
        // Take the encrypted string and make it a big integer
        BigInteger encryptedHash = new BigInteger(signatureString);
        // Decrypt it
        BigInteger bige = new BigInteger(e);
        BigInteger bign = new BigInteger(n);
        BigInteger decryptedHash = encryptedHash.modPow(bige, bign);
        // Get the bytes from messageToCheck
        byte[] bytesOfMessageToCheck = message.getBytes("UTF-8");
        // compute the digest of the message with SHA-256
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] messageToCheckDigest = md.digest(bytesOfMessageToCheck);
        // messageToCheckDigest is a full SHA-256 digest
        // take two bytes from SHA-256 and add a zero byte
        byte[] extraByte = new byte[messageToCheckDigest.length + 1];
        extraByte[0] = 0;
        System.arraycopy(messageToCheckDigest, 0, extraByte, 1, messageToCheckDigest.length);
        // Make it a big int
        BigInteger bigIntegerToCheck = new BigInteger(extraByte);
        // inform the client on how the two compare
        if (bigIntegerToCheck.compareTo(decryptedHash) == 0) {
            return false;
        } else {
            return true;
        }
    }
}