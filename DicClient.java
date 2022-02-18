package com.fr.tests.serveur;

import java.util.Random;
import java.util.Scanner;

import javax.print.DocFlavor.STRING;

import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.UnknownHostException;

public class DicClient {

    public final static String SERVER = "127.0.0.1";
    public final static int PORT = 5000;
    public final static int TIMEOUT = 30000;
    
    public static void main(String[] args) throws UnknownHostException {
        
        SocketAddress addr = new InetSocketAddress(InetAddress.getByName(SERVER), PORT);
        Scanner sc =new Scanner(System.in);

        for(;;) {
            // Les deux joueurs entrent leurs noms dans la console pour lancer la partie
            System.out.print("Enter the name of the 2 players separated by a / to start or Q to quit : ");
            String names = sc.next();
            
            if(names.equalsIgnoreCase("q")) {
                break;
            }

            // Les noms des joueurs sont envoyés au serveur
            String[] name = names.split("/");
            String joueur1 = name[0];
            String joueur2 = name[1];         
            if(name.length == 2) {
                try {
                    request(addr, "POST", "Game", joueur1 + " VS " + joueur2);
                    request(addr, "GET", "", "");
                }
                catch(IOException e) {
                    System.err.println(e.getMessage());
                }                
            }

            // La partie se lance dans la console
            System.out.println("Launching of the game !");

            // Les joueurs choissisent chacun leur tour ce qu'ils souhaitent jouer
            System.out.println(joueur1 + ", it's your turn !");
    		System.out.println("Choose un number between : Rock (0), Paper (1), Scissors (2)");   		
            int choixJoueur1 = sc.nextInt();
            String strChoixJoueur1; 
            if(choixJoueur1 == 0) {
                strChoixJoueur1 = "rock"; 
            } else if(choixJoueur1 == 1) {
                strChoixJoueur1 = "paper"; 
            } else {
                strChoixJoueur1 = "scissors"; 
            }
            System.out.println(joueur2 + ", it's your turn !");
            int choixJoueur2 = sc.nextInt();
            String strChoixJoueur2; 
            if(choixJoueur2 == 0) {
                strChoixJoueur2 = "rock"; 
            } else if(choixJoueur2 == 1) {
                strChoixJoueur2 = "paper"; 
            } else {
                strChoixJoueur2 = "scissors";
            }
            
            // Le choix des joueurs est affiché dans la console et est envoyé au serveur
            String choixJoueurs = joueur1 + " choose " + strChoixJoueur1 + " and " + joueur2 + " choose " + strChoixJoueur2;

            System.out.println(choixJoueurs);

            if(choixJoueurs != null) {
                try {
                    request(addr, "POST", "Choices", choixJoueurs);
                    request(addr, "GET", "", "");
                }
                catch(IOException e) {
                    System.err.println(e.getMessage());
                }                
            }
            
            // Le résultat de la manche est affiché dans la console et est envoyé au serveur
            String result;
            if(choixJoueur1 - choixJoueur2 == 0) {  
                result = "It's a draw !"; 	
            } else if((choixJoueur1 == 0 && choixJoueur2 == 1) || (choixJoueur1 == 1 && choixJoueur2 == 2) || (choixJoueur1 == 2 && choixJoueur2 == 0)) {
            	result = joueur2 + " won !";
            } else {
                result = joueur1 + " won !";
            }         

            System.out.println(result);

            if(result != null) {
                try {
                    request(addr, "POST", "Result", result);
                    request(addr, "GET", "", "");
                }
                catch(IOException e) {
                    System.err.println(e.getMessage());
                }                
            }

            continue;
            
        }          
           
        sc.close();        
    }

    public static void request(SocketAddress addr, String verb, String url, String content) throws IOException
    {	
        Socket socket = new Socket();

        socket.connect(addr, TIMEOUT);

        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        PrintWriter pw = new PrintWriter(socket.getOutputStream(), true);

        pw.printf("%s /%s HTTP/1.1\r\n", verb, url);
        pw.printf("Content-Type: text/plain\r\n");
        pw.printf("Content-Length: %d\r\n\r\n", content.length());
        pw.printf("%s\r\n", content);
        pw.flush();
        
        String tmp;
        
        while((tmp=br.readLine())!=null){
            System.out.println(tmp);
        }

        pw.close();
        br.close();
        socket.close();
    }    
}
