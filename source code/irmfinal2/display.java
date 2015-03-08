/* <html>
<body>
<applet code="display.class" height=600 width=1200>
<param name ="imgn" value = "C:\\irmproject\\irmfinal2\\12.jpg">

</applet>
</body>
</html>*/

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.lang.Math; 
import java.applet.*;

class histogram implements Serializable
{
   public int imgno;
   public String imgname;
   public float[][] he1=new float[256][4];
   public histogram(){}
}  

public class display extends Applet implements Runnable
{
   private Thread imageThread = null;
   private Image imagetoDisplay;
   private Image imageArray[];
   private int noimgs = 6,currentimg=0,sleeptime=1000;   
   float[][] w2=new float[99][2];

   Image rawimg1;
   int imgcols1,imgrows1;
   MediaTracker tracker1;
   int[] pix1;
   int[][][] pix3;
   float[][] hes=new float[256][4];
   float[][] hes1=new float[256][4];
   float w1=0,w3;
   int fileno=1;
   static int n=1;
   int l=10,k=10,m=0;
   Image img1;
   static String str1,str4,str11,strn1,strn2,strn,strn0;
   int x,y;
   
   public void init()

   {
	  
	   /*setLayout(new BorderLayout());
	   Panel p=new Panel();
	   TextField tf1=new TextField(30);
		Button b1=new Button("Browse");
		Button b2=new Button("Search");
		//b1.addActionListener(new ButtonHandler());
		//b2.addActionListener(new ButtonHandler());
			p.add(tf1);
			p.add(b1);
			p.add(b2);
			add("South",p);*/
     try
    {
      str1 = getParameter("imgn");
     input123(str1); 
     
     for(int i=1;i<=94;i++)
     {
      System.out.println("image no: "+w2[i][0]+"irm distance:"+w2[i][1]);
     }
     
     for(int i=1;i<94;i++)
     {
      for(int j=1;j<94-i-1;j++)
      {
       if(w2[j][1]>w2[j+1][1])
       {
        float k=w2[j+1][1];
        float k1=w2[j+1][0];
        w2[j+1][1]=w2[j][1];
        w2[j][1]=k;
        w2[j+1][0]=w2[j][0];
        w2[j][0]=k1;
       }
      }
     }
        
      
     imageArray = new Image[noimgs];
     int j=0;
     String str,strn,strn1;
       
     for(int i=1;i<imageArray.length;i++)
     { 
      strn=""+w2[i][0];
      int x=strn.indexOf('.');
      strn1=strn.substring(0,x);
      str=strn1+".jpg";
      System.out.println(str);
      imageArray[i] = getImage(getDocumentBase(),str);
     } 
     imageThread = new Thread(this);
     imageThread.start(); 
     }catch(Exception e){};
   x=0;y=0;
   }

   public void start()
   {
    imageThread.resume();
   }
   
   public void suspend()
   {
    imageThread.suspend();
   }
  
   public void destroy()
   {
    //imageThread.stop();
   }

   public void run()
   {
    for(int i=1;i<imageArray.length;i++)
    {
     imagetoDisplay = imageArray[i];
     try
     {
      imageThread.sleep(1000);
     }
     catch(Exception e){}
    }
   }

   public void paint(Graphics g)
   { 
    for(int i=0;i<imageArray.length;i++)
    {
     imagetoDisplay = imageArray[i];
     if(imagetoDisplay != null)
        g.drawImage(imagetoDisplay,0+i*150,0,150,150,this);
    }
   }
   
   
   public void input123(String s)
   {
    try
    {

     //creating image object
     System.out.println("processing"+s);
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
     hes[i][j]=0;

     for(int i=0;i<imgrows1;i++)
     {
     for(int j=0;j<imgcols1;j++)
     {
     for(int k=1;k<4;k++)
     {
       int l=pix3[i][j][k];
       hes[l][k]=hes[l][k]+1;
     }
     }
     }
     for(int i=0;i<256;i++)
     {
     for(int j=1;j<4;j++) 
     {
      hes[i][j]=hes[i][j]/tt1;
     }
     }

    //reading from the histogram file
    histogram hist = new histogram();

   for(int k=1;k<=94;k++)
   { 

     for(int i=0;i<256;i++)
     {
     for(int j=1;j<4;j++) 
     {
      hes1[i][j]=hes[i][j];
     }
     }
     
     FileInputStream fis = new FileInputStream("C:\\irmproject\\irmfinal2\\histogram"+k+".dat");
     ObjectInputStream ois = new ObjectInputStream(fis);
     hist=(histogram)ois.readObject(); 
     
     int[][] f1=new int[256][256];
     int[] temp1=new int[256];
     int[] t1=new int[256];
     int[] t2=new int[256];
  
 
     System.out.println("reading image no:"+hist.imgno+"image name:"+hist.imgname);
       
     for(int i=0;i<256;i++)
     {
       temp1[i]=i;
       t2[i]=0;
       t1[i]=0;
     }
  
  
     for(int i=0;i<256;i++)
     {
      for(int j=0;j<256;j++)
      {
       f1[i][j]=Math.abs(temp1[i]-temp1[j]);
      }
     }

     for(int l=0;l<256;l++)
     {
      for(int i=0;i<256;i++)
      {
       for(int j=0;j<256;j++)
       {
        if(f1[i][j]==l&&t1[i]==0&&t2[j]==0)
        {
         float l1,l2;
         l1=hes1[i][1];
         l2=hist.he1[j][1];
         if(l1<l2)
         {
          w1=w1+l*l1;
          hes1[i][1]=0;
          t1[i]=1;
          hist.he1[j][1]=l2-l1;
         } 
 
         if(l1>l2)
         {
          w1=w1+l*l2;
          hes1[i][1]=l1-l2;
          t2[j]=1;
          hist.he1[j][1]=0;
         } 
 
         if(l1==l2)
         {
          w1=w1+l*l1;
          hes1[i][1]=0;
          t1[i]=1;
          t2[j]=1;
          hist.he1[j][1]=0;
         } 
        }
       }
      }
     }
  

     for(int i=0;i<255;i++)
     {
      t1[i]=0;
      t2[i]=0;
     }


     for(int l=0;l<255;l++)
     {
      for(int i=0;i<255;i++)
      {
       for(int j=0;j<255;j++)
       {
        if(f1[i][j]==l&&t1[i]==0&&t2[j]==0)
        {
         float l1,l2;
         l1=hes1[i][2];
         l2=hist.he1[j][2];
         if(l1<l2)
         {
          w1=w1+l*l1;
          hes1[i][2]=0;
          t1[i]=1;
          hist.he1[j][2]=l2-l1;
         } 
    
         if(l1>l2)
         {
          w1=w1+l*l2;
          hes1[i][2]=l1-l2;
          t2[j]=1;
          hist.he1[j][2]=0;
         } 
      
         if(l1==l2)
         {
          w1=w1+l*l1;
          hes1[i][2]=0;
          t1[i]=1;
          t2[j]=1;
          hist.he1[j][2]=0;
         } 
        }
       }
      }
     }


     for(int i=0;i<255;i++)
     {
      t1[i]=0;
      t2[i]=0;
     }

     for(int l=0;l<255;l++)
     {
      for(int i=0;i<255;i++)
      {
       for(int j=0;j<255;j++)
       {
        if(f1[i][j]==l&&t1[i]==0&&t2[j]==0)
        {
         float l1,l2;
         l1=hes1[i][3];
         l2=hist.he1[j][3];
         if(l1<l2)
         {
          w1=w1+l*l1;
          hes1[i][3]=0;
          t1[i]=1;
          hist.he1[j][3]=l2-l1;
         } 
     
         if(l1>l2)
         {
          w1=w1+l*l2;
          hes1[i][3]=l1-l2;
          t2[j]=1;
          hist.he1[j][3]=0;
         } 
      
         if(l1==l2)
         {
          w1=w1+l*l1;
          hes1[i][3]=0;
          t1[i]=1;
          t2[j]=1;
          hist.he1[j][3]=0;
         } 
        }
       }
      }
     }
     System.out.println("distance for this image is:"+w1);  
     w2[k][0]=k;
     w2[k][1]=w1;
     w1=0;
     ois.close();
     fis.close(); 
    }
   }catch(Exception e2){System.out.println(e2);} 
  }
/*  class ButtonHandler implements ActionListener
	{
	  public void actionPerformed(actionEvent e)
		{
		  String s=e.getActionCommand();
		  if("Browse".equals(s))
			{



		  }
	  }
  }*/

}
 
