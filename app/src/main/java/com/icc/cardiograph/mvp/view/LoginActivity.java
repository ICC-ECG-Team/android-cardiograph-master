package com.icc.cardiograph.mvp.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.text.InputFilter.LengthFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.icc.cardiograph.R;
import com.icc.cardiograph.base.BaseActivity;
import com.icc.cardiograph.component.BtnWithTopLine;
import com.icc.cardiograph.component.NavigationBar;
import com.icc.cardiograph.mvp.contract.LoginContract;
import com.icc.cardiograph.mvp.model.LoginModel;
import com.icc.cardiograph.mvp.presenter.LoginPresenter;

import butterknife.BindView;
import butterknife.OnClick;
import butterknife.OnEditorAction;

/**
 * 用户登录
 */
public class LoginActivity extends BaseActivity<LoginPresenter, LoginModel> implements LoginContract.View{

	/** 登录手机号 */
	@BindView(R.id.id_phone_text) ImageView phoneText;
	@BindView(R.id.id_phone_edit) EditText phoneEdit;
	/** 登录密码 */
	@BindView(R.id.id_passwor_text) ImageView passwordText;
	@BindView(R.id.id_passwor_edit) EditText passwordEdit;
	/** 是否记录登录手机号 */
	@BindView(R.id.keep_phone) CheckBox checkkeep;
	/** 找回登录密码 */
	@BindView(R.id.find_login_PW) TextView findPWText;
	/** 注册 */
	@BindView(R.id.tv_register) TextView registerBtn;
	/** 登录 */
	@BindView(R.id.login) BtnWithTopLine loginBtn;

	private String userToken = null;
	private String info = null;
	private String phone = null;
	private String password = null;
	private SharedPreferences sp;
	private Editor editor;
	@BindView(R.id.id_navigation_bar) NavigationBar navigationBar; // 导航条
	private NavigationBar.OnNavBarClickListener onNavBarClickListener = new NavigationBar.OnNavBarClickListener() {
		@Override
		public void onNavItemClick(NavigationBar.NavigationBarItem navBarItem) {
			if (navBarItem == NavigationBar.NavigationBarItem.back) {
				finish();
			}
		}
	};

	@Override
	protected int getLayoutResID() {
		return R.layout.activity_login;
	}

	@Override
	protected void initView() {
		// 标题
		navigationBar.setTitle(R.string.login);
//		navigationBar.setBackBtnVisibility(View.GONE);
		navigationBar.setOnNavBarClickListener(onNavBarClickListener);

		passwordEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
		LengthFilter[] filters2 = { new LengthFilter(20) };
		passwordEdit.setFilters(filters2);

		// 是否记住账号初始化
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		editor = sp.edit();
		String userName = sp.getString("userName", "");
		String password = sp.getString("password", "");
		if (!TextUtils.isEmpty(userName)) {// 如果记住账号，初始化时自动用户名
			phoneEdit.setText(userName);
			phoneEdit.setSelection(userName.length());
			if(!TextUtils.isEmpty(password)){
				passwordEdit.setText(new String(Base64.decode(password.getBytes(),0)));
				checkkeep.setChecked(true);
			}
		}

//		phoneEdit.setText("icc");
//		passwordEdit.setText("jcdlsjzx");
		//
		long key_time = 0;
		if(!TextUtils.isEmpty(sp.getString("key_time", ""))){
			key_time = Long.parseLong(sp.getString("key_time", ""));
		}
		if(System.currentTimeMillis() - key_time < 72*3600*1000){//72小时
			Intent intent = new Intent(this, MainActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra("userName", phone);
			startActivity(intent);
			recordUserName();
			finish();
		}
	}

	@OnClick({R.id.find_login_PW, R.id.tv_register, R.id.login})
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.find_login_PW:// 找回密码
				findPWText.requestFocus();
				findPWText.setFocusable(true);
//			startActivity(new Intent(this, ResetPassword1_PhoneNumberActivity.class));
				break;

			case R.id.tv_register:// 跳转到注册
				Intent intent = new Intent(this, RegisterActivity.class);
				startActivityForResult(intent, 1);
				break;

			case R.id.login:// 登录
				login();
				break;
		}
	}

	@OnEditorAction(R.id.id_passwor_edit)
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if (actionId == EditorInfo.IME_ACTION_GO) {
			InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
			if (imm.isActive()) {
				imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);	// 隐藏软键盘
			}
			login();
			return true;
		}
		return false;
	}

	private void login(){
		phone = phoneEdit.getText().toString().trim();
		password = passwordEdit.getText().toString().trim();
		mPresenter.login(this, phone, password);
	}

	/**
	 * 记住账号
	 */
	private void recordUserName(){
		String userName = phoneEdit.getText().toString();
		editor.putString("userName", userName);
		if (checkkeep.isChecked()) {// checkbox勾选记住账号
			String password = passwordEdit.getText().toString();
			editor.putString("password", new String(Base64.encode(password.getBytes(), 0)));
		} else {
			editor.putString("password", "");
		}
		editor.commit();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data != null) {
			if (requestCode == 1) {// 注册后返回注册手机号填入用户名输入框
				phoneEdit.setText(data.getStringExtra("code"));
			}
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	@Override
	public void loginSuccess(String key) {
		editor.putString("key", key);
		editor.putString("key_time", System.currentTimeMillis()+"");
		msgToast("登录成功！");
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("userName", phone);
		startActivity(intent);
		recordUserName();
		finish();
	}

	@Override
	public void showMsg(String msg) {
		msgToast(msg);
	}
}
