package com.nanhuajiaren.metrocalculator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//老规矩，夜间模式
		if(DarkMode.isDarkMode){
			setTheme(R.style.AppThemeDark);
		}
		setContentView(R.layout.about);
		Toolbar tool = (Toolbar) findViewById(R.id.toolbar3);
		//标题栏
		tool.setTitle(R.string.app_name);
		tool.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		setSupportActionBar(tool);
		
		try
		{
			//动态输出版本
			String currentVersion = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
			((TextView) findViewById(R.id.aboutTextView1)).setText("V" + currentVersion);
		}
		catch (Exception e)
		{
			//不可能，空着
		}
	}
	
}
