package com.doext.module.activity.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import core.DoServiceContainer;
import doext.app.do_TencentWX_App;
import doext.implement.do_TencentWX_Model;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String appId = getIntent().getStringExtra("appId");
		if (appId == null) {
			finish();
			Toast.makeText(this, "appId不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		api = WXAPIFactory.createWXAPI(this, appId);
		api.handleIntent(getIntent(), this);
		String operatFlag = getIntent().getStringExtra("operatFlag");
		if (do_TencentWX_Model.LOING_FLAG.equals(operatFlag)) {
			login();
		} else if (do_TencentWX_Model.PAY_FLAG.equals(operatFlag)) {
			pay();
		}

	}

	private void login() {
		boolean isFlag = getIntent().getBooleanExtra("isFlag", true);
		if (!isFlag) {
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "deviceone";
			api.sendReq(req);
			this.finish();
		}
	}

	private void pay() {
		PayReq req = new PayReq();
		
		String _appId = getIntent().getStringExtra("appId");
		String _partnerId = getIntent().getStringExtra("partnerId");
		String _package = getIntent().getStringExtra("package");
		String _nonceStr = getIntent().getStringExtra("nonceStr");
		String _timeStamp = getIntent().getStringExtra("timeStamp");
		String _sign = getIntent().getStringExtra("sign");
		if (checkIsNull("appId", _appId)
				&& !checkIsNull("partnerId", _partnerId)
				&& !checkIsNull("package", _package)
				&& !checkIsNull("nonceStr", _nonceStr)
				&& !checkIsNull("timeStamp", _timeStamp)
				&& !checkIsNull("sign", _sign)) {
			return;
		} else {
			req.appId = _appId;
			req.partnerId = _partnerId;
			req.packageValue = _package;
			req.nonceStr = _nonceStr;
			req.timeStamp = _timeStamp;
			req.sign = _sign;
		}
		api.sendReq(req);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp r) {
		SendAuth.Resp resp = (SendAuth.Resp) r;
		try {
			do_TencentWX_Model _model = (do_TencentWX_Model) DoServiceContainer
					.getSingletonModuleFactory().getSingletonModuleByID(null,
							do_TencentWX_App.getInstance().getModuleTypeID());
			_model.callBack(resp,do_TencentWX_Model.LOING_FLAG);
			if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
				_model.callBack(resp,do_TencentWX_Model.PAY_FLAG);
			}
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError(
					"do_TencentWX_Model onResp \n\t", e);
		}
		this.finish();
		
	}
	private boolean checkIsNull(String name,String data){
		if(data==null){
			finish();
			Toast.makeText(this, name+"不能为空", Toast.LENGTH_SHORT).show();
			return true;
		}else{
			return false;
		}
		
	}
}
