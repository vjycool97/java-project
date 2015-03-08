 
 import java.awt.*;
 import java.awt.image.*;
 import java.io.*;
 import java.lang.Math;


 
   
 public class input  extends Frame
 {
   //declaration
   Image rawimg1;
   int imgcols1,imgrows1;
   MediaTracker tracker1;
   int[] pix1;
   int[][][] pix3;
   static int fileno=1;
   static  float[][] he1=new float[256][4];
   static String str; 
   

   //main method
   public static void main(String args[]) 
   {
  
    try
    {
    Runtime runtime = Runtime.getRuntime();
    System.out.println ("Free memory : "  + runtime.freeMemory() );

    FileReader fs=new FileReader("input.txt");
    BufferedReader br=new BufferedReader(fs);
    histogram hist1 = new histogram();
    while((str=br.readLine())!=null)
    {
      hist1.imgno = fileno;
      hist1.imgname = str;
      System.out.println("image no:"+hist1.imgno+"image name:"+hist1.imgname);
      input obj = new input(str);
      FileOutputStream fos = new FileOutputStream("histogram"+fileno+".dat");
      ObjectOutputStream oos = new ObjectOutputStream(fos);
      for(int i=0;i<256;i++)
      {
         for(int j=0;j<4;j++)
         {    
            hist1.he1[i][j] = he1[i][j];
         }
      }
      oos.writeObject(hist1);
      fileno++;
      oos.close();
      fos.close();
    }
    System.out.println("File Saved");
    histogram hist2;
    for(int k=1;k<fileno;k++)
    { 
        FileInputStream fis = new FileInputStream("histogram"+k+".dat");
        ObjectInputStream ois = new ObjectInputStream(fis);
        hist2=(histogram)ois.readObject();
        System.out.println("image no:"+hist2.imgno+"image name:"+hist2.imgname);
        for(int i=0;i<256;i++)
        {
            System.out.println("");
            for(int j=0;j<4;j++)
            {    
                System.out.print(" "+hist2.he1[i][j]);
            }
        }
       ois.close();
       fis.close();   
   }
   System.exit(0);       
   }
   catch(Exception e)
   {
         System.out.println(e);
         e.printStackTrace();
   }
  }

   //constructer
   input(String s)
   {
    setLayout(new FlowLayout());
    //creating image object
    rawimg1=Toolkit.getDefaultToolkit().getImage(s);
    tracker1= new MediaTracker(this);
    tracker1.addImage(rawimg1,1);

    //checking the image is loaded or not
    try
    {
      if(!tracker1.waitForID(1,10000))
      {
      System.out.println("load error");
      System.exit(1);
      }
    }
    catch(Exception e)
    {
      e.printStackTrace();
      System.exit(1);
    }

    //checking whether all the pixels are loaded of not
    if((tracker1.statusAll(false)&MediaTracker.ERRORED&MediaTracker.ABORTED)!=0)
    {
      System.out.println("Load error");
      System.exit(1);
    }
     

    imgcols1=rawimg1.getWidth(this);
    imgrows1=rawimg1.getHeight(this);
     

     //grabbing pixels into the array
     pix1=new int[imgcols1*imgrows1];
     try
     {
      PixelGrabber pgobj=new PixelGrabber(rawimg1,0,0,imgcols1,imgrows1,pix1,0,imgcols1);
      pgobj.grabPixels();
     }
     catch(Exception e)
     {
     }

     //converting the 1D pixel data to 3D data
     pix3=new int[imgrows1][imgcols1][4];
     
     for(int row=0;row<imgrows1;row++)
     {
       int[] aRow=new int[imgcols1];
       for(int col=0;col<imgcols1;col++)
       {
        int element=row*imgcols1+col;
        aRow[col]=pix1[element];
       }
       for(int col=0;col<imgcols1;col++)
       {
        pix3[row][col][0]=(aRow[col]>>24)&0xFF;
        pix3[row][col][1]=(aRow[col]>>16)&0xFF;
        pix3[row][col][2]=(aRow[col]>>8)&0xFF;
        pix3[row][col][3]=(aRow[col])&0xFF;
       }
     }

     
     int tt1=imgrows1*imgcols1;
     
     for(int i=0;i<256;i++)
     for(int j=0;j<4;j++)
     he1[i][j]=0;

     for(int i=0;i<imgrows1;i++)
     {
     for(int j=0;j<imgcols1;j++)
     {
     for(int k=1;k<4;k++)
     {
       int l=pix3[i][j][k];
       he1[l][k]=he1[l][k]+1;
     }
     }
     }
     for(int i=0;i<256;i++)
     {
     for(int j=1;j<4;j++)
     {
      he1[i][j]=he1[i][j]/tt1;
     }
     }
  }
}

     
