/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.redditprog.webcrawler;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Scanner;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 *
 * @author ryan
 */
public class Extractor {

    // Instance variables
    private int num_pics;
    private String sub;
    private String dir;
    private Scanner scanner;

    // Constructor
    public Extractor(Scanner scan) {
        this.scanner = scan;
    }

    public void beginExtract() {
        this.greeter();

        Document page;
        int i = 0;
        try {
            page = Jsoup.connect("http://www.reddit.com/r/"
                    + sub + "/top/?sort=top&t=month").get();

            //Selecting all the elements with HTML class "title", 
            //that have nested inside <a href="..">..</a> tags
            //that end with jpg or png
            Elements images = page.select(".title").select("a[href$=jpg], a[href$=png]");

            for (Element link : images) {
                if (i == num_pics) {
                    break;
                }

                //Saving the url of the picture
                URL addr = new URL(link.attr("href"));
                InputStream in = addr.openStream();
                OutputStream op = null;
                String[] tab = link.attr("href").split("/");
                //TODO: If the specified path doesn't exist try and create it
                try {
                    if ((link.attr("href").endsWith("jpg"))
                            || (link.attr("href").endsWith("png"))) {
                        op = new FileOutputStream(dir + tab[tab.length - 1]);
                    } else {
                        System.out.println("Sorry, no dice.. yet");
                    }
                } catch (FileNotFoundException e) {
                    System.out.println("You have entered an invalid path. Try again.");
                    System.exit(-1);
                }
                //Saving the picture to the file
                byte[] b = new byte[20480];
                try {
                    int length;
                    while ((length = in.read(b)) != -1) {
                        op.write(b, 0, length);
                    }
                } catch (IOException e) {
                    System.out.println("An error occured while saving the picture.");
                }
                in.close();
                op.close();

                System.out.println("Download complete: " + link.attr("href"));
                System.out.println("File has been saved in: " + dir + tab[tab.length - 1]);
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void greeter() {
        System.out.println("Enter how many pictures do you want to download: ");
        this.num_pics = this.scanner.nextInt();

        System.out.println("What subbredit do you want to download from?");
        this.sub = this.scanner.next();

        System.out.println("What directory do you want to save in? Ex: C:\\Users\\<your_username>\\Desktop\\");
        this.dir = this.scanner.next();
    }

}
