package com.pluralsight;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.stream.Collectors;

public class OnlineStoreApp {
    static String inventoryFilePath="src/main/resources/products.csv";


    public static void main(String[] args) {
        System.out.println("Welcome to 'Best' Buy");
        ArrayList<Product> inventory = getInventory();
        ArrayList<Product> cart = new ArrayList<>();
        homeMenu(inventory,cart);
    }

    public static void homeMenu(ArrayList<Product> inventory,ArrayList<Product> cart){
        System.out.println("Home Menu");
        boolean menuRunning=true;
        while(menuRunning) {
            MyUtils.printDivider(75);
            System.out.println("How can we help you today?\n   1-Products\n   2-Cart\n   3-Quit the application");
            switch (MyUtils.askQuestionGetInt("Enter command: ")){
                case 1:{
                    cart=productMenu(inventory,cart);
                    break;
                }
                case 2:{
                    cartMenu(cart);
                    break;
                }
                case 3:{
                    menuRunning=false;
                    break;
                }
                default:{
                    System.out.println("Error:Invalid Input");
                }

            }

        }
    }

    public static ArrayList<Product> cartMenu(ArrayList<Product> cart){
        System.out.println("Cart Menu");
        boolean menuRunning=true;
        while(menuRunning) {
            MyUtils.printDivider(75);
            System.out.println("Your cart currently contains:");
            listProducts(cart);
            float total=0;
            for(Product product:cart){
                total+=product.getPrice();
            }
            System.out.printf("Your current subtotal is: $%.2f\n",total);
            System.out.println("What would you like to do?\n   1-Check Out\n   2-Remove Product from Cart\n   3-Return to home");
            switch (MyUtils.askQuestionGetInt("Enter command: ")){
                case 1:{
                    checkOut(total);
                    cart.clear();
                    break;
                }
                case 2:{
                    cart=removeFromCart(cart);
                    break;
                }
                case 3:{
                    menuRunning=false;
                    break;
                }
                default:{
                    System.out.println("Error:Invalid Input");
                }

            }

        }
        return cart;
    }

    public static void checkOut(float total){
        MyUtils.printDivider(75);
        System.out.printf("Your total after taxes is $%.2f.\n",total);
        System.out.println("Thank you for shopping with us today!");
    }

    public static ArrayList<Product> removeFromCart(ArrayList<Product> cart){
        MyUtils.printDivider(75);
        String sku=MyUtils.askQuestionGetString("Enter SKU of Product to remove from the cart: ");
        boolean found=false;
        Iterator<Product> iterator = cart.iterator();
        while (iterator.hasNext()) {
            Product product = iterator.next();
            if (sku.equalsIgnoreCase(product.getSku())) {
                System.out.printf("\nRemoved %s to cart.\n",product.getProductName());
                iterator.remove(); // safe removal
                found=true;
                break;
            }
        }
        if(!found){
            System.out.println("\nProduct not found.");
        }
        return cart;
    }

    public static ArrayList<Product> productMenu(ArrayList<Product> inventory,ArrayList<Product> cart){
        System.out.println("Product Menu");
        MyUtils.printDivider(75);
        System.out.println("We carry the following products:");
        listProducts(inventory);
        boolean menuRunning=true;
        while(menuRunning) {
            MyUtils.printDivider(75);
            System.out.println("What would you like to do?\n   1-Search for Product\n   2-Add Product to Cart\n   3-Return to home");
            switch (MyUtils.askQuestionGetInt("Enter command: ")){
                case 1:{
                    searchProducts(inventory);
                    break;
                }
                case 2:{
                    cart=addToCart(inventory,cart);
                    break;
                }
                case 3:{
                    menuRunning=false;
                    break;
                }
                default:{
                    System.out.println("Error:Invalid Input");
                }

            }

        }
        return cart;
    }

    public static ArrayList<Product> addToCart(ArrayList<Product> inventory, ArrayList<Product> cart){
        MyUtils.printDivider(75);
        String sku=MyUtils.askQuestionGetString("Enter SKU of Product to add to the cart: ");
        boolean found=false;
        for(Product product:inventory){
            if(sku.equalsIgnoreCase(product.getSku())){
                System.out.printf("\nAdded %s to cart.\n",product.getProductName());
                cart.add(product);
                found=true;
            }
        }
        if(!found){
            System.out.println("\nProduct not found.");
        }
        return cart;
    }

    public static void searchProducts(ArrayList<Product> inventory){
        MyUtils.printDivider(75);
        System.out.println("Choose how you'd like to filter our selection. Leave option blank to not filter by that option.");
        String filterSKU=MyUtils.askQuestionGetString("SKU: ").trim();
        String name=MyUtils.askQuestionGetString("Product Name: ").trim();
        String maxPriceString=MyUtils.askQuestionGetString("Maximum Price: $");
        float maxPrice;
        try{
            maxPrice=Float.parseFloat(maxPriceString);
            if(maxPrice<=0){
                maxPrice=-1;
            }
        }catch (Exception e){
            maxPrice=-1;
        }
        String department=MyUtils.askQuestionGetString("Department: ").trim();
        ArrayList<Product> filteredInventory=filterInventory(inventory,filterSKU,name,maxPrice,department);
        MyUtils.printDivider(75);
        if(filteredInventory.isEmpty()){
            System.out.println("No items match search.");
        }
        for(Product product:filteredInventory){
            printProduct(product);
        }
    }

    public static ArrayList<Product> filterInventory(ArrayList<Product> inventory,String sku,String name,float maxPrice,String department){
        return inventory.stream()
                .filter(product->sku==null||sku.isEmpty()||product.getSku().equalsIgnoreCase(sku))
                .filter(product->name==null||name.isEmpty()||product.getProductName().toLowerCase().contains(name.toLowerCase()))
                .filter(product->maxPrice==-1||product.getPrice()<=maxPrice)
                .filter(product->department==null||department.isEmpty()||product.getDepartment().equalsIgnoreCase(department))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static void listProducts(ArrayList<Product> inventory){
        for(Product product:inventory){
            printProduct(product);
        }
    }

    public static void printProduct(Product product){
        StringBuilder line=new StringBuilder();
        line.append("   SKU: ");
        line.append(product.getSku());
        line.append(" | ");
        line.append(product.getProductName());
        line.append(" ".repeat(Math.max(0,(40-product.getProductName().length()))));
        line.append("| $");
        line.append(String.format("%.2f",product.getPrice()));
        line.append(" ".repeat(Math.max(0,(6- String.format("%.2f",product.getPrice()).length()))));
        line.append(" | ");
        line.append(product.getDepartment());
        System.out.println(line);
    }

    public static ArrayList<Product> getInventory(){
        ArrayList<Product> inventory = new ArrayList<>();
        try{
            FileReader fr = new FileReader(inventoryFilePath);
            BufferedReader readInventory = new BufferedReader(fr);
            String line;
            readInventory.readLine();
            while ((line = readInventory.readLine()) != null) {
                String[] splitLine = line.split("\\|");
                Product product = new Product(splitLine[0], splitLine[1], Float.parseFloat(splitLine[2]), splitLine[3]);
                inventory.add(product);
            }
            readInventory.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        inventory.sort(Comparator.comparing(Product::getProductName));
        return inventory;
    }
}
