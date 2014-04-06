package cn.fython.shutdown;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

@SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity {
	
	Button btn_shutdown, btn_reboot, btn_recovery;
	AlertDialog dialogExit, dialogHelp;
	
	private final static String TAG = "MainActivity";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ShutdownManager.readConfig(getApplicationContext());
		
		if (!ExecCommand.isRooted()){
			Log.v(TAG, "No rooted!");
			showUnrootedDialog();
		}
		
		btn_shutdown = (Button) findViewById(R.id.btn_shutdown);
		btn_reboot = (Button) findViewById(R.id.btn_reboot);
		btn_recovery = (Button) findViewById(R.id.btn_recovery);
		
		btn_shutdown.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				if (ShutdownManager.getTime() != ShutdownManager.TIME_NONE){
					ShutdownManager.stop(getApplicationContext());
					ShutdownManager.setMode(ShutdownManager.SHUTDOWN);
					ShutdownManager.start(getApplicationContext());
					MainActivity.this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
					Toast.makeText(getApplicationContext(), getString(R.string.tips_finishsetting), Toast.LENGTH_SHORT).show();
					return;
				}
				if (!ShutdownManager.shutdown(getApplicationContext(), ShutdownManager.SHUTDOWN)){
					showUnrootedDialog();
					//ShutdownManager.shutdown(getApplicationContext(), ShutdownManager.SHUTDOWN_NOROOT);
				}
			}
			
		});
		
		btn_reboot.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				if (ShutdownManager.getTime() != ShutdownManager.TIME_NONE){
					ShutdownManager.stop(getApplicationContext());
					ShutdownManager.setMode(ShutdownManager.REBOOT);
					ShutdownManager.start(getApplicationContext());
					MainActivity.this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
					Toast.makeText(getApplicationContext(), getString(R.string.tips_finishsetting), Toast.LENGTH_SHORT).show();
					return;
				}
				if (!ShutdownManager.shutdown(getApplicationContext(), ShutdownManager.REBOOT)){
					showUnrootedDialog();
				}
			}
			
		});
		
		btn_recovery.setOnClickListener(new OnClickListener(){
			
			@Override
			public void onClick(View arg0) {
				if (ShutdownManager.getTime() != ShutdownManager.TIME_NONE){
					ShutdownManager.stop(getApplicationContext());
					ShutdownManager.setMode(ShutdownManager.RECOVERY);
					ShutdownManager.start(getApplicationContext());
					MainActivity.this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
					Toast.makeText(getApplicationContext(), getString(R.string.tips_finishsetting), Toast.LENGTH_SHORT).show();
					return;
				}
				if (!ShutdownManager.shutdown(getApplicationContext(), ShutdownManager.RECOVERY)){
					showUnrootedDialog();
				}
			}
			
		});
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		if (ShutdownManager.getTime() == ShutdownManager.TIME_NONE){
			MenuItemCompat.setShowAsAction(
					menu.add(R.string.item_settime)
					.setIcon(android.R.drawable.ic_menu_recent_history),
					MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		} else {
			MenuItemCompat.setShowAsAction(
					menu.add(R.string.item_deletetime)
					.setIcon(android.R.drawable.ic_menu_delete),
					MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
		}
		
		MenuItemCompat.setShowAsAction(
				menu.add(R.string.item_help)
				.setIcon(android.R.drawable.ic_menu_help), 
				MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menu){
		if (menu.getTitle() == getString(R.string.item_settime)){
			Intent intent = new Intent(MainActivity.this, SetTimeActivity.class);
			startActivityForResult(intent, 0);
		}
		
		if (menu.getTitle() == getString(R.string.item_deletetime)){
			ShutdownManager.setTime(ShutdownManager.TIME_NONE);
			ShutdownManager.stop(getApplicationContext());
			MainActivity.this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
			Toast.makeText(getApplicationContext(), getString(R.string.tips_cancelsetting), Toast.LENGTH_SHORT).show();
		}
		
		if (menu.getTitle() == getString(R.string.item_help)){
			showHelpDialog();
		}
		
		return super.onOptionsItemSelected(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.i(TAG, "onActivityResult requestCode=" + requestCode + ", resultCode=" + resultCode);
		switch (resultCode){
		case 1:
			ShutdownManager.setTime(ShutdownManager.TIME_ONTIME);
			ShutdownManager.setClock(data.getLongExtra("time", 0));
			MainActivity.this.getWindow().invalidatePanelMenu(Window.FEATURE_OPTIONS_PANEL);
			Toast.makeText(getApplicationContext(), getString(R.string.tips_selectmode), Toast.LENGTH_SHORT).show();
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void showUnrootedDialog(){
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				finish();
				System.exit(0);
			}
		};
		
		dialogExit = new AlertDialog.Builder(this)
		.setTitle(getString(R.string.title_sorry))
		.setMessage(getString(R.string.context_norooted))
		.setPositiveButton(android.R.string.cancel, listener).show();
		
		dialogExit.setOnCancelListener(new OnCancelListener(){

			@Override
			public void onCancel(DialogInterface arg0) {
				finish();
				System.exit(0);
			}
			
		});
		
		dialogExit.show();
	}
	
	private void showHelpDialog(){
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialogHelp.dismiss();
			}
		};
		
		dialogHelp = new AlertDialog.Builder(this)
		.setTitle(getString(R.string.title_help))
		.setMessage(getString(R.string.context_help))
		.setPositiveButton(android.R.string.ok, listener).show();
	}
	
}
