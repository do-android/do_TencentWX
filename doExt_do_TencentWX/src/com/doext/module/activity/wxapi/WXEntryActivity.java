package com.doext.module.activity.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
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
		if(appId == null){
			finish();
			Toast.makeText(this, "appId不能为空", Toast.LENGTH_SHORT).show();
			return;
		}
		api = WXAPIFactory.createWXAPI(this, appId);
		api.handleIntent(getIntent(), this);
		boolean isFlag = getIntent().getBooleanExtra("isFlag", true);
		if (!isFlag) {
			SendAuth.Req req = new SendAuth.Req();
			req.scope = "snsapi_userinfo";
			req.state = "deviceone";
			api.sendReq(req);
			this.finish();
		}
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp r) {
		SendAuth.Resp resp = (SendAuth.Resp) r;
		try {
			do_TencentWX_Model _model = (do_TencentWX_Model) DoServiceContainer.getSingletonModuleFactory().getSingletonModuleByID(null, do_TencentWX_App.getInstance().getModuleTypeID());
			_model.callBack(resp);
		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError("do_TencentWX_Model onResp \n\t", e);
		}
		this.finish();
	}
}
