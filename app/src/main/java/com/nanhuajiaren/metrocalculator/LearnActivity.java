package com.nanhuajiaren.metrocalculator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

public class LearnActivity extends AppCompatActivity
{

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO: Implement this method
		super.onCreate(savedInstanceState);
		//日常
		if(DarkMode.isDarkMode){
			setTheme(R.style.AppThemeDark);
		}
		setContentView(R.layout.learn);
		//标题
		Toolbar tool = (Toolbar) findViewById(R.id.toolbar2);
		tool.setTitle(R.string.app_name);
		tool.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
		setSupportActionBar(tool);
		//惊不惊喜意不意外，这就一图
		ImageView image = (ImageView) findViewById(R.id.learnImageView);
		//我也不知道为什么要写这种傻逼东西
		image.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth());
		image.setMinimumHeight((int)((double)image.getWidth() / 586 * 2047));
	}
}
