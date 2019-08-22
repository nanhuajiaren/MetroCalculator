package com.nanhuajiaren.metrocalculator;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import android.support.v7.app.AlertDialog;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.DialogInterface;
import android.net.Uri;
import android.view.Gravity;
import android.content.SharedPreferences;
import android.content.ClipboardManager;

public class MainActivity extends AppCompatActivity 
{
	//待输出的文字，在output中使用
	private static String waitingText = "";
	
	//更新信息
	private static String waitingInfo = "";
	
	//事实上充当锁，在动画期间禁止更改
	private static boolean isRunning = false;
	
	//当前版本，用于查找更新
	private static int currentVersion;
	
	//侧边栏
	private DrawerLayout drawer;
	
	private static final String[][] OVERRIDETRAINS = 
	{
		//将无公式列车及临时调换列车和518写在此处

		//01A01老老八
		{ "101","92011" ,"92022" ,"92033" ,"93442" ,"93453" ,"94102" ,"94113" ,"94121" },
		{ "102","93121" ,"93262" ,"93273" ,"14652" ,"14663" ,"92102" ,"93233" ,"93311" },
		{ "103","92131" ,"92142" ,"92153" ,"94022" ,"94033" ,"92162" ,"92173" ,"92181" },
		{ "104","92191" ,"92202" ,"92213" ,"94202" ,"94213" ,"92222" ,"92233" ,"92241" },
		{ "105","93011" ,"93022" ,"93033" ,"94162" ,"94173" ,"93042" ,"93053" ,"93061" },
		{ "106","92061" ,"92053" ,"92042" ,"94053" ,"94042" ,"94093" ,"94082" ,"94071" },
		{ "107","93251" ,"93142" ,"93153" ,"93462" ,"93473" ,"93282" ,"93173" ,"92121" },
		{ "108","93191" ,"93202" ,"93213" ,"94142" ,"94153" ,"93222" ,"92113" ,"93361" },
		{ "109","93131" ,"93322" ,"93333" ,"93402" ,"93413" ,"93162" ,"93293" ,"93301" },
		{ "110","93181" ,"93353" ,"93342" ,"94233" ,"94222" ,"92093" ,"92082" ,"92071" },
		{ "114","93071" ,"93082" ,"93093" ,"93382" ,"93393" ,"93102" ,"93113" ,"93241" },

		//01A02伪八二世
		{ "111","93371" ,"014352","014363","014372","014383","014392","014403","93421" },
		{ "112","93431" ,"014412","014423","014432","014443","014452","014463","93481" },
		{ "113","94011" ,"014472","014483","014492","014503","014512","014523","94061" },
		{ "115","94131" ,"014532","014543","014552","014563","014572","014583","94181" },
		{ "116","94191" ,"014592","014603","014612","014623","014632","014643","92421" },

		//01A03老八
		{ "117","98011" ,"98022" ,"98033" ,"14672" ,"14683" ,"98042" ,"98053" ,"98061" },
		{ "118","99011" ,"99022" ,"99033" ,"01802" ,"01813" ,"99042" ,"99053" ,"99061" },
		{ "119","99091" ,"99082" ,"99093" ,"01822" ,"01833" ,"99102" ,"99113" ,"99121" },
		{ "120","99131" ,"99142" ,"99153" ,"99502" ,"99513" ,"99162" ,"99173" ,"99181" },
		{ "121","99191" ,"99202" ,"99213" ,"99522" ,"99533" ,"99222" ,"99233" ,"99241" },
		{ "122","99251" ,"99262" ,"99273" ,"00022" ,"00033" ,"99282" ,"99293" ,"99301" },
		{ "123","99311" ,"99322" ,"99333" ,"00042" ,"00053" ,"99342" ,"99353" ,"99361" },
		{ "124","99371" ,"99382" ,"99393" ,"01742" ,"01753" ,"99402" ,"99413" ,"99421" },
		{ "125","99431" ,"99442" ,"99453" ,"01762" ,"01773" ,"99462" ,"99473" ,"99481" },

		//01A04伪八
		{ "126","99491" ,"013682","013673","013663","013652","013643","013632","99541" },
		{ "127","00011" ,"013802","013793","013783","013772","013763","013752","00061" },
		{ "128","01731" ,"013862","013853","013843","013832","013823","013812","01781" },
		{ "129","01791" ,"019742","013733","013723","013712","013703","013692","01841" },

		{ "130","01251" ,"013992","014003","014012","014023","014033","014042","01301" },
		{ "131","01311" ,"014172","014183","014192","014203","014213","014222","01361" },
		{ "132","01371" ,"014232","014243","014252","014263","014273","014282","01421" },
		{ "133","01431" ,"014112","014123","014132","014143","014153","014162","01481" },
		{ "134","01491" ,"014052","014063","014072","014083","014093","014102","01541" },
		{ "135","01551" ,"013872","013883","013892","013903","013913","013922","01601" },
		{ "136","01611" ,"013932","013943","013952","013963","013973","013982","01661" },
		{ "137","01671" ,"014292","014303","014312","014323","014333","014342","01721" },

		//02A01西瓜
		{ "201","00071" ,"00082" ,"00093" ,"01262" ,"01273" ,"00102" ,"00113" ,"00121" },
		{ "202","00131" ,"00142" ,"00153" ,"01282" ,"01293" ,"00162" ,"00173" ,"00181" },
		{ "203","00191" ,"00202" ,"00213" ,"01322" ,"01333" ,"00222" ,"00233" ,"00241" },
		{ "204","00251" ,"00262" ,"00273" ,"01342" ,"01353" ,"00282" ,"00293" ,"00301" },
		{ "205","00311" ,"00322" ,"00333" ,"01382" ,"01393" ,"00342" ,"00353" ,"00361" },
		{ "206","00371" ,"00382" ,"00393" ,"01402" ,"01413" ,"00402" ,"00413" ,"00421" },
		{ "207","00431" ,"00442" ,"00453" ,"01442" ,"01453" ,"00462" ,"00473" ,"00481" },
		{ "208","00491" ,"00502" ,"00513" ,"01462" ,"01473" ,"00522" ,"00533" ,"00541" },
		{ "209","00551" ,"00562" ,"00573" ,"01502" ,"01513" ,"00582" ,"00593" ,"00601" },
		{ "210","00611" ,"00622" ,"00633" ,"01522" ,"01533" ,"00642" ,"00653" ,"00661" },
		{ "211","00671" ,"00682" ,"00693" ,"01562" ,"01573" ,"00702" ,"00713" ,"00721" },
		{ "212","00731" ,"00742" ,"00753" ,"01582" ,"01593" ,"00762" ,"00773" ,"00781" },
		{ "213","01011" ,"01022" ,"01033" ,"01622" ,"01633" ,"01042" ,"01053" ,"01061" },
		{ "214","01071" ,"01082" ,"01093" ,"01642" ,"01653" ,"01102" ,"01113" ,"01121" },
		{ "215","01131" ,"01142" ,"01153" ,"01682" ,"01693" ,"01162" ,"01173" ,"01181" },
		{ "216","01191" ,"01202" ,"01213" ,"01702" ,"01713" ,"01222" ,"01233" ,"01241" },

		//256和257调换车厢
		//似乎不换了?
//		{"0256","023131","023142","023192","023201"},
//		{"0257","023211","023222","023232","023163","023172","023263","023272","023281"},

		//518特殊情况
		{ "518","04091" ,"04102" ,"04112" ,"04121" },

		//170044
		{"1701","170011","170022","170033","170044","170052","170061"}
	};

	//夜间模式开关状态，用于设置后改变文字。实际的状态在DarkMode.java。
	private boolean isDarkModeShowed;
	
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
		
		//这里还有更先进的写法，早期写的时候这么写就随他去了
		
		//读取是否夜间模式
		DarkMode.isDarkMode = readIsDarkMode();
		
		//实际设置主题
		//很显然正常换肤不是这样的，但是没必要，我也不会
		if(DarkMode.isDarkMode){
			setTheme(R.style.AppThemeDark);
		}
		
		//初始化
		isDarkModeShowed = DarkMode.isDarkMode;
		
        setContentView(R.layout.main);
		
		//开关文字
		if(isDarkModeShowed){
			((TextView) findViewById(R.id.mainTextView1)).setText(R.string.dark_mode_on);
		}
		
		//标题栏，侧边栏
		Toolbar tool = (Toolbar) findViewById(R.id.toolbar);
		tool.setTitle(R.string.app_name);
		tool.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		setSupportActionBar(tool);
		drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
		ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this, drawer, tool, R.string.open, R.string.close){};		
		toggle.syncState();
		drawer.setDrawerListener(toggle);

		//获取当前版本
		try
		{
			currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
		}
		catch (Exception e)
		{
			//不可能到这里，不写。
		}

		//另开线程查找更新
		new Thread(){
			@Override
			public void run()
			{
				try
				{
					getInfo();
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}.start();
    }

	public void onLearnClick(View p1)
	{
		//侧边栏学习按钮点击事件
		
		//防止闪瞎眼
		if(!DarkMode.isDarkMode){
			//非夜间模式
			Intent i = new Intent(this, LearnActivity.class);
			startActivity(i);
		}else{
			//夜间模式提示
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(R.string.picture_too_light);
			builder.setPositiveButton(R.string.show,new AlertDialog.OnClickListener(){
				@Override
				public void onClick(DialogInterface p1,int p2){
					Intent i = new Intent(MainActivity.this, LearnActivity.class);
					startActivity(i);
				}
			});
			builder.show();
		}
	}

	public void onAboutClick(View p1){
		//关于
		Intent i = new Intent(this, AboutActivity.class);
		startActivity(i);
	}
	
	public void calculate(View p1)
	{
		String input = ((EditText) findViewById(R.id.input)).getText().toString();
		if(input.equals("170043")){
			//170043不应该有结果
			output(new String[1],input);
		}
		else if (!isRunning && !input.equals(""))
		{
			try{
				//正常情况下不会出错，但是仍然用try-catch防止意外
				output(getTrainNumberFromCarrierNumber(input.toUpperCase()), input);
			}catch(Exception e){
				Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
				isRunning = false;
			}
		}
	}

	public void output(String[] trainNumbers, String input)
	{
		//锁定
		isRunning = true;
		
		//最终输出
		String text = "";
		
		//逐个输出
		for (int i = 0;i < trainNumbers.length;i++)
		{
			//防止意外
			if (trainNumbers[i] != "" && trainNumbers[i] != null)
			{
				//模式:线路 \n 车号 \n 车型
				text = text + getResources().getString(R.string.line) + getLineName(i) + "\n" + getResources().getString(R.string.train_number) + trainNumbers[i] + "\n" + getResources().getString(R.string.train_type) + getTrainType(trainNumbers[i]) + "\n\n";
			}
		}
		if (text.equals(""))
		{
			//无结果
			text = text + getResources().getString(R.string.not_found).replaceAll("%c", input) + "\n";
		}
		//作者
		text = text + getResources().getString(R.string.author);
		
		//缓存输出备用
		waitingText = text;
		//输出
		TextView outputView = (TextView) findViewById(R.id.result);
		//第一步:原文字淡出
		TranslateAnimation animation = new TranslateAnimation(0, 0, 0, this.getWindowManager().getDefaultDisplay().getHeight());
		animation.setDuration(250);
		outputView.startAnimation(animation);
		new Handler().postDelayed(new Runnable(){
				@Override
				public void run()
				{
					//第二步:淡出完成，更改文本框内容，开始淡入
					TextView o = (TextView) findViewById(R.id.result);
					o.setText(waitingText);
					TranslateAnimation animation = new TranslateAnimation(0, 0, MainActivity.this.getWindowManager().getDefaultDisplay().getHeight(), 0);
					animation.setDuration(250);
					o.startAnimation(animation);
					new Handler().postDelayed(new Runnable(){
							@Override
							public void run()
							{
								//第三步:淡入完成，释放锁
								isRunning = false;
							}
						}, 250);
				}
			}, 250);
	}

	//计算主体
	public String[] getTrainNumberFromCarrierNumber(String carrierNumber)
	{
		//结果数组
		String[] result = new String[100];
		//第x位为x号线，98=浦江线，99=浦东机场捷运
		
		char[] inputChars = carrierNumber.toCharArray();

		//1号线(新车)
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '1' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			//01A06,01A07 钢铁侠(二世) y=(x-467)/8+55
			if (middle >= 468)
			{
				if (varify(w8(middle - 467), inputChars[5]))
				{
					result[1] = "01" + get2((int)(up((double)(middle - 467) / 8) + 55));
				}
			}
			//01A05 胖头鱼 y=(x-234)/8+39
			if (middle >= 235 && middle <= 362)
			{
				if (varify(w8(middle - 234), inputChars[5]))
				{
					result[1] = "01" + get2((int)(up((double)(middle - 234) / 8) + 39));
				}
			}
		}
		//2号线
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '2' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			//02A02 青鱼 y=x/8+16
			if (middle >= 129 && middle <= 296)
			{
				if (varify(w8(middle), inputChars[5]))
				{
					result[2] = "02" + get2((int)up((double)middle / 8) + 16);
				}
			}
			//02A03 鲶鱼 y=x/8+16
			if (middle >= 297 && middle <= 424)
			{
				if (varify(w8Strange(middle), inputChars[5]))
				{
					result[2] = "02" + get2((int)up((double)middle / 8) + 16);
				}
			}
			//02A04 小鲶鱼(老车厢) y=x/8-37
			if (middle >= 425 && middle <= 488)
			{
				if (varify(w4(middle), inputChars[5]))
				{
					result[2] = "02" + get2((int)up((double)middle / 4) - 37);
				}
			}
			//02A04 扩编鲶鱼(新车厢) y=x/8-55
			if (middle >= 489 && middle <= 552)
			{
				if (varify(w4Added(middle), inputChars[5]))
				{
					result[2] = "02" + get2((int)up((double)middle / 4) - 53);
				}
			}
			//02A05 绿灯侠 y=x/8+16
			if (middle >= 553)
			{
				int trainNumber = (int)up((double)middle / 8) + 16;
				if (varify(w8(middle), inputChars[5]))
				{
					result[2] = "02" + get3(trainNumber);
				}
			}
		}
		//3号线(03A01 黄鱼)
		if (inputChars.length == 5 && inputChars[0] == '0')
		{
			int n = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3]}));
			
			//2002年购入 y=x/6
			if (inputChars[1] == '2')
			{
				int trainNumber = (int)up(((double)n) / 6);
				if (trainNumber <= 5)
				{
					if (varify(w6(n), inputChars[4]))
					{
						result[3] = "3" + get2(trainNumber);
					}
				}
			}
			
			//2003年购入 y=x/6+5
			if (inputChars[1] == '3')
			{
				int trainNumber = (int)up((double)n / 6) + 5;
				if (trainNumber >= 6)
				{
					if (trainNumber <= 21)
					{
						if (varify(w6(n), inputChars[4]))
						{
							result[3] = "3" + get2(trainNumber);
						}
					}
				}
			}
			
			//2004年购入 y=x/6+21
			if (inputChars[1] == '4')
			{
				int trainNumber = (int)up((double)n / 6) + 21;
				if (trainNumber >= 22)
				{
					if (trainNumber <= 28)
					{
						if (varify(w6(n), inputChars[4]))
						{
							result[3] = "3" + get2(trainNumber);
						}
					}
				}
			}
		}

		//3号线(03A02,04A02 包公)
		//叛徒包公仍以4号线计，只是输出时标明
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '3' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			if (middle >= 169)
			{
				if (varify(w6(middle), inputChars[5]))
				{
					result[3] = "03" + get2((int)up((double)middle / 6));
				}
			}
		}

		//4号线(04A01 奶嘴,04A02 包公)
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '4' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			if (varify(w6(middle), inputChars[5]))
			{
				result[4] = "04" + get2((int)up((double)middle / 6));
			}
		}

		//5号线(05C01 番茄炒蛋)
		//518问题随其他特殊列车之后覆盖
		if (inputChars.length == 5 && inputChars[0] == '0')
		{
			//2002年购入 y=x/4
			int n = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3]}));
			if (inputChars[1] == '2')
			{
				int trainNumber = (int)up((double)n / 4);
				if (trainNumber <= 1)
				{
					if (varify(w4(n), inputChars[4]))
					{
						result[5] = "5" + get2(trainNumber);
					}
				}
			}
			
			//2003年购入 y=x/4+1
			if (inputChars[1] == '3')
			{
				int trainNumber = (int)up((double)n / 4) + 1;
				if (trainNumber <= 11)
				{
					if (varify(w4(n), inputChars[4]))
					{
						result[5] = "5" + get2(trainNumber);
					}
				}
			}
			
			//2004年购入 y=x/4+11
			if (inputChars[1] == '4')
			{
				int trainNumber = (int)up((double)n / 4) + 11;
				if (trainNumber <= 18)
				{
					if (varify(w4(n), inputChars[4]))
					{
						result[5] = "5" + get2(trainNumber);
					}
				}
			}
		}

		//5号线(05C02 紫罗兰)
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '5' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			if (middle >= 69)
			{
				if (varify(w6(middle - 68), inputChars[5]))
				{
					result[5] = "05" + get2((int)up((double)(middle - 68) / 6) + 18);
				}
			}
		}

		//6号线(统一) y=x/4+(x+24)/36+1
		//极度睿智
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '6' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			if (varify(w4(middle), inputChars[5]))
			{
				result[6] = "06" + get2((int)(up((double)middle / 4) + up((double)(middle + 24) / 36) - 1));
			}
		}

		//7,9,11,12,13,17号线(统一) y=x/6
		if (inputChars.length == 6 && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int line = Integer.parseInt(String.valueOf(new char[] {inputChars[0],inputChars[1]}));
			if ((line >= 9 && line <= 13 && line != 10) || line == 17 || line == 7)
			{
				int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
				if (varify(w6(middle), inputChars[5]) && middle <= getMaxCarrierNumber(line))
				{
					result[line] = get2(line) + get2((int)(up((double)middle / 6)));
				}
			}
		}

		//10号线 y=x/6
		//热带鱼二世车头为5位，故不并入上面的通用算法
		if (inputChars.length == 6 && inputChars[0] == '1' && inputChars[1] == '0' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			//10A01 热带鱼
			if (middle <= 246)
			{
				if (varify(w6(middle), inputChars[5]))
				{
					result[10] = "10" + get2((int)up((double)middle / 6));
				}
			}
			//10A02 热带鱼二世
			else
			{
				if (varify(w6(middle), inputChars[5]))
				{
					result[10] = "10" + get3((int)up((double)middle / 6));
				}
			}
		}

		//8号线
		//睿智规划
		if (inputChars.length == 6 && inputChars[0] == '0' && inputChars[1] == '8' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			//08C01 蓝精灵 y=x/6
			if (middle >= 1 && middle <= 168)
			{
				if (varify(w6(middle), inputChars[5]))
				{
					result[8] = "08" + get2((int)up((double)middle / 6));
				}
			}
			//08C02,08C03,08C04 泥鳅(纸,电,三世) y=x/7+4
			if (middle >= 169)
			{
				if (varify(w7(middle), inputChars[5]))
				{
					result[8] = "08" + get2((int)up((double)middle / 7) + 4);
				}
			}
		}

		//16号线
		if (inputChars.length == 6 && inputChars[0] == '1' && inputChars[1] == '6' && (inputChars[5] == '1' || inputChars[5] == '2' || inputChars[5] == '3'))
		{
			int middle = Integer.parseInt(String.valueOf(new char[] {inputChars[2],inputChars[3],inputChars[4]}));
			//16A01 抹茶 y=x/3
			//睿智规划
			if (middle <= 138)
			{
				if (varify(w3(middle), inputChars[5]))
				{
					result[16] = "16" + get2((int)up((double)middle / 3));
				}
			}
			//16A02 抹茶二世 y=x/6+23
			else if (middle >= 139)
			{
				if (varify(w6(middle - 138), inputChars[5]))
				{
					result[16] = "16" + get2((int)up((double)middle / 6) + 23);
				}
			}
		}

		//浦江线(98)
		if (inputChars.length == 7 && inputChars[0] == 'T' && inputChars[1] == '0' && inputChars[2] == '1' && (inputChars[6] == '1' || inputChars[6] == '2'))
		{
			String m = String.valueOf(new char[] {inputChars[3],inputChars[4],inputChars[5]});
			int trainNumber = (int)(up((double)Integer.parseInt(m) / 4));
			if (varify(w4(Integer.parseInt(m)), inputChars[6]))
			{
				result[98] = "T01" + get2(trainNumber);
			}
		}

		
		//浦东机场捷运(99)
		if(inputChars.length == 8 && inputChars[0] == 'J' && inputChars[1] == 'Y' && inputChars[2] == '0' && inputChars[3] == '1'){
			String m = carrierNumber.substring(4,7);
			int trainNumber = (int)(up((double)Integer.parseInt(m) / 4));
			if (varify(w4(Integer.parseInt(m)), inputChars[7]))
			{
				result[99] = "JY01" + get2(trainNumber);
			}
		}
		
		//老车特殊车
		for (int i = 0;i < OVERRIDETRAINS.length;i++)
		{
			for (int j = 1;j < OVERRIDETRAINS[i].length;j++)
			{
				if (OVERRIDETRAINS[i][j].equals(carrierNumber))
				{
					String trainNumber = OVERRIDETRAINS[i][0];
					result[(int)(down((double)(Integer.parseInt(trainNumber) / 100)))] = trainNumber;
				}
			}
		}
		return result;
	}

	//向上取整
	private double up(double r)
	{
		if (r % 1 == 0)
		{
			return r;
		}
		else
		{
			return Math.ceil(r);
		}
	}

	//向下取整
	private double down(double r)
	{
		if (r % 1 == 0)
		{
			return r;
		}
		else
		{
			return Math.floor(r);
		}
	}

	//Toast输出
	//惊不惊喜，意不意外，我也是ModPE玩家
	public void print(String text)
	{
		Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
	}

	//保证2位
	public String get2(int n)
	{
		if (n < 10)
		{
			return "0" + n;
		}
		else
		{
			return "" + n;
		}
	}

	//保证3位
	public String get3(int n)
	{
		if (n < 100)
		{
			return "0" + get2(n);
		}
		else
		{
			return "" + n;
		}
	}

	//获取车型
	public String getTrainType(String trainNumber)
	{
		char[] inputChars = trainNumber.toCharArray();
		//浦江线
		if (inputChars.length == 5 && inputChars[0] == 'T' && inputChars[1] == '0' && inputChars[2] == '1')
		{
			return getResources().getString(R.string.traintype_apm300);
		}
		//浦东机场捷运
		else if(inputChars.length == 6 && inputChars[0] == 'J' && inputChars[1] == 'Y' && inputChars[2] == '0' && inputChars[3] == '1'){
			//暂未确定
			return "";
		}
		else
		{
			int train = Integer.parseInt(trainNumber);
			if ((train >= 101 && train <= 110) || train == 114)
			{
				return getResources().getString(R.string.traintype_01a01);
			}
			else if (train >= 111 && train <= 116 && train != 114)
			{
				return getResources().getString(R.string.traintype_01a02);
			}
			else if (train >= 117 && train <= 125)
			{
				return getResources().getString(R.string.traintype_01a03);
			}
			else if (train >= 126 && train <= 137)
			{
				return getResources().getString(R.string.traintype_01a04);
			}
			else if (train >= 140 && train <= 155)
			{
				return getResources().getString(R.string.traintype_01a05);
			}
			else if (train >= 156 && train <= 166)
			{
				return getResources().getString(R.string.traintype_01a06);
			}
			else if (train >= 167 && train <= 186)
			{
				return getResources().getString(R.string.traintype_01a07);
			}
			else if (train >= 201 && train <= 224)
			{
				return getResources().getString(R.string.traintype_02a01);
			}
			else if (train >= 233 && train <= 253)
			{
				return getResources().getString(R.string.traintype_02a02);
			}
			else if (train >= 254 && train <= 269)
			{
				return getResources().getString(R.string.traintype_02a03);
			}
			else if (train >= 270 && train <= 285)
			{
				return getResources().getString(R.string.traintype_02a04);
			}
			else if (train >= 2086 && train <= 2116)
			{
				return getResources().getString(R.string.traintype_02a05);
			}
			else if (train >= 301 && train <= 328)
			{
				return getResources().getString(R.string.traintype_03a01);
			}
			else if (train >= 329 && train <= 336)
			{
				return getResources().getString(R.string.traintype_03a02);
			}
			else if (train >= 337 && train <= 349)
			{
				return getTrainType((train + 100) + "");
			}
			else if (train >= 401 && train <= 428)
			{
				return getResources().getString(R.string.traintype_04a01);
			}
			else if ((train >= 429 && train <= 436) || (train >= 450 && train <= 455))
			{
				return getResources().getString(R.string.traintype_04a02);
			}
			else if (train >= 437 && train <= 449)
			{
				return getResources().getString(R.string.traintype_04a02_lent);
			}
			else if (train >= 501 && train <= 518)
			{
				return getResources().getString(R.string.traintype_05c01);
			}
			else if (train >= 519 && train <= 551)
			{
				return getResources().getString(R.string.traintype_05c02);
			}
			else if (train >= 601 && train <= 623)
			{
				return getResources().getString(R.string.traintype_06c01);
			}
			else if (train >= 624 && train <= 636)
			{
				return getResources().getString(R.string.traintype_06c02);
			}
			else if (train >= 637 && train <= 656)
			{
				return getResources().getString(R.string.traintype_06c03);
			}
			else if (train >= 657 && train <= 685)
			{
				return getResources().getString(R.string.traintype_06c04);
			}
			else if (train >= 701 && train <= 742)
			{
				return getResources().getString(R.string.traintype_07a01);
			}
			else if (train >= 743 && train <= 772)
			{
				return getResources().getString(R.string.traintype_07a02);
			}
			else if (train >= 801 && train <= 828)
			{
				return getResources().getString(R.string.traintype_08c01);
			}
			else if (train >= 829 && train <= 846)
			{
				return getResources().getString(R.string.traintype_08c02);
			}
			else if (train >= 847 && train <= 866)
			{
				return getResources().getString(R.string.traintype_08c03);
			}
			else if (train >= 867 && train <= 890)
			{
				return getResources().getString(R.string.traintype_08c04);
			}
			else if (train >= 901 && train <= 910)
			{
				return getResources().getString(R.string.traintype_09a01);
			}
			else if (train >= 911 && train <= 951)
			{
				return getResources().getString(R.string.traintype_09a02);
			}
			else if (train == 952)
			{
				return getResources().getString(R.string.traintype_09ash);
			}
			else if (train >= 953 && train <= 998)
			{
				return getResources().getString(R.string.traintype_09a03);
			}
			else if (train >= 1001 && train <= 1041)
			{
				return getResources().getString(R.string.traintype_10a01);
			}
			else if (train >= 10042 && train <= 10067)
			{
				return getResources().getString(R.string.traintype_10a02);
			}
			else if (train >= 1101 && train <= 1166)
			{
				return getResources().getString(R.string.traintype_11a01);
			}
			else if (train >= 1167 && train <= 1172)
			{
				return getResources().getString(R.string.traintype_11a02);
			}
			else if (train >= 1173 && train <= 1182)
			{
				return getResources().getString(R.string.traintype_11a03);
			}
			else if (train >= 1201 && train <= 1241)
			{
				return getResources().getString(R.string.traintype_12a01);
			}
			else if (train >= 1242 && train <= 1256)
			{
				return getResources().getString(R.string.traintype_12a02);
			}
			else if (train >= 1301 && train <= 1324)
			{
				return getResources().getString(R.string.traintype_13a01);
			}
			else if (train >= 1325 && train <= 1362)
			{
				return getResources().getString(R.string.traintype_13a02);
			}
			else if (train >= 1601 && train <= 1646)
			{
				return getResources().getString(R.string.traintype_16a01);
			}
			else if (train >= 1647 && train <= 1661)
			{
				return getResources().getString(R.string.traintype_16a02);
			}
			else if (train >= 1701 && train <= 1728)
			{
				return getResources().getString(R.string.traintype_17a01);
			}
			else
			{
				return getResources().getString(R.string.traintype_unknown);
			}
		}
	}

	//获取线路名称
	public String getLineName(int line)
	{
		if (line >= 1 && line <= 21)
		{
			//我为什么预留到了21?我也忘记了，因为有些睿智把金山铁路叫22?
			return "" + line;
		}
		else if(line == 98)
		{
			//浦江线
			return getResources().getString(R.string.line_pujiang);
		}
		else if(line == 99)
		{
			//浦东机场捷运
			return getResources().getString(R.string.line_jieyun01);
		}
		else
		{
			//不可能的
			return "err" + line;
		}
	}

	//w[n](x):获取n节编组列车中车体号x"应该"的尾号
	public int w3(int x)
	{
		switch (x % 3)
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 0:
				return 1;
			default:
				return 0;
		}
	}

	public int w4(int x)
	{
		switch (x % 4)
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 2;
			case 0:
				return 1;
			default:
				return 0;
		}
	}
	
	//02A04 鲶鱼 专用
	public int w4Added(int x)
	{
		switch (x % 4)
		{
			case 1:
				return 3;
			case 2:
				return 2;
			case 3:
				return 3;
			case 0:
				return 2;
			default:
				return 0;
		}
	}

	public int w6(int x)
	{
		switch (x % 6)
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 3;
			case 5:
				return 2;
			case 0:
				return 1;
			default:
				return 0;
		}
	}

	public int w7(int x)
	{
		switch (x % 7)
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 3;
			case 5:
				return 3;
			case 6:
				return 2;
			case 0:
				return 1;
			default:
				return 0;
		}
	}

	//1号线部分列车不跟着这个，不过那些列车是5位的，不调用这个方法
	public int w8(int x)
	{
		switch (x % 8)
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 3;
			case 4:
				return 2;
			case 5:
				return 3;
			case 6:
				return 3;
			case 7:
				return 2;
			case 0:
				return 1;
			default:
				return 0;
		}
	}
	
	//02A03 鲶鱼
	//谁来给我解释一下鲶鱼一家子都这么奇怪?
	public int w8Strange(int x)
	{
		switch (x % 8)
		{
			case 1:
				return 1;
			case 2:
				return 2;
			case 3:
				return 2;
			case 4:
				return 3;
			case 5:
				return 2;
			case 6:
				return 3;
			case 7:
				return 2;
			case 0:
				return 1;
			default:
				return 0;
		}
	}

	//验证车体号是不是符合尾号规律
	public boolean varify(int shouldBeEndedWith, char lastChar)
	{
		return (shouldBeEndedWith + "").equals(String.valueOf(lastChar));
	}

	//获取显示更新信息。在线文件"协议"(样式):
	//[int 新版内部版本号]\n[String 更新信息]
	public void getInfo() throws Exception
	{
		//我绝对不会告诉你这个东西我在控制台调试了半天
		System.out.println("Hello World!");
		//已更换至国内源
		//智障移动还我github
		URL url = new URL("https://gitee.com/nanhuajiaren/metroCalculator/raw/master/update.txt");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		System.out.println("connected");
		System.out.println(connection.getResponseCode());
		//照理应该读取ResponseCode判断的，但我不在乎，反正有try-catch包着
		InputStream is = connection.getInputStream();
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		String line;
		StringBuilder info =new StringBuilder("");
		int newVersion = Integer.parseInt(br.readLine());
		line = br.readLine();
		while (line != null)
		{
			info.append(line);
			info.append("\n");
			//这样做会导致字符串结尾多出一个\n，但谁在意呢?
			line = br.readLine();
		}
		//缓存更新信息
		waitingInfo = info.toString();
		if (newVersion > currentVersion)
		{
			//别忘了onCreate里面是另开线程调用的，所以要回UI线程
			this.runOnUiThread(new Runnable()
				{
					@Override
					public void run()
					{
						AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
						builder.setTitle(R.string.update);
						builder.setMessage(waitingInfo);
						builder.setPositiveButton(R.string.update,new AlertDialog.OnClickListener(){
							@Override
							public void onClick(DialogInterface p1,int p2){
								//有人说傻逼百度要他输提取码，怒换剪贴板
								ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
								cm.setText("https://pan.baidu.com/s/1EhbFfAPEYgA1koqLyXeumA 提取码:x3z7");
								print(getResources().getString(R.string.link_copied));
							}
						});
						builder.show();
					}
				}
			);
		}
	}

	//修复在DrawerLayout展开时按返回直接退出的bug
	//怀疑与焦点有关，但是焦点太复杂，遂出此下策
	@Override
	public void onBackPressed()
	{
		if(drawer.isDrawerOpen(Gravity.START)){
			drawer.closeDrawer(Gravity.START);
		}else{
			super.onBackPressed();
		}
	}
	
	//为7,9,11,12,13,17等6A线路通用算法准备的上限
	public int getMaxCarrierNumber(int line){
		switch(line){
			case 7:return 432;
			case 9:return 528;
			case 10:return 402;
			case 11:return 482;
			case 12:return 336;
			case 13:return 372;
			case 17:return 168;
			default:return 999;
		}
	}
	
	//夜间模式开关点击事件
	public void onDarkModeClick(View view){
		isDarkModeShowed = !isDarkModeShowed;
		saveDarkMode(isDarkModeShowed);
		if(isDarkModeShowed){
			((TextView) view).setText(R.string.dark_mode_on);
		}else{
			((TextView) view).setText(R.string.dark_mode_off);
		}
		print(getResources().getString(R.string.require_restart));
	}
	
	//保存夜间模式状态
	private void saveDarkMode(boolean isDarkMode){
		SharedPreferences.Editor edit = getSharedPreferences("settings",MODE_PRIVATE).edit();
		edit.putBoolean("Style.darkMode",isDarkMode);
		edit.commit();
	}
	
	//读取夜间模式状态
	private boolean readIsDarkMode(){
		return getSharedPreferences("settings",MODE_PRIVATE).getBoolean("Style.darkMode",false);
	}
}
