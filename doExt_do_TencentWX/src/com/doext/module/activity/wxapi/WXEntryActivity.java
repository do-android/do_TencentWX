package com.doext.module.activity.wxapi;

import android.app.Activity;
import android.os.Bundle;

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
	public static String WEIXIN_APP_ID = "wx7589880f174273b5";
	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		api = WXAPIFactory.createWXAPI(this, WEIXIN_APP_ID);
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
