package com.bingghost.simpletetris.activity;

import com.bingghost.simpletetris.R;
import com.bingghost.simpletetris.astar.AStar;
import com.bingghost.simpletetris.astar.AStarView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

// implements OnTouchListener,OnGestureListener
public class AStarActivity extends Activity {

	public static final int VIEW_REFRESH = 1;
	public static final String DEBUG_TAG = "__BING__";

	private AStarView mAStarView = null;
	public AStar mAStar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// mAStarView = new AStarView(this);
		setContentView(R.layout.astar);
		mAStarView = (AStarView) findViewById(R.id.aStarView);
		mAStarView.initAstar(this);
		initAStar();

		Button mBtnFindPath = (Button) findViewById(R.id.btn_find_path);
		mBtnFindPath.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				findPath();
			}
		});
		Button mBtnCreateMap = (Button) findViewById(R.id.btn_create_map);
		mBtnCreateMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				createMap();
			}
		});
	}

	private void initAStar() {
		int screenHeight = mAStarView.mScreenHeight;
		int screenWidth = mAStarView.mScreenWidth;

		int boxLen = mAStarView.getBoxLen();

		int aStarHeight = AStarView.ASTAR_WIDTH + (((screenHeight - screenWidth) / 3) * 2) / boxLen;

		mAStar = new AStar(AStarView.ASTAR_WIDTH, aStarHeight);
	}

	@SuppressLint("HandlerLeak")
	Handler m_Handler = new Handler() // 定义Handler变量 实现更新View视图
	{
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case VIEW_REFRESH:
				mAStarView.invalidate();
				break;
			}

			super.handleMessage(msg);
		}
	};

	public void findPath() {
		Message message = new Message();
		mAStar.FindPath();

		message.what = VIEW_REFRESH;
		m_Handler.sendMessage(message);
	}

	public void createMap() {
		Message message = new Message();
		mAStar.createMap();

		message.what = VIEW_REFRESH;
		m_Handler.sendMessage(message);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:

			break;
		case KeyEvent.KEYCODE_DPAD_RIGHT:

			break;

		case KeyEvent.KEYCODE_DPAD_UP:
			findPath();
			break;

		case KeyEvent.KEYCODE_DPAD_DOWN:
			createMap();
			break;

		case KeyEvent.KEYCODE_DPAD_CENTER:

			break;

		// 按下返回按钮
		case KeyEvent.KEYCODE_BACK:
			if (event.getRepeatCount() == 0) {
				// 询问是否退出

				dialogExit();
			}
			break;
		default:
			break;
		}

		return true;
	}

	public AStarView getAStarView() {
		return mAStarView;
	}

	public void setAStarView(AStarView mAStarView) {
		this.mAStarView = mAStarView;
	}

	protected void dialogExit() // 对话框函数
	{
		AlertDialog.Builder builder = new Builder(AStarActivity.this);
		builder.setMessage("确定要退出吗?");
		builder.setTitle("提示");
		builder.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		builder.create().show();
	}
}
