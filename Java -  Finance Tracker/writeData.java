import java.io.*;
import java.time.LocalDate;
//import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Scanner;
class records implements Serializable
{
    private final String time, send, rec, type, cat;
    private final double amount;

    private final static double iSalary = 100000;
    private final static double iStocks = 20000;
    private final static double iFDs = 10000;
    private final static double iMF = 12000;
    private final static double iRE = 10000;
    private final static double iMisc = 5000;

    private final static double eHouse = 20000;
    private final static double eFood = 13000;
    private final static double eTravel = 10000;
    private final static double eUtilities = 15000;
    private final static double eLeisure = 4000;
    private final static double eMisc = 5000;
    private final static double eLoans = 10000;

    private final static double tDay = 2333.33;





    public records(String ti, String s, String r, double a, String t, String c)
    {
        this.time=ti;
        this.send=s;
        this.rec=r;
        this.amount=a;
        this.type=t;
        this.cat=c;
    }

    public static void enterData()
    {
        Scanner in =  new Scanner(System.in);
        File file = new File("records.txt");
        ArrayList<records> al = new ArrayList<>();
        ObjectOutputStream oos;
        System.out.println("Enter the number of records: ");
        int n=in.nextInt();
        int count=n;
        for(int i=0;i<n;i++)
        {
            System.out.println("Enter the date of transaction(in yyyy/MM/dd format): ");
            String time =  in.next();
            in.nextLine();
            System.out.println("Enter sender name: ");
            String send = in.nextLine();
            System.out.println("Enter receiver name: ");
            String rec=in.nextLine();
            System.out.println("Enter amount: ");
            double amount = in.nextDouble();

            System.out.println("type: ");
            System.out.println("Options: 1. income 2. expense");
            String type = "";
            int op=in.nextInt();
            switch (op) {
                case 1 -> type = "income";
                case 2 -> type = "expense";
            }
            String cat="";
            int op2;
            switch (type)
            {
                case "income" ->
                {
                    System.out.println("Enter category: ");
                    System.out.println("Options: 1. salary\t2. stocks\t3. FDs\t4. mutual funds\t5. real estate\t6. miscellaneous");
                    op2=in.nextInt();
                    cat=switch (op2) {
                        case 1 -> "salary";
                        case 2 -> "stocks";
                        case 3 -> "FDs";
                        case 4 -> "mutual funds";
                        case 5 -> "real estate";
                        case 6 -> "miscellaneous";
                        default -> null;

                    };
                }
                case "expense" ->
                {
                    System.out.println("Enter category: ");
                    System.out.println("Options: 1. housing\t2. food\t3. travel\t4. utilities\t5.leisure\t6. miscellaneous\t7. emi/loans");
                    op2=in.nextInt();
                    switch (op2) {
                        case 1 -> cat = "housing";
                        case 2 -> cat = "food";
                        case 3 -> cat = "travel";
                        case 4 -> cat = "utilities";
                        case 5 -> cat = "leisure";
                        case 6 -> cat = "miscellaneous";
                        case 7 -> cat = "emi/loans";
                    }
                }
                default -> throw new IllegalStateException("Unexpected value: ");
            }
            al.add(new records(time, send, rec, amount, type, cat));

        }
       
        try 
        {
            oos = new ObjectOutputStream(new FileOutputStream(file, true));
            oos.writeObject(al);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void filterTime()
    {
        Scanner in = new Scanner(System.in);
        String start, end;
        ArrayList<records> al =new  ArrayList<>();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDate st=null;
        LocalDate ed=null;
        try {
            FileInputStream readData = new FileInputStream("records.txt");
            ObjectInputStream readStream = new ObjectInputStream(readData);

            al = (ArrayList<records>)readStream.readObject();
            readStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Choose an option: 1. last 1 week\t2. last 1 month\t3. last 3 months\t4. last 6 months\t5. last 1 year\t6. custom");
        int ch = in.nextInt();
        switch(ch)
        {
            case 1 -> {
                            ed = LocalDate.now();
                            st = ed.minusDays(7);
                        }
            case 2 -> {
                            ed = LocalDate.now();
                            st = ed.minusMonths(1);
                        }
            case 3 -> {
                            ed = LocalDate.now();
                            st = ed.minusMonths(3);
                        }
            case 4 -> {
                            ed = LocalDate.now();
                            st = ed.minusMonths(6);
                        }
            case 5 -> {
                            ed = LocalDate.now();
                            st = ed.minusMonths(12);
                        }
            case 6 -> {
                            System.out.println("Enter the start date: ");
                            start=in.next();
                            System.out.println("Enter the end date: ");
                            end=in.next();
                            st = LocalDate.parse(start, dtf);
                            ed = LocalDate.parse(end, dtf);

                        }
            default -> System.out.println("Please choose a valid option");
        }
        double sd=0, rc=0;
        boolean found=false;
        for(records r: al)
        {
            LocalDate i = LocalDate.parse(r.time, dtf);
            assert st != null;
            if (i.isAfter(st) && i.isBefore(ed))
            {
                found=true;
                System.out.println(r.toString());
                if (r.type.equals("expense"))
                {
                    sd = sd + r.amount;
                }
                else
                {
                    rc = rc + r.amount;
                }
            }
        }

        if(found)
        {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Amount sent in this frame : Rs. "+sd);
            System.out.println("Amount received in this time frame Rs."+rc);
            System.out.println("Net income in this time frame: Rs."+(rc-sd));
            System.out.println("-------------------------------------------------------------------");
            System.out.println("*******************************************************************");
        }
        else
        {
            System.out.println("-------------------------------------------------------------------");
            System.out.println("There have been no transactions in this time frame");
            System.out.println("-------------------------------------------------------------------");
            System.out.println("*******************************************************************");
        }



    }

    public String toString()
        {
            return "{ "+time+"\t"+send+"\t"+rec+"\t"+amount+"\t"+type+"\t"+cat+"}\n";
        }

    public static void displayAll() 
    {
        System.out.println("Data: ");
        double sd=0,rc=0;
        try{
            FileInputStream readData = new FileInputStream("records.txt");
            ObjectInputStream readStream = new ObjectInputStream(readData);
            
            ArrayList<records> al2 = (ArrayList<records>)readStream.readObject();
            for (records r : al2) {
                System.out.println(r.toString());
                if (r.type.equals("expense"))
                {
                    sd = sd + r.amount;
                } else {
                    rc = rc + r.amount;
                }
            }
            System.out.println("-------------------------------------------------------------------");
            System.out.println("Amount sent: Rs."+sd);
            System.out.println("Amount received: Rs."+rc);
            System.out.println("Total balance: Rs."+(rc-sd));
            System.out.println("-------------------------------------------------------------------");
            System.out.println("*******************************************************************");


            //String t1 = al2.get(0).time;
            //String t2 = al2.get(al2.size()-1).time;

            //DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
            //LocalDate l1 = LocalDate.parse(t1, dtf);
            //LocalDate l2 = LocalDate.parse(t2, dtf);
            //long days = ChronoUnit.DAYS.between(l2, l1);
            //System.out.println(days);




        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void filterCat() 
    {
        Scanner in = new Scanner(System.in);
        double sd=0,rc=0;
        boolean found=false;
        System.out.println("Enter type of transaction: 1. income\t2. expense");
        int ch1=in.nextInt();
        String op="";
        switch(ch1)
        {
            case 1 ->
            {
                System.out.println("Choose a category: ");
                System.out.println("Options: 1. salary\t2. stocks\t3. FDs\t4. mutual funds\t5. real estate\t6. miscellaneous");
                int ch2 = in.nextInt();
                op = switch (ch2) {
                    case 1 -> "salary";
                    case 2 -> "stocks";
                    case 3 -> "FDs";
                    case 4 -> "mutual funds";
                    case 5 -> "real estate";
                    case 6 -> "miscellaneous";
                    default -> null;
                };
            }

            case 2 -> {
                System.out.println("Choose a category: ");
                System.out.println("Options: 1. housing\t2. food\t3. travel\t4. utilities\t5.leisure\t6. miscellaneous\t7. emi/loans");
                int ch2 = in.nextInt();
                op = switch (ch2) {
                    case 1 -> "housing";
                    case 2 -> "food";
                    case 3 -> "travel";
                    case 4 -> "utilities";
                    case 5 -> "leisure";
                    case 6 -> "miscellaneous";
                    case 7 -> "emi/loans";
                    default -> null;
                };
            }
        }

        System.out.println("op: "+op);
        System.out.println("Data: ");
        try{
            FileInputStream readData = new FileInputStream("records.txt");
            ObjectInputStream readStream = new ObjectInputStream(readData);
            
            ArrayList<records> al2 = (ArrayList<records>)readStream.readObject();
            readStream.close();
            for (records r : al2)
            {
                if (r.cat.equals(op))
                {
                    System.out.println(r);
                    found = true;

                        sd = sd + r.amount;
                }
                else
                {
                    rc = rc + r.amount;
                }
            }
            if(found)
            {
                System.out.println("-------------------------------------------------------------------");
                System.out.println("Amount sent in category "+op+": Rs."+sd);
                System.out.println("Amount received in category "+op+": Rs."+rc);
                System.out.println("Net spend in category "+op+": Rs."+(rc-sd));
                System.out.println("-------------------------------------------------------------------");
                System.out.println("*******************************************************************");
            }

            else
            {
                System.out.println("-------------------------------------------------------------------");
                System.out.println("There have been no transactions under "+op+" category.");
                System.out.println("-------------------------------------------------------------------");
                System.out.println("*******************************************************************");
            }

        }catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class writeData implements Serializable
{
   public static void main(String[] args) {

        //FileOutputStream fos = null;
        //ObjectOutputStream oos = null;
       try {
           File file = new File("records.txt");
           if (file.createNewFile()) {
               System.out.println("File " + file.getName() + " is created successfully.");
           } else {
               System.out.println("File is already exists in the directory.");
           }
       } catch (IOException exception) {
           System.out.println("An unexpected error is occurred.");
           exception.printStackTrace();
       }

       while (true) {
           Scanner in = new Scanner(System.in);
           System.out.println("-------------------------------------------------------------------");
           System.out.println("Enter a choice: ");
           System.out.println("1. Enter a transaction\n2. View all Transactions\n3. Filter according to time\n4. Filter according to categories\n5. Exit");
           System.out.println("-------------------------------------------------------------------");
           int ch = in.nextInt();
           switch (ch) {
               case 1 -> records.enterData();
               case 2 -> records.displayAll();
               case 3 -> records.filterTime();
               case 4 -> records.filterCat();
               case 5 -> System.exit(0);
               default -> System.out.println("Please choose a valid option");
           }
       }


   }
}


//record fields: time, sender, receiver, amount, type, category.

// categories: housing, food, travel, utilities, leisure, miscellaneous, emi/loans