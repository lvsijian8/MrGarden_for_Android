package com.lvsijian8.flowerpot;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class ClientThread implements Runnable
{
	private Socket s;
	// 定义向UI线程发送消息的Handler对象
	static public Handler handler;
	// 定义接收UI线程的消息的Handler对象
	static public Handler revHandler;
	// 该线程所处理的Socket所对应的输入流
	BufferedReader br = null;
	OutputStream os = null;

	public void run()
	{
		try
		{
			s = new Socket("lvsijian.cn", 2345);
			br = new BufferedReader(new InputStreamReader(s.getInputStream()));
			os = s.getOutputStream();
			// 启动一条子线程来读取服务器响应的数据
			new Thread()
			{
				@Override
				public void run()
				{
					String content = null;
					// 不断读取Socket输入流中的内容
					try
					{
						while ((content = br.readLine()) != null)
						{
							// 每当读到来自服务器的数据之后，发送消息通知程序
							// 界面显示该数据
							Message msg = new Message();
							msg.what = 0x123;
							msg.obj = content;
							handler.sendMessage(msg);
						}
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}.start();
			// 为当前线程初始化Looper
			Looper.prepare();
			// 创建revHandler对象
			revHandler = new Handler()
			{
				@Override
				public void handleMessage(Message msg)
				{
					// 接收到UI线程中用户输入的数据
					if (msg.what == 0x345)//登陆
					{
						try
						{
							String name=msg.getData().getString("name");
							String pass=msg.getData().getString("pass");
							os.write(("0"+name+"|"+pass+"\r\n").getBytes("utf-8"));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					if (msg.what == 0x346)//注册
					{
						try
						{
							String name=msg.getData().getString("name");
							String pass=msg.getData().getString("pass");
							String phone=msg.getData().getString("phone");
							os.write(("1"+name+"|"+pass+"|"+phone+"\r\n").getBytes("utf-8"));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					if (msg.what == 0x347)//用户信息初始化
					{
						try
						{
							String name=msg.getData().getString("name");
							int sex=msg.getData().getInt("sex");
							String potname=msg.getData().getString("potname");
							os.write(("2"+name+"|"+sex+"|"+potname+"\r\n").getBytes("utf-8"));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
					if (msg.what == 0x348)//刷新温度
					{
						try
						{
							int pot_id=msg.getData().getInt("pot_id");
							os.write(("3"+pot_id+"|"+"\r\n").getBytes("utf-8"));
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			};
			// 启动Looper
			Looper.loop();

		}
		catch (SocketTimeoutException e1)
		{
			System.out.println("网络连接超时！！");
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}

