/*
Author Name: Tanvi Murke
Andrew EmailID: tmurke@andrew.cmu.edu
 */

import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.Scanner;

public class SigningClientTCP {
    //use the port 7777
    static int serverPort = 7777;
    //initialize public key, private key, e, d, n
    static String clientPublicKey = "";
    static String clientPrivateKey = "";
    static String e, d, n;

    public static void main(String args[]) throws Exception {
        // arguments supply hostname
        //start the code
        System.out.println("The TCP client is running.");
        //get e, d, n from the method
        String edn = getEDN();
        //aplit it to get e, d, n
        String[] ednParts = edn.split(",");
        e = ednParts[0];
        d = ednParts[1];
        n = ednParts[2];
        //generate private and public key
        clientPublicKey = e + ";" + n;
        clientPrivateKey = d + ";" + n;
        //prints the whole public and private key
        System.out.println("Public Key: " + clientPublicKey);
        System.out.println("Private Key: " + clientPrivateKey);
        //initialize message digest
        MessageDigest md = null;
        try {
            //implements SHA-256 algorithm
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException(ex);
        }
        //create bytearray of public key and remove the appended ;
        byte[] bigDigestpublic = md.digest(clientPublicKey.replace(";", "").getBytes());
        //create bytearray id
        byte[] id = new byte[20];
        //copies the array
        System.arraycopy(bigDigestpublic, bigDigestpublic.length - 20, id, 0, 20);
        //initializes client
        String clientid = null;
        try {
            //create client id string
            clientid = new String(id, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
        //initialize flag
        boolean flag = true;
        while (flag) {
            //prints console output for user to chose menu
            System.out.println("1. Add a value to your sum.");
            System.out.println("2. Subtract a value from your sum.");
            System.out.println("3. Get your sum.");
            System.out.println("4. Exit client.");
            //choose user input
            Scanner sc1 = new Scanner(System.in);
            int choice = sc1.nextInt();
            //initialize number, operation
            int number = 0;
            String operation = "";
            switch (choice) {
                case 1:
                    //accepts number for addition
                    System.out.print("Enter value to add: ");
                    number = sc1.nextInt();
                    //sets operation as add
                    operation = "add";
                    break;
                case 2:
                    //accepts number for subtraction
                    System.out.print("Enter value to subtract: ");
                    number = sc1.nextInt();
                    //sets operation as subtract
                    operation = "subtract";
                    break;
                case 3:
                    //sets operation as get
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
                //if add and subtract then pass clientid, operation, number, publickey
                requestString = clientid + "," + operation + "," + number + "," + clientPublicKey;
            } else {
                //if get then pass clientid, operation, publickey
                requestString = clientid + "," + operation + "," + clientPublicKey;
            }
            //get signature from sign method
            String signature = sign(requestString);
            //get data from connection method
            String data = connection(requestString + "," + signature);
            //prints the data
            System.out.println("The result is : " + data);

        }

    }

    //connection method to encapsulate socket communication
    public static String connection(String requestString) {
        //initialize socket
        Socket clientSocket = null;
        String data = "";
        try {
            //bind the socket
            clientSocket = new Socket("localhost", serverPort);
            //create input stream
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            //create output stream
            PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream())));
            //send request string
            out.println(requestString);
            //flush stream
            out.flush();
            // read a line of data from the stream
            data = in.readLine();
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
        return data;
    }

    //method to get e, d, n
    //code from project github
    public static String getEDN() {
        // Each public and private key consists of an exponent and a modulus
        BigInteger n; // n is the modulus for both the private and public keys
        BigInteger e; // e is the exponent of the public key
        BigInteger d; // d is the exponent of the private key

        Random rnd = new Random();

        // Step 1: Generate two large random primes.
        // We use 400 bits here, but best practice for security is 2048 bits.
        // Change 400 to 2048, recompile, and run the program again and you will
        // notice it takes much longer to do the math with that many bits.
        BigInteger p = new BigInteger(400, 100, rnd);
        BigInteger q = new BigInteger(400, 100, rnd);

        // Step 2: Compute n by the equation n = p * q.
        n = p.multiply(q);

        // Step 3: Compute phi(n) = (p-1) * (q-1)
        BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        // Step 4: Select a small odd integer e that is relatively prime to phi(n).
        // By convention the prime 65537 is used as the public exponent.
        e = new BigInteger("65537");

        // Step 5: Compute d as the multiplicative inverse of e modulo phi(n).
        d = e.modInverse(phi);

        //combine e d n together and return this string
        String edn = e + "," + d + "," + n;
        return edn;

    }

    //sign method signs the data
    //code from github project
    public static String sign(String message) throws Exception {
        //create message digest to implement SHA-256
        byte[] bytesOfMessage = message.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bigDigest = md.digest(bytesOfMessage);

        // we only want two bytes of the hash for ShortMessageSign
        // we add a 0 byte as the most significant byte to keep
        // the value to be signed non-negative.
        byte[] messageDigest = new byte[bytesOfMessage.length];
        messageDigest[0] = 0;   // most significant set to 0
        System.arraycopy(bigDigest, 0, messageDigest, 1, bigDigest.length);
        // The message digest now has three bytes. Two from SHA-256
        // and one is 0.
        // From the digest, create a BigInteger
        BigInteger m = new BigInteger(messageDigest);
        // encrypt the digest with the private key
        BigInteger bigd = new BigInteger(d);
        BigInteger bign = new BigInteger(n);
        BigInteger c = m.modPow(bigd, bign);
        return c.toString();
    }

}