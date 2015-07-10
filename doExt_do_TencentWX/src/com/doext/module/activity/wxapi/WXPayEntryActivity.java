package com.doext.module.activity.wxapi;

import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import core.DoServiceContainer;
import doext.app.do_TencentWX_App;
import doext.implement.do_TencentWX_Model;
import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String appId = getIntent().getStringExtra("appId");
		api = WXAPIFactory.createWXAPI(this, appId);
		api.registerApp(appId);
		api.handleIntent(getIntent(), this);
		if(appId!=null&&!appId.equals("")){
			pay(appId);
		}
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {

		try {
			do_TencentWX_Model _model = (do_TencentWX_Model) DoServiceContainer
					.getSingletonModuleFactory().getSingletonModuleByID(null,
							do_TencentWX_App.getInstance().getModuleTypeID());
			_model.callBack(resp);

		} catch (Exception e) {
			DoServiceContainer.getLogEngine().writeError(
					"do_TencentWX_Model onResp \n\t", e);
		}
		this.finish();

	}

	private void pay(String appId) {
		PayReq req = new PayReq();

		String _partnerId = getIntent().getStringExtra("partnerId");
		String _prepayId = getIntent().getStringExtra("prepayId");
		String _package = getIntent().getStringExtra("package");
		String _nonceStr = getIntent().getStringExtra("nonceStr");
		String _timeStamp = getIntent().getStringExtra("timeStamp");
		String _sign = getIntent().getStringExtra("sign");
		if (checkIsNull("partnerId", _partnerId)|| checkIsNull("package", _package)|| checkIsNull("nonceStr", _nonceStr)|| checkIsNull("timeStamp", _timeStamp)|| checkIsNull("sign", _sign)) {
			DoServiceContainer.getLogEngine().writeError(
					"do_TencentWX_Model onResp \n\t", new Exception("支付参数异常"));
			return;
		} else {
			req.appId = appId;
			req.partnerId = _partnerId;
			req.prepayId = _prepayId;
			req.packageValue = _package;
			req.nonceStr = _nonceStr;
			req.timeStamp = _timeStamp;
			req.sign = _sign;
		}
		api.registerApp(appId);
		api.sendReq(req);
		finish();
	}

	private boolean checkIsNull(String name, String data) {
		if (data == null) {
			finish();
			Toast.makeText(this, name + "不能为空", Toast.LENGTH_SHORT).show();
			return true;
		} else {
			return false;
		}

	}
}