import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.List;
import java.awt.event.*;
import java.applet.*;
import java.util.*;
import javax.swing.*;


class Mthread extends Thread
{
	Socket sen;
	Socket rec;
	OutputStream out;

	Mthread(Socket recc)
	{
		rec = recc;
		start();
	}

	public boolean isAllow(String ip) throws Exception
	{
		FileInputStream in = new FileInputStream("DAT");
		int allow = in.read();
		in.read();in.read();

		String fip = new String();
		
		int c;
		while( (c=in.read()) != -1)
		{
			if((char)c != '\r')
			{
				fip = fip + (char)c;
			}
			else
			{
				if(fip.equals(ip) == true && (char)allow == '0')
				{
					return false;
				}
				else if(fip.equals(ip) == true && (char)allow == '1')
				{
					return true;
				}
				fip = "";
				c=in.read();
			}
			
		}

		if((char)allow == '0')
		{
			return true;
		}
		if((char)allow == '1')
		{
			return false;
		}
		return true;

	}

	public void run()
	{	
		try{
		InputStream in = rec.getInputStream();
		out = rec.getOutputStream();
		String dat = new String();
		String post = "POST";
		while(true)
		{
			int c = in.read();
			if(c==-1)
			{	
				break;
	
			}
			dat = dat + (char)c;
			
			if(dat.length()-3>0)	
			{
				if(dat.charAt(dat.length()-3) == '\n' && (char)c=='\n')
				{	
					if(dat.charAt(0)=='P' && dat.charAt(1)=='O')
					{
						c=0;
						int pos = dat.indexOf("Content-Length: ");
						pos=pos+16;
						
						String len=new String();
						while(dat.charAt(pos)!='\r')
						{
							len = len + dat.charAt(pos);	
							pos++;
						}
					
						int ll = Integer.parseInt(len);
						while(ll!=0)
						{
							c=in.read();
							dat = dat + (char)c;
							ll--;
						}
						System.out.println(dat);
						break;
						
					}
					else
					{
						break;				
					}
					
				}
			}
			
			
		}
		
		int i=dat.indexOf("//")+2;
		while(dat.charAt(i)!= '/')
		{
			i++;
		}
		
		String host = dat.substring(dat.indexOf("//")+2,i);
		String req = new String();
		System.out.println("HOST IS :-- " + host);
		
		i=0;
		while(dat.charAt(i)!=' ')
		{
			req = req + dat.charAt(i);
			i++;
		}
		req = req + ' ';

		i=dat.indexOf("//")+2;
		while(dat.charAt(i)!= '/')
		{
			i++;
		}
		
		while(i!=dat.length())
		{
			req = req + dat.charAt(i);
			i++;
		}
					
		System.out.println("REQUEST IS : --- " + req);
				
		sen = new Socket(host,80);

		String check_ip = "" + sen.getInetAddress();
		String c_ip = check_ip.substring(check_ip.indexOf("/")+1,check_ip.length());

		if(isAllow(c_ip) == false)
		{
			sen.close();
			String er = "You are not allowded to visit this web page !!";
			out.write(er.getBytes());
			out.close();
			rec.close();
			stop();
		}

		InputStream br = sen.getInputStream();
		OutputStream bo = sen.getOutputStream();

		bo.write(req.getBytes());

		int qq;
		int q;
		while(true)
		{
			qq=br.read();
			
			if(qq==-1)
			{
				sen.close();
				rec.close();
				stop();
			}
			out.write(qq);
		
		}	
	
		}

		catch(IOException e)
		{
			System.out.println(e);
			try{
			String er = "Error: could not able to connect to the specified server ";
			out.write(er.getBytes());
			out.close();
			sen.close();
			rec.close();
			stop();
			}
			catch(Exception ee){}


		}
		
		catch(Exception e)
		{
			System.out.println(e);
			try{
			String er = "Error: could not able to connect to the specified server ";
			out.write(er.getBytes());
			out.close();
			sen.close();
			rec.close();
			stop();
			}
			catch(Exception ee){}
			
		}

	}
	

}



public class clock extends Applet implements WindowListener, ActionListener, ItemListener
{
	static Frame win;
	static clock cl;
	static Mthread thr;
	static int x=0;
	static TextField ptext;
	static Button okbut;
	static Label enter_port;
	static Label configuration;
	static Choice choice;
	static List iplist;
	static Button removeip;
	static Label new_ip;
	static TextField newip_text;
	static Button newip_but;
	static int port;
	
	public static void main(String arg[]) throws Exception
	{
		win = new Frame("HTTP Proxy Server");
		cl = new clock();
		new_ip = new Label("Enter a new IP address is the list ");
		new_ip.setBackground(new Color(33,44,55));
		new_ip.setForeground(new Color(255,255,255));
		newip_text = new TextField("000.000.000.000");
		newip_but = new Button("ADD");
		newip_but.addActionListener(cl);
		ptext = new TextField("4445");
		okbut = new Button("START");
		okbut.addActionListener(cl);
		enter_port = new Label("Enter the port number : ");
		enter_port.setForeground(new Color(255,255,255));
		enter_port.setBackground(new Color(33,44,55));
		configuration = new Label("Proxy server configuration : ");
		configuration.setForeground(new Color(255,255,255));
		configuration.setBackground(new Color(33,44,55));
		choice = new Choice();
		choice.add("Allow only the below IP address ");
		choice.add("Don't allow these IP address ");
		choice.addItemListener(cl);
		iplist = new List();
		removeip = new Button("Remove the selected IP addresse(s) from the list ");
		removeip.addActionListener(cl);

		iplist.setMultipleMode(true);

		FileInputStream fil = new FileInputStream("DAT");
		String temp = new String();
		
		int opt = fil.read();fil.read();fil.read();
		
		int c;
		while( (c=fil.read()) != -1)
		{
			if((char)c != '\r')
			{
				temp = temp + (char)c;
			}
			else
			{
				iplist.add(temp);
				temp="";
				c=fil.read();
			}
		}
		fil.close();		
		
		cl.add(removeip);
		cl.add(iplist);
		cl.add(choice);
		cl.add(enter_port);	
		cl.add(configuration);
		cl.add(ptext);
		cl.add(okbut);
		cl.add(new_ip);
		cl.add(newip_text);
		cl.add(newip_but);
		cl.setBackground(okbut.getBackground());		

		if((char)opt=='0')
		{
			choice.select(1);
		}
		else
		{
			choice.select(0);
		}
	
		win.setBackground(new Color(33,44,55));		
		win.add(cl);
		win.setSize(500,500);
		win.setVisible(true);
		win.addWindowListener(cl);
		
		ServerSocket ser=null;
		Socket soc;
		String dat = new String();
////////////////////////////////////////////////////////////////////////////////////////////
		enter_port.setLocation(10,10);
		ptext.setLocation((int)enter_port.getLocation().getX() + (int)enter_port.getSize().getWidth() + 10, 10);
 		okbut.setLocation((int)ptext.getLocation().getX(),(int)ptext.getLocation().getY()+50);
		okbut.setSize((int)ptext.getSize().getWidth(),(int)okbut.getSize().getHeight());
		
		

		configuration.setLocation(10,130);
		choice.setLocation(10,170);
		iplist.setLocation(10,200);
		iplist.setSize((int)choice.getSize().getWidth(),100); 
		removeip.setLocation(10,350);

		new_ip.setLocation((int)iplist.getLocation().getX() + (int)iplist.getSize().getWidth()+50,(int)iplist.getLocation().getY());
		newip_text.setLocation((int)new_ip.getLocation().getX(),(int)new_ip.getLocation().getY()+30);
		newip_text.setSize((int)new_ip.getSize().getWidth(),(int)newip_text.getSize().getHeight());
		newip_but.setLocation((int)newip_text.getLocation().getX(),(int)newip_text.getLocation().getY()+50);
		newip_but.setSize((int)newip_text.getSize().getWidth(),(int)newip_text.getSize().getHeight());
///////////////////////////////////////////////////////////////////////////////////////////////
	
		while(true)
		{
			
			if(x==1)
			{	
				if(ser != null && ser.getLocalPort() != port)
				{
					ser = new ServerSocket(port);
				}
				else if(ser==null)
				{
					ser = new ServerSocket(port);
				}
				while(x==1)
				{
					soc = ser.accept();
					thr = new Mthread(soc);			
				}
				ser.close();
			}
			else 
			{
				soc=null;
				if(ser!=null)
					ser=null;

			}
			
		}
		
				

	}

	public void paint(Graphics g)
	{
		enter_port.setLocation(10,10);
		ptext.setLocation((int)enter_port.getLocation().getX() + (int)enter_port.getSize().getWidth() + 10, 10);
 		okbut.setLocation((int)ptext.getLocation().getX(),(int)ptext.getLocation().getY()+50);
		okbut.setSize((int)ptext.getSize().getWidth(),(int)okbut.getSize().getHeight());
		
		g.setColor(new Color(255,255,255));
		g.drawLine(10,100,800,100);

		configuration.setLocation(10,130);
		choice.setLocation(10,170);
		iplist.setLocation(10,200);
		iplist.setSize((int)choice.getSize().getWidth(),100); 
		removeip.setLocation(10,350);

		new_ip.setLocation((int)iplist.getLocation().getX() + (int)iplist.getSize().getWidth()+50,(int)iplist.getLocation().getY());
		newip_text.setLocation((int)new_ip.getLocation().getX(),(int)new_ip.getLocation().getY()+30);
		newip_text.setSize((int)new_ip.getSize().getWidth(),(int)newip_text.getSize().getHeight());
		newip_but.setLocation((int)newip_text.getLocation().getX(),(int)newip_text.getLocation().getY()+50);
		newip_but.setSize((int)newip_text.getSize().getWidth(),(int)newip_text.getSize().getHeight());
	}

	public void actionPerformed(ActionEvent e)
	{
		try
		{
		String item = " " + e;
		int pos = item.indexOf("cmd=");
		pos = pos + 4;
		if(item.charAt(pos) == 'R')
		{
			String dat = new String();
			FileInputStream in = new FileInputStream("DAT");
			int c;
			c = in.read();
			in.close();
				
			dat = dat + (char)c + '\r' + '\n';
			int itt[] = iplist.getSelectedIndexes();
			for(int i=0;i<itt.length;i++)
			{
				iplist.remove(itt[i]);
			}
			
			pos = iplist.getItemCount();
			for(int i=0;i<pos;i++)
			{
				dat = dat + iplist.getItem(i) + '\r' + '\n';
				
			}
			File del = new File("DAT");
			del.delete();
			
			File crt = new File("DAT");
			crt.createNewFile();
			
			FileOutputStream fil = new FileOutputStream("DAT");
			for(pos=0;pos<dat.length();pos++)
			{
				fil.write(dat.charAt(pos));
			}
			fil.close();
		}

		else if(item.charAt(pos) == 'S')
		{
			if(x==0)
			{
				x=1;
				String po = new String();
				po = ptext.getText();
				System.out.println(po);
				port = Integer.parseInt(po);
				okbut.setLabel("STOP");
			
				choice.setEnabled(false);
				removeip.setEnabled(false);
				newip_but.setEnabled(false);
				
			}
			else
			{
				x=0;
				okbut.setLabel("START");
				choice.setEnabled(true);
				removeip.setEnabled(true);
				newip_but.setEnabled(true);

			}
		}

		else if(item.charAt(pos) == 'A')
		{
			String newip = new String();
			newip = newip_text.getText();
			if(newip.length() > 0)
			{
				iplist.add(newip);		
				File tmp = new File("DAT");
				long len = tmp.length();
				tmp = null;
				RandomAccessFile out = new RandomAccessFile("DAT","rw");
				out.seek(len);
				newip = newip + "\r\n";
				out.write(newip.getBytes());
				out.close();
				
				
			}
		}

		}
		catch(Exception ee){}
	}

	public void itemStateChanged(ItemEvent e)
	{
		try{
		RandomAccessFile out = new RandomAccessFile("DAT","rw");
		if(choice.getSelectedIndex() == 0)
		{
			out.write((int)'1');
		}
		else
		{
			out.write((int)'0');
		}
		out.close();
		}
		catch(Exception ee)
		{
			System.out.println(ee);	
		}
		
	}

	public void windowOpened(WindowEvent e){}  
      public void windowClosing(WindowEvent e)
	{
		System.exit(0);
	}
      public void windowClosed(WindowEvent e){}
      public void windowIconified(WindowEvent e){}
      public void windowDeiconified(WindowEvent e){}
      public void windowActivated(WindowEvent e){}
      public void windowDeactivated(WindowEvent e){}

	

}